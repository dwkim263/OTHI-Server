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
import java.util.logging.Logger;

import othi.thg.common.Commands;
import othi.thg.common.Commands.Direction;
import othi.thg.server.GameBoard;
import othi.thg.server.agents.player.Player;
import othi.thg.server.entities.Portal;


/**
 * player moves forward
 * @author Dong Won Kim
 */
public class Move extends Action implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(Move.class.getName());

	int placeId;	
	float tx;
	float ty;

	Direction direction;

	public Move(){
		super();
	}

	public Move(int frameNum, String playerName, int placeId, float tx, float ty, Direction d) {
		super(frameNum, playerName);
		this.placeId = placeId;
		this.tx = tx;
		this.ty = ty;
		direction = d;
	}

	@Override
	public void move() {    
		Player player = getPlayer(getPlayerName());            
		GameBoard gameBoard = player.getGameBoard();
          
		player.setXY(tx, ty);
		player.setFacing(direction);
		gameBoard.getGameBoardTask().addMove(Commands.moveForwardCommand(player.getId(), placeId, tx, ty, direction));
		//                  logger.info("Player (" + player.getId() + ") complete moving (" + getFrame()+ ")");                    
	}

	@Override
	public void act() {

	}

	@Override
	public void turn() {

	}

	@Override
	public void talk() {

	}

	@Override
	public void talkToNPC() {

	}
}
