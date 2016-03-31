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

import org.json.JSONObject;

public class SortEntity extends SADisplayMessageEntity implements SADisplayPersistedEntity{
	
	private String action;
	private String sortClass;
	private Object parentId;
	private Object newParentId;
	private Object id;
	private int index = -1;
	private int nextSiblingIndex = -1;
	private int previousSiblingIndex = -1;
	private int workspaceId = -1;
	
	public static final String UPDATE_POS = "updatePosition";
	public static final String REMOVE = "remove";
	public static final String MOVE = "move";
	
	public SortEntity(){}
	
	public int getNextSiblingIndex(){
		return this.nextSiblingIndex;
	}
	
	public void setNextSiblingIndex(int index){
		this.nextSiblingIndex = index;
	}
	
	public int getPreviousSiblingIndex(){
		return this.previousSiblingIndex;
	}
	
	public void setPreviousSiblingIndex(int index){
		this.previousSiblingIndex = index;
	}
	
	public Object getParentId(){
		return this.parentId;
	}
	
	public void setParentId(Object id){
		this.parentId = id;
	}
	
	public Object getNewParentId(){
		return this.newParentId;
	}
	
	public void setNewParentId(Object id){
		this.newParentId = id;
	}
	
	public void setId(Object id){
		this.id = id;
	}
	
	public Object getId(){
		return this.id;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public String getAction(){
		return this.action;
	}
	
	public void setAction(String action){
		this.action = action;
	}
	
	public String getSortClass(){
		return this.sortClass;
	}
	
	public void setSortClass(String sortClass){
		this.sortClass = sortClass;
	}
	
	public void setWorkspaceId(int workspaceId){
		this.workspaceId = workspaceId;
	}
	
	public int getWorkspaceId(){
		return this.workspaceId;
	}
	
	public JSONObject toJSONObject(){
		JSONObject obj = new JSONObject();
		try{
			obj.put("parentId", this.parentId);
			obj.put("newParentId", this.newParentId);
			obj.put("id", this.id);
			obj.put("index", this.index);
			obj.put("nextSiblingIndex", this.nextSiblingIndex);
			obj.put("previousSiblingIndex", this.previousSiblingIndex);
			obj.put("action", this.action);
			obj.put("sortClass", this.sortClass);
			obj.put("workspaceid", this.workspaceId);
		}catch(Exception e){
			e.printStackTrace();
		}
		return obj;
	}
}