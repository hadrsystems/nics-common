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

public final class UserInitiateReconnectionResponse extends ReconnectionMessage {
	
	private ReconnectionMessageType messageType = ReconnectionMessageType.USER_INIT_RECONN_RESP;
	
	private List<String> featureIds;
	private boolean status;
	
	public UserInitiateReconnectionResponse(String mergeId, boolean status) {
		this.mergeId = mergeId;
		this.status = status;
		this.featureIds = new ArrayList<String>(5);
	}
	
	public UserInitiateReconnectionResponse(String mergeId, List<String> featureIds, boolean status) {
		this.mergeId = mergeId;
		this.featureIds = featureIds;
		this.status = status;
	}
	
	public UserInitiateReconnectionResponse() {
	}
	
	
	/**
	 * @return the featureIds
	 */
	public List<String> getFeatureIds() {
		return featureIds;
	}
	/**
	 * @param featureIds the featureIds to set
	 */
	public void setFeatureIds(List<String> featureIds) {
		this.featureIds = featureIds;
	}
	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	@Override
	public ReconnectionMessageType getMessageType() {
		return this.messageType;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject obj = new JSONObject();
		obj.put("mergeId", this.mergeId);
		obj.put("status", this.status);
		obj.put("featureIds", this.featureIds);
		json.put(this.getMessageType().toString(), obj);
		return json;
	}
		
}
