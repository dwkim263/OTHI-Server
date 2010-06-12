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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import othi.thg.common.InputSerializer;
import othi.thg.common.OutputSerializer;
import othi.thg.common.Serializer;


/**
 * Stores a definition of a tileset.
 * Mainly its name, the source image used and the starting global id.
 * 
 * @author miguel
 *
 */
public class TileSetDefinition implements Serializer, Serializable {
    private static final long serialVersionUID = 1L;
	/** The name of the tileset. Useless */
	private String name;
	/** The source image of this tileset */
	private String source;
	/** The id where this tileset begins to number tiles. */ 
	private int gid;			

	/**
	 * Constructor
	 * @param name the *useless* name of the tileset.
	 * @param firstGid the id where this tileset begins to number tiles.
	 */
	public TileSetDefinition(String name, int firstGid) {
		this.name=name;
		this.gid=firstGid;
    }
	
	/**
	 * Returns the id where this tileset begins to number tiles
	 * @return the id where this tileset begins to number tiles
	 */
	public int getFirstGid() {
		return gid;
	}

	/**
	 * Set the filename of the source image of the tileset. 
	 * @param attributeValue the filename
	 */
	public void setSource(String attributeValue) {
		this.source=attributeValue;
    }
	
	/**
	 * Returns the filename of the source image of the tileset.
	 * @return the filename of the source image of the tileset.
	 */
	public String getSource() {
		return source;		
	}
	
	public byte[] encode() throws IOException {
		ByteArrayOutputStream array = new ByteArrayOutputStream();
		OutputSerializer out = new OutputSerializer(array);
		
		writeObject(out);
		
		return array.toByteArray();
    }

    @Override
	public void readObject(InputSerializer in) throws IOException, ClassNotFoundException {
		name=in.readString();
		source=in.readString();
		gid=in.readInt();
    }

    @Override
	public void writeObject(OutputSerializer out) throws IOException {
		out.write(name);
		out.write(source);
		out.write(gid);		
    }
	
    @Override
    public boolean equals(Object object) {
            if(!(object instanceof TileSetDefinition)) {
                    return false;
            }

            TileSetDefinition set=(TileSetDefinition) object;
            return set.name.equals(name) && set.source.equals(source) && set.gid==gid;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 23 * hash + (this.source != null ? this.source.hashCode() : 0);
        hash = 23 * hash + this.gid;
        return hash;
    }
}
