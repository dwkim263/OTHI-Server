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

/**
 * player's talking
 * @author Dong Won Kim
 */
import java.io.Serializable;

import othi.thg.common.Commands;
import othi.thg.server.agents.Dialogue;
import othi.thg.server.agents.player.PlayerInventory;


public class Talk extends Action implements Serializable {
    private static final long serialVersionUID = 20070731137532L;    
    private String message;
    private String receiverName;
    
    public Talk(){
        super();
    }
        
    public Talk(int frameNum, String callerName, String receiverName, String message) {
       super(frameNum, callerName);
       this.receiverName = receiverName;       
       this.message = message;
    }
    
    @Override    
    public void talk() {

        PlayerInventory pi = getInventory(getPlayerName());
     
        String talkMessage = "[" + getPlayerName() +"]"  + Dialogue.NEWLINE + message;
        pi.addDialogueHistory(talkMessage);
        getPlayerListener(getPlayerName()).sendMove(Commands.talkCommand(getPlayerListener(getPlayerName()).getPlayerId(), getPlayerName(), talkMessage));

        PlayerInventory cpi = getInventory(receiverName);
        cpi.addDialogueHistory(talkMessage);
        getPlayerListener(receiverName).sendMove(Commands.talkCommand(getPlayerListener(getPlayerName()).getPlayerId(), getPlayerName(), talkMessage));        
    }

    @Override    
    public void talkToNPC() {

    }
    
    @Override    
    public void move() {

    }
    
    @Override
    public void act() {

    }
    
    @Override
    public void turn() {

    }
}
