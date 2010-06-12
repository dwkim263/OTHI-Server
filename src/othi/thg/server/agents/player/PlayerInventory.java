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
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import othi.thg.common.Commands;
import othi.thg.server.ManagedReferenceList;
import othi.thg.server.THGServerDefault;
import othi.thg.server.agents.Dialogue;
import othi.thg.server.agents.npcs.Npc;
import othi.thg.server.agents.npcs.Speaker;
import othi.thg.server.events.evaluation.Penalty;
import othi.thg.server.events.evaluation.Reward;
import othi.thg.server.events.evaluation.Treasure;
import othi.thg.server.events.orientation.Quest;
import othi.thg.server.stations.EvaluationStation;
import othi.thg.server.stations.HelpStation;
import othi.thg.server.stations.OrientationStation;
import othi.thg.server.stations.Station.StationType;


import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.ManagedObject;
import com.sun.sgs.app.DataManager;
/**
 * managing player's inventory
 * @author Dong Won Kim
 */
public class PlayerInventory implements ManagedObject, Serializable {

    private static final long serialVersionUID =  200707051132L;

    private static final Logger logger = Logger.getLogger(PlayerInventory.class.getName());

    private String playerName;
    
    private String guideName = null;

    private int topicId;

    private String topicName = null;

    private String stationName = StationType.Orientation.toString();

    private String treasureBoxName = null;

    private int nextReplyIndex = 0;
    
    private List<String> dialogueHistory = new LinkedList<String>();        
    
    private ManagedReference<? extends Npc> contactNPCRef;
    
    public PlayerInventory(String name) {
        playerName = name;
    }                         
    
	public PlayerListener getPlayerListener() {
		if (playerName == null) {
			return null; 			
		}
		
		DataManager dm = AppContext.getDataManager();           
		return (PlayerListener) dm.getBinding(THGServerDefault.USERPREFIX + playerName);
	}
	
	private PlayerCompetence getCompetence(){
		if (playerName == null) {
			return null; 			
		}
		
		DataManager dm = AppContext.getDataManager();           
		return (PlayerCompetence) dm.getBinding(THGServerDefault.PLAYER_COMPETENCE + playerName);
	}
	
    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        AppContext.getDataManager().markForUpdate(this);       	
        this.topicId = topicId;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        AppContext.getDataManager().markForUpdate(this);    
        
    	if (topicName == null) {
    		this.topicName = null;
    		return;
    	}
   	
        this.topicName = topicName;
    }
    
    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        AppContext.getDataManager().markForUpdate(this);       	
        this.stationName = stationName;
    }

    public String getTreasureBoxName() {
        return treasureBoxName;
    }

    public void setTreasureBoxName(String treasureBoxName) {
        AppContext.getDataManager().markForUpdate(this);       	
        this.treasureBoxName = treasureBoxName;
    }

    public int getNextReplyIndex() {
        return nextReplyIndex;
    }

    public void setNextReplyIndex(int nextSpeakingIndex) {
        AppContext.getDataManager().markForUpdate(this);       	
        this.nextReplyIndex = nextSpeakingIndex;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String npcName) {
        AppContext.getDataManager().markForUpdate(this);     
        
    	if (npcName == null) {
            this.guideName = null;
            return;
    	}
  	
        this.guideName = npcName;
    }
    
    public void setNextStation() {
        DataManager dm = AppContext.getDataManager();

        String[] nextAvailableStations = null;
        if (stationName.equals(StationType.Orientation.toString())) {
            OrientationStation station = (OrientationStation) dm.getBinding(THGServerDefault.STATION + topicName + ":" + stationName);
            nextAvailableStations = station.getNextAvailableStationNames();
        } else if (stationName.equals(StationType.Evaluation.toString())) {
            EvaluationStation station = (EvaluationStation) dm.getBinding(THGServerDefault.STATION + topicName + ":" + stationName);
        } else if (stationName.equals(StationType.Obstacle.toString())) {
//will be implemented
        } else {
            HelpStation station = (HelpStation) dm.getBinding(THGServerDefault.STATION + topicName + ":" + stationName);
            nextAvailableStations = station.getNextAvailableStationNames();
        }

        if (nextAvailableStations != null) {
            String nextStationName = nextAvailableStations[0];

            if (nextStationName.equals(StationType.Orientation.toString())) {
                OrientationStation station = (OrientationStation) dm.getBinding(THGServerDefault.STATION + topicName + ":" + nextStationName);
            } else if (nextStationName.equals(StationType.Evaluation.toString())) {
                EvaluationStation station = (EvaluationStation) dm.getBinding(THGServerDefault.STATION + topicName + ":" + nextStationName);
                setStationName(nextStationName);
                setTreasureBoxName(station.getTreasureBoxName());
            } else if (nextStationName.equals(StationType.Obstacle.toString())) {
    //will be implemented
            } else {
                HelpStation station = (HelpStation) dm.getBinding(THGServerDefault.STATION + topicName + ":" + nextStationName);
                setStationName(nextStationName);
                setGuideName(station.getGuide());
            }
        }
        setNextReplyIndex(0);
    }

    public void addDialogueHistory(String speaking) {
        AppContext.getDataManager().markForUpdate(this);       	
        dialogueHistory.add(speaking);
    }

    public List<String> getDialogueHistory() {
        return dialogueHistory;
    }

    public void clearDialogueHistory() {
        AppContext.getDataManager().markForUpdate(this);   
        dialogueHistory.clear();
    }
    
    public void addMyDialogue(int npcId, String npcName, Dialogue dialogue) {
        MyDialogue myDialogue = new MyDialogue(npcId, npcName, dialogue);
        DataManager dm = AppContext.getDataManager();
        dm.setBinding(THGServerDefault.MYDIALOGUE + getPlayerListener().getPlayerId() + ":" + npcName, myDialogue);
    }

    public MyDialogue getMyDialogue(String npcName ) {
        try {
            DataManager dm = AppContext.getDataManager();
            MyDialogue myDialogue = (MyDialogue) dm.getBinding(THGServerDefault.MYDIALOGUE + getPlayerListener().getPlayerId() + ":" + npcName);
            return myDialogue;
        } catch (com.sun.sgs.app.NameNotBoundException e) {
            return null;
        }       
    }    

    public void addMyTreasure(MyTreasure myTreasure, int placeId, int treasureId, int topicId ) {
        DataManager dm = AppContext.getDataManager();
        dm.setBinding(THGServerDefault.MYTREASURE + getPlayerListener().getPlayerId() + ":" + placeId + ":" + treasureId + ":" + topicId, myTreasure);
    }

    public MyTreasure getMyTreasure(int placeId, int treasureId, int topicId) {
        try {
            DataManager dm = AppContext.getDataManager();
            MyTreasure myTreasure = (MyTreasure) dm.getBinding(THGServerDefault.MYTREASURE + getPlayerListener().getPlayerId() + ":" + placeId + ":" + treasureId + ":" + topicId );
            return myTreasure;
        } catch (com.sun.sgs.app.NameNotBoundException e) {
            return null;
        }       
    }
            
    public void addCompletedQuest(Quest quest) {        
        DataManager dm = AppContext.getDataManager();
        dm.setBinding(THGServerDefault.COMPLETED_QUEST + getPlayerListener().getPlayerId() + ":" + quest.getId(), quest);
    }
    
    public Quest getCompletedQuest(int questId ) {
        try {
            DataManager dm = AppContext.getDataManager();
            Quest quest = (Quest) dm.getBinding(THGServerDefault.COMPLETED_QUEST + getPlayerListener().getPlayerId() + ":" + questId);
            return quest;
        } catch (com.sun.sgs.app.NameNotBoundException e) {
            return null;
        } 
    }      
    
    public boolean isCompletedQuest(int questId ) {
        Quest quest = getCompletedQuest(questId);
        if (quest != null) {
            return true;
        } else {
            return false;
        }        
    } 

    public void addMyQuest(int questId) {
        MyQuest myQuest = new MyQuest(questId);
        
        DataManager dm = AppContext.getDataManager();
        dm.setBinding(THGServerDefault.MYQUEST + getPlayerListener().getPlayerId() + ":" + questId, myQuest);
    }

    public MyQuest getMyQuest(int questId) {
        try {
            DataManager dm = AppContext.getDataManager();
            MyQuest quest = (MyQuest) dm.getBinding(THGServerDefault.MYQUEST + getPlayerListener().getPlayerId() + ":" + questId);
            return quest;
        } catch (com.sun.sgs.app.NameNotBoundException e) {
            return null;
        } 
    }    
    
    public boolean isMyQuest(int questId) {
        MyQuest myQuest = getMyQuest(questId);
        if (myQuest != null) {
            return true;
        } else {
            return false;
        }        
    } 
    
    public void removeMyQuest(int questId) {
        DataManager dm = AppContext.getDataManager();
        dm.removeBinding(THGServerDefault.MYQUEST + getPlayerListener().getPlayerId() + ":" + questId);
    }    
   
    @SuppressWarnings("unchecked")
	public Quest findNotGivenQuest(int inquirerId) {
        DataManager dm = AppContext.getDataManager();
        ManagedReferenceList<Quest> ql= (ManagedReferenceList<Quest>) dm.getBinding(THGServerDefault.QUEST_LIST + inquirerId);   
        for (int i=0; i < ql.size(); ++i) {
            Quest quest = ql.get(i);                        
            if (!isCompletedQuest(quest.getId()) && !isMyQuest(quest.getId())) {
                return quest;
            }         
        }        
        return null;                
    }
            
    public void cutDialogue(){
        if (contactNPCRef != null) {
            Speaker npc = (Speaker) contactNPCRef.getForUpdate();
            npc.addRemoveDialogue(getPlayerListener().getPlayerId());
        }
        removeContactNPC();
    } 

    public void removeContactNPC() {    
        AppContext.getDataManager().markForUpdate(this);   
        contactNPCRef = null;
    }
    
    public boolean isContactNPC(int npcId){
        Npc npc = getContactNPC();
        if (npc == null) return false;
        else return npc.getId() == npcId;
    }
    
    public void setContactNPC(Npc npc) {
        DataManager dm = AppContext.getDataManager();
        dm.markForUpdate(this);   
        contactNPCRef = dm.createReference(npc);
    }

    public Npc getContactNPC() {
        if (contactNPCRef == null) return null;
        else return (Npc) contactNPCRef.get();
    }
    
    public void addKnownNPC(Npc npc) {
        MyKnownNPC myKnownNPC = new MyKnownNPC(npc);
        
        DataManager dm = AppContext.getDataManager();
        dm.setBinding(THGServerDefault.KNOWNNPC + getPlayerListener().getPlayerId() + ":" + npc.getId(), myKnownNPC);
    }
              
    public MyKnownNPC getKnownNPC(int npcId) {     
        try {
            DataManager dm = AppContext.getDataManager();
            MyKnownNPC npc = (MyKnownNPC) dm.getBinding(THGServerDefault.KNOWNNPC + getPlayerListener().getPlayerId() + ":" + npcId);
            return npc;
        } catch (com.sun.sgs.app.NameNotBoundException e) {
            return null;
        }           
    }
    
    public void removeKnownNPC(int npcId) {     
        DataManager dm = AppContext.getDataManager();
        dm.removeBinding(THGServerDefault.KNOWNNPC + getPlayerListener().getPlayerId() + ":" + npcId);
    }    
    
    public boolean isKnownNPC(int npcId){
        MyKnownNPC npc = getKnownNPC(npcId);
        if (npc == null) return false;
        else return true;
    }
                               
    
    public void takeQuiz(MyTreasure myTreasure) {
        Treasure treasure = myTreasure.getTreasure();       

        int questID = treasure.getQuestID();

        MyQuest quest = getMyQuest(questID);
        quest.setState(MyQuest.QuestState.Question);
        
        getPlayerListener().sendMove(Commands.giveQuestionCommand(getPlayerListener().getPlayerId(), treasure.getId(), questID));
    } 
    
    protected void openTreasure(int placeId, int treasureId, int topicId) {
        MyTreasure myTreasure = getMyTreasure(placeId, treasureId, topicId);
        if (myTreasure == null) {
            myTreasure = new MyTreasure(placeId, treasureId);
            addMyTreasure(myTreasure, placeId, treasureId, topicId);
        }
                
        if (!myTreasure.isTaken()) {
            Treasure treasure = myTreasure.getTreasure();
            String question = treasure.getQuestion();

logger.info("openTreasure => " + question);

            if (question != null) {
                if (treasure.getQuestID() == topicId && getMyQuest(topicId).isAccepted()) {
                    myTreasure.setOpened(true);
                    takeQuiz(myTreasure);
                }
            } else {
                myTreasure.setOpened(true);
                Reward reward = treasure.getReward();

                if (reward != null) {

                   getPlayerListener().sendMove(Commands.paymentCommand(getPlayerListener().getPlayerId(), treasureId, treasure.getName(),
                                        reward.getHp(),
                                        reward.getMp(),
                                        reward.getExp(),
                                        reward.getMoney(),
                                        reward.getArmor(),
                                        reward.getTool(),
                                        reward.getWeapon()
                                    ));
                    getCompetence().addCompetence(reward);
                } 
                myTreasure.setTaken(true);
            }
        }
    }    
    
    

    public void giveBenefit(int treasureId, Treasure treasure) {
        Reward reward = treasure.getReward();

        if (reward != null) {
            getPlayerListener().sendMove(Commands.paymentCommand(getPlayerListener().getPlayerId(), treasureId, treasure.getName(),
                                    reward.getHp(),
                                    reward.getMp(),
                                    reward.getExp(),
                                    reward.getMoney(),
                                    reward.getArmor(),
                                    reward.getTool(),
                                    reward.getWeapon()
                            ));       
            getCompetence().addCompetence(reward);
        }
        completeQuest(treasure.getQuestID());
    }

    public void completeQuest(int questId) {
        MyQuest quest = getMyQuest(questId);
        quest.setState(MyQuest.QuestState.Complete);

        // initialize pi for the quest
        setTopicId(0);
        setTopicName(null);
        setGuideName(null);
        setNextReplyIndex(0);

        sendComplete(questId);
    }

    private void sendComplete(int questID){
        getPlayerListener().sendMove(Commands.changeQuestStatusCommand(getPlayerListener().getPlayerId(), questID,
                MyQuest.getState(MyQuest.QuestState.Complete)));
    }

    private void sendFail(int questID){
        getPlayerListener().sendMove(Commands.changeQuestStatusCommand(getPlayerListener().getPlayerId(), questID,
                MyQuest.getState(MyQuest.QuestState.Fail)));
    }

    private void sendMark(int questID,  int mark){
        getPlayerListener().sendMove(Commands.giveMarkCommand(getPlayerListener().getPlayerId(), questID, mark));
    }

    public void givePenalty(int treasureId, Treasure treasure) {
        Penalty penalty = treasure.getPenalty();

        if (penalty != null) {

/*
 * Apply given penalty
 */

        }
        failQuest(treasure.getQuestID());
    }

    public void failQuest(int questId) {
        MyQuest quest = getMyQuest(questId);
        quest.setState(MyQuest.QuestState.Fail);

        // initialize pi for the quest
        setTopicId(0);
        setTopicName(null);
        setGuideName(null);
        setNextReplyIndex(0);

        sendFail(questId);
    }

    public void evaluateQuest(int placeId, int treasureId, int topicId, String answer) {

        MyTreasure myTreasure = getMyTreasure(treasureId, placeId, topicId);
        
        if (myTreasure == null ) return;

        Treasure treasure = myTreasure.getTreasure();

        int mark = treasure.checkAnswer(answer);

        sendMark(treasure.getQuestID(), mark);

        if (mark > 50) {
            giveBenefit(treasureId, treasure);
            myTreasure.setTaken(true);
        } else {
            givePenalty(treasureId, treasure);
        }
    }
}
