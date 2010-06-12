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
 * player changes direction
 * @author Dong Won Kim
 */
import java.io.Serializable;
import java.util.logging.Logger;
import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.GameBoard;
import othi.thg.server.agents.player.Player;


public class Turn extends Action implements Serializable {

    private static final long serialVersionUID = 200706211015431L;
    private static final Logger logger = Logger.getLogger(Turn.class.getName()); 
    
    int placeId;
    Direction direction;

    public Turn(){
       super();
    }

    public Turn(int frameNum, String playerName, int placeId, Direction d){
            super(frameNum, playerName);
            this.placeId = placeId;
            direction = d;
    }

    @Override          
    public void turn() {
        Player player = getPlayer(getPlayerName());
        GameBoard gameBoard = player.getGameBoard();
        
        player.setFacing(direction);
        gameBoard.getGameBoardTask().addMove(Commands.turnCommand(player.getId(), placeId, direction));
//        logger.info("Player (" + player.getId() + ")" + " complete turning (" + getFrame()+ ")");                            
    }      

    @Override          
    public void move() {

    }

    @Override          
    public void act() {

    }

    @Override          
    public void talk() {

    }

    @Override          
    public void talkToNPC() {

    }      
}

