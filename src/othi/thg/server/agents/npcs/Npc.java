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
 * Human.java
 * 
 * Created on 2007. 8. 9, 9:37:42
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package othi.thg.server.agents.npcs;

/**
 *
 * @author Dong Won Kim
 */
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import othi.thg.server.NonPlayable;
import othi.thg.server.agents.RegularMovable;
import othi.thg.server.pathFinding.Path;
import com.sun.sgs.app.AppContext;

public class Npc extends NonPlayable<NPCTask> implements RegularMovable {
	
    private static final long serialVersionUID = 1L;	

    private static final Logger logger = Logger.getLogger(Npc.class.getName());

    protected String nickName;

    public Npc(int id, String name, String placeName) {
        this.id = id;
        this.name = name;
        this.placeName = placeName;                        
    }

    public String getNickName() {
        return nickName;           
    }

    public void setNickName(String nickName) {
    	AppContext.getDataManager().markForUpdate(this);
        this.nickName = nickName;
    }
           
    @Override
    public void createPath(int width, int height, boolean[][] blocked, int maxSearch){
          setPaths(null);
    }    
             
    @Override
    public void setPaths(List<Path> paths){        
        Path allPaths = new Path();        
        if (paths == null) {
            allPaths.appendStep((int) getPosX(), (int) getPosY());
        } else {
            for (Path aPath : paths) {
                if (aPath != null) {
                    for (int i = 0; i < aPath.getSize(); ++i) {
                        allPaths.appendStep(aPath.getX(i), aPath.getY(i));
                    }
                }
            }   
        }    
        setPath(allPaths);
    }    
    
    @Override
    public void tickMove() {   
//        logger.info("NPC " + name + " is moving around " + placeName + "." );  
        
        Path aPath = getPath();
        if (aPath != null && aPath.getSize() > 1) {         
//logger.log(Level.INFO, name + " NPC Action => tickMove, aPath size: " + aPath.getSize() );
            int nextPosX = aPath.getX(getPathIndex());
            int nextPosY = aPath.getY(getPathIndex());

            walk(nextPosX, nextPosY);
            
            if (getPathIndex() >= aPath.getSize()) setPathIndex(0);
        }
    }                   
    
	public void remove() {
		removeNPCTask();
	}

	private void removeNPCTask() {
		getManagedTHGTask().getTaskHandle().cancel();
		AppContext.getDataManager().removeObject(this);
	}		
}
