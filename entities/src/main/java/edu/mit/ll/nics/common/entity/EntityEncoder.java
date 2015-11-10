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
package edu.mit.ll.nics.common.entity;

import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.owasp.esapi.ESAPI;

/**
 * Encode HTML and Validate Email and URL
 * @author st23420
 */
public final class EntityEncoder{
	
	private static String URL = "URL";
	private static String EMAIL = "Email";
	private static String SAFE_STRING = "SafeString";
	private static String INPUT_ERROR_MSG = "Input value could not be validated: ";
	private static String EMAIL_ERROR_MSG = "Email address could not be validated: ";
	private static String URL_ERROR_MSG = "URL could not be validated: ";
	private static String HTML_ERROR_MSG = "There was a problem encoding value : " ;
	private static String INVALID_URL = "Invalid URL";
	private static String JSON_ERROR_MSG = "Error encoding/validating JSON object.";
	private static String INVALID_INPUT = "Invalid Input";
	
	/**encodeForHTML
     * encode the string to display safe HTML
     * @param value
     * @return String - safe HTML or null if error occurred 
     */
	public static String encodeForHTML(String value){
		if(value != null && !StringUtils.isEmpty(value)){
			try{
				return ESAPI.encoder().encodeForHTML(value);
			}catch(Exception e){
				e.printStackTrace();
				System.out.println(HTML_ERROR_MSG + value);
			}
			return null;
		}else{
			return value;
		}
	}
	
	/**validateInputvalue
     * Validate user input
     * @param value
     * @return boolean
     */
	public static boolean validateInputValue(String value){
		try{
			if(value != null){
				if(StringUtils.isEmpty(value)){
					return false;
				}
				return ESAPI.validator().isValidInput(SAFE_STRING, value, SAFE_STRING, 200, false);
				//return ESAPI.validator().getValidSafeHTML(SAFE_STRING, value, 2000, true);
			}
		}catch(Exception e){
			System.out.println(INPUT_ERROR_MSG + value);
		}
		
		return false;
	}
	
	/**validateEmailAddress
     * verify valid email address
     * @param value
     * @return boolean
     */
	public static boolean validateEmailAddress(String value){
		try{
			if(value != null && !StringUtils.isEmpty(value)){
				return ESAPI.validator().isValidInput(EMAIL, value, EMAIL, 100, false);
			}
		}catch(Exception e){
			System.out.println(EMAIL_ERROR_MSG + value);
		}
		return false;
	}
	
	/**validateURL
     * verify valid URL
     * @param value
     * @return String - URL or "Invalid URL" 
     */
	public static String validateURL(String value){
		try{
			if(value != null && !StringUtils.isEmpty(value) &&
					ESAPI.validator().isValidInput(URL, value, URL, 200, false)){
				return value;
			}
		}catch(Exception e){
			System.out.println(URL_ERROR_MSG + value);
		}
		return INVALID_URL; 
	}
	
	/**encodeJSONObject
     * encode each string value found in the json object
     * *Currently used for featureattributes on the Feature Object
     * *Does not handle nested json objects
     * @param jsonString
     * @return String - safe HTML or empty string 
     */
	public static String encodeJSONObject(String jsonString){
		if(jsonString != null && !StringUtils.isEmpty(jsonString)){
			JSONObject result = new JSONObject();
			try{
				JSONObject obj = new JSONObject(jsonString);
				Iterator<String> keys = obj.keys();
				while(keys.hasNext()){
					String key = keys.next();
					Object value = obj.get(key);
					if(value.getClass().equals(String.class)){
						result.put(key, encodeForHTML((String)value));
					}else{
						result.put(key, value);
					}
				}
			}catch(Exception e){
				System.out.println(JSON_ERROR_MSG);
			}
			return result.toString();
		}else{
			return jsonString;
		}
	}
	
	/**validateJSONObject
     * validate each string value found in the json object
     * @param jsonString
     * @return String - safe HTML or empty string 
     */
	public static String validateJSONObject(String jsonString){
		if(jsonString != null && !StringUtils.isEmpty(jsonString)){
			JSONObject result = new JSONObject();
			try{
				JSONObject obj = new JSONObject(jsonString);
				Iterator<String> keys = obj.keys();
				while(keys.hasNext()){
					String key = keys.next();
					Object value = obj.get(key);
					if(value.getClass().equals(String.class)){
						result.put(key, validateInputValue((String)value));
					}else{
						result.put(key, value);
					}
				}
			}catch(Exception e){
				System.out.println(JSON_ERROR_MSG);
			}
			return result.toString();
		}else{
			return jsonString;
		}
	}
}