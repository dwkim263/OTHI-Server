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

package othi.thg.server.entities.foods;

import java.util.logging.Level;
import java.util.logging.Logger;
import othi.thg.server.ManagedTHGTask;

/**
 * Food Task
 * @author Dong Won Kim
 */
public class FoodTask extends ManagedTHGTask<Food> {

	private static final long serialVersionUID = -1213602031177582361L;

	private static final Logger logger = Logger.getLogger(FoodTask.class.getName());

	/**
	 * This is the task service message that gets run once per tick
	 * @throws java.lang.Exception 
	 */
	@Override
	public void run() throws Exception {                          
		long currentTimestamp = System.currentTimeMillis();
		long delta = currentTimestamp - getLastTimestamp();

		startMove();

		Food food = (Food) getManagedTHGObj();

		if (food != null) {

			if (food.isRegenerating()) {
				int elapsedTime = (int) ((currentTimestamp - food.getRegenIssueTime()) / SECOND);
				if (elapsedTime > food.getReGenTime()) {
					food.setRegenerating(false);

					food.getGameBoard().sendAddFood(food.getId(), food.getName(),
							food.getPosX(), food.getPosY(), food.getAttractionPoint(), food.getImageRef());  
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
}