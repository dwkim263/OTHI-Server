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
 * AbstractThoObj.java
 * 
 * Created on 2007. 9. 16, 5:29:27
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server;

/**
 *
 * @author Dong Won Kim
 */
import java.io.Serializable;
import java.nio.ByteBuffer;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.PeriodicTaskHandle;

/**
 * abstract of game objects
 * @author Dong Won Kim
 */
public abstract class ManagedTHGTask<T extends ManagedTHGObj> implements THGTask, ManagedObject, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4730186993574004320L;

	protected final int BUFFERSIZE = 512;  //1024

	// allocate a buffer for output
	transient protected ByteBuffer moveBuffer;
	
	protected static final long SECOND = 1000;

	protected PeriodicTaskHandle taskHandle = null;

	private long lastTimestamp = System.currentTimeMillis();
	
    private long lastMoveTime;

	protected ManagedReference<T> managedTHGObjRef = null; 	

	public void setManagedTHGObj(T managedTHGObj) {
		if (managedTHGObj == null) {
			managedTHGObjRef = null;
			return;
		}
		DataManager dataManager = AppContext.getDataManager();  
		dataManager.markForUpdate(this);
		managedTHGObjRef =  dataManager.createReference(managedTHGObj);
	}

	public T getManagedTHGObj() {
		if (managedTHGObjRef == null) {
			return null;
		}

		return managedTHGObjRef.get();
	}

	public PeriodicTaskHandle getTaskHandle() {
		return taskHandle;
	}

	public void setTaskHandle(PeriodicTaskHandle taskHandle) {
        AppContext.getDataManager().markForUpdate(this);		
		this.taskHandle = taskHandle;
	}

	public void startMove() {
        AppContext.getDataManager().markForUpdate(this);		
		if (moveBuffer == null) {
			moveBuffer = ByteBuffer.allocate(BUFFERSIZE);
		} else {
			moveBuffer.clear();
		}
	}

	public void addMove(byte[] move) {
        AppContext.getDataManager().markForUpdate(this);		
		moveBuffer.put(move);
	}

	public void sendMove() {		
		getManagedTHGObj().getGameBoard().sendMove(moveBuffer);
	}
	
	public void remove() {
		removeManagedTHGTask();
	}

	private void removeManagedTHGTask() {
		getTaskHandle().cancel();
		AppContext.getDataManager().removeObject(this);
	}		
		
    public long getLastTimestamp() {
		return lastTimestamp;
	}

	public void setLastTimestamp(long lastTimestamp) {
        AppContext.getDataManager().markForUpdate(this);   		
		this.lastTimestamp = lastTimestamp;
	}

	public long getLastMoveTime() {
        return lastMoveTime;
    }

    public void setLastMoveTime(long lastMoveTime) {    	
        AppContext.getDataManager().markForUpdate(this);    	
        this.lastMoveTime = lastMoveTime;
    }
}
