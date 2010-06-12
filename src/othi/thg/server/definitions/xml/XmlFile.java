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
 * 
 */
import java.io.*;
import java.util.logging.Logger;
import java.util.logging.Level;


public class XmlFile implements Serializable {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 4998970102060047448L;

	private static final Logger logger = Logger.getLogger(XmlFile.class.getName());
    
    private boolean existed = false;
    private String fileName = null;

    public XmlFile (){
        
    }
    
    public XmlFile (String fileName){
        setExisted(fileName);
        setFileName(fileName);                
        makeDir(fileName);
    }

    public boolean isExisted() {
        return existed;
    }

    public void setExisted(boolean existed) {       
        this.existed = existed;
    }
        
    public void setExisted(String fileName) {       
        existed = new File(fileName).exists();
    }
            
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setOutStream(String fileName) {

    }

    public OutputStream getOutStream() {
        OutputStream outStream = null;
        try {            
            outStream = new FileOutputStream(getFileName());                            
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }        
        return outStream;
    }

    public void makeDir(String fileName) {
        String dirNames = fileName.substring(0, fileName.lastIndexOf("\\"));
        File dir = new File(dirNames);
        if (!dir.exists()){
            boolean success = dir.mkdirs();   
            if (success) {
              logger.info("Directories: " + dirNames + " created");
            }        
        }
    }        
}
