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
 * Regenerated.java
 * 
 * Created on Oct 31, 2007, 10:03:09 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedReference;


/**
 *
 * @author Dong Won Kim
 */
public class Regenerated<T extends ManagedTHGTask<?>> extends ManagedTHGObj {
    
	private static final long serialVersionUID = 5109122422753351660L;
	
	/** The time that the object is disappeared from the gameboard. */
	private long regenIssueTime;
	
	/** The time to wait before displaying the object on the gameboard. */
    private int reGenTime;  //second
    
    /** check if the object is under regenerating itself. */
    private boolean regenerating = false;

	protected ManagedReference<T> managedTHGTaskRef = null; 	

	public void setManagedTHGTask(T managedTHGTask) {
		if (managedTHGTask == null) {
			managedTHGTaskRef = null;
			return;
		}

		DataManager dataManager = AppContext.getDataManager();        
		dataManager.markForUpdate(this);
		managedTHGTaskRef =  dataManager.createReference(managedTHGTask);
	}

	public T getManagedTHGTask() {
		if (managedTHGTaskRef == null) {
			return null;
		}

		return managedTHGTaskRef.get();
	}    
	
    public int getReGenTime() {
        return reGenTime;
    }

    public void setReGenTime(int reGenTime) {
    	AppContext.getDataManager().markForUpdate(this);
        this.reGenTime = reGenTime;
    }

    public boolean isRegenerating() {
        return regenerating;
    }

    public void setRegenerating(boolean regenerating) {
    	AppContext.getDataManager().markForUpdate(this);
        this.regenerating = regenerating;
    }

    public long getRegenIssueTime() {
        return regenIssueTime;
    }

    public void setRegenIssueTime(long regenIssueTime) {
    	AppContext.getDataManager().markForUpdate(this);
        this.regenIssueTime = regenIssueTime;
    }        
}
