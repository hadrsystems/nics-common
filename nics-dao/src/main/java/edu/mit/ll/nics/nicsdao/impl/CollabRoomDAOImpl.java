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
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.CollabRoom;
import edu.mit.ll.nics.common.entity.datalayer.Folder;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.CollabRoomDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.CollabRoomPermissionRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.CollabRoomRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.FolderRowMapper;


public class CollabRoomDAOImpl extends GenericDAO implements CollabRoomDAO {

private static Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(CollabRoomDAOImpl.class);
    	System.out.println("Initializing CollabRoom DAO....");
    	System.out.println("DATASOURCE: " + datasource);
    	
        this.template = new NamedParameterJdbcTemplate(datasource);
    }

    public int getCollabRoomSystemRole(int collabRoomId, int userId){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
    			.selectFromTable(SADisplayConstants.SYSTEM_ROLE_ID)
    			.where().equals(SADisplayConstants.COLLAB_ROOM_ID)
    			.and().equals(SADisplayConstants.USER_ID);
    	try{
	    	return this.template.queryForObject(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId)
						.addValue(SADisplayConstants.USER_ID, userId), Integer.class);
    	}catch(Exception e){
    		//e.printStackTrace();
    		log.info("No role found in the collabroompermission table for room #0 and userid #1", collabRoomId, userId);
    	}
    	return SADisplayConstants.READ_ONLY_ROLE_ID;
    }
    
    public int getCollabRoomId(String roomname, int workspaceId){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
    			.selectFromTable(SADisplayConstants.COLLAB_ROOM_ID)
    			.join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.INCIDENT_ID)
    			.where().equals(SADisplayConstants.COLLAB_ROOM_NAME)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	try{
	    	return this.template.queryForInt(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_NAME, roomname)
	    				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
    	}catch(Exception e){
    		log.info("there was an error retrieving the collab room id for collab room #0", roomname);
    	}
    	return -1;
    }
    
    public int getCollabRoomId(String roomname){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
    			.selectFromTable(SADisplayConstants.COLLAB_ROOM_ID)
    			.join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.INCIDENT_ID)
    			.where().equals(SADisplayConstants.COLLAB_ROOM_NAME)
    			.and().equals(SADisplayConstants.INCIDENT_ID);
    	
    	try{
	    	return this.template.queryForInt(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_NAME, roomname)
	    			.addValue(SADisplayConstants.INCIDENT_ID, 0)); //Assume no workspace - no incident
    	}catch(Exception e){
    		log.info("there was an error retrieving the collab room id for collab room #0:\n" +
    				e.getMessage(), roomname);
    	}
    	return -1;
    }

	public void createCollabPermission(int collabroomId, int systemRoleId, int userId){
		
		List<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.COLLAB_ROOM_PERMISSION_ID);
		fields.add(SADisplayConstants.COLLAB_ROOM_ID);
		fields.add(SADisplayConstants.USER_ID);
		fields.add(SADisplayConstants.SYSTEM_ROLE_ID);
		
		int collabroomPermissionId = this.getNextCollabRoomPermissionId();
		
		MapSqlParameterSource map = new MapSqlParameterSource(
				SADisplayConstants.COLLAB_ROOM_PERMISSION_ID, collabroomPermissionId);
		map.addValue(SADisplayConstants.COLLAB_ROOM_ID, collabroomId);
		map.addValue(SADisplayConstants.USER_ID, userId);
		map.addValue(SADisplayConstants.SYSTEM_ROLE_ID, systemRoleId);
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
				.insertInto(fields);
		
		System.out.println(collabroomPermissionId + ":" + collabroomId + ":" + userId + ":" + systemRoleId);
		
		System.out.println(queryModel.toString());
				
		log.info("Persisting collabroomid<" + collabroomId + "> user<" + userId + ">");
		
		this.template.update(queryModel.toString(), map);
	}
	
	public int getNextCollabRoomPermissionId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.HIBERNATE_SEQUENCE_TABLE).selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Error retrieving the next incident incident type id: #0", e.getMessage());
    	}
    	return -1;
	}
	
	public void clearCollabroomPermissions(int collabroomid){
		QueryModel model = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.COLLAB_ROOM_ID, collabroomid);
		
		this.template.update(model.toString(), new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomid));
	}
	
	public String getCollabroomName(int collabroomid){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE).
				selectFromTable(SADisplayConstants.COLLAB_ROOM_NAME).where().equals(SADisplayConstants.COLLAB_ROOM_ID);
		
		try{
			return this.template.queryForObject(
					query.toString(), 
					new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomid), String.class);
		}catch(Exception e){
			log.info("Could not find collab room name with collab room id #0", collabroomid);
		}
		return null;
	}
	
	public List<CollabRoom> getAllCollabroomDatalayers(int workspaceId){
		String query = "select * from CollabRoom cr, Datalayer dl , Datalayerfolder dlf , Folder f " +
				"where cr.name=dl.displayname and cr.incidentid=0 and dlf.datalayerid=dl.datalayerid " +
				"and f.folderid=dlf.folderid and workspaceid=" + workspaceId + " and name not like '%KPI_%'";
		
		JoinRowCallbackHandler<CollabRoom> handler = getHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(), handler);
		
		return handler.getResults();
	}
	
	public boolean hasRoomNamed(int incidentId, String collabRoomName){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
				.selectFromTable(SADisplayConstants.COLLAB_ROOM_ID)
				.join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.INCIDENT_ID)
				.where().equals(SADisplayConstants.INCIDENT_ID).and()
				.equals(SADisplayConstants.COLLAB_ROOM_NAME);
		
		try{
			this.template.queryForObject(queryModel.toString(), 
					new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentId)
						.addValue(SADisplayConstants.COLLAB_ROOM_NAME, collabRoomName), Integer.class);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public boolean hasRoomNamed(String text){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
				.selectFromTable(SADisplayConstants.COLLAB_ROOM_ID)
				.join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.INCIDENT_ID)
				.where().equals(SADisplayConstants.INCIDENT_ID).and()
				.equals(SADisplayConstants.COLLAB_ROOM_NAME);
		
		try{
			this.template.queryForInt(queryModel.toString(), 
					new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, 0)
					.addValue(SADisplayConstants.COLLAB_ROOM_NAME, text));
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public List<CollabRoom> updateCollabrooms(String currentIncident, int currentIncidentId){
		QueryModel queryModel;
		MapSqlParameterSource map = new MapSqlParameterSource();
		
		if(currentIncident == null || currentIncident.equalsIgnoreCase("") || currentIncidentId == -1){
			queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
				.selectAllFromTable()
				.left().join(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE).using(SADisplayConstants.COLLAB_ROOM_ID)
				.where().isNull(SADisplayConstants.INCIDENT_ID);
			currentIncident = "";
		}
		else{
			queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
				.selectAllFromTable()
				.left().join(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE).using(SADisplayConstants.COLLAB_ROOM_ID)
				.where().equals(SADisplayConstants.INCIDENT_ID);
			map.addValue(SADisplayConstants.INCIDENT_ID, currentIncidentId);
		}
		
		JoinRowCallbackHandler<CollabRoom> handler = getHandlerWith(new CollabRoomPermissionRowMapper());
		if(this.template == null){
			this.initialize();
		}
		this.template.query(queryModel.toString(), map, handler);
		
		return handler.getResults();
	}
	
	public boolean isSecure(long collabroomId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
				.selectFromTableWhere(SADisplayConstants.COLLAB_ROOM_ID)
				.equals(SADisplayConstants.COLLAB_ROOM_ID);
		try{
			this.template.queryForObject(query.toString(), 
					new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomId),
					Integer.class);
		}catch(Exception e){
			return false;
		}
		return true;
	}
	
	public boolean hasPermissions(long userId, long collabroomId){
		if(this.isSecure(collabroomId)){
			QueryModel query = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
					.selectFromTableWhere(SADisplayConstants.COLLAB_ROOM_ID)
					.equals(SADisplayConstants.COLLAB_ROOM_ID)
					.and().equals(SADisplayConstants.USER_ID);
			try{
				this.template.queryForObject(query.toString(), 
						new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomId)
						.addValue(SADisplayConstants.USER_ID, userId),
						Integer.class);
			}catch(Exception e){
				return false;
			}
			return true;
		}else{
			return true;
		}
	}
	
	public int create(CollabRoom collabroom){
		
		int collabroomid = this.getNextCollabRoomId();
		collabroom.setCollabRoomId(collabroomid);

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(SADisplayConstants.USERSESSION_ID, collabroom.getUsersessionid());
		params.put(SADisplayConstants.COLLAB_ROOM_ID, collabroom.getCollabRoomId());
		params.put(SADisplayConstants.COLLAB_ROOM_NAME, collabroom.getName());
		params.put(SADisplayConstants.CREATED, collabroom.getCreated());
		params.put(SADisplayConstants.INCIDENT_ID, collabroom.getIncidentid());
		
		List<String> fields = new ArrayList<String>(params.keySet());
		QueryModel model = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE).insertInto(fields);
		
		this.template.update(model.toString(), params);
		return collabroomid;
	}
	
	public int getNextCollabRoomId() {
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_SEQUENCE_TABLE).selectNextVal();
		try{
			return this.template.queryForObject(queryModel.toString(), new HashMap<String, Object>(), Integer.class);
		}catch(Exception e){
			log.info("Could not retrieve the next value for the seuqence table #0: #1", 
					SADisplayConstants.COLLAB_ROOM_SEQUENCE_TABLE, e.getMessage());
		}
		return -1;
	}
	
	public List<CollabRoom> getSecuredRooms(int userId, int incidentId) throws DataAccessException, Exception {
		try {
			//Get all protected rooms that the user has access to
			//secureRoomsWithPermissions = em.createQuery(
			String sqlSecureRooms =	"SELECT DISTINCT on (cr.collabroomid) cr.* FROM collabroom cr, CollabroomPermission cp where cr.incidentid=:incidentId" +
			        " and ((cp.userId=:userId AND cp.collabRoomId=cr.collabRoomId) or cr.name like '%-IncidentMap%')"; //)
			MapSqlParameterSource params = new MapSqlParameterSource();
			params.addValue(SADisplayConstants.USER_ID, userId);
			params.addValue(SADisplayConstants.INCIDENT_ID, incidentId);
			
			JoinRowCallbackHandler<CollabRoom> secureHandler = getHandlerWith();//new CollabRoomRowMapper());
			template.query(sqlSecureRooms, params, secureHandler);				
			return secureHandler.getResults();
		}catch (DataAccessException e) {
			log.error("Exception querying for list of CollabRooms user has access to: " + e.getMessage(), e);
			throw e;
		} catch (Exception ex) {
			String errmsg = "Failed to execute query: " + ex.getMessage();
			log.error("Unhandled exception querying for accessibleCollabRooms: " + ex.getMessage(), ex);
			throw ex;
		}
	}
	
	public boolean unsecureRoom(long collabRoomId){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.COLLAB_ROOM_ID);
		
		try{
			int ret = this.template.update(queryModel.toString(), 
					new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId));
			return (ret>0);
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 
	 * TODO: brought over from PhinicsDBFacade
	 * 
	 * @param em
	 * @param userId
	 * @param incidentId
	 * @return
	 * @throws DataAccessException
	 */
	public List<CollabRoom> getAccessibleCollabRooms(long userId, int incidentId) throws DataAccessException, Exception {
		try {
			//Get all of the open rooms
			//unsecuredRooms = em.createQuery(
			String sqlUnsecuredRooms ="SELECT DISTINCT on (cr.collabroomid) cr.* FROM CollabRoom cr where cr.incidentid=:incidentId" + 
					" and cr.collabRoomId not in " +
					"(SELECT DISTINCT cr.collabRoomId FROM CollabRoom cr, CollabroomPermission cp where  "
					+ "cr.collabRoomId=cp.collabRoomId and " + "incidentid=:incidentId)"; //)
			
			JoinRowCallbackHandler<CollabRoom> unsecuredHandler = getHandlerWith();//new CollabRoomRowMapper());
			template.query(sqlUnsecuredRooms, 
					new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentId), unsecuredHandler);
			return unsecuredHandler.getResults();
		} catch (DataAccessException e) {
			log.error("Exception querying for list of CollabRooms user has access to: " + e.getMessage(), e);
			throw e;
		} catch (Exception ex) {
			String errmsg = "Failed to execute query: " + ex.getMessage();
			log.error("Unhandled exception querying for accessibleCollabRooms: " + ex.getMessage(), ex);
			throw ex;
		}
	}
	

    public List<Map<String, Object>> getCollabRoomSecureUsers(int collabRoomId){
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.USER_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ID);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.SYSTEM_ROLE_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
   			 	 .selectFromTable(fields.toString())
				 .join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				 .where().equals(SADisplayConstants.COLLAB_ROOM_ID);
				 
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId);
		
		return template.queryForList(queryModel.toString(), map);
	}
    
    public List<Map<String, Object>> getUsersWithoutPermission(int collabroomid, int orgId, int workspaceId){
		StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.USER_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.USER_ID);
    	
    	QueryModel permissionQuery = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
   			 	 .selectFromTable(SADisplayConstants.USER_ID)
				 .where().equals(SADisplayConstants.COLLAB_ROOM_ID);
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ESCAPED)
				.selectFromTable(fields.toString())
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.USER_ORG_WORKSPACE_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.where().notIn(SADisplayConstants.USER_ID, permissionQuery.toString())
				.and().equals(SADisplayConstants.ORG_ID)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
		
		return template.queryForList(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomid)
				.addValue(SADisplayConstants.ORG_ID, orgId)
				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
	}
	
	
	public CollabRoom getCollabRoomById(long collabRoomId) throws DataAccessException, Exception {
		CollabRoom room = null;	
		//try {
		QueryModel model = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_TABLE)
				.selectAllFromTable().where().equals(SADisplayConstants.COLLAB_ROOM_ID);
		JoinRowCallbackHandler<CollabRoom> handler = getHandlerWith();
		
		template.query(model.toString(), 
				new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId), 
				handler);
		room = handler.getSingleResult();
			
		/*} catch(DataAccessException e) {
			throw e;
		} catch(Exception e) {
			throw e;
		}*/
		
		return room;
	}
	
	public boolean secureRoom(long collabRoomId, int userId, int systemRoleId){
		QueryModel model = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
				.insertInto(Arrays.asList(SADisplayConstants.USER_ID, SADisplayConstants.SYSTEM_ROLE_ID,
						SADisplayConstants.COLLAB_ROOM_ID), SADisplayConstants.COLLAB_ROOM_PERMISSION_ID);
		
		MapSqlParameterSource map = new MapSqlParameterSource()
			.addValue(SADisplayConstants.USER_ID, userId)
			.addValue(SADisplayConstants.SYSTEM_ROLE_ID, systemRoleId)
			.addValue(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId);
		
		try{
			int retval = this.template.update(model.toString(),map);
			return retval == 1;
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Folder>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Folder> getFolderHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new FolderRowMapper(), mappers);
    }
    
    /** getHandlerWith
   	 *  @param mappers - optional additional mappers
   	 *  @return JoinRowCallbackHandler<Folder>
   	 */
       @SuppressWarnings({ "rawtypes", "unchecked" })
   	private JoinRowCallbackHandler<CollabRoom> getHandlerWith(JoinRowMapper... mappers) {
    	   return new JoinRowCallbackHandler(new CollabRoomRowMapper(), mappers);
       }
}
