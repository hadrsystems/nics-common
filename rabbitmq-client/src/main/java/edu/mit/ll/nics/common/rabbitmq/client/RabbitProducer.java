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
package edu.mit.ll.nics.common.rabbitmq.client;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitProducer {

	private static final String CMPT_NAME = "rabbitProducer";
		
	private Logger log = Logger.getLogger(RabbitProducer.class);
	
	private String host;
	private int port;
	private String username;
	private String password;
	private String exchange = "amq.topic"; 
	
	transient private Connection conn;
	transient private ConnectionFactory factory = null;
	transient private Channel channel;
		
	public RabbitProducer(String username, String password, String host, int port){
				
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		
		log.info("Starting " + CMPT_NAME + " with:"+
				"\n\t" + username + "@" + host + ":" + port + "\n");
		
		boolean initResult = initConn();
		if(!initResult){
			throw new InstantiationError("could not initialize connection to rabbit server");
		}
	}
	
	public boolean initConn(){
		boolean result = true;
		try{
			log.debug("In initConn...");			
			
			if(factory == null){
				factory = new ConnectionFactory();
				factory.setUsername(this.username);
				factory.setPassword(this.password);
				factory.setHost(host);
				factory.setPort(port);
			}	
			
			conn = null;
			conn = factory.newConnection();
			
			channel = conn.createChannel();
			
			log.debug("\tconn and channel successfully created.");
		}catch(IOException ioe){
			log.error("Error initializing connection: ", ioe);
			result = false;
		}catch(Exception e){
			log.error("Unhandled error initializing connection: ", e);
			result = false;
		}
		
		return result;
	}
	
	public boolean sendMessage(String topic, String message){
		return sendMessage(this.exchange, topic, message);
	}
	
	
	public boolean sendMessage(String exchange, String topic, String message){
	
    	try{    		
    		
    		if(!isConnected()){    			
    			initConn();
    		}
    		
			if (exchange == null || exchange.isEmpty()) {
                exchange = this.exchange;
            }
			
			if(topic == null || topic.isEmpty()){
				topic = "default";
			}
    		
    		log.debug("Sending message (exchange=" + exchange + ", topic=" + topic + "): " + message);
    		channel.basicPublish(exchange, topic, null, message.getBytes());
    		
    	}catch(IOException ioe){
    		log.error("Exception sending message: ", ioe);
    		return false;
    	}catch(Exception e){
    		log.error("Unhandled exception while sending message: ", e);
    		return false;    	
    	}
    	
    	return true;
	}
	
	public boolean isConnected(){
		if(conn != null && channel != null){
			return conn.isOpen() && channel.isOpen();
		}
		return false;
	}
	
	public void destroy(){
		
		log.debug("Destroying '" + CMPT_NAME + "' component");
		
		if(null != conn){
			try{
				if(conn.isOpen()){ conn.close(); }
				conn = null;
				
			}catch(IOException ioe){
				log.error("IOException while destroying "+ CMPT_NAME +" component: ", ioe);				
			}catch(ShutdownSignalException sse){
				log.error("Shutdown Exception while destroying "+ CMPT_NAME + " component: ", sse);
			}catch(Exception e){
				log.error("Caught unhandled exception while destroying " + CMPT_NAME + 
						" component: ", e);
			}
		}
		
		log.info("\tdestroyed.");
	}
}
