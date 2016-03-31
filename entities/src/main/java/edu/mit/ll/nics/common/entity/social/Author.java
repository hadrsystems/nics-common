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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.SADisplayMessageEntity;
import edu.mit.ll.nics.common.entity.SADisplayPersistedEntity;

/**
 * @author ri18384
 * A known account that publishes messages
 */
@Entity
public class Author  extends SADisplayMessageEntity implements SADisplayPersistedEntity{
	
	public Author(String accountName, String fullName) {
		this.accountName = accountName;
		this.fullName = fullName;
	}
	
	/**
	 * for Hibernate
	 */
	private Author() {		
	}

	// TODO add int field for account userid
	
	@Id
	@GeneratedValue
	private int id;
	
	// e.g. "@PatchWesternPA2"
	@Column(nullable = false)
	private String accountName;
	
	// e.g. "Patch: Western PA"
	@Column(nullable = false)
	private String fullName;

	public String getAccountName()
	{
		return this.accountName;
	}
	
	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}
	
	public String getFullName()
	{
		return this.fullName;
	}
	
	public void setFullName(String fullName)
	{
		this.fullName = fullName;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	// Override of equals allows you to do SocialMediaTopic.getAuthors().contains( anAuthor )
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Author))
			return false;
		Author other = (Author) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
	@Override
	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject obj = new JSONObject();
		
		try
		{
			obj.put("id", this.id);
			obj.put("accountName", this.accountName);
			obj.put("fullName", this.fullName);
			return obj;
		} catch (JSONException ex)
		{
			ex.printStackTrace();
			return null;
		}
	} 
	
} // class Author
