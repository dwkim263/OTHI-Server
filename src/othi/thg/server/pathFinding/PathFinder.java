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
/**
 * A description of any implementation of path finding. The class will
 * be responsible for finding paths between two points. The map and tileset
 * being searched will be specified in the constructor to the particular
 * search object.
 * 
 * @author Kevin Glass
 */
public interface PathFinder {
	/**
	 * Reset the internal state of the path finder. This is expected to be
	 * called between path finds to clear out search data. 
	 * 
	 * Note, implementations should endevour to keep this an efficient method
	 */
	public abstract void reset();

	/**
	 * Retrieve an array of distances for different points on the map. This
	 * data is likely to be cleared after a call to <code>reset()</code>. 
	 * 
	 * This data is probably only useful for test/debug tools. 
	 * 
	 * @return An array of distances from the end point of the last search
	 * for different points on the map.
	 */
	public abstract int[] getSearchData();

	/**
	 * Find a path from a starting location to a destination point. 
	 * 
	 * @param sx The starting x coordinate
	 * @param sy The starting y coordinate
	 * @param dx The destination x coordinate
	 * @param dy The destination y coordinate
	 * @param maxsearch The maximum search depth that will be reached
	 * during the search. This is helpful for restricting the amount of 
	 * time a search may take
	 * @return The path found or null if no path could be determined
	 */
	public abstract Path findPath(int sx, int sy, int dx, int dy,
                                      int maxsearch);
}