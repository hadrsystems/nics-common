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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.Org;
import edu.mit.ll.nics.common.entity.OrgOrgType;
import edu.mit.ll.nics.common.entity.OrgType;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.OrgDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.OrgOrgTypeRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.OrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.OrgTypeRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserOrgRowMapper;


public class OrgDAOImpl extends GenericDAO implements OrgDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;
    
    private String ADMIN_ID = "adminsystemroleid";
    private String SUPER_ID = "supersystemroleid";
    
    @Override
    public void initialize() {
        log = LoggerFactory.getLogger(OrgDAOImpl.class);
    	this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    /** getOrgAdmins 
     * @param int orgid
   	 * @return String - return a comma delimited list of email addresses
   	 */
    public List<String> getOrgAdmins(int orgid){
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.selectFromTable(SADisplayConstants.VALUE)
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.CONTACT_TYPE_ID)
				.and().equals(SADisplayConstants.ORG_ID)
				.and().equals(SADisplayConstants.ACTIVE)
				.and().equals(SADisplayConstants.USER_ENABLED, SADisplayConstants.USER_ENABLED_PARAM, null)
				.and().open().equals(SADisplayConstants.SYSTEM_ROLE_ID, ADMIN_ID, null) //open = (
				.or().equals(SADisplayConstants.SYSTEM_ROLE_ID, SUPER_ID, null)
				.close(); //close = )
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(SADisplayConstants.CONTACT_TYPE_ID, SADisplayConstants.EMAIL_TYPE_ID);
        params.put(SADisplayConstants.ORG_ID, orgid);
        params.put(ADMIN_ID, SADisplayConstants.ADMIN_ROLE_ID);
        params.put(SUPER_ID, SADisplayConstants.SUPER_ROLE_ID);
        params.put(SADisplayConstants.ACTIVE, true);
        params.put(SADisplayConstants.USER_ENABLED_PARAM, true);
		
		return this.template.queryForList(queryModel.toString(), params, String.class);
	}
    
    /** getOrgAdmins 
     * @param int orgid
   	 * @return String - return a comma delimited list of email addresses
   	 */
    public List<Integer> getOrgAdmins(int orgid, int workspaceId){
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_TABLE)
				.selectFromTable(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.where().equals(SADisplayConstants.ORG_ID)
				.and().equals(SADisplayConstants.ACTIVE)
				.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED)
				.and().equals(SADisplayConstants.USER_ENABLED, SADisplayConstants.USER_ENABLED_PARAM, null)
				.and().open().equals(SADisplayConstants.SYSTEM_ROLE_ID, ADMIN_ID, null) //open = (
				.or().equals(SADisplayConstants.SYSTEM_ROLE_ID, SUPER_ID, null)
				.close(); //close = )
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(SADisplayConstants.ORG_ID, orgid);
        params.put(ADMIN_ID, SADisplayConstants.ADMIN_ROLE_ID);
        params.put(SUPER_ID, SADisplayConstants.SUPER_ROLE_ID);
        params.put(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true);
        params.put(SADisplayConstants.ACTIVE, true);
        params.put(SADisplayConstants.USER_ENABLED_PARAM, true);
		
		return this.template.queryForList(queryModel.toString(), params, Integer.class);
	}
	
    /** getOrgFolders
     * @param int orgid
   	 * @return List<String> - return a list of folder ids that belong to the given organization
   	 */
	public List<String> getOrgFolderIds(int orgid){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_FOLDER_TABLE)
				.selectFromTable(SADisplayConstants.FOLDER_ID)
				.where().equals(SADisplayConstants.ORG_ID);
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(SADisplayConstants.ORG_ID, orgid);
		return this.template.queryForList(queryModel.toString(), params, String.class);
	}
	
	/** getUserOrgs
     * @param id - userid
   	 * @return List<Org> - List of organizations this user belongs to
   	 */
    public List<Org> getUserOrgs(int userid, int workspaceId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED)
    			.and().equals(SADisplayConstants.WORKSPACE_ID).orderBy(SADisplayConstants.ORG_NAME);
    	
    	JoinRowCallbackHandler<Org> handler = getHandlerWith(new UserOrgRowMapper());
        template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_ID, userid)
        	.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true)
        	.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), 
            handler);
        return handler.getResults();
    }
    
    /** getUserOrgs
     * @param id - userid
   	 * @return List<Org> - List of organizations this user belongs to
   	 */
    public List<Org> getAdminOrgs(int userid, int workspaceId){
    	String roles = StringUtils.join(Arrays.asList(new Integer(SADisplayConstants.SUPER_ROLE_ID).toString(),
    			new Integer(SADisplayConstants.ADMIN_ROLE_ID).toString()), ",");
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.join(SADisplayConstants.ORG_ORGTYPE_TABLE).using(SADisplayConstants.ORG_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED)
    			.and().equals(SADisplayConstants.WORKSPACE_ID)
    			.and().inAsSQL(SADisplayConstants.SYSTEM_ROLE_ID, roles)
    			.orderBy(SADisplayConstants.ORG_NAME);
    	
    	System.out.println("ADMIN ORGS: " + queryModel.toString());
    	
    	JoinRowCallbackHandler<Org> handler = getHandlerWith(new UserOrgRowMapper(),new OrgOrgTypeRowMapper());
        template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_ID, userid)
        	.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true)
        	.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), 
            handler);
        return handler.getResults();
    }
    
    // TODO: Old API call, revisit necessity of this call
    // this version returns key/value pairs, whereas the old one returned just an array of values. New one is better,
    // but will break Mobile calls to it
    public List<Map<String, Object>> getUserOrgsWithOrgName(int userid, int workspaceId) {
    	List<String> fields = Arrays.asList(SADisplayConstants.USER_ORG_ID, SADisplayConstants.ORG_NAME,
    			SADisplayConstants.ORG_ID, SADisplayConstants.SYSTEM_ROLE_ID, SADisplayConstants.DEFAULT_ORG,
    			SADisplayConstants.COUNTRY, SADisplayConstants.STATE);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
    			.selectFromTable(fields)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.ENABLED)
    			.and().equals(SADisplayConstants.WORKSPACE_ID)
    			.orderBy(SADisplayConstants.ORG_NAME);
    	
    	return template.queryForList(queryModel.toString(),
    		new MapSqlParameterSource(SADisplayConstants.USER_ID, userid)
    			.addValue(SADisplayConstants.ENABLED, true)
    			.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
    }
    
    public List<Org> getUserOrgsByUsername(String username, int workspaceId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.USER_NAME)
    			.and().equals(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	JoinRowCallbackHandler<Org> handler = getHandlerWith(new UserOrgRowMapper());
        template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_NAME, username)
        		.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ENABLED, true)
        		.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), 
        		handler);
        return handler.getResults();
    }
    
    public List<String> getOrgNames(){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
    			.selectDistinctFromTable(SADisplayConstants.ORG_NAME);
    	
    	return this.template.queryForList(queryModel.toString(), new MapSqlParameterSource(), String.class);
    }
    
    public List<Org> getOrgsByType(int orgtypeid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_ORGTYPE_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.ORG_ORGTYPE_TABLE).using(SADisplayConstants.ORG_ID)
    			.where().equals(SADisplayConstants.ORG_TYPE_ID);
    	
    	JoinRowCallbackHandler<Org> handler = getHandlerWith();
    	
    	this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.ORG_TYPE_ID, orgtypeid), handler);
    	return handler.getResults();
    }
    
    public int removeOrgOrgType(int orgId, int orgTypeId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_ORGTYPE_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.ORG_ID).and().equals(SADisplayConstants.ORG_TYPE_ID);
		
		return this.template.update(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.ORG_TYPE_ID, orgTypeId)
				.addValue(SADisplayConstants.ORG_ID, orgId));
	}
    
    public List<Org> getOrganizations(){
    	List<Org> orgs = new ArrayList<Org>();
    	
    	//Search for Orgs associated with Org Types
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
				.selectAllFromTable().join(SADisplayConstants.ORG_ORGTYPE_TABLE).using(SADisplayConstants.ORG_ID);
		
		JoinRowCallbackHandler<Org> handler = getHandlerWith(new OrgOrgTypeRowMapper());
		this.template.query(queryModel.toString(), new MapSqlParameterSource(), handler);
    	
		orgs.addAll(handler.getResults());
    	
    	//Search for Orgs without Org Types
    	JoinRowCallbackHandler<Org> allOrgsHandler = getHandlerWith();
    	QueryModel allOrgQuery = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
    			.selectAllFromTable().where().notIn(SADisplayConstants.ORG_ID, 
						QueryManager.createQuery(SADisplayConstants.ORG_ORGTYPE_TABLE)
						.selectFromTable(SADisplayConstants.ORG_ID).toString());
		
    	this.template.query(allOrgQuery.toString(), new MapSqlParameterSource(), allOrgsHandler);
    	
		orgs.addAll(allOrgsHandler.getResults());
		return orgs;
	}
    
    public List<OrgOrgType> getOrgTypes(int orgId){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.ORG_ORGTYPE_TABLE)
    			.selectAllFromTable().where().equals(SADisplayConstants.ORG_ID);
    	
    	JoinRowCallbackHandler<OrgOrgType> handler = getOrgOrgTypeHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.ORG_ID, orgId), handler);
		
		return handler.getResults();
    	
    }
    
	public List<OrgType> getOrgTypes(){
		
		QueryModel query = QueryManager.createQuery(SADisplayConstants.ORG_TYPE_TABLE)
				.selectAllFromTable().orderBy(SADisplayConstants.ORG_TYPE_NAME).asc();
		
		JoinRowCallbackHandler<OrgType> handler = getOrgTypeHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(), handler);
		
		return handler.getResults();
	}
	
	public List<OrgOrgType> getOrgOrgTypes() {
		
		QueryModel query = QueryManager.createQuery(SADisplayConstants.ORG_ORGTYPE_TABLE)
				.selectAllFromTable().orderBy(SADisplayConstants.ORG_TYPE_ID).asc();
		
		JoinRowCallbackHandler<OrgOrgType> handler = getOrgOrgTypeHandlerWith();
		try{
			this.template.query(query.toString(), new MapSqlParameterSource(), handler);
			
			return handler.getResults();
		}catch(Exception e){
			return new ArrayList<OrgOrgType>();
		}
	}
	
	public Org getOrganization(String name){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
				.selectAllFromTableWhere().equals(SADisplayConstants.ORG_NAME);
		
		JoinRowCallbackHandler<Org> handler = getHandlerWith();
		this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.ORG_NAME, name), handler);
    	
		Org ret = null;
		try {
			ret = handler.getSingleResult();
		} catch(Exception e) {
			log.error("Exception querying for Organization(#0): #1", name, e.getMessage());
		}
		
		return ret;
	}
	
	public Org getOrganization(int orgId){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
				.selectAllFromTableWhere().equals(SADisplayConstants.ORG_ID);
		
		JoinRowCallbackHandler<Org> handler = getHandlerWith();
		this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.ORG_ID, orgId), handler);
    	
		Org ret = null;
		try {
			ret = handler.getSingleResult();
		} catch(Exception e) {
			log.error("Exception querying for Organization(#0): #1", orgId, e.getMessage());
		}
		
		return ret;
	}
	

	public String getOrgNameByAgency(String agency){
		try{
			QueryModel query = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
					.selectFromTableWhere(SADisplayConstants.ORG_NAME)
					.equals(SADisplayConstants.PREFIX, agency);
			
			return this.template.queryForObject(query.toString(), 
					new MapSqlParameterSource(SADisplayConstants.PREFIX, agency), String.class);
		}catch(Exception e){
			log.info("No organization was found for agency #0", agency);
			return "No Organization was found.";
		}
	}
	
	public Org getLoggedInOrg(int userid){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
				.join(SADisplayConstants.USERSESSION_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.join(SADisplayConstants.CURRENT_USERSESSION_TABLE).using(SADisplayConstants.USERSESSION_ID)
				.where().equals(SADisplayConstants.CURRENT_USERSESSION_USER_ID);
		
		JoinRowCallbackHandler<Org> handler = getHandlerWith();
		this.template.query(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.CURRENT_USERSESSION_USER_ID, userid), 
				handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			log.info("No Orgnaization was found for userid #0. User may not be logged in.", userid);
		}
		return null;
	}
	
	public String getDistributionList(int incidentid){
		//select distribution from org join userorg using(orgid) join usersession using(userorgid) join incident using(usersessionid) where incidentid=851;
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
				.selectFromTable(SADisplayConstants.DISTRIBUTION)
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
				.join(SADisplayConstants.USER_SESSION_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.USERSESSION_ID)
				.where().equals(SADisplayConstants.INCIDENT_ID);
		try{
			return this.template.queryForObject(queryModel.toString(), 
					new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentid), 
					String.class);
		}catch(Exception e){
			log.info("No distribution list was found for incident id #0", incidentid);
		}
		return null;
	}
	
	public int addOrg(Org org) throws Exception{
		if(org.getOrgId() > 0) {
			QueryModel query = QueryManager.createQuery(SADisplayConstants.ORG_TABLE).update()
					.equals(SADisplayConstants.ORG_NAME).comma()
					.equals(SADisplayConstants.COUNTY).comma()
					.equals(SADisplayConstants.STATE).comma()				
					.equals(SADisplayConstants.PREFIX).comma()			
					.equals(SADisplayConstants.DISTRIBUTION).comma()			
					.equals(SADisplayConstants.DEFAULT_LAT).comma()			
					.equals(SADisplayConstants.DEFAULT_LON).comma()			
					.equals(SADisplayConstants.COUNTRY)
					.where().equals(SADisplayConstants.ORG_ID);
			
			BeanPropertySqlParameterSource map = new BeanPropertySqlParameterSource(org);
			
			int ret = -1;
			try {
				ret = this.template.update(query.toString(), map);
			} catch (Exception e) {
				throw new Exception("Error updating the org with id: " + 
						org.getOrgId() + ": " + e.getMessage());
			}
			
			return org.getOrgId();
		}else{
			List<String> fields = Arrays.asList(
		    		SADisplayConstants.ORG_ID, SADisplayConstants.ORG_NAME, SADisplayConstants.COUNTY,
		    		SADisplayConstants.STATE, SADisplayConstants.PREFIX, SADisplayConstants.DISTRIBUTION,
		    		SADisplayConstants.DEFAULT_LAT, SADisplayConstants.DEFAULT_LON, SADisplayConstants.COUNTRY);
		    		
	    	QueryModel model = QueryManager.createQuery(SADisplayConstants.ORG_TABLE).insertInto(fields);
	    	
	    	//generate an orgid if not set
	    	if (org.getOrgId() <= 0) {
	    		QueryModel idModel = QueryManager.createQuery("org_seq").selectNextVal();
	    		int orgId = this.template.queryForObject(idModel.toString(), new MapSqlParameterSource(), Integer.class);
	    		org.setOrgId(orgId);
	    	}
	    	
	    	try {
	    		this.template.update(model.toString(), new BeanPropertySqlParameterSource(org));
	    		return org.getOrgId();
	    	} catch(Exception e) {
	    		throw new Exception("Unhandled exception while persisting Org entity:", e);
	    	}
		}
	}
	
	public int addOrgOrgType(int orgId, int orgTypeId) throws Exception{
		List<String> fields = Arrays.asList(
	    		SADisplayConstants.ORG_ORGTYPE_ID, SADisplayConstants.ORG_TYPE_ID,
	    		SADisplayConstants.ORG_ID);
	    		
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.ORG_ORGTYPE_TABLE).insertInto(fields);
    	
    	//generate an orgid if not set
    	QueryModel orgOrgTypeIdQuery = QueryManager.createQuery("hibernate_sequence").selectNextVal();
		int orgOrgTypeId = this.template.queryForObject(orgOrgTypeIdQuery.toString(), new MapSqlParameterSource(), Integer.class);
		
    	try {
    		return this.template.update(model.toString(), new MapSqlParameterSource(
    				SADisplayConstants.ORG_ORGTYPE_ID, orgOrgTypeId)
    				.addValue(SADisplayConstants.ORG_TYPE_ID, orgTypeId)
    				.addValue(SADisplayConstants.ORG_ID, orgId));
    	} catch(Exception e) {
    		throw new Exception("Unhandled exception while persisting Org entity:", e);
    	}
	}
    
    /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<UserOrg>
	   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private JoinRowCallbackHandler<Org> getHandlerWith(JoinRowMapper... mappers) {
	  return new JoinRowCallbackHandler(new OrgRowMapper(), mappers);
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private JoinRowCallbackHandler<OrgType> getOrgTypeHandlerWith(JoinRowMapper... mappers) {
	  return new JoinRowCallbackHandler(new OrgTypeRowMapper(), mappers);
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private JoinRowCallbackHandler<OrgOrgType> getOrgOrgTypeHandlerWith(JoinRowMapper... mappers) {
	  return new JoinRowCallbackHandler(new OrgOrgTypeRowMapper(), mappers);
  }
}
