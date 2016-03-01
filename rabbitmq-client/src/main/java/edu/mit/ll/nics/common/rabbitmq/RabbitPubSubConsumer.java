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
package edu.mit.ll.nics.common.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitPubSubConsumer extends RabbitClient {
	
	private QueueingConsumer consumer = null;

	public RabbitPubSubConsumer(String hostname, String exchangeName,
			String[] bindingKeys)
			throws IOException {
		super(hostname);
		initialize(hostname, exchangeName, bindingKeys);
	}
	
	public RabbitPubSubConsumer(String hostname, String exchangeName,
			String[] bindingKeys, String rabbitUsername, String rabbitUserpwd)
			throws IOException {
		super(hostname, rabbitUsername, rabbitUserpwd);
		initialize(hostname, exchangeName, bindingKeys);
	}	
	
	private void initialize (String hostname, String exchangeName,
			String[] bindingKeys)
			throws IOException {
		declareExchange(exchangeName);
		String queueName = getChannel().queueDeclare().getQueue();
		for(String bindingKey : bindingKeys){
			getChannel().queueBind(queueName, exchangeName, bindingKey);
		}
		System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
		consumer = new QueueingConsumer(getChannel());
		getChannel().basicConsume(queueName, true, consumer);		
	}
	
	public RabbitPubSubMsg consume() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {		
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
			String message = new String(delivery.getBody());
			String routingKey = delivery.getEnvelope().getRoutingKey();
			System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
			RabbitPubSubMsg ret = new RabbitPubSubMsg(message, routingKey);
			return ret;
	}
	
	public void destroy() {
		if (consumer != null) {
			consumer = null;
		}
		super.destroy();
	}		
}
