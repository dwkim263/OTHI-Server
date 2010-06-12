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

package othi.thg.server.auth;


import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 *
 * @author Steve
 */

public class DataBase {

    private static final Logger logger = Logger.getLogger(DataBase.class.getName());

	//These constants are used to connect to the MySQL database
	private static final String SQL_HOST_NAME      = "localhost";
	private static final String SQL_DATABASE_NAME  = "wss";
	private static final String SQL_USERNAME       = "wss";
	private static final String SQL_PASSWORD       = "Wlss@003";
	public static final String JOS_USER_TABLE_NAME = "jos_users";
	public static final String JOS_USERNAME_COLUMN_NAME = "username";
	public static final String PASSWORD_COLUMN_NAME = "password";

    //OTHI Tables
    public static final String OTHI_USER_TABLE_NAME = "jos_othi_users";
    public static final String COURSE_TABLE_NAME = "jos_othi_courses";
    public static final String OTHI_USER_COLUMN_NAME = "username";
	public static final String COURSE_ID_COLUMN_NAME = "course_id";
	public static final String COURSE_NAME_COLUMN_NAME = "course_name";
	public static final String BLOG_CATEGORY_ID_COLUMN_NAME = "blog_category_id";   
	public static final String BLOG_ID_COLUMN_NAME = "blog_id";

    //Blog Tables;
    public static final String BLOG_CATEGORYMETADATA_TABLE_NAME = "blog_CategoryMetadata";
	public static final String CATEGORY_METADATA_ID_COLUMN_NAME = "category_metadata_id";
	public static final String CATEGORY_ID_COLUMN_NAME = "category_id";
	public static final String METADATA_KEY_COLUMN_NAME = "metadata_key";
	public static final String METADATA_VALUE_COLUMN_NAME = "metadata_value";


    public static Connection getConnection () {
        Connection conn = null;
    	try {
        	/* Set up the correct database driver for MySQL
        	 * NOTE: This driver is the MySQL Connector/J driver, which is NOT part of
        	 * a vanilla Java installation. If you do not have the MySQL Connector/J
        	 * jar file in your classpath, this line will throw a runtime exception.
        	 * See http://www.mysql.com/products/connector/j/ */
    		DriverManager.registerDriver(
            		(Driver)Class.forName("com.mysql.jdbc.Driver").newInstance() );

            // Build the URL we will use to connect to the database
    		String connURL = "jdbc:mysql://" + SQL_HOST_NAME + "/" +
    		SQL_DATABASE_NAME + "?" +
            "&user=" + SQL_USERNAME +
            "&password=" + SQL_PASSWORD;

    		// Attempt to connect to the database
            conn = DriverManager.getConnection( connURL );

        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.log(Level.SEVERE, "MY SQL NOT STARTED!", e);
        } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL ERROR!", e);
        } catch (ClassNotFoundException e) {
                logger.log(Level.SEVERE, "JDBC Driver Not Found", e);
        } catch (InstantiationException e) {
                logger.log(Level.SEVERE, "JDBC Driver Instantiation Error ", e);
        } catch (IllegalAccessException e) {
                logger.log(Level.SEVERE, null, e);
        }

        return conn;
    }
}
