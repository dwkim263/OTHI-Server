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

import othi.thg.server.definitions.xml.CourseIntroductionXML;
import othi.thg.server.definitions.xml.TopicOrientationXML;

public class TopicDefinition {
    private static final Logger logger = Logger.getLogger(TopicDefinition.class.getName());
    
    private static Map<String, TopicDefinition> topics = new HashMap<String, TopicDefinition>();

    private String courseId;
    private String courseTitle;
    private String topicTitle;
    private String topicIntroduction;    
    private String topicQuestion;
    private String topicClue;
    
    private TopicDefinition(String courseId, String courseTitle, String topicTitle,
            String topicIntroduction, String topicQuestion, String topicClue){
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.topicTitle = topicTitle;
        this.topicIntroduction = topicIntroduction;        
        this.topicQuestion = topicQuestion;
        this.topicClue = topicClue;
        topics.put(courseId + ":" + topicTitle, this);
    }
      
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public static Map<String, TopicDefinition> getTopics() {
        if (topics.isEmpty()) {
            setTopics();            
        }
        return topics;
    }
    
    public static void setTopics() {
        
        File[] directories = (new File(DefinitionsDefault.getDefinitionsDir())).listFiles();
        for (File directory: directories) {
            if (directory.isDirectory()) {                
                CourseIntroductionXML introductionXml = null;
                TopicOrientationXML orientationXml = null;

                File[] files = directory.listFiles();
                for (File file: files) {
                    if (file.getName().equals(DefinitionsDefault.INTRODUCTION_XML)) {
                        introductionXml = new CourseIntroductionXML(file.getAbsolutePath());
                    } else if (file.getName().equals(DefinitionsDefault.ORIENTATION_XML)) {
                        orientationXml = new TopicOrientationXML(file.getAbsolutePath());
                    }                    
                }
                
                if (introductionXml != null && orientationXml != null) {
                    Map <String, Object> introduction = introductionXml.getProperties();
                    Map <String, Object> orientation = orientationXml.getProperties();   
                    Iterator it = orientation.keySet().iterator();                    
                    while(it.hasNext()) {
                        String title = (String) it.next();                        
                        Map <String, String> topic = (Map <String, String>) orientation.get(title);                        
                        new TopicDefinition((String) introduction.get("id"), (String) introduction.get("title"),
                                    title, topic.get("introduction"), topic.get("question"), topic.get("clue") );    
                    }
                }
            }
        }        
    }
      
    public static void setTopics(Map<String, TopicDefinition> courses) {
        TopicDefinition.topics = courses;
    }
    
   
    public static TopicDefinition getById(String id) {
        return topics.get(id);
    }

    public String getTopicQuestion() {
        return topicQuestion;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicQuestion(String topicQuestion) {
        this.topicQuestion = topicQuestion;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTopicClue() {
        return topicClue;
    }

    public void setTopicClue(String topicClue) {
        this.topicClue = topicClue;
    }

    public String getTopicIntroduction() {
        return topicIntroduction;
    }

    public void setTopicIntroduction(String topicIntroduction) {
        this.topicIntroduction = topicIntroduction;
    }
        
}
