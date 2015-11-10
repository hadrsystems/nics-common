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
package edu.mit.ll.nics.socialMedia.messages;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.social.Message;

public class SocialMediaMessage extends AbstractSocialMediaMessage 
{
	public static final String TWEET = "tweet",
							REL_SCORE = "relevanceScore",
							TOPIC = "topic";	
	
	private String topic;
	private Message message;
	private Double relevancescore;
	
	public SocialMediaMessage(String topic, Message message, Double relevancescore)
	{
		this.topic = topic;
		this.message = message;
		this.relevancescore = relevancescore;
	}
	
	public SocialMediaMessage(String topic, Message message)
	{
		this.topic = topic;
		this.message = message;
	}
	
	public SocialMediaMessage()
	{
	}
	
	
	public Message getMessage() 
	{
		return message;
	}
	public void setMessage(Message message) 
	{
		this.message = message;
	}
	public Double getRelevancescore() 
	{
		return relevancescore;
	}
	public void setRelevancescore(Double relevancescore) 
	{
		this.relevancescore = relevancescore;
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public JSONObject toJSONObject() 
	{
		JSONObject json = new JSONObject();
		
		try
		{
			if (message != null)
				json.put(TWEET, message.toJSONObject());
			if (relevancescore != null)
				json.put(REL_SCORE, relevancescore);
			if (topic != null)
				json.put(TOPIC, topic);
			return json;
		} catch (JSONException je)
		{
			je.printStackTrace();
			return null;
		}
	}
}
