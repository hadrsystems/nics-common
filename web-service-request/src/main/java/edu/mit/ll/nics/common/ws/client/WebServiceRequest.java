/**
 * Copyright (c) 2008-2016, Massachusetts Institute of Technology (MIT)
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
package edu.mit.ll.nics.common.ws.client;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class WebServiceRequest<T> {
	
	private Logger log = LoggerFactory.getLogger(WebServiceRequest.class);

	private String GET = "GET";
	private String POST = "POST";
	
	/** Make a service request 
	 * @param String webServiceUrl
	 * @param Map headerOptions
	 * @return Object - parsed based on child implementations
	 */
	public T getRequest(final String webServiceUrl, final Map headerOptions){
		return this.getRequest(webServiceUrl, headerOptions, GET);
	}
	
	public T getRequest(final String webServiceUrl, final Map headerOptions, final String requestMethod){
		T result = null;
		try{
			log.debug("Attempting connection to " + webServiceUrl);
			URL url = new URL(webServiceUrl);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setConnectTimeout(30000);
		    connection.setReadTimeout(30000);
		    //load headerOption
		    if(headerOptions != null){
				for(Iterator itr=headerOptions.keySet().iterator(); itr.hasNext();){
					String key = (String) itr.next();
					String value = (String)headerOptions.get(key);
					connection.setRequestProperty(key, value);
				}
			}

		    connection.setRequestMethod(requestMethod);
			connection.connect();
		    log.debug("Connected to: " + webServiceUrl);

		    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
		    	result = this.parseRequest((InputStream) connection.getInputStream());
			}
			else {
				log.debug("Did not receive a 200 response from upstream server for: " + webServiceUrl);
			}
		    
		    connection.disconnect();
		 }catch(Exception e){
			log.error("Unable to connect to " + webServiceUrl, e);
		}
	    return result;
	}
	
	public T postRequest(final String webServiceUrl, final Map headerOptions, final String jsonPayload){
		T result = null;
		try{
			log.debug("Attempting connection to " + webServiceUrl);
			System.out.println("Attempting connection to " + webServiceUrl);
			URL url = new URL(webServiceUrl);
		    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		    connection.setConnectTimeout(30000);
		    connection.setReadTimeout(30000);
		    //load headerOption
		    if(headerOptions != null){
				for(Iterator itr=headerOptions.keySet().iterator(); itr.hasNext();){
					String key = (String) itr.next();
					String value = (String)headerOptions.get(key);
					connection.setRequestProperty(key, value);
				}
			}
		    
		    connection.setDoInput(true);
            connection.setDoOutput(true);
		    connection.setRequestMethod(POST);
		    connection.setRequestProperty("Content-Type", "application/json");
		    connection.setRequestProperty("Accept", "application/json");
		    
		    
		    DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
            
		    System.out.println("\n... about to write out jsonPayload bytes:\n" + jsonPayload + "\n-------");
		    
		    if(jsonPayload != null && !jsonPayload.isEmpty()) {
		    	dataOutputStream.write(jsonPayload.getBytes());
		    	System.out.println("Wrote " + jsonPayload.getBytes().length + " bytes!");
		    } else {
		    	dataOutputStream.write("".getBytes());
		    	System.out.println("Wrote NO bytes!");
		    }
            
            dataOutputStream.flush();
            dataOutputStream.close();
		    		    		    
			connection.connect();
		    log.debug("Connected to: " + webServiceUrl);

		    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
		    	result = this.parseRequest((InputStream) connection.getInputStream());
			} else {
				log.debug("Did not receive a 200 response from upstream server for: " + webServiceUrl);
				//result = this.parseRequest((InputStream) connection.getInputStream());
				// TODO: try getErrorStream, getInputSTream causes exception
				result = (T)("{\"code\":\"" + connection.getResponseCode() + "\", \"message\":\"" + connection.getResponseMessage() + 
						"\", \"info\":\"" + this.parseRequest((InputStream) connection.getErrorStream()) + "\"}");
			}
		    
		    connection.disconnect();
		 }catch(Exception e){
			log.error("Unable to connect to " + webServiceUrl, e);
		}
	    return result;
	}
	
	
	/** Make a service request 
	 * @param String webServiceUrl
	 * @return Object - parsed based on child implementations
	 */
	public T getRequest(final String webServiceUrl){
		return this.getRequest(webServiceUrl, null, GET);
	}
	
	public T postRequest(final String webServiceUrl, final String jsonData) {
		return this.postRequest(webServiceUrl, null, jsonData);
	}
	
	/** Parse the service response 
	 * @param InputStream is
	 * @return Object - parsed based on child implementations
	 */
	protected abstract T parseRequest(InputStream is);

}