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
 * @author DongWon Kim
 */
import java.io.IOException;
import java.io.File;
import java.util.Map;
import java.util.Iterator;
import java.util.logging.Logger;

import othi.thg.server.THGServerDefault;
import othi.thg.server.ManagedReferenceList;
import othi.thg.server.definitions.xml.StationXML;
import othi.thg.server.information.Topic;
import othi.thg.server.stations.EvaluationStation;
import othi.thg.server.stations.HelpStation;
import othi.thg.server.stations.OrientationStation;
import othi.thg.server.stations.Station.StationType;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;


public class StationDefinition {
    
    private static final Logger logger = Logger.getLogger(StationDefinition.class.getName());   
  
	public static void loadConfiguredStations() throws IOException {

        String xmlFileName = DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.DEFAULT_COURSE_ID + File.separator + DefinitionsDefault.STATION_XML;

        File xmlFile = new File(xmlFileName);

        if (!xmlFile.exists()) {
            logger.info("Not found a file " + DefinitionsDefault.STATION_XML + " of " + DefinitionsDefault.DEFAULT_COURSE_ID + " !");
            return;
        }

        DataManager dm;

        ManagedReferenceList<Topic> topicList = new ManagedReferenceList<Topic>();

        StationXML stationXML = new StationXML(xmlFileName);

        Map<String, Object> stationProperties = stationXML.getProperties();

        Iterator<String> it = stationProperties.keySet().iterator();

        int numberOfStations = 0;

        while(it.hasNext()) {

            numberOfStations++;

            String key = (String) it.next();
            String[] keys = key.split(":");
            String topicName = keys[0];
            String name = keys[1];

            Topic topic = new Topic(topicName);

            if (!topicList.contains(topic)) {
                topicList.add(topic);
            }


            //we can get the topic id from the topicList or the station group id.
            int topicId = topicList.indexOf(topic) + 1;

            //one station group id for each topic.
            //stationGroupId.
            int stationGroupId = topicId * 1000;

            // station id in a topic.
            int stationId = stationGroupId + numberOfStations;

            Map<String, String> stationProperty = (Map<String, String>) stationProperties.get(key);
            String placeName = stationProperty.get("place");
            String placeNickName = stationProperty.get("place_nick_name");

            StationType stationType;
            if (name.equals(StationType.Orientation.toString())) {
                stationType = StationType.Orientation;
            } else if (name.equals(StationType.Evaluation.toString())) {
                stationType = StationType.Evaluation;
            } else if (name.equals(StationType.Obstacle.toString())) {
                stationType = StationType.Obstacle;
            } else {
                stationType = StationType.Help;
            }

            switch (stationType) {
             case Orientation:
                 OrientationStation orientationStation = new OrientationStation(
                                        stationId, placeName, placeNickName, topicName, name);
                 orientationStation.setGroupId(stationGroupId);
                 orientationStation.setStationType(stationType);
                 String guide = stationProperty.get("guide");
                 orientationStation.setGuide(guide);
                 String nextAvailableStationNames = stationProperty.get("next_quests");
                 orientationStation.setNextAvailableStationNames(nextAvailableStationNames);
                 dm = AppContext.getDataManager();
                 dm.setBinding(THGServerDefault.STATION + key, orientationStation);
                 break;
             case Evaluation:
                 EvaluationStation evaluationStation = new EvaluationStation(
                                        stationId, placeName, placeNickName, topicName, name);
                 evaluationStation.setGroupId(stationGroupId);
                 evaluationStation.setStationType(stationType);
                 String treasureBoxName = stationProperty.get("treasure_box");
                 evaluationStation.setTreasureBoxName(treasureBoxName);
                 String clue = stationProperty.get("clue");
                 evaluationStation.setClue(clue);
                 dm = AppContext.getDataManager();
                 dm.setBinding(THGServerDefault.STATION + key, evaluationStation);
             case Obstacle:
                  break;
             case Help:
                 HelpStation helpStation = new HelpStation(
                                        stationId, placeName, placeNickName, topicName, name);
                 helpStation.setGroupId(stationGroupId);
                 helpStation.setStationType(stationType);
                 guide = stationProperty.get("guide");
                 helpStation.setGuide(guide);
                 nextAvailableStationNames = stationProperty.get("next_quests");
                 helpStation.setNextAvailableStationNames(nextAvailableStationNames);
                 clue = stationProperty.get("clue");
                 helpStation.setClue(clue);
                 dm = AppContext.getDataManager();
                 dm.setBinding(THGServerDefault.STATION + key, helpStation);
                 break;
             default: logger.info("Invalid Station Type!");
            }
        }

        dm = AppContext.getDataManager();
        dm.setBinding(THGServerDefault.TOPIC_LIST, topicList);

        logger.info("Complete loading Station Definition!");


    }
 }