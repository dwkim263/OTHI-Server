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
 * @author Steve
 */
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.dom.*; 
import org.xml.sax.SAXException;

public abstract class TssXML implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -2125132444668666439L;

	private static final Logger logger = Logger.getLogger(TssXML.class.getName());

    private Document xmlDoc = null;
    private XmlFile xmlFile = null;    

    public void createXmlDoc(String qualifiedName) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();

            xmlDoc = impl.createDocument(null, qualifiedName, null);
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, null,"XmlManager" + ex);
        }        
    }   
    
    // public abstract Document buildXML(Document xmldoc, String elementName, String attributeName, String attributeValue);
    public Element putAttributeNode(Document xmlDoc, Element parentElement, String elementName, String[] attributes, String[] attributeValues){
        Element element = xmlDoc.createElementNS(null, elementName);    
        for (int i=0; i<attributes.length; ++i){
            element.setAttributeNS(null, attributes[i], attributeValues[i]);
        }
        parentElement.appendChild(element);
        return element;
    }
    
    public void putTextNodes(Document xmlDoc, Element parentElement, String[] elementNames, String[] textNodeValues) {
        Element element = null;
        Node node = null;   
        
        for (int i=0; i<elementNames.length; ++i){
            element = xmlDoc.createElementNS(null, elementNames[i]);
            node = xmlDoc.createTextNode(textNodeValues[i]);
            element.appendChild(node);
            parentElement.appendChild(element);
        }
    }    
            
    public String getFistNodeValue(Element element, String tagName)
    {
        NodeList list = element.getElementsByTagName(tagName);
        Element cElement = (Element)list.item(0);

        if(cElement != null && cElement.getFirstChild()!=null){
             return cElement.getFirstChild().getNodeValue();
        }else{
            return "";
        }   
    }    
    
    public NodeList getNamedNode(Element element, String name) {
            NodeList list = element.getElementsByTagName(name);
            if (list.getLength() == 0) {
                    return null;
            }
            return list;
    }
    
    public String[] parseParagraphElement(Element element,  String tagName)
    {
        NodeList list = element.getElementsByTagName(tagName);
            
        String[] nodeValues = new String[list.getLength()];
                
        for (int i = 0; i < list.getLength(); i++) {        
            Element cElement = (Element)list.item(i);
         
            if(cElement.getFirstChild()!=null){
                nodeValues[i] = cElement.getFirstChild().getNodeValue();
            }else{
                nodeValues[i] = "";
            }   
        }
        return nodeValues;
    }      
    
    public void writeXML(XmlFile file) {        
        
        DOMSource domSource = new DOMSource(xmlDoc);
        OutputStream outStream = null;
        
        try {
            TransformerFactory transformerFactory  = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT,"yes");
            
            outStream = file.getOutStream();

            StreamResult streamResult = new StreamResult(outStream);        
            transformer.transform(domSource, streamResult); 
    
        } catch (TransformerException ex) {
            logger.log(Level.SEVERE, null,"writeXML" + ex);
        } finally {
            //Close the BufferedWriter
            try {
                if (outStream != null) {
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public Document getXmlDoc() {
        return xmlDoc;
    }
    
    public void setXmlDoc(XmlFile xmlFile, String qualifiedName) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = builder.parse(xmlFile.getFileName());

            Element element = document.getDocumentElement();
            if (!element.getNodeName().equals(qualifiedName)) {
                    throw new IOException("Not a " + qualifiedName + "file");
            } else {
               xmlDoc =  document;
            }       
        } catch (ParserConfigurationException ex) {
            logger.log(Level.SEVERE, "setXmlDoc" + ex);            
        } catch (SAXException ex) {
            logger.log(Level.SEVERE, "setXmlDoc" + ex);                  
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "setXmlDoc" + ex);            
        }
    }   
    
    public void setXmlFile(XmlFile xmlFile) {
        this.xmlFile = xmlFile;
    }

    public XmlFile getXmlFile() {
        return xmlFile;
    }    
 
}
