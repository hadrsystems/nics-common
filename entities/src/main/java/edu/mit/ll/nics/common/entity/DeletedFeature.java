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

import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Proxy;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * DeletedFeature
 */
@Entity
@Proxy(lazy=false)
@Table(name = "deletedfeature")
public class DeletedFeature extends SADisplayMessageEntity implements SADisplayPersistedEntity {

	private String featureid;
	private int collabroomid;
	private Date timestamp;
	
	public DeletedFeature() {
		this.setTimestamp(Calendar.getInstance().getTime());
	}

	public DeletedFeature(String featureid, int collabroomid, Date timestamp) {
		this.featureid = featureid;
		this.collabroomid = collabroomid;
		this.timestamp = timestamp;
	}	

	@Id
	@Column(name = "featureid", unique = true, nullable = false)
	public String getFeatureid() {
		return this.featureid;
	}

	public void setFeatureid(String featureid) {
		this.featureid = featureid;
	}	

	@Column(name="collabroomid", unique = false, nullable = false)
	public int getCollabroomid() {
		return this.collabroomid;
	}
	
	public void setCollabroomid(int collabroomid) {
		this.collabroomid = collabroomid;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "timestamp", nullable = false, length = 29)
	public Date getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}	
	
	public String toHistoryJSON() throws JSONException{
		JSONObject hist = new JSONObject();
		hist.put("history", this.toJSONObject());
		return hist.toString();
	}
	
	public String toJSONString(){
		try{
			return this.toJSONObject().toString();
		}catch(JSONException e){
			return null;
		}
	}
	
    @Override
	public JSONObject toJSONObject() throws JSONException{
		JSONObject obj = new JSONObject();
				
		obj.put("featureid", this.featureid);
		obj.put("collabroomid", this.collabroomid);		
		obj.put("timestamp", this.timestamp); // TODO: Should this get formatted?
		
		return obj;
	}
}
