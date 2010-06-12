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

/**
 * Loads a TMX file to server so it can understand:
 * a) The objects layer
 * b) The collision layer
 * c) The protection layer.
 * d) All the layers that are sent to client
 * e) The tileset data that is also transfered to client
 * f) A preview of the zone for the minimap.
 * 
 * Client would get the layers plus the tileset info.
 * 
 * @author miguel
 *
 */
package othi.thg.tools.tiled;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;
import java.util.List;
import java.util.Properties;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import othi.thg.common.Base64;

public class TMXLoader {

        private static final Logger logger = Logger.getLogger(TMXLoader.class.getName());        

	private THGMapStructure thoMap;
	private String xmlPath;
	
 	private static String makeUrl(String filename) {
		final String url;

                if (filename.indexOf("://") > 0 || filename.startsWith("file:")) {
                        url = filename;
                } else {
                        url = (new File(filename)).toURI().toString();
                }

		return url;
	}

	private static String getAttributeValue(Node node, String attribname) {
		NamedNodeMap attributes = node.getAttributes();
		String att = null;
		if (attributes != null) {
			Node attribute = attributes.getNamedItem(attribname);
			if (attribute != null) {
				att = attribute.getNodeValue();
			}
		}
		return att;
	}

	private static int getAttribute(Node node, String attribname, int def) {
		String attr = getAttributeValue(node, attribname);
		if (attr != null) {
			return Integer.parseInt(attr);
		} else {
			return def;
		}
	}

	private TileSetDefinition unmarshalTileset(Node t) throws IOException {
		String name=getAttributeValue(t, "name");
		int firstGid = getAttribute(t, "firstgid", 1);

		TileSetDefinition set = new TileSetDefinition(name,firstGid);

		boolean hasTilesetImage = false;
		NodeList children = t.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);

			if (child.getNodeName().equalsIgnoreCase("image")) {
				if (hasTilesetImage) {
					continue;
				}

				set.setSource(getAttributeValue(child, "source"));
			}
		}

		return set;
	}

	/**
	 * Reads properties from amongst the given children. When a "properties"
	 * element is encountered, it recursively calls itself with the children
	 * of this node. This function ensures backward compatibility with tmx
	 * version 0.99a.
	 *
	 * @param children the children amongst which to find properties
	 * @param props    the properties object to set the properties of
	 */
	@SuppressWarnings("unused")
    private static void readProperties(NodeList children, Properties props) {
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			if ("property".equalsIgnoreCase(child.getNodeName())) {
				props.setProperty(
						getAttributeValue(child, "name"),
						getAttributeValue(child, "value"));
			}
			else if ("properties".equals(child.getNodeName())) {
				readProperties(child.getChildNodes(), props);
			}
		}
	}

	/**
	 * Loads a map layer from a layer node.
	 */
	private LayerDefinition readLayer(Node t) throws IOException {
		int layerWidth = getAttribute(t, "width", thoMap.getWidth());
		int layerHeight = getAttribute(t, "height", thoMap.getHeight());

		LayerDefinition layer=new LayerDefinition(layerWidth, layerHeight);

		int offsetX = getAttribute(t, "x", 0);
		int offsetY = getAttribute(t, "y", 0);

		if(offsetX!=0 || offsetY!=0) {
			System.err.println("Severe error: maps has offset displacement");
		}
		
		layer.setName(getAttributeValue(t, "name"));

		// XXX: Ignored by now.
		//readProperties(t.getChildNodes(), ml.getProperties());

		for (Node child = t.getFirstChild(); child != null;
		child = child.getNextSibling())
		{
			if ("data".equalsIgnoreCase(child.getNodeName())) {
				String encoding = getAttributeValue(child, "encoding");

				if (encoding != null && "base64".equalsIgnoreCase(encoding)) {
					Node cdata = child.getFirstChild();
					if (cdata != null) {
						char[] enc = cdata.getNodeValue().trim().toCharArray();
						byte[] dec = Base64.decode(enc);
						ByteArrayInputStream bais = new ByteArrayInputStream(dec);
						InputStream is;

						String comp = getAttributeValue(child, "compression");

						if (comp != null && "gzip".equalsIgnoreCase(comp)) {
							is = new GZIPInputStream(bais);
						} else {
							is = bais;
						}

						byte[] raw=layer.exposeRaw();
						int offset=0;
						
						while(offset!=raw.length) {
							offset+=is.read(raw,offset,raw.length-offset);							 
						}
					}
				}
			}
		}

		return layer;
	}

	private void buildMap(Document doc) throws IOException {
		Node mapNode;

		mapNode = doc.getDocumentElement();

		if (!"map".equals(mapNode.getNodeName())) {
			throw new IOException("Not a valid tmx map file.");
		}

		// Get the map dimensions and create the map
		int mapWidth = getAttribute(mapNode, "width", 0);
		int mapHeight = getAttribute(mapNode, "height", 0);

		if (mapWidth > 0 && mapHeight > 0) {
			thoMap= new THGMapStructure(mapWidth, mapHeight);
		}
		
		if (thoMap == null) {
			throw new IOException("Couldn't locate map dimensions.");
		}

		// Load the tilesets, properties, layers and objectgroups
		for (Node sibs = mapNode.getFirstChild(); sibs != null; sibs = sibs.getNextSibling())
		{
			if ("tileset".equals(sibs.getNodeName())) {
				thoMap.addTileset(unmarshalTileset(sibs));
			}
			else if ("layer".equals(sibs.getNodeName())) {
				thoMap.addLayer(readLayer(sibs));
			}
		}
	}

	private THGMapStructure unmarshal(InputStream in) throws IOException  {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setExpandEntityReferences(false);
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        doc = builder.parse(in);                          
//			doc = builder.parse(in, xmlPath);
		} catch (SAXException e) {
			e.printStackTrace();
			throw new IOException("Error while parsing map file: " +
					e.toString());
	        } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                }  

		buildMap(doc);
		return thoMap;
	}

	public THGMapStructure readMap(String filename) throws IOException {
	   	xmlPath = filename.substring(0,filename.lastIndexOf(File.separatorChar) + 1);
          
		InputStream is = getClass().getClassLoader().getResourceAsStream(filename);

		if(is==null) {
			String xmlFile = makeUrl(filename);
			//xmlPath = makeUrl(xmlPath);

			URL url = new URL(xmlFile);
			is = url.openStream();
		}
		
		if(is==null) {
			return null;
		}

		// Wrap with GZIP decoder for .tmx.gz files
		if (filename.endsWith(".gz")) {
			is = new GZIPInputStream(is);
		}

		THGMapStructure unmarshalledMap = unmarshal(is);
		unmarshalledMap.setFilename(filename);

		return unmarshalledMap;
	}

	/*
	public static void main(String[] args) throws Exception {
		System.out.println("Test: loading map");
		
		THGMapStructure map=null;
		/*
		long start=System.currentTimeMillis();
		for(int i=0;i<90;i++) {			
			map=new ServerTMXLoader().readMap("D:/Desarrollo/tho/tiled/interiors/abstract/afterlife.tmx");
			map=new ServerTMXLoader().readMap("D:/Desarrollo/tho/tiled/Level 0/ados/city_n.tmx");
			map=new ServerTMXLoader().readMap("D:/Desarrollo/tho/tiled/Level 0/network/city.tmx");
			map=new ServerTMXLoader().readMap("D:/Desarrollo/tho/tiled/Level 0/nalwor/city.tmx");
			map=new ServerTMXLoader().readMap("D:/Desarrollo/tho/tiled/Level 0/orril/castle.tmx");
		}
		
		System.out.println("Time ellapsed (ms): "+(System.currentTimeMillis()-start));
	
		map=new TMXLoader().readMap("D:/Desarrollo/tho/tiled/Level 0/network/village_w.tmx");
		map.buildLayers();
		System.out.printf("MAP W: %d H:%d\n", map.getWidth(), map.getHeight());
		List<TileSetDefinition> tilesets=map.getTilesets();
		for(TileSetDefinition set: tilesets) {
			System.out.printf("TILESET firstGID: '%d' name: '%s'\n", set.getFirstGid(), set.getSource());
		}

		List<LayerDefinition> layers=map.getLayers();
		for(LayerDefinition layer: layers) {			
			System.out.printf("LAYER name: %s\n", layer.getName());
			int w=layer.getWidth();
			int h=layer.getHeight();
			
			for(int y=0;y<h;y++) {
				for(int x=0;x<w;x++) {
					int gid=layer.getTileAt(x, y);
					logger.info(gid + ((x == w - 1) ? "" : ","));
				}
			System.out.println();
			}
		}

	}
*/

	public static THGMapStructure load(String filename) throws IOException {
	    return new TMXLoader().readMap(filename);
    }
}

