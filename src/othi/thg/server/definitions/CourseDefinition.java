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

public class CourseDefinition {
    private static final Logger logger = Logger.getLogger(CourseDefinition.class.getName());
    
    private static Map<String, CourseDefinition> courses = new HashMap<String, CourseDefinition>();

    private String courseId;
    private String courseTitle;
        
    private CourseDefinition(String courseId, String courseTitle){
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        courses.put(courseId, this);
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

    public static Map<String, CourseDefinition> getCourses() {
        if (courses.isEmpty()) {
            setCourses();            
        }
        return courses;
    }
    
    public static void setCourses() {
        
        File[] directories = (new File(DefinitionsDefault.getDefinitionsDir())).listFiles();
        
        for (File directory: directories) {
            if (directory.isDirectory()) {                
                CourseIntroductionXML introductionXml = null;
                File[] files = directory.listFiles();
                for (File file: files) {
                    if (file.getName().equals(DefinitionsDefault.INTRODUCTION_XML)) {
                        introductionXml = new CourseIntroductionXML(file.getAbsolutePath());
                    }                  
                }
                
                if (introductionXml != null) {
                    Map <String, Object> introduction = introductionXml.getProperties();             
                    new CourseDefinition((String) introduction.get("id"), (String) introduction.get("title"));
                }
            }
        }    
    }
        
    public static void setCourses(Map<String, CourseDefinition> courses) {
        CourseDefinition.courses = courses;
    }
    
   
    public static CourseDefinition getById(String id) {
        return courses.get(id);
    }    
}
