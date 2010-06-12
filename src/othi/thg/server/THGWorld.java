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

package othi.thg.server;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import othi.thg.server.agents.npcs.NPCTask;
import othi.thg.server.agents.npcs.Npc;
import othi.thg.server.agents.player.PlayerListener;
import othi.thg.server.definitions.DefinitionsDefault;
import othi.thg.server.definitions.FoodDefinition;
import othi.thg.server.definitions.LevelDefinition;
import othi.thg.server.definitions.MonsterDefinition;
import othi.thg.server.definitions.PlaceDefinition;
import othi.thg.server.definitions.StationDefinition;
import othi.thg.server.entities.foods.Food;
import othi.thg.server.entities.foods.FoodTask;
import othi.thg.server.obstacles.Monster;
import othi.thg.server.obstacles.MonsterTask;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.AppListener;
import com.sun.sgs.app.Channel;
import com.sun.sgs.app.ChannelManager;
import com.sun.sgs.app.ClientSession;
import com.sun.sgs.app.ClientSessionListener;
import com.sun.sgs.app.Delivery;
import com.sun.sgs.app.ManagedReference;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.PeriodicTaskHandle;
import com.sun.sgs.app.TaskManager;

/**
 * game world
 * @author Dong Won Kim
 */
public class THGWorld implements AppListener, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger logger = Logger.getLogger(THGWorld.class.getName());

	/** The initial place of the player. */
	private static final String ENTRY_PLACE = "math_5_1_townhall";
	private static final float ENTRY_X = 16;
	private static final float ENTRY_Y = 32;

	/*	
	private static final String ENTRY_PLACE = "math_5_1_townhall_storage";
	private static final float ENTRY_X = 7;
	private static final float ENTRY_Y = 7;
	 */
	/** The name of the first channel: {@value #CHANNEL_1_NAME} */
	public static final String CHANNEL_1_NAME = "Foo";
	/** The name of the second channel: {@value #CHANNEL_2_NAME} */
	public static final String CHANNEL_2_NAME = "Bar";

	/**
	 * The first {@link Channel}.
	 * (The second channel is looked up by name only.)
	 */
	private ManagedReference<Channel> channel1 = null;     

	/** The THG object lists. */
	private ManagedReference<ManagedReferenceList<GameBoard>> boardList = null;
	private ManagedReference<ManagedReferenceList<Npc>> npcList = null;
	private ManagedReference<ManagedReferenceList<Monster>> monsterList = null;
	private ManagedReference<ManagedReferenceList<Food>> foodList = null;	

	/** The delay before the first run of the task. */
	public static final int DELAY_MS = 5000; //0

	/** The time to wait before repeating the task. */
	public static final int GAMEBOARD_PERIOD_MS = 500; //100
	public static final int NPC_PERIOD_MS = 500; //100
	public static final int MONSTER_PERIOD_MS = 500; //100
	public static final int FOOD_PERIOD_MS = 500; //100	

	public void initialize(Properties props) {	

		String appRoot = props.getProperty("com.sun.sgs.app.root");

		DefinitionsDefault.setDefinitionsDir(appRoot + "/definitions/");

		THGServerDefault.setMapsDir(appRoot + "/maps/"); 

		/** initialize the channels. */		
		initializeChannels();

		/** initialize the object lists. */
		initializeTHGObjectLists();	

		/** load game objects */
		try {

			LevelDefinition.loadConfiguredLevel();

			MonsterDefinition.loadConfiguredMonsters();  		/** initialize the THG object lists. */

			FoodDefinition.loadConfiguredFoods();

			StationDefinition.loadConfiguredStations();

			PlaceDefinition.loadConfiguredPlaces();

		} catch (IOException e) {
			logger.log(Level.SEVERE, null, e);
		}                  

		/** create tasks and make connections between objects and their tasks. */
		initializeTasks();  

		/** print properties and their values */
		logProperties(props);
	}

	private void initializeChannels(){
		ChannelManager channelManager = AppContext.getChannelManager();            
		// Create and keep a reference to the first channel.
		Channel c1 = channelManager.createChannel(CHANNEL_1_NAME, null, Delivery.RELIABLE);  

		channel1 = AppContext.getDataManager().createReference(c1);

		// We don't keep the second channel object around, to demonstrate
		// looking it up by name when needed.
		channelManager.createChannel(CHANNEL_2_NAME, null, Delivery.RELIABLE);		
	}

	/** initialize the object lists. */
	private void initializeTHGObjectLists() {
		DataManager dm = AppContext.getDataManager();            
		ManagedReferenceList<GameBoard> bl = new ManagedReferenceList<GameBoard>();
		ManagedReferenceList<Npc> nl = new ManagedReferenceList<Npc>();
		ManagedReferenceList<Monster> ml = new ManagedReferenceList<Monster>();
		ManagedReferenceList<Food> fl = new ManagedReferenceList<Food>();		

		dm.setBinding(THGServerDefault.BOARD_LIST, bl);
		dm.setBinding(THGServerDefault.NPC_LIST, nl);
		dm.setBinding(THGServerDefault.MONSTER_LIST, ml);
		dm.setBinding(THGServerDefault.FOOD_LIST, fl);		

		boardList = dm.createReference(bl);
		npcList = dm.createReference(nl);
		monsterList = dm.createReference(ml);
		foodList = dm.createReference(fl);		
	}

	/** create tasks, and links between objects and their tasks. */
	private void initializeTasks() {	
		ManagedReferenceList<GameBoard> bl = (ManagedReferenceList<GameBoard>) boardList.get();             
		for (int i=0; i < bl.size(); ++i) {
			GameBoard gameBoard = bl.get(i);			
			GameBoardTask gameBoardTask = new GameBoardTask();
			gameBoard.setGameBoardTask(gameBoardTask);
			gameBoardTask.setManagedTHGObj(gameBoard);
			TaskManager taskManager = AppContext.getTaskManager();
			PeriodicTaskHandle taskHandle = taskManager.schedulePeriodicTask(gameBoardTask, DELAY_MS, GAMEBOARD_PERIOD_MS);	
			gameBoardTask.setTaskHandle(taskHandle);
		}

		ManagedReferenceList<Npc> nl = (ManagedReferenceList<Npc>) npcList.get();
		for (int i=0; i < nl.size(); ++i) {                      
			Npc npc = nl.get(i);
			NPCTask npcTask = new NPCTask();
			npc.setManagedTHGTask(npcTask);
			npcTask.setManagedTHGObj(npc);
			TaskManager taskManager = AppContext.getTaskManager();
			PeriodicTaskHandle taskHandle = taskManager.schedulePeriodicTask(npcTask, DELAY_MS, NPC_PERIOD_MS);
			npcTask.setTaskHandle(taskHandle);
		}    

		ManagedReferenceList<Monster> ml = (ManagedReferenceList<Monster>) monsterList.get();            
		for (int i=0; i < ml.size(); ++i) {
			Monster monster = ml.get(i);
			MonsterTask monsterTask = new MonsterTask();			
			monster.setManagedTHGTask(monsterTask);
			monsterTask.setManagedTHGObj(monster);
			TaskManager taskManager = AppContext.getTaskManager();
			PeriodicTaskHandle taskHandle = taskManager.schedulePeriodicTask(monsterTask, DELAY_MS, MONSTER_PERIOD_MS);
			monsterTask.setTaskHandle(taskHandle);
		}

		ManagedReferenceList<Food> fl = (ManagedReferenceList<Food>) foodList.get();            
		for (int i=0; i < fl.size(); ++i) {
			Food food = fl.get(i);
			FoodTask foodTask = new FoodTask();			
			food.setManagedTHGTask(foodTask);
			foodTask.setManagedTHGObj(food);
			TaskManager taskManager = AppContext.getTaskManager();
			PeriodicTaskHandle taskHandle = taskManager.schedulePeriodicTask(foodTask, DELAY_MS, FOOD_PERIOD_MS);
			foodTask.setTaskHandle(taskHandle);
		}		
	}      

	/** print properties and their values */	
	private void logProperties(Properties arg0) {
		String app_root = arg0.getProperty("com.sun.sgs.app.root");
		logger.info("[com.sun.sgs.app.root : " + app_root + "]");   

		Enumeration<?> e = arg0.propertyNames();
		while (e.hasMoreElements()) {
			Object o = e.nextElement();
			logger.info("[Property : " + o.toString() + "]");
		}                
	}

	@Override
	public ClientSessionListener loggedIn(ClientSession session) {
		logger.log(Level.INFO, "OTHI Client login: {0}", session.getName());		

		PlayerListener playerListener = PlayerListener.loggedIn(session);

		//playerListener.setChannel(channel1, loginCount);		
		if (playerListener.isNewPlayer()) {
			playerListener.setupPlayer(ENTRY_PLACE, ENTRY_X, ENTRY_Y);
		}  else {
			playerListener.setupPlayer();
		}

		return playerListener;
	}	            
}
