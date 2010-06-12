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

package othi.thg.server;

import java.nio.ByteBuffer; 
import java.util.logging.Level;
import java.util.logging.Logger;
import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.agents.npcs.Npc;
import othi.thg.server.agents.player.ArmedArmor;
import othi.thg.server.agents.player.PlayerCompetence;
import othi.thg.server.agents.player.Player;
import othi.thg.server.agents.player.PlayerInventory;
import othi.thg.server.agents.player.PlayerListener;
import othi.thg.server.agents.player.PlayerTomb;
import othi.thg.server.entities.foods.Food;
import othi.thg.server.obstacles.Monster;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;

/*
 * managing player's movement on game board 
 * @author Dong Won Kim
 */
public class GameBoard extends ManagedTHGObj {

	private static final long serialVersionUID = 20070628104954L;

	private static final Logger logger = Logger.getLogger(GameBoard.class.getName());       

	private ManagedReference<GameBoardTask> gameBoardTaskRef = null; 
 
	private ManagedReference<ManagedReferenceList<PlayerListener>> playerListenerList = null;     	

	private ManagedReference<ManagedReferenceList<PlayerTomb>> playerTombList = null;       

	public GameBoard(int placeId, String placeName) {
		this.id = placeId;
		this.placeId = placeId;
		this.placeName = placeName;

		DataManager dm = AppContext.getDataManager();      
		ManagedReferenceList<PlayerListener> pl = new ManagedReferenceList<PlayerListener>();		
		ManagedReferenceList<PlayerTomb> ptl = new ManagedReferenceList<PlayerTomb>();

		playerListenerList = dm.createReference(pl);     
		playerTombList = dm.createReference(ptl);                    
	}

	public void setGameBoardTask(GameBoardTask gameBoardTask) {
		if (gameBoardTask == null) {
			gameBoardTaskRef = null;
			return;
		}

		DataManager dm = AppContext.getDataManager();
		dm.markForUpdate(this);
		gameBoardTaskRef =  dm.createReference(gameBoardTask);
	}

	public GameBoardTask getGameBoardTask() {
		if (gameBoardTaskRef == null) {
			return null;
		}

		return gameBoardTaskRef.get();
	}  

	public ManagedReference<ManagedReferenceList<PlayerListener>> getPlayerListenerList() {
		return playerListenerList;
	}	

	public ManagedReference<ManagedReferenceList<PlayerTomb>> getPlayerTombList() {
		return playerTombList;
	}

    public void addPlayerListener(PlayerListener playerListener) {
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.getForUpdate();            
        pl.add(playerListener);
    }        
    
    public void removePlayerListener(PlayerListener playerListener) {
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.getForUpdate();
        	pl.remove(playerListener);
    }       

	public void removePlayerOnClient(Player player) {
		getGameBoardTask().addMove(Commands.removePlayerCommand(player.getId()));
	}   	

	public void removePlayerTomb(PlayerTomb pTomb) {
		ManagedReferenceList<PlayerTomb> ptl = (ManagedReferenceList<PlayerTomb>) playerTombList.getForUpdate(); 
		ptl.remove(pTomb);
	}

	public void addPlayerTomb(PlayerTomb pTomb) {
		ManagedReferenceList<PlayerTomb> ptl = (ManagedReferenceList<PlayerTomb>) playerTombList.getForUpdate();            
		ptl.add(pTomb);
		getGameBoardTask().addMove(Commands.addPlayerTomb(-1, pTomb.getPosX(), pTomb.getPosY()));            
	}	

	public PlayerTomb getPlayerTomb(int id) {
		ManagedReferenceList<PlayerTomb> ptl = (ManagedReferenceList<PlayerTomb>) playerTombList.getForUpdate(); 
		return ptl.get(id);
	}    

	public PlayerListener getPlayerListener(String userName) {
		if (userName == null) {
			return null;
		}

		DataManager dm = AppContext.getDataManager();           
		return (PlayerListener) dm.getBinding(THGServerDefault.USERPREFIX + userName);
	}

	public PlayerInventory getInventory(String name){
		DataManager dm = AppContext.getDataManager();           
		return (PlayerInventory) dm.getBinding(THGServerDefault.PLAYER_INVENTORY + name);
	}

	public ArmedArmor getArmedArmor(String name){
		DataManager dm = AppContext.getDataManager();           
		return (ArmedArmor) dm.getBinding(THGServerDefault.PLAYER_ARMOR + name);
	}

	public PlayerCompetence getCompetence(String name){
		DataManager dm = AppContext.getDataManager();           
		return (PlayerCompetence) dm.getBinding(THGServerDefault.PLAYER_COMPETENCE + name);
	}

	public Food getFood(int foodId){   
		DataManager dm = AppContext.getDataManager();           
		return (Food) dm.getBinding(THGServerDefault.FOOD + getPlaceId() + ":" + foodId);
	}     

	public Monster getMonster(int monsterId){   
		DataManager dm = AppContext.getDataManager();           
		return (Monster) dm.getBinding(THGServerDefault.MONSTER + getPlaceId() + ":" + monsterId);
	}   

	public Npc getNPC(int npcId){
		DataManager dm = AppContext.getDataManager();           
		return (Npc) dm.getBinding(THGServerDefault.NPC + getPlaceId() + ":" + npcId);
	}                      

	/**
	 * Every task calls the sendMove.
	 * @param msgBuffer
	 */
	public void sendMove(ByteBuffer msgBuffer) {
		if (msgBuffer.position() > 0) {
			byte[] bytes = new byte[msgBuffer.position()];
			msgBuffer.flip();
			msgBuffer.get(bytes);

	        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
            for (int i=0; i < pl.size(); ++i) {                      
				pl.get(i).sendMove(bytes);   
			}          			
		}
	}	

	public void sendAddFood(int foodId, String name, float x, float y, int attractionPoint, String imgFile) 
	{
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
        for (int i=0; i < pl.size(); ++i) {     
        	pl.get(i).sendMove(Commands.addFoodCommand(foodId, name, x, y, attractionPoint, imgFile));
		}            
	}	

	public void sendAddMonster(int monsterID, String name, float x, float y, int level,
			int hp, int mp, int power, float speed, Direction d, String imgFile)
	{  		
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
        for (int i=0; i < pl.size(); ++i) {    
        	pl.get(i).sendMove(Commands.addMonsterCommand(monsterID, name,  x, y, level,
					hp, mp, power, d.ordinal(), imgFile));
		}
	}

	public void sendAddNpc(int npcId, String npcNickName, int level, int hp, int mp,
			String imgRef, float x, float y, Direction d)
	{         
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
        for (int i=0; i < pl.size(); ++i) {
        	pl.get(i).sendMove(Commands.addNPCCommand
					(npcId, npcNickName, level, hp, mp, imgRef, x, y, d.ordinal()));
		}
	}

	// add new player to the map of other players who are playing at the same map.
	public void sendAddPlayer(String userName)
	{                     
		Player newPlayer =  getPlayerListener(userName).getPlayer();		
		PlayerCompetence cp = newPlayer.getCompetence(userName);        
		ArmedArmor armor = newPlayer.getArmedArmor(userName);

        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
        for (int i=0; i < pl.size(); ++i) {                   
        	pl.get(i).sendMove(Commands.addPlayerCommand(newPlayer.getId(), 
					userName, cp.getGameLevel(),
					armor.getOutfitCode(), newPlayer.getPosX(),
					newPlayer.getPosY(), newPlayer.getFacing().ordinal()));                                     
		}            
	}  

	public void sendleaveGameBoard(int playerId)
	{
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
        for (int i=0; i < pl.size(); ++i) {                       
        	pl.get(i).sendMove(Commands.leaveGameBoardCommand(playerId, getPlaceName()));
		}                  		         
	}	

	public boolean isPlayerAt(float bx, float by) 
	{    
        ManagedReferenceList<PlayerListener> pl = (ManagedReferenceList<PlayerListener>) playerListenerList.get();            
        for (int i=0; i < pl.size(); ++i) {              
			if (pl.get(i).getPlayer().isAt(bx, by) == true) {
				return true;
			}        	
		}
		return false;
	}        

	public void addPlayerGrave(Player player) {
		// TODO Auto-generated method stub
	}	
	
	public void remove() {
		removeGameBoard();
	}

	private void removeGameBoard() {
		getGameBoardTask().getTaskHandle().cancel();	
		AppContext.getDataManager().removeObject(this);
	}	
}
