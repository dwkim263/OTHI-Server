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

package othi.thg.server.events.evaluation;

/**
 *
 * @author Dong Won Kim
 */
import java.io.Serializable;
import java.util.Set;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.util.Arrays;

public class Answer extends Evaluation implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sampleAnswer;
    private String[] stmmersOfSampleAnswer = null;

    public Answer(String sampleAnswer) {
        this.sampleAnswer = this.removeHighlyFrequentWord(sampleAnswer);
        stmmersOfSampleAnswer = sortUniqueStemmers(getStemmers(this.sampleAnswer));
    }

    private String[] sortUniqueStemmers(Set<String> stemmers) {
        String[] arr = stemmers.toArray(new String[stemmers.size()]);
        Arrays.sort(arr);

        List<String> sortedStemmers = new LinkedList<String>();
        String oldStem = null;

        for (int i=0; i < arr.length; ++i) {
            if (sortedStemmers.isEmpty()) {
                sortedStemmers.add(arr[i]);
                oldStem = arr[i];
            } else {
                String newStem = arr[i];

                if (!oldStem.equals(newStem)) {
                    sortedStemmers.add(newStem);
                    oldStem = newStem;
                }
            }
        }

        return sortedStemmers.toArray(new String[sortedStemmers.size()]);
    }

    @Override
    public int getMark(Set<String> answerStemmers) {
        Iterator<String> iter = answerStemmers.iterator();
        int matchingCount = 0;
        while (iter.hasNext()){
            if (Arrays.binarySearch(stmmersOfSampleAnswer, iter.next()) >= 0) ++matchingCount;
        }

        return (int) (((float) matchingCount/(float) stmmersOfSampleAnswer.length) * 100);
    }    
}