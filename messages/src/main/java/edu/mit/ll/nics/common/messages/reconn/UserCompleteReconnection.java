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
package edu.mit.ll.nics.common.messages.reconn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.messages.PackedFeature;
import edu.mit.ll.nics.common.messages.PackedFeaturesObject;

public final class UserCompleteReconnection extends ReconnectionMessage {
	
	private ReconnectionMessageType messageType = ReconnectionMessageType.USER_COMPL_RECONN;
	
	private String responseTopic;
	private String nodename;
	private List<PackedFeaturesObject> features;
		
	public UserCompleteReconnection(String mergeId,	String responseTopic, String nodename) {
		this.mergeId = mergeId;
		this.responseTopic = responseTopic;
		this.nodename = nodename;
		this.features = new ArrayList<PackedFeaturesObject>(5);
	}


	public UserCompleteReconnection(String mergeId, 
			String responseTopic, String nodename, List<PackedFeaturesObject> features) {
		this.mergeId = mergeId;
		this.responseTopic = responseTopic;
		this.nodename = nodename;
		this.features = features;
	}
	
	public UserCompleteReconnection() {
	}
	
	/**
	 * @return the responseTopic
	 */
	public String getResponseTopic() {
		return responseTopic;
	}
	/**
	 * @param responseTopic the responseTopic to set
	 */
	public void setResponseTopic(String responseTopic) {
		this.responseTopic = responseTopic;
	}
	/**
	 * @return the nodename
	 */
	public String getNodename() {
		return nodename;
	}
	/**
	 * @param nodename the nodename to set
	 */
	public void setNodename(String nodename) {
		this.nodename = nodename;
	}
	/**
	 * @return the features
	 */
	public List<PackedFeaturesObject> getFeatures() {
		return features;
	}
	/**
	 * @param features the features to set
	 */
	public void setFeatures(List<PackedFeaturesObject> features) {
		this.features = features;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject obj = new JSONObject();
		obj.put("mergeId", this.mergeId);
		obj.put("nodename", this.nodename);
		obj.put("responseTopic", this.responseTopic);
		obj.put("features", this.features);
		json.put(this.getMessageType().toString(), obj);
		return json;
	}
		
	@Override
	public ReconnectionMessageType getMessageType() {
		return this.messageType;
	}
	
}
