/**
 * Copyright (c) 2008-2015, Massachusetts Institute of Technology (MIT)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package edu.mit.ll.nics.common.geoserver.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.commons.codec.binary.Base64;
import java.util.HashMap;

/**
 * API for interaction with geoserver and geowebcache REST interfaces
 * this is the super class that contains all the http methods
 * @author LE22005
 */
public class SuperGeoServer {

    private static final Logger log = Logger.getLogger(GeoServer.class.getSimpleName());
    private final String serverUrl; //url for the server ie http://hostname:8080
    private String username; //username for REST access
    private String password; //password for REST access
    /**Rest Parameters*/
    public final String METHOD_DELETE = "DELETE";
    public final String METHOD_GET = "GET";
    public final String METHOD_POST = "POST";
    public final String METHOD_PUT = "PUT";
    public final Integer GET_SUCCESS = 200;
    public final Integer POST_SUCCESS = 201;
    public final Integer PUT_SUCCESS = 200;
    public final Integer DELETE_SUCCESS = 200;

    public SuperGeoServer(String _url, String username, String password) {
        this.serverUrl = checkUrl(_url);
        this.username = username;
        this.password = password;
    }

    /**
     * @return The rest URL
     */
    public String getUrl() {
        return serverUrl;
    }

    /**
     * Check that serverUrl url is valid
     * @param url used in constructor
     * @return valid url, if input is invalid
     */
    private String checkUrl(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        if (!url.endsWith("/rest")) {
            url.concat("/rest");
        }
        return url;
    }

    /**
     * Private HTTPcall function
     * @param appendUrl rest endpoint to append to geoserver url
     * @param method GET,POST, PUT, or DELETE
     * @param content data to post or put
     * @param contentType mime type of data
     * @return connnection object
     * @throws MalformedURLException
     * @throws IOException 
     */
    protected HttpURLConnection httpCall(String appendUrl, String method, String content, String contentType) throws MalformedURLException, IOException {
        return httpCall(appendUrl, method, content, contentType, new HashMap<String, String>());
    }

    protected HttpURLConnection httpCall(String appendUrl, String method, String content, String contentType, Map<String, String> params)
            throws MalformedURLException, IOException {
        //Collect output data
        boolean doOut = !method.equals(METHOD_DELETE) && content != null;
        String link = serverUrl + appendUrl;
        //Replace spaces in link
        link = link.replaceAll(" ","%20");
        URL url = new URL(link);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(doOut);
        if (contentType != null && contentType.length() > 0) {
            connection.setRequestProperty("Content-type", contentType);
            connection.setRequestProperty("Content-Type", contentType);
        }
        for (String key : params.keySet()) {
            connection.setRequestProperty(key, params.get(key));
        }
        String authString = username + ":" + password;
        String authEncString = new String(Base64.encodeBase64(authString.getBytes()));
        connection.setRequestMethod(method);
        connection.setRequestProperty("Authorization", "Basic " + authEncString);
        connection.connect();
        if (connection.getDoOutput()) {
            Writer writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(content);
            writer.flush();
            writer.close();
        }
        return connection;
    }

    /**
     * Extract response string from connection object
     * @param connection
     * @return response string
     * @throws IOException 
     */
    protected String responseString(HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        //TODO Add error handling
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * Extract error string from connection object
     * @param connection
     * @return response string
     * @throws IOException 
     */
    protected String errorString(HttpURLConnection connection) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        StringBuilder sb = new StringBuilder();
        String line = null;
        //TODO Add error handling
        while ((line = br.readLine()) != null) {
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    /**
     * Rest GET method
     * @param appendUrl url of rest object to get
     * @param contentType format to retrieve it in
     * @return String of object in specified format
     */
    protected String restGet(String appendUrl, String contentType) {
        try {
            HttpURLConnection connection = httpCall(appendUrl, METHOD_GET, null, contentType);
            if (connection.getResponseCode() == GET_SUCCESS) {
                log.log(Level.INFO, "Succesful GET from {0}", serverUrl.concat(appendUrl));
                String response = responseString(connection);
                return response;
            } else {
                log.warning(errorString(connection));
                return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Rest POST method
     * @param appendUrl url to post object to
     * @param content string representation of object
     * @param contentType object type (xml or json)
     * @param params map off additional parameters to set in url
     * @return whether post was successful
     */
    protected boolean restPost(String appendUrl, String content, String contentType) {
        return restPost(appendUrl, content, contentType, new HashMap<String, String>());
    }

    protected boolean restPost(String appendUrl, String content, String contentType, Map<String, String> params) {
        try {
            HttpURLConnection connection = httpCall(appendUrl, METHOD_POST, content, contentType, params);
            if (connection.getResponseCode() == POST_SUCCESS || connection.getResponseCode() == GET_SUCCESS) {
                log.log(Level.INFO, "Succesful POST to {0}{1}", new Object[]{serverUrl, appendUrl});
                return true;
            } else {
                log.warning(errorString(connection));
                return false;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Rest PUT method
     * @param appendUrl url to PUT object to
     * @param content string representation of object
     * @param contentType object type (xml or json)
     * @return whether PUT was successful
     */
    protected boolean restPut(String appendUrl, String content, String contentType) {
        try {
            HttpURLConnection connection = httpCall(appendUrl, METHOD_PUT, content, contentType);
            if (connection.getResponseCode() == PUT_SUCCESS) {
                log.log(Level.INFO, "Succesful PUT to {0}{1}", new Object[]{serverUrl, appendUrl});
                return true;
            } else {
                log.warning(errorString(connection));
                return false;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Rest DELETE Function
     * @param appendUrl url of object to delete
     * @return 
     */
    protected boolean restDelete(String appendUrl) {
        try {
            HttpURLConnection connection = httpCall(appendUrl, METHOD_DELETE, null, null);
            if (connection.getResponseCode() == DELETE_SUCCESS) {
                log.log(Level.INFO, "Succesful DELETE of {0}{1}", new Object[]{serverUrl, appendUrl});
                return true;
            } else {
                log.warning(errorString(connection));
                return false;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * GetList
     * @param objectType object to get list of ie layer, datastore, workspace, featuretype
     * @return gets a list of the objects from the URL
     */
    protected List<String> getList(String url, String objectType) {
        List<String> names = new ArrayList<String>();
        try {
            JSONObject response = new JSONObject(restGet(url, "application/json"));
            if (response.has(objectType + "s") && !response.get(objectType + "s").equals("") && response.getJSONObject(objectType + "s").has(objectType)) {
                JSONArray layers = response.getJSONObject(objectType + "s").getJSONArray(objectType);
                for (int i = 0; i < layers.length(); i++) {
                    names.add(layers.getJSONObject(i).getString("name"));
                }
            }
        } catch (JSONException ex) {
            Logger.getLogger(SuperGeoServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return names;
    }
}
