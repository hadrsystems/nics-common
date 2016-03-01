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

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.CurrentUserSession;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.UserSessionDAO;
import edu.mit.ll.nics.nicsdao.mappers.CurrentUserSessionRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserRowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;


public class UserSessionDAOImpl extends GenericDAO implements UserSessionDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;
   
    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(UserSessionDAOImpl.class);
    	this.template = new NamedParameterJdbcTemplate(datasource);
    }

    public int create(String sessionid, int userorgid, String displayname, int userid, int systemroleid, int workspaceId){
		// Insert a web session for them we can track
		Timestamp now = new Timestamp(new Date().getTime());

		BigInteger usersessionid = this.getNextUserSessionId();
		BigInteger currentusersessionid = this.getNextCurrentUserSessionId();
		
		try{
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(SADisplayConstants.USERSESSION_ID);
			fields.add(SADisplayConstants.USER_ORG_ID);
			fields.add(SADisplayConstants.LOGGED_IN);
			fields.add(SADisplayConstants.SESSION_ID);
			
			
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_SESSION_TABLE)
					.insertInto(fields);
			
			MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.USERSESSION_ID, usersessionid.longValue());
			map.addValue(SADisplayConstants.USER_ORG_ID, userorgid);
			map.addValue(SADisplayConstants.LOGGED_IN, now);
			map.addValue(SADisplayConstants.SESSION_ID, sessionid);
			
			this.template.update(queryModel.toString(), map);
			
			//Create CurrentUserSession
			ArrayList<String> c_fields = new ArrayList<String>();
			c_fields.add(SADisplayConstants.CURRENT_USERSESSION_ID);
			c_fields.add(SADisplayConstants.USERSESSION_ID);
			c_fields.add(SADisplayConstants.LAST_SEEN);
			c_fields.add(SADisplayConstants.LOGGED_IN);
			c_fields.add(SADisplayConstants.DISPLAY_NAME);
			c_fields.add(SADisplayConstants.USER_ID);
			c_fields.add(SADisplayConstants.SYSTEM_ROLE_ID);
			c_fields.add(SADisplayConstants.WORKSPACE_ID);
			
			QueryModel c_queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
					.insertInto(c_fields);
			
			MapSqlParameterSource c_map = new MapSqlParameterSource(SADisplayConstants.USERSESSION_ID, usersessionid.longValue());
			c_map.addValue(SADisplayConstants.CURRENT_USERSESSION_ID, currentusersessionid.longValue());
			c_map.addValue(SADisplayConstants.LAST_SEEN , now);
			c_map.addValue(SADisplayConstants.LOGGED_IN , now);
			c_map.addValue(SADisplayConstants.DISPLAY_NAME, displayname);
			c_map.addValue(SADisplayConstants.USER_ID, userid);
			c_map.addValue(SADisplayConstants.SYSTEM_ROLE_ID, systemroleid);
			c_map.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId);

			this.template.update(c_queryModel.toString(), c_map);
		
		}catch(Exception e){
			e.printStackTrace();
			return -1;
		}
		return usersessionid.intValue();
	}
    
    public boolean removeCurrentUserSession(int userid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
    			.deleteFromTableWhere().equals(SADisplayConstants.USER_ID);
    	
    	Map<String, Object> params = new HashMap<String, Object>();
        params.put(SADisplayConstants.USER_ID, userid);
        
        this.template.update(queryModel.toString(), params);
        
        return true;
    }
    
    public int getUserId(String sessionId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectFromTable(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
    			.join(SADisplayConstants.USERSESSION_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.SESSION_ID);
    	try{
    		return this.template.queryForInt(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.SESSION_ID, sessionId));
    	}catch(Exception e){
    		log.info("Could not find usersession for session id: " + sessionId);
    	}
    	return -1;
    }
    
    public int getWorkspaceId(int usersessionid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
    			.selectFromTable(SADisplayConstants.WORKSPACE_ID)
    			.where().equals(SADisplayConstants.USERSESSION_ID);
    	try{
    		return this.template.queryForInt(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.USERSESSION_ID, usersessionid));
    	}catch(Exception e){
    		log.info("Could not get workspaceid for usersessionid #0", usersessionid);
    	}
    	return -1;
    }
    
    public int getUserSessionid(String username){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
    			.selectFromTable(SADisplayConstants.USERSESSION_ID)
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.USER_NAME);
    	try{
    		//return this.template.queryForInt(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.USER_NAME, username));
    		return this.template.queryForObject(queryModel.toString(), 
    				new MapSqlParameterSource(SADisplayConstants.USER_NAME, username), Integer.class);
    	}catch(Exception e){
    		log.info("Could not get usersessionid for username: " + username);
    	}
    	return -1;
    }
    
    // TODO: IDs should be longs...
    public int getUserSessionid(long userId) {
    	
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
    			.selectFromTable(SADisplayConstants.USER_NAME)
    			.where().equals(SADisplayConstants.USER_ID);
    	
    	String username = this.template.queryForObject(model.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.USER_ID, userId), String.class);
    	
    	if(username == null || username.isEmpty()) {
    		log.warn("Did not find user with UserID: " + userId);
    		return -1;
    	}
    	
    	return getUserSessionid(username);
    }
    
    public long getCurrentUserSessionid(long userId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
    			.selectFromTable(SADisplayConstants.CURRENT_USERSESSION_ID)
    			.where().equals(SADisplayConstants.USER_ID);
    	try{
    		//return this.template.queryForInt(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.USER_NAME, username));
    		return this.template.queryForObject(queryModel.toString(), 
    				new MapSqlParameterSource(SADisplayConstants.USER_ID, userId), Long.class);
    	}catch(Exception e){
    		log.info("Could not get current usersessionid for username: " + userId);
    	}
    	return -1;
    }
    
    public List<CurrentUserSession> getAllCurrentSessions(int workspaceid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    			.where().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	JoinRowCallbackHandler<CurrentUserSession> handler = this.getCurrentSessionHandlerWith(new UserRowMapper());
    	
    	this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceid), handler);
    	
    	return handler.getResults();
    }
    
    /**
     * Returns whether or not the specified user has a CurrentUserSessionId for the 
     * specified workspace
     * 
     * @param workspaceId
     * @param userId
     * @return The user's CurrentUserSession if it exists, null otherwise
     */
    public boolean hasCurrentUserSession(int workspaceId, int userId) {
    	boolean hasCurUserSession = false;
    	
    	MapSqlParameterSource paramMap = new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId)
    		.addValue(SADisplayConstants.USER_ID, userId);
    	// TODO: any better to query for the actual entity. Querying the count seems more light weight
    	int count = template.queryForObject("select count(*) from currentusersession "
    			+ "where userid=:userId and workspaceid=:workspaceid", paramMap, Integer.class);
    	
    	if(count > 0) {
    		hasCurUserSession = true;
    	}
    	
    	return hasCurUserSession;
    }
    
    
    /**
     * Gets the specified user's CurrentUserSession
     * 
     * @param workspaceId
     * @param userId
     * @return The user's CurrentUserSession if it exists, null otherwise
     */
    public CurrentUserSession getCurrentUserSession(long userId) {
    	List<CurrentUserSession> sessions = null;
    	
    	MapSqlParameterSource paramMap = new MapSqlParameterSource(SADisplayConstants.USER_ID, userId);
    	    	
    	JoinRowCallbackHandler<CurrentUserSession> handler = getCurrentSessionHandlerWith();
    	
    	// TODO: may want to try getting a list of results, and choosing the latest
    	// one, unless it's impossible to have 2 current user sessions... better to return
    	// one than throw an exception?
    	template.query("select * from currentusersession "
    			+ "where userid=:userId order by loggedin desc", paramMap, handler);
    	
    	sessions = handler.getResults();
    	
    	if(sessions == null || sessions.isEmpty()) {
    		return null;
    	}
    	
    	return sessions.get(0);
    }
    
    /**
     * Gets the specified user's CurrentUserSession
     * 
     * @param workspaceId
     * @param userId
     * @return The user's CurrentUserSession if it exists, null otherwise
     */
    public CurrentUserSession getCurrentUserSession(int workspaceId, int userId) {
    	List<CurrentUserSession> sessions = null;
    	
    	MapSqlParameterSource paramMap = new MapSqlParameterSource(SADisplayConstants.USER_ID, userId)
    		.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId);
    	    	
    	JoinRowCallbackHandler<CurrentUserSession> handler = getCurrentSessionHandlerWith();
    	
    	// TODO: may want to try getting a list of results, and choosing the latest
    	// one, unless it's impossible to have 2 current user sessions... better to return
    	// one than throw an exception?
    	template.query("select * from currentusersession "
    			+ "where userid=:userId and workspaceid=:workspaceid order by loggedin desc", paramMap, handler);
    	
    	sessions = handler.getResults();
    	
    	if(sessions == null || sessions.isEmpty()) {
    		return null;
    	}
    	
    	return sessions.get(0);
    }
    
    
    public BigInteger getNextCurrentUserSessionId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_SEQ).selectNextVal();
    	try{
    		return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), BigInteger.class);
    	}catch(Exception e){
    		log.info("Could not get the next current user session id #0", e.getMessage());
    	}
    	return null;
    }
    
    public BigInteger getNextUserSessionId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USERSESSION_SEQ).selectNextVal();
    	try{
    		return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), BigInteger.class);
    	}catch(Exception e){
    		log.info("Could not get the next user session id #0", e.getMessage());
    	}
    	return null;
     }
    
    public long getNextMessageSeqNum() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.MESSAGE_SEQUENCE).selectNextVal();
    	try{
    		return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), BigInteger.class).longValue();
    	}catch(Exception e){
    		e.printStackTrace();
    		//log.info("Could not retrieve get next message sequence number #0", e.getMessage());
    	}
    	return -1;
     }
    
    public void updateLastSeen(int userid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
    			.update(SADisplayConstants.LAST_SEEN).now().where().equals(SADisplayConstants.USER_ID);
    	
    	this.template.update(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.USER_ID, userid));
    	
    	/*em.createQuery("update CurrentUserSession set lastseen=now() where userid=:userid")
		.setParameter("userid", cUserHome.getCurrentUser().getUserId()).executeUpdate();*/
    }
    
    public int updateLoggedOutToNow(long userSessionId) {
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.USERSESSION_TABLE)
    			.update("loggedout").now().where().equals(SADisplayConstants.USERSESSION_ID);
    	
    	int result = template.update(model.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.USERSESSION_ID, userSessionId));
    	
    	return result;
    }
    
    /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<UserOrg>
	   */
	  @SuppressWarnings({ "unchecked", "rawtypes" })
	  private JoinRowCallbackHandler<CurrentUserSession> getCurrentSessionHandlerWith(JoinRowMapper... mappers) {
		  return new JoinRowCallbackHandler(new CurrentUserSessionRowMapper(), mappers);
	  }

	public CurrentUserSession createUserSession(long userid, String displayname, int userorgid, int systemroleid, int workspaceid, String sessionid){
		
    	Integer usersessionid = -1;
    	Integer currentusersessionid = -1;

		try{
			
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(SADisplayConstants.USER_ORG_ID);
			fields.add(SADisplayConstants.SESSION_ID);
			
	    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USERSESSION_TABLE)
					.insertInto(fields,SADisplayConstants.USERSESSION_ID).returnValue(SADisplayConstants.USERSESSION_ID);
			
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue(SADisplayConstants.USERSESSION_ID,SADisplayConstants.DEFAULT);
			map.addValue(SADisplayConstants.USER_ORG_ID, userorgid);
			map.addValue(SADisplayConstants.SESSION_ID, sessionid);
			
			usersessionid = this.template.queryForObject(queryModel.toString(), map,
					Integer.class);
		}
		catch(Exception e){
			log.info("Could not create usersession object", e.getMessage());
			return null;
		}

		
		try{
			ArrayList<String> fields = new ArrayList<String>();
			fields.add(SADisplayConstants.USERSESSION_ID);
			fields.add(SADisplayConstants.USER_ID);
			fields.add(SADisplayConstants.DISPLAY_NAME);
			fields.add(SADisplayConstants.SYSTEM_ROLE_ID);
			fields.add(SADisplayConstants.WORKSPACE_ID);
			
			
	    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
					.insertInto(fields,SADisplayConstants.CURRENT_USERSESSION_ID).returnValue(SADisplayConstants.CURRENT_USERSESSION_ID);
			
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue(SADisplayConstants.CURRENT_USERSESSION_ID,SADisplayConstants.DEFAULT);
			map.addValue(SADisplayConstants.USERSESSION_ID,usersessionid);
			map.addValue(SADisplayConstants.USER_ID,userid);
			map.addValue(SADisplayConstants.DISPLAY_NAME,displayname);
			map.addValue(SADisplayConstants.SYSTEM_ROLE_ID,systemroleid);
			map.addValue(SADisplayConstants.WORKSPACE_ID,workspaceid);
			
			
			currentusersessionid = this.template.queryForObject(queryModel.toString(), map,
				Integer.class);
		}
		catch(Exception e){
			log.info("Could not create currentusersession object", e.getMessage());
			return null;
		}
	
		try{
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
					.selectAllFromTableWhere()
					.equals(SADisplayConstants.CURRENT_USERSESSION_ID, currentusersessionid);
			
			JoinRowCallbackHandler<CurrentUserSession> handler = getCurrentSessionHandlerWith();
			template.query(queryModel.toString(), queryModel.getParameters(), handler);
			
			return handler.getResults().get(0);
			
		}catch(Exception e){
			log.info("Could not return currentusersession object", e.getMessage());
			return null;
		}
	}
	
	public boolean removeUserSession(long currentUserSessionId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.CURRENT_USERSESSION_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.CURRENT_USERSESSION_ID);
		
		MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.CURRENT_USERSESSION_ID, currentUserSessionId);
		
		int ret = template.update(query.toString(), map);
		
		return ret == 1;
	}

	/**
	 * Utility method for checking if the provided userSessionId exists
	 * 
	 * @param userSessionId
	 * @return true if it exists, false otherwise
	 */
	public boolean userSessionIdExists(int userSessionId) {
		// TODO:1209 could do a select count(*) where usersession id equals...
		QueryModel query = QueryManager.createQuery(SADisplayConstants.USER_SESSION_TABLE)
				.selectFromTable(SADisplayConstants.USERSESSION_ID).where()
				.equals(SADisplayConstants.USERSESSION_ID);
		int ret = -99999;
		
		try {
			ret = template.queryForObject(query.toString(), 
					new MapSqlParameterSource(SADisplayConstants.USERSESSION_ID, userSessionId), Integer.class);
			
		} catch(EmptyResultDataAccessException e) {
			log.debug("UserSessionId " + userSessionId + " does not exist in database");
		} catch(Exception e) {			
			log.error("Unhandled exception checking if UserSessionId(" + userSessionId + ") exists: " + 
					e.getMessage());
		}
		
		return ret == userSessionId;
	}
}
