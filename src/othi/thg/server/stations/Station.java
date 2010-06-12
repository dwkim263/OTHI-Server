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
 * LearningObject.java
 * 
 * Created on 2007. 7. 2, 5:30:53
 * 
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package othi.thg.server.stations;

/**
 *
 * @author Dong Won Kim
 */

import com.sun.sgs.app.AppContext;

import othi.thg.server.ManagedTHGEntity;

public class Station extends ManagedTHGEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = -5848364323973129738L;

	public static enum StationType {
            Orientation, Help, Obstacle, Evaluation
    }

    private String placeName;
    private String placeNickName;
    private String topic;
    private StationType stationType;

    public Station(int id, String placeName, String placeNickName, String topic, String name){
        this.id = id;
        this.name = topic + ":" + name;
        this.placeName = placeName;
        this.placeNickName = placeNickName;
        this.topic = topic;
    }

    public StationType getStationType() {
        return stationType;
    }

    public void setStationType(StationType stationType) {
		AppContext.getDataManager().markForUpdate(this);
        this.stationType = stationType;
    }
    
    public void setStationType(String name){
		AppContext.getDataManager().markForUpdate(this);
        if (name.equals(StationType.Orientation.toString())) {
            stationType = StationType.Orientation;
        } else if (name.equals(StationType.Evaluation.toString())) {
            stationType = StationType.Evaluation;
        } else if (name.equals(StationType.Obstacle.toString())) {
            stationType = StationType.Obstacle;
        } else {
            stationType = StationType.Help;
        }
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
		AppContext.getDataManager().markForUpdate(this);    	
        this.placeName = placeName;
    }

    public String getPlaceNickName() {
        return placeNickName;
    }

    public void setPlaceNickName(String placeNickName) {
		AppContext.getDataManager().markForUpdate(this);    	
        this.placeNickName = placeNickName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
		AppContext.getDataManager().markForUpdate(this);    	
        this.topic = topic;
    }
}
