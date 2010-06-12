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

package othi.thg.tools.tiled;

import java.util.LinkedList;
import java.util.List;
import java.io.Serializable;
/* 
 * @author miguel
 * @author Dong Won Kim
 */
public class THGMapStructure implements Serializable {

	private static final long serialVersionUID = 20070722164027L; 
	/** TMX Filename that contains this map. */ 
	private String filename;
	/** Width of the map */
	private int width;
	/** Height of the map */
	private int height;
	/** List of tilesets that this map contains */
	List<TileSetDefinition> tilesets;
	/** List of layers this map contains */
	List<LayerDefinition> layers;		

	/**
	 * Constructor.
	 * @param w the width of the map
	 * @param h the height of the map.
	 */
	public THGMapStructure(int w, int h){
		width=w;
		height=h;
		tilesets=new LinkedList<TileSetDefinition>();
		layers=new LinkedList<LayerDefinition>();
	}

	/**
	 * Add a new tileset to the map
	 * @param set new tileset
	 */
	public void addTileset(TileSetDefinition set) {
		tilesets.add(set);	        
	}

	/**
	 * Add a new layer to the map
	 * @param layer new layer
	 */
	public void addLayer(LayerDefinition layer) {
		layer.setMap(this);
		layers.add(layer);
	}

	/**
	 * Sets the map TMX filename
	 * @param filename the map TMX filename
	 */
	public void setFilename(String filename) {
		this.filename=filename;	        
	}

	/**
	 * Returns a list of the tilesets this map contains.
	 * @return a list of the tilesets this map contains.
	 */
	public List<TileSetDefinition> getTilesets() {
		return tilesets;
	}

	/**
	 * Returns a list of the layers this map contains.
	 * @return a list of the layers this map contains.
	 */
	public List<LayerDefinition> getLayers() {
		return layers;
	}

	/**
	 * Return true if the layer with given name exists.
	 * @param layername the layer name
	 * @return true if it exists.
	 */
	public boolean hasLayer(String layername) {
		return getLayer(layername)!=null;
	}

	/**
	 * Returns the layer whose name is layer name or null
	 * @param layername the layer name
	 * @return the layer object or null if it doesnt' exists
	 */
	public LayerDefinition getLayer(String layername) {
		for(LayerDefinition layer: layers) {
			if(layername.equals(layer.getName())) {
				return layer;
			}	
		}

		return null;
	}

	/**
	 * Build all layers data.
	 */
	public void buildLayers() {
		for(LayerDefinition layer: layers) {
			layer.build();			
		}	    
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
