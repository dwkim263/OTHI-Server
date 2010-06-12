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

package othi.thg.server.stations;

import java.util.logging.Logger;

import othi.thg.server.ManagedTHGObj;

import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.AppContext;
/*
 * containing place properties 
 * @author Dong Won Kim
 */
public class Place extends ManagedTHGObj{
    
    private static final long serialVersionUID = 20070625112327L;     
        
    private static final Logger logger = Logger.getLogger(Place.class.getName());  
    
    private int level;        
    
    private String mapFileRef;    
                            
    public Place(int id, String name) {
        this.id = id;
        this.name = name;      
    }                     
   
    public void setMapFileRef (String fileRef) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);         
        mapFileRef = fileRef;
    }
    
    public String getMapFileRef() {
        if (mapFileRef == null) return null;
        return mapFileRef;
    }     
    
    public void setLevel (int levelLocation) {
        DataManager dataManager = AppContext.getDataManager();
        dataManager.markForUpdate(this);         
        level = levelLocation;
    }

    public int getLevel() {
        return level;
    }
           
    public String getCityName(){
        String cityName = name.substring(name.indexOf("_")+1);
        return cityName.substring(0, cityName.indexOf("_")-1);
    }
}
