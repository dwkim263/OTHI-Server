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

package othi.thg.server.agents.npcs;

import java.util.LinkedList;
import java.util.List;

import othi.thg.server.pathFinding.*;
/**
 *
 * @author Dong Won Kim
 */
public class NormalNpc extends Speaker {
    /**
	 * 
	 */
	private static final long serialVersionUID = 7999054499471214952L;

	public NormalNpc(int id, String name, String placeName){
        super(id, name, placeName);            
    }

    public void createPath(int width, int height, boolean[][] blocked, List<String[][]> definedPaths, int maxSearch) {
        DJKPathFinder pathfinder = new DJKPathFinder(width, height, blocked);
        List<Path> paths = new LinkedList<Path>();        
        for (String[][] definedPath:definedPaths) {
            pathfinder.reset();
            //from the well to a barrel
            int sx = Integer.parseInt(definedPath[0][0]);
            int sy = Integer.parseInt(definedPath[0][1]);            
            int dx = Integer.parseInt(definedPath[1][0]);            
            int dy = Integer.parseInt(definedPath[1][1]);            

            paths.add(pathfinder.findPath(sx, sy, dx, dy, maxSearch));   
        }                
        setPaths(paths);    
    }
}
