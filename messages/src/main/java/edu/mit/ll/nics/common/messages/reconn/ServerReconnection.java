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

import org.json.JSONException;
import org.json.JSONObject;

public final class ServerReconnection extends ReconnectionMessage {

	private ReconnectionMessageType messageType = ReconnectionMessageType.SERVER_RECONN;
	
	private String ip;
	private String nodename;
	private String responseTopic;
	private String type;
		
	public ServerReconnection(String ip, String mergeId, String nodename,
			String responseTopic, String type) {
		this.ip = ip;
		this.mergeId = mergeId;
		this.nodename = nodename;
		this.responseTopic = responseTopic;
		this.type = type;
	}

	public ServerReconnection() {
	}
	
	@Override
	public ReconnectionMessageType getMessageType() {
		return this.messageType;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}



	/**
	 * @param ip the ip to set
	 */
	public void setIp(String ip) {
		this.ip = ip;
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
	 * @return the type
	 */
	public String getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		JSONObject obj = new JSONObject();
		obj.put("ip", this.ip);
		obj.put("mergeId", this.mergeId);
		obj.put("nodename", this.nodename);
		obj.put("responseTopic", this.responseTopic);
		obj.put("type", this.type);
		json.put(this.getMessageType().toString(), obj);
		return json;
	}

}
