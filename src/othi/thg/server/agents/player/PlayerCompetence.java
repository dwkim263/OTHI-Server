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

package othi.thg.server.agents.player;

import java.io.Serializable;

import othi.thg.server.THGServerDefault;
import othi.thg.server.definitions.GameLevel;
import othi.thg.server.events.evaluation.Reward;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;

/*
 * player's competence 
 * @author Dong Won Kim
 */
public class PlayerCompetence implements ManagedObject, Serializable {
    private static final long serialVersionUID = 1L;
    
    //The myEP is my game experience point.
    private int myEP = 0;       
            
    private int gameLevel = 1;
    
    //The myHP is my game health point.
    private int myHP = 100;
    
    //The myMP is my game mana point.
    private int myMP = 100;    
    
    private int myMoney = 100;
    
    private String playerName;
    
    public PlayerCompetence(String name) {
        playerName = name;
    }     
        
    public String getPlayerName() {
		return playerName;
	}

 	public void setGameLevel (int gameLevel){
        AppContext.getDataManager().markForUpdate(this); 		
        this.gameLevel = gameLevel;
    }  
 	
 	public void increaseGameLevel (){
        AppContext.getDataManager().markForUpdate(this); 		
        ++gameLevel;
    }  
    
 	public void decreaseGameLevel (){
        AppContext.getDataManager().markForUpdate(this); 		
        --gameLevel;
    }  
 	
    public int getGameLevel (){
        return gameLevel;
    }    
    
    public GameLevel getLevelEntity() {
        DataManager dm = AppContext.getDataManager();        
        return (GameLevel) dm.getBinding(THGServerDefault.LEVEL + gameLevel);
    }
    
    public void setMyHP (int hp) {    	
        AppContext.getDataManager().markForUpdate(this);       
        myHP = hp;          
    }
    
    public int getMyHP (){      
        return myHP;
    }
        
    public void setMyMP (int mp) {
        AppContext.getDataManager().markForUpdate(this);               
        myMP = mp;       
    }
    
    public int getMyMP (){   
        return myMP;
    }   
    
    public void setMyEP(int exp){
        AppContext.getDataManager().markForUpdate(this);   
        myEP = exp;    	
    }
    
    public int getMyEP() {       
        return myEP;    
    }   
    
    public void setScore(int exp) {     
    	setMyEP(exp);
        
        GameLevel level = getLevelEntity();          
        if (getMyEP() >= level.getExp()) {
            levelUp();
        }             
    }
    
    public void addHP (int hp) {
    	setMyHP (getMyHP() + hp); 

        GameLevel level = getLevelEntity();          
        if (getMyHP() > level.getHp()) {
        	setMyHP(level.getHp());
        } else if (getMyHP() < 0) {
            passAway();            
        }
    }
        
    public void addMP (int mp) {         
        setMyMP(getMyMP() + mp);

        GameLevel level = getLevelEntity();         
        if (getMyMP() > level.getMp()) {
        	setMyMP(level.getMp());
        } else if (getMyMP() < 0) {
        	setMyMP(0);
        }
    }
    
    public int addEP(int exp) {
        int levelDownFlag = 0; 
        
        setMyEP(getMyEP() + exp);
        
        GameLevel level = getLevelEntity();      
        if (getMyEP() >= level.getExp()) {
            levelUp();
        } else if (getMyEP() < 0) {    
            levelDown();
            levelDownFlag = -1;     
        }
        return levelDownFlag;
    }
    
    private void levelUp() {       
    	increaseGameLevel();   
        setMyEP(0);

        GameLevel level = getLevelEntity();        
        setMyHP(level.getHp());
        setMyMP(level.getMp());
    }
    
    private void levelDown () {
        if (getGameLevel() == 1) {
            setMyEP(0);
        }
        else {
        	decreaseGameLevel();        	
            GameLevel level = getLevelEntity();              
            setMyHP(level.getHp());
            setMyMP(level.getMp());    
            setMyEP(level.getExp() + getMyEP());
        }   
    }
    
    private void passAway() {
        GameLevel level = getLevelEntity();             
        
        //Reduce experience
        int levelDownFlag = addEP(-1 * level.getExp() / 10);
        
        //Reduce money        
        addMyMoney(-1 * getMyMoney()/10);
        
        // Normal
        if (levelDownFlag != -1) {
            setMyHP(level.getHp());
            setMyMP(level.getMp());          
        } else {
            level = getLevelEntity();            
        }   
    }
    
    public void addMyMoney(int money) {
    	setMyMoney(getMyMoney()+money);
    }    

    public void setMyMoney(int myMoney) {
        AppContext.getDataManager().markForUpdate(this);   
		this.myMoney = myMoney;
	}
    
    public int getMyMoney() {
        return myMoney;
    }               
    
    public void addCompetence(Reward reward) {
        addHP(reward.getHp());
        addMP(reward.getMp());    
        addEP(reward.getExp());             
        addMP(reward.getMp());             
        addMyMoney(reward.getMoney());                                          
    }            
}
