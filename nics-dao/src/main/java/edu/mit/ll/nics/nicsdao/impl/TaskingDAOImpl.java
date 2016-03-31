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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;

import edu.mit.ll.nics.common.entity.Assignment;
import edu.mit.ll.nics.common.entity.AssignmentId;
import edu.mit.ll.nics.common.entity.OperationalPeriod;
import edu.mit.ll.nics.common.entity.ResourceAssign;
import edu.mit.ll.nics.common.entity.TaskAssign;
import edu.mit.ll.nics.common.entity.Unit;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.CollabroomPermission;
import edu.mit.ll.nics.common.entity.Form;
import edu.mit.ll.nics.common.entity.User;
import edu.mit.ll.nics.common.constants.TaskingConstants;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
//import edu.mit.ll.nics.sadisplay.util.Util;
import edu.mit.ll.nics.nicsdao.CollabRoomDAO;
import edu.mit.ll.nics.nicsdao.FormDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.TaskingDAO;
import edu.mit.ll.nics.nicsdao.mappers.CollabRoomPermissionRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.FormRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.AssignmentRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.OperationalPeriodRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.ResourceAssignRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.TaskAssignRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UnitRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserRowMapper;

public class TaskingDAOImpl extends GenericDAO implements TaskingDAO {

	private Logger log;
	
	private static final FormDAOImpl formDao = new FormDAOImpl();
	private static final CollabRoomDAOImpl collabRoomDao = new CollabRoomDAOImpl();
	
	private NamedParameterJdbcTemplate template;
	
	final public static int SUCCESS = 0;
	final public static int FAILURE = 1;
	final public static int ALREADY_EXISTS = 2;

	@Override
	public void initialize() {
		log = LoggerFactory.getLogger(TaskingDAOImpl.class);
		this.template = new NamedParameterJdbcTemplate(datasource);
	}
	
	private int getNextOpPeriodId() {
    	QueryModel queryModel = QueryManager.createQuery("operational_period_seq").selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Could not retrieve next operational_period_id #0", e.getMessage());
    	}
    	return -1;
	}
	
	private int getNextTaskAssignId() {
		QueryModel queryModel = QueryManager.createQuery("task_assign_seq").selectNextVal();
    	int id = -1;
		try{    		
    		id = this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    		log.info("Got next sequence value for task_assign: " + id);
    		return id;
    	}catch(Exception e){
    		log.info("Could not retrieve next task_assign_id #0", e.getMessage());
    	}
    	return id;
	}

	/**
	 * Checks to see if the specified op period exists.
	 * 
	 * @param start
	 * @param end
	 * @param incidentId
	 * @return The id of the op period, if it exists, -1 otherwise
	 */
	public long opPeriodExists(long start, long end, long incidentId) {
		/*
		QueryModel query = QueryManager.createQuery(TaskingConstants.OPPERIOD_TABLE)
				.selectFromTableWhere()
				.equals(TaskingConstants.OPPERIOD_INCIDENT_ID, incidentId).and()
				.equals("start_utc", start).and()
				.equals("end_utc", end);
		*/			
		
		// TODO: unsafe to just concatenate parameters, but can't find way to simply get a count
		// via spring jdbc or QueryManager/QueryModel
		
		//String sql = "SELECT COUNT(*) FROM operational_period where incident_id=" + incidentId +
		// 		" and " + "start_utc=" + start + " and end_utc=" + end;
		
		QueryModel query = QueryManager.createQuery(TaskingConstants.OPPERIOD_TABLE)
				.selectFromTable("id").where()
				.equals("incident_id", incidentId).and()
				.equals("start_utc", start).and()
				.equals("end_utc", end);
		long existingId = -1;
		try {
			existingId = this.template.queryForLong(query.toString(), query.getParameters());
		} catch(EmptyResultDataAccessException e) {
			log.info("Op Period does not exist!");
		} catch(DataAccessException e) {
			log.error("got an exception checking for existing OpPeriod", e);
		} catch(Exception e) {
			log.error("Caught unhandled exception checking for existing OpPeriod", e);
		}
		
		return existingId;
		
		/*
		// Really still need to pass in a map? ...
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("incident_id", incidentId);
		paramMap.addValue("start_utc", start);
		paramMap.addValue("end_utc", end);		
		int count = this.template.queryForInt(sql, paramMap);
		
		if(count > 0) {
			return true;
		} else {
			return false;
		}
		*/
	}
	
	
	public int createOperationalPeriod(long start, long end, long incidentId) {
		
		if(opPeriodExists(start, end, incidentId) > 0) {
			log.info("The opPeriod with the specified start and end already exists(" + 
					incidentId + "), so not adding");
			
			// TODO: why return all op periods? Wasteful
			//return this.getOperationalPeriodsString((int)incidentId);
			return ALREADY_EXISTS;
		}
				
		try {
			List<String> fields = new ArrayList<String>();
			fields.add("id");
			fields.add(TaskingConstants.OPPERIOD_INCIDENT_ID);
			fields.add(TaskingConstants.OPPERIOD_START_UTC);
			fields.add(TaskingConstants.OPPERIOD_END_UTC);			
			
			long id = getNextOpPeriodId();
			// TODO: handle if -1, means there was a failure retrieving it
			
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("id", id);
			paramMap.addValue(TaskingConstants.OPPERIOD_INCIDENT_ID, incidentId);
			paramMap.addValue(TaskingConstants.OPPERIOD_START_UTC, start);
			paramMap.addValue(TaskingConstants.OPPERIOD_END_UTC, end);
			
			QueryModel query = QueryManager.createQuery(TaskingConstants.OPPERIOD_TABLE)
					.insertInto(fields);
									
			int result = this.template.update(query.toString(), paramMap);
			log.info("Affected rows on addOpPeriod: " + result);
			// TODO: check if affected 1 row as expected, or if 0 comes back?
			if(result == 1) {
				return SUCCESS;
			}
			
		} catch(Exception e) {
			log.error("failed persisting new opPeriod: " + e.getMessage());
		}
		// TODO: why return all op periods? Wasteful
		//return this.getOperationalPeriods();
		//return this.getOperationalPeriodsString((int)incidentId);
		return FAILURE;
	}
	
	
	public String getOperationalPeriodsString(int incidentId) {
		QueryModel query = QueryManager.createQuery(TaskingConstants.OPPERIOD_TABLE)
				.selectAllFromTableWhere()
				.equals(TaskingConstants.OPPERIOD_INCIDENT_ID, incidentId);
		log.info("\n\nQuery for OP Periods with id set to " + incidentId + ": " + query.toString() + "\n\n");		
		List<OperationalPeriod> ops = this.template.query(query.toString(),
				new MapSqlParameterSource(TaskingConstants.OPPERIOD_INCIDENT_ID, incidentId),
				new OperationalPeriodRowMapper());
		
		JSONArray arr = new JSONArray();
		for(OperationalPeriod pop : ops) {
			arr.put(pop.toJSONObject());
		}
		
		return arr.toString();
	}
	
	
	public int createAssignment(long unitId, long operationalPeriodId) {
		int status = FAILURE;
		
		// TODO: check to see if assignment exists, Try finding out in exception first
		// 	rather than querying and checking for results
		if(getAssignment(unitId, operationalPeriodId) != null) {
			return ALREADY_EXISTS;
		}
		
		List<String> fields = new ArrayList<String>();
		fields.add("unit_id");
		fields.add("operational_period_id");
		fields.add("published");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("unit_id", unitId);
		paramMap.addValue("operational_period_id", operationalPeriodId);
		paramMap.addValue("published", false);
		
		QueryModel query = QueryManager.createQuery(TaskingConstants.ASSIGNMENT_TABLE)
				.insertInto(fields);
		
		try {
			int result = this.template.update(query.toString(), paramMap);
			
			if(result == 1) {
				status = SUCCESS;
			}
		} catch(DataAccessException e) {
			log.error("DataAccessException while creating assignment: " + e.getMessage() + 
					"\nMost Specific Cause: " 
					+ ((e.getMostSpecificCause() != null) ? "Unknown" : e.getMostSpecificCause().getMessage()));
			
			log.debug("Exception", e);
			
			// TODO: See if you can find out if it hit an already exists exception, and return ALREADY_EXISTS
		} catch(Exception e) {
			log.error("Unhandled exception while creating assignment: " + e.getMessage(), e);
		}
		
		return status;
	}
	
	
	public int publishAssignment(int unitId, int opPeriod) {
		
		QueryModel query = QueryManager.createQuery(TaskingConstants.ASSIGNMENT_TABLE)
				.update().equals("published", true)
				.where()
				.equals("unit_id").and()
				.equals("operational_period_id");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("published", true);
		paramMap.addValue("unit_id", unitId);
		paramMap.addValue("operational_period_id", opPeriod);
		
		int ret = this.template.update(query.toString(), paramMap);
		
		if(ret == 0) {
			return FAILURE;		
		}
		
		return SUCCESS;
	}
		
	public Assignment getAssignment(AssignmentId id) {
		return getAssignment(id.getUnitId(), id.getOperationalPeriodId());
	}
	
	public Assignment getAssignment(long unitId, long opPeriodId) {
		QueryModel queryAssignment = QueryManager.createQuery(TaskingConstants.ASSIGNMENT_TABLE)
				.selectAllFromTableWhere()
				.equals(TaskingConstants.ASSIGNMENT_UNIT_ID, unitId)
				.and()
				.equals(TaskingConstants.ASSIGNMENT_OPPERIOD_ID, opPeriodId);
				
		
		Assignment assignment = null;
		Unit unit = null;
		OperationalPeriod opPeriod = null;
				
		log.info("Attempting to query for Assignment with unitId and opPeriod: #?, #?", unitId, opPeriodId);
		
		try{
			unit = (Unit) this.template.queryForObject("select * from unit where id=" + unitId,
					new MapSqlParameterSource("id", unitId),
					new UnitRowMapper());
			
			log.info("unit: " + ((unit != null)? unit.toJSONObject().toString() : "null"));
			
			opPeriod = (OperationalPeriod) this.template.queryForObject(
					"select * from operational_period where id=" + opPeriodId,
					new MapSqlParameterSource("id", opPeriodId),
					new OperationalPeriodRowMapper());
			log.info("opPeriod: " + ((opPeriod != null) ? opPeriod.toJSONObject().toString() : "null"));
						
			assignment = (Assignment) this.template.queryForObject(queryAssignment.toString(),
					queryAssignment.getParameters(),
					new AssignmentRowMapper());
			
			assignment.setOperationalPeriod(opPeriod);
			assignment.setUnit(unit);

			if(assignment != null) {
				log.info("\n!!!Got assignment(unitid, opperiodid, published: " +
						assignment.getUnit().getId() + ", " +
						assignment.getOperationalPeriod().getId() + ", " +
						assignment.isPublished());
			}
			
			MapSqlParameterSource paramMap = new MapSqlParameterSource();
			paramMap.addValue("unit_id", unitId);
			paramMap.addValue("operational_period_id", opPeriodId);
			
			List<ResourceAssign> resourceAssigns = this.template.query(
					"select * from resource_assign where unit_id="+unitId + 
						" and operational_period_id="+opPeriodId,
					paramMap, 
					new ResourceAssignRowMapper());
			log.info("Got resourceAssigns?" + ((resourceAssigns != null) ? resourceAssigns.size() : "none"));

			// Populate assignment, since the row mapper currently DOES NOT
			//for(ResourceAssign ra : resourceAssigns) {
			//	ra.setAssignment(assignment);
			//}			
						
			// Populate assignment, since the row mapper currently DOES NOT
			List<TaskAssign> taskAssigns = this.template.query(
					"select * from task_assign where unit_id="+unitId+
						" and operational_period_id="+opPeriodId,
						paramMap, new TaskAssignRowMapper());
			log.info("Got taskAssigns?" + ((taskAssigns != null) ? taskAssigns.size() : "none"));
			
			// TODO: TaskAssignRowMapper doesn't set all Form fields, just ID 
			//for(TaskAssign ta : taskAssigns) {
			//	ta.setAssignment(assignment);
			//}
			
			assignment.setResourceAssigns(new HashSet<ResourceAssign>(resourceAssigns));
			assignment.setTaskAssigns(new HashSet<TaskAssign>(taskAssigns));
			
			
			/* join mappers method...
			JoinRowCallbackHandler<Assignment> handler = getAssignmentHandlerWith(
					new OperationalPeriodRowMapper(), new UnitRowMapper()
					);
			
			this.template.query(query.toString(), query.getParameters(),
					handler);			
			assignment = handler.getSingleResult();
			log.info( ((assignment != null) ? "Got Assignment: \n" + 
					assignment.toJSONObject().toString() : "NULL"));
			*/
			
		} catch(EmptyResultDataAccessException e) {
			log.debug("No results with unitid and opperiodid ("+unitId+","+opPeriodId+")");
		} catch(Exception e) {
			log.error("Caught unhandled exception querying for single assignment with " +
					"unitid and opperiodid ("+unitId+","+opPeriodId+")", e);
		}
		
		return assignment;
	}
	
	public String getAssignmentString(long unitId, long opPeriodId) {
		Assignment pa = getAssignment(unitId, opPeriodId);
		return pa.toJSONObject().toString();
	}
	
	public String getAssignmentString(AssignmentId id) {
		Assignment pa = getAssignment(id);
		return pa.toJSONObject().toString();
	}
		
	public List<Assignment> getAssignments(int userId, Map<String, Object> queryOpts) {
		
		List<Assignment> assignments = null;
		
		StringBuilder sql = new StringBuilder();
		DateTime now = new DateTime();
		if (userId < 0) {
			sql.append("SELECT * from assignment where published = 't'");
		} else {
			boolean activeOnly = false;
			if (queryOpts.containsKey("activeOnly")) {
				activeOnly = (Boolean)queryOpts.get("activeOnly");
			}
			sql.append("SELECT a.unit_id, a.operational_period_id, a.published from assignment a, resource_assign r");
			if (activeOnly) {
				sql.append(", operational_period p");
			}
			sql.append(" WHERE a.published = 't' AND a.unit_id = r.unit_id");
			sql.append(" AND a.operational_period_id = r.operational_period_id");
			if (activeOnly) {
				sql.append(" AND a.operational_period_id = p.id");
			}
			sql.append(" AND r.user_id = ").append(userId);
			if (activeOnly) {
				sql.append(" AND ").append(now.getMillis()).append(" BETWEEN p.start_utc and p.end_utc");
			}
		}

		// TODO: We cannot implement Date range based queries at this time due
		// to the fact that task assignment entities do not have a seqTime
		// or other date column. The only concept of time is its association
		// with an Operational Period, which does have a beginning and ending
		// time. While this could be used, it is a deviation from the boiler
		// plate code written to handle NICS entities.
		// processQueryConstraintOptions(sql, queryOpts);

		//List<Object> entities = nicsDbUtil.sqlExecuteNative(sql.toString(), Assignment.class, em, log);
		//List<Assignment> ret = new ArrayList<Assignment>(entities.size());
		//for (Object e : entities) {
		//	ret.add((Assignment)e);
		//}
				
		JoinRowCallbackHandler<Assignment> handler = new JoinRowCallbackHandler<Assignment>(new AssignmentRowMapper());
		template.query(sql.toString(), handler);
		assignments = handler.getResults();
		
		return assignments;
	}
	
	public List<Assignment> getAssignments(int incidentId) {
//	public List<Object[]> getAssignments(int incidentId) {
		// TODO: Constants with relation names were being used, but a native query is being used
		//		 so I think the actual table names need used?
		/*String query = "SELECT * FROM " + TaskingConstants.ASSIGNMENT_TABLE + " where " +
				TaskingConstants.UNIT_TABLE + " IN (SELECT " + TaskingConstants.UNIT_ID +
				" FROM " + TaskingConstants.UNIT_TABLE + " WHERE " + 
				TaskingConstants.UNIT_INCIDENT_ID + " = :incId)";*/ 
		
		String query = "SELECT * FROM " + "assignment" + " where " +
				"unit_id" + " IN (SELECT " + "id" +
				" FROM " + "unit" + " WHERE " + 
				TaskingConstants.UNIT_INCIDENT_ID + " = :incId)";
		/* Original... native query isn't returning Assignments, but arrays with
		 * each index being a field from the assignment table
		List<Assignment> assignments = (List<Assignment>) em.createNativeQuery(query)
				.setParameter("incId", incidentId)
				.getResultList();*/
		
		QueryModel queryModel = QueryManager.createQuery("assignment").
				selectAllFromTable()
				.join("unit").on().equals("incident_id", incidentId);
				//.where().equals("incident_id", incidentId);
		
		List<Assignment> assignments = this.template.query(queryModel.toString(), 
				queryModel.getParameters(), new AssignmentRowMapper());
		
		//List<Object[]> assignments = em.createNativeQuery(query)
			//	.setParameter("incId", incidentId)
			//	.getResultList();
		
		// DEBUGGING
		//for(int i = 0; i < assignments.size(); i++) {
		//	Object obj[] = assignments.get(i);			
		//	log.info("\n!!! ArrayAssignment: " + obj[0] + ", " + obj[1] + ", " + obj[2]);			
		//}
		// DEBUGGING
		
		return assignments;
	}
	
	
	public String getAssignmentsString(int incidentId) {
		List<Assignment> assignments = getAssignments(incidentId);
		
		if(assignments.isEmpty()) {
			log.info("NO assignments returned for incident id: " + incidentId);
			return new JSONArray().toString();
		}
		
		JSONArray arr = new JSONArray();
				
		/* works if this was actually a list of full assignments, but narrowing it to 
		 * just return the 3 fields in a assignment
		for(Assignment a : assignments) {
			arr.put(a.toJSONObject());
		}*/
		JSONObject json;
		//for(Object[] objArr : assignments) {
		for(Assignment assign : assignments) {
			
			if(assign == null) {
				continue;
			}
			
			json = new JSONObject();
			
			try {
				AssignmentId id = assign.getId();
				
				if(id == null) {
					log.info("Warning! AssignmentId was null on Assignment, cannot return Assignment");
					continue;
				}
				
				json.put("unitid", id.getUnitId());
				json.put("opperiodid", id.getOperationalPeriodId());
				json.put("published", assign.isPublished());
				
				arr.put(json);
			} catch (JSONException e) {
				log.error("JSONException while building Assignment object: " + e.getMessage());
				e.printStackTrace();
			}
			
		}
		
		return arr.toString();
	}
	
	public int assignTask(AssignmentId id, long taskId) {
		return assignTask(id.getUnitId(), id.getOperationalPeriodId(), taskId);
	}
		
	public int assignTask(long unitId, long opPeriodId, long taskId) {
			
		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("unit_id");
		fields.add("operational_period_id");
		fields.add("form_id");
		fields.add("completed");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("id", getNextTaskAssignId());
		paramMap.addValue("unit_id", unitId);
		paramMap.addValue("operational_period_id", opPeriodId);
		paramMap.addValue("form_id", taskId);
		paramMap.addValue("completed", false);
		
		QueryModel query = QueryManager.createQuery("task_assign")
				.insertInto(fields);
		
		int result = -1;
		
		try {
			result = this.template.update(query.toString(), paramMap);
		} catch(DataAccessException e) {
			log.error("There was an exception attempting to insert a Task Assignment: " + e.getMessage(), e);
		}
						
		return result;
	}
	
	public int unassignTask(int taskId) {
		int result = -1;
		
		QueryModel query = QueryManager.createQuery("task_assign")
				.deleteFromTableWhere()
				.equals("id", taskId);
		
		try {
			result = this.template.update(query.toString(), query.getParameters());
		} catch(DataAccessException e) {
			log.error("Exception removing Task Assignment with ID: " + taskId, e);
		}
		
		return result;
	}
	
	public List<Form> getTasks(int[] formIds) {
		ArrayList<Form> forms = new ArrayList<Form> ();
		
		Form f = null;
		JoinRowCallbackHandler<Form> handler = new JoinRowCallbackHandler<Form>(new FormRowMapper());
		QueryModel query = null;
		for(int id : formIds) {
			query = QueryManager.createQuery("form")
					.selectAllFromTableWhere().equals("formid", id);
			this.template.query(
					query.toString(), 
					//"SELECT from form where formid = ?", 
					new MapSqlParameterSource("formid", id), 
					//new BeanPropertyRowMapper<Form>(Form.class));
					handler);
			
			f = handler.getSingleResult();
			
			if(f != null) {
				log.info("Got form with formid("+id+")!: " + f.getMessage());
				forms.add(f);
			} else {
				log.info("Query for form with id: " + id + " resulted in a null Form");
			}
		}
		
		return forms;
	}
	
	public String getTasksString(int[] formIds) {
		List<Form> forms = getTasks(formIds);
		
		JSONArray arr = new JSONArray();
		for(Form f : forms) {
			arr.put(f.toJSONObject());
		}
		
		return arr.toString();
	}
	
	
	public List<Form> getTasks(int incidentId) {
		return getForms(incidentId, TaskingConstants.TASK_TYPE);
	}
	
	/* (non-Javadoc)
	 * @see edu.mit.ll.nics.sadisplay.dao.TaskingDAOGenerated#getTasksString(int)
	 */
	/* (non-Javadoc)
	 * @see edu.mit.ll.nics.sadisplay.dao.TaskingDaoGen2#getTasksString(int)
	 */
	@Override
	public String getTasksString(int incidentId) {
		List<Form> tasks = getTasks(incidentId);
		JSONArray arr = new JSONArray();
		for(Form f : tasks) {
			arr.put(f.toJSONObject());
		}
		
		return arr.toString();
	}
	
	public String getTaskString(long seqnum, long seqtime, long incidentid, long formtypeid) {
			
		Form f = formDao.getForm(seqnum, seqtime, incidentid, formtypeid);
		if(f != null) {
			return f.toJSONObject().toString();
		}
		
		return new JSONObject().toString();
	}
	
	// Unit Accessors
	
	public List<Unit> getUnits(int incidentId) {
		QueryModel query = QueryManager.createQuery(TaskingConstants.UNIT_TABLE)
				.selectAllFromTableWhere()				
				.equals(TaskingConstants.UNIT_INCIDENT_ID, incidentId);
		
		List<Unit> units = this.template.query(query.toString(), 
				query.getParameters(), new UnitRowMapper());
		
		return units;
	}
	
	
	public String getUnitsString(int incidentId) {
				
		List<Unit> units = getUnits(incidentId);
		
		JSONArray arr = new JSONArray();
		for(Unit u : units) {
			arr.put(u.toJSONObject());
		}
		
		return arr.toString();
	}
	
	public String getUnitString(int incidentId, int collabRoomId) {
		QueryModel query = QueryManager.createQuery(TaskingConstants.UNIT_TABLE)
				.selectAllFromTableWhere()				
				.equals(TaskingConstants.UNIT_INCIDENT_ID, incidentId).and()
				.equals("collabroom_id", collabRoomId);
		
		JoinRowCallbackHandler<Unit> handler = new JoinRowCallbackHandler<Unit>(new UnitRowMapper());
		
		this.template.query(query.toString(), 
				query.getParameters(), handler);
		
		Unit unit = null;
		
		try {
			unit = handler.getSingleResult();
		} catch(Exception e) {
			log.error("Exception while querying for Unit with incidentid, collabroomid: " + incidentId + 
					", " + collabRoomId);
			return null;
		}
		
		return unit.toJSONObject().toString();
	}
	
	/**
	 *  TODO: maybe collabDao should have this query of permissions?
	 * 	TODO: maybe not... it additionally queries permissions and only adds permission based on results
	 */
	public void addCollabRoomPermission(int userid, int collabroomid, int roleid) {
		QueryModel query = QueryManager.createQuery(SADisplayConstants.COLLAB_ROOM_PERMISSION_TABLE)
				.selectAllFromTableWhere().equals(SADisplayConstants.USER_ID, userid).and()
				.equals(SADisplayConstants.COLLAB_ROOM_ID, collabroomid);
								
		List<CollabroomPermission> perms = this.template.query(query.toString(), 
				query.getParameters(), new CollabRoomPermissionRowMapper());
						
		log.info("Found permissions on room?: " + ((perms != null) ? perms.size() : "none"));
		
		boolean add = true;
		for(CollabroomPermission cp : perms) {
			if(cp.getSystemRoleId() == roleid) {
				add = false;
			}
		}
		
		if(add) {
			log.info("ADD is true, so adding permission with id " + roleid + " to collabroomid " + 
					collabroomid + " for user with id: " + userid);
			// TODO: no indication whether or not this fails or succeeds, since its return type is void
			collabRoomDao.createCollabPermission(collabroomid, roleid, userid);
		} else {
			log.info("NO permissions were found to add for " + "permission with id " + roleid + " to collabroomid " + 
					collabroomid + " for user with id: " + userid);
		}
	}
	
	// Util function
//	private static JSONArray getJSONArray(List<SADisplayMessageEntity> entities) {
//		JSONArray arr = new JSONArray();
//		for(SADisplayMessageEntity e : entities) {
//			try {
//				arr.put(e.toJSONObject());
//			} catch (JSONException e1) {
//				log.error("failure producign JSONObject: " + e1.getMessage());
//			}
//		}
//		
//		return arr;
//	}
	
/*
	private static List<Form> getForms_(EntityManager em, int incidentId, String type){
		List<Form> results = new ArrayList<Form>();
		
		int formTypeId = Util.getFormTypeId(type);
		if(formTypeId == -1){
			//formTypeId = Util.getFormTypeId(em, type);
		}
		
		if(formTypeId != -1){
			QueryModel incidentQuery = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
			.selectFromTableWhere().equals(SADisplayConstants.FORM_TYPE_ID, formTypeId)
			.and()
			.equals(SADisplayConstants.INCIDENT_ID, incidentId)
			.orderBy(SADisplayConstants.SEQ_TIME).asc();
			
			results = QueryManager.getResultList(em, incidentQuery);
			
			//log.debug("got result list of size #0 for incident status messages", results.size());
		}
		
		return results;
	}
*/	
	
	private List<Form> getForms(int incidentId, String type){
		List<Form> results = new ArrayList<Form>();
		
		//int formTypeId = Util.getFormTypeId(type);
		int formTypeId = formDao.getFormTypeId(type);
		if(formTypeId == -1){
			log.error("Formtypeid not found for type: " + type + ", unable to get forms");			
		} else {
			results = formDao.getForms(incidentId, formTypeId);
			log.debug("got result list of size #0 for forms of type: " + type + ", ", results.size());
		}
		
		return results;
	}
	
	
	public int updateAssignmentLeader(long unitId, long userId, long opPeriodId) {
		QueryModel query = QueryManager.createQuery("resource_assign")
				.update().equals("leader", false)
				.where().equals("unit_id")
				.and().equals("operational_period_id")
				.and().notEqual("user_id");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("unit_id", unitId);
		paramMap.addValue("leader", false);
		paramMap.addValue("operational_period_id", opPeriodId);
		paramMap.addValue("user_id", userId);
				
		int falsify = this.template.update(
				query.toString(),
				paramMap
		);
		log.debug("Set " + falsify + " members to leader=false");		
		
		MapSqlParameterSource paramMapTrue = new MapSqlParameterSource();
		paramMapTrue.addValue("unit_id", unitId);
		paramMapTrue.addValue("leader", true);
		paramMapTrue.addValue("operational_period_id", opPeriodId);
		paramMapTrue.addValue("user_id", userId);
		
		QueryModel update = QueryManager.createQuery("resource_assign")
				.update().equals("leader", true)
				.where().equals("unit_id", unitId)
				.and().equals("operational_period_id", opPeriodId)
				.and().equals("user_id", userId);
		
		//String updateQuery = "update resource_assign set leader=? where unit_id=? and " +
				//"operational_period_id=? and user_id =?";
		int newLeader = this.template.update(
				//updateQuery, 
				update.toString(), 
				//update.getParameters()
				paramMapTrue
			);
		
		if(newLeader == 1) {
			log.info("Set user with id " + userId + " as the leader");
		} else {
			log.warn("Failed to set new leader with userId: " + userId);
		}
		
		return newLeader;
	}
	
	
	public long getNextAssignResourceId() {
		QueryModel queryModel = QueryManager.createQuery("resource_assign_seq").selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Could not retrieve next resource_assign_seq #0", e.getMessage());
    	}
    	return -1;
	}
	
	public long getNextUnitId() {
		QueryModel queryModel = QueryManager.createQuery("unit_seq").selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Could not retrieve next unit_seq #0", e.getMessage());
    	}
    	return -1;
	}
	
	// TODO: don't use/delete? Look at updateAssignmentLeader
	public int assignLeader(int unitId, int opPeriodId, int userId) {
		
		int result = -1;
		
		QueryModel query = QueryManager.createQuery("resource_assign")
				.update("leader").value("f")
				.where()
				.equals("user_id", userId).and()
				.equals("unit_id", unitId).and()
				.equals("operational_period_id", opPeriodId);
		
		try {
			result = this.template.update(query.toString(), query.getParameters());
		} catch(DataAccessException e) {
			log.error("Exception assigning new leader: " + e.getMessage());
		}
			
		return result;
	}
	
	public int assignResource(ResourceAssign resourceAssignment) {
		
		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("user_id");
		fields.add("unit_id");
		fields.add("operational_period_id");
		fields.add("leader");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("id", getNextAssignResourceId());
		paramMap.addValue("user_id", resourceAssignment.getUserid());
		paramMap.addValue("unit_id", resourceAssignment.getAssignment().getUnit().getId());
		paramMap.addValue("operational_period_id", resourceAssignment.getAssignment().getOperationalPeriod().getId());
		paramMap.addValue("leader", resourceAssignment.isLeader());
		
		QueryModel query = QueryManager.createQuery("resource_assign")
				.insertInto(fields);
		
		int ret = this.template.update(query.toString(), paramMap);
		if(ret == 0) {
			return FAILURE;			
		}
		
		return SUCCESS;
	}
	
	public int assignResource(int userId, int unitId, int opPeriodId, boolean leader) {
		
		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("user_id");
		fields.add("unit_id");
		fields.add("operational_period_id");
		fields.add("leader");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("id", getNextAssignResourceId());
		paramMap.addValue("user_id", userId);
		paramMap.addValue("unit_id", unitId);
		paramMap.addValue("operational_period_id", opPeriodId);
		paramMap.addValue("leader", leader);
		
		QueryModel query = QueryManager.createQuery("resource_assign")
				.insertInto(fields);
		
		int ret = this.template.update(query.toString(), paramMap);
		if(ret == 0) {
			return FAILURE;			
		}
		
		return SUCCESS;
	}
	
	public int removeResourceAssign(ResourceAssign resourceAssign) {
				
		QueryModel query = QueryManager.createQuery("resource_assign")
				.deleteFromTableWhere().equals("id", resourceAssign.getId());
		
		int result = 0;
		try {
			result = this.template.update(query.toString(), query.getParameters());
		} catch(Exception e) {
			log.error("Exception deleting Resource Assignment with id("+resourceAssign.getId()+"): " +
					e.getMessage(), e);
		}
		
		return result; 
	}
	
	
	public ResourceAssign getResourceAssign(long unitId, long opPeriodId, long userId) {
				
		QueryModel query = QueryManager.createQuery("resource_assign").selectAllFromTableWhere()
			.equals("unit_id", unitId).and().equals("operational_period_id", opPeriodId)
			.and().equals("user_id", userId);
		
		ResourceAssign resource = (ResourceAssign) this.template.queryForObject(query.toString(),
				query.getParameters(),
				new ResourceAssignRowMapper());		
		
		log.info("getResourceAssign: Found resource: " + ((resource != null) ? resource.getId() : "null"));
		
		return resource;
	}

	
	public boolean isResourceAssignedForOpPeriod(long opPeriodId, long userId) {
		QueryModel query = QueryManager.createQuery("resource_assign")
				.selectAllFromTableWhere()
				.equals("operational_period_id", opPeriodId)
				.and()
				.equals("user_id", userId);
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		paramMap.addValue("operational_period_id", opPeriodId);
		paramMap.addValue("user_id", userId);
		
		
		ResourceAssign ra = null;
		try {
			ra = this.template.queryForObject(query.toString(), paramMap, new ResourceAssignRowMapper());
		} catch(EmptyResultDataAccessException e) {
			log.warn("No Resource assignment found for [userId, opPeriodId]: " +
					userId + ", " + opPeriodId);
		}
		
		if( ra != null) {
			log.info("Testing to see if resource was assigned for op period, got: " + 
					ra.toJSONObject().toString());
			return true;
		} else {
			log.info("NO resource found assigned for that op period!");
		}
		
		return false;
	}
	
	
	public List<User> getAssignedUsers() {
			
		QueryModel query = QueryManager.createQuery("resource_assign")
				.selectDistinctFromTable("user_id")
				.join(SADisplayConstants.USER_ESCAPED);
		
		String queryStr = "select * from " + SADisplayConstants.USER_ESCAPED + ", resource_assign where " +
				"user_id = user_id";
				
		// TODO: there aren't really any mapped parameters, but... 
		List<User> assignedUsers = this.template.query(queryStr, query.getParameters(),
				new UserRowMapper());
		
		return assignedUsers;
	}
	
	public int createUnit(Unit unit) {
		int status = FAILURE;
		
		List<String> fields = new ArrayList<String>();
		fields.add("id");
		fields.add("incident_id");
		fields.add("unitname");
		fields.add("collabroom_id");
		
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		long newId = getNextUnitId();
		if(newId == -1) {
			log.info("Failed to get a valid id for the next unit!");
			return FAILURE;
		}
		paramMap.addValue("id", newId);
		paramMap.addValue("incident_id", unit.getIncidentid());
		paramMap.addValue("unitname", unit.getUnitname());
		paramMap.addValue("collabroom_id", unit.getCollabroomid());
		
		QueryModel query = QueryManager.createQuery(TaskingConstants.UNIT_TABLE)
				.insertInto(fields);
		
		try {
			int result = this.template.update(query.toString(), paramMap);
			
			if(result == 1) {
				status = SUCCESS;
			}
		} catch(DataAccessException e) {
			log.error("DataAccessException while creating Unit: " + e.getMessage() + 
					"\nMost Specific Cause: " 
					+ ((e.getMostSpecificCause() != null) ? "Unknown" : e.getMostSpecificCause().getMessage()));
			
			log.debug("Exception", e);
			
			// TODO: See if you can find out if it hit an already exists exception, and return ALREADY_EXISTS
		} catch(Exception e) {
			log.error("Unhandled exception while creating Unit: " + e.getMessage(), e);
		}
		
		return status;
	}
	
	@SuppressWarnings({"rawtypes"})
	private JoinRowCallbackHandler<Assignment> getAssignmentHandlerWith(JoinRowMapper...mappers) {
		return new JoinRowCallbackHandler<Assignment>(new AssignmentRowMapper(), mappers);
	}
}
