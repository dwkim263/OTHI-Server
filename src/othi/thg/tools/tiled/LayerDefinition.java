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

import java.io.Serializable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import othi.thg.common.InputSerializer;
import othi.thg.common.OutputSerializer;
import othi.thg.common.Serializer;


/**
 * The class that stores the definition of a layer.
 * A Layer consists mainly of:<ul>
 * <li>width and height
 * <li>name <b>VERY IMPORTANT</b>
 * <li>data 
 * </ul>
 * 
 * @author miguel
 *
 */
public class LayerDefinition implements Serializer, Serializable {
    private static final long serialVersionUID = 1L;
	/** To which map this layer belong */
	private THGMapStructure map;
	
	/** Width of the layer that SHOULD be the same that the width of the map. */
	private int width;
	/** Height of the layer that SHOULD be the same that the height of the map. */
	private int height;

	/** Name of the layer that MUST be one of the available:<ul>
	 * <li>0_floor
	 * <li>1_terrain
	 * <li>2_object
	 * <li>3_roof
	 * <li>4_roof_add
	 * <li>objects
	 * <li>collision
	 * <li>protection
	 * </ul>
	 */
	private String name;
	
	/** The data encoded as int in a array of size width*height */
	private int[] data;
	/** The same data in a raw byte array, so we save reencoding it again for serialization */
	private byte[] raw;

	/**
	 * Constructor
	 * @param layerWidth the width of the layer.
	 * @param layerHeight the height of the layer
	 */ 
	public LayerDefinition(int layerWidth, int layerHeight) {
		raw=new byte[4*layerWidth*layerHeight];
		width=layerWidth;
		height=layerHeight;
	}
	
	/**
	 * Sets the map to which this layer belong to.
	 * @param map the map
	 */
	void setMap(THGMapStructure map) {
		this.map=map;
	}

	/**
	 * Builds the real data array based on the byte array.
	 * It is only needed for objects, collision and protection, which is at most 40% of the layers.
	 */
	public void build() {
		data=new int[height*width];
		int offset=0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {								
				int tileId = 0;
				tileId |= ((int)raw[0+offset]& 0xFF);
				tileId |= ((int)raw[1+offset]& 0xFF) <<  8;
				tileId |= ((int)raw[2+offset]& 0xFF) << 16;
				tileId |= ((int)raw[3+offset]& 0xFF) << 24;
				
				data[x+y*width]=tileId;
				offset+=4;
			}
		}
	}

	/**
	 * Returns the allocated raw array so it can be modified.
	 * @return
	 */
	public byte[] exposeRaw() {
		return raw;
	}

	/**
	 * Set a tile at the given x,y position.
	 * @param x the x position
	 * @param y the y position 
	 * @param tileId the tile code to set ( Use 0 for none ).
	 */
	public void set(int x, int y, int tileId) {
		data[y*width+x]=tileId;
	}

	/**
	 * Returns the tile at the x,y position 
	 * @param x the x position
	 * @param y the y position 
	 * @return the tile that exists at that position or 0 for none.
	 */
	public int getTileAt(int x, int y) {
		return data[y*width+x];
	}

	/**
	 * 
	 * @return
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 */
	public byte[] encode() throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		DeflaterOutputStream out_stream = new DeflaterOutputStream(array);
		OutputSerializer out = new OutputSerializer(out_stream);
		
		writeObject(out);
		out_stream.close();
		
		return array.toByteArray();
    }

	/**
	 * Deserialize a layer definition
	 * 
	 * @param in input serializer
	 * @return an instance of a layer definition
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static LayerDefinition decode(InputStream in) throws IOException, ClassNotFoundException {
		LayerDefinition layer=new LayerDefinition(0,0);

		InflaterInputStream szlib = new InflaterInputStream(in,new Inflater());
		InputSerializer ser=new InputSerializer(szlib);
		
		layer=(LayerDefinition) ser.readObject(layer);
		layer.build();
		return layer;
    }

	/**
	 * Returns the width of the layer
	 * @return
	 */
	public int getWidth() {
	    return width;
    }

	/**
	 * Returns the height of the layer
	 * @return
	 */
	public int getHeight() {
	    return height;
    }

	/**
	 * Returns the name of the tileset a tile belongs to.
	 * @param value the tile id
	 * @return the name of the tileset
	 */
	public TileSetDefinition getTilesetFor(int value) {
		if(value==0) {
			return null;
		}
		
		List<TileSetDefinition> tilesets=map.getTilesets();

		int pos=0;
		for(pos=0;pos<tilesets.size();pos++) {
			if(value<tilesets.get(pos).getFirstGid()) {
				break;
			}
		}
		
		return tilesets.get(pos-1);
    }

	/** 
	 * Sets the name of the layer 
	 * @param layerName the name of the layer
	 */
	public void setName(String layerName) {
	    name=layerName;
    }

	/**
	 * Returns the name of the layer
	 * @return
	 */
	public String getName() {
		return name;
    }

    @Override
	public void readObject(InputSerializer in) throws IOException, ClassNotFoundException {
		name=in.readString();
		width=in.readInt();
		height=in.readInt();
		raw=in.readByteArray();
    }

    @Override
	public void writeObject(OutputSerializer out) throws IOException {
		out.write(name);
		out.write(width);
		out.write(height);
		out.write(raw);
    }

    public int[] expose() {
	    return data;
    }
}
