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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;

import com.sun.sgs.auth.Identity;
import com.sun.sgs.auth.IdentityAuthenticator;
import com.sun.sgs.auth.IdentityCredentials;
import com.sun.sgs.impl.auth.NamePasswordCredentials;

/**
 * Straightforward MySQL authenticator for Project Darkstar.
 * This code is based on the custom authenticator from
 * the BunnyHunters project.
 *
 * This authenticator will connect to a specific table in the
 * MySQL database and try to retrieve a password field from a row
 * in that table. Be sure to set the constants at the start
 * of the source code so that the Database, Table, and Column
 * Names match the ones in your MySQL database.
 *
 * @author Chuck Liddell, Dong Won Kim
 *
 */
public class THGAuthenticator implements IdentityAuthenticator {

    private static final Logger logger = Logger.getLogger(THGAuthenticator.class.getName());

  /**
	 * An Authenticator must have a constructor that accepts
	 * a set of Properties.
	 *
	 * @param prop Reference properties (unused in this implementation)
	 */
    public THGAuthenticator(Properties prop){
    	// To double-check your authenticator is being called
    	// -- remove when you are done testing
    	System.out.println("Custom Authenticator instantiated.");
    }

    @Override
    public Identity authenticateIdentity(IdentityCredentials credentials) throws LoginException
    {
        // Check and cast the IdentityCredentials to Name/Password,
    	// which is a format we can deal with. Future PDS versions
    	// will support more IdentityCredentials implementations.
    	if (! (credentials instanceof NamePasswordCredentials))
            throw new CredentialException("Unsupported credentials");
        NamePasswordCredentials npc = (NamePasswordCredentials)credentials;

        // Pull the data that we need from the credentials
        String userName = npc.getName().toLowerCase(); // don't worry about case
        String password = new String(npc.getPassword());

        // Call a method that will check the MySQL database for this user
        String matchPassword = getPasswordForUser(userName);

        if (matchPassword == null){ // This user was not found in the database
            throw new CredentialException("User does not exist");
        }
        else {
            try {
                String[] part = matchPassword.split(":");
                String salt = part[1];

                MessageDigest m = MessageDigest.getInstance("MD5");
                byte  b[] = m.digest(new String(password + salt).getBytes());
                java.math.BigInteger bi = new java.math.BigInteger(1, b);
                String encryptedPasswd = bi.toString(16);
                while (encryptedPasswd.length() < 32)
                encryptedPasswd = "0" + encryptedPasswd;

                encryptedPasswd = encryptedPasswd + ":" + salt;

                if (!matchPassword.equals(encryptedPasswd)){ // User found, but wrong password
                    throw new CredentialException("Invalid password");
                }
            } catch (NoSuchAlgorithmException ex) {
                logger.log(Level.SEVERE, null, ex);
            }            
        }

        // IdentityImpl is an extremely simple class that implements the Identity interface.
        // If we reach this point in the method code we are acknowledging that
        // the user credentials we were given are acceptable.
        //return new IdentityImpl(userName);
        return new THGIdentity(userName, password);
    }

    /**
     * Searches a MySQL database for a record that matches the given username.
     * @param userName The username to search for in the database
     * @return The matching password for the given username, or null if no valid record was found.
     */
    private String getPasswordForUser(String userName) {
        String password = null; // value we will return at the end of the method
    	try {
    		// Attempt to connect to the database
            Connection conn = DataBase.getConnection();

            // Build an SQL query that will locate the correct username / password record
            String query = "SELECT " + DataBase.PASSWORD_COLUMN_NAME + " FROM " + DataBase.JOS_USER_TABLE_NAME +
                 " WHERE " + DataBase.JOS_USERNAME_COLUMN_NAME + " = ?";
            PreparedStatement stmt = conn.prepareStatement( query );
            stmt.setString(1, userName);
            logger.info("Query: " + stmt.toString());

            // The time statements let you know how long your MySQL queries are taking
            long startTime = System.nanoTime();

            // This line applies your SQL query to the database
            // The database returns the results as a ResultSet
            ResultSet results = stmt.executeQuery();
            
            long estimatedTime = System.nanoTime() - startTime;
            logger.info("Execution time: " + estimatedTime / Math.pow(10, 6) + " ms" );

            // Check to see if the ResultSet has any entries
            // If it does, take the first one and grab the password from the 'Password' database column
            if( results.next() ) { // results.next() returns true if there is at least one result
                password = results.getString( DataBase.PASSWORD_COLUMN_NAME );
            }
            
            stmt.close();
        } catch (com.mysql.jdbc.exceptions.jdbc4.CommunicationsException e) {
                logger.log(Level.SEVERE, "MY SQL NOT STARTED!", e);
        } catch (SQLException e) {
                logger.log(Level.SEVERE, "SQL ERROR!", e);
        }
        // Return the password, or null if no records were returned by the database
        return password;
    }

    @Override
    public String[] getSupportedCredentialTypes() {
        return new String[] {"NameAndPasswordCredentials"};
    }
}
