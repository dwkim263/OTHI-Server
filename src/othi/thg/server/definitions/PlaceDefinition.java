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
 * ReadPlaceDefinition.java
 * 
 * Created on Aug 1, 2007, 11:49:13 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othi.thg.server.definitions;

/**
 *
 * @author DongWon Kim
 */
import java.io.IOException;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import othi.thg.common.Commands.Direction;
import othi.thg.server.GameBoard;
import othi.thg.server.THGServerDefault;
import othi.thg.server.ManagedReferenceList;
import othi.thg.server.TerrainMap;
import othi.thg.server.THGServerDefault.Terrain;
import othi.thg.server.agents.npcs.Npc;
import othi.thg.server.definitions.xml.GuideXML;
import othi.thg.server.definitions.xml.TreasureXML;
import othi.thg.server.entities.Portal;
import othi.thg.server.entities.foods.Food;
import othi.thg.server.events.evaluation.Reward;
import othi.thg.server.events.evaluation.Treasure;
import othi.thg.server.events.orientation.Quest;
import othi.thg.server.information.Topic;
import othi.thg.server.obstacles.Monster;
import othi.thg.server.stations.OrientationStation;
import othi.thg.server.stations.Place;
import othi.thg.server.stations.Station;
import othi.thg.tools.tiled.*;
import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;

public class PlaceDefinition {

	private static final Logger logger = Logger.getLogger(PlaceDefinition.class.getName());   

	private static final String PROTECTION_LAYER = "protection";    
	private static final String COLLISION_LAYER = "collision";
	private static final String OBJECTS_LAYER = "objects";

	public static void loadConfiguredPlaces() throws IOException {

		try {
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			org.w3c.dom.Document document = builder.parse(DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.PLACE_XML);

			Element element = document.getDocumentElement();
			if (!element.getNodeName().equals("places")) {
				throw new IOException("Not a place file");
			}

			NodeList placelist = element.getElementsByTagName("place");
			for (int i = 0; i < placelist.getLength(); i++) {
				Node placeNode = placelist.item(i);
				if (placeNode.getNodeType() == Node.ELEMENT_NODE){                                    
					Element placeElement = (Element)placeNode;
					String placeName = placeElement.getAttribute("name");
					
					Place place = new Place(i, placeName);
					
					int x=0;
					int y=0;
					String xValue = placeElement.getAttribute("x");
					if (!xValue.equals("") ) {
						x = Integer.parseInt(xValue); 
					}                     
					String yValue = placeElement.getAttribute("y");
					if (!yValue.equals("") ) {
						y = Integer.parseInt(yValue);               
					}        
					place.setXY(x, y);
					int level = Integer.MIN_VALUE;
					String levelValue = placeElement.getAttribute("level");
					if (!levelValue.equals("") ) {
						y = Integer.parseInt(levelValue);               
					}     
					place.setLevel(level);

					String mapFileRef = placeElement.getAttribute("file");

					place.setMapFileRef(mapFileRef);

					DataManager dm = AppContext.getDataManager();
					dm.setBinding(THGServerDefault.PLACE + placeName, place);     

					loadGameBoards(i, placeName);

					loadPortals(placeName, i, placeElement);

					loadFoods(placeName, i, placeElement);

					loadTreasures(placeName, i, placeElement);

					loadTerrainMap(placeName, i, mapFileRef);

					loadMonsters(placeName, i, placeElement);

					loadNPCs(placeName, i, placeElement);

					logger.info("Complete loading Place information of " + placeName + "!");
				}
			}
		} catch (ParserConfigurationException e) {
			logger.log(Level.SEVERE, "Parsing error", e);    
		} catch ( SAXException e) {
			logger.log(Level.SEVERE, null, e);         
		}

	}

	@SuppressWarnings("unchecked")
	private static void addGameBoard(GameBoard gameBoard) {
		DataManager dm = AppContext.getDataManager();		
		ManagedReferenceList<GameBoard> bl = (ManagedReferenceList<GameBoard>) dm.getBindingForUpdate(THGServerDefault.BOARD_LIST);		
		bl.add(gameBoard);
	}

	@SuppressWarnings("unchecked")
	private static void addNPC(Npc npc) {
		DataManager dm = AppContext.getDataManager();		
		ManagedReferenceList<Npc> nl = (ManagedReferenceList<Npc>) dm.getBindingForUpdate(THGServerDefault.NPC_LIST);     
		nl.add(npc);
	}

	@SuppressWarnings("unchecked")
	private static void addMonster(Monster monster) {
		DataManager dm = AppContext.getDataManager();			
		ManagedReferenceList<Monster> ml = (ManagedReferenceList<Monster>) dm.getBindingForUpdate(THGServerDefault.MONSTER_LIST);		
		ml.add(monster);
	}

	@SuppressWarnings("unchecked")
	private static void addFood(Food food) {
		DataManager dm = AppContext.getDataManager();			
		ManagedReferenceList<Food> fl = (ManagedReferenceList<Food>) dm.getBindingForUpdate(THGServerDefault.FOOD_LIST);		
		fl.add(food);
	}	

	private static void loadGameBoards(int placeId, String placeName) {
		GameBoard gameBoard = new GameBoard(placeId, placeName);

		DataManager dm = AppContext.getDataManager();
		dm.setBinding(THGServerDefault.GAMEBOARD + placeName, gameBoard);

		addGameBoard(gameBoard);
	}

	@SuppressWarnings("unchecked")
	private static void loadTreasures(String placeName, int placeId, Element placeElement ) throws IOException
	{

		NodeList nodes = getNamedNode(placeElement, "treasure");

		if (nodes == null) {
			logger.info("No Treasures at " + placeName + "!");
			return;
		}

		TreasureXML treasureXML = new TreasureXML(DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.DEFAULT_COURSE_ID
				+ File.separator + DefinitionsDefault.TREASURE_XML);
		Map<String, Object> treasureProperties = treasureXML.getProperties();

		DataManager dm = AppContext.getDataManager();
		ManagedReferenceList<Topic> topicList;
		try {
			topicList = (ManagedReferenceList<Topic>) dm.getBinding(THGServerDefault.TOPIC_LIST);
		} catch (com.sun.sgs.app.NameNotBoundException e) {
			topicList = null;
			logger.info("Name " + THGServerDefault.TOPIC_LIST + " is not bound!");
		}

		for (int i=0; i < nodes.getLength(); ++i) {
			Node treasureNode = nodes.item(i);
			if (treasureNode.getNodeType() == Node.ELEMENT_NODE){   
				Element treasureElement = (Element)treasureNode;    
				String name =  treasureElement.getAttribute("name");    

				float bx =  Float.parseFloat(treasureElement.getAttribute("x"));
				//Add 1 to match with tile map                                                                   
				float by =  Float.parseFloat(treasureElement.getAttribute("y")) + 1;                     

				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */				
				int treasureId = (i + 1) * 1000 + 1;

				try {
					Topic topic = null;

					String className = "othi.thg.server.events.evaluation.Treasure";

					Class<?> c = Class.forName(className);
					Constructor con = c.getConstructor(new Class[]{String.class, int.class, float.class,float.class});
					Object treasure = con.newInstance(new Object[]{name, treasureId, bx, by});

					Map<String, String> treasureProperty = null;

					if (topicList != null) {

						for (int j=0; j < topicList.size(); j++) {
							topic = topicList.get(j);
							String topicName = topic.getName();

							treasureProperty = (Map<String, String>) treasureProperties.get(
									topicName + ":" + placeName+ ":" +name);
							if (treasureProperty !=null) {

								Method setQuestID = c.getMethod("setQuestID", new Class[]{int.class});
								setQuestID.invoke(treasure, j+1);

								Reward reward = new Reward();
								reward.setExp(Integer.parseInt(treasureProperty.get("experience_point")));
								reward.setMoney(Integer.parseInt(treasureProperty.get("golds")));
								reward.setArmor(treasureProperty.get("armor"));
								reward.setTool(treasureProperty.get("tool"));
								reward.setWeapon(treasureProperty.get("weapon"));

								Method setReward = c.getMethod("setReward", new Class[]{Reward.class});
								setReward.invoke(treasure, reward);

								String question = treasureProperty.get("question");
								Method setQuestion = c.getMethod("setQuestion", new Class[]{String.class});
								setQuestion.invoke(treasure, question);

								String sampleAnswer = treasureProperty.get("sample_answer");

								Method setSampleAnswer = c.getMethod("setSampleAnswer", new Class[]{String.class});
								setSampleAnswer.invoke(treasure, sampleAnswer);

								String conclusion = treasureProperty.get("conclusion");
								Method setConclusion = c.getMethod("setConclusion", new Class[]{String.class});
								setConclusion.invoke(treasure, conclusion);

								String bindingIndex = THGServerDefault.TREASURE + placeId + ":"+ treasureId + ":" + topic.getName();
								dm.setBinding(bindingIndex, (Treasure) treasure);
							}
						}
					}

					if (treasureProperty == null) {
						String bindingIndex = THGServerDefault.TREASURE + placeId + ":"+ treasureId + ":" + 0;
						dm.setBinding(bindingIndex, (Treasure) treasure);                             
					}
					
				} catch (NoSuchMethodException ex) {
					logger.log(Level.SEVERE, null, ex);   
				} catch (SecurityException ex) {
					logger.log(Level.SEVERE, null, ex);   
				} catch (ClassNotFoundException e) {
					logger.log(Level.SEVERE, null, e);   
				} catch (InstantiationException e) {
					logger.log(Level.SEVERE, null, e);   
				} catch ( IllegalAccessException e) {
					logger.log(Level.SEVERE, null, e);                           
				} catch (InvocationTargetException e) {
					logger.log(Level.SEVERE, null, e);                         
				}                     
			}    
		}
		
		logger.info("Complete loading Treasures of " + placeName + "!");
	}    

	private static void loadMonsters(String placeName, int placeId, Element placeElement)
	{

		NodeList nodes = getNamedNode(placeElement, "monster");

		if (nodes == null) {
			logger.info("No Monsters at " + placeName + "!");
			return;
		}

		for (int i=0; i < nodes.getLength(); ++i) {
			Node monsterNode = nodes.item(i);
			if (monsterNode.getNodeType() == Node.ELEMENT_NODE){                                    
				Element monsterElement = (Element) monsterNode;
				String name = monsterElement.getAttribute("name");                      

				int bx =  Integer.parseInt(monsterElement.getAttribute("x"));

				//Add 1 to match with tile map                                                                   
				int by =  Integer.parseInt(monsterElement.getAttribute("y")) + 1;   

				Direction direction = Direction.valueOf(monsterElement.getAttribute("direction"));

				DataManager dm = AppContext.getDataManager();
				GameMonster gameMonster = (GameMonster) dm.getBinding(THGServerDefault.GAMEMONSTER + name);
				
				String className = gameMonster.getClassRef();
				
				if (className.length() == 0) {
					className = "othi.thg.server.obstacles.monsters.NormalMonster";	
				}
				
				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */				
				int monsterId = (i + 1) * 1000 + 3;

				try {        

					String imgRef = gameMonster.getImageRef();

					Class<?> c = Class.forName(className);

					/** monster id, place id, x, y */
					Constructor<?> con = c.getConstructor(new Class[]{int.class, String.class, int.class, String.class, int.class, int.class, String.class});
					Object monster = con.newInstance(new Object[]{monsterId, name, placeId, placeName, bx, by, imgRef});

					Method setFacing = c.getMethod("setFacing", new Class[]{Direction.class}); 
					setFacing.invoke(monster,direction);  

					int gameLevel = gameMonster.getGameLevel();

					Method setGameLevel = c.getMethod("setGameLevel", new Class[]{int.class}); 
					setGameLevel.invoke(monster,gameLevel);    

					int hp = gameMonster.getHp();

					Method setHP = c.getMethod("setHP", new Class[]{int.class}); 
					setHP.invoke(monster,hp);     

					int mp = gameMonster.getMp();

					Method setMP = c.getMethod("setMP", new Class[]{int.class}); 
					setMP.invoke(monster,mp);                

					int power = gameMonster.getPower();

					Method setPower = c.getMethod("setPower", new Class[]{int.class}); 
					setPower.invoke(monster,power);                               

					float speed = gameMonster.getSpeed();

					Method setSpeed = c.getMethod("setSpeed", new Class[]{float.class}); 
					setSpeed.invoke(monster,speed);    

					dm.setBinding(THGServerDefault.MONSTER + placeId + ":" + monsterId, (Monster) monster);

					addMonster((Monster) monster);	

				} catch (NoSuchMethodException ex) {
					logger.log(Level.SEVERE, className, ex);   
				} catch (SecurityException ex) {
					logger.log(Level.SEVERE, className, ex);   
				} catch (ClassNotFoundException e) {
					logger.log(Level.SEVERE, "Class Not Found : " + className, e);   
				} catch (InstantiationException e) {
					logger.log(Level.SEVERE, className, e);   
				} catch ( IllegalAccessException e) {
					logger.log(Level.SEVERE, className, e);                           
				} catch (InvocationTargetException e) {
					logger.log(Level.SEVERE, className, e);                         
				}

			}
		}        
		
		logger.info("Complete loading Monsters of " + placeName + "!");
	}

	private static void loadFoods(String placeName, int placeId, Element placeElement)
	{
		NodeList nodes = getNamedNode(placeElement, "food");

		if (nodes == null) {
			logger.info("No Foods at " + placeName + "!");
			return;
		}

		for (int i=0; i < nodes.getLength(); ++i) {
			Node foodNode = nodes.item(i);
			if (foodNode.getNodeType() == Node.ELEMENT_NODE){                                    
				Element foodElement = (Element)foodNode;
				String name = foodElement.getAttribute("name");                          

				int bx =  Integer.parseInt(foodElement.getAttribute("x"));

				//Add 1 to match with tile map                                                                   
				int by =  Integer.parseInt(foodElement.getAttribute("y")) + 1;   

				DataManager dm = AppContext.getDataManager();
				GameFood gameFood = (GameFood) dm.getBinding(THGServerDefault.ITEM_FOOD + name);                    

				int regenTime = gameFood.getReGenTime();
				String className = gameFood.getClassRef();
				String imgRef = gameFood.getImageRef();
				int attractionPoint = gameFood.getAttractionPoint();		
				
				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */					
				int foodId = (i + 1) * 1000 + 4;

				try {                            

					Class<?> c = Class.forName(className);
					Constructor<?> con = c.getConstructor(new Class[]{int.class, String.class, int.class, String.class, float.class, float.class, int.class, String.class});
					Object food = con.newInstance(new Object[]{foodId, name, placeId, placeName, bx, by, attractionPoint, imgRef});

					Method setReGenTime = c.getMethod("setReGenTime", new Class[]{int.class}); 
					setReGenTime.invoke(food, regenTime);                   

					dm.setBinding(THGServerDefault.FOOD + placeId + ":" + foodId, (Food) food);  

					addFood((Food) food);		

				} catch (NoSuchMethodException ex) {
					logger.log(Level.SEVERE, className, ex);   
				} catch (SecurityException ex) {
					logger.log(Level.SEVERE, className, ex);   
				} catch (ClassNotFoundException e) {
					logger.log(Level.SEVERE, "Class Not Found : " + className, e);   
				} catch (InstantiationException e) {
					logger.log(Level.SEVERE, className, e);   
				} catch ( IllegalAccessException e) {
					logger.log(Level.SEVERE, className, e);                           
				} catch (InvocationTargetException e) {
					logger.log(Level.SEVERE, className, e);                         
				}                     
			}                    
		}     		
		logger.info("Complete loading Foods of " + placeName + "!");
	}    


	private static void loadPortals(String placeName, int placeId, Element placeElement)
	throws IOException
	{   
		NodeList portalList = placeElement.getElementsByTagName("portal");

		if (portalList == null) {
			logger.info("No Portals at " + placeName + "!");
			return;
		}

		for (int i = 0; i < portalList.getLength(); i++) {
			Node portalNode = portalList.item(i);
			if (portalNode.getNodeType() == Node.ELEMENT_NODE){      
				Element portalElement = (Element)portalNode;
				float x = Float.parseFloat(portalElement.getAttribute("x"));
				float y = Float.parseFloat(portalElement.getAttribute("y"));    
				String portalName = portalElement.getAttribute("name");    
				
				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */				
				Portal portal = new Portal( (i + 1) * 1000 + 5, placeId, x, y, portalName);
				
				Destination d = 
					parseDestinationElement(getFirstNamedElement(portalElement, "destination"));
				if (d != null) {
					portal.setDestPlaceName(d.placeName);
					portal.setDestPortalName(d.portalName);
				}
				String className = 
					parseImplementationElement(getFirstNamedElement(portalElement, "implementation"), "class");   
				if (className != null) {
					portal.setClassName(className);
				}

				DataManager dm = AppContext.getDataManager();
				dm.setBinding(THGServerDefault.PORTAL + placeId + ":" + portalName, portal);    
			}
		}                    
		logger.info("Complete loading Portals of " + placeName);  
	}

	private static void loadTerrainMap(String placeName, int placeId, String mapFileRef) throws IOException
	{
		THGMapStructure thgMap = TMXLoader.load(THGServerDefault.getMapsDir() + mapFileRef);
		thgMap.buildLayers();
		
		int width = thgMap.getWidth();
		int height = thgMap.getHeight();

		Terrain[][][] terrain; 
		terrain = new Terrain[2][width][height];
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < width; j++) {              
				Arrays.fill(terrain[i][j], Terrain.EMPTY);
			}    
		}

		//Finding protection area        
		LayerDefinition protectionLayer = thgMap.getLayer(PROTECTION_LAYER);
		protectionLayer.build();        

		for(int j=0;j<height;j++) {
			for(int i=0;i<width;i++) {
				if (protectionLayer.getTileAt(i, j)!=0){
					terrain[1][i][j]= Terrain.PROTECT;
				}    
			}
		}    

		//Finding block
		LayerDefinition collisionLayer = thgMap.getLayer(COLLISION_LAYER);
		collisionLayer.build();

		for(int j=0;j<height;j++) {
			for(int i=0;i<width;i++) {
				if (collisionLayer.getTileAt(i, j)!=0){
					terrain[0][i][j]= Terrain.BLOCK;
				}    
			}
		}        

		//Portals        
		DataManager dm = AppContext.getDataManager();
		String binding = THGServerDefault.PORTAL + placeId + ":";
		String bindingName = dm.nextBoundName(binding);   
		
		while (bindingName != null && bindingName.startsWith(binding)) {
			Portal portal = (Portal) dm.getBinding(bindingName);
			int posX = (int) portal.getPosX();
			int posY = (int) portal.getPosY();
			terrain[0][posX][posY] = Terrain.PORTAL;
			bindingName = dm.nextBoundName(bindingName);
		}   

		//Treasures       
		binding = THGServerDefault.TREASURE + placeId;
		bindingName = dm.nextBoundName(binding);
		
		while (bindingName != null && bindingName.startsWith(binding)) {
			Treasure treasure = (Treasure) dm.getBinding(bindingName);
			int posX = (int) treasure.getPosX();
			int posY = (int) treasure.getPosY();
			terrain[0][posX][posY] = Terrain.TREASURE;
			bindingName = dm.nextBoundName(bindingName);  
		} 

		//Initial Foods before playing       
		binding = THGServerDefault.FOOD + placeId + ":";
		bindingName = dm.nextBoundName(binding);
		
		while (bindingName != null && bindingName.startsWith(binding)) {
			Food food = (Food) dm.getBinding(bindingName);
			int posX = (int) food.getPosX();
			int posY = (int) food.getPosY();
			terrain[0][posX][posY] = Terrain.FOOD;
			bindingName = dm.nextBoundName(bindingName); 
		} 

		TerrainMap terrainMap = new TerrainMap(placeId, width, height, terrain);

		dm.setBinding(THGServerDefault.TERRAIN_MAP + placeId, terrainMap);

		logger.info("Complete loading TerrainMaps of " + placeName + "!");     
	}

	/** load NPCs and Quests. */
	@SuppressWarnings("unchecked")
	private static void loadNPCs(String placeName, int placeId, Element placeElement) throws IOException
	{

		NodeList nodes = getNamedNode(placeElement, "npc");

		if (nodes == null) {
			logger.info("No NPCs at " + placeName + "!");
			return;
		}

		GuideXML guideXML = new GuideXML(DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.DEFAULT_COURSE_ID
				+ File.separator + DefinitionsDefault.GUIDE_XML);
		Map<String, Object> guideProperties = guideXML.getProperties();

		DataManager dm = AppContext.getDataManager();
		ManagedReferenceList<Topic> topicList;

		try {
			topicList = (ManagedReferenceList<Topic>) dm.getBinding(THGServerDefault.TOPIC_LIST);
		} catch (com.sun.sgs.app.NameNotBoundException e) {
			topicList = null;
			logger.info("Name " + THGServerDefault.TOPIC_LIST + " is not bound!");
		}

		for (int i=0; i < nodes.getLength(); ++i) {
			Node npcNode = nodes.item(i);
			if (npcNode.getNodeType() == Node.ELEMENT_NODE){                                    
				Element npcElement = (Element)npcNode;
				String x = npcElement.getAttribute("x");
				String y = npcElement.getAttribute("y");                    
				String name = npcElement.getAttribute("name");
				String className = npcElement.getAttribute("class");
				if (name.length() == 0) name = className.substring(className.lastIndexOf('.')+1);
				String level = npcElement.getAttribute("level");
				String hp = npcElement.getAttribute("hp");
				String imgRef = npcElement.getAttribute("imgRef");
				String direction = npcElement.getAttribute("direction");                       
				String speed = npcElement.getAttribute("speed");
				List<String[][]> paths = loadNpcPaths(getNamedNode(npcElement, "path"));   

				/** Player: x000, Treasure: x001, Npc: x002, Monster: x003, Food: x004, Portal: x005 */				
				int npcId = (i + 1) * 1000 + 2;

				try {
					if (className.length() == 0) {
						className = "othi.thg.server.agents.npcs.NormalNpc";
					}       

					Class<?> c = Class.forName(className);
					Constructor<?> con = c.getConstructor(new Class[]{int.class,String.class,String.class});
					Object npc = con.newInstance(new Object[]{npcId, name, placeName});              

					dm = AppContext.getDataManager();
					TerrainMap tm = (TerrainMap) dm.getBinding(THGServerDefault.TERRAIN_MAP + placeId);
					boolean blocked[][] = tm.getBlocked();
					int width = tm.getWidth();
					int height = tm.getHeight();

					if (paths != null) {
						Class<?>[] argTypes = { int.class, int.class, boolean[][].class, List.class, int.class};
						Method createPath = c.getMethod("createPath", argTypes);                            
						createPath.invoke(npc,width,height, blocked, paths, THGServerDefault.PATH_SEARCH_DEPTH);                            
					} else {
						Class<?>[] argTypes = { int.class, int.class, boolean[][].class, int.class};
						Method createPath = c.getMethod("createPath", argTypes);                            
						createPath.invoke(npc,width,height, blocked, THGServerDefault.PATH_SEARCH_DEPTH);
					}                          

					if (x.length() > 0 && y.length() > 0) {
						float cx =  Float.parseFloat(x);
						//Add 1 to match with tile map                                                                   
						float cy =  Float.parseFloat(y) + 1;
						Method setXY = c.getMethod("setXY", new Class[]{float.class, float.class}); 
						setXY.invoke(npc,cx,cy);
					}

					if (hp.length() > 0) {
						int npcHP = Integer.parseInt(hp);                                       
						Method setHP = c.getMethod("setHP",new Class[]{int.class}); 
						setHP.invoke(npc,npcHP);   
					}

					if (level.length() > 0) {
						int npcGameLevel = Integer.parseInt(level);                                           
						Method setGameLevel = c.getMethod("setGameLevel",new Class[]{int.class}); 
						setGameLevel.invoke(npc,npcGameLevel);                    
					}

					if (direction.length() > 0) {
						Direction npcDirection = Direction.valueOf(direction);
						Method setFacing = c.getMethod("setFacing", new Class[]{Direction.class}); 
						setFacing.invoke(npc,npcDirection);
					}

					if (speed.length() > 0) {
						float npcSpeed = Float.parseFloat(speed);                                           
						Method setSpeed = c.getMethod("setSpeed",new Class[]{float.class}); 
						setSpeed.invoke(npc,npcSpeed);  
					}

					if (imgRef.length() > 0) {                 
						Method setImageRef = c.getMethod("setImageRef",new Class[]{String.class}); 
						setImageRef.invoke(npc,imgRef);  
					}

					String guideNickName = null;
					
					if (topicList != null) {

						for (int j=0; j < topicList.size(); j++) {
							Topic topic = topicList.get(j);
							String topicName = topic.getName();

							Map<String, String> guideProperty = (Map<String, String>) guideProperties.get(topicName + ":" + placeName+ ":" + name);

							if (guideProperty != null && !guideProperty.isEmpty()) {

								guideNickName = guideProperty.get("nick_name");
								String talk = guideProperty.get("talk");
								String question = guideProperty.get("question");

								Method setNickName = c.getMethod("setNickName", new Class[]{String.class});
								setNickName.invoke(npc, guideNickName);

								Method addInformation = c.getMethod("addInformation", new Class[]{String.class, String.class});
								addInformation.invoke(npc, topicName, talk);
								addInformation.invoke(npc, topicName, question);

								//This gives the npc the topic names that it will give an orientation.
								String key = topicName + ":" + Station.StationType.Orientation.toString();
								OrientationStation station = (OrientationStation) dm.getBinding(THGServerDefault.STATION + key);
								int questID = 0;

								if (name.equals(station.getGuide())) {
									
									/** For orientation guide, set the topic to orient. 
									    Station Group Id = Topic Id * 1000
									    Topic Id = Quest Id
									*/
									questID = station.getGroupId()/1000;
									Method addOrientationTopic = c.getMethod("addOrientationTopic", new Class[]{int.class, String.class});
									addOrientationTopic.invoke(npc, questID, topicName);

									/** create Quest Class. */
									String  questClassName = "othi.thg.server.events.orientation.Quest";
									Class<?> cQuest = Class.forName(questClassName);
									Constructor<?> conQuest = cQuest.getConstructor(new Class[]{int.class,String.class});
									Object quest = conQuest.newInstance(new Object[]{questID, topicName});

									Method setIntroduction = cQuest.getMethod("setIntroduction",new Class[]{String.class});
									setIntroduction.invoke(quest,talk);

									Method setQuestion = cQuest.getMethod("setQuestion",new Class[]{String.class});
									setQuestion.invoke(quest,question);

									dm.setBinding(Quest.ITEM_QUEST + questID, (Quest) quest);
								}

								/** The following codes are intended that one speaker will be able to deal with several subtopics. */								
								dm.setBinding(THGServerDefault.GUIDE_NPC + placeId + ":"
										+ npcId + ":" + topicList.indexOf(topic) + 1, (Npc) npc);								
							}
						}

					}

					// The name becomes the guideNickname if it has not been given any value yet.
					if (guideNickName == null) {
						Method setNickName = c.getMethod("setNickName", new Class[]{String.class});
						setNickName.invoke(npc, name);
					}
					
					dm.setBinding(THGServerDefault.NPC + placeId + ":" + npcId, (Npc) npc);
					logger.log(Level.INFO, "NPC [{0}] is loaded at a place [{1}].", new Object[] {npcId, placeId});
					addNPC((Npc) npc);

				} catch (NoSuchMethodException ex) {
					logger.log(Level.SEVERE, className, ex);   
				} catch (SecurityException ex) {
					logger.log(Level.SEVERE, className, ex);   
				} catch (ClassNotFoundException e) {
					logger.log(Level.SEVERE, "Class Not Found : " + className, e);   
				} catch (InstantiationException e) {
					logger.log(Level.SEVERE, className, e);   
				} catch ( IllegalAccessException e) {
					logger.log(Level.SEVERE, className, e);                           
				} catch (InvocationTargetException e) {
					logger.log(Level.SEVERE, className, e);                         
				}                   
			}    
		}        
		logger.info("Complete loading NPCs of " + placeName + "!");
	}    

	private static List<String[][]> loadNpcPaths(NodeList nodes) throws IOException
	{
		if (nodes == null) {
			return null;
		}    

		List<String[][]> paths = new LinkedList<String[][]>();
		for (int i=0; i < nodes.getLength(); ++i) {
			Node pathNode = nodes.item(i);
			if (pathNode.getNodeType() == Node.ELEMENT_NODE){   
				String[][] path = new String[2][2];                    
				Element pathElement = (Element)pathNode;
				String sx = pathElement.getAttribute("sx");
				String sy = pathElement.getAttribute("sy");       
				String dx = pathElement.getAttribute("dx");
				String dy = pathElement.getAttribute("dy");        
				path[0][0] = sx;
				path[0][1] = sy;
				path[1][0] = dx;
				path[1][1] = dy;
				paths.add(path);
			}
		}
		return paths;
	}    

	private static Destination parseDestinationElement(Element element){
		if (element == null) {
			return null;
		}
		String placeName = element.getAttribute("place");
		String portalName = element.getAttribute("name");
		return new Destination(placeName, portalName);
	}

	private static String parseImplementationElement(Element element, String attribute){
		if (element == null) {
			return null;
		}
		return element.getAttribute(attribute);
	}

	private static NodeList getNamedNode(Element element, String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}
		return list;
	}

	private static Element getFirstNamedElement(Element element, String name) {
		NodeList list = element.getElementsByTagName(name);
		if (list.getLength() == 0) {
			return null;
		}
		return (Element) list.item(0);
	}

	private static class Destination {
		String placeName;
		String portalName;

		private Destination(String placeName, String portalName) {
			this.placeName = placeName;
			this.portalName = portalName;
		}                       
	}
}