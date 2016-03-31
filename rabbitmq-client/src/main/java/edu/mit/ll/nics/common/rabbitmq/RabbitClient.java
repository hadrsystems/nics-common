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
package edu.mit.ll.nics.common.rabbitmq;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public abstract class RabbitClient {

	public static final String AMQ_TOPIC = "amq.topic"; 
	
	private Connection connection;

	private Channel channel;

	private String serverHostname;

	private static final List<String> undeclarables =
			Arrays.asList(AMQ_TOPIC);

	protected RabbitClient(String serverHostname) throws IOException {
		initialize(serverHostname, null, null);
	}
	
	protected RabbitClient(String serverHostname, String rabbitUsername,
			String rabbitUserpwd) throws IOException {
		initialize(serverHostname, rabbitUsername, rabbitUserpwd);
	}
	
	private void initialize(String serverHostname, String rabbitUsername,
			String rabbitUserpwd) throws IOException {
		setServerHostname(serverHostname);		
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(serverHostname);
		if (rabbitUsername != null && !rabbitUsername.isEmpty())
			factory.setUsername(rabbitUsername);
		if (rabbitUserpwd != null && !rabbitUserpwd.isEmpty())		
		factory.setPassword(rabbitUserpwd);
		connection = factory.newConnection();
		channel = connection.createChannel();		
	}

	protected Connection getConnection() {
		return connection;
	}

	protected Channel getChannel() {
		return channel;
	}

	public String getServerHostname() {
		return serverHostname;
	}

	protected void setConnection(Connection connection) {
		this.connection = connection;
	}

	protected void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setServerHostname(String serverHostname) {
		this.serverHostname = serverHostname;
	}

	public boolean isDeclaredByRabbit(String name) {
		return undeclarables.contains(name);
	}

	protected void destroy() {
		if (channel != null && channel.isOpen()) {		
			try {
				channel.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				channel = null;				
			}
		}
		if (connection != null && connection.isOpen()) {
			try {
				connection.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				connection = null;
			}
		}
	}

	protected void declareQueue(String queueName) throws IOException {
		if (queueName == null) {
			throw new NullPointerException("Unexpected null \"queueName\" argument.");
		}
		if (queueName.isEmpty()) {
			throw new IllegalArgumentException("Argument \"queueName\" must have a value.");
		}
		if (!isDeclaredByRabbit(queueName)) {
			getChannel().queueDeclare(queueName, false, false, false, null);
		}
	}

	protected void declareExchange(String exchangeName) throws IOException {
		if (exchangeName == null) {
			throw new NullPointerException("Unexpected null \"exchangeName\" argument.");
		}
		if (exchangeName.isEmpty()) {
			throw new IllegalArgumentException("Argument \"exchangeName\" must have a value.");
		}
		if (!isDeclaredByRabbit(exchangeName)) {
			getChannel().exchangeDeclare(exchangeName, "topic", true, true, new HashMap<String, Object>());
		}
	}		
}
