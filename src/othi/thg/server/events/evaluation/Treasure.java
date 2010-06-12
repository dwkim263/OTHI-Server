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

package othi.thg.server.events.evaluation;

/**
 *
 * @author Dong Won Kim
 */

import java.util.Set;
import java.util.TreeSet;

import com.sun.sgs.app.AppContext;

import othi.thg.server.ManagedTHGObj;

public class Treasure extends ManagedTHGObj {
    private static final long serialVersionUID = 1L; 

    private int questID;
    private String question = null;
    private Answer sampleAnswer = null;
    private String conclusion = null;
    private Reward reward = null;
    private Set<String> stemmersOfSampleAnswer = new TreeSet<String>();

    private Penalty penalty = null; //Will be implemented.

    public Treasure(String name, int id, float x, float y) {
        this.name = name;           
        this.id = id;
        setXY(x,y);
    }

    public Treasure(String name) {
        this.name = name;
    }            

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
    	AppContext.getDataManager().markForUpdate(this);
        this.question = question;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
    	AppContext.getDataManager().markForUpdate(this);
        this.conclusion = conclusion;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
    	AppContext.getDataManager().markForUpdate(this);
        this.reward = reward;
    }

    public Penalty getPenalty() {
        return penalty;
    }

    public void setPenalty(Penalty penalty) {
    	AppContext.getDataManager().markForUpdate(this);
        this.penalty = penalty;
    }


    public Answer getSampleAnswer() {
        return sampleAnswer;
    }

    public void setSampleAnswer(String answer) {
    	AppContext.getDataManager().markForUpdate(this);
        this.sampleAnswer = new Answer(answer);
    }

    public int getQuestID() {
        return questID;
    }

    public void setQuestID(int questID) {
    	AppContext.getDataManager().markForUpdate(this);
        this.questID = questID;
    }

    public int checkAnswer(String answer) {
        answer = sampleAnswer.removeHighlyFrequentWord(answer);
        Set stemmersofAnswer =  sampleAnswer.getStemmers(answer);
        return sampleAnswer.getMark(stemmersofAnswer);
    }
}
