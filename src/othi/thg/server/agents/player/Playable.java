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

import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.GameBoard;
import othi.thg.server.ManagedTHGObj;
import othi.thg.server.THGServerDefault;
import othi.thg.server.TerrainMap;
import othi.thg.server.actions.Action;
import othi.thg.server.entities.Portal;
import othi.thg.server.stations.Place;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;

/*
 * abstract of playable objects
 * @author Dong Won Kim
 */
public abstract class Playable extends ManagedTHGObj {

	private static final long serialVersionUID =  1L;

	private static final int TOMBROUNDS = 6;

	private boolean enabled = false;// allowed to start actions

	private boolean stopped = false;  // If stop playing or not

	private int lives = 3;            

	private Action nextAction;

	private int lastProcessedFrame = -1;        

	public abstract void init();

	/**
	 * This takes the shot for any hunters shooting We have to do shots
	 * before move so we don't move targets out of the way.
	 * @param frame 
	 */
	public abstract void tickAct();

	public abstract void tickTurn();

	public abstract void tickTalk();

	public abstract void tickTalktoNPC();

	/**
	 * This takes the move for this tick Players who have shot can't move
	 * this tick
	 * @param frame 
	 */
	public abstract void tickMove();   

	public abstract void disable();

	public PlayerInventory getInventory(String name){
		if (name == null) {
			return null;			
		}

		DataManager dm = AppContext.getDataManager();           
		return (PlayerInventory) dm.getBinding(THGServerDefault.PLAYER_INVENTORY + name);
	}

	public PlayerLogbook getLogbook(String name){
		if (name == null) {
			return null;			
		}

		DataManager dm = AppContext.getDataManager();
		return (PlayerLogbook) dm.getBinding(THGServerDefault.PLAYER_LOGBOOK + name);
	}

	public ArmedArmor getArmedArmor(String name){
		if (name == null) {
			return null;			
		}

		DataManager dm = AppContext.getDataManager();           
		return (ArmedArmor) dm.getBinding(THGServerDefault.PLAYER_ARMOR + name);
	}

	public ArmedWeapon getArmedWeapon(String name){
		if (name == null) {
			return null;			
		}

		DataManager dm = AppContext.getDataManager();           
		return (ArmedWeapon) dm.getBinding(THGServerDefault.PLAYER_WEAPON + name);
	}    

	public PlayerCompetence getCompetence(String name){
		if (name == null) {
			return null;			
		}

		DataManager dm = AppContext.getDataManager();           
		return (PlayerCompetence) dm.getBinding(THGServerDefault.PLAYER_COMPETENCE + name);
	}

	public PlayerListener getPlayerListener(String name) {
		if (name == null) {
			return null;			
		}

		DataManager dm = AppContext.getDataManager();           
		return (PlayerListener) dm.getBinding(THGServerDefault.USERPREFIX + name);
	}

	public GameBoard getGameBoard(String placeName) {
		if (placeName == null) return null;

		DataManager dm = AppContext.getDataManager();
		return (GameBoard) dm.getBinding(THGServerDefault.GAMEBOARD + placeName);        
	}

	public Place getPlace(String placeName) {
		if (placeName == null) return null;    

		DataManager dm = AppContext.getDataManager();
		return (Place) dm.getBinding(THGServerDefault.PLACE + placeName);    
	}

	public Portal getPortal(int placeId, String portalName) {
		if (placeId == Integer.MIN_VALUE || portalName == null) return null;

		DataManager dm = AppContext.getDataManager();
		return (Portal) dm.getBinding(THGServerDefault.PORTAL + placeId + ":" + portalName);
	}	
	   
	public TerrainMap getTerrainMap(int placeId) {
		if (placeId == Integer.MIN_VALUE) return null;    

		DataManager dm = AppContext.getDataManager();
		return (TerrainMap) dm.getBinding(THGServerDefault.TERRAIN_MAP + placeId);
	}	
		
	public float getSpeed() {
		int gameLevel = getCompetence(getName()).getGameLevel();
		return (1.0f + gameLevel/10);
	}            

	public Action getNextAction() {
		return nextAction;
	}
	
	public void setNextAction(Action nextAction) {
		AppContext.getDataManager().markForUpdate(this);
		
		if (nextAction == null) {
			this.nextAction = null;
			return;
		}
		
		this.nextAction = nextAction;
	}
	
	public float[] step(Direction direction) {
		return Commands.step(getPosX(), getPosY(), direction);
	}        

	public void setLives(int n) {
		AppContext.getDataManager().markForUpdate(this);
		lives = n;
	}

	public int getLives() {
		return lives;
	}
	
	private void decreaseLives(){
		AppContext.getDataManager().markForUpdate(this);		
		--lives;
	}

	public void kill() {        
		decreaseLives();   
		getGameBoard().addPlayerTomb(new PlayerTomb(TOMBROUNDS, id, name, posX, posY));        
		disable();
	}    

	public void setEnabled(boolean enabled) {
		AppContext.getDataManager().markForUpdate(this);
		this.enabled = enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}
	
	public void updateLastProcessedFrame() {
		if (nextAction != null) {
			setLastProcessedFrame(nextAction.getFrame());
		}
	}

	public void setLastProcessedFrame(int frame) {
		AppContext.getDataManager().markForUpdate(this);	
		lastProcessedFrame = frame;
	}

	public int getLastProcessedFrame() {
		return lastProcessedFrame;
	}


	public Place getPlace() {
		if (placeName == null) {
			return null;
		}

		DataManager dm = AppContext.getDataManager();        
		return (Place) dm.getBinding(THGServerDefault.PLACE + placeName);
	}        

	public void setStopped(boolean stopped) {
		AppContext.getDataManager().markForUpdate(this);	   
		this.stopped = stopped;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void remove() {
		removePlayer();    
	}

	private void removePlayer() {
		AppContext.getDataManager().removeObject(this); // take board out of
	}
}
