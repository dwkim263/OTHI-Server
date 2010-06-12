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

package othi.thg.server.logbooks;

/**
 *
 * @author Dong Won Kim
 */
import java.net.MalformedURLException;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;
import java.net.URL;

//import java.nio.ByteBuffer;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public abstract class Logbook  {

    private static final Logger logger = Logger.getLogger(Logbook.class.getName());
    
    public static final String BLOG_BASE_URL = "http://localhost/blojsom/xmlrpc/";
    public static final String APPL_KEY ="IGNORE";  // IGNORED BY BLOJSOM
    
    private XmlRpcClientConfigImpl xmlrpcConfig = new XmlRpcClientConfigImpl();
    private XmlRpcClient xmlRpcClient = new XmlRpcClient();

    protected void getCategories(String blogUrl, String blogLongId, String playerName,  String password) {

        setXmlrpcConfig(blogUrl);

        HashMap cats = null;

        try {
            Object[] params = new Object[] { blogLongId, playerName, password };
            cats = (HashMap) xmlRpcClient.execute("metaWeblog.getCategories", params);
        } catch (XmlRpcException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (cats != null) {
            Object[] categories = cats.values().toArray();
            for (int i=0; i < categories.length; i++) {
                HashMap category = (HashMap) categories[i];
                Set keys = category.keySet();
                Iterator it = keys.iterator();
                while (it.hasNext()) {
                    Object key = it.next();
                    logger.info(key.toString() + ":" + category.get(key));      
                }
            }
        }
    }

    protected List<String> getRecentPosts(String blogUrl, String categoryId, String playerName, String password) {

        setXmlrpcConfig(blogUrl);

        List<String> postids = new LinkedList<String>();
        Object[] returnedObj = null;

        try {
            Object[] params = new Object[] { categoryId, playerName, password, 10 };
            returnedObj = (Object[]) xmlRpcClient.execute("metaWeblog.getRecentPosts", params);

        } catch (XmlRpcException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (returnedObj != null) {
            for (Object o : returnedObj) {
    //                System.out.println(" Type : " + o.getClass().getCanonicalName());

                Map map = (Map) o;
                for (Object key : map.keySet()) {
                    if (key.toString().equalsIgnoreCase("postid")) {
                        postids.add(map.get(key).toString());
                    }
                    logger.info("- " + key + " : " + map.get(key));
                }
            }
        }
        return postids;
    }

    protected void getPost(String blogUrl, String postid, String playerName, String password) {

        setXmlrpcConfig(blogUrl);

        HashMap postdetail = null;

        try {
            Object[] params = new Object[] { postid, playerName, password };
            postdetail = (HashMap) xmlRpcClient.execute("metaWeblog.getPost", params);
        } catch (XmlRpcException ex) {
            logger.log(Level.SEVERE, null, ex);
        }

        if (postdetail != null) {
            Collection col = postdetail.values();
            Iterator it = col.iterator();
            while (it.hasNext()) {
                logger.info(it.next().toString());
            }
        }
    }

    protected String newPost(String blogUrl, String categoryId, String title, 
                             String comment, String playerName, String password){

        setXmlrpcConfig(blogUrl);

        String newpostid = null;

        try {
           Vector params = new Vector();
           params.addElement(categoryId); //Category ID
           params.addElement(playerName);
           params.addElement(password);

           Hashtable hashtable = new Hashtable();
           hashtable.put("title", title );
           hashtable.put("description", comment);

           long currentTime = System.currentTimeMillis();
           hashtable.put("dateCreated", new Date(currentTime));

           params.addElement(hashtable);
           params.add( new Boolean(true)); //true몇 공개 false면 비공개

           newpostid = (String) xmlRpcClient.execute(xmlrpcConfig, "metaWeblog.newPost", params);

        } catch (XmlRpcException ex) {
            logger.log(Level.SEVERE, null, ex);
        /*
        } catch (Exception exception) {
            System.err.println("newpost: " + exception.toString());
        */
        }

      return newpostid;
    }

    protected boolean editPost(String blogUrl, String postid, String playerName, String password) throws Exception{

        setXmlrpcConfig(blogUrl);

        Boolean result = Boolean.FALSE;

        Vector params = new Vector();
        params.addElement(postid);
        params.addElement(playerName);
        params.addElement(password);

        Hashtable hashtable = new Hashtable();
        hashtable.put("title", "Test" );
        hashtable.put("description", "This is a MetaWeblog test1111.");

        long currentTime = System.currentTimeMillis();
        hashtable.put("dateCreated", new Date(currentTime));

        params.addElement(hashtable);
        params.add( new Boolean(true)); //true몇 공개 false면 비공개

        result = (Boolean) xmlRpcClient.execute("metaWeblog.editPost", params);

        return result.booleanValue();
    }

    protected boolean deletePost(String blogUrl, String appkey, String postid, String playerName, String password) {

        setXmlrpcConfig(blogUrl);

        Boolean result = Boolean.FALSE;

        try {
            Vector params = new Vector();
            params.addElement(appkey);
            params.addElement(postid);
            params.addElement(playerName);
            params.addElement(password);
            params.add( new Boolean(true)); //true몇 공개 false면 비공개

            result = (Boolean) xmlRpcClient.execute("metaWeblog.deletePost", params);
        } catch (XmlRpcException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return result.booleanValue();
    }

    protected String newMediaObject(String blogUrl, String categoryId, String playerName, String password){
        setXmlrpcConfig(blogUrl);

        String url = null;
        HashMap returnObject = null;

        try {
        Vector params = new Vector();
        params.addElement(categoryId); //Category ID
        params.addElement(playerName);
        params.addElement(password);

        Hashtable hashtable = new Hashtable();
        hashtable.put("name", "C:/Users/Steve/Documents/My Develop/thg/data/gui/armor-slot.png" );
        hashtable.put("type", "image/png");

        byte[] bytes = new byte[280];
        /*
        Need a function to get the media file into bytes.
        */
        hashtable.put("bits", bytes);

        params.addElement(hashtable);

        returnObject = (HashMap) xmlRpcClient.execute(xmlrpcConfig, "metaWeblog.newMediaObject", params);

        } catch (XmlRpcException ex) {
            logger.log(Level.SEVERE, null, ex);
        /*
        } catch (Exception exception) {
        System.err.println("newMediaObject: " + exception.toString());
        */
        }

        if (returnObject != null) {
            url = returnObject.get("url").toString();
        }
        return url;
    }


    private void setXmlrpcConfig(String blogUrl) {

        try {
            xmlrpcConfig.setServerURL(new URL(blogUrl));
            xmlRpcClient.setConfig(xmlrpcConfig);
        } catch (MalformedURLException ex) {
            logger.log(Level.SEVERE, "Wrong URL", ex);
        }
    }
}