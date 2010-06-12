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
 * defining default value of game
 * @author Dong Won Kim
 */

public class DefinitionsDefault {
    
    public static final String DEFAULT_COURSE_ID = "MATH5";	

    //Treasure Hunt Model Framework

    public static final String INTRODUCTION_XML = "introduction.xml";
    public static final String ORIENTATION_XML = "orientation.xml";
    public static final String EVALUATION_XML = "evaluation.xml";
    public static final String SUB_TOPIC_XML = "sub-topics.xml";
    public static final String STATION_XML = "stations.xml";
    public static final String TREASURE_XML = "treasures.xml";
    public static final String GUIDE_XML = "guides.xml";

    //Game Objects
    public static final String LEVEL_XML = "level.xml";
    public static final String MONSTER_XML = "monster.xml";
    public static final String FOOD_XML = "food.xml";
    public static final String NPC_XML = "npc.xml";
    
    //PLACE XML
    public static final String PLACE_XML = "place.xml";
    public static final String PLACE_QUALIFIED_NAME = "places";
    public static final String PLACE_FIRST_ELEMENT_NAME = "place";
    public static final String[] PLACE_FIRST_ELEMENT_ATTRIBUTES = new String[] {"name", "level", "x", "y", "file"};

    //TOOL XML
    public static final String TOOL_XML = "tool.xml";
    public static final String TOOL_QUALIFIED_NAME = "tools";
    public static final String TOOL_FIRST_ELEMENT_NAME = "tool";
    public static final String[] TOOL_FIRST_ELEMENT_ATTRIBUTES = new String[] {"name", "file"};

    //ARMOR XML
    public static final String ARMOR_XML = "armor.xml";
    public static final String ARMOR_QUALIFIED_NAME = "armors";
    public static final String ARMOR_FIRST_ELEMENT_NAME = "armor";
    public static final String[] ARMOR_FIRST_ELEMENT_ATTRIBUTES = new String[] {"name", "file"};

    //WEAPON XML
    public static final String WEAPON_XML = "weapon.xml";
    public static final String WEAPON_QUALIFIED_NAME = "weapons";
    public static final String WEAPON_FIRST_ELEMENT_NAME = "weapon";
    public static final String[] WEAPON_FIRST_ELEMENT_ATTRIBUTES = new String[] {"name", "file"};

    private static String definitionsDir;

    private static DefinitionsDefault definitions;
    

    /** Returns the GameScreen object */
    public static DefinitionsDefault get() {    
            return definitions;
    }

    public static String getDefinitionsDir() {
        return definitionsDir;
    }

    public static void setDefinitionsDir(String dir) {
        DefinitionsDefault.definitionsDir = dir;
    }
}
