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

package othi.thg.server.agents.npcs;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.sgs.app.AppContext;

import othi.thg.server.ManagedTHGTask;
import othi.thg.server.entities.Gunshot;

/**
 * NPC Task
 * @author Dong Won Kim
 */
public class NPCTask extends ManagedTHGTask<Npc> {
	
	private static final long serialVersionUID = 1L;	
	
	private static final Logger logger = Logger.getLogger(NPCTask.class.getName());

	List<Gunshot> gunshots = new ArrayList<Gunshot>();
	
	protected long lastTalkTime;

	/**
	 * This is the task service message that gets run once per tick
	 * @throws java.lang.Exception 
	 */
	@Override
	public void run() throws Exception {                          
		long currentTimestamp = System.currentTimeMillis();
		long delta = currentTimestamp - getLastTimestamp();

		startMove();

		Speaker npc = (Speaker) getManagedTHGObj();
		
		if (npc != null) {

			long lastMoveTime = getLastMoveTime();
			long lastTalkTime = getLastTalkTime();

			float speed = npc.getSpeed();
			boolean inConversation = npc.isInConversation();

			// make NPC talk                              
			if ( (currentTimestamp - lastTalkTime) * (speed/100) > 1 && inConversation) {
				npc.tickTalk();
				setLastTalkTime(currentTimestamp);
			}

			//make NPC move                       
			if ( (currentTimestamp - lastMoveTime) * (speed/100) > 1 && !inConversation) {
				//logger.info("NPC Move speed=> " + npc.name + " speed: " + speed + " " +  (currentTime - lastMoveTime)*(speed/100));
				npc.tickMove();
				setLastMoveTime(currentTimestamp);
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

	private long getLastTalkTime() {
		return lastTalkTime;
	}

	private void setLastTalkTime(long lastTalkTime) {
    	AppContext.getDataManager().markForUpdate(this);		
		this.lastTalkTime = lastTalkTime;
	}	
}