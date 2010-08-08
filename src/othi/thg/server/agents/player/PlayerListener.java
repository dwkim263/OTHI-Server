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

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.logging.Logger;
import java.util.logging.Level;
import othi.thg.common.CommandListener;
import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.GameBoard;
import othi.thg.server.THGServerDefault;
import othi.thg.server.THGWorld;
import othi.thg.server.actions.*;
import othi.thg.server.definitions.GameLevel;
import othi.thg.server.entities.Portal;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.NameNotBoundException;

/**
 * communication between server and client
 * @author Dong Won Kim
 */
public class PlayerListener implements ManagedObject, Serializable, ClientSessionListener
{
	private static final long serialVersionUID =  1L;

	private static final Logger logger = Logger.getLogger(PlayerListener.class.getName());

	private boolean newPlayer;
	
	private ManagedReference<ClientSession> sessionRef = null;

	private ManagedReference<Player> playerRef = null;

	private String playerName = null;     

	private int playerId = Integer.MIN_VALUE;

	private int clientFrame = 0;

	public PlayerListener(Player player, ClientSession session) {
		DataManager dataMgr = AppContext.getDataManager();
		playerRef = dataMgr.createReference(player);
		sessionRef = dataMgr.createReference(session);
		playerName = session.getName();
		newPlayer = true;
	}    
	
	public boolean isNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(boolean isNewPlayer) {
        AppContext.getDataManager().markForUpdate(this);   		
		this.newPlayer = isNewPlayer;
	}

	public int getPlayerId() {
		return playerId;
	}

	public Player getPlayer(){        
		if (playerRef == null) {
			return null;
		}

		return playerRef.get();
	}

	public String getPlayerName() {
		return playerName;
	}

	
	public int getClientFrame() {
		return clientFrame;
	}

	public void setClientFrame(int clientFrame) {
		AppContext.getDataManager().markForUpdate(this);  
		this.clientFrame = clientFrame;
	}

	private PlayerInventory getInventory(){		
		DataManager dm = AppContext.getDataManager();           
		return (PlayerInventory) dm.getBinding(THGServerDefault.PLAYER_INVENTORY + getPlayerName());
	}

	private PlayerCompetence getCompetence(){
		DataManager dm = AppContext.getDataManager();           
		return (PlayerCompetence) dm.getBinding(THGServerDefault.PLAYER_COMPETENCE + getPlayerName());
	}

	private ArmedArmor getArmedArmor(){
		DataManager dm = AppContext.getDataManager();           
		return (ArmedArmor) dm.getBinding(THGServerDefault.PLAYER_ARMOR + getPlayerName());
	}

	private GameBoard getGameBoard(String placeName){
		if (placeName == null) {
			return null;
		}

		DataManager dm = AppContext.getDataManager();				
		return (GameBoard) dm.getBinding(THGServerDefault.GAMEBOARD + placeName);
	}

	public void setChannel(ClientSession session, ManagedReference<Channel> channel1) {
		DataManager dataMgr = AppContext.getDataManager();
        dataMgr.markForUpdate(this);   	
		
		sessionRef = dataMgr.createReference(session);
		// Lookup channel2 by name
		ChannelManager channelMgr = AppContext.getChannelManager();
		channel1.get().join(session);
		Channel channel2 = channelMgr.getChannel(THGWorld.CHANNEL_2_NAME);
		// channel2 gets a per-session listener
		channel2.join(session);        
	}

	public ClientSession getSession() {
		if (sessionRef == null) {
			return null;
		}

		ClientSession clientSession = null;

		try {
			clientSession = sessionRef.get();
		} catch (com.sun.sgs.app.ObjectNotFoundException notFoundObject) {
			logger.log(Level.WARNING, "Client session was forcely disconnted.", notFoundObject);
			disconnected(false);   
		}

		return clientSession;
	}

	/**
	 * Mark this player as logged in on the given session.
	 *
	 * @param session the session this player is logged in on
	 */
	public void setSession(ClientSession session) {		
		DataManager dataMgr = AppContext.getDataManager();
		dataMgr.markForUpdate(this);
		
		if (session == null) {
			sessionRef = null;
			return;
		}

		sessionRef = dataMgr.createReference(session);

		playerId = sessionRef.getId().intValue() * 1000;

		logger.log(Level.INFO,
				"Set session for {0} to {1}",
				new Object[] { this, session });		
	}

	public static PlayerListener loggedIn(ClientSession session) {

		String name = session.getName();
		String playerBinding = THGServerDefault.USERPREFIX + name;

		DataManager dm = AppContext.getDataManager();        
		PlayerListener playerListener;

		try {
			
			playerListener = (PlayerListener) dm.getBinding(playerBinding);    			
			playerListener.setNewPlayer(false);
			
		} catch (NameNotBoundException ex) {
			Player player = new Player(name);	
			playerListener = new PlayerListener(player, session);
			logger.log(Level.INFO, "New player[{0}] created: {1}", new Object[] { name, playerListener});

			dm.setBinding(playerBinding, playerListener);  			
		}

		playerListener.setSession(session);

		return playerListener;
	}

	public void setupPlayer() 
	{
		Player player = getPlayer();

		player.setId(getPlayerId());       
		sendMove(Commands.setIDCommand(getPlayerId()));            	

		sendPlayerInitialInfo(player);        
	}   
	
	public void setupPlayer(String placeName, float x, float y) {

		Player player = getPlayer();


		player.setId(getPlayerId());       
		sendMove(Commands.setIDCommand(getPlayerId()));            	
       			
		player.locate(getGameBoard(placeName), placeName, x, y);   

		sendPlayerInitialInfo(player);        		
	}        

	protected void sendPlayerInitialInfo(Player player)
	{
		PlayerCompetence pCompetence= getCompetence();
		ArmedArmor pArmor = getArmedArmor();

		GameLevel gameLevel = pCompetence.getLevelEntity();
		int maxHp = gameLevel.getHp();
		int maxMp = gameLevel.getMp();
		int maxExp = gameLevel.getExp();
		sendMove(Commands.initializePlayerCommand(
				getPlayerId(),
				pCompetence.getGameLevel(), pCompetence.getMyHP(), maxHp,
				pCompetence.getMyMP(), maxMp,
				pCompetence.getMyEP(), maxExp,
				pCompetence.getMyMoney(),
				getInventory().getDialogueHistory().toArray(new String[0]), pArmor.getOutfitCode())
		);
	}

	public void sendMove(byte[] bytes) {   
		ClientSession clientSession = getSession();

		if (clientSession != null) {
			clientSession.send(ByteBuffer.wrap(bytes));
		}
	}    

	@Override
	public void disconnected(boolean arg0) {
		setSession(null);		
		getGameBoard(getPlayer().getPlaceName()).removePlayerListener(this);	
	}

	private void sendChangedLevelInfo(int id){
		PlayerCompetence playerCompetence = getCompetence();
		int gameLevel = playerCompetence.getGameLevel();

		GameLevel level = playerCompetence.getLevelEntity();

		sendMove(Commands.levelCommand(
				id, gameLevel, playerCompetence.getMyHP(), level.getHp(), 
				playerCompetence.getMyMP(), level.getMp(), playerCompetence.getMyEP(), level.getExp(), level.getPower()
		));               
	}    

	private void sendPassAwayInfo(int id){ 
		PlayerCompetence playerCompetence = getCompetence();
		int gameLevel = playerCompetence.getGameLevel();
		GameLevel level = playerCompetence.getLevelEntity();        

		sendMove(Commands.passAwayCommand(
				id, gameLevel, playerCompetence.getMyHP(), level.getHp(), 
				playerCompetence.getMyMP(), level.getMp(), playerCompetence.getMyEP(),
				level.getExp(), playerCompetence.getMyMoney()
		));             
	}    

	protected void setNextAction(Action action) {
		getPlayer().setNextAction(action);
	}  

	protected void startPlay() {

		Player player = getPlayer();
		String placeName = player.getPlaceName();

		player.enterGameBoard(placeName);

		player.init();
		
		sendMove(Commands.startCommand(getPlayerId()));                
		logger.info("Player " + player.getName() + "[" + getPlayerId() + "] starts playing");        
	}

	public void stopPlay() {
		getPlayer().setStopped(true);			
	}    

	protected void leaveGameBoard(String placeName) {
		getGameBoard(placeName).removePlayerListener(this);	
		if (getPlayer().isStopped()) {
			sendMove(Commands.stopCommand(getPlayerId()));	
		}
	}    
	    
	@Override
	public void receivedMessage(ByteBuffer buff) {
		while (buff.hasRemaining()) {
			Commands.parseCommandBuffer(buff, new CommandListener() {
				@Override                
				public void commandHp(int id, int hp){

				}

				@Override                                
				public void commandAddFood(int id, String name, float x, float y, int attractionPoint, String imgRef) {

				}

				@Override
				public void commandClearDialogueHistory(int id){
					//                        Player player = getPlayer();
					getInventory().clearDialogueHistory();
					sendMove(Commands.clearMsgLogCommand(id));   
				}

				@Override
				public void commandClearMsgLog(int id){

				}

				@Override                
				public void commandBlock(int id, float x, float y){

				}   

				@Override
				public void commandRequestLevel(int id){
					sendChangedLevelInfo(id);
				}

				@Override
				public void commandRequestPassAway(int id){
					sendPassAwayInfo(id);
				}

				@Override
				public void commandPassAway(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int money){

				}

				@Override
				public void commandLevel(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int power) {

				}

				@Override                   
				public void commandPayment(int id, int treasureId, String name, int hp, int mp, int point, int money, String armor, String tool, String weapon){

				}

				@Override                   
				public void commandAnswerQuiz(int id,  int placeId, int treasureId, int topicId, String answer) {
					getInventory().evaluateQuest(placeId, treasureId, topicId, answer);
				}

				@Override                              
				public void commandQuestIntroduction(int id, int npcId, String npcName, int questID, String questName, String introduction){

				}

				@Override
				public void commandQuestQuestion(int id, int npcId, String npcName, int questID, String questName, String question) {

				}

				@Override
				//For help and clue
				public void commandQuestInformation(int id, int npcId, String npcName, int questID, String questName, String information, int iType) {

				}

				@Override              
				public void commandChangeQuestStatus(int id, int questID, int status) {

				}        

				@Override                
				public void commandAttackMonster(int id, int placeId, int monsterId){
					getPlayer().firstAttackMonster(monsterId);
				}

				@Override
				public void commandTalk(int id, String receiverName, String dailogue){
					if (dailogue != null) setNextAction(new Talk(getClientFrame(), getPlayerName(), receiverName, dailogue));                        
				}        

				@Override
				public void commandTalktoNPC(int npcId, int replyFlag, String message){  
					if (message != null)
						setNextAction(new TalkToNPC(getClientFrame(), getPlayerName(), npcId, replyFlag, message));
				}        

				@Override                    
				public void commandAddPlayer(int id, String name, int level, int outfitCode,
						float x, float y, int facing)
				{
				}

				@Override
				public void commandAddNPC(int id, String name, int level, int hp, int mp,
						String imgRef, float x, float y, int facing)
				{

				}

				@Override
				public void commandAddTreasure(int treasureId, int questID, float x, float y) {
					// TODO Auto-generated method stub
				}         

				@Override
				public void commandFrame(int id, int frameCount) {
					setClientFrame(frameCount);
				}

				@Override
				public void commandJoinGroup(int id) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandKill(int id) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandLeaveGameBoard(int id, String placeName) {
					leaveGameBoard(placeName);
				}      
				
				@Override
				public void commandPortal(int id, int placeId, String portalName) {
					  Player player = getPlayer();
	                  Portal portal = player.getPortal(placeId, portalName);
	                  if (portal != null) {
	                      player.moveToOtherPlace(portal.getDestPlaceName(), portal.getDestPortalName());
	                  }   					
				}
							
				@Override				
		        public void commandAddPortal(int id, int placeId, String portalName, float x, float y, int isOneWay){
		        					
		        }
		        
				@Override
				public void commandMoveForward(int id, int placeId, float tx, float ty, Direction direction) {                        
					setNextAction(new Move(getClientFrame(), getPlayerName(), placeId, tx, ty, direction));
				}
				
				@Override                     
				public void commandTurn(int id, int placeId, Direction direction) {                    	
					setNextAction(new Turn(getClientFrame(), getPlayerName(), placeId, direction));
				}
				
				@Override
				public void commandScore(int id, int score) {
					logger.info("Score changed to : " + score );                        
					getCompetence().setScore(score);
				}

				@Override
				public void commandInitializePlayer(
						int id, int level, int hp, int maxHp, int mp, int maxMp, 
						int exp, int maxExp, int money,
						String[] dialogues, int outfitCode )
				{
					
				}

				@Override                    
				public void commandEnterGameBoard(int id, int placeId, String mapFileRef) {

				}   

				@Override                                        
				public void commandStart(int id) {
					startPlay();
				}

				@Override
				public void commandStop(int id) {
					stopPlay();                        
				}      

				@Override
				public void commandOpenTreasure(int id, int placeId, int treasureId, int topicId){                        
					getInventory().openTreasure(placeId, treasureId, topicId);
				}   

				@Override
				public void commandSetID(int id) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandAddMonster(int id, String name, float x, float y, int gameLevel, 
						int hp, int mp, int power,                      
						Direction direction, String imgRef)
				{
					// TODO Auto-generated method stub
				}

				@Override
				public void commandAddMonsterTomb(int id, float x, float y) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandRemoveMonster(int id) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandRemoveMonsterTomb(int id, float x, float y) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandRemovePlayer(int id) {

				}

				@Override                    
				public void commandGiveQuestion(int id, int treasureId, int questID){

				}

				@Override
				public void commandGiveMark(int id, int questID, int mark) {

				}

				@Override
				public void commandAddPlayerTomb(int id, float x, float y) {
					// TODO Auto-generated method stub
				}

				@Override
				public void commandRemovePlayerTomb(int id, float x, float y)
				{
					// TODO Auto-generated method stub
				}

				@Override                    
				public void commandLogin(String userName, String password) {
					// TODO need to move user resolution to here						
				}

				@Override                    
				public void commandRemoveFood(int id, float x, float y) {
					// TODO Auto-generated method stub
				}
			});
		}
	}
}
