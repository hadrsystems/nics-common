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
package edu.mit.ll.nics.common.messages;

import java.lang.reflect.Field;

import org.json.JSONException;

import edu.mit.ll.nics.common.messages.sadisplay.SADisplayMessageType;

public abstract class NICSMessage implements JSONWritable {

	public final static String CURRENT_MESSAGE_VERSION = "1.2.3";
		
	/* (non-Javadoc)
	 * @see org.json.JSONString#toJSONString()
	 */
	@Override
	public String toJSONString() {
		try {
			return this.toJSONObject().toString();
		} catch (JSONException e) {
			return null;
		}
	}
	
	public String getValue(String key){
		
		try {
			Field f = this.getClass().getField(key);
			Object value = f.get(this);
			if(SADisplayMessageType.class.isInstance(value)){
				return value.toString();
			}
			else{
				return (String) value;
			}
		} catch (SecurityException e) {
			return null;
		} catch (NoSuchFieldException e) {
			return null;
		} catch (IllegalArgumentException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	
	public boolean equals(NICSMessage other) throws IllegalArgumentException, IllegalAccessException{
		Field[] fields = this.getClass().getFields();
		
		for (Field field : fields) {
			if(field.get(this).getClass().isArray()){
				Object[] list1 = (Object[]) field.get(this);
				Object[] list2 = (Object[]) field.get(other);
				
				for (Object object2 : list2) {
					boolean val = false;
					for (Object object1 : list1) {
						if(object1.equals(object2)){
							val = true;
							break;
						}
					}
					
					if(!val){
						return false;
					}
				}

				for (Object object1 : list1) {
					boolean val = false;
					for (Object object2 : list2) {
						if(object1.equals(object2)){
							val = true;
							break;
						}
					}
					
					if(!val){
						return false;
					}
				}
				
			}
			else{
				if(!field.get(this).equals(field.get(other))){
					return false;
				}
			}
		}
		
		return true;
	}
}
