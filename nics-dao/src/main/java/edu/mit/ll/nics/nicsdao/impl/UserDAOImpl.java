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
package edu.mit.ll.nics.nicsdao.impl;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.sql.DataSource;
import javax.xml.bind.DatatypeConverter;

import org.hibernate.annotations.Where;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.Contact;
import edu.mit.ll.nics.common.entity.User;
import edu.mit.ll.nics.common.entity.UserFeature;
import edu.mit.ll.nics.common.entity.UserOrg;
import edu.mit.ll.nics.common.entity.UserOrgWorkspace;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
//import edu.mit.ll.nics.sadisplay.common.util.Util;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.UserDAO;
import edu.mit.ll.nics.nicsdao.mappers.ContactRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.ContactTypeRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.CurrentUserSessionRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.FeatureRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.OrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserFeatureRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserOrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserRowMapper;

//@Name(SADisplayConstants.USER_DAO)
public class UserDAOImpl extends GenericDAO implements UserDAO {

    private Logger log;

    private PlatformTransactionManager txManager;
    private NamedParameterJdbcTemplate template;
    
    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(UserDAOImpl.class);
    	if(datasource != null) {
    		this.template = new NamedParameterJdbcTemplate(datasource);
    		txManager = new DataSourceTransactionManager(datasource);
    	}
    }
    /*
    public boolean reInitWithNewDatasource(DataSource newDatasource) {
    	boolean success = false;
    	try {
    		this.template = new NamedParameterJdbcTemplate(newDatasource);
    		txManager = new DataSourceTransactionManager(newDatasource);
    		success = true;
    	} catch (Exception e) {
    		log.error("Caught unhandled exception re-initializing template with custom datasource");
    	}
    	
    	return success;
    }*/
    
    public int create(String firstname, String lastname, String username, String password){
    	ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.USER_ID);
		fields.add(SADisplayConstants.FIRSTNAME);
		fields.add(SADisplayConstants.LASTNAME);
		fields.add(SADisplayConstants.USER_NAME);
		fields.add(SADisplayConstants.PASSWORD_HASH);
		fields.add(SADisplayConstants.ENABLED);
		fields.add(SADisplayConstants.ACTIVE);
		fields.add(SADisplayConstants.LAST_UPDATED);
		fields.add(SADisplayConstants.CREATED);
		
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.insertInto(fields);
		
		int userid = this.getNextUserId();
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.USER_ID, userid);
		map.addValue(SADisplayConstants.FIRSTNAME, firstname);
		map.addValue(SADisplayConstants.LASTNAME, lastname);
		map.addValue(SADisplayConstants.USER_NAME, username);
		//map.addValue(SADisplayConstants.PASSWORD_HASH, PasswordHash.instance().generateSaltedHash(password, username, "sha"));
		map.addValue(SADisplayConstants.PASSWORD_HASH, generateSaltedHash(password, username, "sha"));
		map.addValue(SADisplayConstants.ENABLED, false);
		map.addValue(SADisplayConstants.ACTIVE, true);
		map.addValue(SADisplayConstants.LAST_UPDATED, Calendar.getInstance().getTime());
		map.addValue(SADisplayConstants.CREATED, Calendar.getInstance().getTime());
		
		this.template.update(queryModel.toString(), map);
		
		return userid;
    }
    

    /**
     * 
     * Updates the users first and last name in the db
     * 
     * @param userid
     * @param firstName
     * @param lastNa,e
     * @return
     */
    
    public void updateNames(int userId, String firstName, String lastName){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_TABLE)
    			.update().equals(SADisplayConstants.FIRSTNAME).comma().equals(SADisplayConstants.LASTNAME)
    			.where().equals(SADisplayConstants.USER_ID);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.USER_ID, userId);
		map.addValue(SADisplayConstants.FIRSTNAME, firstName);
		map.addValue(SADisplayConstants.LASTNAME, lastName);
		
    	try{
    		this.template.update(queryModel.toString(), map);
    	}
    	catch(Exception e){
    		log.info("Failed up update user first and last name.", e.getMessage());
    	}
    
    }
    
    public boolean updateUserPW(int userId, String passwordHash){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_TABLE)
    			.update().equals(SADisplayConstants.PASSWORD_HASH).comma().equals(SADisplayConstants.PASSWORD_CHANGED)
    			.where().equals(SADisplayConstants.USER_ID);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.USER_ID, userId);
		map.addValue(SADisplayConstants.PASSWORD_HASH, passwordHash);
		map.addValue(SADisplayConstants.PASSWORD_CHANGED,Calendar.getInstance().getTime());
    	
    	try{
    		this.template.update(queryModel.toString(), map);
    	}
    	catch(Exception e){
    		log.info("Failed up update user password.", e.getMessage());
    		return false;
    	}
    	
    	
    	return true;
    }
    
    
    /**
     * 
     * TODO: This is taken from JBoss' PasswordHash class, which itself is deprecated. But we need
     * 		 to implement our own system wide password hashing scheme, especially since we're not
     * 		 using SEAM/JBoss anymore
     * 
     * @param password
     * @param saltPhrase
     * @param algorithm
     * @return
     */
    @Deprecated
    private String generateSaltedHash(String password, String saltPhrase, String algorithm)
    {
       try {        
          MessageDigest md = MessageDigest.getInstance(algorithm);
                   
          if (saltPhrase != null)
          {
             md.update(saltPhrase.getBytes());
             byte[] salt = md.digest();
             
             md.reset();
             md.update(password.getBytes());
             md.update(salt);
          }
          else
          {
             md.update(password.getBytes());
          }
          
          byte[] raw = md.digest();
          
          // TODO: verify results
          return DatatypeConverter.printBase64Binary(raw);
          //return Base64.encodeBytes(raw);
      } 
      catch (Exception e) {
          throw new RuntimeException(e);        
      } 
    }
    
    public boolean validateUser(String username, long userId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectFromTable(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.USER_NAME);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.USER_NAME, username);
    	
    	try{
    		return (template.queryForObject(queryModel.toString(), map , Long.class) == userId);
    	}catch(Exception e){
    		log.info("Could not find user #0", username);
    	}
		return false;
    }
    
    public int createContact(String type, String value, int userid){
    	int contacttypeid = this.getContactTypeId(type);

    	ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.USER_ID);
		fields.add(SADisplayConstants.VALUE);
		fields.add(SADisplayConstants.CREATED);
		fields.add(SADisplayConstants.ENABLED);
		fields.add(SADisplayConstants.CONTACT_ID);
		fields.add(SADisplayConstants.CONTACT_TYPE_ID);
		
		int contactid = this.getNextContactId();
    	
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.USER_ID, userid);
		map.addValue(SADisplayConstants.VALUE, value);
		map.addValue(SADisplayConstants.CREATED, Calendar.getInstance().getTime());
		map.addValue(SADisplayConstants.ENABLED, true);
		map.addValue(SADisplayConstants.CONTACT_ID, contactid);
		map.addValue(SADisplayConstants.CONTACT_TYPE_ID, contacttypeid);
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.insertInto(fields);
		
		this.template.update(queryModel.toString(), map);
		
		return contactid;
    }

    /** getMyUserID
     * @param username - String
   	 * @return Long
   	 */
    public Long getMyUserID(String username) {
        if(this.template == null) {
            this.initialize();
        }
        
        QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
        		.selectFromTableWhere(SADisplayConstants.USER_ID).equals(SADisplayConstants.USER_NAME);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put(SADisplayConstants.USER_NAME, username);
        try{
        	// TODO: replaced deprecated queryForLong with queryForObject, TEST
        	//return this.template.queryForLong(queryModel.toString(), params);
        	return this.template.queryForObject(queryModel.toString(), params, Long.class);
        }catch(Exception e){
        	log.info("Could not find user id for username " + username);
        }
        return null;
    }
    
    /** getUser
     * @param username - String
   	 * @return User
   	 */
	public User getUser(String username){
		 if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
				.left().join(SADisplayConstants.CONTACT_TABLE).using(SADisplayConstants.USER_ID)
				.left().join(SADisplayConstants.CONTACT_TYPE_TABLE).using(SADisplayConstants.CONTACT_TYPE_ID)
				.where().equals(SADisplayConstants.USER_NAME);
		
		JoinRowCallbackHandler<User> handler = getHandlerWith(
				new UserOrgRowMapper().attachAdditionalMapper(new OrgRowMapper()),
				new ContactRowMapper().attachAdditionalMapper(new ContactTypeRowMapper()));
        
		this.template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_NAME, username), 
            handler);
        
        try{
        	return handler.getSingleResult();
        }catch(Exception e){
        	log.info("No user was found with username " + username);
        	System.out.println("No user found with username?: " + username);
        }
        return null;
	}
	
    /** getUser
     * @param username - String
   	 * @return User
   	 */
	public User getUserWithSession(long userId){
		if(this.template == null) {
			this.initialize();
		}
		QueryModel query = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.join(SADisplayConstants.CURRENT_USERSESSION_TABLE).using(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.USER_ID, userId);
			
		JoinRowCallbackHandler<User> handler = getHandlerWith(new CurrentUserSessionRowMapper());
		
		template.query(query.toString(), query.getParameters(), handler);
		
		return handler.getSingleResult();
	}
	
	public List<User> findUser(String firstName, String lastName, boolean exact){
		if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable().where();
		
		if(exact){
			queryModel = queryModel.equals(SADisplayConstants.FIRSTNAME).and().equals(SADisplayConstants.LASTNAME);
		}else{
			queryModel = queryModel.ilike(SADisplayConstants.FIRSTNAME).value("'%" + firstName + "%'")
					.and().ilike(SADisplayConstants.LASTNAME).value("'%" + lastName + "%'");
		}
		
		JoinRowCallbackHandler<User> handler = getHandlerWith();
        
		this.template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.FIRSTNAME, firstName)
			.addValue(SADisplayConstants.LASTNAME, lastName), 
            handler);
        
        try{
        	return handler.getResults();
        }catch(Exception e){
        	log.info("No user was found with firstName " + firstName);
        }
        return null;
	}
	
	public List<User> findUserByLastName(String lastName, boolean exact){
		return this.findUserByField(SADisplayConstants.LASTNAME, lastName, exact);
	}
	
	public List<User> findUserByFirstName(String firstName, boolean exact){
		return this.findUserByField(SADisplayConstants.FIRSTNAME, firstName, exact);
	}
	
	private List<User> findUserByField(String field, String value, boolean exact){
		if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable().where();
		
		if(exact){
			queryModel = queryModel.equals(field);
		}else{
			queryModel = queryModel.ilike(field).value("'%" + value + "%'");
		}
		
		JoinRowCallbackHandler<User> handler = getHandlerWith();
        
		this.template.query(queryModel.toString(), 
            new MapSqlParameterSource(field, value), 
            handler);
        
        try{
        	return handler.getResults();
        }catch(Exception e){
        	log.info("No user was found with value " + value);
        }
        return null;
	}

    /** getUserById
     * @param useId - Long
   	 * @return User
   	 */
	public User getUserById(long userId){
		 if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
				.left().join(SADisplayConstants.CONTACT_TABLE).using(SADisplayConstants.USER_ID)
				.left().join(SADisplayConstants.CONTACT_TYPE_TABLE).using(SADisplayConstants.CONTACT_TYPE_ID)
				.where().equals(SADisplayConstants.USER_ID);
		
		JoinRowCallbackHandler<User> handler = getHandlerWith(
				new UserOrgRowMapper().attachAdditionalMapper(new OrgRowMapper()),
				new ContactRowMapper().attachAdditionalMapper(new ContactTypeRowMapper()));
        
		this.template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_ID, userId), 
            handler);
        
        try{
        	return handler.getSingleResult();
        }catch(Exception e){
        	log.info("No user was found with userid " + userId);
        }
        return null;
	}
	
	/** getUserById
     * @param useId - Long
   	 * @return User
   	 */
	public long getUserId(String username){
		 if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectFromTable(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.USER_NAME);
		
		return this.template.queryForObject(queryModel.toString(), 
	            new MapSqlParameterSource(SADisplayConstants.USER_NAME, username), Long.class);
	}
	
	/** getAllUserInfoById(
     * @param useId - Long
   	 * @return User
   	 */
	public User getAllUserInfoById(long userId){
		 if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.where().equals(SADisplayConstants.USER_ID);
		
		JoinRowCallbackHandler<User> handler = getHandlerWith();
        
		this.template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USER_ID, userId), 
            handler);
        
        try{
        	return handler.getSingleResult();
        }catch(Exception e){
        	log.info("No user was found with userid " + userId);
        }
        return null;
	}
	
	/** getUserSId
     * @param useSessionId - Long
   	 * @return User
   	 */
	public User getUserBySessionId(long userSessionId){
		 if(this.template == null) {
            this.initialize();
        }
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.join(SADisplayConstants.CURRENT_USERSESSION_TABLE).using(SADisplayConstants.USER_ID)
				.left().join(SADisplayConstants.CONTACT_TABLE).using(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.USERSESSION_ID);
		
		System.out.println("here1");
		
		JoinRowCallbackHandler<User> handler = getHandlerWith(
				new UserOrgRowMapper().attachAdditionalMapper(new OrgRowMapper()),
				new ContactRowMapper().attachAdditionalMapper(new ContactTypeRowMapper()));
        
		this.template.query(queryModel.toString(), 
            new MapSqlParameterSource(SADisplayConstants.USERSESSION_ID, userSessionId), 
            handler);
		
		
        
        try{
        	return handler.getSingleResult();
        }catch(Exception e){
        	log.info("No user was found with userid " + userSessionId);
        }
        return null;
	}
	
	 /** getUserFeaturesState
     * @param id - userid
   	 * @return List<String> - a list of features represented as JSON
   	 */
    public List<UserFeature> getUserFeatures(int id, int workspaceId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_FEATURE)
    			.selectAllFromTable().join(SADisplayConstants.FEATURE).using(SADisplayConstants.FEATURE_ID)
    			.where().equals(SADisplayConstants.USER_ID)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	JoinRowCallbackHandler<UserFeature> handler = getUserFeatureHandlerWith(new FeatureRowMapper());
    	
        template.query(queryModel.toString(), 
        				new MapSqlParameterSource(SADisplayConstants.USER_ID, id)
        				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), 
        				handler);
        return handler.getResults();
	}
    
    public List<User> getActiveUsers(int workspaceId){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectAllFromTable()
    			.join(SADisplayConstants.CURRENT_USERSESSION_TABLE).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.WORKSPACE_ID);
		
    	JoinRowCallbackHandler<User> handler = getHandlerWith(new CurrentUserSessionRowMapper());
    	
		template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
		
		return handler.getResults();
    }
    
    public List<User> getAllEnabledUsers(){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectAllFromTableWhere().equals(SADisplayConstants.ENABLED);
    	
    	JoinRowCallbackHandler<User> handler = getHandlerWith();
    	
		template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.ENABLED, true), handler);
		
		return handler.getResults();
    }
    
    public int isEnabled(String username){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectFromTable(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.ENABLED)
    			.and().equals(SADisplayConstants.ACTIVE)
    			.and().equals(SADisplayConstants.USER_NAME);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.USER_NAME, username)
    		.addValue(SADisplayConstants.ENABLED, true)
    		.addValue(SADisplayConstants.ACTIVE, true);
    	
    	try{
    		return template.queryForObject(queryModel.toString(), map , Integer.class);
    	}catch(Exception e){
    		log.info("Could not find user #0", username);
    	}
		return -1;
    }
    
    public List<User> getEnabledUsersInWorkspace(int workspaceId) {
    	
    	StringBuilder sb = new StringBuilder();
		sb.append("SELECT u.* FROM public.user u, userorg o, userorg_workspace w ")
			.append("WHERE u.userid = o.userid AND o.userorgid = w.userorgid AND ")
			.append("w.enabled = true and u.active = true and w.workspaceid = ")
			.append(":" + SADisplayConstants.WORKSPACE_ID).append(" ")
			.append("ORDER BY u.userid ASC");
		log.info("getEnabledUsersInWorkspace:\n" + sb.toString() + "\n=====\n");
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				//.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.where().equals(SADisplayConstants.ENABLED)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	//log.info("getEnabledUsersInWorkspace:\n" + queryModel.toString() + "\n=====\n");
    	
    	JoinRowCallbackHandler<User> handler = getHandlerWith();   	
    	
    	
    	template.query(sb.toString(), //queryModel.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId),
    			 handler);
    	
    	return handler.getResults();
    }
    
    public List<User> getUsersNotInOrg(int notInOrgId){
    	
    	/*select username from "user" join userorg using(userid) join userorg_workspace using(userorgid) where orgid<>3 
    	 * and userid not in (select userid from "user" join userorg using(userid) join userorg_workspace using(userorgid) where orgid=3);
    	 */
    	
    	/*QueryModel queryUserId = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectFromTable(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.ORG_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().notEqual(SADisplayConstants.ORG_ID)
    			.and().notIn(SADisplayConstants.USER_ID, queryUserId.toString());*/
    	
    	//select * from "user" where active='t' and userid not in(select userid from userorg where orgid=40) 
    	
    	QueryModel queryUserId = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
    			.selectFromTable(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.ORG_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
			.selectAllFromTable()
			.where().equals(SADisplayConstants.ACTIVE)
			.and().notIn(SADisplayConstants.USER_ID, queryUserId.toString());
    	
    	JoinRowCallbackHandler<User> handler = getHandlerWith();
    	
		template.query(queryModel.toString(), new MapSqlParameterSource(
				SADisplayConstants.ORG_ID, notInOrgId)
				.addValue(SADisplayConstants.ACTIVE, true), handler);
		
		return handler.getResults();
    }
    
    @Override
	public List<User> getUsers(String orgName) {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.where().equals(SADisplayConstants.ORG_NAME);
		
		JoinRowCallbackHandler<User> handler = getHandlerWith();
		
		this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.ORG_NAME, orgName), handler);
		
		return handler.getResults();
	}
    
   	public List<Map<String, Object>> getUsers(int orgId, int workspaceId) {
   		StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.USER_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ID);
   		
       	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
   				.selectFromTable(fields.toString())
   				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
       			.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
   				.where().equals(SADisplayConstants.ORG_ID)
   				.and().equals(SADisplayConstants.WORKSPACE_ID);
   		
   		return template.queryForList(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.ORG_ID, orgId)
				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
   	}
    
    public boolean verifyEmailAddress(String username, String email){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
    			.selectFromTable(SADisplayConstants.VALUE)
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.VALUE)
    			.and().equals(SADisplayConstants.USER_NAME);
    	
    	try{
    		this.template.queryForObject(queryModel.toString(),
        			new MapSqlParameterSource(SADisplayConstants.VALUE, email)
        			.addValue(SADisplayConstants.USER_NAME, username),
        			String.class);
    		return true;
    	}catch(Exception e){
    		log.info("No email found for " + username + " matching " + email);
    	}
    	return false;
    }
    
    public Contact getContact(String value){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
        		.where().equals(SADisplayConstants.VALUE, value);
    	
    	JoinRowCallbackHandler<Contact> handler = getContactHandlerWith(new UserRowMapper());
		
    	 if(this.template == null) {
    		 System.out.println("Initializing tempalate...");
             this.initialize();
         }
    	
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.VALUE, value), handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			return null;
		}
    }
    
    public boolean requiresPasswordChange(int userid, int days){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectFromTableWhere(SADisplayConstants.PASSWORD_CHANGED)
    			.equals(SADisplayConstants.USER_ID);
    	
    	Date passwordChanged = this.template.queryForObject(queryModel.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.USER_ID, userid), 
    			Date.class);
    	
    	return (getTimeElapsedInDays(passwordChanged) > days);
    }

    /**
     * Calculates the number of days elapsed since the date specified
     * 
     * TODO: Will be refactored when there's a requiresPasswordChange API endpoint, for now this is a duplicate
     * 		 of the method in sadisplay.common.util.Util
     * @param d
     * @return
     */
    @Deprecated
	private int getTimeElapsedInDays(Date d) {
		SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        DateTime then = null;
        Date utc = null;
        try{
            utc = isoFormat.parse(d.toString());
        }catch(Exception e){
            log.info("Could not parse date.");
        }
        if(utc != null){
            then = new DateTime(utc);
        }else{
            then = new DateTime(d);
        }

        DateTime now = new DateTime(DateTimeZone.UTC);
        Period period = new Period(then, now);
        int days = 0;
        if(period.getYears() > 0){
            days += (period.getYears() * 365);
        }
        if(period.getMonths() > 0){
            days += (period.getMonths() * 30); //close enough? 
        }
        return (days + period.getDays());

	}

	public List<String> getUsersWithPermission(int collabroomid, int roleid){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectFromTable(SADisplayConstants.USER_NAME)
				.join(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE).using(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.COLLAB_ROOM_ID)
				.and().equals(SADisplayConstants.SYSTEM_ROLE_ID);
		
		return this.template.queryForList(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomid)
				.addValue(SADisplayConstants.SYSTEM_ROLE_ID, roleid), String.class);
	}
	
	public int getContactTypeId(String name){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TYPE_TABLE)
				.selectFromTableWhere(SADisplayConstants.CONTACT_TYPE_ID)
				.equals(SADisplayConstants.TYPE);
		try{
			return this.template.queryForInt(queryModel.toString(), 
					new MapSqlParameterSource(SADisplayConstants.TYPE, name));
		}catch(Exception e){
			log.info("Could not retrieve the contact id for contact name " + name);
		}
		return -1;
	}
	
	public List<Contact> getContacts(String username, String type){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.CONTACT_TYPE_TABLE).using(SADisplayConstants.CONTACT_TYPE_ID)
				.where().equals(SADisplayConstants.USER_NAME)
				.and().equals(SADisplayConstants.TYPE)
				.and().equals(SADisplayConstants.CONTACT_ENABLED);
		
		JoinRowCallbackHandler<Contact> handler = getContactHandlerWith();
		
		this.template.query(queryModel.toString(), 
			new MapSqlParameterSource(SADisplayConstants.USER_NAME, username)
			.addValue(SADisplayConstants.TYPE, type)
			.addValue(SADisplayConstants.CONTACT_ENABLED, true),
			handler);
		
		return handler.getResults();
	}
	
	public List<Contact> getAllUserContacts(String username){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.selectAllFromTable()
				.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.USER_NAME)
				.and().equals(SADisplayConstants.CONTACT_ENABLED);
		
		JoinRowCallbackHandler<Contact> handler = getContactHandlerWith();
		
		this.template.query(queryModel.toString(), 
			new MapSqlParameterSource(SADisplayConstants.USER_NAME, username)
			.addValue(SADisplayConstants.CONTACT_ENABLED, true),
			handler);
		
		return handler.getResults();
	}
	
	public boolean addContact(String username, int contactTypeId, String value){
		
		int userId = -1;
		int contactId = -1;
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectFromTable(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.USER_NAME);
		
		userId = this.template.queryForObject(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.USER_NAME, username), Integer.class);
		
		QueryModel idModel = QueryManager.createQuery("contact_seq").selectNextVal();
		contactId = this.template.queryForObject(idModel.toString(), new MapSqlParameterSource(), Integer.class);
		
		if(userId == -1){
			return false;
		}
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.CONTACT_ID);
		fields.add(SADisplayConstants.USER_ID);
		fields.add(SADisplayConstants.CONTACT_TYPE_ID);
		fields.add(SADisplayConstants.ENABLED);
		fields.add(SADisplayConstants.VALUE);
		
		
		queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.insertInto(fields);
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.CONTACT_ID, contactId);
		map.addValue(SADisplayConstants.USER_ID, userId);
		map.addValue(SADisplayConstants.CONTACT_TYPE_ID, contactTypeId);
		map.addValue(SADisplayConstants.ENABLED, true);
		map.addValue(SADisplayConstants.VALUE, value);
		
		System.out.println(queryModel.toString());
		
		int updated = this.template.update(queryModel.toString(), map);
		
		if(updated == 0){
			return false;
		}
		
		return true;
	}
	
	public boolean deleteContact(String username, int contactTypeId, String value){
		
		int userId = -1;
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectFromTable(SADisplayConstants.USER_ID)
				.where().equals(SADisplayConstants.USER_NAME);
		
		userId = this.template.queryForObject(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.USER_NAME, username), Integer.class);
		
		if(userId == -1){
			return false;
		}
		
		queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.USER_ID)
				.and().equals(SADisplayConstants.CONTACT_TYPE_ID).and()
				.equals(SADisplayConstants.VALUE);
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.USER_ID, userId);
		map.addValue(SADisplayConstants.CONTACT_TYPE_ID, contactTypeId);
		map.addValue(SADisplayConstants.VALUE, value);
		
		try{
			this.template.update(queryModel.toString(), map);
			
		}catch(DataAccessException e){
			return false;
		}
		
		return true;
	}
	
	public String getUsernameFromEmail(String emailAddress){
					
		try{
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
					.selectFromTable(SADisplayConstants.USER_NAME)
					.join(SADisplayConstants.CONTACT_TABLE).using(SADisplayConstants.USER_ID)
					.where().equalsLower(SADisplayConstants.VALUE)
					.and().equals(SADisplayConstants.CONTACT_ENABLED);
			
			return this.template.queryForObject(queryModel.toString(), 
					new MapSqlParameterSource(SADisplayConstants.VALUE, emailAddress.toLowerCase())
						.addValue(SADisplayConstants.CONTACT_ENABLED, true), String.class);
		}catch(DataAccessException e){
			return null;
		}
		
	}
	
	 public int setUserEnabled(int userId, boolean enabled){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_TABLE)
    			.update().equals(SADisplayConstants.ENABLED)
    			.where().equals(SADisplayConstants.USER_ID);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.ENABLED, enabled)
    		.addValue(SADisplayConstants.USER_ID, userId);
    	
    	return template.update(queryModel.toString(), map);
    }
	
	public long createUserOrg(long userId, UserOrg userOrg) {
		/*
		 this.template.update(userorgQuery, new MapSqlParameterSource("userorgid", userOrg.getUserorgid())
						.addValue("userid", userId)
						.addValue("orgid", userOrg.getOrgid()) // TODO: getOrgId or getOrg().getOrgId() ?
						.addValue("systemroleid", userOrg.getSystemroleid()) // or getSystemRole().getSystemRoleId()
						.addValue("enabled", true) // this is the userorg.enabled field, which I believe is going away
						.addValue("created", null)  // created timestamp, defaults to now()
						.addValue("unit", null)
						.addValue("rank", userOrg.getRank())
						.addValue("description", userOrg.getDescription())
						.addValue("jobtitle", userOrg.getJobTitle())						
					);
		 */
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.USER_ORG_ID);
		fields.add(SADisplayConstants.USER_ID);
		fields.add(SADisplayConstants.ORG_ID);
		fields.add(SADisplayConstants.SYSTEM_ROLE_ID);
		//fields.add(SADisplayConstants.ENABLED);
		fields.add(SADisplayConstants.CREATED);
		//fields.add(SADisplayConstants.UNIT);
		fields.add(SADisplayConstants.RANK);
		fields.add(SADisplayConstants.DESCRIPTION);
		fields.add(SADisplayConstants.JOB_TITLE);
		
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
				.insertInto(fields);
		
		// TODO: what if the userorg object comes with one already?
		//int userorgid = this.getNextUserorgId();
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.USER_ORG_ID, userOrg.getUserorgid());
		map.addValue(SADisplayConstants.USER_ID, userId);
		map.addValue(SADisplayConstants.ORG_ID, userOrg.getOrgid());
		map.addValue(SADisplayConstants.SYSTEM_ROLE_ID, userOrg.getSystemroleid());
		//map.addValue(SADisplayConstants.ENABLED, true); // TODO: field is probably going away, it's not used		
		map.addValue(SADisplayConstants.CREATED, Calendar.getInstance().getTime());
		//map.addValue(SADisplayConstants.UNIT, userOrg.getu);
		map.addValue(SADisplayConstants.RANK, userOrg.getRank());
		map.addValue(SADisplayConstants.DESCRIPTION, userOrg.getDescription());
		map.addValue(SADisplayConstants.JOB_TITLE, userOrg.getJobTitle());
		
		this.template.update(queryModel.toString(), map);
		
		return userOrg.getOrgid();
	}
	
	public long createUserOrgWorkspace(UserOrgWorkspace userOrgWorkspace) {
		/*
		  	userorg_workspace_id | integer | not null
			 workspaceid          | integer | not null
			 userorgid            | integer | not null
			 enabled              | boolean | not null
			 defaultorg           | boolean | not null default false
		 */
		
		
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.USER_ORG_WORKSPACE_ID);
		fields.add(SADisplayConstants.WORKSPACE_ID);
		fields.add(SADisplayConstants.USER_ORG_ID);		
		fields.add(SADisplayConstants.ENABLED);				
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_WORKSPACE_TABLE)
				.insertInto(fields);
		
		// TODO: or is it already set on the object?
		long userOrgWorkspaceId = this.getNextUserorgWorkspaceId();
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.USER_ORG_WORKSPACE_ID, userOrgWorkspaceId);
		map.addValue(SADisplayConstants.WORKSPACE_ID, userOrgWorkspace.getWorkspaceid());
		map.addValue(SADisplayConstants.USER_ORG_ID, userOrgWorkspace.getUserorgid());
		map.addValue(SADisplayConstants.ENABLED, false);
		
		this.template.update(queryModel.toString(), map);
		
		return userOrgWorkspaceId;
	}
	
	public boolean registerUser(User user, List<Contact> contacts, List<UserOrg> userOrgs, 
			List<UserOrgWorkspace> userOrgWorkspaces) {
		boolean status = false;
		TransactionDefinition txDef = new DefaultTransactionDefinition();
    	TransactionStatus txStatus = txManager.getTransaction(txDef);
		
	try { // Any failure in this try results in a transaction rollback
		/* TODO: FIX!!
		 * - create() gets next User.id, but the API already does that
		 * - create() hashes password, but the API already gives it a hashed password?
		 * - create() returns the userid you sent it, make sure to check what happens in a failure
		*/
		int userId = create(user.getFirstname(), user.getLastname(), user.getUsername(), user.getPasswordHash());
		
		// TODO: better way to do batch update, rather than loop through all this?
		if(contacts != null && contacts.size() > 0) {
			for(Contact contact : contacts) {
				// TODO: createContact() queries for contacttypeid? It's already here in the Contact entity
				//		 Contact already has ContactID, createContact() gets another one
				
				
				createContact(contact.getContacttype().getType(), contact.getValue(), userId);
			}
		}
				
		// Create UserOrgs
		if(userOrgs != null && userOrgs.size() > 0) {
			for(UserOrg userOrg : userOrgs) {
				createUserOrg(userId, userOrg);
			}
		}
		
		// Create UserOrgWorkspaces
		if(userOrgWorkspaces != null && userOrgWorkspaces.size() > 0) {
			for(UserOrgWorkspace userOrgWorkspace : userOrgWorkspaces) {
				createUserOrgWorkspace(userOrgWorkspace);
			}
		}
		
		txManager.commit(txStatus);
		status = true;
	} catch (Exception e) {
		log.error("Exception during process of persisting User, rolling back");
		System.out.println("Exception during process of persisting User, rolling back: " + e.getMessage());
		//e.printStackTrace();
		txManager.rollback(txStatus);
	}
		
		
		return status;
	}
	
	public boolean addUserToOrg(long userId, List<UserOrg> userOrgs, 
			List<UserOrgWorkspace> userOrgWorkspaces) {
		boolean status = false;
		TransactionDefinition txDef = new DefaultTransactionDefinition();
    	TransactionStatus txStatus = txManager.getTransaction(txDef);
		
		try {
			// Create UserOrgs
			if(userOrgs != null && userOrgs.size() > 0) {
				for(UserOrg userOrg : userOrgs) {
					createUserOrg(userId, userOrg);
				}
			}
			
			// Create UserOrgWorkspaces
			if(userOrgWorkspaces != null && userOrgWorkspaces.size() > 0) {
				for(UserOrgWorkspace userOrgWorkspace : userOrgWorkspaces) {
					createUserOrgWorkspace(userOrgWorkspace);
				}
			}
			
			txManager.commit(txStatus);
			status = true;
		} catch (Exception e) {
			log.error("Exception during process of persisting User, rolling back");
			System.out.println("Exception during process of persisting User, rolling back: " + e.getMessage());
			//e.printStackTrace();
			txManager.rollback(txStatus);
		}
		
		
		return status;
	}
	
	public int getNextUserId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_SEQUENCE_TABLE).selectNextVal();
    	try{
    		//return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    		return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), Integer.class);
    	}catch(Exception e){
    		log.info("Could not retrieve next user id " + e.getMessage());
    	}
    	return -1;
	}
	
	public int getNextContactId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_SEQUENCE_TABLE).selectNextVal();
    	try{
    		//return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    		return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), Integer.class);
    	}catch(Exception e){
    		log.info("Could not retrieve next contact id " + e.getMessage());
    	}
    	return -1;
	}
	
	public int getNextUserorgId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_SEQUENCE_TABLE).selectNextVal();
    	try{    		
    		return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), Integer.class);
    	}catch(Exception e){
    		log.info("Could not retrieve next userorg id " + e.getMessage());
    	}
    	return -1;
	}
	
	public int getNextUserorgWorkspaceId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.HIBERNATE_SEQUENCE_TABLE).selectNextVal();
    	try{
    		Integer userOrgWorkspaceId = this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), Integer.class);
    		
    		if(userOrgWorkspaceId != null) {
    			return userOrgWorkspaceId;
    		}
    		
    		log.warn("Unable to retrieve ID from " + SADisplayConstants.HIBERNATE_SEQUENCE_TABLE + " sequence!");
    		
    	}catch(Exception e){
    		log.info("Could not retrieve next userorg workspace id " + e.getMessage()); 
    	}
    	return -1;
	}
	
	
    /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<UserFeature>
	   */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JoinRowCallbackHandler<UserFeature> getUserFeatureHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new UserFeatureRowMapper(), mappers);
    }
  
	/** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<User>
	   */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private JoinRowCallbackHandler<User> getHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new UserRowMapper(), mappers);
    }
    
    /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<User>
	   */
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  private JoinRowCallbackHandler<Contact> getContactHandlerWith(JoinRowMapper... mappers) {
		  return new JoinRowCallbackHandler(new ContactRowMapper(), mappers);
	  }

	public long getUserCountInWorkspace(int workspaceId) {
		StringBuilder sb = new StringBuilder();
		// subquery
		//sb.append("select count(distinct userId) from userorg where userorgid in")
		//.append("(select userorgid from userorg_workspace where workspaceid=:workspaceid)");
		// join
		sb.append("select count(distinct uo.userid) from userorg uo inner join ")
		.append("userorg_workspace uow on uo.userorgid = uow.userorgid where uow.workspaceid = :workspaceid");
		
		int count = template.queryForObject(sb.toString(), 
				new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId),
				Integer.class);
		
		return count;
	}


}
