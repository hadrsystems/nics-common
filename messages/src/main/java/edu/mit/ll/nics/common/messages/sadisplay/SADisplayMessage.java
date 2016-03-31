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
package edu.mit.ll.nics.common.messages.sadisplay;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.util.Iterator;

import javax.persistence.MappedSuperclass;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.SADisplayMessageEntity;
import edu.mit.ll.nics.common.messages.NICSMessage;

/**
 * The Class SADisplayMessage.
 */
@MappedSuperclass
public class SADisplayMessage extends NICSMessage {
	
	/** Strings retrieved from the JSON Object to set on the message */
	public static String CLASS_NAME = "className";
	public static String IP = "ip";
	public static String TIME = "time";
	public static String VERSION = "version";
	public static String SEQ_NUM = "seqnum";
	public static String SEQ_TIME = "seqtime";
	public static String TOPIC = "topic";
	public static String RETURN_TOPIC = "returnTopic";
	public static String ENTITY = "entity";
	public static String MESSAGE_TYPE = "messageType";
	public static String MESSAGE_DATA = "messageData";
	public static String FROM = "from";
	public static String USER = "user";
	public static String DATA_MSG = "data";
	
	
	protected List<SADisplayMessageEntity> entities = new ArrayList<SADisplayMessageEntity>(0);
	
	protected Map<String, Object> messageData = new HashMap<String, Object>(0);
	
	public static String CLEAR_FIELD = "clear";
	
	/** The user. */
	protected String user;
	
	/** The time. */
	protected String time;
	
	/** The version. */
	protected String version;
	
	/** The ip. */
	protected String ip;
	
	protected Long seqTime;
	
	protected Long seqNum;
	
	protected String topic;
	
	protected String returnTopic;
	
	protected String messageType;
	
	public SADisplayMessage() {}
	
	public SADisplayMessage(
			String user, String time, String version, String ip,
			Long seqTime, Long seqNum, String topic,
			String messageType, 
			List<SADisplayMessageEntity> entities,
			Map<String, Object> messageData){
		this.user = user;
		this.time = time;
		this.version = version;
		this.ip = ip;
		this.seqTime = seqTime;
		this.seqNum = seqNum;
		this.topic = topic;
		this.messageType = messageType;
		this.entities = entities;
		this.messageData = messageData;
	}
	
	public SADisplayMessage(
			String user, String time, String version, String ip,
			Long seqTime, Long seqNum, String topic, String className, 
			String messageType, List<SADisplayMessageEntity> entities){
		this.user = user;
		this.time = time;
		this.version = version;
		this.ip = ip;
		this.seqTime = seqTime;
		this.seqNum = seqNum;
		this.topic = topic;
		this.messageType = messageType;
		this.entities = entities;
	}
	
	public SADisplayMessage(
			String user, String time, String version, String ip,
			Long seqTime, Long seqNum, String topic,
			String messageType){
		this.user = user;
		this.time = time;
		this.version = version;
		this.ip = ip;
		this.seqTime = seqTime;
		this.seqNum = seqNum;
		this.topic = topic;
		this.messageType = messageType;
	}
		
	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}
	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SADisplayMessage [ip=" + ip + ", time=" + time + ", user="
				+ user + ", version=" + version + "]";
	}
	
	/**
	 * @return the seqTime
	 */
	public Long getSeqTime() {
		return seqTime;
	}

	/**
	 * @param seqTime the seqTime to set
	 */
	public void setSeqTime(Long seqTime) {
		this.seqTime = seqTime;
	}

	/**
	 * @return the seqNum
	 */
	public Long getSeqNum() {
		return seqNum;
	}

	/**
	 * @param seqNum the seqNum to set
	 */
	public void setSeqNum(Long seqNum) {
		this.seqNum = seqNum;
	}

	/**
	 * @return the topic
	 */
	public String getTopic() {
		return topic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setTopic(String topic) {
		this.topic = topic;
	}
	
	/**
	 * @return the topic
	 */
	public String getReturnTopic() {
		return returnTopic;
	}

	/**
	 * @param topic the topic to set
	 */
	public void setReturnTopic(String returnTopic) {
		this.returnTopic = returnTopic;
	}
	
	/**
	 * @return type of message
	 */
	public String getMessageType() {
		return this.messageType;
	}

	/**
	 * @param type of message
	 */
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	
	/**
	 * @param entites
	 */
	public void setEntities(List<SADisplayMessageEntity> entities){
		this.entities = entities;
	}
	
	/**
	 * @return list of entities
	 */
	public List<SADisplayMessageEntity> getEntities(){
		return this.entities;
	}
	
	/**
	 * @param entity to be added to message
	 */
	public void addEntity(SADisplayMessageEntity entity){
		this.entities.add(entity);
	}
	
	public void addEntities(Collection<SADisplayMessageEntity> entities){
		this.entities.addAll(entities);
	}
	
	/**
	 * @param messageData
	 */
	public void setMessageData(Map<String, Object> messageData){
		this.messageData = messageData;
	}
	
	/**
	 * @return list of entities
	 */
	public Map<String, Object> getMessageData(){
		return this.messageData;
	}
	
	private JSONObject buildEntityJSONEntity(SADisplayMessageEntity msgEntity) throws JSONException{
		JSONObject entity = msgEntity.toJSONObject();
		entity.put(SADisplayMessage.CLASS_NAME, msgEntity.getClass().getCanonicalName());
		return entity;
	}
	
	private JSONArray buildEntityJSONObject() throws JSONException{
		JSONArray json = new JSONArray();
		for(Iterator<SADisplayMessageEntity> itr = this.entities.iterator(); itr.hasNext();){
			json.put(buildEntityJSONEntity(itr.next()));
		}
		return json;
	}
	
	protected JSONObject buildMessageJSON(){
		JSONObject json = new JSONObject();
		try{
			JSONObject fromObj = new JSONObject();
			fromObj.put(USER, this.user);
			json.put(FROM, fromObj);
			json.put(TOPIC, this.topic);
			json.put(TIME, this.time);
			json.put(VERSION, this.version);
			json.put(SEQ_TIME, this.seqTime);
			json.put(SEQ_NUM, this.seqNum);
			json.put(IP, this.ip);
			json.put(MESSAGE_TYPE, this.messageType);
			json.put(MESSAGE_DATA, this.messageData);
			json.put(RETURN_TOPIC, this.returnTopic);
		}catch(Exception JSONException){}
		return json;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException {
		try {
			JSONObject json = this.buildMessageJSON();
			json.put(DATA_MSG, this.buildEntityJSONObject());
			return json;
		}
		catch(JSONException e){
			return null;
		}
	}	
}
