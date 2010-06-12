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
 * Portal.java
 * 
 * Created on Aug 2, 2007, 2:57:35 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server.entities;

/**
 *
 * @author Dong Won Kim
 */
import com.sun.sgs.app.AppContext;

import othi.thg.server.ManagedTHGObj;

public class Portal extends ManagedTHGObj {
    
    private static final long serialVersionUID = 20070625112329L; 
    
    private String destPlaceName = null;

    private String destPortalName = null;

    private String className = null;

    public Portal(int id, int placeId, float x, float y, String portalName) {
        this.id = id;
        this.placeId = placeId;
        posX = x;
        posY = y;
        name = portalName;
    }

    public void setClassName(String cName){
    	AppContext.getDataManager().markForUpdate(this);    	
        className = cName;
    }

    public void setDestPlaceName(String placeName){
    	AppContext.getDataManager().markForUpdate(this);    	
        destPlaceName = placeName;
    }

    public void setDestPortalName(String portalName){
    	AppContext.getDataManager().markForUpdate(this);    	
    	this.destPortalName = portalName;
    }

    public String getDestPlaceName() {
        return destPlaceName;
    }

    public String getDestPortalName() {
        return destPortalName;
    }
/*    
    public boolean isAt(float x2, float y2) {
        return (x==x2)&&(y==y2);
    }
 */
}
