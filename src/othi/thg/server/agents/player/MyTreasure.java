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
 * Treasure.java
 * 
 * Created on 2007. 6. 23, 10:01:00
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package othi.thg.server.agents.player;

/**
 *
 * @author Dong Won Kim
 */
import java.io.Serializable;

import othi.thg.server.THGServerDefault;
import othi.thg.server.events.evaluation.Treasure;

import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.AppContext;

public class MyTreasure implements ManagedObject, Serializable
{ 
	private static final long serialVersionUID = 1L; 

	private boolean opened = false;
	private boolean taken = false;

	private int treasureId;

	private int placeId;

	public MyTreasure(int placeId, int treasureId) {
		this.placeId = placeId;    	
		this.treasureId = treasureId;
	}

	public Treasure getTreasure(){
		DataManager dm = AppContext.getDataManager();
		String name = dm.nextBoundName(THGServerDefault.TREASURE + placeId + ":" + treasureId);

		return (Treasure) dm.getBinding(name);
	}

	public void setOpened(boolean opened) {
		AppContext.getDataManager().markForUpdate(this);
		this.opened = opened;
	}

	public boolean isOpened() {
		return opened;
	}

	public boolean isTaken() {
		return taken;
	}

	public void setTaken(boolean taken) {
		AppContext.getDataManager().markForUpdate(this);
		this.taken = taken;
	}
}
