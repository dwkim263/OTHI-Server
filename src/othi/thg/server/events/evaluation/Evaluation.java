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
import java.util.Set;
import java.util.HashSet;
import java.text.*;

public abstract class Evaluation {

    private static enum HighlyFrequentWord {

        A, AND, ARE, AT, BE, BUT, BY, FOR, FROM, HAVE, I, IN, IT, NOT, OF, ON, OR, THAT, THE, THEY, THIS, TO, WITH, WHICH
    }

    public abstract int getMark(Set<String> answerStemmers);

    public String removeHighlyFrequentWord(String textData) {
        textData = textData.substring(0, textData.length() - 1).toUpperCase();

        // Boundary define
        BreakIterator boundary = BreakIterator.getWordInstance();
        boundary.setText(textData);

        StringBuffer temp = new StringBuffer();

        int start = boundary.first();

        for (int end = boundary.next(); end != BreakIterator.DONE;
                start = end, end = boundary.next()) {
            String word = textData.substring(start, end);
        
            if (binarySearch(word) < 0) {
                temp.append(word);
            }

            if (end + 1 < textData.length() - 1) {
                start = end;
            }
        }

        return temp.toString().trim().toLowerCase();
    }

    private int binarySearch(String key) {
        HighlyFrequentWord[] hWords = HighlyFrequentWord.values();

        int first = 0;
        int upto = hWords.length;

        while (first < upto) {
            int mid = (first + upto) / 2;  // Compute mid point.
            if (key.compareTo(hWords[mid].toString()) < 0) {
                upto = mid;       // repeat search in bottom half.
            } else if (key.compareTo(hWords[mid].toString()) > 0) {
                first = mid + 1;  // Repeat search in top half.
            } else {
                return mid;       // Found it. return position
            }
        }
        return -(first + 1);      // Failed to find key
    }

    /** Test program for demonstrating the Stemmer.  It reads text from a
     * a list of files, stems each word, and writes the result to standard
     * output. Note that the word stemmed is expected to be in lower case:
     * forcing lower case must be done outside the Stemmer class.
     * Usage: Stemmer file-name file-name ...
     */
    public Set<String> getStemmers(String text) {
        Stemmer stemmer = new Stemmer();
        
        Set<String> stemmers = new HashSet<String>();

        char[] textChars = new char[text.length() + 1];

        text.getChars(0, text.length(), textChars, 0);

        char[] w = new char[text.length() + 1];

        int i = 0;
        while (i < text.length() + 1) {
            int ch = textChars[i];
            ++i;
            if (Character.isLetter((char) ch)) {
                int j = 0;
                while (true) {
                    ch = Character.toLowerCase((char) ch);
                    w[j] = (char) ch;
                    if (j < text.length()) {
                        j++;
                    }
                    ch = textChars[i];
                    ++i;
                    if (!Character.isLetter((char) ch)) {
                        /* to test add(char ch) */
                        for (int c = 0; c < j; c++) {
                            stemmer.add(w[c]);
                        }

                        /* or, to test add(char[] w, int j) */
                        /* stemmer.add(w, j); */

                        stemmer.stem();
                        {
                            String u;

                            /* and now, to test toString() : */
                            u = stemmer.toString();

                            /* to test getResultBuffer(), getResultLength() : */
                            /* u = new String(stemmer.getResultBuffer(), 0, stemmer.getResultLength()); */

                            stemmers.add(u);
                        }
                        break;
                    }
                }
            }
            if (ch < 0) {
                break;
            }
//            stemmers.add(String.valueOf((char)ch));
        }
        return stemmers;
    }

}
