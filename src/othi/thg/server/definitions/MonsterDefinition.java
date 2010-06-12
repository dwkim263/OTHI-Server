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
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.w3c.dom.*;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import othi.thg.server.THGServerDefault;


public class MonsterDefinition {
    
    private static final Logger logger = Logger.getLogger(MonsterDefinition.class.getName());   
  
    public static void loadConfiguredMonsters() throws IOException {
              
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.MONSTER_XML);

            Element element = document.getDocumentElement();
            if (!element.getNodeName().equals("monsters")) {
                    throw new IOException("Not a monster file");
            }

            NodeList monsterlist = element.getElementsByTagName("monster");
            for (int i = 0; i < monsterlist.getLength(); i++) {
                Node monsterNode = monsterlist.item(i);
                if (monsterNode.getNodeType() == Node.ELEMENT_NODE){                                    
                    Element monsterElement = (Element)monsterNode;
                    
                    String name = monsterElement.getAttribute("name");    
                    
                    String imgRef = monsterElement.getAttribute("file");                            
                    
                    String[] competence = 
                        parseCompetenceElement(getFirstNamedElement(monsterElement, "competence"));         
                    
                    int gameLevel = Integer.parseInt(competence[0]); //level
                    int hp = Integer.parseInt(competence[1]); // hp
                    int mp = Integer.parseInt(competence[2]); // mp
                    int power = Integer.parseInt(competence[3]); // power
                    float speed = Float.parseFloat(competence[4]); // speed
                   
                    String[] implement =  
                        parseImplementElement(getFirstNamedElement(monsterElement, "implement"));                                        
                    
                    String classRef = implement[0];
                    
                    GameMonster gameMonster = new GameMonster(i, name, imgRef, gameLevel, 
                       hp, mp, power, speed, classRef);
                    
                    DataManager dm = AppContext.getDataManager();
                    dm.setBinding(THGServerDefault.GAMEMONSTER + name,  gameMonster);
                }
            }
        } catch (ParserConfigurationException e) {
            logger.log(Level.SEVERE, "Parsing error", e);        
        } catch ( SAXException e) {
            logger.log(Level.SEVERE, null, e); 
        }
        logger.info("Complete loading Monster Definition!");               
    }
    
    private static String[] parseCompetenceElement(Element element){
        if (element == null) {
                return null;
        }
        
        String[] competence = new String[5];        
        
        competence[0] = element.getAttribute("level");
        competence[1] = element.getAttribute("hp");
        competence[2] = element.getAttribute("mp");
        competence[3] = element.getAttribute("power");
        competence[4] = element.getAttribute("speed");        
        return competence;
    }  
    
    private static String[] parseImplementElement(Element element){
        if (element == null) {
                return null;
        }
        String[] implement = new String[1];     
        
        implement[0] = element.getAttribute("class");
        return implement;
    }  
    
    private static Element getFirstNamedElement(Element element, String name) {
            NodeList list = element.getElementsByTagName(name);
            if (list.getLength() == 0) {
                    return null;
            }
            return (Element) list.item(0);
    }
            
 }