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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.CurrentUserSession;
import edu.mit.ll.nics.common.entity.Incident;
import edu.mit.ll.nics.common.entity.IncidentIncidentType;
import edu.mit.ll.nics.common.entity.IncidentType;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.IncidentDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.IncidentRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.IncidentTypeRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.Incident_IncidentTypeRowMapper;
import edu.mit.ll.nics.common.entity.Incident;
import edu.mit.ll.nics.common.entity.datalayer.Folder;

public class IncidentDAOImpl extends GenericDAO implements IncidentDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(IncidentDAOImpl.class);
    	this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    public int create(String incidentname, double lat, double lon, int usersessionid, int workspaceid, int parentid, String description){
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.INCIDENT_ID);
		fields.add(SADisplayConstants.LONGITUDE);
		fields.add(SADisplayConstants.LATITUDE);
		fields.add(SADisplayConstants.INCIDENT_NAME);
		fields.add(SADisplayConstants.CREATED);
		fields.add(SADisplayConstants.ACTIVE);
		fields.add(SADisplayConstants.USERSESSION_ID);
		fields.add(SADisplayConstants.FOLDER);
		fields.add(SADisplayConstants.WORKSPACE_ID);
		fields.add(SADisplayConstants.DESCRIPTION);
		
		if(parentid > 0){
			fields.add(SADisplayConstants.PARENT_INCIDENT_ID);
		}
		
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.insertInto(fields);
		
		int incidentid = this.getNextIncidentId();
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.INCIDENT_ID, incidentid);
		map.addValue(SADisplayConstants.LONGITUDE, lon);
		map.addValue(SADisplayConstants.LATITUDE, lat);
		map.addValue(SADisplayConstants.INCIDENT_NAME, incidentname);
		map.addValue(SADisplayConstants.CREATED, Calendar.getInstance().getTime());
		map.addValue(SADisplayConstants.ACTIVE, true);
		map.addValue(SADisplayConstants.USERSESSION_ID, usersessionid);
		map.addValue(SADisplayConstants.FOLDER, "");
		map.addValue(SADisplayConstants.WORKSPACE_ID, workspaceid);
		map.addValue(SADisplayConstants.DESCRIPTION, description);
		
		if(fields.contains(SADisplayConstants.PARENT_INCIDENT_ID)){
			map.addValue(SADisplayConstants.PARENT_INCIDENT_ID, parentid);
		}
		
		this.template.update(queryModel.toString(), map);
		
		return incidentid;
	}


	public Incident create(Incident incident) {
		int incidentId = -1;
		
		try{
			
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue(SADisplayConstants.USERSESSION_ID, incident.getUsersessionid());
			map.addValue(SADisplayConstants.INCIDENT_NAME, incident.getIncidentname());
			map.addValue(SADisplayConstants.LATITUDE, incident.getLat());
			map.addValue(SADisplayConstants.LONGITUDE, incident.getLon());
			map.addValue(SADisplayConstants.DESCRIPTION, incident.getDescription());
			map.addValue(SADisplayConstants.WORKSPACE_ID, incident.getWorkspaceid());
			map.addValue(SADisplayConstants.BOUNDS, incident.getBounds());
			map.addValue(SADisplayConstants.PARENT_INCIDENT_ID, incident.getParentincidentid());
			
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
					.insertInto(new ArrayList<String>(map.getValues().keySet()), SADisplayConstants.INCIDENT_ID)
					.returnValue(SADisplayConstants.INCIDENT_ID);

			incidentId = this.template.queryForObject(queryModel.toString(), map, Integer.class);
		}
		catch(Exception e){
			log.info("Failed to update incident #0", incident.getIncidentname());
			return null;
		}
		
		try{
		
			for(IncidentIncidentType type: incident.getIncidentIncidenttypes()){
				
				ArrayList<String> fields = new ArrayList<String>();
				fields.add(SADisplayConstants.INCIDENT_ID);
				fields.add(SADisplayConstants.INCIDENT_TYPE_ID);
								
				QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE)
						.insertInto(fields, SADisplayConstants.INCIDENT_INCIDENTTYPE_ID);
				
				MapSqlParameterSource map = new MapSqlParameterSource();
				map.addValue(SADisplayConstants.INCIDENT_ID, incidentId);
				map.addValue(SADisplayConstants.INCIDENT_TYPE_ID,type.getIncidenttypeid());

				this.template.update(queryModel.toString(),map);

			}
			
		}
		catch(Exception e){
			e.printStackTrace();
			log.info("Failed to update incident_incidenttypes");
			return null;
		}
		
		try{
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
					.selectAllFromTable().where().equals(SADisplayConstants.INCIDENT_ID, incidentId);
			
			log.info("Incident Query: " + queryModel.toString());
			
			JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith();
			
			template.query(queryModel.toString(), queryModel.getParameters(), handler);
			
			return handler.getResults().get(0);
			
		}
		catch(Exception E){
			log.info("Could not find incident #0",incident.getIncidentname());
		}
		
		return null;
	}

    
    public int createIncidentIncidentTypes(int incidentid, List<IncidentType> types){
		ArrayList<String> fields = new ArrayList<String>();
		fields.add(SADisplayConstants.INCIDENT_INCIDENTTYPE_ID);
		fields.add(SADisplayConstants.INCIDENT_TYPE_ID);
		fields.add(SADisplayConstants.INCIDENT_ID);
		
		int rows = 0;
		
		for(IncidentType incidentType : types){
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE)
					.insertInto(fields);
			
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue(SADisplayConstants.INCIDENT_ID, incidentid);
			map.addValue(SADisplayConstants.INCIDENT_INCIDENTTYPE_ID, this.getNextIncidentIncidentTypeId());
			map.addValue(SADisplayConstants.INCIDENT_TYPE_ID, incidentType.getIncidentTypeId());
			
			rows += this.template.update(queryModel.toString(), map);
		}
		return rows;
	}
    
    public Incident updateIncident(int workspaceId, Incident incident){
    	
    	Incident dbIncident = null;
    	
		try{

			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
					.update().equals(SADisplayConstants.INCIDENT_NAME).comma().equals(SADisplayConstants.DESCRIPTION)
	    			.comma().equals(SADisplayConstants.PARENT_INCIDENT_ID).comma().equals(SADisplayConstants.LATITUDE)
	    			.comma().equals(SADisplayConstants.LONGITUDE).where().equals(SADisplayConstants.INCIDENT_ID).returnValue("*");

			MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID,incident.getIncidentid());
			map.addValue(SADisplayConstants.INCIDENT_NAME, incident.getIncidentname());
			map.addValue(SADisplayConstants.DESCRIPTION, incident.getDescription());
			map.addValue(SADisplayConstants.PARENT_INCIDENT_ID, incident.getParentincidentid());
			map.addValue(SADisplayConstants.LATITUDE, incident.getLat());
			map.addValue(SADisplayConstants.LONGITUDE, incident.getLon());
			
			
			JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith();
			
			this.template.query(queryModel.toString(), map, handler);
			
			dbIncident = handler.getSingleResult();
			
		}
		catch(Exception e){
			log.info("Failed to update incident #0", incident.getIncidentname());
			return null;
		}
		
		//need to delete/update incidenttypes
		
		try{
			
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE)
					.deleteFromTableWhere().equals(SADisplayConstants.INCIDENT_ID);
			
			this.template.update(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID,incident.getIncidentid()));
			
			
			
		}catch(Exception e){
			log.info("Failed to delete incident types for incident #0", incident.getIncidentname());
			return null;
		}
		
		
		try{
		
			for(IncidentIncidentType type: incident.getIncidentIncidenttypes()){
				
				ArrayList<String> fields = new ArrayList<String>();
				fields.add(SADisplayConstants.INCIDENT_ID);
				fields.add(SADisplayConstants.INCIDENT_TYPE_ID);
								
				QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE)
						.insertInto(fields, SADisplayConstants.INCIDENT_INCIDENTTYPE_ID);
				
				MapSqlParameterSource map = new MapSqlParameterSource();
				map.addValue(SADisplayConstants.INCIDENT_ID, incident.getIncidentid());
				map.addValue(SADisplayConstants.INCIDENT_TYPE_ID,type.getIncidenttypeid());

				this.template.update(queryModel.toString(),map);

			}
			
			return dbIncident;
		}
		catch(Exception e){
			log.info("Failed to update incident_incidenttypes for incident #0",incident.getIncidentname());
			
		}
		
		return null;
    }
    
    public boolean isAdmin(int workspaceId, int incidentId, String username){
	    	try{
	    		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.USER_ORG_TABLE)
		  			 	 .selectFromTable(SADisplayConstants.SYSTEM_ROLE_ID)
						 .join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
						 .join(SADisplayConstants.USER_SESSION_TABLE).using(SADisplayConstants.USER_ORG_ID)
						 .join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.USERSESSION_ID)
						 .where().equals(SADisplayConstants.WORKSPACE_ID)
						 .and().equals(SADisplayConstants.USER_NAME)
						 .and().equals(SADisplayConstants.INCIDENT_ID);
		    	
		    	int ret = this.template.queryForObject(queryModel.toString(), 
		    			new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId)
		    			.addValue(SADisplayConstants.USER_NAME, username)
		    			.addValue(SADisplayConstants.INCIDENT_ID, incidentId), 
		    			Integer.class);
		    	
		    	return (ret == SADisplayConstants.ADMIN_ROLE_ID);
	    	}catch(Exception e){
	    		return false;
	    	}
    }
    
    public List<Map<String, Object>> getIncidentOrg(int workspaceId){
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.INCIDENT_ID);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.ORG_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_TABLE)
   			 	 .selectFromTable(fields.toString())
				 .join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.ORG_ID)
				 .join(SADisplayConstants.USER_SESSION_TABLE).using(SADisplayConstants.USER_ORG_ID)
				 .join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.USERSESSION_ID)
				 .where().equals(SADisplayConstants.WORKSPACE_ID)
				 .and().equals(SADisplayConstants.ACTIVE);
				 
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId)
    		.addValue(SADisplayConstants.ACTIVE, true);
		
		return template.queryForList(queryModel.toString(), map);
    }
    
    public int getIncidentId(String name){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
    			.selectFromTableWhere(SADisplayConstants.INCIDENT_ID)
    			.equals(SADisplayConstants.INCIDENT_NAME);
    	
    	try{
	    	return this.template.queryForInt(queryModel.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.INCIDENT_NAME, name));
    	}catch(Exception e){
    		log.info("Could not find incident id for incident #0", name);
    	}
    	return -1;
    }
    
    public List<IncidentType> getIncidentTypes(){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TYPE_TABLE)
    			.selectAllFromTable().orderBy(SADisplayConstants.INCIDENT_TYPE_NAME);
    	JoinRowCallbackHandler<IncidentType> handler = getIncidentTypeHandlerWith();
	    template.query(queryModel.toString(), new MapSqlParameterSource(), handler);
    	return handler.getResults();
    }
    
    public int getNextIncidentIncidentTypeId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.HIBERNATE_SEQUENCE_TABLE).selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Error retrieving the next incident incident type id: #0", e.getMessage());
    	}
    	return -1;
	}

	/** getIncidents - return all active incidents
   	 *  @return List<Incident> 
   	 */
    public List<Incident> getIncidents(int workspaceId) {
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE).selectAllFromTable()
    			.left().join(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE).using(SADisplayConstants.INCIDENT_ID)
    			.left().join(SADisplayConstants.INCIDENT_TYPE_TABLE).using(SADisplayConstants.INCIDENT_TYPE_ID)
    			.where().equals(SADisplayConstants.ACTIVE)
    			.and().equals(SADisplayConstants.WORKSPACE_ID)
    			.orderBy(SADisplayConstants.CREATED).desc();
    	
    	JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith(new Incident_IncidentTypeRowMapper().attachAdditionalMapper(new IncidentTypeRowMapper()));
	    template.query(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.ACTIVE, true)
	    	 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
    	return handler.getResults();
    }
    
    /** getIncidents - return all active incidents
   	 *  @return List<Incident> 
   	 */
    public List<Map<String, Object>> getActiveIncidents(int workspaceId, int orgId, boolean active) {
    	
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.INCIDENT_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.INCIDENT_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
    			.selectFromTable(fields.toString())
    			.join(SADisplayConstants.USER_SESSION_TABLE).using(SADisplayConstants.USERSESSION_ID)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.ACTIVE)
    			.and().equals(SADisplayConstants.WORKSPACE_ID)
    			.and().equals(SADisplayConstants.ORG_ID)
    			.orderBy(SADisplayConstants.INCIDENT_CREATED).desc();
    	try{
    		return template.queryForList(queryModel.toString(), 
		         new MapSqlParameterSource(SADisplayConstants.ACTIVE, active)
		    	 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceId)
		    	 .addValue(SADisplayConstants.ORG_ID, orgId));
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public boolean setIncidentActive(int incidentId, boolean active){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.update().equals(SADisplayConstants.ACTIVE)
				.where().equals(SADisplayConstants.INCIDENT_ID);

		MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentId);
		map.addValue(SADisplayConstants.ACTIVE, active);
		
		return (this.template.update(queryModel.toString(), map) == 1);
    }
    
    public List<Map<String, Object>> findArchivedIncidentsByPrefix(int workspaceId, String prefix){
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.INCIDENT_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.INCIDENT_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTable(fields.toString())
				.join(SADisplayConstants.USER_SESSION_TABLE).using(SADisplayConstants.USERSESSION_ID)
				.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
				.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
				.where().equals(SADisplayConstants.PREFIX)
				.and().equals(SADisplayConstants.ACTIVE)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	try{
    		return template.queryForList(queryModel.toString(), 
		         new MapSqlParameterSource(SADisplayConstants.ACTIVE, false)
		    	 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceId)
		    	 .addValue(SADisplayConstants.PREFIX, prefix));
    	}catch(Exception e){
    		return null;
    	}
    }
    
    public List<Map<String, Object>> findArchivedIncidentsByName(int workspaceId, String name){
    	StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.INCIDENT_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.INCIDENT_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTable(fields.toString())
				.where().ilike(SADisplayConstants.INCIDENT_NAME).value("'%" + name + "%'")
				.and().equals(SADisplayConstants.ACTIVE)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	try{
    		return template.queryForList(queryModel.toString(), 
		         new MapSqlParameterSource(SADisplayConstants.ACTIVE, false)
		    	 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceId)
		    	 .addValue(SADisplayConstants.INCIDENT_NAME, name));
    	}catch(Exception e){
    		return null;
    	}
	}
    
    /** getIncidentsAndChildren - return all active incidents and there children
   	 *  @return List<Incident> 
   	 */
    public List<Incident> getIncidentsTree(int workspaceId) {
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE).selectAllFromTable()
    			.left().join(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE).using(SADisplayConstants.INCIDENT_ID)
    			.left().join(SADisplayConstants.INCIDENT_TYPE_TABLE).using(SADisplayConstants.INCIDENT_TYPE_ID)
    			.where().equals(SADisplayConstants.ACTIVE)
    			.and().equals(SADisplayConstants.WORKSPACE_ID).and().isNull(SADisplayConstants.PARENT_INCIDENT_ID)
    			.orderBy(SADisplayConstants.CREATED).desc();
    	
    	JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith(new Incident_IncidentTypeRowMapper().attachAdditionalMapper(new IncidentTypeRowMapper()));
	    template.query(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.ACTIVE, true)
	    	 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
    	return getIncidentsTree( workspaceId, handler.getResults());
    }
    
    /** getIncidents - return all active incidents
   	 *  @return List<Incident> 
   	 */
    public List<Incident> getIncidents(){
    	
    	if(this.template == null) {
            this.initialize();
        }
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE).selectAllFromTable()
    			.left().join(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE).using(SADisplayConstants.INCIDENT_ID)
    			.left().join(SADisplayConstants.INCIDENT_TYPE_TABLE).using(SADisplayConstants.INCIDENT_TYPE_ID)
    			.where().equals(SADisplayConstants.ACTIVE)
    			.orderBy(SADisplayConstants.CREATED).desc();
    	
    	JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith(new Incident_IncidentTypeRowMapper().attachAdditionalMapper(new IncidentTypeRowMapper()));
	    template.query(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.ACTIVE, true), handler);
    	return handler.getResults();
    }
    
    
    /** getIncidents - return all active incidents
   	 *  @return List<Incident> 
   	 */
    public List<Map<String,Object>> getArchivedIncidentNames(String prefix, int workspaceid){
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
    			.selectFromTableWhere(SADisplayConstants.INCIDENT_NAME + "," + SADisplayConstants.INCIDENT_ID + "," + 
						SADisplayConstants.PARENT_INCIDENT_ID)
    			.equals(SADisplayConstants.FOLDER)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	return this.template.queryForList(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.FOLDER, SADisplayConstants.ARCHIVED + prefix)
    		 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceid));
    }
    
    public List<Incident> getNonArchivedIncidents(int workspaceid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectAllFromTableWhere()
				.equals(SADisplayConstants.ACTIVE)
				.and().equals(SADisplayConstants.FOLDER)
				.orderBy(SADisplayConstants.CREATED).desc();
    	
    	JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith(new Incident_IncidentTypeRowMapper().attachAdditionalMapper(new IncidentTypeRowMapper()));
	    template.query(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.ACTIVE, true)
	    	.addValue(SADisplayConstants.FOLDER, ""), handler);
    	return handler.getResults();
    }
    
    public List<Incident> getIncidentsByName(List<String> names, int workspaceid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectAllFromTableWhere()
				.inAsString(SADisplayConstants.INCIDENT_NAME, names)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith(new Incident_IncidentTypeRowMapper().attachAdditionalMapper(new IncidentTypeRowMapper()));
	    template.query(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceid), handler);
    	return handler.getResults();
    	
    }
    
    /** getArchivedIncidents
	 *  @return List<String> - list of incident names that have been archived
	 */
	public List<Map<String,Object>> getActiveIncidentNames(int orgid, int workspaceid){
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTable(SADisplayConstants.INCIDENT_NAME + "," + SADisplayConstants.INCIDENT_ID + "," + 
						SADisplayConstants.PARENT_INCIDENT_ID)
    			.join(SADisplayConstants.USER_SESSION_TABLE).using(SADisplayConstants.USERSESSION_ID)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
    			.where().equals(SADisplayConstants.ORG_ID).and().equals(SADisplayConstants.FOLDER)
    			.and().notEqual(SADisplayConstants.INCIDENT_ID)
    			.and().equals(SADisplayConstants.WORKSPACE_ID); //don't include the NoIncident row
    	
		return this.template.queryForList(queryModel.toString(), 
	         new MapSqlParameterSource(SADisplayConstants.FOLDER, "")
				.addValue(SADisplayConstants.ORG_ID, orgid)
				.addValue(SADisplayConstants.INCIDENT_ID, 0)
				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceid));
    }
	
	public List<Incident> getIncidentsAccessibleByUser(int workspaceId, long accessibleByUserId) {
		List<Incident> incidents = new ArrayList<Incident>();
		
		
		
		return incidents;
	}
	
	public void updateIncidentFolder(List<String> incidentNames, String folder, int workspaceid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.update().equals(SADisplayConstants.FOLDER).where()
				.inAsString(SADisplayConstants.INCIDENT_NAME, incidentNames)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	this.template.update(queryModel.toString(),  
    			new MapSqlParameterSource(SADisplayConstants.FOLDER, folder)
    			.addValue(SADisplayConstants.WORKSPACE_ID, workspaceid));
	}
	
	public List<String> getChildIncidentNames(List<String> incidentNames, int workspaceid){
		QueryModel incidentQuery = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTableWhere(SADisplayConstants.INCIDENT_ID)
				.inAsString(SADisplayConstants.INCIDENT_NAME, incidentNames)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTableWhere(SADisplayConstants.INCIDENT_NAME)
				.inAsSQL(SADisplayConstants.PARENT_INCIDENT_ID, incidentQuery.toString());
		
		return this.template.queryForList(queryModel.toString(),
				new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceid), String.class);
	}
	
	public List<String> getParentIncidentNames(List<String> incidentNames, int workspaceid){
		QueryModel incidentQuery = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTableWhere(SADisplayConstants.PARENT_INCIDENT_ID)
				.inAsString(SADisplayConstants.INCIDENT_NAME, incidentNames)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectFromTableWhere(SADisplayConstants.INCIDENT_NAME)
				.inAsSQL(SADisplayConstants.INCIDENT_ID, incidentQuery.toString());
		
		return this.template.queryForList(queryModel.toString(),
				new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceid), String.class);
	}
	
	public List<Map<String,Object>> getIncidentMapAdmins(int incidentid, String roomname){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CONTACT_TABLE)
				.selectFromTable(SADisplayConstants.VALUE + "," + SADisplayConstants.FIRSTNAME + "," + SADisplayConstants.LASTNAME)
				.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE).using(SADisplayConstants.USER_ID)
				.join(SADisplayConstants.COLLAB_ROOM_TABLE).using(SADisplayConstants.COLLAB_ROOM_ID)
				.join(SADisplayConstants.INCIDENT_TABLE).using(SADisplayConstants.INCIDENT_ID)
				.where().equals(SADisplayConstants.INCIDENT_ID)
				.and().equals(SADisplayConstants.COLLABROOM_AND_NAME)
				.and().equals(SADisplayConstants.CONTACT_TYPE_ID);
		
		return this.template.queryForList(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentid)
				.addValue(SADisplayConstants.COLLABROOM_AND_NAME, roomname)
				.addValue(SADisplayConstants.CONTACT_TYPE_ID, SADisplayConstants.EMAIL_TYPE_ID));
	}
	
	public int setIncidentCenter(String incidentname){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
		.update().equals("lat", "#{incident.lat}").comma().equals("lon", "#{incident.lon}")
		.where().equals(SADisplayConstants.INCIDENT_NAME);
		
		return this.template.update(query.toString(), new MapSqlParameterSource(SADisplayConstants.INCIDENT_NAME, incidentname));
	}
	
	public Incident getIncident(int incidentid){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE).selectAllFromTable()
				.left().join(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE).using(SADisplayConstants.INCIDENT_ID)
    			.left().join(SADisplayConstants.INCIDENT_TYPE_TABLE).using(SADisplayConstants.INCIDENT_TYPE_ID)
				.where().equals(SADisplayConstants.INCIDENT_ID);
		
		JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith();
	    
		this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentid), handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			log.info("No Incident was found with incident id #0", incidentid);
		}
		return null;
	}
	
	public Incident getIncidentByName(String incidentname, int workspaceId){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
				.selectAllFromTableWhere().equals(SADisplayConstants.INCIDENT_NAME)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
		
		JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith();
		
		this.template.query(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.INCIDENT_NAME, incidentname)
				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
		
		try{
			return handler.getSingleResult(); //Incident name is unique?
		}catch(Exception e){
			log.info("Error retreiving incident with name #0: #1", incidentname, e.getMessage());
		}
		return null;
	}
	
	public int getNextIncidentId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_SEQUENCE_TABLE).selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("There was an error retrieving the next incident id: #0", e.getMessage());
    	}
    	return -1;
     }
	
	 public List<Incident> getParentIncidents(int workspaceId){
		 QueryModel incidentQuery = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
					.selectAllFromTableWhere().isNull(SADisplayConstants.PARENT_INCIDENT_ID)
					.and()
					.equals(SADisplayConstants.ACTIVE)
					.and().equals(SADisplayConstants.FOLDER)
		 			.and().equals(SADisplayConstants.WORKSPACE_ID);
		 
		 JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith();
			
		 this.template.query(incidentQuery.toString(), 
				 	new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId)
					.addValue(SADisplayConstants.ACTIVE, true)
					.addValue(SADisplayConstants.FOLDER, ""), handler);
		 
		 try{
			return handler.getResults();
		}catch(Exception e){
			log.info("Error getting Incident parents for workspace #0", workspaceId);
		}
		return new ArrayList(); //return empty list
		 
	 }
	 
	 public List<Incident> getChildIncidents(int parentId){
		 QueryModel incidentQuery = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE)
					.selectAllFromTableWhere()
					.equals(SADisplayConstants.PARENT_INCIDENT_ID);
		 
		 JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith();
			
		 this.template.query(incidentQuery.toString(), 
				 	new MapSqlParameterSource(SADisplayConstants.PARENT_INCIDENT_ID, parentId), handler);
		 
		 try{
			return handler.getResults();
		}catch(Exception e){
			log.info("Error getting Incident children for parentid #0", parentId);
		}
		return new ArrayList(); //return empty list
		 
	 }

    /**
     * Query for the number of incidents associated with the specified workspace
     * 
     * @param workspaceId ID specifying workspace to filter incident count by
     * @return Number of incidents associated with the specified workspace if successful, -1 otherwise
     */
	public int getIncidentCount(int workspaceId) {
		int count = -1; 
		try {
			
			count = template.queryForObject("select count(*) from incident where workspaceid=:workspaceid", 
					new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId), Integer.class);
		} catch(Exception e) {
			log.error("Exception querying for number of incidents in workspace with ID: " + workspaceId + 
					": " + e.getMessage());
		}
		
		return count;
	}
	 
 	/** getIncidentsTree
	 *  @param parents
	 *  @return Setr<Incident>
	 */
	
	private List<Incident> getIncidentsTree(int workspaceId, List<Incident> parents) {
		
		List<Incident> incidents = new ArrayList<Incident>();
		
		for(int i = 0; i < parents.size(); i ++){
		
			Incident newIncident = parents.get(i);
			
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.INCIDENT_TABLE).selectAllFromTable()
	    			.left().join(SADisplayConstants.INCIDENT_INCIDENTTYPE_TABLE).using(SADisplayConstants.INCIDENT_ID)
	    			.left().join(SADisplayConstants.INCIDENT_TYPE_TABLE).using(SADisplayConstants.INCIDENT_TYPE_ID)
	    			.where().equals(SADisplayConstants.ACTIVE)
	    			.and().equals(SADisplayConstants.WORKSPACE_ID).and().equals(SADisplayConstants.PARENT_INCIDENT_ID)
	    			.orderBy(SADisplayConstants.CREATED).desc();
	    	
	    	JoinRowCallbackHandler<Incident> handler = getIncidentHandlerWith(new Incident_IncidentTypeRowMapper().attachAdditionalMapper(new IncidentTypeRowMapper()));
		    template.query(queryModel.toString(), 
		         new MapSqlParameterSource(SADisplayConstants.ACTIVE, true)
		    	 .addValue(SADisplayConstants.WORKSPACE_ID, workspaceId).addValue(SADisplayConstants.PARENT_INCIDENT_ID, parents.get(i).getIncidentid()), handler);
			
		    List<Incident> currentChildren = handler.getResults();
		    
			if(currentChildren.size() > 0){
				getIncidentsTree(workspaceId,currentChildren);
				newIncident.setChildren(currentChildren);
				newIncident.setLeaf(false);
			}
			
			incidents.add(newIncident);
		}
		
		return incidents;
	}

	 
    /** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Incident>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JoinRowCallbackHandler<Incident> getIncidentHandlerWith(JoinRowMapper... mappers) {
    	return new JoinRowCallbackHandler(new IncidentRowMapper(), mappers);
    }
    
    /** getHandlerWith
   	 *  @param mappers - optional additional mappers
   	 *  @return JoinRowCallbackHandler<Incident>
   	 */
       @SuppressWarnings({ "rawtypes", "unchecked" })
       private JoinRowCallbackHandler<IncidentType> getIncidentTypeHandlerWith(JoinRowMapper... mappers) {
           return new JoinRowCallbackHandler(new IncidentTypeRowMapper(), mappers);
       }
}
