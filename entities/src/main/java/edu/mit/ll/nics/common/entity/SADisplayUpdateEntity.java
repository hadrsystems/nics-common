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
package edu.mit.ll.nics.common.entity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * SADisplayRemoveEntity
 */

public class SADisplayUpdateEntity extends SADisplayRemoveEntity implements SADisplayPersistedEntity{
	
	private JSONObject updatedFields = new JSONObject();
	
	public static String UPDATED_FIELDS = "updatedFields";
	
	public SADisplayUpdateEntity(){}
	
	public SADisplayUpdateEntity(JSONArray keys, 
			String className, JSONObject updatedFields){
		super(keys, className);
		this.updatedFields = updatedFields;
	}
	
	public JSONObject getUpdatedFields(){
		return this.updatedFields;
	}

	public void setUpdatedFields(JSONObject updatedFields){
		this.updatedFields = updatedFields;
	}
	
	public JSONObject toJSONObject(){
		JSONObject json = new JSONObject();
		try{
			json.put(PERSISTED_CLASS_NAME, this.getPersistedClassName());
			json.put(KEYS, this.getKeys());
			json.put(UPDATED_FIELDS, this.getUpdatedFields());
		}catch(JSONException e){}
		return json;
	}
	
}