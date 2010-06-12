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

package othi.thg.server.definitions.xml;

/**
 *
 * @author Dong Won Kim
 */
import java.io.*;
import java.util.*;
import org.w3c.dom.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class TssXMLReader extends TssXML {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2171608142507762527L;

	private static final Logger logger = Logger.getLogger(TssXMLReader.class.getName());
    
    private String qualifiedName;
    private String firstElementName;
    private String[] firstElementAttributes;

    private Element firstNodeElement = null;
            
    public TssXMLReader (String fileName, String qualifiedName,
                         String firstElementName, String[] firstElementAttributes){
        setXmlFile(new XmlFile());
        getXmlFile().setFileName(fileName);
        getXmlFile().setExisted(true);
        setQualifiedName(qualifiedName);
        setFirstElementName(firstElementName);
        setFirstElementAttributes(firstElementAttributes);
        setXmlDoc(getXmlFile(), qualifiedName);       
    }
    
    public Element getFirstNodeElement() {
        return firstNodeElement;
    }

    public Map<String, Object> getProperties(){

        Map<String, Object> properties = new HashMap<String, Object>();
        
        if (getXmlDoc() != null) {
            Element element = getXmlDoc().getDocumentElement();
            if (!element.getNodeName().equals(qualifiedName)) {
                try {
                    throw new IOException("Not a document file");
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null,ex);
                }
            }

            NodeList documentlist = element.getElementsByTagName(firstElementName);
            for (int i = 0; i < documentlist.getLength(); i++) {
                Node documentNode = documentlist.item(i);
                if (documentNode.getNodeType() == Node.ELEMENT_NODE){    
                    Map<String, Object> property = new HashMap<String, Object>();                    
                    
                    Element documentElement = (Element)documentNode;

                    for (String attribute: firstElementAttributes) {
                        property.put(attribute, documentElement.getAttribute(attribute));
                    }                    
                    properties.put((String) property.get("name"), property);                       
                }
            }
        }
        return properties;
    }

    public Map<String, Object> getObjectProperties(String objectType){

        Map<String, Object> property = new HashMap<String, Object>();

        if (getXmlDoc() != null) {
            Element element = getXmlDoc().getDocumentElement();
            if (!element.getNodeName().equals(qualifiedName)) {
                try {
                    throw new IOException("Not a document file");
                } catch (IOException ex) {
                    logger.log(Level.SEVERE, null,ex);
                }
            }

            NodeList documentlist = element.getElementsByTagName(firstElementName);
            for (int i = 0; i < documentlist.getLength(); i++) {
                Node documentNode = documentlist.item(i);
                if (documentNode.getNodeType() == Node.ELEMENT_NODE){

                    Element documentElement = (Element)documentNode;

                    String elementName = (String) documentElement.getAttribute("name");

                    List<String> objectList = new LinkedList<String>();

                    NodeList nodes = getNamedNode(documentElement, objectType);

                    if (nodes != null) {
                        for (int j=0; j < nodes.getLength(); ++j) {
                                Node objectNode = nodes.item(j);
                                if (objectNode.getNodeType() == Node.ELEMENT_NODE){
                                    Element objectElement = (Element)objectNode;
                                    String name = objectElement.getAttribute("name");
                                    objectList.add(name);
                                }
                        }

                    }

                    property.put(elementName, objectList);
                }
            }
        }
        return property;
    }

    public String[] getFirstElementAttributes() {
        return firstElementAttributes;
    }

    public void setFirstElementAttributes(String[] firstElementAttributes) {
        this.firstElementAttributes = firstElementAttributes;
    }

    public String getFirstElementName() {
        return firstElementName;
    }

    public void setFirstElementName(String firstElementName) {
        this.firstElementName = firstElementName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }
}
