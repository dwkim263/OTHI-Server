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

package othi.thg.server.definitions;

/**
 *
 * @author Steve
 */
import java.io.*;
import java.util.*;
import java.util.logging.Logger;
import java.util.logging.Level;

import othi.thg.server.definitions.xml.CourseIntroductionXML;
import othi.thg.server.definitions.xml.SubTopicXML;

public class SubTopicDefinition {
    private static final Logger logger = Logger.getLogger(SubTopicDefinition.class.getName());
    
    private static Map<String, SubTopicDefinition> subTopics = new HashMap<String, SubTopicDefinition>();

    private String courseId;
    private String courseTitle;
    private String topicTitle;
    private String subTopicTitle;
    private String subTopicDescription;
    private String subTopicClue;    
        
    private SubTopicDefinition(String courseId, String courseTitle, String topicTitle,
            String subTopicTitle, String subTopicDescription, String subTopicClue){
        
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.topicTitle = topicTitle;
        this.subTopicTitle = subTopicTitle;
        this.subTopicDescription = subTopicDescription;
        this.subTopicClue = subTopicClue;        
        subTopics.put(courseId + ":" + topicTitle + ":" + subTopicTitle, this);
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getSubTopicClue() {
        return subTopicClue;
    }

    public String getSubTopicDescription() {
        return subTopicDescription;
    }

    public String getSubTopicTitle() {
        return subTopicTitle;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public void setSubTopicClue(String subTopicClue) {
        this.subTopicClue = subTopicClue;
    }

    public void setSubTopicDescription(String subTopicDescription) {
        this.subTopicDescription = subTopicDescription;
    }

    public void setSubTopicTitle(String subTopicTitle) {
        this.subTopicTitle = subTopicTitle;
    }
      

    public static Map<String, SubTopicDefinition> getSubTopics() {
        if (subTopics.isEmpty()) {
            setSubTopics();
        }
        return subTopics;
    }
    
    public static void setSubTopics() {
        
        File[] directories = (new File(DefinitionsDefault.getDefinitionsDir())).listFiles();
        for (File directory: directories) {
            if (directory.isDirectory()) {                
                CourseIntroductionXML introductionXml = null;
                SubTopicXML subTopicXml = null;
                
                File[] files = directory.listFiles();
                for (File file: files) {
                    if (file.getName().equals(DefinitionsDefault.INTRODUCTION_XML)) {
                        introductionXml = new CourseIntroductionXML(file.getAbsolutePath());
                    } else if (file.getName().equals(DefinitionsDefault.SUB_TOPIC_XML)) {
                        subTopicXml = new SubTopicXML(file.getAbsolutePath());
                    }        
                }
                
                if (introductionXml != null && subTopicXml != null) {
                    Map <String, Object> introduction = introductionXml.getProperties();
                    Map <String, Object> subTopics = subTopicXml.getProperties();  
                    
                    Iterator it = subTopics.keySet().iterator();                    
                    while(it.hasNext()) {                        
                        String title = (String) it.next();    
                        int indexOfColon = title.indexOf(":");
                        String mainTopicTitle = title.substring(0, indexOfColon);
                        String subTopicTitle = title.substring(indexOfColon + 1);
                        Map <String, String> subTopic = (Map <String, String>) subTopics.get(title);                        
                        new SubTopicDefinition((String) introduction.get("id"), (String) introduction.get("title"),
                             mainTopicTitle, subTopicTitle, subTopic.get("description"), subTopic.get("clue") );    
                    }
                }
            }
        }        
    }
      
    public static void setSubTopics(Map<String, SubTopicDefinition> subTopic) {
        SubTopicDefinition.subTopics = subTopic;
    }
    
   
    public static SubTopicDefinition getById(String id) {
        return subTopics.get(id);
    }
        
}
