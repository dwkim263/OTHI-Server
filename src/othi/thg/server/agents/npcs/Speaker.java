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
 * NonPlayableHelper.java
 * 
 * Created on 2007. 8. 9, 9:37:42
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server.agents.npcs;

/**
 *
 * @author Dong Won Kim
 */
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import othi.thg.common.Commands;
import othi.thg.server.THGServerDefault;
import othi.thg.server.agents.Dialogue;
import othi.thg.server.agents.player.MyQuest;
import othi.thg.server.agents.player.Player;
import othi.thg.server.agents.player.PlayerInventory;
import othi.thg.server.agents.player.PlayerLogbook.CategoryMetaDataKey;
import othi.thg.server.agents.player.PlayerLogbook.RelationToQuestion;
import othi.thg.server.events.orientation.Quest;
import othi.thg.server.stations.EvaluationStation;
import othi.thg.server.stations.HelpStation;
import othi.thg.server.stations.OrientationStation;
import othi.thg.server.stations.Station.StationType;

import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.AppContext;


public class Speaker extends Npc {

	private static final long serialVersionUID = 3294476171737158606L;

	private static final Logger logger = Logger.getLogger(Speaker.class.getName());

	public static final int LEVEL_1 = 1;    
	public static final int REPLY_ON = 1;    
	public static final int REPLY_OFF = 0;

	protected boolean inConversation = false;    

	List<Integer> removeDialogues;

	Map<Integer, String> orientationTopics = new HashMap<Integer, String>();
	Map<String, Object> information = new HashMap<String, Object>();

	public Speaker(int id, String name, String placeName) {
		super(id, name, placeName);

		removeDialogues = new ArrayList<Integer>();
	}

	public void tickTalk() {

		//System.out.println(getName() + " Human Action => tickTalk ");

		DataManager dm = AppContext.getDataManager();
		String binding = THGServerDefault.NPCDIALOGUE + getId();
		String bindingName = dm.nextBoundName(binding);
		while (bindingName != null && bindingName.startsWith(binding)) {

			logger.info(getName() +  " Binding Name => tickTalk " + bindingName);
			NPCDialogue npcDialogue = (NPCDialogue) dm.getBinding(bindingName);
			Player player = npcDialogue.getPlayer();

			if (npcDialogue.isQueryReceived()) {
				if (player != null) {
					String playerName = player.getName();
					if ( npcDialogue.getDialogueType() == Dialogue.DialogueType.HELLO ) {
						giveHello(npcDialogue);
					} else if ( npcDialogue.getDialogueType() == Dialogue.DialogueType.BYE ) {
						if (npcDialogue.getQuery().contains("No")) {
							rejectQuest(playerName);
						}
						giveBye(npcDialogue, playerName);
					} else if ( npcDialogue.getDialogueType() == Dialogue.DialogueType.HELP ){
						giveHelp(npcDialogue, playerName);
					} else if ( isStopTalking(getInventory(player.getName()))) {
						giveBye(npcDialogue, playerName);
					}

					sendReply(npcDialogue, playerName);
					npcDialogue.setQueryReceived(false);

				}  else if (player == null) {
					npcDialogue.setEndConversation(true);
					addRemoveDialogue(npcDialogue.getId());
				}
			} else if ((System.currentTimeMillis() - npcDialogue.getRepliedTime() ) > Dialogue.MAXIMUM_WAITING_TIME_FOR_REPLY ) {
				if (player != null) {
					String playerName = player.getName();					
					giveBye(npcDialogue, playerName);
					sendReply(npcDialogue, playerName);
				}
				addRemoveDialogue(npcDialogue.getId());
			}

			bindingName = dm.nextBoundName(bindingName);			
		}

		if (!removeDialogues.isEmpty()) {
			for(Integer rid : removeDialogues) {
				removeDialogue(rid);
			}
			clearRemoveDialogues();

			if (hasNoActiveDialogue()) setInConversation(false);
		}
	}

	private void clearRemoveDialogues() {
		AppContext.getDataManager().markForUpdate(this);
		removeDialogues.clear();
	}

	public void loadingClue(PlayerInventory pi) {
		String topic = pi.getTopicName();
		String stationName = pi.getStationName();
		DataManager dm = AppContext.getDataManager();

		String[] nextAvailableStations = null;

		if (stationName.equals("Orientation")) {
			OrientationStation station = (OrientationStation) dm.getBinding(THGServerDefault.STATION + topic + ":" + stationName);
			nextAvailableStations = station.getNextAvailableStationNames();
		} else if (stationName.equals("Evaluation")) {
			EvaluationStation station = (EvaluationStation) dm.getBinding(THGServerDefault.STATION + topic + ":" + stationName);
			//
			// Something will be implemented here in order to move to the next topic
			//
		} else if (stationName.equals("Obstacle")) {
			//will be implemented
		} else {
			HelpStation station = (HelpStation) dm.getBinding(THGServerDefault.STATION + topic + ":" + stationName);
			nextAvailableStations = station.getNextAvailableStationNames();
		}

		if (nextAvailableStations != null) {
			String nextStationName = nextAvailableStations[0];

			if (nextStationName.equals(StationType.Orientation.toString())) {
				OrientationStation station = (OrientationStation) dm.getBinding(THGServerDefault.STATION + topic + ":" + nextStationName);
				//
				// Something will be implemented here in order to move to the next topic
				//
			} else if (nextStationName.equals(StationType.Evaluation.toString())) {
				EvaluationStation station = (EvaluationStation) dm.getBinding(THGServerDefault.STATION + topic + ":" + nextStationName);
				addInformation(topic, station.getClue());
			} else if (nextStationName.equals(StationType.Obstacle.toString())) {
				//will be implemented
			} else {
				HelpStation station = (HelpStation) dm.getBinding(THGServerDefault.STATION + topic + ":" + nextStationName);
				addInformation(topic, station.getClue());
			}            
		}
	}   

	private void rejectQuest(String name) {
		PlayerInventory pi = getInventory(name);
		int questID = pi.getTopicId();

		// initialize pi for the myQuest
		pi.setTopicId(0);
		pi.setTopicName(null);
		pi.setGuideName(null);
		pi.setNextReplyIndex(0);
		pi.removeMyQuest(questID);

		// change the myQuest state.
		sendReject(name, questID);
	}

	private void sendReject(String name, int questID){
		getPlayerListener(name).sendMove(Commands.changeQuestStatusCommand(getPlayerListener(name).getPlayerId(), questID, 
				MyQuest.getState(MyQuest.QuestState.Reject)));
	}

	private void sendReply(NPCDialogue dialogue, String name) {
		String reply = dialogue.getReply();

		logger.log(Level.INFO, getName() +  " myReply => tickTalk " + reply);

		int replyFlag = dialogue.isReplyRequested() ? REPLY_ON : REPLY_OFF;
		reply = "[" + getNickName() + "]" + NPCDialogue.NEWLINE + reply + NPCDialogue.NEWLINE;

		getInventory(name).addDialogueHistory(reply);
		getPlayerListener(name).sendMove(Commands.talkToNPCCommand(id, replyFlag, reply));
	}

	@SuppressWarnings("unchecked")
	public void addInformation(String topic, String info){
		AppContext.getDataManager().markForUpdate(this);
		if (info.trim().length() > 0) {
			if (this.information.isEmpty()) {
				List<String> guideReplies = new LinkedList<String>();
				guideReplies.add(info);
				this.information.put(topic, guideReplies);
			} else {
				List<String> guideReplies  = (List<String>) this.information.get(topic);
				if (!guideReplies.contains(info)) guideReplies.add(info);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected List<String> getInformation(String topic) {
		if (information.isEmpty()) {
			return null;
		} else {
			return (List<String>) information.get(topic);
		}
	}

	public void addOrientationTopic(int questID, String topicName){
		AppContext.getDataManager().markForUpdate(this);
		this.orientationTopics.put(Integer.valueOf(questID), topicName);
	}

	protected String getOrientationTopic(int topicId) {
		if (orientationTopics.isEmpty()) {
			return null;
		} else {
			return orientationTopics.get(Integer.valueOf(topicId));
		}
	}

	public void giveHello(NPCDialogue dialogue) {
		dialogue.setReply(dialogue.getHelloQuery().toString() + " " + dialogue.getQuestioner() + "!");
		dialogue.setReplyRequested(true);
		dialogue.setEndConversation(false);
	}

	protected void giveBye(NPCDialogue dialogue, String name) {
		dialogue.setReply(dialogue.getByeQuery().toString() + "!");
		dialogue.setReplyRequested(false);
		getInventory(name).removeContactNPC();
		dialogue.setEndConversation(true);
		if (hasNoActiveDialogue()) setInConversation(false);
	}

	// If the size of helps is 2,  it includes a help (index 0) and a clue (index 2).
	// If the size of helps is 3, it includes an introduction (index 0), an question (index 1) and a clue (index 2).
	// The estimation process is invoked by opening the treasure, not here.
	protected void giveHelp(NPCDialogue dialogue, String name ) {
		String reply = null;
		boolean replyRequested = true;

		PlayerInventory pi = getInventory(name);
		if (pi.getTopicName() == null) provideInitialTopic(pi);

		if (pi.getGuideName() != null && pi.getGuideName().equalsIgnoreCase(getName()) ) {

			List<String> helps = getInformation(pi.getTopicName());
			int replyIndex = pi.getNextReplyIndex();

			if (replyIndex == 0) loadingClue(pi);

			if (replyIndex < helps.size() ) {
				reply  = helps.get(replyIndex);

				if (pi.getStationName().equals(StationType.Orientation.toString())) {
					//The npc gives an orientation to this player.
					switch (replyIndex) {
					case 0:
						suggestQuest(reply, name);
						break;
					case 1:
						acceptQuest(reply, name);
						break;
					case 2:
						sendQuestInformation(reply, name, MyQuest.InformationType.Clue);
						break;
					default:
						logger.log(Level.SEVERE,"The size of infomation for the orientation station is larger than we expected : " + replyIndex);
					}
				} else if (pi.getStationName().equals(StationType.Obstacle.toString())) {
					// It will be used for Obstacle station. Don't delete the above line!
				} else {
					//The npc gives a help to this player.
					switch (replyIndex) {
					case 0:
						sendQuestInformation(reply, name, MyQuest.InformationType.Help);
						break;
					case 1:
						//For Help and Clue
						sendQuestInformation(reply, name, MyQuest.InformationType.Clue);
						break;
					default:
						logger.log(Level.SEVERE, "The size of infomation for the help station is larger than we expected : " + replyIndex);
					}
				}

				replyIndex++;
				pi.setNextReplyIndex(replyIndex);
			} else {
				replyRequested   = false;
				if (dialogue.getQuery().contains("Ok")) {
					//Bye Bye
					reply = dialogue.getByeQuery().toString() + "!";
					dialogue.setEndConversation(true);
					getInventory(name).removeContactNPC();
					pi.setNextStation();
					if (hasNoActiveDialogue()) setInConversation(false);
				} else {
					reply =  "I have no more help I can give you.";
					logger.log(Level.SEVERE,"We have different process we don't know : " + replyIndex);
				}
			}
		} else {        
			//No help is available.
			replyRequested   = false;
			reply = "I'm sorry. I don't have any helps I can give you.";
		}

		dialogue.setReplyRequested(replyRequested);
		dialogue.setReply(reply);
	}

	//A topic is a myQuest.
	private void provideInitialTopic(PlayerInventory pi){
		int topicId = selectSuitableTopic(pi);
		if (topicId != 0) {
			String topic = this.getOrientationTopic(topicId);
			pi.setTopicId(topicId);
			pi.setTopicName(topic);
			pi.setGuideName(getName());
		}
	}

	private int selectSuitableTopic(PlayerInventory pi){

		int questID = 0;

		Set<Integer> set = orientationTopics.keySet();
		Iterator<Integer> iter = set.iterator();

		boolean found = false;

		while (iter.hasNext() && !found) {
			questID = iter.next().intValue();
			found = !pi.isMyQuest(questID) ? true : false;
		}

		if (!found) {
			return 0;
		} else {
			return questID;
		}
	}

	private void suggestQuest(String introduction, String name){
		PlayerInventory pi = getInventory(name);
		pi.addMyQuest(pi.getTopicId());
		MyQuest myQuest = pi.getMyQuest(pi.getTopicId());
		myQuest.setState(MyQuest.QuestState.Suggest);
		sendQuestIntroduction (introduction, name);
	}

	protected void sendQuestIntroduction(String introduction, String name) {
		PlayerInventory pi = getInventory(name);
		getPlayerListener(name).sendMove(Commands.questIntroductionCommand(
				getPlayerListener(name).getPlayerId(), getId(), getNickName(), pi.getTopicId(), pi.getTopicName(), introduction));
	}

	private void acceptQuest(String question, String name) {
		PlayerInventory pi = getInventory(name);

		MyQuest myQuest = pi.getMyQuest(pi.getTopicId());
		Quest quest = myQuest.getQuest();        

		myQuest.setState(MyQuest.QuestState.Accept);
		sendQuestQuestion(question, name);

		//Processing blog
		getLogbook(name).setMetaData(CategoryMetaDataKey.topic, pi.getTopicName());
		getLogbook(name).setMetaData(CategoryMetaDataKey.question, question);
		getLogbook(name).setNewBlogPost(pi.getTopicName(), quest.getIntroduction(), RelationToQuestion.Introduction);
	}

	protected void sendQuestQuestion(String question, String name) {
		PlayerInventory pi = getInventory(name);
		getPlayerListener(name).sendMove(Commands.questQuestionCommand(
				getPlayerListener(name).getPlayerId(), getId(), getNickName(), pi.getTopicId(), pi.getTopicName(), question));
	}

	protected void sendQuestInformation(String infor, String name, MyQuest.InformationType iType){
		PlayerInventory pi = getInventory(name);

		getPlayerListener(name).sendMove(Commands.questInformationCommand(
				getPlayerListener(name).getPlayerId(), getId(), getNickName(), pi.getTopicId(), pi.getTopicName(), infor, MyQuest.getInfomationType(iType)));

		if (iType.equals(MyQuest.InformationType.Help)) {
			getLogbook(name).setNewBlogPost(getNickName(), infor, RelationToQuestion.Sub_topic);
		}
	}

	public NPCDialogue getDialogue(int dialogueId) {
		try {
			DataManager dm = AppContext.getDataManager();
			NPCDialogue dialogue = (NPCDialogue) dm.getBinding(THGServerDefault.NPCDIALOGUE + getId() + ":" + dialogueId);
			return dialogue;
		} catch (com.sun.sgs.app.NameNotBoundException e) {
			return null;
		} 
	}       

	public void addDialogue(NPCDialogue dialogue) {
		DataManager dm = AppContext.getDataManager();
		dm.setBinding(THGServerDefault.NPCDIALOGUE + getId() + ":" + dialogue.getId(), dialogue);
	}    

	public void removeDialogue(int dialogueId) {
		DataManager dm = AppContext.getDataManager();
		dm.removeBinding(THGServerDefault.NPCDIALOGUE + getId() + ":" + dialogueId);
	}    

	public boolean isNotInDialogues(int playerId) {
		return getDialogue(playerId) != null;
	}

	public boolean isStopTalking(PlayerInventory pi) {             
		return !pi.isContactNPC(id);
	}

	public void addRemoveDialogue(int id) {
		AppContext.getDataManager().markForUpdate(this);		
		removeDialogues.add(id);
	}

	protected boolean hasNoActiveDialogue(){        
		DataManager dm = AppContext.getDataManager();
		String binding = THGServerDefault.NPCDIALOGUE + getId();
		String bindingName = dm.nextBoundName(binding);

		while (bindingName != null && bindingName.startsWith(binding)) {
			NPCDialogue dialogue = (NPCDialogue) dm.getBinding(bindingName);
			if (!dialogue.isEndConversation()){
				return false;
			}
			bindingName = dm.nextBoundName(bindingName);
		} 
		return true;
	}

	protected boolean isHelpRequest(String mySpeaking) {
		Pattern helpPattern = Pattern.compile("(help|instruction|information)(.*)");
		Matcher helpMatcher = helpPattern.matcher(mySpeaking);
		return helpMatcher.matches();
	}

	protected boolean isRejected(String reply) {
		Pattern replyPattern = Pattern.compile("(no)(.*)");
		Matcher replyMatcher = replyPattern.matcher(reply);
		return replyMatcher.matches();        
	}    

	public void setInConversation(boolean inConversation) {
		AppContext.getDataManager().markForUpdate(this);
		this.inConversation = inConversation;
	}

	public boolean isInConversation() {
		return inConversation;
	}
}
