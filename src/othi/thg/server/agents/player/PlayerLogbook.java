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

package othi.thg.server.agents.player;

/**
 *
 * @author Dong Won Kim
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.io.Serializable;

import othi.thg.server.auth.DataBase;
import othi.thg.server.auth.THGIdentity;
import othi.thg.server.auth.THGPassword;
import othi.thg.server.logbooks.*;

// import java.nio.ByteBuffer;

import com.sun.sgs.app.AppContext;
import com.sun.sgs.app.DataManager;
import com.sun.sgs.app.ManagedObject;


public class PlayerLogbook extends Logbook implements ManagedObject, Serializable {
    private static final long serialVersionUID =  200907141132L;

    private static final Logger logger = Logger.getLogger(PlayerLogbook.class.getName());   

    public static enum RelationToQuestion {
        Introduction, Sub_topic
    }

    public static enum CategoryMetaDataKey {
        topic, question
    }

    private static long lastNewPostTimeMs;
    private int tickNewPostTimeMs = 5000; //300
    private String metaData = null;

    public String playerName;
    public String blogName;
    public String blogLongId;
    public String blogUrl;
    public String categoryId;

    public PlayerLogbook(String playerName){
        this.playerName = playerName;
        setBlogLongIdAndCategoryId();
        setWebBlogName();
        blogUrl = Logbook.BLOG_BASE_URL + blogLongId + "/";
    }

    public void getCategories() {
        String password = getTHGPassword(playerName).getPassword();
        getCategories(blogUrl, blogLongId, playerName,  password);
    }

    public List<String> getRecentPosts() {
        String password = getTHGPassword(playerName).getPassword();
        return getRecentPosts(blogUrl, categoryId, playerName, password);
    }

    public void getPost(String postid) {
        String password = getTHGPassword(playerName).getPassword();
        getPost(blogUrl, postid, playerName, password);
    }

    public String newPost(String title, String comment){
        String password = getTHGPassword(playerName).getPassword();
        return newPost(blogUrl, categoryId, title, comment, playerName, password);
    }

    private void setBlogLongIdAndCategoryId(){
        String webBlogLongId = null;
        String blogCategoryId = null;
    	try {
    		// Attempt to connect to the database
            Connection conn = DataBase.getConnection();

            // Build an SQL query that will locate the correct username / password record
            String query = "SELECT " + DataBase.COURSE_ID_COLUMN_NAME + "," + DataBase.BLOG_CATEGORY_ID_COLUMN_NAME +
                    " FROM " + DataBase.OTHI_USER_TABLE_NAME +
                 " WHERE " + DataBase.OTHI_USER_COLUMN_NAME + " = ?";
            PreparedStatement stmt = conn.prepareStatement( query );
            stmt.setString(1, playerName);
            logger.info("Query: " + stmt.toString());

            // This line applies your SQL query to the database
            // The database returns the results as a ResultSet
            ResultSet results = stmt.executeQuery();

            // Course id is the blog long id.
            if( results.next() ) { // results.next() returns true if there is at least one result
                webBlogLongId = results.getString( DataBase.COURSE_ID_COLUMN_NAME );
                blogCategoryId = results.getString( DataBase.BLOG_CATEGORY_ID_COLUMN_NAME );
            }
            stmt.close();
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.log(Level.SEVERE, "MY SQL NOT STARTED!", e);
        } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL ERROR!", e);
        }

        blogLongId = webBlogLongId;
        categoryId = blogCategoryId;
    }


    private void setWebBlogName(){
        String webBlogName = null;
    	try {
    		// Attempt to connect to the database
            Connection conn = DataBase.getConnection();

            // Build an SQL query that will locate the correct course name
            String query = "SELECT " + DataBase.COURSE_NAME_COLUMN_NAME + " FROM " + DataBase.COURSE_TABLE_NAME +
                 " WHERE " + DataBase.COURSE_ID_COLUMN_NAME + " = ?";
            PreparedStatement stmt = conn.prepareStatement( query );
            stmt.setString(1, blogLongId);
            logger.info("Query: " + stmt.toString());

            // This line applies your SQL query to the database
            // The database returns the results as a ResultSet
            ResultSet results = stmt.executeQuery();

            // Course id is the blog long id.
            if( results.next() ) { // results.next() returns true if there is at least one result
                webBlogName = results.getString( DataBase.COURSE_NAME_COLUMN_NAME );
            }
            stmt.close();
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.log(Level.SEVERE, "MY SQL NOT STARTED!", e);
        } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL ERROR!", e);
        }
        // Return the password, or null if no records were returned by the database
        blogName = webBlogName;
    }

    private THGPassword getTHGPassword(String userName) {
        DataManager dm = AppContext.getDataManager();
        return (THGPassword) dm.getBinding(THGIdentity.IDENTITY + userName);
    }

    public void setNewBlogPost(String subject, String comment, RelationToQuestion relation) {
        long currentTimeMs = System.currentTimeMillis();
        if ( currentTimeMs - lastNewPostTimeMs > tickNewPostTimeMs) {
            logger.info("currentTime : " + currentTimeMs + ", lastNewPostTime : " + lastNewPostTimeMs);
            String sRelation = null;
            if (relation.equals(RelationToQuestion.Sub_topic)) {
                sRelation = "Sub-topic";
            } else {
                sRelation = relation.toString();
            }
            String title = subject + " - " + sRelation.toLowerCase() + " by OTHI ";
            newPost(title, comment);
            PlayerLogbook.lastNewPostTimeMs = currentTimeMs;
        }
    }

    public void setMetaData(CategoryMetaDataKey key, String value) {
        if (metaData == null) {
            metaData = value;

            try {
                // Attempt to connect to the database
                Connection conn = DataBase.getConnection();

                // Build a select SQL query that will locate the correct meta-data value
                String query = " SELECT " + DataBase.CATEGORY_METADATA_ID_COLUMN_NAME +
                               " FROM " + DataBase.BLOG_CATEGORYMETADATA_TABLE_NAME +
                               " WHERE " + DataBase.CATEGORY_ID_COLUMN_NAME + " = ?" +
                               " AND " + DataBase.METADATA_KEY_COLUMN_NAME + " = ?";
                PreparedStatement stmt = conn.prepareStatement( query );
                stmt.setString(1, categoryId);
                stmt.setString(2, key.toString());
                logger.info("Select Query: " + stmt.toString());

                // This line applies your SQL query to the database
                // The database returns the results as a ResultSet
                ResultSet results = stmt.executeQuery();

                // If it has a value, update it. If not, insert it.
                if( results.next() ) { // results.next() returns true if there is at least one result
                    String categoryMetadataId = results.getString( DataBase.CATEGORY_METADATA_ID_COLUMN_NAME );
                    // Build an update SQL query that update the cmeta-data value
                    query = "UPDATE " + DataBase.BLOG_CATEGORYMETADATA_TABLE_NAME +
                            " SET " + DataBase.METADATA_VALUE_COLUMN_NAME + " = ?" +
                         " WHERE " + DataBase.CATEGORY_METADATA_ID_COLUMN_NAME + " = ?";
                    stmt = conn.prepareStatement( query );
                    stmt.setString(1, value);
                    stmt.setString(2, categoryMetadataId);
                    logger.info("Update Query: " + stmt.toString());

                    int count = stmt.executeUpdate ();

                    logger.info("Update Count: " + count);
                } else {
                    // Build an update SQL query that update the cmeta-data value
                    query = "INSERT INTO " + DataBase.BLOG_CATEGORYMETADATA_TABLE_NAME +
                            " VALUES ( ?, ?, ?, NULL)";
                    stmt = conn.prepareStatement( query );
                    stmt.setString(1, categoryId);
                    stmt.setString(2, key.toString());
                    stmt.setString(3, value);

                    logger.info("Insert Query: " + stmt.toString());

                    int count = stmt.executeUpdate ();

                    logger.info("Insert Count: " + count);
                }
                
                stmt.close();
            } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                    logger.log(Level.SEVERE, "MY SQL NOT STARTED!", e);
            } catch (SQLException e) {
                    logger.log(Level.SEVERE, "SQL ERROR!", e);
            }

            metaData = null;
        }
    }
}