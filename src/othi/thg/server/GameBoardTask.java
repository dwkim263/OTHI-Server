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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;

import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.agents.player.ArmedArmor;
import othi.thg.server.agents.player.PlayerCompetence;
import othi.thg.server.agents.player.Player;
import othi.thg.server.agents.player.PlayerListener;
import othi.thg.server.agents.player.PlayerTomb;

/*
 * managing player's movement on game board 
 * @author Dong Won Kim
 */
public class GameBoardTask extends ManagedTHGTask<GameBoard> {

	private static final long serialVersionUID = 20070628104954L;

	private static final Logger logger = Logger.getLogger(GameBoardTask.class.getName());       

	private List<String> forcelyLeftPlayerName = new ArrayList<String>();
	
	private List<String> leavingPlayerNames = new ArrayList<String>();

	/**
	 * This is the task service message that gets run once per tick
	 * @throws java.lang.Exception 
	 */
	@Override
	public void run() throws Exception {                          

		long currentTimestamp = System.currentTimeMillis();
		long delta = currentTimestamp - getLastTimestamp();

		//logger.info("Number of placeIDs => " + placeIDs.size() );
		startMove(); // mark the start of a new move in the buffer                

		ManagedReferenceList<PlayerTomb> plt = getManagedTHGObj().getPlayerTombList().get();    		
		for (int i=0; i < plt.size(); ++i) {
			PlayerTomb tomb = plt.get(i);             
			if (tomb.isTicking()) {
				tomb.tick();
				if (tomb.isDone()) {
					getManagedTHGObj().removePlayerTomb(tomb);
					String userName = tomb.getName();
					Player player = getManagedTHGObj().getPlayerListener(userName).getPlayer();

					if (player.getLives() > 0) {
						player.setEnabled(true);      
						player.updateLastProcessedFrame();
						
						addMove(Commands.removePlayerTombCommand(-1, tomb.getPosX(), tomb.getPosY()));

						PlayerCompetence playerCompetence = getManagedTHGObj().getCompetence(userName);
						ArmedArmor armor = getManagedTHGObj().getArmedArmor(userName);

						addMove(Commands.addPlayerCommand(
								player.getId(), player.getName(),
								playerCompetence.getGameLevel(), armor.getOutfitCode(),
								player.getPosX(), player.getPosY(), Direction.SOUTH.ordinal()));
					} else {
						addForcelyLeftPlayerName(player.getName());                                          
					}
				}
			}
		}

		//Act
        ManagedReferenceList<PlayerListener> pl = getManagedTHGObj().getPlayerListenerList().get();        
        for (int i=0; i < pl.size(); ++i) {     
			PlayerListener playerListener = pl.get(i); 
//			logger.log(Level.INFO, "Player name is {0}", playerListener.getPlayerName());
			Player player = playerListener.getPlayer();
			if (player.isEnabled()){
				player.tickTurn();       //turn
				player.tickAct();        //shoot
				player.tickTalk();       //talk
				player.tickTalktoNPC();  //talkToNPC
				player.tickMove();       //move
				player.updateLastProcessedFrame();
				if (player.isStopped()) {
					addLeavingPlayerNames(player.getName());
					player.setEnabled(false);
				}
			}
		}

		// stop playing if players have died more three times.
		for (String userName : forcelyLeftPlayerName) {
			PlayerListener playerListener = getManagedTHGObj().getPlayerListener(userName);
			playerListener.stopPlay();
		}
		
		// remove players who are leaving the game board.
		for (String userName : leavingPlayerNames) {
			PlayerListener playerListener = getManagedTHGObj().getPlayerListener(userName);       			
			addMove(Commands.leaveGameBoardCommand(playerListener.getPlayerId(), getManagedTHGObj().getPlaceName()));
		}

		sendMove();

		clearForcelyLeftPlayerName();

		clearLeavingPlayerNames();
		
		setLastTimestamp(currentTimestamp);

		/*
	 logger.log(Level.INFO,
			 "timestamp = {0,number,#}, delta = {1,number,#}",
			 new Object[] { currentTimestamp, delta }
	 );   
		 */             
	}
	
	private void clearForcelyLeftPlayerName(){
		AppContext.getDataManager().markForUpdate(this);
		forcelyLeftPlayerName.clear();
	}
	
	private void clearLeavingPlayerNames() {
		AppContext.getDataManager().markForUpdate(this);
		leavingPlayerNames.clear();
	}
	
	private void addForcelyLeftPlayerName(String playerName) {
		AppContext.getDataManager().markForUpdate(this);
		forcelyLeftPlayerName.add(playerName);
	}

	private void addLeavingPlayerNames(String playerName) {
		AppContext.getDataManager().markForUpdate(this);		
		leavingPlayerNames.add(playerName);
	}

	public void sendMove() {		
		getManagedTHGObj().sendMove(moveBuffer);
	}
}
