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

import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

public class RabbitQueueConsumer extends RabbitClient {

	private QueueingConsumer consumer = null;

	public RabbitQueueConsumer(String hostname, String queueName) throws IOException {
		super(hostname);
		initialize(hostname, queueName);
	}

	public RabbitQueueConsumer(String hostname, String queueName,
			String rabbitUsername, String rabbitUserpwd) throws IOException {
		super(hostname, rabbitUsername, rabbitUserpwd);
		initialize(hostname, queueName);
	}
	
	private void initialize(String hostname, String queueName) throws IOException {
		declareQueue(queueName);
		System.out.println(" [*] Waiting for messages on queue " + queueName + ". To exit press CTRL+C");
		consumer = new QueueingConsumer(getChannel());
		getChannel().basicConsume(queueName, true, consumer);		
	}

	public String consume() throws ShutdownSignalException, ConsumerCancelledException, InterruptedException {
		String ret = null;
		QueueingConsumer.Delivery delivery = consumer.nextDelivery();
		ret = new String(delivery.getBody());
		return ret;
	}

	public void destroy() {
		if (consumer != null) {
			consumer = null;
		}
		super.destroy();
	}	
}
