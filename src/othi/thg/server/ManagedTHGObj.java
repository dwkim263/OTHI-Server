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

import othi.thg.common.Commands.Direction;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;

/**
 * abstract of game objects
 * @author Dong Won Kim
 */
public abstract class ManagedTHGObj implements THGObj, Serializable {
	
    private static final long serialVersionUID =  1L;	
	
    protected int id = Integer.MIN_VALUE;
    
    protected int groupId = Integer.MIN_VALUE;
    
    protected String name = null;
    
    protected int placeId = Integer.MIN_VALUE;
    
    protected String placeName = null;
    
    protected float posX;
    
    protected float posY;
    
    protected Direction facing = Direction.SOUTH;
    
    protected String imageRef = null;

    /** The attractionPoint indicating how much the object is attractive. */
	protected int attractionPoint = Integer.MIN_VALUE;
	    
    @Override
    public void setId(int id) {
        AppContext.getDataManager().markForUpdate(this);    	
        this.id = id;
    }

    @Override    
    public int getId() {
        return id;
    }

    @Override
    public int getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(int gId) {
        AppContext.getDataManager().markForUpdate(this);
        groupId = gId;
    }
    
    @Override
    public void setName(String name) {
        AppContext.getDataManager().markForUpdate(this);
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setPlaceId(int placeId) {
        AppContext.getDataManager().markForUpdate(this);
        this.placeId = placeId;
    }

    public int getPlaceId() {
        return placeId;
    }

    @Override
    public String getPlaceName() {
        return placeName;
    }

    @Override
    public void setPlaceName(String placeName) {
        AppContext.getDataManager().markForUpdate(this);
        this.placeName = placeName;
    }
	
    public GameBoard getGameBoard() {
        if (placeName == null) return null;
        
        DataManager dm = AppContext.getDataManager();
        return (GameBoard) dm.getBinding(THGServerDefault.GAMEBOARD + placeName);        
    }
            
    @Override
    public void setImageRef(String ref) {
        AppContext.getDataManager().markForUpdate(this);
        imageRef = ref;
    }  
    
    @Override
    public String getImageRef() {
      if (imageRef == null) return null;      
      return imageRef;
    }  
        
    @Override
    public void setFacing(Direction facing){
        AppContext.getDataManager().markForUpdate(this);
        this.facing = facing;
    }
    
    @Override
    public Direction getFacing(){
        return facing;
    }

    @Override
    public void setXY(float x, float y) {
        AppContext.getDataManager().markForUpdate(this);
        posX = x;
        posY = y;
    }
    
    @Override
    public boolean setXY(int width, int height, float i, float j) {
        // clip to board
        i = Math.max(i, 0);
        i = Math.min(i, width - 1);
        j = Math.max(j, 0);
        j = Math.min(j, height - 1);

        if ((getPosX() != i) || (getPosY() != j)) {
            setPosX(i);
            setPosY(j);
            return true;
        } else { // blocked by edge
            return false;
        }
    }
           
    @Override
    public void setPosX(float x) {    
        AppContext.getDataManager().markForUpdate(this);
        posX = x;
    }

    @Override
    public void setPosY(float y) {      
        AppContext.getDataManager().markForUpdate(this);
        posY = y;
    }
    
    @Override
    public float getPosX() {
        return posX;
    }

    @Override
    public float getPosY() {
        return posY;
    }
    
    public int getAttractionPoint() {
        return attractionPoint;
    }
    
    @Override
    public boolean isAt(float posX2, float posY2) {
            float dx = Math.abs(posX - posX2);
            float dy = Math.abs(posY - posY2);

            return (( 0 <= dx && dx < 1) && (0 <= dy && dy < 1));         
    }           
    
    @Override
    public void remove(){
    	
    }
    
}
