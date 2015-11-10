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
package edu.mit.ll.nics.common.rabbitmq.client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitConsumer {
	
	private Logger log = Logger.getLogger(RabbitConsumer.class);
	
	private static String CMPT_NAME = "rabbitConsumer";
		
	private transient ConnectionFactory factory = null;	
	private transient Connection conn;

	private List<Thread> workers;

	private String host;
	private int port;	
	private String username;
	private String password;
	private String queue;
	private String exchange;
	
	private String THREAD_PREFIX = "";
				
	private ArrayList<String> messages = null;	
	
	/**
	 * 
	 */
	public RabbitConsumer(String username, String password, String host, int port, String exchange, String queue, List<String> topics){				
		
		this.workers = new ArrayList<Thread>();
		
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
		this.exchange = exchange;
		this.queue = queue;
		
		boolean initConnStatus = false;
		
		log.info("Starting " + CMPT_NAME + " with:"+
			"\n\thost="+host +
			"\n\tport="+port +
			"\n\texchange="+exchange +
			"\n\tqueue="+queue +
			"\n\tuser:=" + username + "\n");
		
		this.messages = new ArrayList<String>();
			
		THREAD_PREFIX = CMPT_NAME + ":" + username + "-";
			
		initConnStatus = initConn();
		
		if(!initConnStatus){
			throw new InstantiationError("could not create rabbit connection");
		}
		
		for (String topic : topics) {
			subscribe(topic);
		}
	}

	
	public boolean subscribe(String topic) {
		return initNewRunner(topic);
	}
	
	public boolean unsubscribe(String topic) {
		
		if(workers != null){		
			for(Thread t : workers){
				//if( t.getName().equals(topic) ) {
				if( isTopicEqual(topic, t.getName()) ) {
					try{
						t.interrupt();
						Thread.sleep(100);					
						
						// TODO: verify the thread is dead before removing
						if(!isThreadAlive(topic)){
							workers.remove(t);
							return true;
						}
						else{
							return false;
						}
						
					}catch(InterruptedException ie){
						log.error("InterruptedException while trying to ubsubscribe from topic: " + topic, ie);
						Thread.currentThread().interrupt();
						return false;
					}catch(Exception e){
						log.error("Caught unhandled exception while trying to unsubscribe from topic: " + topic, e);
						return false;
					}
				}
			}		
			return true;
			
		}
		else{
			return true;
		}
	}
	
	/**
	 * Checks to see if the topic is still in workers, and if it's active.  If not,
	 * the topic is unsubscribed, and subscribed to. 
	 * 
	 * @param topic The topic being resubscribed to
	 */
	public boolean resubscribe(String topic) {
		
		if(!isThreadAlive(topic)) {
			boolean status = unsubscribe(topic);
			log.debug("resubscribe: thread status: " + status);
			if(status) {				
				return subscribe(topic);
			}else{
				return status;
			}
			
		}else{
			return true;
		}
		
	}
	
	/**
	 * Checks to see if the worker thrad for this given topic is still
	 * alive and running. 
	 * 
	 * @param topic The topic being checked
	 * @return True if the topic thread is active, false if not
	 */
	private boolean isThreadAlive(String topic) {
		if(workers != null){
			for(Thread t : workers) {
				//if(t.getName().equals(topic)){
				if( isTopicEqual(topic, t.getName()) ) {
					try{
						if(t.isInterrupted() || !t.isAlive()) {						
							return false;
						}else{
							return true;
						}
						
					}catch(Exception e){
						log.debug("Caught unhandled exception while checking if topic ("+topic+") is alive", e);
					}
				}
			}
		}
		else{
			log.debug("workers list is null -- shouldn't be");
		}
		return false;
	}
	
	/**
	 * Starts up a new consumer for a particular topic.  
	 * 
	 * TODO: - Meant to have subscribe called,
	 *       where we can maybe validate the 
	 *       topic before subscribing?
	 *       - Maybe return a string for multiple return info, like
	 *         didn't subscribe due to one already subscribing...which isn't a failure exactly.
	 *  
	 * 
	 * @param topic The collaboration room/topic/routing key to listen to
	 */
	private boolean initNewRunner(String topic){
		
		if(workers == null){
			workers = new ArrayList<Thread>();
		}
		
		for(Thread thread : workers){		

			if( isTopicEqual(topic, thread.getName()) ) {
				log.warn("A consumer thread is already listening to the topic: " + topic);
				return true;
			}
		}
		
		RabbitConsumerWorker worker = new RabbitConsumerWorker(this, this.conn, this.queue, this.exchange, topic);
	
		Thread t = new Thread(worker);
		t.setName(getFullThreadName(topic));
		t.start();
		workers.add(t);
		
		log.debug("Subscribed user '"+username+"' to topic: " + topic);
		return true;
	}	
	
	
	/**
	 * Initialize the connection to the AMQP bus
	 */
	public boolean initConn(){
		boolean result = true;
		try{
					
			log.debug("In initConn...");
						
			if(factory == null){
				factory = new ConnectionFactory();
				factory.setHost(host);
				factory.setPort(port);
				factory.setUsername(this.username);
				factory.setPassword(this.password);
			}

			conn = factory.newConnection();
			
			log.debug("\tconn successfully created.");
		}catch(IOException ioe){
			log.error("Error initializing connection: will stop consuming", ioe);
			result = false;
		}catch(Exception e){
			log.error("Unhandled error initializing connection: will stop consuming", e); 
			result = false;
		}
		return result;
	}
				
	/**
	 * Re-initializes connection and channel with current
	 * settings.
	 * 
	 * TODO: Assumes conn and channel are disconnected
	 */
	public void reInitConnAndChannel() {
		conn = null;
		log.info("re-initializing connection...");
		
		initConn();
		
		for (Thread worker : workers) {
			subscribe(getTopicFromThreadName(worker.getName()));
		}
	}
	
	public void addMessage(String message){
		// TODO: place some error checking here
		//
		log.debug("Adding message to internal list: \n\t" + message);
		synchronized(messages){
			messages.add(message);
		}
	}
		
	public List<String> getLatestMessages(){
			
		ArrayList<String> tempMessages = null;
		
		// check if the conn is dead
		if(!isConnected()){
			reInitConnAndChannel();
		}
		
		
		synchronized(messages){
			if(messages == null){
				// messages should never be null here, but saw it happen, possibly when an old
				// client is still online after a JBoss reboot.  Need to possibly cause a full
				// re-init here... Ideally they client would "know" this happened, and force a
				// logout.
				log.debug("messages list was empty!");
				messages = new ArrayList<String>();
			}
		}
		
		if(tempMessages == null){
			tempMessages = new ArrayList<String>();
		}
		
		synchronized(messages){
			if(!messages.isEmpty()){
				
				log.debug("Copying " + messages.size() + " messages to return...");
							
				tempMessages = (ArrayList<String>) messages.clone();
				
				if(messages.size() == tempMessages.size()){
					log.debug("Message list sizes are equal, clearing original");
					messages.clear();
				}else{
					log.debug("Number of messages not equal...");
				}			
			}else{
				log.debug("No messages to return");
			}
		}
		
		log.debug("Returning message list of size: " + tempMessages.size());
				
		return tempMessages;
	}
	
	/**
	 * Forms a long "name" string to be set as a thread's .getName() value, for 
	 * filtering/thread identification purposes.
	 * 
	 * @param topic Topic which will be appended to THREAD_PREFIX for the full thread name
	 * @return The full thread name to set on the thread
	 */
	private String getFullThreadName(String topic) {
		return THREAD_PREFIX + "-" + topic;
	}
		
	/**
	 * Each thread name is given a prefix of this bean's name, and the name
	 * of the user logged in.  So when a topic comes in to be unsubscribed,
	 * it'll only contain the topic, so this comparison adds the prefix so
	 * they'll match.
	 * 
	 * @param topic The string of just the topic
	 * @param threadName The thread name, which contains the THREAD_PREFIX and the topic
	 * @return true if the THREAD_PREFIX + topic equals the threadName, false otherwise
	 */
	private boolean isTopicEqual(String topic, String threadName) {
		return ((THREAD_PREFIX + topic).equals(threadName)) ? true : false;
	}
	
	private String getTopicFromThreadName(String name){
		return name.replace(THREAD_PREFIX, "");
	}
	
		
	public boolean isConnected(){
		if(conn != null){
			if(conn.isOpen()){
				return true;
			}
		}
		return false;		
	}
		
	private void stopWorkers()
	{
		if(workers != null){
			log.debug("Stopping all consumer threads for user '"+username+"'...");
			List<Thread> workersToRemove = new ArrayList<Thread>();
			try{
				for(Thread t : workers){
					log.debug("\tInterrupting worker thread: " + t.getName());
					t.interrupt();
					workersToRemove.add(t);
					
					//Thread.sleep(100);
				}
			}catch(Exception e){
				log.error("Caught unhandled exception attempting to stop consumer threads: " + e.getMessage(), e);
			}
			finally{
				for (Thread worker : workersToRemove) {
					workers.remove(worker);
				}
				workers = null;
			}
		}
	}
		
	
	public void destroy(){
		log.debug("destroying '"+CMPT_NAME+"' component...");
				
		stopWorkers();
		try {
			Thread.sleep(200);
		} catch (InterruptedException e1) {
			log.debug("sleep 1: ",e1);
			
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			Thread.currentThread().interrupt();
		}
				
						
		if(null != conn && conn.isOpen()){
			try{
				conn.close();
				conn = null;
				
			}catch(IOException ioe){
				log.error("IOException while destroying "+ CMPT_NAME +" component: unexpected shutdown: ", ioe);
			}catch(ShutdownSignalException sse){				
				log.debug("Shutdown Exception while destroying "+ CMPT_NAME + " component: initiated clean shutdown.");
			}catch(Exception e){
				log.error("Caught unhandled exception while destroying "+ CMPT_NAME + 
						" component: ", e);
			}
		}
		
		log.info("\tdestroyed.");
	}

}
