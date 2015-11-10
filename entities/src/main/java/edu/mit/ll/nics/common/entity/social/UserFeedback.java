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

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.NaturalId;
import org.json.JSONException;
import org.json.JSONObject;

import edu.mit.ll.nics.common.entity.SADisplayMessageEntity;
import edu.mit.ll.nics.common.entity.SADisplayPersistedEntity;

/**
 * @author ri18384
 *
 */
@Entity
public class UserFeedback extends SADisplayMessageEntity implements SADisplayPersistedEntity {

	@Id
	@GeneratedValue
	private long id;
	
	@NaturalId	
	@ManyToOne
	private Message message;

	@NaturalId	
	@ManyToOne
	private SocialMediaTopic topic;		
	
	@NaturalId
	@Column(nullable=false)
	private int userId;
	
	@Column(nullable=false)
	private Date createdTime;
	
	private double relevanceScore;
	
	
	@Override
	public JSONObject toJSONObject() throws JSONException 
	{
		JSONObject json = new JSONObject();
		
		try
		{
			json.put("id", this.id);
			json.put("message", this.message.toJSONObject());
			json.put("topic", this.topic.toJSONObject());
			json.put("userId", this.userId);
			json.put("createdTime", this.createdTime);
			json.put("relevanceScore", this.relevanceScore);
			
			return json;
		} catch (JSONException je)
		{
			je.printStackTrace();
			return null;
		}
	}
} // class userFeedback

