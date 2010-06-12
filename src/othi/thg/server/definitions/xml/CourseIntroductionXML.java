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

public class CourseIntroductionXML extends TssXML {
    
	private static final long serialVersionUID = 6277264134324790799L;

	private static final Logger logger = Logger.getLogger(CourseIntroductionXML.class.getName());
    
    private final String qualifiedName = "introduction";
    private final String[] textElementNames = new String[]{"id", "title", "objective", "references"};

    private String[] textNodeValues = new String[4];

    public CourseIntroductionXML (String xmlFileName){     
        setXmlFile(new XmlFile());
        getXmlFile().setFileName(xmlFileName);
        getXmlFile().setExisted(xmlFileName);
       if (getXmlFile().isExisted()){
            setXmlDoc(getXmlFile(), qualifiedName);
       }                
    }
    
    public CourseIntroductionXML 
            (String courseId, String courseTitle, String courseObjective, String courseReferences) {
        
        setTextNodeValues(courseId, courseTitle, courseObjective, courseReferences);
        setXmlFile(new XmlFile(DefinitionsDefault.getDefinitionsDir() + DefinitionsDefault.DEFAULT_COURSE_ID + File.separator + qualifiedName + ".xml"));
        if (!getXmlFile().isExisted()){
            createXmlDoc(qualifiedName);
        }
    }
    
    public void putTextNodes() {
        Element element = getXmlDoc().getDocumentElement();            
        putTextNodes(getXmlDoc(), element, textElementNames, textNodeValues);
    }
            
    public void writeXML(){
        writeXML(getXmlFile());
    }
    
    public void setTextNodeValues
            (String courseId, String courseTitle, String courseObjective, String courseReferences){
        textNodeValues[0] = courseId;
        textNodeValues[1] = courseTitle;   
        textNodeValues[2] = courseObjective;   
        textNodeValues[3] = courseReferences;           
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

            for (String elementName : textElementNames) {
                 properties.put(elementName, getFistNodeValue(element, elementName));            
            }
        }

        return properties;
    }    
}
