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
package edu.mit.ll.nics.common.messages.parser.test;


/*import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import edu.mit.ll.nics.common.messages.NICSMessage;
import edu.mit.ll.nics.common.messages.parser.SADisplayMessageParser;

public class SADisplayMessageParserTest {

	private static Logger log = Logger.getLogger(SADisplayMessageParserTest.class.getSimpleName());
	
	private String chatMessage = Messages.getString("chat-message");
	private String featureAddMessage = Messages.getString("feature-add-message");
	private String featureRemoveMessage = Messages.getString("feature-remove-message");
	private String featureMoveMessage = Messages.getString("feature-move-message");
	private String featureModifyMessage = Messages.getString("feature-modify-message");
	private String batchAddMessage = Messages.getString("batch-add-message");
	private String batchRemoveMessage = Messages.getString("batch-remove-message");
	private String batchMoveMessage = Messages.getString("batch-move-message");
	private String mapMessage = Messages.getString("map-message");
	private String incidentStatusMessage = Messages.getString("incident-status-message");
	private String resourceMessage = Messages.getString("resource-message");
	private String systemControlMessage = Messages.getString("system-control-message");
	private String presenceMessage = Messages.getString("presence-message");
	private String invitationMessage = Messages.getString("invitation-message");
	private String roomLockMessage = Messages.getString("room-lock-message");
	private String userInitReconnMessage = Messages.getString("user-init-reconn-message");
	private String userInitReconnRespMessage = Messages.getString("user-init-reconn-resp-message");
	private String userComplReconnMessage = Messages.getString("user-compl-reconn-message");
	private String userComplReconnRespMessage = Messages.getString("user-compl-reconn-resp-message");
	private String serverReconnMessage = Messages.getString("server-reconn-message");
	private String serverReconnRespMessage = Messages.getString("server-reconn-resp-message");
	
	@Test
	public void testBatchMoveMessage() throws JSONException {
		//checkMessage(this.batchMoveMessage);
	}

	//@Test
	public void testBatchAddMessage() throws JSONException {
		checkMessage(this.batchAddMessage);
	}

	//@Test
	public void testChatMessage() throws JSONException {
		checkMessage(this.chatMessage);
	}

	//@Test
	public void testFeatureAddMessage() throws JSONException {
		checkMessage(this.featureAddMessage);
	}

	//@Test
	public void testFeatureRemoveMessage() throws JSONException {
		checkMessage(this.featureRemoveMessage);
	}

	//@Test
	public void testFeatureMoveMessage() throws JSONException {
		checkMessage(this.featureMoveMessage);
	}

	//@Test
	public void testFeatureModifyMessage() throws JSONException {
		checkMessage(this.featureModifyMessage);
	}

	//@Test
	public void testBatchRemoveMessage() throws JSONException {
		checkMessage(this.batchRemoveMessage);
	}

	//@Test
	public void testMapMessage() throws JSONException {
		checkMessage(this.mapMessage);
	}

	//@Test
	public void testIncidentStatusMessage() throws JSONException {
		checkMessage(this.incidentStatusMessage);
	}

	//@Test
	public void testResourceMessage() throws JSONException {
		checkMessage(this.resourceMessage);
	}

	//@Test
	public void testSystemControlMessage() throws JSONException {
		checkMessage(this.systemControlMessage);
	}

	//@Test
	public void testPresenceMessage() throws JSONException {
		checkMessage(this.presenceMessage);
	}

	//@Test
	public void testInvitationMessage() throws JSONException {
		checkMessage(this.invitationMessage);
	}
	
	//@Test
	public void testRoomLockMessage() throws JSONException {
		checkMessage(this.roomLockMessage);
	}

	//@Test
	public void testUserInitReconnMessage() throws JSONException {
		checkMessage(this.userInitReconnMessage);
	}

	//@Test
	public void testUserInitReconnRespMessage() throws JSONException {
		checkMessage(this.userInitReconnRespMessage);
	}

	//@Test
	public void testUserComplReconnMessage() throws JSONException {
		checkMessage(this.userComplReconnMessage);
	}

	//@Test
	public void testUserComplReconnRespMessage() throws JSONException {
		checkMessage(this.userComplReconnRespMessage);
	}

	//@Test
	public void testServerReconnMessage() throws JSONException {
		checkMessage(this.serverReconnMessage);
	}
	
	//@Test
	public void testServerReconnRespMessage() throws JSONException {
		checkMessage(this.serverReconnRespMessage);
	}

	private void checkMessage(String message) throws JSONException {
		log.debug("Checking message...");
		NICSMessage msgObj = SADisplayMessageParser.parse(message);
		log.debug("of type: " + msgObj.getMessageType().toString());
		assertNotNull(msgObj); // after parsing message object shouldn't be null
		JSONObject jsonObjFromString = new JSONObject(message);
		JSONObject jsonObjFromObject = new JSONObject(msgObj.toJSONObject().toString());
		log.debug("\n\t" + jsonObjFromString + "\n\t" + jsonObjFromObject);
		assertTrue(areJSONObjectsEqual(jsonObjFromString, jsonObjFromObject));
	}

	
	private boolean areJSONObjectsEqual(JSONObject obj1, JSONObject obj2){
		if(obj1.length() == 0 && obj2.length() == 0){
			return true;
		}
		else{
			String[] names1 = org.json.JSONObject.getNames(obj1);
			String[] names2 = org.json.JSONObject.getNames(obj2);
			
			Object obj1Val, obj2Val;
			
			for (String name : names1) {
				try {
					obj1Val = obj1.get(name);
					obj2Val = obj2.get(name);
					if(obj1Val instanceof JSONObject){
						if(!areJSONObjectsEqual((JSONObject)obj1Val, (JSONObject)obj2Val)){
							log.info("unequal field: " + name);
							return false;
						}
					}
					else if(obj1Val instanceof JSONArray){
						if(!areJSONArraysEqual((JSONArray)obj1Val, (JSONArray)obj2Val)){
							log.info("unequal field: " + name);
							return false;
						}
					}
					else if(obj1Val == null && obj2Val == null){
					}
					else{
						if(!obj1Val.toString().equals(obj2Val.toString())){
							if(name.equals("geo")){
								if(obj1Val.toString().replaceAll(" ","").equals(obj2Val.toString().replaceAll(" ",""))){
									return true;
								}
							}
							log.info("unequal field: " + name);
							return false;
						}
					}
					
				} catch (JSONException e) {
					log.info("failed comparison: " + e);
					return false;
				}
			}
			
			for (String name : names2) {
				try {
					obj1Val = obj1.get(name);
					obj2Val = obj2.get(name);
					if(obj2Val instanceof JSONObject){
						if(!areJSONObjectsEqual((JSONObject)obj2Val,(JSONObject)obj1Val)){
							log.info("unequal field: " + name);
							return false;
						}
					}
					else if(obj2Val instanceof JSONArray){
						if(!areJSONArraysEqual((JSONArray)obj2Val,(JSONArray)obj1Val)){
							log.info("unequal field: " + name);
							return false;
						}
					}
					else if(obj1Val == null && obj2Val == null){
					}
					else{
						if(!obj2Val.toString().equals(obj1Val.toString())){
							log.info("unequal field: " + name);
							return false;
						}
					}
				} catch (JSONException e) {
					log.info("failed comparison: " + e);
					return false;
				}
			}
			
			return true;
		}
	}
	
	private boolean areJSONArraysEqual(JSONArray arr1, JSONArray arr2){
		
		if(arr1.length() == 0 && arr2.length() == 0){
			return true;
		}
		else{
			Object arr1Val, arr2Val;
			
			for(int i=0; i<arr1.length(); i++){
				try {
					arr1Val = arr1.get(i);
					arr2Val = arr2.get(i);
					
					if(arr1Val instanceof JSONObject){
						if(!areJSONObjectsEqual((JSONObject)arr1Val, (JSONObject)arr2Val)){
							return false;
						}
					}
					else if(arr1Val instanceof JSONArray){
						if(!areJSONArraysEqual((JSONArray)arr1Val, (JSONArray)arr2Val)){
							return false;
						}
					}else if(arr1Val == null && arr2Val == null){
					}
					else{
						if(!arr1Val.toString().equals(arr2Val.toString())){
							return false;
						}
					}
						
				}
				catch(Exception e){
					log.info("failed comparison: " + e);
					return false;
				}
			}
			
			for(int j=0; j<arr2.length(); j++){
				try {
					arr1Val = arr1.get(j);
					arr2Val = arr2.get(j);
					
					if(arr2Val instanceof JSONObject){
						if(!areJSONObjectsEqual((JSONObject)arr2Val, (JSONObject)arr1Val)){
							return false;
						}
					}
					else if(arr2Val instanceof JSONArray){
						if(!areJSONArraysEqual((JSONArray)arr2Val, (JSONArray)arr1Val)){
							return false;
						}
					}
					else if(arr1Val == null && arr2Val == null){
					}
					else{
						if(!arr2Val.toString().equals(arr1Val.toString())){
							return false;
						}
					}
						
				}
				catch(Exception e){
					log.info("failed comparison: " + e);
					return false;
				}
			}
			
			return true;
		}
	}

}*/
