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

package othi.thg.server.pathFinding;

import java.util.Arrays;
import java.util.logging.Logger;
import java.io.Serializable;

import othi.thg.server.THGServerDefault.Terrain;
/**
 * A simple interface for testing path finding. This allows you
 * to watch the nodes being searched by the finder
 * 
 * @author Kevin Glass
 * @author Dong Won Kim
 */
public class DJKPathFinder implements PathFinder, Serializable {

	private static final long serialVersionUID = -3694637378736410999L;

	private static final Logger logger = Logger.getLogger(DJKPathFinder.class.getName());

	private int bestPath = 1000;

	private boolean[][] blocked = null;

	private int mapWidth;

	private int mapHeight;

	/** The tileset describing the tile properties on the map */
	private int[] distance;
	/** The maximum search depth reached before giving up */
	private int maxsearch;
	/** The starting x coordinate */
	private int sx;
	/** The starting y coordinate */
	private int sy;
	/** The distance from the destination of the best path found so far */

	private Terrain[][] terrain;

	/**
	 * Create a new DJK based path finder
	 * 
	 * @param map The map being searched
	 * @param set The set describing the tiles on the map
	 */
	public DJKPathFinder(int width, int height, boolean[][] blocked) {
		mapWidth = width;
		mapHeight = height;
		this.blocked = blocked;
		distance = new int[mapWidth*mapHeight];      
	}

	public DJKPathFinder(int width, int height) {
		mapWidth = width;
		mapHeight = height;
		distance = new int[mapWidth*mapHeight];      
	}        

	public DJKPathFinder(Terrain[][] terrain) {
		mapWidth = terrain.length;
		mapHeight = terrain[0].length;
		distance = new int[mapWidth*mapHeight];
		this.terrain = terrain;               
	}

	public void setTerrain(Terrain[][] terrain) {
		this.terrain = terrain;
	}        

	/**
	 * @see org.newdawn.util.map.PathFinder#reset()
	 */
	 @Override
	 public void reset() {
		 Arrays.fill(distance,0);
	 }

	 /**
	  * @see org.newdawn.util.map.PathFinder#getSearchData()
	  */
	 @Override
	 public int[] getSearchData() {
		 return distance;
	 }

	 /**
	  * @see org.newdawn.util.map.PathFinder#findPath(int, int, int, int, int)
	  */
	 @Override
	 public synchronized Path findPath(int sx,int sy,int dx,int dy,int maxsearch) {
		 int depth = 1;
		 this.sx = sx;
		 this.sy = sy;

		 bestPath = 100; //1000

		 this.maxsearch = maxsearch;

		 if (processNode(dx,dy,depth)) {
			 Path path = new Path();

			 findNextPoint(path,sx,sy,distance[sx+(sy*mapWidth)]-1);

			 //          printPath(path);

			 return path;
		 } else {
			 return null;
		 }
	 }

	 /** Print the area around the (x,y) useful for debugging */
	 private void printPath(Path path) {
		 int height = mapHeight;
		 int width = mapWidth;
		 int x = path.getX(0);
		 int y = path.getY(0);
		 int length = path.getSize();
		 int size = Math.max(Math.abs(x - path.getX(length-1)),
				 Math.abs(y - path.getY(length-1)));
		 size = size + 2;
		 for (int j = y - size; j < y + size; j++) {
			 for (int i = x - size; i < x + size; i++) {
				 if ((j >= 0) && (j < height) && (i >= 0) && (i < width)) {
					 boolean found = false;
					 int k = 0;
					 for (; k < length; ++k ) {
						 if (i == path.getX(k) && j==path.getY(k)) {
							 found = true;
							 break;
						 }
					 }
					 if (found) {
						 logger.info(Integer.toString(k));
						 found = false;
					 } else if (collides(i, j) == false) {
						 logger.info(".");
					 } else {
						 logger.info("X");
					 }
				 }
			 }
			 System.out.println();
		 }
	 }        

	 /**
	  * Find the next point on the discovered path by moving 
	  * along the distances. After finding the path by DJK we need
	  * to trace back along the distances working out how we got to the
	  * end.
	  * 
	  * @param path The path being built up
	  * @param x The current x position
	  * @param y The current y position
	  * @param d The distance we're searching for at the moment.
	  */
	 private void findNextPoint(Path path,int x,int y,int d) {
		 if (d < 0) {
			 return;
		 }
		 path.appendStep(x,y);

		 for (int xo=-1;xo<2;xo++) {
			 for (int yo=-1;yo<2;yo++) {
				 if ((xo != 0) || (yo != 0)) {
					 if ((xo == 0) || (yo == 0)) {
						 int sX = x+xo;
						 int sY = y+yo;

						 if (distance[sX+(sY*mapWidth)] == d) {
							 findNextPoint(path,sX,sY,distance[sX+(sY*mapWidth)]-1);
							 return;
						 }
					 }
				 }
			 }
		 }

		 for (int xo=-1;xo<2;xo++) {
			 for (int yo=-1;yo<2;yo++) {
				 if ((xo != 0) && (yo != 0)) {
					 int sX = x+xo;
					 int sY = y+yo;

					 if (distance[sX+(sY*mapWidth)] == d) {
						 findNextPoint(path,sX,sY,distance[sX+(sY*mapWidth)]-1);
						 return;
					 }
				 }
			 }
		 }
	 }

	 /**
	  * Traverse the specified graph node (in this case grid cell)
	  * 
	  * @param x The x location we're processing
	  * @param y The y location we're processing
	  * @param depth The depth of the search we're at
	  * @return True if we're reached the destination
	  */
	 private boolean processNode(int x,int y,int depth) {
		 if (depth >= maxsearch) {
			 return false;
		 }
		 if (depth >= bestPath) {
			 return false;
		 }

		 if ((x < 0) || (y < 0) || (x >= mapWidth) || (y >= mapHeight)) {
			 return false;
		 }

		 if (collides(x,y)) {
			 return false;
		 }

		 int val = distance[x+(y*mapWidth)];

		 if ((val != 0) && (val <= depth)) {
			 return false;
		 }

		 distance[x+(y*mapWidth)] = depth;

		 if ((x == sx) && (y == sy)) {
			 bestPath = depth;
			 return true;
		 }

		 boolean found = false;

		 for (int xo=-1;xo<2;xo++) {
			 for (int yo=-1;yo<2;yo++) {
				 if ((xo == 0) || (yo == 0)) {                            
					 //                          if ((xo != 0) || (yo != 0)) {
					 if (validMove(x,y,xo,yo)) {
						 found |= processNode(x+xo,y+yo,depth+1);
					 }
				 }
			 }
		 }

		 return found;
	 }

	 /**
	  * Check if the move specified is valid 
	  *  
	  * @param x The x position we're starting from
	  * @param y The y position we're starting from
	  * @param xo The x direction we're moving in
	  * @param yo The y direction we're moving in
	  * @return True if the move specified is valid
	  */
	 private boolean validMove(int x,int y,int xo,int yo) {
		 if ((xo == 0) || (yo == 0)) {
			 return true;
		 }

		 if (collides(x+xo,y)) {
			 return false;
		 }
		 if (collides(x,y+yo)) {
			 return false;
		 }

		 return true;
	 }

	 public boolean collides(int x, int y) {
		 if ((x < 0) || (x >= mapWidth)) {
			 return true;
		 }

		 if ((y < 0) || (y >= mapHeight)) {
			 return true;
		 }

		 if (terrain != null) {
			 return (terrain[x][y] == Terrain.BLOCK) || (terrain[x][y] == Terrain.TREASURE);   
		 } else {
			 return blocked[x][y];
		 } 
	 }        

}
