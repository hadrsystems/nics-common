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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.SystemRole;
import edu.mit.ll.nics.common.entity.UserOrg;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.UserOrgDAO;




import edu.mit.ll.nics.nicsdao.mappers.OrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.SystemRoleRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserOrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserRowMapper;





//import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.AutoCreate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
//import org.jboss.seam.log.Log;
//import org.jboss.seam.annotations.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


//@Name(SADisplayConstants.USER_ORG_DAO)
public class UserOrgDAOImpl extends GenericDAO implements UserOrgDAO {

    private org.slf4j.Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(UserOrgDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }

    /** getUserOrgById
     * @param id - orgId
     * @param int userId
   	 * @return UserOrg
   	 */
    public UserOrg getUserOrgById(int orgId, long userId, int workspaceId){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectAllFromTable().join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.SYSTEM_ROLE_TABLE).using(SADisplayConstants.SYSTEM_ROLE_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.USER_ORG_ORG_ID)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	JoinRowCallbackHandler<UserOrg> handler = getHandlerWith(new OrgRowMapper(), new UserRowMapper(), new SystemRoleRowMapper());
        template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_ID, userId)
        	.addValue(SADisplayConstants.USER_ORG_ORG_ID, orgId).addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
        List<UserOrg> userorgs = handler.getResults();
        
        //If multiple rows in table - return the first one
        if(userorgs != null && userorgs.size() > 0){
        	return userorgs.get(0);
        }
        return null;
    }
    
    /** getUserOrg
     * @param id - userOrgId
   	 * @return UserOrg
   	 */
    public UserOrg getUserOrg(int userOrgId){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectAllFromTable().where().equals(SADisplayConstants.USER_ORG_ID);
    	
    	JoinRowCallbackHandler<UserOrg> handler = getHandlerWith();
    	
    	MapSqlParameterSource paramMap = new MapSqlParameterSource(SADisplayConstants.USER_ORG_ID, userOrgId);
    	
        template.query(queryModel.toString(), paramMap, handler);
        List<UserOrg> userorgs = handler.getResults();
        
        //If multiple rows in table - return the first one
        if(userorgs != null && userorgs.size() > 0){
        	return userorgs.get(0);
        }
        return null;
    }
    
    public void updateUserOrg(int userOrgId, String jobTitle, String rank, String jobDesc, int sysRoleId){
    
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.update().equals(SADisplayConstants.JOB_TITLE).comma().equals(SADisplayConstants.RANK)
    			.comma().equals(SADisplayConstants.DESCRIPTION).comma().equals(SADisplayConstants.SYSTEM_ROLE_ID)
    			.where().equals(SADisplayConstants.USER_ORG_ID);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.USER_ORG_ID, userOrgId);
		map.addValue(SADisplayConstants.JOB_TITLE, jobTitle);
		map.addValue(SADisplayConstants.RANK, rank);
		map.addValue(SADisplayConstants.DESCRIPTION, jobDesc);
		map.addValue(SADisplayConstants.SYSTEM_ROLE_ID, sysRoleId);
		
    	try{
    		this.template.update(queryModel.toString(), map);
    	}
    	catch(Exception e){
    		log.info("Failed up update userorg account info.", e.getMessage());
    	}
    }
    
    /** getUserOrgById
     * @param id - orgId
     * @param int userId
   	 * @return UserOrg
   	 */
    public int getUserOrgId(int orgId, int userId){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.ORG_ID);
    	try{
	    	return template.queryForInt(queryModel.toString(), 
	            new MapSqlParameterSource(SADisplayConstants.USER_ID, userId)
	        	.addValue(SADisplayConstants.ORG_ID, orgId));
    	}catch(Exception e){}
    	
    	return -1;
    }
    
    /** getUserOrgByName
     * @param id - orgName
     * @param int username
   	 * @return UserOrg
   	 */
    public UserOrg getUserOrgByName(String username, String orgName, int workspaceId){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectAllFromTable().join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.SYSTEM_ROLE_TABLE).using(SADisplayConstants.SYSTEM_ROLE_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_NAME)
    			.and().equals(SADisplayConstants.ORG_NAME)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	JoinRowCallbackHandler<UserOrg> handler = getHandlerWith(new OrgRowMapper(), new UserRowMapper(), new SystemRoleRowMapper());
        template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_NAME, username).addValue(SADisplayConstants.ORG_NAME, orgName)
            	.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
        List<UserOrg> userorgs = handler.getResults();
        
        //If multiple rows in table - return the first one
        if(userorgs != null && userorgs.size() > 0){
        	return userorgs.get(0);
        }
        return null;
    }
    
    public int hasEnabledOrgs(int userid, int workspaceid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_WORKSPACE_TABLE)
    			.selectFromTable(SADisplayConstants.USER_ORG_WORKSPACE_ID)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.WORKSPACE_ID)
    			.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED);
    	try{
	    	List<Integer> workspaceids = template.queryForList(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.USER_ID, userid)
	    				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceid)
	    				.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true), Integer.class);
	    	
	    	if(workspaceids != null){
	    		return workspaceids.size();
	    	}
    	}catch(Exception e){
    		log.info("Could not find userorgs for userid #0 in workspace #1", userid, workspaceid);
    	}
    	return 0;
    }
    
    public List<Map<String, Object>> getEnabledUserOrgs(int orgid, int workspaceId){
    	//{ name: 'user'}, {name: 'userid'}, {name: 'userorgid'}, { name:'userorg_workspaceid'}
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.USER_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ID);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ORG_ID);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ORG_WORKSPACE_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			 .selectFromTable(fields.toString())
				 .join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				 .join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
				 .join(SADisplayConstants.SYSTEM_ROLE_TABLE).using(SADisplayConstants.SYSTEM_ROLE_ID)
				 .join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				 .where().equals(SADisplayConstants.ACTIVE)
				 .and().equals(SADisplayConstants.USER_ENABLED, SADisplayConstants.USER_ENABLED_PARAM, null)
				 .and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED)
				 .and().equals(SADisplayConstants.ORG_ID)
				 .and().equals(SADisplayConstants.WORKSPACE_ID);
		 
    	return template.queryForList(queryModel.toString(), 
		            new MapSqlParameterSource(SADisplayConstants.ACTIVE, true)
		 			.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true)
		 			.addValue(SADisplayConstants.USER_ENABLED_PARAM, true)
		 			.addValue(SADisplayConstants.ORG_ID, orgid)
		 			.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
	}
    
    public List<Map<String, Object>> getDisabledUserOrgs(int orgid, int workspaceId){
    	
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.USER_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ID);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ORG_ID);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ORG_WORKSPACE_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
   			 	 .selectFromTable(fields.toString())
				 .join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				 .join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
				 .join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				 .where().equals(SADisplayConstants.ACTIVE)
				 .and().equals(SADisplayConstants.ORG_ID)
				 .and().equals(SADisplayConstants.WORKSPACE_ID)
				 .and().open().equals(SADisplayConstants.USER_ENABLED, SADisplayConstants.USER_ENABLED_PARAM, null)
				 .or().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED).close();
				 
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.ACTIVE, true)
			.addValue(SADisplayConstants.ORG_ID, orgid)
			.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, false)
 			.addValue(SADisplayConstants.USER_ENABLED_PARAM, false)
			.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId);
		
		return template.queryForList(queryModel.toString(), map);
	}
    
    public boolean isUserRole(String username, int roleId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.USER_ORG_ID)
    			.join(SADisplayConstants.USER_TABLE).using(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.SYSTEM_ROLE_ID)
    			.and().equals(SADisplayConstants.USER_NAME)
    			.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED);
    	
    	try{
	    	List<Integer> ids = template.queryForList(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.SYSTEM_ROLE_ID, roleId)
	    			.addValue(SADisplayConstants.USER_NAME,  username)
	    			.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true), Integer.class);
	    	
	    	return (ids.size() > 0);
    	}catch(Exception e){
    		log.info("Could not verify #0 as a SUPER user.", username);
    	}
    	return false;
    }
    
    public List<SystemRole> getSystemRoles(){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.SYSTEM_ROLE_TABLE)
    			.selectAllFromTable();
		
		JoinRowCallbackHandler<SystemRole> handler = getSystemRoleHandlerWith();
        template.query(queryModel.toString(), new MapSqlParameterSource(), handler);
        return handler.getResults();
    }
    
    public List<Integer> getSuperUsers(){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_TABLE)
    			.selectDistinctFromTable(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.where().equals(SADisplayConstants.ACTIVE)
				.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED)
				.and().equals(SADisplayConstants.USER_ENABLED, SADisplayConstants.USER_ENABLED_PARAM, null)
				.and().equals(SADisplayConstants.SYSTEM_ROLE_ID);
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SADisplayConstants.SYSTEM_ROLE_ID, SADisplayConstants.SUPER_ROLE_ID);
        params.put(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true);
        params.put(SADisplayConstants.ACTIVE, true);
        params.put(SADisplayConstants.USER_ENABLED_PARAM, true);
		
		return this.template.queryForList(queryModel.toString(), params, Integer.class);
    }
    
    public int setUserOrgEnabled(int userOrgWorkspaceId, boolean enabled){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_WORKSPACE_TABLE)
    			.update().equals(SADisplayConstants.ENABLED)
    			.where().equals(SADisplayConstants.USER_ORG_WORKSPACE_ID);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.ENABLED, enabled)
    		.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ID, userOrgWorkspaceId);
    	
    	try{
    		return template.update(queryModel.toString(), map);
    	}catch(Exception e){
    		log.info("Could not set User Enabled with userorgworksapceid #0", userOrgWorkspaceId);
    	}
    	return -1;
    }
    
    public int getSystemRoleId(int userOrgId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.SYSTEM_ROLE_ID)
    			.where().equals(SADisplayConstants.USER_ORG_ID);
    	
    	try{
	    	return template.queryForObject(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.USER_ORG_ID, userOrgId), Integer.class);
    	}catch(Exception e){
    		log.info("Could not retrieve system role id for user org id #0", userOrgId);
    	}
    	return -1;
    }
    
    public int getSystemRoleId(String username, int userorgWorkspaceId){
    	
    	QueryModel orgQueryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_ORG_WORKSPACE_ID);
    	
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.SYSTEM_ROLE_ID)
    			.join(SADisplayConstants.USER_TABLE).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.USER_NAME)
    			.and().equalsInnerSelect(SADisplayConstants.ORG_ID, orgQueryModel.toString());
    	
    	try{
	    	return template.queryForObject(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.USER_NAME, username)
	    			.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ID, userorgWorkspaceId), Integer.class);
    	}catch(Exception e){
    		log.info("Could not retrieve system role id for username #0", username);
    	}
    	return -1;
    
    }
    
    public int getSystemRoleIdForUserOrg(String username, int userOrgId){
    	//select systemroleid from userorg join "user" using(userid) where username=<username> and orgid=(select orgid from userorg where userorgid=<userorgid>)
    	QueryModel orgQueryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.ORG_ID)
    			.where().equals(SADisplayConstants.USER_ORG_ID);
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.SYSTEM_ROLE_ID)
    			.join(SADisplayConstants.USER_TABLE).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.USER_NAME)
    			.and().equalsInnerSelect(SADisplayConstants.ORG_ID, orgQueryModel.toString());
    	
    	try{
	    	return template.queryForObject(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.USER_NAME, username)
	    			.addValue(SADisplayConstants.USER_ORG_ID, userOrgId), Integer.class);
    	}catch(Exception e){
    		log.info("Could not retrieve system role id for username #0", username);
    	}
    	return -1;
    }
    
   public int getNextUserOrgId(){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_SEQUENCE_TABLE).selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Could not retrieve next user org id #0", e.getMessage());
    	}
    	return -1;
    }
    
    /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<UserOrg>
	   */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JoinRowCallbackHandler<UserOrg> getHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new UserOrgRowMapper(), mappers);
    }
    
    /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<UserOrg>
	   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private JoinRowCallbackHandler<SystemRole> getSystemRoleHandlerWith(JoinRowMapper... mappers) {
  	 return new JoinRowCallbackHandler(new SystemRoleRowMapper(), mappers);
  }
}
