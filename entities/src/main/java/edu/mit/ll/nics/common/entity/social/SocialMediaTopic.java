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
/*
* ===================================================================*
*                                                                    *
*    (c) Copyright, 2008-2012 Massachusetts Institute of Technology. *
*        This material may be reproduced by or for the               *
*        U.S. Government pursuant to the copyright license           *
*        under the clause at DFARS 252.227-7013 (June, 1995).        *
*                                                                    *
*    WARNING: This material may contain technical data whose export  *
*    is restricted by the Arms Export Control Act (AECA) or the      *
*    Export Administration Act (EAA). Transfer of this data by       *
*    any means to a non-U.S. person who is not eligible to obtain    *
*    export-controlled data is prohibited. By accepting this data,   *
*    the consignee agrees to honor the requirements of the           *
*    AECA and EAA.                                                   *
*                                                                    *
* ===================================================================*
*/
/**
 * 
 */
package edu.mit.ll.nics.common.entity.social;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.SADisplayMessageEntity;
import edu.mit.ll.nics.common.entity.SADisplayPersistedEntity;

/**
 * @author ri18384
 * A topic that a user has requested we retreive Social Media relevant to
 */
@Entity
public class SocialMediaTopic extends SADisplayMessageEntity implements SADisplayPersistedEntity {

	public SocialMediaTopic( String topicName, int userId ) {
		this.name = topicName;
		this.createdByUserId = userId;
		this.lastChangedByUserId = userId;		
	}
	
	/**
	 * For Hibernate
	 */
	private SocialMediaTopic() {
		
	}
	
	@Id
	@GeneratedValue
	private int id;
	
	@Column(nullable=false)	
	private String name;
	
	@Column(nullable=false)
	private int createdByUserId;
	
	@Column(nullable=false)
	private int lastChangedByUserId;	
	
//	@Audited
//	@ElementCollection
//	@Enumerated(EnumType.STRING)
//	private Set<SocialMediaSource> sources = new HashSet<SocialMediaSource>(0);
	
	@ManyToMany
	private Set<Author> authors = new HashSet<Author>(0);
	
	@Column(nullable=false)	
	private String queryString;
	
	private Double latitude;
	
	private Double longitude;
	
	private Double radiusKm;
	
	public boolean hasLocationCriteria() {
		return (latitude!=null)&&(longitude!=null)&&(radiusKm!=null);
	}
	
	/* 
	 * The source specific queries this topic has created to collect messages from Twitter, Facebook, etc.
	 */
	@ManyToMany
	private Set<TwitterQuery> queries = new HashSet<TwitterQuery>();
	
	@Column(nullable=false)	
	private boolean isActive;
	
	@Column(nullable=false)	
	private boolean isRecording;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
//		this.lastChangedByUserId = userId;
	}

	/**
	 * @return the queryString
	 */
	public String getQuerystring() {
		return queryString;
	}

	/**
	 * @param queryString the queryString to set
	 */
	public void setQuerystring(String queryString) {
		this.queryString = queryString;
//		this.lastChangedByUserId = userId;
	}
	
	/**
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @param latitude the latitude to set
	 */
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @param longitude the longitude to set
	 */
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
//		this.lastChangedByUserId = userId;
	}

	/**
	 * @return the radiusKm
	 */
	public Double getRadiusKm() {
		return radiusKm;
	}

	/**
	 * @param radiusKm the radiusKm to set
	 */
	public void setRadiusKm(Double radiusKm) {
		this.radiusKm = radiusKm;
//		this.lastChangedByUserId = userId;
	}

	/**
	 * @return the isActive
	 */
	public boolean isActive() {
		return isActive;
	}

	/**
	 * @param isActive the isActive to set
	 */
	public void setIsactive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	 * @return the isRecording
	 */
	public boolean isRecording() {
		return isRecording;
	}

	/**
	 * @param isRecording the isRecording to set
	 */
	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the createdByUserId
	 */
	public int getCreatedByUserId() {
		return createdByUserId;
	}

	/**
	 * @return the lastChangedByUserId
	 */
	public int getLastChangedByUserId() {
		return lastChangedByUserId;
	}

	/**
	 * Note that the returned Set is read only!
	 * Must use setSources() to change the sources.
	 * @return the sources
	 */
//	public Set<SocialMediaSource> getSources() {
//		return Collections.unmodifiableSet(sources);
//	}

	/**
	 * Note that the returned Set is read only!
	 * Must user setSources() to change the sources.
	 * @return the authors
	 */
	public Set<Author> getAuthors() {
		return Collections.unmodifiableSet(authors);
	}

	/**
	 * @return the queries
	 */
	public Set<TwitterQuery> getQueries() {
		return queries;
	}
	
	/**
	 * @param sources the sources to set
	 */
//	public void setSources(Set<SocialMediaSource> sources) {
//		this.sources = sources;
////		this.lastChangedByUserId = userId;
//	}

	/**
	 * @param sources the sources to set
	 */
//	public void setSources(SocialMediaSource[] sources) {
//		this.sources = new HashSet<SocialMediaSource>(Arrays.asList(sources));
////		this.lastChangedByUserId = userId;
//	}	
	
	public void setQueries(Set<TwitterQuery> queries) {
		this.queries = queries;
	}
	
	public boolean addTwitterQuery(TwitterQuery tq)
	{
		return this.queries.add(tq);
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(Set<Author> authors) {
		this.authors = authors;
//		this.lastChangedByUserId = userId;
	}

	/**
	 * @param authors the authors to set
	 */
	public void setAuthors(Author[] authors) {
		this.authors = new HashSet<Author>(Arrays.asList(authors));
//		this.lastChangedByUserId = userId;
	}

	public void setLastchangedbyuserid(int userid)
	{
		this.lastChangedByUserId = userid;
	}
	
	@Override
	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject json = new JSONObject();
		JSONArray jarr = new JSONArray();
		int i = 0;
		
		try
		{
			json.put("id", this.id);
			json.put("name", this.name);
			json.put("createdByUserId", this.createdByUserId);
			json.put("lastChangedByUserId", this.lastChangedByUserId);

			// package sources into jsonarray
//			Iterator<SocialMediaSource> it = this.sources.iterator();
//			while (it.hasNext())
//			{
//				SocialMediaSource s = (SocialMediaSource) it.next();
//				JSONObject j = new JSONObject();
//				j.put("type", s.toString());
//				jarr.put(j);
//			}
//			json.put("sources", jarr); // jsonarray of the sources

			jarr = new JSONArray(); 
			Iterator<Author> auth = this.authors.iterator();
			while (auth.hasNext())
			{
				Author a = auth.next();
				jarr.put(a.toJSONObject());
			}
			json.put("authors", jarr);
			
			json.put("queryString", this.queryString);
			json.put("latitude", this.latitude);
			json.put("longitude", this.longitude);
			json.put("radiusKm", this.radiusKm);
			json.put("isActive", this.isActive);
			json.put("isRecording", this.isRecording);
			
			jarr = new JSONArray();
			Iterator<TwitterQuery> tq = this.queries.iterator();
			while (tq.hasNext())
			{
				TwitterQuery t = tq.next();
				jarr.put(t.toJSONObject());
			}
			json.put("queries", jarr);
			
			return json;
		} catch (JSONException je)
		{
			je.printStackTrace();
			return null;
		}
	}
	
}
