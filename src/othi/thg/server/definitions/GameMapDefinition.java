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

/**
 *
 * @author Steve
 */
import java.util.*;
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import othi.thg.server.definitions.xml.GuideXML;
import othi.thg.server.definitions.xml.PlaceXML;

public class GameMapDefinition {
    private static final Logger logger = Logger.getLogger(GameMapDefinition.class.getName());
    
    private static Map<String, GameMapDefinition> gameMaps = new HashMap<String, GameMapDefinition>();

    private static List<String> placesForNPC = new LinkedList<String>();

    private static List<String> placesForTreasureBox = new LinkedList<String>();

    private static PlaceXML placeXML;

    private List<String> gameGuides = new LinkedList<String>();

    private List<String> treasureBoxes = new LinkedList<String>();
    
    private String placeName;
    
    private GameMapDefinition(String placeName){
        gameMaps.put(placeName, this);
    }

    public String getPlaceName() {
        return placeName;
    }
    
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

   public static List<String> getPlacesForNPC(String courseId, String topicTitle) {
        if (gameMaps.isEmpty()) {
            setGameMaps(courseId, topicTitle);
        } else {
            setGuides(courseId, topicTitle);
        }
        return placesForNPC;
    }

    public void setPlacesForNPC(List<String> placesForNPC) {
        GameMapDefinition.placesForNPC = placesForNPC;
    }

    public static List<String> getPlacesForTreasureBox(String courseId, String topicTitle) {
        if (gameMaps.isEmpty()) {
            setGameMaps(courseId, topicTitle);
        } else {
            setTreasureBoxes(courseId);
        }
        return placesForTreasureBox;
    }

    public void setPlacesForTreasureBox(List<String> placesForTreasureBox) {
        GameMapDefinition.placesForTreasureBox = placesForTreasureBox;
    }

    public static List<String> getGameGuides(String courseId, String topicTitle, String placeName) {
        GameMapDefinition gameMap = GameMapDefinition.getGameMaps(courseId, topicTitle).get(placeName);
        if (gameMap != null) {
            List<String> guideNames = gameMap.getGameGuides();

            if (guideNames != null) {
                return guideNames;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<String> getGameGuides() {
        return gameGuides;
    }

    public void setGameGuides(List<String> gameGuides) {
        this.gameGuides = gameGuides;
    }

    public static List<String> getTreasureBoxes(String courseId, String topicTitle, String placeName) {
        GameMapDefinition gameMap = GameMapDefinition.getGameMaps(courseId, topicTitle).get(placeName);
        if (gameMap != null) {
            List<String> treasureBoxNames = gameMap.getTreasureBoxes();
            if (treasureBoxNames != null) {
                return treasureBoxNames;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public List<String> getTreasureBoxes() {
        return treasureBoxes;
    }

    public void setTreasureBoxes(List<String> treasureBoxes) {
        this.treasureBoxes = treasureBoxes;
    }

    public static void setGameMaps(Map<String, GameMapDefinition> gameMaps) {
        GameMapDefinition.gameMaps = gameMaps;
    }

    public static GameMapDefinition getById(String placeName) {
        return gameMaps.get(placeName);
    }


    public static Map<String, GameMapDefinition> getGameMaps(String courseId, String topicTitle) {
        if (gameMaps.isEmpty()) {
            setGameMaps(courseId, topicTitle);
        }
        return gameMaps;
    }

    public static void setGuides(String courseId, String topicTitle) {
        //npc load
        Map <String, Object> npcs = placeXML.getObjectProperties("npc");

        List<String> places = new LinkedList<String>();

        Iterator<String> it = npcs.keySet().iterator();
        while(it.hasNext()) {
            String placeName = (String) it.next();

            List<String> npcNames = (List<String>) npcs.get(placeName);

            if (npcNames !=null && npcNames.size()>0) {
                //check available guides by referring to guide xml
                List<String> guides = new LinkedList<String>();

                GuideXML guideXML = new GuideXML(DefinitionsDefault.getDefinitionsDir() + courseId + File.separator + DefinitionsDefault.GUIDE_XML);
                Map<String, Object> guideMap = guideXML.getProperties();

                for (String guide: npcNames) {
                   if (!guideMap.containsKey(topicTitle + ":" + placeName + ":" + guide)) guides.add(guide);
                }

                if (!guides.isEmpty()) {
                    GameMapDefinition gameMap = new GameMapDefinition(placeName);
                    places.add(placeName);
                    gameMap.setGameGuides(guides);
                }
            }
        }
        GameMapDefinition.placesForNPC = places;
    }

    public static void setTreasureBoxes(String courseId) {
                //treasureBox load
        Map <String, Object> treasureBoxes = placeXML.getObjectProperties("treasure");

        List<String> places = new LinkedList<String>();

        Iterator<String> it = treasureBoxes.keySet().iterator();
        while(it.hasNext()) {
            String placeName = (String) it.next();

            List<String> treasureBoxNames = (List<String>) treasureBoxes.get(placeName);
            if (treasureBoxNames !=null && treasureBoxNames.size()>0) {
                GameMapDefinition gameMap = getById(placeName);
                if (gameMap == null) gameMap = new GameMapDefinition(placeName);
                places.add(placeName);
                gameMap.setTreasureBoxes(treasureBoxNames);
            }
        }
        GameMapDefinition.placesForTreasureBox = places;
    }

    public static void setGameMaps(String courseId, String topicTitle) {
        PlaceXML placeXml = new PlaceXML();
        setPlaceXML(placeXml);
        setGuides(courseId, topicTitle);
        setTreasureBoxes(courseId);
    }

    public static PlaceXML getPlaceXML() {
        return placeXML;
    }

    public static void setPlaceXML(PlaceXML placeXml) {
        GameMapDefinition.placeXML = placeXml;
    }
}
