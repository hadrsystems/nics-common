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

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Geometry;

public class PackedFeature implements JSONWritable {
	
	public String id;
	
	/** The attributes. */
	public Map<String,String> attributes;
	
	/** The geometry. */
	public Geometry geometry; // should be in WKT format

	
	public PackedFeature(String id, Map<String,String> attributes, Geometry geometry) {
		this.id = id;
		this.attributes = attributes;
		this.geometry = geometry;
	}
	
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the attributes
	 */
	public Map<String,String> getAttributes() {
		return attributes;
	}

	/**
	 * @param attributes the attributes to set
	 */
	public void setAttributes(Map<String,String> attributes) {
		this.attributes = attributes;		
	}

	/**
	 * @return the geometry
	 */
	public Geometry getGeometry() {
		return geometry;
	}

	/**
	 * @param geometry the geometry to set
	 */
	public void setGeometry(Geometry geometry) {
		this.geometry = geometry;
	}
	
	@Override
	public boolean equals(Object other) {
		
		if(other instanceof PackedFeature){
			PackedFeature other1 = (PackedFeature) other;
			if(this.geometry.equals(other1.getGeometry()) &&
					this.id.equals(other1.getId()) &&
					this.attributes.equals(other1.getAttributes())){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see edu.mit.ll.nics.common.messages.JSONWritable#toJSONObject()
	 */
	@Override
	public JSONObject toJSONObject() throws JSONException {
		JSONObject obj = new JSONObject();
		obj.put("attributes", this.attributes);
		obj.put("id", this.id);
		obj.put("geo", this.geometry.toString());
		return obj;
	}

	/* (non-Javadoc)
	 * @see org.json.JSONString#toJSONString()
	 */
	@Override
	public String toJSONString() {
		try {
			return toJSONObject().toString();
		} catch (JSONException e) {
			return null;
		}
	}
	
}
