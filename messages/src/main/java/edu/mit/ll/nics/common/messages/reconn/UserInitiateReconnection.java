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
package edu.mit.ll.nics.common.messages.reconn;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.messages.FeatureIdTimestamp;


public final class UserInitiateReconnection extends ReconnectionMessage {

	private String nodename;
	private String responseTopic;
	private List<FeatureIdTimestamp> featureIds;
	private final ReconnectionMessageType messageType = ReconnectionMessageType.USER_INIT_RECONN;
	
	public UserInitiateReconnection(String mergeId, String nodename, String responseTopic) {
		this.mergeId = mergeId;
		this.nodename = nodename;
		this.responseTopic = responseTopic;
		this.featureIds = new ArrayList<FeatureIdTimestamp>(5);
	}
	
	public UserInitiateReconnection(String mergeId, String nodename,
			String responseTopic, List<FeatureIdTimestamp> featureIds) {
		this.mergeId = mergeId;
		this.nodename = nodename;
		this.responseTopic = responseTopic;
		this.featureIds = featureIds;
	}
	
	public UserInitiateReconnection() {
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
	 * @return the featureIds
	 */
	public List<FeatureIdTimestamp> getFeatureIds() {
		return featureIds;
	}
	/**
	 * @param featureIds the featureIds to set
	 */
	public void setFeatureIds(List<FeatureIdTimestamp> featureIds) {
			this.featureIds = featureIds;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject obj = new JSONObject();
		obj.put("mergeId", this.mergeId);
		obj.put("nodename", this.nodename);
		obj.put("responseTopic", this.responseTopic);
		obj.put("featureIds", this.featureIds);
		json.put(this.getMessageType().toString(), obj);
		return json;
	}
		
	@Override
	public ReconnectionMessageType getMessageType() {
		return this.messageType;
	}
	
}
