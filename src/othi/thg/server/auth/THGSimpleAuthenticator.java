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

package othi.thg.server.auth;

import java.io.File;
import java.util.Properties;

import javax.security.auth.login.CredentialException;
import javax.security.auth.login.LoginException;

import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sun.sgs.auth.Identity;
import com.sun.sgs.auth.IdentityAuthenticator;
import com.sun.sgs.auth.IdentityCredentials;
import com.sun.sgs.impl.auth.NamePasswordCredentials;

public class THGSimpleAuthenticator implements IdentityAuthenticator {

    private Environment myDbEnvironment = null;
    private Database myDatabase = null;
    
    public THGSimpleAuthenticator(Properties prop){
        File homeDir = new File(prop.getProperty("com.sun.sgs.app.root")+  File.separator + "passwd");
        if (!homeDir.exists()){
            homeDir.mkdirs();
        }
        try {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setAllowCreate(true);
            myDbEnvironment = new Environment(homeDir, envConfig);
//          Open the database. Create it if it does not already exist.
            DatabaseConfig dbConfig = new DatabaseConfig();
            dbConfig.setAllowCreate(true);
            myDatabase = myDbEnvironment.openDatabase(null,  "passwd",  dbConfig); 
        } catch (DatabaseException dbe) {
            dbe.printStackTrace();
        } 
    }
    
    @Override
    public Identity authenticateIdentity(IdentityCredentials credentials) throws LoginException
    {
//      make sure that we were given the right type of credentials
        if (! (credentials instanceof NamePasswordCredentials))  throw new CredentialException("unsupported credentials");
        NamePasswordCredentials namePassCred = (NamePasswordCredentials)credentials;
        String userName = namePassCred.getName().toLowerCase(); // make name case insig.
        String password = new String(namePassCred.getPassword());
        String matchPassword = getPasswordForUser(userName);
        if (matchPassword == null){ // new user
            addPasswordForUser(userName,password);
        } else if (!matchPassword.equals(password)){
            throw new CredentialException("Invalid password");
        }
        return new THGIdentity(userName, password);
    }

    private void addPasswordForUser(String userName, String password) {
        try {
            DatabaseEntry theKey = new DatabaseEntry(userName.getBytes("UTF-8"));
            DatabaseEntry theData = new DatabaseEntry(password.getBytes("UTF-8"));
            myDatabase.put(null, theKey, theData);
        } catch (Exception e) {
            e.printStackTrace();
        } 
        
    }

    private String getPasswordForUser(String userName) {
        try {
            DatabaseEntry theKey = new DatabaseEntry(userName.getBytes("UTF-8"));
            DatabaseEntry theData = new DatabaseEntry();
            if (myDatabase.get(null, theKey, theData, LockMode.DEFAULT) ==
                OperationStatus.SUCCESS) {

                // Recreate the data String.
                byte[] retData = theData.getData();
                String foundData = new String(retData, "UTF-8");
                return foundData;
            } else {
                return null;
            } 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String[] getSupportedCredentialTypes() {
        return new String[] {"NameAndPasswordCredentials"};
    }        
}
