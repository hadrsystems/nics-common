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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Interface for entities that will be turned into an SADisplay message
 * @author LE22005
 */
public abstract class SADisplayMessageEntity{
	
	public abstract JSONObject toJSONObject() throws JSONException;

	private boolean encode = false;
	
	/**encodeForHTML
     * encode the string to display safe HTML
     * @param value
     * @return String - safe HTML or null if error occurred 
     */
	public String encodeForHTML(String value){
		if(this.useEncoding()){
			return EntityEncoder.encodeForHTML(value);
		}else{
			return value;
		}
	}
	
	/**encodeJSONObject
     * encode each string value found in the json object
     * *Currently used for featureattributes on the Feature Object
     * *Does not handle nested json objects
     * @param jsonString
     * @return String - safe HTML or empty string 
     */
	public String encodeJSONObject(String jsonString){
		if(this.useEncoding()){
			return EntityEncoder.encodeJSONObject(jsonString);
		}else{
			return jsonString;
		}
	}
	
	/**useEncoding
     * check to see if this persisted entity should be encoded
     * @return boolean
     */
	private boolean useEncoding(){
		return this.encode;
	}
	
	/**setEncoding
     * Defaults to true. This value is usually set in the Message Parser
     * @param encode
     */
	public void setEncoding(boolean encode){
		this.encode = encode;
	}
}