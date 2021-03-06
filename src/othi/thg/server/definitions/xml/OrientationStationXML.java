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
import java.util.List;

public class OrientationStationXML extends StationXML {
       
    /**
	 * 
	 */
	private static final long serialVersionUID = 3534929724637691046L;
	
	private final static String[] textElementNames = new String[]{"guide", "next_quests"};
    private String[] textNodeValues = new String[2];
        
    public OrientationStationXML(String courseId, String guideName, List<String> nextQuests) {
        super(courseId, textElementNames);
        setTextNodeValues(guideName, nextQuests);

    }      

    public void putTextNodes(){
        putTextNodes(textNodeValues);
    }
    
    public void setTextNodeValues (String guideName, List<String> nextQuests ){
        textNodeValues[0] = guideName;
        String quests = "";
        for (String quest: nextQuests) {
            if (quests.isEmpty()) quests = quest;
            else quests = quests + "," + quest;
        }
        textNodeValues[1] = quests;
    }        
}
