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
/**
 * 
 */
package edu.mit.ll.nics.common.messages;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class PackedFeaturesObject implements JSONWritable {
	private String room;
	private String topic;
	private List<PackedFeature> features;
	
	public PackedFeaturesObject() {
	}
	
	public PackedFeaturesObject(String room, String topic,
			List<PackedFeature> features) {
		this.room = room;
		this.topic = topic;
		this.features = features;
	}

	public String getRoom() {
		return room;
	}

	public void setRoom(String room) {
		this.room = room;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public List<PackedFeature> getFeatures() {
		return features;
	}

	public void setFeatures(List<PackedFeature> features) {
		this.features = features;
	}
	
	
	/* (non-Javadoc)
	 * @see edu.mit.ll.nics.common.messages.JSONWritable#toJSONObject()
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("room", this.room);
		obj.put("topic", this.topic);
		obj.put("features", this.features);
		return obj;
	}
	
	/* (non-Javadoc)
	 * @see org.json.JSONString#toJSONString()
	 */
	@Override
	public String toJSONString() {
		try {
			return this.toJSONObject().toString();
		} catch (JSONException e) {
			return null;
		}
	}
	
}
