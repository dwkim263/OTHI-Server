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

import java.nio.ByteBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import othi.thg.server.agents.player.Player;

/**
 * This is shared between the client and the server and used to send
 * commands from the player to the server. It is an entirely static class.
 * It cannot be instantiated.
 * @author Dong Won Kim
 */
public class Commands {           
	
    private static final Logger logger = Logger.getLogger(Player.class.getName());   	

	public static enum OpCode {
		ADDFOOD,     		
		ADDMONSTER,
		ADDMONSTERTOMB, 		
		ADDNPC,		
		ADDPLAYER,
		ADDPLAYERTOMB,
		ADDPORTAL,
		ADDTREASURE,
		ANSWER,        
		ATTACKMONSTER,
		BENEFIT,
		BLOCK,
		CHANGEQUESTSTATUS,
		CLEARDLGHIST,
		CLEARMSGLOG,
		DELMONSTERTOMB, 
		DELPLAYERTOMB, 
		ENTERGAMEBOARD,          
		FRAME,
		GIVEMARK,
		GIVEQUESTION,
		HP,
		ID,
		INITIALUSER,
		JOINGROUP,
		KILL,
		LEAVEGAMEBOARD,
		LEVEL,
		LEVELREQUEST,
		MOVEFORWARD,
		NPCTALK,
		OPENTREASURE, 
		PASSAWAY,
		PASSAWAYREQUEST,   
		PORTAL,
		QUESTINFORMATION,  //For Help and Clue
		QUESTINTRODUCTION,
		QUESTQUESTION,
		REMOVEFOOD, 
		REMOVEMONSTER, 
		REMOVEPLAYER, 
		SCORE,
		START,
		STOP,
		TALK, 
		TURN
	}

	public static enum Direction {
		NORTH, EAST, SOUTH, WEST
	}

	public static int[][] DirectionAdds =
		new int[][] { { 0, -1 }, { 1, 0 }, { 0, 1 }, { -1, 0 } };

	private static final boolean TRACEOPS = true;

	// this class is just statics so we prevent instantiation
	private Commands() {

	}

	public static float[] step(float posX, float posY, Direction direction) {
		int[] adds = DirectionAdds[direction.ordinal()];
		return new float[] {posX + adds[0], posY + adds[1]};
	}
	
	public static byte[] blockCommand(int id, float x, float y){
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.BLOCK.ordinal());
		buff.putInt(id);
		buff.putFloat(x);
		buff.putFloat(y);                 
		return bytes; 
	}    

	public static byte[] clearDialogueHistoryCommand(int id)  {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.CLEARDLGHIST.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] clearMsgLogCommand(int id)  {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.CLEARMSGLOG.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] passAwayCommand(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int money)  {
		byte[] bytes = new byte[4 * 10];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.PASSAWAY.ordinal());
		buff.putInt(id);
		buff.putInt(gameLevel);        
		buff.putInt(hp); 
		buff.putInt(maxHp); 
		buff.putInt(mp);
		buff.putInt(maxMp); 
		buff.putInt(exp);         
		buff.putInt(maxExp); 
		buff.putInt(money);         
		return bytes;
	}

	public static byte[] levelCommand(int id, int gameLevel, int hp, int maxHp, int mp, int maxMp, int exp, int maxExp, int power)  {
		byte[] bytes = new byte[4 * 10];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.LEVEL.ordinal());
		buff.putInt(id);
		buff.putInt(gameLevel);        
		buff.putInt(hp); 
		buff.putInt(maxHp);         
		buff.putInt(mp); 
		buff.putInt(maxMp);         
		buff.putInt(exp);
		buff.putInt(maxExp);         
		buff.putInt(power);         
		return bytes;
	}

	public static byte[] requestChangeLevelCommand(int id)  {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.LEVELREQUEST.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] requestPassAwayCommand(int id)  {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.PASSAWAYREQUEST.ordinal());
		buff.putInt(id);
		return bytes;
	}    

	public static byte[] attackMonsterCommand(int id, int placeId, int monsterId) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ATTACKMONSTER.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);        
		buff.putInt(monsterId);                
		return bytes;
	}
	public static byte[] paymentCommand(int id, int treasureId, String name,
			int hp, int mp, int myExp, int money,
			String armor, String tool, String weapon
	) {
		byte[] bytes = new byte[4 * 11 + name.length()+ armor.length() + tool.length() + weapon.length() ];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.BENEFIT.ordinal());
		buff.putInt(id);
		buff.putInt(treasureId);        
		buff.putInt(name.length());
		buff.put(name.getBytes());           
		buff.putInt(hp);
		buff.putInt(mp);
		buff.putInt(myExp);
		buff.putInt(money);
		buff.putInt(armor.length());
		buff.put(armor.getBytes());
		buff.putInt(tool.length());
		buff.put(tool.getBytes());
		buff.putInt(weapon.length());
		buff.put(weapon.getBytes());
		return bytes;
	}    

	public static byte[] talkCommand(int id, String name, String speaking) {
		byte[] bytes = new byte[4 * 4 + name.length() + speaking.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.TALK.ordinal());
		buff.putInt(id);     
		buff.putInt(name.length());
		buff.put(name.getBytes());                
		buff.putInt(speaking.length());
		buff.put(speaking.getBytes());        
		return bytes;
	}

	public static byte[] talkToNPCCommand(int npcId, int replyFlag, String dialogue) {
		byte[] bytes = new byte[4 * 4 + dialogue.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.NPCTALK.ordinal());
		buff.putInt(npcId);  
		buff.putInt(replyFlag);
		buff.putInt(dialogue.length());
		buff.put(dialogue.getBytes());
		return bytes;
	}

	public static byte[] setIDCommand(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ID.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] moveForwardCommand(int id, int placeId, float tx, float ty, Direction direction) {
		byte[] bytes = new byte[4 * 6];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.MOVEFORWARD.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);
		buff.putFloat(tx);
		buff.putFloat(ty);          
		buff.putInt(direction.ordinal());
		return bytes;
	}    
	
	public static byte[] portalCommand(int id, int placeId, String portalName) {
		byte[] bytes = new byte[4 * 4 + portalName.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.PORTAL.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);
		buff.putInt(portalName.length());
		buff.put(portalName.getBytes());
		return bytes;
	}

	public static byte[] addPortalCommand(int id, int placeId, String portalName, float x, float y) {
		byte[] bytes = new byte[4 * 6 + portalName.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDPORTAL.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);
		buff.putInt(portalName.length());
		buff.put(portalName.getBytes());
		buff.putFloat(x);
		buff.putFloat(y);		
		return bytes;
	}  
	
	public static byte[] answerQuizCommand(int id, int placeId, int treasureId, int topicId, String answer) {
		byte[] bytes = new byte[4 * 6 + answer.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ANSWER.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);		
		buff.putInt(treasureId);
		buff.putInt(topicId);		
		buff.putInt(answer.length());
		buff.put(answer.getBytes());
		return bytes;
	}    

	public static byte[] removeFoodCommand(int id, float posX, float posY) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.REMOVEFOOD.ordinal());
		buff.putInt(id);     
		buff.putFloat(posX);
		buff.putFloat(posY);
		return bytes;
	}

	public static byte[] hpCommand(int id, int hp) {
		byte[] bytes = new byte[4 * 3];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.HP.ordinal());
		buff.putInt(id);
		buff.putInt(hp);
		return bytes;
	}    

	public static byte[] frameCommand(int id, int frameNum) {
		byte[] bytes = new byte[4 * 3];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.FRAME.ordinal());
		buff.putInt(id);
		buff.putInt(frameNum);
		return bytes;
	}

	public static byte[] turnCommand(int id, int placeId, Direction direction) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.TURN.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);
		buff.putInt(direction.ordinal());
		return bytes;
	}

	public static byte[] addPlayerCommand(int id, String name, int level, int outfitCode,
			float x, float y, int facing) {
		byte[] bytes = new byte[4 * 8 + name.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDPLAYER.ordinal());
		buff.putInt(id);
		buff.putInt(name.length());
		buff.put(name.getBytes());    
		buff.putInt(level);       
		buff.putInt(outfitCode);            
		buff.putFloat(x);
		buff.putFloat(y);
		buff.putInt(facing);
		return bytes;
	}

	public static byte[] addTreasureCommand(int treasureId, int questID, float x, float y) {
		byte[] bytes = new byte[4 * 5 ];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDTREASURE.ordinal());
		buff.putInt(treasureId);
		buff.putInt(questID);
		buff.putFloat(x);
		buff.putFloat(y);      
		return bytes;
	}

	public static byte[] addNPCCommand(int npcId, String npcName, int level, int hp, int mp,
			String imgRef, float x, float y, int facing) {
		byte[] bytes = new byte[4 * 10 + npcName.length() + imgRef.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDNPC.ordinal());
		buff.putInt(npcId);
		buff.putInt(npcName.length());
		buff.put(npcName.getBytes());
		buff.putInt(level);
		buff.putInt(hp);     
		buff.putInt(mp);          
		buff.putInt(imgRef.length());
		buff.put(imgRef.getBytes());           
		buff.putFloat(x);
		buff.putFloat(y);      
		buff.putInt(facing);
		return bytes;
	}

	public static byte[] addFoodCommand(int id, String name, float x, float y, int attractionPoint, String imgRef) {
		byte[] bytes = new byte[4 * 7 + name.length() + imgRef.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDFOOD.ordinal());     
		buff.putInt(id);   
		buff.putInt(name.length());
		buff.put(name.getBytes());             
		buff.putFloat(x);
		buff.putFloat(y);
		buff.putInt(attractionPoint);        
		buff.putInt(imgRef.length());
		buff.put(imgRef.getBytes());        
		return bytes;
	}

	public static byte[] addMonsterCommand(int id, String name, float x, float y, int gameLevel, 
			int hp, int mp, int power,
			int facing, String imgRef) {
		byte[] bytes = new byte[4 * 11 + name.length() + imgRef.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDMONSTER.ordinal());     
		buff.putInt(id);
		buff.putInt(name.length());
		buff.put(name.getBytes());             
		buff.putFloat(x);
		buff.putFloat(y);
		buff.putInt(gameLevel);
		buff.putInt(hp);
		buff.putInt(mp);
		buff.putInt(power);    
		buff.putInt(facing);
		buff.putInt(imgRef.length());
		buff.put(imgRef.getBytes());        
		return bytes;
	}

	public static byte[] addMonsterTomb(int id, float x, float y) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDMONSTERTOMB.ordinal());
		buff.putInt(id);
		buff.putFloat(x);
		buff.putFloat(y);
		return bytes;
	}

	public static byte[] addPlayerTomb(int id, float x, float y) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ADDPLAYERTOMB.ordinal());
		buff.putInt(id);
		buff.putFloat(x);
		buff.putFloat(y);
		return bytes;
	}

	public static byte[] changeQuestStatusCommand(int id, int questId, int status) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.CHANGEQUESTSTATUS.ordinal());
		buff.putInt(id);            
		buff.putInt(questId);       
		buff.putInt(status);       
		return bytes;                    
	}    

	public static byte[] questIntroductionCommand(int id, int npcId, String npcName, int questID, String questName, String introduction) {
		byte[] bytes = new byte[4 * 7 + npcName.length() + questName.length() + introduction.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.QUESTINTRODUCTION.ordinal());
		buff.putInt(id);
		buff.putInt(npcId);
		buff.putInt(npcName.length());
		buff.put(npcName.getBytes());
		buff.putInt(questID);
		buff.putInt(questName.length());
		buff.put(questName.getBytes());
		buff.putInt(introduction.length());
		buff.put(introduction.getBytes());
		return bytes;                    
	}

	public static byte[] questQuestionCommand(int id, int npcId, String npcName, int questID, String questName, String question) {
		byte[] bytes = new byte[4 * 7 + npcName.length() + questName.length() + question.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.QUESTQUESTION.ordinal());
		buff.putInt(id);
		buff.putInt(npcId);
		buff.putInt(npcName.length());
		buff.put(npcName.getBytes());
		buff.putInt(questID);
		buff.putInt(questName.length());
		buff.put(questName.getBytes());
		buff.putInt(question.length());
		buff.put(question.getBytes());
		return bytes;
	}

	public static byte[] questInformationCommand(int id, int npcId, String npcName, int questID, String questName, String infor, int iType) {
		byte[] bytes = new byte[4 * 8 + npcName.length() + questName.length() + infor.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.QUESTINFORMATION.ordinal());
		buff.putInt(id);
		buff.putInt(npcId);
		buff.putInt(npcName.length());
		buff.put(npcName.getBytes());
		buff.putInt(questID);
		buff.putInt(questName.length());
		buff.put(questName.getBytes());
		buff.putInt(infor.length());
		buff.put(infor.getBytes());
		buff.putInt(iType);
		return bytes;
	}

	public static byte[] giveQuestionCommand(int id, int treasureId, int questID) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.GIVEQUESTION.ordinal());
		buff.putInt(id);   
		buff.putInt(treasureId);
		buff.putInt(questID);
		return bytes;
	}

	public static byte[] giveMarkCommand(int id, int questID, int mark) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.GIVEMARK.ordinal());
		buff.putInt(id);
		buff.putInt(questID);
		buff.putInt(mark);
		return bytes;
	}

	public static byte[] scoreCommand(int id, int score) {
		byte[] bytes = new byte[4 * 3];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.SCORE.ordinal());
		buff.putInt(id);
		buff.putInt(score);
		return bytes;
	}

	public static byte[] enterGameBoardCommand(int id, int placeId, String mapFileRef) { 
		byte[] bytes = new byte[4 * 4 + mapFileRef.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.ENTERGAMEBOARD.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);    
		buff.putInt(mapFileRef.length());
		buff.put(mapFileRef.getBytes());   
		return bytes;
	}   

	public static byte[] initializePlayerCommand (
			int id, int level, int hp, int maxHp, int mp, int maxMp, 
			int myExp, int maxExp, int money,
			String[] dialogues, int outfitCode)
	{       
		int charcount =0;
		for(int i=0;i<dialogues.length;i++){
			charcount += 4;
			charcount += dialogues[i].length();
		}        
		byte[] bytes = new byte[4 * 12 + charcount ];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.INITIALUSER.ordinal());
		buff.putInt(id);
		buff.putInt(level);
		buff.putInt(hp);      
		buff.putInt(maxHp);            
		buff.putInt(mp);        
		buff.putInt(maxMp);          
		buff.putInt(myExp);      
		buff.putInt(maxExp);         
		buff.putInt(money);        
		buff.putInt(dialogues.length);
		for(int i=0;i<dialogues.length;i++){
			buff.putInt(dialogues[i].length());
			buff.put(dialogues[i].getBytes());
		}
		buff.putInt(outfitCode);
		return bytes;
	}

	public static byte[] openTreasureCommand(int id, int placeId, int treasureId, int topicId) {
		byte[] bytes = new byte[4 * 5];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.OPENTREASURE.ordinal());
		buff.putInt(id);
		buff.putInt(placeId);       		
		buff.putInt(treasureId);        
		buff.putInt(topicId);
		return bytes;        
	}

	public static byte[] killCommand(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.KILL.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] removePlayerCommand(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.REMOVEPLAYER.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] removeMonster(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.REMOVEMONSTER.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] removeMonsterTombCommand(int id, float x, float y) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.DELMONSTERTOMB.ordinal());
		buff.putInt(id);
		buff.putFloat(x);
		buff.putFloat(y);
		return bytes;
	}

	public static byte[] removePlayerTombCommand(int id, float x, float y) {
		byte[] bytes = new byte[4 * 4];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.DELPLAYERTOMB.ordinal());
		buff.putInt(id);
		buff.putFloat(x);
		buff.putFloat(y);
		return bytes;
	}

	public static byte[] joinGroupCommand(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.JOINGROUP.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] leaveGameBoardCommand(int id, String placeName) {
		byte[] bytes = new byte[4 * 3 + placeName.length()];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.LEAVEGAMEBOARD.ordinal());
		buff.putInt(id);
		buff.putInt(placeName.length());
		buff.put(placeName.getBytes());  		
		return bytes;
	}

	public static byte[] startCommand(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.START.ordinal());
		buff.putInt(id);
		return bytes;
	}

	public static byte[] stopCommand(int id) {
		byte[] bytes = new byte[4 * 2];
		ByteBuffer buff = ByteBuffer.wrap(bytes);
		buff.putInt(OpCode.STOP.ordinal());
		buff.putInt(id);
		return bytes;
	}   

	public static void parseCommandBuffer(ByteBuffer buff, CommandListener listener)  {
		OpCode opcode = OpCode.values()[buff.getInt()];
		int id = buff.getInt();
		
		if (!opcode.name().equals("FRAME")&& TRACEOPS) {
			logger.log(Level.INFO, "ID [{0}} Received op: {1}", new Object[] {id, opcode.name()});
		}
		
		switch (opcode) {     
		case ADDNPC:
			int strlen = buff.getInt();
			byte[] strbytes = new byte[strlen];
			buff.get(strbytes);
			String name = new String(strbytes); 
			int level = buff.getInt();
			int hp = buff.getInt();
			int mp = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String imgRef = new String(strbytes);                  
			float x = buff.getFloat();
			float y = buff.getFloat();
			int facing = buff.getInt();
			listener.commandAddNPC(id, name, level, hp, mp, imgRef, x, y, facing);
			break;
		case ADDTREASURE:
			int questID = buff.getInt();
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandAddTreasure(id, questID, x, y);    
			break;
		case ANSWER:
			int placeId = buff.getInt();			
			int treasureId = buff.getInt();
			int topicId = buff.getInt();			
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String answer = new String(strbytes);
			listener.commandAnswerQuiz(id, placeId, treasureId, topicId, answer);
			break;                        
		case ATTACKMONSTER:
			placeId = buff.getInt();
			int monsterId = buff.getInt();
			listener.commandAttackMonster(id, placeId, monsterId);
			break;

		case CLEARDLGHIST:
			listener.commandClearDialogueHistory(id);    
			break;
		case  CLEARMSGLOG:
			listener.commandClearMsgLog(id);
			break;
		case ADDFOOD:
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			name = new String(strbytes);                    
			x = buff.getFloat();
			y = buff.getFloat();
			int attractionPoint = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			imgRef = new String(strbytes);                              
			listener.commandAddFood(id, name, x, y, attractionPoint, imgRef);
			break;             
		case BENEFIT:
			treasureId = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			name = new String(strbytes);               
			hp = buff.getInt();
			mp = buff.getInt();      
			int myExp = buff.getInt();
			int money = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String armor = new String(strbytes);
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String tool = new String(strbytes);
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String weapon = new String(strbytes);

			listener.commandPayment(id, treasureId, name, hp, mp, myExp, money, armor, tool, weapon);
			break;
		case BLOCK:
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandBlock(id, x, y);
			break;        
		case ENTERGAMEBOARD:
			placeId = buff.getInt(); 
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String mapFileRef = new String(strbytes);
			listener.commandEnterGameBoard(id, placeId, mapFileRef);
			break;                
		case DELMONSTERTOMB: 
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandRemoveMonsterTomb(id, x, y);
			break; 
		case DELPLAYERTOMB: 
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandRemovePlayerTomb(id, x, y);
			break;            
		case FRAME:
			int frameCount = buff.getInt();
			listener.commandFrame(id, frameCount);
			break;        
		case HP:
			hp = buff.getInt();
			listener.commandHp(id, hp);            
			break;
		case ID:
			listener.commandSetID(id);
			break;   
		case INITIALUSER:
			level = buff.getInt();
			hp = buff.getInt();     
			int maxHp = buff.getInt();                
			mp = buff.getInt(); 
			int maxMp = buff.getInt();  
			myExp = buff.getInt(); 
			int maxExp = buff.getInt();  
			money = buff.getInt();   
			int dialoguesCount = buff.getInt();
			String[] dialogues = new String[dialoguesCount];
			for(int i=0;i<dialoguesCount;i++){
				strlen = buff.getInt();
				strbytes = new byte[strlen];
				buff.get(strbytes);
				dialogues[i] = new String(strbytes);
			}          
			int outfitCode = buff.getInt();

			listener.commandInitializePlayer(id, level, hp, maxHp, mp, 
					maxMp, myExp, maxExp, money, dialogues, outfitCode 
			);
			break;
		case JOINGROUP:
			listener.commandJoinGroup(id);
			break;                
		case KILL:
			listener.commandKill(id);
			break;     
		case LEAVEGAMEBOARD:
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String placeName = new String(strbytes);			
			listener.commandLeaveGameBoard(id, placeName);
			break;
		case LEVEL:
			int gameLevel = buff.getInt();
			hp = buff.getInt();
			maxHp = buff.getInt();                
			mp = buff.getInt();
			maxMp = buff.getInt();                                
			int exp = buff.getInt();
			maxExp = buff.getInt();                                
			int power = buff.getInt();
			listener.commandLevel(id, gameLevel, hp, maxHp, mp, maxMp, exp, maxExp, power);    
			break;
		case LEVELREQUEST:
			listener.commandRequestLevel(id);    
			break;                
		case ADDMONSTER:
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			name = new String(strbytes);                    
			x = buff.getFloat();
			y = buff.getFloat();
			gameLevel = buff.getInt();
			hp = buff.getInt();
			mp = buff.getInt();
			power = buff.getInt();                
			Direction direction = Direction.values()[buff.getInt()];        
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			imgRef = new String(strbytes);                              
			listener.commandAddMonster(id, name, x, y, gameLevel, hp, mp, power, direction, imgRef);
			break;             
		case ADDMONSTERTOMB: 
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandAddMonsterTomb(id, x, y);
			break;                          
		case MOVEFORWARD:
			placeId = buff.getInt();
			float tx = buff.getFloat();
			float ty = buff.getFloat();                	
			direction = Direction.values()[buff.getInt()];
			listener.commandMoveForward(id, placeId, tx, ty, direction);
			break;       
		case NPCTALK:                     
			int replyFlag = buff.getInt();
			strlen = buff.getInt();     
			strbytes = new byte[strlen];
			buff.get(strbytes);                
			String dialogue = new String(strbytes);
			listener.commandTalktoNPC(id, replyFlag, dialogue);
			break;                                     
		case PASSAWAY:
			gameLevel = buff.getInt();
			hp = buff.getInt();
			maxHp = buff.getInt();                
			mp = buff.getInt();
			maxMp = buff.getInt();
			exp = buff.getInt();
			maxExp = buff.getInt();
			money = buff.getInt();
			listener.commandPassAway(id, gameLevel, hp, maxHp, mp, maxMp, exp, maxExp, money);    
			break;         
		case PASSAWAYREQUEST:
			listener.commandRequestPassAway(id);    
			break;                                 
		case ADDPLAYER:
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			name = new String(strbytes); 
			level = buff.getInt();      
			outfitCode = buff.getInt();                   
			x = buff.getFloat();
			y = buff.getFloat();           
			facing = buff.getInt();
			listener.commandAddPlayer(id, name, level, outfitCode, x, y, facing);
			break;
		case ADDPLAYERTOMB:
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandAddPlayerTomb(id, x, y);
			break;        			
		case PORTAL:
			placeId = buff.getInt();    
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String portalName = new String(strbytes);			
			listener.commandPortal(id, placeId, portalName);
			break;     			
		case ADDPORTAL:
			placeId = buff.getInt();    
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			portalName = new String(strbytes);
			x = buff.getFloat();
			y = buff.getFloat();    			
			listener.commandAddPortal(id, placeId, portalName, x, y);
			break; 					
		case CHANGEQUESTSTATUS:
			int 	questId = buff.getInt();
			int  status = buff.getInt();
			listener.commandChangeQuestStatus(id, questId, status);
			break;                            
		case QUESTINTRODUCTION:
			int npcId = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String npcName = new String(strbytes);
			questID = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String questName = new String(strbytes);
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String introduction = new String(strbytes);
			listener.commandQuestIntroduction(id, npcId, npcName, questID, questName, introduction);
			break;
		case QUESTQUESTION:
			npcId = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			npcName = new String(strbytes);
			questID = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			questName = new String(strbytes);
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String question = new String(strbytes);
			listener.commandQuestQuestion(id, npcId, npcName, questID, questName, question);
			break;
		case QUESTINFORMATION:
			npcId = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			npcName = new String(strbytes);
			questID = buff.getInt();
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			questName = new String(strbytes);
			strlen = buff.getInt();
			strbytes = new byte[strlen];
			buff.get(strbytes);
			String information = new String(strbytes);
			int iType = buff.getInt();
			listener.commandQuestInformation(id, npcId, npcName, questID, questName, information, iType);
			break;
		case GIVEQUESTION:
			treasureId = buff.getInt();
			questID = buff.getInt();
			listener.commandGiveQuestion(id, treasureId, questID);
			break;
		case GIVEMARK:
			questID = buff.getInt();
			int mark = buff.getInt();
			listener.commandGiveMark(id, questID, mark);
			break;
		case REMOVEFOOD:          
			x = buff.getFloat();
			y = buff.getFloat();
			listener.commandRemoveFood(id, x, y);
			break;       
		case REMOVEMONSTER:
			listener.commandRemoveMonster(id);
			break;    
		case REMOVEPLAYER:
			listener.commandRemovePlayer(id);
			break;                      
		case SCORE:
			int score = buff.getInt();
			listener.commandScore(id, score);
			break;        
		case START:
			listener.commandStart(id);
			break;
		case STOP:
			listener.commandStop(id);
			break;                              
		case TALK:
			strlen = buff.getInt();     
			strbytes = new byte[strlen];
			buff.get(strbytes);                
			name = new String(strbytes);                             
			strlen = buff.getInt();     
			strbytes = new byte[strlen];
			buff.get(strbytes);                
			String speaking = new String(strbytes);
			listener.commandTalk(id, name, speaking);
			break;          
		case OPENTREASURE:
			placeId = buff.getInt();   			
			treasureId = buff.getInt();
			topicId = buff.getInt();                  
			listener.commandOpenTreasure(id, treasureId, placeId, topicId);
			break;            
		case TURN:
			placeId = buff.getInt();                       
			direction = Direction.values()[buff.getInt()];
			listener.commandTurn(id, placeId, direction);
			break;            
		default:
			if (TRACEOPS) {
				System.out.println("Unknown op: " + opcode);
			}

		}
	}
}