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

/*
 * NonPlayableCharacter.java
 * 
 * Created on Aug 9, 2007, 3:51:50 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server;

import java.util.logging.Logger;

import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.agents.player.Player;
import othi.thg.server.agents.player.PlayerInventory;
import othi.thg.server.agents.player.PlayerListener;
import othi.thg.server.agents.player.PlayerLogbook;
import othi.thg.server.obstacles.Monster;
import othi.thg.server.pathFinding.DJKPathFinder;
import othi.thg.server.pathFinding.Path;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;

/**
 *
 * @author Dong Won Kim
 */
public class NonPlayable<T extends ManagedTHGTask<?>> extends Regenerated<T> {
            
	private static final long serialVersionUID = -2010400686568199839L;
	
	private static final Logger logger = Logger.getLogger(NonPlayable.class.getName());   
    
	protected float destPosX;
    
    protected float destPosY;
    
    /** hp stands for Health Point, which indicates the life of the object. */
    protected int hp ;
    
    /** mp stands for Mana Point, which indicates the mental ability of the object. */
    protected int mp ;
    
    protected int power ;
    
    protected float speed;
    
    protected int gameLevel = 1;    
    
    protected DJKPathFinder pathFinder = null;
            
    protected Path path = null;    
    
    protected int pathIndex = 0;
           
    public TerrainMap getTerrainMap(){
        DataManager dm = AppContext.getDataManager(); 
        return (TerrainMap) dm.getBinding(THGServerDefault.TERRAIN_MAP + getPlaceId());
    }
        
	public PlayerListener getPlayerListener(String name) {
		if (name == null) {
			return null;			
		}
				
		DataManager dm = AppContext.getDataManager();           
		return (PlayerListener) dm.getBinding(THGServerDefault.USERPREFIX + name);
	}
    
	public Player getPlayer(String name){
		if (name == null) {
			return null;			
		}		
		
		return getPlayerListener(name).getPlayer();
	}
	
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
	
    public float getDestPosX() {
        return destPosX;
    }

    public float getDestPosY() {
        return destPosY;
    }

    public void setDestPosX(float destPosX) {
    	AppContext.getDataManager().markForUpdate(this);
        this.destPosX = destPosX;
    }

    public void setDestPosY(float destPosY) {
    	AppContext.getDataManager().markForUpdate(this);
        this.destPosY = destPosY;
    }
        
    public Path getPath(){
        if (path == null) return null;
        return path;
    }

    public void setPath(Path path){
    	AppContext.getDataManager().markForUpdate(this);
        this.path = path;
    }

    public void setPathIndex(int pathIndex) {
    	AppContext.getDataManager().markForUpdate(this);
        this.pathIndex = pathIndex;
    }
     
    public void increasePathIndex() {
    	AppContext.getDataManager().markForUpdate(this);
        ++pathIndex;
    }
    
    public int getPathIndex() {
        return pathIndex;
    }

    public void setHP(int hp) {
    	AppContext.getDataManager().markForUpdate(this);
        this.hp = hp; 
    }

    public int getHP() {
        return hp;
    }
    
	public int getMP() {
    	AppContext.getDataManager().markForUpdate(this);
        return mp; 
    }    
    
    public void setMP(int mp) {
    	AppContext.getDataManager().markForUpdate(this);
        this.mp = mp; 
    }
    
    public void setGameLevel(int level) {
    	AppContext.getDataManager().markForUpdate(this);
        gameLevel = level;
    }
    
    public int getGameLevel() {
        return gameLevel;
    }
        
    public float getSpeed(){
        return speed;
    }

    public void setSpeed(float speed){
    	AppContext.getDataManager().markForUpdate(this);
        this.speed = speed;
    }
    
    public void setPower(int power){
    	AppContext.getDataManager().markForUpdate(this);
        this.power = power;
    }    

    public int getPower(){
        return power;
    }
    	
    public Direction getDirection(float dx, float dy) {
        Direction direction = getFacing();
        float playerX = getPosX();
        float playerY = getPosY();     
        if (playerX == dx && playerY > dy ) {
            direction = Direction.NORTH;
        } else if (playerX == dx && playerY < dy ) {
            direction = Direction.SOUTH;
        } else if (playerX > dx && playerY == dy ) {
            direction = Direction.WEST;
        } else if (playerX < dx && playerY == dy ) {
            direction = Direction.EAST;
        }             
        return direction;            
    }

    public boolean isSameDirection(Direction inputDirection) {
       Direction d = getFacing();
       if (inputDirection.name().equals(d.name())) 
            return true;
       else
            return false;       
    }

    public void walk(int nextPosX, int nextPosY) {
        Direction inputDirection = null;
        	
        if  (getPosX() == nextPosX && getPosY() == nextPosY ) {
            increasePathIndex();         
        } else {
            inputDirection = getDirection(nextPosX, nextPosY);
        }
        
        if (inputDirection != null) {
            if (isSameDirection(inputDirection)) {
                GameBoard gameBoard = getGameBoard();                   
                if (!gameBoard.isPlayerAt(nextPosX, nextPosY) && !isMonsterAt(nextPosX, nextPosY)){
                    getManagedTHGTask().addMove(Commands.moveForwardCommand(getId(), getPlaceId(), nextPosX, nextPosY, inputDirection));
                    setXY(nextPosX, nextPosY);
                    increasePathIndex();
                    
//                 	logger.info(getName() + "(" + getId() + ")" + " complete moving forward at " + getPlaceName()  + ".");
                }             
            } else {
                setFacing(inputDirection);            
                getManagedTHGTask().addMove(Commands.turnCommand(getId(), getPlaceId(), inputDirection));

//             	logger.info(getName() + "(" + getId() + ")" + " complete moving forward at " + getPlaceName()  + ".");
            }
        }    	
    }      
    
	private boolean isMonsterAt(float bx, float by) {
		int placeId = getPlaceId();

		DataManager dm = AppContext.getDataManager();
		String binding = THGServerDefault.MONSTER + placeId + ":";
		String bindingName = dm.nextBoundName(binding);  
		while (bindingName != null && bindingName.startsWith(binding)) {
			Monster monster = (Monster) dm.getBinding(bindingName);
			if (monster.isAt(bx, by)) {
				return true;
			}
			bindingName = dm.nextBoundName(bindingName);
		}
		return false;
	}
}
