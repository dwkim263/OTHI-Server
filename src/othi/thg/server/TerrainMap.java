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

import java.io.Serializable;
import java.util.logging.Logger;

import othi.thg.server.THGServerDefault.Terrain;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.ManagedObject;


/**
 * game map
 * @author Dong Won Kim
 */
public class TerrainMap implements ManagedObject, Serializable{

	private static final long serialVersionUID = 1L;     

	private static final Logger logger = Logger.getLogger(TerrainMap.class.getName());  

	private int id;   //Place id

	private int width;

	private int height;                   

	private Terrain[][][] terrain; 

	private boolean[][] blocked;

	public TerrainMap(int id, int width, int height, Terrain[][][] terrain) {
		this.id = id;
		this.width = width;
		this.height = height;
		this.terrain = terrain;
		setBlocked();
	}                     

	public void setBlocked() {
		AppContext.getDataManager().markForUpdate(this);    	
		if (blocked == null) {
			blocked = new boolean[width][height];
		}

		for(int j=0;j<height;j++) {
			for(int i=0;i<width;i++) {
				if ((terrain[0][i][j] == Terrain.BLOCK) || (terrain[0][i][j] == Terrain.TREASURE)){
					blocked[i][j] = true;                    
				} else {
					blocked[i][j] = false;                     
				}   
			}
		}             
	}   

	public boolean[][] getBlocked() {
		if (blocked == null) return null;
		else return blocked;
	}                

	public int getId() {
		return id;
	}

	public int getWidth(){
		return width;
	}

	public int getHeight(){
		return height;
	}        

	public Terrain[][] getTerrainWithBlock() {
		return terrain[0];
	}

	public Terrain[][] getTerrainWithProtection() {
		return terrain[1];
	}    

	public Terrain getTerrainAt(int terrainLayer, float posX, float posY) {
		return terrain[terrainLayer][(int)posX][(int)posY];
	}        

	public void setTerrainAt(Terrain terrain, float posX, float posY) {
		AppContext.getDataManager().markForUpdate(this);
		this.terrain[0][(int)posX][(int)posY] = terrain;
	}   

	public boolean isLegalCell(float i, float j) {
		if (i < 0)
			return false;
		if (i > width - 1)
			return false;
		if (j < 0)
			return false;
		if (j > height - 1)
			return false;
		return true;
	}    

	public boolean isBlocked(int x, int y) {
		if ((x < 0) || (x >= width)) {
			return true;
		}

		if ((y < 0) || (y >= height)) {
			return true;
		}

		return (terrain[0][x][y] == Terrain.BLOCK) || (terrain[0][x][y] == Terrain.TREASURE);   
	}    

	public boolean isProtected (int x, int y) {
		if ((x < 0) || (x >= width)) {
			return true;
		}

		if ((y < 0) || (y >= height)) {
			return true;
		}

		return terrain[1][x][y] == Terrain.PROTECT;   
	}        
}
