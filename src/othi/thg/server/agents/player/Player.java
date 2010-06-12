/**
 * Copyright (c) 2010, The Project OTHI
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions 
 * are met:
 *
 *    * Redistributions of source code must retain the above copyright 
 *      notice, this list of conditions and the following disclaimer.
 *    * Redistributions in binary form must reproduce the above copyright 
 *      notice, this list of conditions and the following disclaimer in 
 *      the documentation and/or other materials provided with the 
 *      distribution.
 *    * Neither the name of the OTHI nor the names of its contributors 
 *      may be used to endorse or promote products derived from this 
 *      software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT 
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR 
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT 
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 */

package othi.thg.server.agents.player;

import java.util.logging.Logger;
import othi.thg.common.Commands;
import othi.thg.server.GameBoard;
import othi.thg.server.ManagedReferenceList;
import othi.thg.server.THGServerDefault;
import othi.thg.server.TerrainMap;
import othi.thg.server.actions.Action;
import othi.thg.server.agents.npcs.Npc;
import othi.thg.server.entities.Portal;
import othi.thg.server.entities.foods.Food;
import othi.thg.server.events.evaluation.Treasure;
import othi.thg.server.obstacles.Monster;
import othi.thg.server.stations.Place;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;


/*
 * managing player object
 * @author Dong Won Kim
 */
public class Player extends Playable {

	private static final long serialVersionUID =  1L;

	private static final Logger logger = Logger.getLogger(Player.class.getName());        

	private boolean combating = false;

	private int attackerId;

	public Player(String name) {
		this.name = name;               

		DataManager dm = AppContext.getDataManager();   
		dm.setBinding(THGServerDefault.PLAYER_INVENTORY + name, new PlayerInventory(name));
		dm.setBinding(THGServerDefault.PLAYER_ARMOR + name, new ArmedArmor(name));     
		dm.setBinding(THGServerDefault.PLAYER_WEAPON + name, new ArmedWeapon(name));          
		dm.setBinding(THGServerDefault.PLAYER_COMPETENCE + name, new PlayerCompetence(name));
		dm.setBinding(THGServerDefault.PLAYER_LOGBOOK + name, new PlayerLogbook(name));    
	}

	@Override
	public void init() {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);    
		setLives(3);
		setStopped(false);        
		setEnabled(true);  
		setNextAction(null);
		setLastProcessedFrame(-1);
	}                 

	@Override
	public void tickAct() {
		Action nextAction = getNextAction();
		if ((nextAction != null) && nextAction.getFrame() > getLastProcessedFrame()) {
			nextAction.act();
		}
	}

	@Override
	public void tickTurn() {
		Action nextAction = getNextAction();    	
		if ((nextAction != null)  && nextAction.getFrame() > getLastProcessedFrame()) {
			nextAction.turn();
		} 
	}

	@Override
	public void tickTalk() {
		Action nextAction = getNextAction();    	
		if ((nextAction != null) && nextAction.getFrame() > getLastProcessedFrame()) {
			nextAction.talk();
		}
	}

	@Override
	public void tickTalktoNPC() {
		Action nextAction = getNextAction();
		if ((nextAction != null) && nextAction.getFrame() > getLastProcessedFrame()) {
			nextAction.talkToNPC();
		}
	}

	@Override
	public void tickMove() {  
		Action nextAction = getNextAction();
		if (nextAction != null) {
			int frame = nextAction.getFrame();            
			if ( frame > getLastProcessedFrame()) {
				nextAction.move();                
			} else {
				setNextAction(null);    
				/*
                logger.info("Player (" + getId() + ")Action frame = " + frame
                                   + "  Last processed="
                                   + getLastProcessedFrame());
				 */
			}
		}
	}    

	public void moveToOtherPlace(String newPlaceName, String portalName) 
	{
		leaveGameBoard();         
		enterGameBoard(newPlaceName, portalName);			
	}       

	private void leaveGameBoard() {
		getInventory(getName()).cutDialogue();                
		GameBoard gameBoard = getGameBoard();         
		gameBoard.sendleaveGameBoard(getId());         	
	}

	private void sendEnterGameBoard(int placeId, String mapfileRef) {
		getPlayerListener(getName()).sendMove(Commands.enterGameBoardCommand(
				getId(), placeId, mapfileRef                              
		));    	
	}

	private void enterGameBoard(String placeName, String portalName)
	{    	    	
		Place place = getPlace(placeName); 
		int placeId = place.getId();
		String mapfileRef = place.getMapFileRef();        

		sendEnterGameBoard(placeId, mapfileRef);

		GameBoard gameBoard = getGameBoard(placeName);
		gameBoard.addPlayerListener(getPlayerListener(getName()));           

		sendAddPortalPlaced(placeId);
		sendAddTreasuresPlaced(placeId);
		sendAddFoodsPlaced(placeId);             
		sendAddMonstersPlaced(placeId);      
		sendAddNpcPlaced(placeId);
		sendAddPlayersPlaying();   

		Portal portal = getPortal(placeId, portalName);         
		float newPos[] = Commands.step(portal.getPosX(), portal.getPosY(), getFacing());

		TerrainMap tm = getTerrainMap(placeId);        
		locate(placeId, placeName, tm.getWidth(), tm.getHeight(), newPos[0], newPos[1]);

		gameBoard.sendAddPlayer(getName());      
		logger.info("Player enters " + placeName);     		        
	}      	

	public void enterGameBoard(String placeName){   
		Place place = getPlace(placeName); 
		int placeId = place.getId();
		String mapfileRef = place.getMapFileRef();  
		sendEnterGameBoard(placeId, mapfileRef);

		GameBoard gameBoard = getGameBoard(placeName);
		gameBoard.addPlayerListener(getPlayerListener(getName()));     

		sendAddPortalPlaced(placeId);
		sendAddTreasuresPlaced(placeId);
		sendAddFoodsPlaced(placeId);     
		sendAddMonstersPlaced(placeId);      
		sendAddNpcPlaced(placeId);
		sendAddPlayersPlaying();          

		TerrainMap tm = getTerrainMap(placeId);
		locate(tm.getWidth(), tm.getHeight(), getPosX(), getPosY());

		gameBoard.sendAddPlayer(getName());		
		logger.info("Player enters " + placeName);     		        
	}       

	private void locate(int placeId, String placeName, int width, int height, float posX, float posY) 
	{
		DataManager dm = AppContext.getDataManager();    	
		dm.markForUpdate(this);
		setPlaceName(placeName);         
		setPlaceId(placeId);               
		setXY(width, height, posX, posY);                         
	}

	public void locate(int width, int height, float posX, float posY) 
	{
		DataManager dm = AppContext.getDataManager();    	
		dm.markForUpdate(this);         
		setXY(width, height, posX, posY);
	}

	public void locate(GameBoard gameBoard, String placeName, float x, float y) 
	{
		DataManager dm = AppContext.getDataManager();	
		dm.markForUpdate(this);

		setPlaceId(gameBoard.getPlaceId());
		setPlaceName(placeName);     
		setXY(x, y);                
	}	      

	private void sendAddPortalPlaced(int placeId) {            
		DataManager dm = AppContext.getDataManager();  
		String binding = THGServerDefault.PORTAL + placeId + ":";
		String bindingName = dm.nextBoundName(binding);

		while (bindingName != null && bindingName.startsWith(binding)) {
			Portal portal = (Portal) dm.getBinding(bindingName);	
			getPlayerListener(getName()).sendMove(
					Commands.addPortalCommand(
							portal.getId(), placeId, portal.getName(),
							portal.getPosX(), portal.getPosY()));
			bindingName = dm.nextBoundName(bindingName);
		} 	
	}	

	private void sendAddTreasuresPlaced(int placeId) {            
		DataManager dm = AppContext.getDataManager();  
		String binding = THGServerDefault.TREASURE + placeId + ":";
		String bindingName = dm.nextBoundName(binding);

		while (bindingName != null && bindingName.startsWith(binding)) {
			Treasure treasure = (Treasure) dm.getBinding(bindingName);
			getPlayerListener(getName()).sendMove(Commands.addTreasureCommand(treasure.getId(), treasure.getQuestID(), treasure.getPosX(), treasure.getPosY()));
			bindingName = dm.nextBoundName(bindingName);
		}      
	}    

	private void sendAddMonstersPlaced(int placeId) {
		DataManager dm = AppContext.getDataManager();  
		String binding = THGServerDefault.MONSTER + placeId + ":";
		String bindingName = dm.nextBoundName(binding);

		while (bindingName != null && bindingName.startsWith(binding)) {
			Monster monster = (Monster) dm.getBinding(bindingName);
			getPlayerListener(getName()).sendMove(Commands.addMonsterCommand(monster.getId(), monster.getName(), monster.getPosX(), monster.getPosY(),
					monster.getGameLevel(), monster.getHP(), monster.getMP(), monster.getPower(),
					monster.getFacing().ordinal(), monster.getImageRef()));
			bindingName = dm.nextBoundName(bindingName);
		}
	}	

	private void sendAddFoodsPlaced(int placeId) {             
		DataManager dm = AppContext.getDataManager();  
		String binding = THGServerDefault.FOOD + placeId + ":";
		String bindingName = dm.nextBoundName(binding);

		while (bindingName != null && bindingName.startsWith(binding)) {
			Food food = (Food) dm.getBinding(bindingName);
			if (!food.isRegenerating()) {
				getPlayerListener(getName()).sendMove(Commands.addFoodCommand(food.getId(), food.getName(), food.getPosX(),
						food.getPosY(), food.getAttractionPoint(), food.getImageRef()));
			}
			bindingName = dm.nextBoundName(bindingName);
		}
	}

	private void sendAddNpcPlaced(int placeId)
	{
		DataManager dm = AppContext.getDataManager();  
		String binding = THGServerDefault.NPC + placeId + ":";
		String bindingName = dm.nextBoundName(binding);

		while (bindingName != null && bindingName.startsWith(binding)) {                 
			Npc npc = (Npc) dm.getBinding(bindingName);
			getPlayerListener(getName()).sendMove(Commands.addNPCCommand(npc.getId(), npc.getNickName(),
					npc.getGameLevel(), npc.getHP(), npc.getMP(),
					npc.getImageRef(), npc.getPosX(), npc.getPosY(), 
					npc.getFacing().ordinal()));  			
			bindingName = dm.nextBoundName(bindingName);
		}          		
	}     	    

	private void sendAddPlayersPlaying()
	{                     	
		ManagedReferenceList<PlayerListener> pl = getGameBoard().getPlayerListenerList().get();
		for (int i=0; i < pl.size(); ++i) {  
			PlayerListener playerListener = pl.get(i);                
			PlayerCompetence playerCompetence = getCompetence(playerListener.getPlayerName());
			ArmedArmor armor = getArmedArmor(playerListener.getPlayerName());
			Player player = playerListener.getPlayer();

			getPlayerListener(getName()).sendMove(Commands.addPlayerCommand(playerListener.getPlayerId(),
					playerListener.getPlayerName(), playerCompetence.getGameLevel(),
					armor.getOutfitCode(), player.getPosX(), 
					player.getPosY(), player.getFacing().ordinal()));
		}            
	}                          

	protected void firstAttackMonster(int monsterId) {
		GameBoard gameBoard = getGameBoard();
		Monster monster = gameBoard.getMonster(monsterId);
		monster.setAttackerName(name);
		setAttackerId(monsterId);
		setCombating(true);
	}

	public int getAttackerId() {
		return attackerId;   
	}

	public void setCombating(boolean combating) {
		AppContext.getDataManager().markForUpdate(this);    	    	
		this.combating = combating;
	}

	public boolean isCombating() {
		return combating;
	}

	private void setAttackerId(int attackerId) {
		AppContext.getDataManager().markForUpdate(this);    
		this.attackerId = attackerId;
	}                     

	@Override
	public void disable() {
		setEnabled(false);
		GameBoard gameBoard = getGameBoard();    
		gameBoard.removePlayerOnClient(this);
	}

}
