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
/**
 * 
 */
package edu.mit.ll.nics.common.entity.social;

import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.SADisplayMessageEntity;
import edu.mit.ll.nics.common.entity.SADisplayPersistedEntity;

/**
 * @author ri18384
 *
 */
@Entity
public class TwitterQuery extends SADisplayMessageEntity implements SADisplayPersistedEntity {

	@Id
	@GeneratedValue
	private int id;
	
	@Column(nullable=false)
	private Date creationTime;
	
	@Enumerated(EnumType.STRING)
	private SocialMediaSource source;
	
	@Column(nullable=false)	
	private String queryString;
	
	@Column
	private double latitude;
	
	@Column
	private double longitude;
	
	private double radiusKm;
	
	// Restricts tweets to the given language, given by an ISO 639-1 code. Language detection is best-effort.
	private String lang;
	
	public static enum ResultType {
		MIXED,
		RECENT,
		POPULAR
	}
	
	@Column(nullable=false)	
	private ResultType resultType = ResultType.RECENT;
	
	public TwitterQuery()
	{
		
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public SocialMediaSource getSource() {
		return source;
	}

	public void setSource(SocialMediaSource source) {
		this.source = source;
	}

	public String getQueryString() {
		return queryString;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getRadiusKm() {
		return radiusKm;
	}

	public void setRadiusKm(double radiusKm) {
		this.radiusKm = radiusKm;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	@Override
	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject json = new JSONObject();
		
		try
		{
			json.put("id", this.id);
			if (this.source != null)
				json.put("source", this.source.toString());
			json.put("queryString", this.queryString);
			json.put("latitude", this.latitude);
			json.put("longitude", this.longitude);
			json.put("radiusKm", this.radiusKm);
			json.put("lang", this.lang);
			json.put("creationTime", this.creationTime);
			json.put("resultType", this.resultType.toString());
			
			return json;
		} catch (JSONException je)
		{
			je.printStackTrace();
			return null;
		}
	}
} // class TwitterQuery


