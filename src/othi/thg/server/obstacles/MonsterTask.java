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

package othi.thg.server.obstacles;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;

import othi.thg.common.Commands;
import othi.thg.server.ManagedTHGTask;

/**
 * Monster master
 * @author Dong Won Kim
 */
public class MonsterTask extends ManagedTHGTask<Monster> {

	private static final long serialVersionUID = 20070628104954L; 

	private static final Logger logger = Logger.getLogger(MonsterTask.class.getName());       

	/** check if the monster is alive or not. */	
	private boolean alive = true;

	/**
	 * This is the task service message that gets run once per tick
	 * @throws java.lang.Exception 
	 */
	@Override
	public void run() throws Exception {                         


		long currentTimestamp = System.currentTimeMillis();
		long delta = currentTimestamp - getLastTimestamp();

		startMove(); // mark the start of a new move in the buffer

		Monster monster = (Monster) getManagedTHGObj();    		
		if (monster != null) {					
			if (!isAlive()) {				
				MonsterTomb tomb = monster.getMonsterTomb();				
				tomb.tick();
				if (tomb.isDone()) {
					setAlive(true);
					addMove(Commands.removeMonsterTombCommand(-1, tomb.getPosX(), tomb.getPosY()));
				}						
			} else { 
				long lastMoveTime = getLastMoveTime();
				float speed = monster.getSpeed();
				
//				logger.log(Level.INFO, "The time gap is {0}.", (currentTimestamp - lastMoveTime) * (speed/100));
				
				if ((currentTimestamp - lastMoveTime) * (speed/100) > 1) {
					monster.tickMove();
					setLastMoveTime(currentTimestamp);
					//logger.info("Monster name => " + monster.getName());
				}    
			}
		}

		sendMove();

		setLastTimestamp(currentTimestamp);

		/*
		logger.log(Level.INFO,
				"timestamp = {0,number,#}, delta = {1,number,#}",
				new Object[] { currentTimestamp, delta }
		);   	
		 */	
	}	

	private boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		DataManager dataManager = AppContext.getDataManager();
		dataManager.markForUpdate(this);		
		this.alive = alive;
	}		
}