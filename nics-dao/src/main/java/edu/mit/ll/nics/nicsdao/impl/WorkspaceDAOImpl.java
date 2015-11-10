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
package edu.mit.ll.nics.nicsdao.impl;

import java.util.ArrayList;
import java.util.List;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.NicsSystem;
import edu.mit.ll.nics.common.entity.Workspace;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.WorkspaceDAO;
import edu.mit.ll.nics.nicsdao.mappers.NicsSystemRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.WorkspaceRowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


public class WorkspaceDAOImpl extends GenericDAO implements WorkspaceDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(WorkspaceDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    public int getWorkspaceId(String workspacename){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.WORKSPACE_TABLE)
    			.selectFromTable(SADisplayConstants.WORKSPACE_ID)
    			.where().equals(SADisplayConstants.WORKSPACE_NAME);
    	
    	try{
	    	return this.template.queryForInt(query.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.WORKSPACE_NAME, workspacename));
    	}catch(Exception e){
    		e.printStackTrace();
    		log.info("Could not find workspace id for workspace #0", workspacename);
    	}
    	return -1;
    }
    
	public String getWorkspaceName(int id){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.WORKSPACE_TABLE)
    			.selectFromTable(SADisplayConstants.WORKSPACE_NAME)
    			.where().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	try{
	    	return this.template.queryForObject(query.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, id), String.class);
    	}catch(Exception e){
    		e.printStackTrace();
    		log.info("Could not find workspace name for workspace id #0", id);
    	}
    	return "";
	}
	
	public List<Workspace> getAdminWorkspaces(int userorgid){
		
		List<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.ADMIN_ROLE);
		fields.add(SADisplayConstants.SUPER_ROLE);
		
		QueryModel query = QueryManager.createQuery(SADisplayConstants.WORKSPACE_TABLE)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.WORKSPACE_ID)
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.join(SADisplayConstants.SYSTEM_ROLE_TABLE).using(SADisplayConstants.SYSTEM_ROLE_ID)
				.where().equals(SADisplayConstants.USER_ORG_ID)
				.and().inAsString(SADisplayConstants.SYSTEM_ROLE_NAME, fields)
				.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED);
		
		JoinRowCallbackHandler<Workspace> handler = getHandlerWith();
    	
		template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.USER_ORG_ID, userorgid)
					.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true), 
					handler);
		
		return handler.getResults();
	}
	
	public List<Integer> getWorkspaceIds(){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.WORKSPACE_TABLE)
    			.selectFromTable(SADisplayConstants.WORKSPACE_ID);
		
		return this.template.queryForList(query.toString(), new MapSqlParameterSource(),  Integer.class);
	}
	
	public List<Integer> getUserOrgWorkspaceIds(int userOrgId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.USER_ORG_WORKSPACE_TABLE)
    			.selectFromTable(SADisplayConstants.USER_ORG_WORKSPACE_ID)
    			.where().equals(SADisplayConstants.USER_ORG_ID);
		
		return this.template.queryForList(query.toString(), 
				new MapSqlParameterSource(SADisplayConstants.USER_ORG_ID, userOrgId),  Integer.class);
	}
	
	public List<NicsSystem> getSystems(){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.SYSTEM_TABLE)
    			.selectAllFromTableWhere().equals(SADisplayConstants.ENABLED);
		
		JoinRowCallbackHandler<NicsSystem> handler = getSystemHandlerWith();
    	
		template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.ENABLED, true), handler);
		
		return handler.getResults();
	}
	
	public int getUserOrgWorkspaceId(int userOrgId, int workspaceId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.USER_ORG_WORKSPACE_TABLE)
    			.selectFromTable(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	try{
	    	return this.template.queryForInt(query.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.USER_ORG_ID, userOrgId)
	    			.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
    	}catch(Exception e){
    		e.printStackTrace();
    		log.info("Could not find userorg_workspace_id for userorgid #0 and workspaceid #1", userOrgId, workspaceId);
    	}
    	return -1;
	}
	
	public boolean workspaceIsEnabled(int workspaceid){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.WORKSPACE_TABLE)
				.selectFromTable(SADisplayConstants.ENABLED)
				.where().equals(SADisplayConstants.WORKSPACE_ID);
		
		return this.template.queryForObject(query.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceid), Boolean.class);
	}
	
	public List<Workspace> getSystemWorkspaces(String systemName){
		//select * from workspace join system_workspace using (workspaceid) join system using(systemid) where systemname='CA';
		QueryModel query = QueryManager.createQuery(SADisplayConstants.WORKSPACE_TABLE)
				.selectAllFromTable()
				.join(SADisplayConstants.SYSTEM_WORKSPACE).using(SADisplayConstants.WORKSPACE_ID)
				.join(SADisplayConstants.SYSTEM).using(SADisplayConstants.SYSTEM_ID)
				.where().equals(SADisplayConstants.SYSTEM_NAME)
				.and().equals(SADisplayConstants.WORKSPACE_ENABLED)
				.and().equals(SADisplayConstants.SYSTEM_ENABLED);
		
		JoinRowCallbackHandler<Workspace> handler = getHandlerWith();
    	
		template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.SYSTEM_NAME, systemName)
			.addValue(SADisplayConstants.WORKSPACE_ENABLED, true)
			.addValue(SADisplayConstants.SYSTEM_ENABLED, true), handler);
		
		return handler.getResults();
	}
	
	 /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<User>
	   */
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  private JoinRowCallbackHandler<Workspace> getHandlerWith(JoinRowMapper... mappers) {
	  	 return new JoinRowCallbackHandler(new WorkspaceRowMapper(), mappers);
	  }
	  
	  /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<User>
	   */
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  private JoinRowCallbackHandler<NicsSystem> getSystemHandlerWith(JoinRowMapper... mappers) {
	  	 return new JoinRowCallbackHandler(new NicsSystemRowMapper(), mappers);
	  }
}