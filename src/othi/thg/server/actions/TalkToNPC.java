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
 * talk with NPC
 * @author Dong Won Kim
 */
import java.io.Serializable;
import java.util.logging.Logger;

import othi.thg.common.Commands;
import othi.thg.server.GameBoard;
import othi.thg.server.agents.npcs.NPCDialogue;
import othi.thg.server.agents.npcs.Speaker;
import othi.thg.server.agents.player.PlayerInventory;


public class TalkToNPC extends Action implements Serializable {

    private static final long serialVersionUID = 20070814137532L;    
    private static final Logger logger = Logger.getLogger(TalkToNPC.class.getName());

    private String query;
    private int npcId;
    
    public TalkToNPC(int frameNum, String playerName, int npcId, int replyFlag, String query) {
       super(frameNum, playerName);
       this.npcId = npcId;   
       this.query = query;
    }
    
    @Override
    public void talkToNPC() {

        String echo = "[" + getPlayerName() +"]" + NPCDialogue.NEWLINE + query;
        getInventory(getPlayerName()).addDialogueHistory(echo);
        getPlayerListener(getPlayerName()).sendMove(Commands.talkCommand(getPlayerListener(getPlayerName()).getPlayerId(), getPlayerName(), echo));

        GameBoard gameBoard = getGameBoard(getPlayerName());
        Speaker npc = (Speaker) gameBoard.getNPC(npcId);

        NPCDialogue npcDialogue = npc.getDialogue(getPlayerListener(getPlayerName()).getPlayerId());

        //active and enabled npcDialogue
        if (npcDialogue != null){
            npcDialogue.setQuery(query);

            if (npcDialogue.isHelloQuery(query) ) {
                //reuse npcDialogue
                getInventory(getPlayerName()).setContactNPC(npc);
                npcDialogue.setDialogueType(NPCDialogue.DialogueType.HELLO);
                npcDialogue.setQueryReceived(true);
                npc.setInConversation(true);
            } else if (!npcDialogue.isEndConversation() && npcDialogue.isHelpQuery(query)) {
                npcDialogue.setDialogueType(NPCDialogue.DialogueType.HELP);
                npcDialogue.setQueryReceived(true);
            } else if (!npcDialogue.isEndConversation() && npcDialogue.isByeQuery(query)) {
                npcDialogue.setDialogueType(NPCDialogue.DialogueType.BYE);
                npcDialogue.setQueryReceived(true);
            } else {
                logger.info("Useless query => " + query);
            }
        } else {
            //new Dialogue With NPC
            npcDialogue = new NPCDialogue(getPlayerListener(getPlayerName()).getPlayerId(), getPlayerName());
            npcDialogue.setQuery(query);
            npcDialogue.setDialogueType(NPCDialogue.DialogueType.HELLO);
            npcDialogue.setQueryReceived(true);
            npc.addDialogue(npcDialogue);

            PlayerInventory pi = getInventory(getPlayerName());
            pi.setContactNPC(npc);
            pi.addMyDialogue(npc.getId(), npc.getName(), npcDialogue);
            npc.setInConversation(true);
        }
    }

    @Override    
    public void talk() {

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
