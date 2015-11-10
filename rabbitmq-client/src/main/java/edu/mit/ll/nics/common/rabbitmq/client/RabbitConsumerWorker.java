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

import org.apache.log4j.Logger;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitConsumerWorker implements Runnable{
	
	private Logger log = Logger.getLogger(RabbitConsumerWorker.class);
	
	private Connection conn;
	private QueueingConsumer consumer;
	private Channel channel;
	
	private String queue;
	private String exchange;
	private String topicPattern;
	
	private RabbitConsumer rabbitConsumer;
	
	private boolean keepConsuming = true;
	private boolean channelFailed = false;
		
	
	public RabbitConsumerWorker(RabbitConsumer rabbitConsumer, Connection conn, String queue, 
			String exchange, String topicPattern){
		
		this.rabbitConsumer = rabbitConsumer;
		this.conn = conn;
		this.queue = queue;
		this.exchange = exchange;
		this.topicPattern = topicPattern;
	}
		
	
	/**
	 * Initializes the rabbit channel consumer on currently set 
	 * exchange and topic pattern
	 * 
	 */
	public void initChannelConsumer(){
		try{
			
			if(conn != null && !conn.isOpen()) {
				keepConsuming = false;
				handleDeadTopic();
				channelFailed = true;
				// need to bail out...
				return;
			}
			
			channel = null;
			channel = conn.createChannel();
	
			if (exchange == null || exchange.isEmpty()) {
				exchange = "amq.topic";
			} else {
				if(!exchange.equalsIgnoreCase("amq.topic")){
					channel.exchangeDeclare(exchange, "topic");
				}
			}
			
			if (queue == null || queue.isEmpty()) {
				queue = channel.queueDeclare().getQueue();
			} else {
				channel.queueDeclare(queue, false, true, true, null);
			}
			
			channel.queueBind(queue, exchange, topicPattern);
			
			consumer = null;
			consumer = new QueueingConsumer(channel);					
			
			channel.basicConsume(queue, consumer);
			
			log.debug("Listening to exchange " + exchange + ", pattern " + topicPattern +
					" from queue " + queue);
			
			if(channelFailed){
				channelFailed = false;
				handleDeadTopic();
			}
			
		}catch(IOException ioe){
			log.error("IOException while initializing the channel", ioe);
		}catch(Exception e){
			log.error("Unhandled error re-initializing channel",e);
		}
	}
	
	public void run() {

		initChannelConsumer();

		Envelope envelope = null;
		String body = null;
		Object msgObj = null;
		QueueingConsumer.Delivery delivery;
		
		while (keepConsuming) {
			try{
				if(channel == null || !channel.isOpen()){
					// Possible missed messages here?  amqp should queue these up for
					// this particular subscription?
					log.debug("Channel was closed, informing client...");
					
					handleDeadTopic();
					channelFailed = true;
					keepConsuming = false;
					break;
				}
				
				delivery = null;
				delivery = consumer.nextDelivery(); // NOTE: execution holds here until a message is received
				
				envelope = null;
				envelope = delivery.getEnvelope();
				
				msgObj = null;
				//msgObj = new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(delivery.getBody())).readObject();
				msgObj = new String(delivery.getBody()); 
					
				body = null;
				body = (String) msgObj;

				log.debug(envelope.getRoutingKey() + ": " + body);
						
				if(rabbitConsumer != null){
					rabbitConsumer.addMessage(body);

					// TODO: necessary to send the ack?
					channel.basicAck(envelope.getDeliveryTag(), false);
					
					//Thread.sleep(100);
				}
				else{
					log.error("[" + this.topicPattern + "] Error: rabbitConsumer was null -- ending thread");
					keepConsuming = false;
					Thread.currentThread().interrupt();
				}

			}catch(IOException ioe){
				keepConsuming = false;
				handleDeadTopic();
				log.error("[" + this.topicPattern + "] error consuming messages",ioe);
			}catch(InterruptedException ie){
				keepConsuming = false;
				// Let's assume this was due to being interrupted on purpose
				log.debug(" * Thread was interrupted. Leaving topic: " + this.topicPattern);				
			}catch(ShutdownSignalException sse){
				keepConsuming = false;
				handleDeadTopic();
				log.error("Error while consuming messages: on topic '" + topicPattern + "': " 
						+ sse.getMessage(), sse);				
			}catch(Exception e){
				handleDeadTopic();
				log.debug("Caught unhandled exception while consuming messages on topic '" 
						+ topicPattern + "': " + e.getMessage(), e);				
			}

		} // END while(keepConsuming)
				
		
		try{
			if(channel != null){
				log.debug("Broke out of consuming loop, closing channel for topic '"+topicPattern+"'...");
				channel.close();
			}
			else {
				log.debug("[" + this.topicPattern + "] channel is null");
			}
		}catch(IOException ioe){
			log.error("[" + this.topicPattern + "] Exception closing channel after breaking out of consuming loop: unexpected shutdown: " + ioe.getMessage(), ioe);
		}catch(ShutdownSignalException sse){
			log.debug("[" + this.topicPattern + "] Shutdown Signal Exception: component initiated clean shutdown.");
		}catch(Exception e){
			log.error("[" + this.topicPattern + "] Caught unhandled exception after breaking out of consuming loop: " + e.getMessage(), e);
		}
		
		log.debug("worker closed for '" + topicPattern + "'");
	}
		
	private void handleDeadTopic(){
		if(rabbitConsumer != null){
			rabbitConsumer.resubscribe(this.topicPattern);
		}
	}
	
	public boolean getKeepConsuming(){
		return keepConsuming;
	}
	
	public void setKeepConsuming(boolean keepConsuming){
		this.keepConsuming = keepConsuming;
	}
	
	public String getTopic(){
		return topicPattern;
	}
}
