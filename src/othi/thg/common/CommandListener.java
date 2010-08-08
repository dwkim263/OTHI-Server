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

package othi.thg.common;

import othi.thg.common.Commands.Direction;
/*
 * interface of commands
 * @author Dong Won Kim
 */ 
public interface CommandListener {

        void commandHp(int id, int hp);
        
        void commandBlock(int id, float x, float y);

        void commandClearDialogueHistory(int id);

        void commandClearMsgLog(int id);
            
        void commandRequestLevel(int id);

        void commandRequestPassAway(int id);
        
        void commandPassAway(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int money);
        
        void commandLevel(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int power);
        
        void commandAnswerQuiz(int id, int placeId, int treasureId, int topicId, String answer);
        
        void commandPayment(int id, int treasureId, String name, int hp, int mp, int myExp, int money, String armor, String tool, String weapon);
                
        void commandQuestIntroduction(int id, int npcId, String npcName, int questID, String questName, String introduction);

        void commandQuestQuestion(int id, int npcId, String npcName, int questID, String questName, String question);

        //For help and clue
        void commandQuestInformation(int id, int npcId, String npcName, int questID, String questName, String information, int iType);
        
    	void commandChangeQuestStatus(int id, int questID, int status);

        //Giving a question at the evaluation station.
        void commandGiveQuestion(int id, int treasureId, int questID);

        void commandGiveMark(int id, int questID, int mark);
        
        void commandAttackMonster(int id, int placeId, int monsterId);
        
        void commandTurn(int id, int placeId, Direction direction);

        void commandTalk(int id, String name, String speaking);
        
        void commandTalktoNPC(int npcId, int replyFlag, String dailogue);
        
        void commandMoveForward(int id, int placeId, float tx, float ty, Direction direction);

        void commandPortal(int id, int placeId, String portalName);
        
        void commandAddPortal(int id, int placeId, String portalName, float x, float y, int isOneWay);
        
        void commandScore(int id, int score);
        
        void commandInitializePlayer(int id, int level, int hp, int maxHp, int mp, int maxMp, 
                                     int myExp, int maxExp, int money,
                                     String[] dialogues, int outfitCode );

        void commandEnterGameBoard(int id, int placeId, String mapFileRef);

        void commandKill(int id);

        void commandJoinGroup(int id);
        
        void commandLeaveGameBoard(int id, String placeName);

        void commandStart(int id);
        
        void commandStop(int id);
        
        void commandOpenTreasure(int id, int placeId, int treasureId, int topicId);    
        
        void commandAddPlayer(int id, String name, int level, int outfitCode, float x, float y, int facing);

        void commandAddNPC(int npcId, String npcName, int level, int hp, int mp, String imgRef, float x, float y, int facing);
               
        void commandAddTreasure(int treasureId, int questID, float x, float y);

        void commandFrame(int id, int frameCount);

        void commandSetID(int id);
        
        void commandAddFood(int id, String name, float x, float y, int attractionPoint, String imgRef);
        
        void commandAddMonster(int id, String name, float x, float y, int level, int hp, int mp, 
                               int power, Direction direction, String imgRef);
        
        void commandAddMonsterTomb(int id, float x, float y);

        void commandRemoveMonster(int id);
        
        void commandRemoveMonsterTomb(int id, float x, float y);

        void commandRemovePlayer(int id);

        void commandAddPlayerTomb(int id, float x, float y);

        void commandRemovePlayerTomb(int id, float x, float y);

        void commandLogin(String userName, String password);
        
        void commandRemoveFood(int id, float x, float y);        
}
