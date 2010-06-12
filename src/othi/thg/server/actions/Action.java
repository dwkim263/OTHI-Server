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

package othi.thg.server.actions;

import java.io.Serializable;

import othi.thg.server.GameBoard;
import othi.thg.server.THGServerDefault;
import othi.thg.server.agents.player.Player;
import othi.thg.server.agents.player.PlayerInventory;
import othi.thg.server.agents.player.PlayerListener;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
/*
 * abstract of actions
 * @author Dong Won Kim
 */
public abstract class Action implements Serializable {
    private static final long serialVersionUID =  1L;
    
    private String playerName;
    private int frameNumber;
	
    public Action(int frame, String playerName){
        frameNumber = frame;
        this.playerName = playerName;
    }
	
    public Action() {
        //for deseralizing
    }

    public abstract void act();
    public abstract void move();      
    public abstract void turn();
    public abstract void talk();    
    public abstract void talkToNPC();
    
    public int getFrame() {
        return frameNumber;
    }

    public String getPlayerName() {
        return playerName;
    }
    
	public PlayerInventory getInventory(String name){
		if (name == null) {
			return null;			
		}
		
		DataManager dm = AppContext.getDataManager();           
		return (PlayerInventory) dm.getBinding(THGServerDefault.PLAYER_INVENTORY + name);
	}    
    
	public PlayerListener getPlayerListener(String name) {
		if (name == null) {
			return null;			
		}
				
		DataManager dm = AppContext.getDataManager();           
		return (PlayerListener) dm.getBinding(THGServerDefault.USERPREFIX + name);
	}	
	
	public Player getPlayer(String name) {
		if (name == null) {
			return null;			
		}
        
		return getPlayerListener(name).getPlayer();
	}	
	
	public GameBoard getGameBoard(String name) {
		if (name == null) {
			return null;			
		}
        
		return getPlayer(name).getGameBoard();
	}		
}
