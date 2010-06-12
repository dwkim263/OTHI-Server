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
 * Question.java
 * 
 * Created on Aug 29, 2007, 2:04:20 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server.agents.player;

/**
 *
 * @author Dong Won Kim
 */
import java.io.Serializable;

import othi.thg.server.events.orientation.Quest;


import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;


// On going quest
public class MyQuest implements ManagedObject, Serializable {    
    private static final long serialVersionUID = 1L;

    public static enum QuestState {
        Suggest, Accept, Reject, Question, Fail, Complete
    }

    public static enum InformationType {
        Help, Clue
    }

    QuestState state;

    private int questId;
    
    public MyQuest(int questId) {
        this.questId = questId;
    }    

    public Quest getQuest() {
        DataManager dm = AppContext.getDataManager();
        return (Quest) dm.getBinding(Quest.ITEM_QUEST + questId);
    }

    public boolean isAccepted() {
        return (this.state == QuestState.Accept);
    }

    public boolean isFailed() {
        return (this.state == QuestState.Fail);
    }
    
    public boolean isSuccessful() {
        return (this.state == QuestState.Complete);
    }       

    public int getQuestId() {
        return questId;
    }

    public QuestState getState() {
        return state;
    }

    public void setState(QuestState state) {
		AppContext.getDataManager().markForUpdate(this);    	
        this.state = state;
    }

    public void setState(int status) {
		AppContext.getDataManager().markForUpdate(this);
        switch (status) {
            case 0:
                this.state = QuestState.Suggest;
                break;
            case 1:
                this.state = QuestState.Accept;
                break;
            case 2:
                this.state = QuestState.Reject;
                break;
            case 3:
                this.state = QuestState.Fail;
                break;
            case 4:
                this.state = QuestState.Complete;
                break;
            default:
                this.state = null;
        }
    }

    public static int getState(QuestState qstate) {
        int stateNo = 5;
        switch (qstate) {
            case Suggest:
                stateNo = 0;
                break;
            case Accept:
                stateNo = 1;
                break;
            case Reject:
                stateNo = 2;
                break;
            case Fail:
                stateNo = 3;
                break;
            case Complete:
                stateNo = 4;
                break;
            default:
                stateNo = 5;
        }
        return stateNo;
    }

    public static int getInfomationType(InformationType iType){
        int inforType = 0;

        switch (iType) {
            case Help:
                inforType = 0;
                break;
            case Clue:
                inforType = 1;
                break;
            default:
                inforType = 2;
        }
        return inforType;
    }
}
