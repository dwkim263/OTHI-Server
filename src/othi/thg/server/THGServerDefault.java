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

/**
 * defining default value of game
 * @author Dong Won Kim
 */
public class THGServerDefault {
    
    public static final int PATH_SEARCH_DEPTH = 50;
    
    //Strings of SetBinding
    public static final String TOPIC_LIST = "TopicList.";
    public static final String COMPLETED_QUEST = "CompetedQuest.";    
    public static final String NPCDIALOGUE = "NpcDialogue.";
    public static final String STATION = "Station.";
    
    public static final String FOOD_LIST = "FoodList";
    public static final String FOOD = "Food.";
    public static final String GAMEBOARD = "GameBoard.";
    public static final String BOARD_LIST = "BoardList.";    
    public static final String GAMEMONSTER = "GameMonster.";    
    public static final String HELP = "Help.";
    public static final String HELP_LIST = "Help.";
    public static final String HUMAN_LIST = "HumanList.";
    public static final String GUIDE_NPC = "GuideNpc.";
    public static final String ITEM_DOCUMENT = "ItemDocument.";
    public static final String ITEM_FOOD = "ItemFood.";    
    public static final String ITEM_HELP = "ItemHelp.";    
    public static final String ITEM_QUIZ = "ItemQuiz.";    
//    public static final String ITEM_TREASURE = "ItemTreasure.";
    public static final String KNOWNNPC = "KnownNPC.";
    public static final String LEVEL = "Level.";
    public static final String MASTER_CONTROL = "MasterControl.";
    public static final String MEDIATOR_LIST = "MediatorList.";
    public static final String MONSTER = "Monster.";
    public static final String MONSTER_LIST = "MonsterList.";
    
    public static final String MYDIALOGUE = "MyDialogue.";
    public static final String MYDOCUMENT = "MyDocument.";
    public static final String MYQUEST = "MyQuest.";    
    public static final String MYQUIZ = "MyQuiz.";    
    public static final String MYTREASURE = "MyTreasure.";
    
    public static final String NPC = "Npc.";
    public static final String NPC_LIST = "NPCList.";
    public static final String PLACE = "Place.";
    
    public static final String PLAYER_ARMOR = "PlayerArmor.";
    public static final String PLAYER_COMPETENCE = "PlayerCompetence.";
    public static final String PLAYER_INVENTORY = "PlayerInventory.";
    public static final String PLAYER_LOGBOOK = "PlayerLogbook.";
    public static final String PLAYER_LIST = "PlayerList.";
    public static final String PLAYER_LISTENER = "PlayerListener.";
    public static final String PLAYER_WEAPON = "PlayerWeapon.";
    
    public static final String PORTAL = "Portal.";

    public static final String QUEST_LIST = "Quest.";

    public static final String TERRAIN_MAP = "TerrainMap.";
    public static final String TREASURE = "Treasure.";
    public static final String USERPREFIX = "Player.";        
    
    public static enum GameState {
        GAMELOADING, INTRODUCING, LOGIN, LOGINING, LOGINED,LOGINFAILED,STAGELOADING, LOBBY, PAUSED, PLAYING, DISCONNECTED
    };
    
    
    public static enum EntityType {
            PLAYER, MONSTER, NPC
    }

    public static enum Action {
            ADD, MOVE, TURN, SHOOT, KILL, REMOVE
    }

    public static enum Terrain {
        BLOCK, FOOD, EMPTY, MONSTER, PLAYER, NPC, MONSTERTOMB, PLAYERTOMB, PORTAL, PROTECT, TREASURE
    }
    
    public static enum ItemGroup {
        GENERAL, GOLD, SWORD, SHIELD, FOOD
    }

/*
    public static final String[] Layer = {
         "0_floor", "1_terrain", "2_object", "3_roof", "4_roof_add", "collision","protection"
    };
 */       
    
    private static THGServerDefault thgServer;
    
    private static String mapsDir;
    
    public static String getMapsDir() {
		return mapsDir;
	}

	public static void setMapsDir(String mapsDir) {
		THGServerDefault.mapsDir = mapsDir;
	}

	public static void setTHGServerDefault(THGServerDefault appDefault) {
            THGServerDefault.thgServer = appDefault;
    }

    /** Returns the GameScreen object */
    public static THGServerDefault get() {    
            return thgServer;
    }

    public THGServerDefault() {
        
    }
}
