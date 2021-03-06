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
 * Weapon.java
 * 
 * Created on Oct 19, 2007, 12:52:12 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server.entities.weapons;

/**
 *
 * @author Dong Won Kim
 */

import com.sun.sgs.app.AppContext;

import othi.thg.server.ManagedTHGObj;

public abstract class Weapon extends ManagedTHGObj {

    /**
	 * 
	 */
	private static final long serialVersionUID = -310788040700924661L;

	private float range;
    
    private int power;
    
    private boolean armed = false;
        
    public Weapon(String name, float range, 
                        int power, String imgRef){
        this.name = name;
        this.range = range;
        this.power = power;
        this.imageRef = imgRef;        
    }

    public boolean isArmed() {
        return armed;
    }

    public int getPower() {
        return power;
    }

    public float getRange() {
        return range;
    }

    public void setArmed(boolean armed) {
    	AppContext.getDataManager().markForUpdate(this);
        this.armed = armed;
    }
    
    public abstract void attackMotion();
    
    public boolean isInRangeToAttack(float playerX, float playerY, float attackerX, float attackerY) {
        float dx = attackerX - playerX;
        float dy = attackerY - playerY;
        
        return (Math.abs(dx) < range && Math.abs(dy) < range); 
    }
}
