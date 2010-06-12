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

package othi.thg.server.definitions;

import java.util.logging.Logger;

import othi.thg.server.ManagedTHGObj;
/*
 * containing monster properties 
 * @author Dong Won Kim
 */
public class GameMonster extends ManagedTHGObj {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(GameMonster.class.getName());       
                        
    private int gameLevel;
    
    private int hp ;
    
    private int mp ;
    
    private int power ;
    
    private float speed;

    private String classRef;
        
    public GameMonster(int id, String name, String imgRef, int gameLevel, 
                       int hp, int mp, int power, float speed, String classRef) {
        this.id = id;        
        this.name = name;                
        this.imageRef = imgRef;      
        this.gameLevel = gameLevel;
        this.hp = hp;
        this.mp = mp;
        this.power = power;
        this.speed = speed;
        this.classRef = classRef;   
    }

    public String getClassRef() {
        return classRef;
    }

    public int getGameLevel() {
        return gameLevel;
    }

    public int getHp() {
        return hp;
    }
    
    public int getMp() {
        return mp;
    }

    public int getPower() {
        return power;
    }

    public float getSpeed() {
        return speed;
    }
    
}
