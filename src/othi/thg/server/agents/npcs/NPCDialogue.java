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
 * Answer.java
 * 
 * Created on Aug 14, 2007, 10:51:47 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server.agents.npcs;


/**
 *
 * @author Dong Won Kim
 */

import othi.thg.server.THGServerDefault;
import othi.thg.server.agents.Dialogue;
import othi.thg.server.agents.player.Player;
import othi.thg.server.agents.player.PlayerListener;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;


public class NPCDialogue extends Dialogue {
    
    private static final long serialVersionUID = 1L;    
    
    //related Quest
    private int questID;
    private boolean questSuggested = false;
    private boolean rejectQuest = false;
    private boolean acceptQuest = false;
    
    public NPCDialogue(int id, String playerName) {
        this.id = id;
        this.name = playerName;  // The player name is also this dialogue name !!!
        this.questioner = playerName;   // To easily understand this program I use this variable, "questioner", not "name".
    }

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
    	AppContext.getDataManager().markForUpdate(this);    	
        this.questID = questID;
    }

    public void setQuestSuggested(boolean questSuggested) {
    	AppContext.getDataManager().markForUpdate(this);
        this.questSuggested = questSuggested;
    }
        
	public PlayerListener getPlayerListener(String name) {
		if (name == null) {
			return null;			
		}
				
		DataManager dm = AppContext.getDataManager();           
		return (PlayerListener) dm.getBinding(THGServerDefault.USERPREFIX + name);
	}
    
	public Player getPlayer(){
		if (name == null) {
			return null;			
		}		
		
		return getPlayerListener(name).getPlayer();
	}    
    
    public int getPlayerId() {
        return id;
    }
    
    public boolean isQuestSuggested() {
        return questSuggested;
    }

    public boolean isAcceptQuest() {
        return acceptQuest;
    }

    public boolean isRejectQuest() {
        return rejectQuest;
    }

    public void setAcceptQuest(boolean acceptQuest) {
    	AppContext.getDataManager().markForUpdate(this);
        this.acceptQuest = acceptQuest;
    }

    public void setRejectQuest(boolean rejectQuest) {
    	AppContext.getDataManager().markForUpdate(this);
        this.rejectQuest = rejectQuest;
    }
}
