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
package edu.mit.ll.nics.common.messages.parser;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.SADisplayMessageEntity;
import edu.mit.ll.nics.common.entity.SADisplayUpdateEntity;
import edu.mit.ll.nics.common.messages.NICSMessage;
import edu.mit.ll.nics.common.messages.sadisplay.SADisplayMessage;
import edu.mit.ll.nics.common.entity.Log;

/**
 * The Class SADisplayMessageParser.
 */
public class SADisplayMessageParser {
	
	/** The log. */
	private static Logger log = Logger.getLogger(SADisplayMessageParser.class.getSimpleName());
	
	//ERROR MESSAGES
	private static String ERROR_MSG = "An error occurred in SADisplayMessageParser: ";
	private static String NO_CLASS_FOUND_MSG = "Class was not found to instantiate: ";
	private static String MISSING_CLASSNAME_MSG = "No className was indicated in message. No updates were made.";
	private static String MISSING_DATA_MSG = "No data was indicated in message. Can not identify which SADisplayEntity to update. No updates were made";
	private static String NOT_JSON_MSG = "Message was not JSON: ";
	
	/**
	 * parses the passed string to JSON and gets the SADisplayMessage from it
	 * @param string the string that represents the sadisplay message (in json)
	 * @return SADisplayMessage object that contains the data from the passed string
	 */
	public static NICSMessage parse(JSONObject obj, boolean useEncoding){
		SADisplayMessage message = createMessage(new SADisplayMessage(), obj, useEncoding);
		if(message != null){
			fillInCommonFields(message, obj);
			return message;
		}
		return message;
	}
	
	
	/**
	 * parses the passed string to JSON and gets the SADisplayMessage from it
	 * @param string the string that represents the sadisplay message (in json)
	 * @return SADisplayMessage object that contains the data from the passed string
	 */
	public static NICSMessage parse(String string, boolean useEncoding){
		log.info("Parsing...." + string);
		JSONObject obj;
				
		try {
			obj = new JSONObject(string);
		} catch (JSONException e) {
			log.error(NOT_JSON_MSG + e);
			return null;
		}
		return parse(obj, useEncoding);
	}
	
	/** createAddMessage
	 *  @param jsonMessage 
	 *  @return SADisplayMessage
	 */
	public static SADisplayMessage createMessage(SADisplayMessage message, JSONObject jsonMessage, boolean useEncoding){
		if(jsonMessage.has(SADisplayMessage.MESSAGE_TYPE) &&
			jsonMessage.optString(SADisplayMessage.MESSAGE_TYPE).equals("alert")){
			return parseAlertMessage(message, jsonMessage, useEncoding);
		}

		return updateMessage(message, jsonMessage, useEncoding);
	}

	public static SADisplayMessage parseAlertMessage(SADisplayMessage message, JSONObject jsonMessage, boolean useEncoding){
		Log log = new Log();
		log.setMessage(jsonMessage.optString("message"));
		log.setStatus(jsonMessage.optInt("status"));
		log.setCreated(Calendar.getInstance().getTime());
		log.setUsersessionid(1);
		log.setLogtypeid(1);//Alert Log type - should probably look this up somehow
	
		//handle incident-log join entity
		//handle collabroom-log join entity

		message.addEntity(log);
		return message;
	}
	
	private static JSONObject encodeUpdateObject(JSONObject updateObject){
		JSONObject encodedValues = new JSONObject();
		try{
			String persistedClassName = updateObject.getString(SADisplayUpdateEntity.PERSISTED_CLASS_NAME);
			JSONObject updatedFields = updateObject.getJSONObject(SADisplayUpdateEntity.UPDATED_FIELDS);
			Class<?> c = Class.forName(persistedClassName);
			/** Create a new instance of the class */
			Object object = c.newInstance();
			
			SADisplayMessageEntity persistedEntity = (SADisplayMessageEntity) object;
			persistedEntity.setEncoding(true);
			
			SADisplayMessageEntity encodedEntity = updateEntity(persistedEntity, updatedFields);
			
			//Set each updated value on the class. Entity class knows which values to encode
			for(Iterator<String> itr=updatedFields.keys(); itr.hasNext();){
				String key = itr.next();
				if( (updatedFields.get(key).getClass().equals(String.class) &&
					updatedFields.get(key).equals(SADisplayMessage.CLEAR_FIELD)) ||
					updatedFields.get(key).getClass().equals(Boolean.class)
				){
						//Add clear fields back to update object
						//They will be set to null in the entity
						//Or - if boolean - no need to encode
						encodedValues.put(key, updatedFields.get(key)); 
				}else{
					encodedValues.put(key, 
							PopulateEntityHelper.getField(c, PopulateEntityHelper.prePendGet(key), encodedEntity));
				}
			}
			updateObject.put(SADisplayUpdateEntity.UPDATED_FIELDS, encodedValues);
		}catch(Exception e){
			e.printStackTrace();
		}
		return updateObject;
	}
	
	public static SADisplayMessage updateMessage(SADisplayMessage message, JSONObject jsonMessage, boolean useEncoding){
		if(!jsonMessage.has(SADisplayMessage.DATA_MSG)){
			log.error(MISSING_DATA_MSG);
		}else{
			try{
				JSONArray addEntities = jsonMessage.getJSONArray(SADisplayMessage.DATA_MSG);
				JSONObject updateObject = new JSONObject();
				if(jsonMessage.has(SADisplayMessage.MESSAGE_DATA)){
					JSONObject msgData = jsonMessage.getJSONObject(SADisplayMessage.MESSAGE_DATA);
					updateObject.put(SADisplayMessage.MESSAGE_DATA, msgData);
					message.setMessageData(buildMessageData(msgData));
				}
				for(int i=0; i<addEntities.length(); i++){
					JSONObject entity = addEntities.getJSONObject(i);
					String className = entity.getString(SADisplayMessage.CLASS_NAME);
					if(className == "" || className == null){
						log.error(MISSING_CLASSNAME_MSG);
					}else{
						try{
							Class<?> c = Class.forName(className);
							/** Create a new instance of the class */
							Object object = c.newInstance();
							
							SADisplayMessageEntity persistedEntity = (SADisplayMessageEntity) object;
							persistedEntity.setEncoding(useEncoding);
							
							/**Encode Updated Fields**/
							if(useEncoding && persistedEntity instanceof SADisplayUpdateEntity){
								updateObject.put(SADisplayMessage.ENTITY, encodeUpdateObject(entity));
							}else{
								/** Put the current entity properties on the object,
								    replacing previous. */
								updateObject.put(SADisplayMessage.ENTITY, entity);
							}
							
							message.addEntity(updateEntity(persistedEntity, updateObject));
						}catch(InstantiationException ie){ log.error(NO_CLASS_FOUND_MSG + className);}
					}
				}
			}
			catch(Exception e){ 
				e.printStackTrace();
				//log.error(ERROR_MSG + e.getMessage()); }
			}
		}
		return message;
	}
	
	@SuppressWarnings("unchecked")
	/** Update entity
	 *  @param object - entity object
	 *  @param fields - JSONObject of name, value pairs
	 *  @return object - updated object
	 *  NOTE: name must be the same (case-sensitive) as is defined
	 *  in the class and the set method
	 */
	public static SADisplayMessageEntity updateEntity(SADisplayMessageEntity object, JSONObject fields) throws SADisplayParserException{
		String className = object.getClass().getName();
		try{
			/** Retrieve the class object **/
			Class<?> c = Class.forName(className);
			for(Iterator<String> itr=fields.keys(); itr.hasNext();){
				String key = itr.next(); /** field variable */
				if(!fields.isNull(key)){
					Object value = fields.get(key); /** value to set */
					String methodName = PopulateEntityHelper.prePendSet(key); /** returns method name set<Key> */
					if(value instanceof JSONObject){
						/** Check to see if there is a setter for this JSONObject/Map*/
						PopulateEntityHelper.setField(c, methodName, JSONObject.class, object, value);
						/** update entity with fields in the JSON object */
						updateEntity(object, (JSONObject) value);
					}else{
						/** Get the type of the field **/
						Class<?> fieldType = PopulateEntityHelper.getFieldType(c, key);
						if(fieldType != null && object != null && value != null){
							PopulateEntityHelper.setField(c, methodName, fieldType, object, value);
						}
					}
				}
			}
		}catch(ClassNotFoundException cnf){ 
			log.error(NO_CLASS_FOUND_MSG + className);
		}catch(JSONException e){ 
			log.error(ERROR_MSG + e.getMessage()); 
		}catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return object;
	}
	
	/** fillInCommonFields - set fields that apply to all messages
	 *  @param message 
	 *  @param obj - JSON message object
	 */
	private static void fillInCommonFields(SADisplayMessage message, JSONObject obj){
		//Setting to null if the fields are not set in the message
		if(message != null){
			message.setTime(obj.optString(SADisplayMessage.TIME, Calendar.getInstance().getTime().toString()));
			message.setSeqTime(obj.optLong(SADisplayMessage.SEQ_TIME,System.currentTimeMillis()));
			message.setTopic(obj.optString(SADisplayMessage.TOPIC,"No_Topic"));
			message.setMessageType(obj.optString(SADisplayMessage.MESSAGE_TYPE));
			message.setVersion(obj.optString(SADisplayMessage.VERSION, "1.2.3"));
			if(obj.has(SADisplayMessage.FROM)){
				message.setUser((obj.optJSONObject(SADisplayMessage.FROM)).optString(SADisplayMessage.USER));
			}else{
				message.setUser("messageParser");
			}
			if(obj.has(SADisplayMessage.SEQ_NUM)){
				message.setSeqNum(obj.optLong(SADisplayMessage.SEQ_NUM));
			}
			if(obj.has(SADisplayMessage.IP)){
				message.setIp(obj.optString(SADisplayMessage.IP));
			}
			if(obj.has(SADisplayMessage.RETURN_TOPIC)){
				message.setReturnTopic(obj.optString(SADisplayMessage.RETURN_TOPIC));
			}
		}
	}
	
	private static Map<String,Object> buildMessageData(JSONObject data){
		Map<String,Object> messageData = new HashMap<String,Object>();
		if(data != null){
			try{
				for(Iterator<String> itr = data.keys(); itr.hasNext();){
					String key = itr.next();
					messageData.put(key, data.get(key));
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return messageData;
	}
}
