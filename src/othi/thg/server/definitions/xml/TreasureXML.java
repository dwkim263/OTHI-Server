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
import java.util.Map;
import java.util.HashMap;
import org.w3c.dom.*;

import othi.thg.server.definitions.*;

import java.util.logging.Logger;
import java.util.logging.Level;


public class TreasureXML extends TssXML {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -2811258831157619930L;

	private static final Logger logger = Logger.getLogger(TreasureXML.class.getName());
    
    private final String qualifiedName = "treasures";
    private final String firstElementName = "treasure";
    private final String[] firstElementAttributes = new String[] {"topic", "place", "name"};
    private final String[] textElementNames = new String[]{"question", "sample_answer", "conclusion", "golds", "experience_point", "tool", "armor", "weapon"};

    private String[] textNodeValues = new String[8];
    private Element firstNodeElement = null;
            
    public TreasureXML (String xmlFileName){     
        setXmlFile(new XmlFile());
        getXmlFile().setFileName(xmlFileName);
        getXmlFile().setExisted(xmlFileName);        
       if (getXmlFile().isExisted()){
            setXmlDoc(getXmlFile(), qualifiedName);
       }
    }
        
    public TreasureXML(String courseId, String question, String sampleAnswer, String conclusion, String golds, String exps, String tool, String armor, String weapon) {
        setTextNodeValues(question, sampleAnswer, conclusion, golds, exps, tool, armor, weapon);
        setXmlFile(new XmlFile(DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.DEFAULT_COURSE_ID + File.separator + DefinitionsDefault.TREASURE_XML));
        if (getXmlFile().isExisted()){
            setXmlDoc(getXmlFile(), qualifiedName);
        } else {
            createXmlDoc(qualifiedName);        
        }    
    }
    
    public void putFirstNode(String[] attributeValues){
        Element root = getXmlDoc().getDocumentElement();
        firstNodeElement = putAttributeNode(getXmlDoc(), root, firstElementName, firstElementAttributes, attributeValues);
    }
    
    public void putTextNodes(){
        putTextNodes(getXmlDoc(), getFirstNodeElement(), textElementNames, textNodeValues);
    }
            
    public void writeXML(){
        writeXML(getXmlFile());
    }
    
    public void setTextNodeValues (String question, String sampleAnswer, String conclusion, String golds, String exps, String tool, String armor, String weapon){
        textNodeValues[0] = question;
        textNodeValues[1] = sampleAnswer;
        textNodeValues[2] = conclusion;
        textNodeValues[3] = golds;
        textNodeValues[4] = exps;
        textNodeValues[5] = tool;
        textNodeValues[6] = armor;
        textNodeValues[7] = weapon;
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
                    
                    Map<String, String> property = new HashMap<String, String>();
                    
                    Element documentElement = (Element)documentNode;

                    for (String attribute: firstElementAttributes) {
                        property.put(attribute, documentElement.getAttribute(attribute));
                    }

                    for (String elementName: textElementNames) {
                        property.put(elementName, getFistNodeValue(documentElement, elementName));
                    }
                    
                    properties.put(property.get("topic") + ":" + property.get("place") + ":" + property.get("name"), property);
                }
            }
        }
        
        return properties;            
    }         
}
