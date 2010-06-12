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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
import othi.thg.server.definitions.xml.TopicEvaluationXML;

public class EvaluationDefinition {
    private static final Logger logger = Logger.getLogger(EvaluationDefinition.class.getName());
    
    private static Map<String, EvaluationDefinition> evaluations = new HashMap<String, EvaluationDefinition>();

    private String courseId;
    private String topicTitle;     
    private String topicSampleAnswer;      
    private String topicConclusion;
    private String treasureClue;

    private EvaluationDefinition(String courseId, String topicTitle,
            String topicSampleAnswer, String topicConclusion, String clue){
        this.courseId = courseId;
        this.topicTitle = topicTitle;        
        this.topicSampleAnswer = topicSampleAnswer;   
        this.topicConclusion = topicConclusion;
        this.treasureClue = clue;
        evaluations.put(courseId + ":" + topicTitle, this);        
    }
      
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseId() {
        return courseId;
    }

    public static Map<String, EvaluationDefinition> getEvaluations() {
        if (evaluations.isEmpty()) {
            setEvaluations();            
        }
        return evaluations;
    }
    
    public static void setEvaluations() {        
        File[] directories = (new File(DefinitionsDefault.getDefinitionsDir())).listFiles();
        for (File directory: directories) {
            if (directory.isDirectory()) {                
                CourseIntroductionXML introductionXml = null;
                TopicEvaluationXML topicEvaluationXml = null;

                File[] files = directory.listFiles();
                for (File file: files) {
                    if (file.getName().equals(DefinitionsDefault.INTRODUCTION_XML)) {
                        introductionXml = new CourseIntroductionXML(file.getAbsolutePath());
                    } else if (file.getName().equals(DefinitionsDefault.EVALUATION_XML)) {
                        topicEvaluationXml = new TopicEvaluationXML(file.getAbsolutePath());
                    }                    
                }
                
                if (introductionXml != null && topicEvaluationXml != null) {
                    Map <String, Object> introduction = introductionXml.getProperties();
                    Map <String, Object> evaluation = topicEvaluationXml.getProperties();   
                    Iterator it = evaluation.keySet().iterator();                    
                    while(it.hasNext()) {
                        String title = (String) it.next();                        
                        Map <String, String> topic = (Map <String, String>) evaluation.get(title);                        
                        new EvaluationDefinition((String) introduction.get("id"), title, topic.get("sample_answer"),
                                topic.get("conclusion"), topic.get("clue") );   
                    }
                }
            }
        }        
    }
        
    public static void setEvaluations(Map<String, EvaluationDefinition> evaluations) {
        EvaluationDefinition.evaluations = evaluations;
    }
    
   
    public static EvaluationDefinition getById(String id) {
        return evaluations.get(id);
    }

    public String getTopicConclusion() {
        return topicConclusion;
    }

    public void setTopicConclusion(String topicConclusion) {
        this.topicConclusion = topicConclusion;
    }

    public String getTopicSampleAnswer() {
        return topicSampleAnswer;
    }

    public void setTopicSampleAnswer(String topicSampleAnswer) {
        this.topicSampleAnswer = topicSampleAnswer;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public String getTreasureClue() {
        return treasureClue;
    }

    public void setTreasureClue(String treasureClue) {
        this.treasureClue = treasureClue;
    }
    
}
