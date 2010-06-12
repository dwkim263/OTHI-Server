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
 * Answer.java
 * 
 * Created on Aug 14, 2007, 10:51:47 AM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package othi.thg.server.agents;

/**
 *
 * @author Dong Won Kim
 */
import com.sun.sgs.app.AppContext;

import othi.thg.server.ManagedTHGEntity;

public abstract class Dialogue extends ManagedTHGEntity  {

    /**
	 * 
	 */
	private static final long serialVersionUID = -563155970956443167L;
	public static final long MAXIMUM_WAITING_TIME_FOR_REPLY = 1000 * 60 * 10; //1 minutes
    public static final String NEWLINE = "\n";
    
    public static enum DialogueType {
        HELLO, BYE, HELP, QUEST
    }

    public static enum Hello {
        Hello, Hi
    };

    public static enum Bye {
        Bye, Goodbye, No
    };

    public static enum Help {
        Help, Yes, Ok
    };

    protected DialogueType dialogueType = DialogueType.HELLO;

    protected String questioner;
    protected String query;
    protected String reply;   
    protected long repliedTime;
    protected boolean endConversation = false;
    protected boolean queryReceived = false;
    protected boolean replyRequested = true;

    public void setReplyRequested(boolean replyRequested) {
    	AppContext.getDataManager().markForUpdate(this);
        this.replyRequested = replyRequested;
    }

    public boolean isReplyRequested() {
        return replyRequested;
    }
   
    public void setQuestioner(String questioner){
    	AppContext.getDataManager().markForUpdate(this);    	
        this.questioner = questioner;
    }
            

    public DialogueType getDialogueType(){
        return dialogueType;
    }
    
    public void setDialogueType(DialogueType type) {
    	AppContext.getDataManager().markForUpdate(this);    	
        dialogueType = type;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
    	AppContext.getDataManager().markForUpdate(this);    	
        this.query = query;
    }

    public void  setReply(String reply){
    	AppContext.getDataManager().markForUpdate(this);    	
        this.reply = reply;  
        repliedTime = System.currentTimeMillis();
    }       
    
    public String getReply(){
        return reply;
    }   
    
    public String getQuestioner(){
        return questioner;
    }   

    //It will randomly choose.
    public Hello getHelloQuery(){
        return Hello.Hello;
    }

    //It will randomly choose them excluding "No".
    public Bye getByeQuery()
    {
        return Bye.Bye;
    }

    public Help getHelpQuery()
    {
        return Help.Help;
    }

    public boolean isQueryReceived() {
        return queryReceived;
    }

    public void setQueryReceived(boolean queryReceived) {
    	AppContext.getDataManager().markForUpdate(this);    	
        this.queryReceived = queryReceived;
    }

    public boolean isByeQuery(String message) {
        if (message == null) return false;

        for ( Bye goodBye: Bye.values()) {
            if (message.contains(goodBye.toString())) return true;
        }
        return false;
    }

    public boolean isByeQuery() {
        if (query == null) return false;

        for ( Bye goodBye: Bye.values()) {
            if (query.contains(goodBye.toString())) return true;
        }
        return false;
    }

    public boolean isHelloQuery(String message) {
        if (message == null) return false;

        for ( Hello greeting : Hello.values()) {
            if (message.contains(greeting.toString())) return true;
        }
        return false;
    }

    public boolean isHelloQuery() {
        if (query == null) return false;

        for ( Hello greeting : Hello.values()) {
            if (query.contains(greeting.toString())) return true;
        }
        return false;
    }

    public boolean isHelpQuery(String message) {
        if (message == null) return false;

        for ( Help help : Help.values()) {
            if (message.contains(help.toString())) return true;
        }
        return false;
    }

    public boolean isHelpQuery() {
        if (query == null) return false;

        for ( Help help : Help.values()) {
            if (query.contains(help.toString())) return true;
        }
        return false;
    }

    public void setRepliedTime(long enabledTime) {
    	AppContext.getDataManager().markForUpdate(this);    	
        this.repliedTime = enabledTime;
    }
    
    public void setRepliedTime(){
    	AppContext.getDataManager().markForUpdate(this);    	
        repliedTime = System.currentTimeMillis();
    }
    
    public long getRepliedTime(){
        return repliedTime;
    }     
    
    public String refineReply(String reply){
        byte[] bytes = reply.trim().getBytes();
        String refinedReply = new String();
        boolean wasSpace = false;
        for (int i=0; i< bytes.length; ++i) {
            char ch = (char) bytes[i];
            if (Character.isLetterOrDigit(ch) || ch == '#') {
                refinedReply = refinedReply + ch;
                wasSpace = false;
            } else if (Character.isSpaceChar(ch) && !wasSpace ) {
                refinedReply = refinedReply + ch;
                wasSpace = true;
            } 
        }
        return refinedReply.toLowerCase();
    }

    public boolean isEndConversation() {
        return endConversation;
    }

    public void setEndConversation(boolean endConversation) {
    	AppContext.getDataManager().markForUpdate(this);    	
        this.endConversation = endConversation;
    }
}
