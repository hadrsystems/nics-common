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
package edu.mit.ll.nics.common.rabbitmq.client.test;


import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

import edu.mit.ll.nics.common.rabbitmq.client.RabbitConsumer;
import edu.mit.ll.nics.common.rabbitmq.client.RabbitProducer;


public class BasicRabbitTest {
	
	private RabbitConsumer consumer;
	private RabbitProducer producer;
	private String rabbitHost;
	
	@Parameters({"rabbitHost"})
	public BasicRabbitTest(String rabbitHost) {
		if(rabbitHost.isEmpty()){
			this.rabbitHost = "localhost";
		} else {
			this.rabbitHost = rabbitHost;
		}
	}
	
	@BeforeMethod
	public void setUp() throws Exception {
		
		// create rabbit consumer and producer
		List<String> topics = new ArrayList<String>();
		topics.add("TestTopic");
		consumer = new RabbitConsumer("guest", "guest", rabbitHost, 5672, "", "", topics);
		producer = new RabbitProducer("guest", "guest", rabbitHost, 5672);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void simpleSendAndReceive(){
		String msgBody = "hi";
		producer.sendMessage("amq.topic", "TestTopic", msgBody);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> msgs = consumer.getLatestMessages();
		assertTrue("one message in results", msgs.size() == 1);
		assertTrue("message contents == '" + msgBody + "'", msgs.get(0).equalsIgnoreCase(msgBody));
	}

	@Test
	public void multiTopicSendAndReceive(){
		// add subscriptions
		String top1 = "TestTopic1";
		String top2 = "TestTopic2";
		consumer.subscribe(top1);
		consumer.subscribe(top2);
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		String msg1 = "hi on topic1";
		String msg2 = "hi on topic2";
		producer.sendMessage(top1, msg1);
		producer.sendMessage(top2, msg2);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		List<String> msgs = consumer.getLatestMessages();
		assertTrue("one message in results", msgs.size() == 2);
		assertTrue("message contents == '" + msg1 + "'", msgs.get(0).equalsIgnoreCase(msg1) || msgs.get(0).equalsIgnoreCase(msg2));
		assertTrue("message contents == '" + msg2 + "'", msgs.get(1).equalsIgnoreCase(msg1) || msgs.get(1).equalsIgnoreCase(msg2));
		
	}
	
	@AfterMethod
	public void tearDown() throws Exception {
		consumer.destroy();
		producer.destroy();
	}

}
