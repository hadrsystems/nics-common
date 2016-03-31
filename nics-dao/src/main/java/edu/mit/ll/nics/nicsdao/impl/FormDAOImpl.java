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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.nics.nicsdao.query.QueryConstraint;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.Form;
import edu.mit.ll.nics.common.entity.FormType;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.FormDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.FormRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.FormTypeRowMapper;


public class FormDAOImpl extends GenericDAO implements FormDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(FormDAOImpl.class);
    	try {
    		this.template = new NamedParameterJdbcTemplate(datasource);
    	} catch(Exception e) {
    		e.printStackTrace();
    		log.warn("Exception initializing datasource! Continuing to allow for setting manually with "
    				+ "setDataSource()");
    	}
    }
    
    
    /**
     * Get a form with the specified ID
     * 
     * @param formId
     * @return
     */
    public Form getForm(long formId) {
    	Form form = null;
    	
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
    			.selectAllFromTable().where().equals(SADisplayConstants.FORM_ID, formId);
    	
    	try {
    		form = this.template.queryForObject(query.toString(), query.getParameters(), new FormRowMapper());
    	} catch (EmptyResultDataAccessException e) {
    		log.warn("No form with formid("+formId+") found...");
    	}
    	
    	return form;
    }
    
    /** getFolder
	 *  @param folderid - String - id of folder
	 *  @return Folder 
	 */
    public Form getForm(int incidentid, int usersessionid){
    	
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
    			.selectAllFromTable()    			
    			.join(SADisplayConstants.USERSESSION_TABLE).using(SADisplayConstants.USERSESSION_ID)
    			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
    			.where().equals(SADisplayConstants.INCIDENT_ID).and().equals(SADisplayConstants.USERSESSION_ID);
    	
		JoinRowCallbackHandler<Form> handler = getHandlerWith();
		
		template.query(
        		query.toString(), 
        		new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentid)
        		.addValue(SADisplayConstants.USERSESSION_ID, usersessionid), 
        		handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			log.info("No form was found for incidentid #0", incidentid);
		}
		return null;
	}
    
    public Form getForm(long seqnum, long seqtime, long incidentid, long formtypeid) {
    	Form form = null;    	
    	JoinRowCallbackHandler<Form> handler = getHandlerWith();
    	
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
    			.selectAllFromTable().where()
    			.equals("seqnum", seqnum).and()
    			.equals("seqtime", seqtime).and()
    			.equals("incidentid", incidentid).and()
    			.equals("formtypeid", formtypeid);
    	
    	try {
    		this.template.query(query.toString(), query.getParameters(), handler);
    		form = handler.getSingleResult();
    	} catch(Exception e) {
    		log.error("Exception querying for single form with seqnum, seqtime, incidentid, formtypeid: " +
    				seqnum + ", " + seqtime + ", " + incidentid + ", " + formtypeid + "\n", e);
    	}
    	
    	return form;
    }
    
    public int getFormTypeId(String typename){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TYPE_TABLE)
    			.selectFromTableWhere(SADisplayConstants.FORM_TYPE_ID)
    			.equals(SADisplayConstants.FORM_TYPE_NAME);
    	
    	try{
	    	return template.queryForInt(
	        		query.toString(), 
	        		new MapSqlParameterSource(SADisplayConstants.FORM_TYPE_NAME, typename));
    	}catch(Exception e){
    		log.info("Could not find form typeid for typename #0", typename);
    	}
    	return -1;
    }
    
    public List<Form> getForms(int incidentid, int formtypeid){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
			.selectAllFromTableWhere().equals(SADisplayConstants.FORM_TYPE_ID)
			.and()
			.equals(SADisplayConstants.INCIDENT_ID)
			.orderBy(SADisplayConstants.SEQ_TIME).asc();
    	
    	JoinRowCallbackHandler<Form> handler = getHandlerWith();
    	
    	template.query(
        		query.toString(), 
        		new MapSqlParameterSource(SADisplayConstants.INCIDENT_ID, incidentid)
        		.addValue(SADisplayConstants.FORM_TYPE_ID, formtypeid), 
        		handler);
        return handler.getResults();
    }
    
    /**
     * Currently just updates the 'message' field of the form specified
     */    
    public int updateFormMessage(Form form) {
    	if(form == null || form.getFormId() < 1) {
    		log.warn("Received invalid Form (was either null, or had no " + 
    				SADisplayConstants.FORM_ID + " set. Not updating message field.");
    		return 0;
    	}
    	
    	int result = 0;
    	
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
    			.update().equals("message", form.getMessage())
    			.where()
    			.equals(SADisplayConstants.FORM_ID, form.getFormId());
    	try {
    		result = this.template.update(query.toString(), query.getParameters());
    	} catch(Exception e) {
    		log.error("Exception updating 'message' field of Form with ID("+form.getFormId()+"): " 
    				+ e.getMessage(), e);
    	}
    	
    	return result;
    }
    
	public List<FormType> getFormTypes() {
		
		QueryModel query  = QueryManager.createQuery(SADisplayConstants.FORM_TYPE_TABLE)
				.selectAllFromTable();
		
		return template.query(query.toString(), query.getParameters(), new FormTypeRowMapper());
	}    

	
	public long getNextFormId() {
		long formId = -1;
		try {
			QueryModel model = QueryManager.createQuery("form_seq").selectNextVal();
			formId = template.queryForObject(model.toString(), new MapSqlParameterSource(), Long.class);
		} catch(Exception e) {
			log.error("Exception querying for next value in form_seq: " + e.getMessage());
		}
		return formId;
	}
	
	
	
	
	public List<Form> readForms(Set<Integer> formTypeIds,
			Map<String, Object> queryOpts) throws Exception {
				
		StringBuilder sql = new StringBuilder();

		// Build the Form type ID predicate; i.e., single value vs. multi-value.
		StringBuilder ftPred = new StringBuilder();
		if (formTypeIds.size() == 1) {
			ftPred.append("WHERE formtypeid=").append(formTypeIds.iterator().next());
		} else if (formTypeIds.size() > 1) {
			ftPred.append("WHERE formtypeid in (");
			boolean firstOne = true;
			for (Integer i : formTypeIds) {
				if (firstOne) {
					firstOne = false;
				} else {
					ftPred.append(", ");
				}
				ftPred.append(i);		
			}
			ftPred.append(")");
		}

		sql.append("SELECT ");		
		// Assemble the fields clause.
		if (queryOpts.containsKey(QueryConstraint.KEY_COLUMN_SELECTION)) {
			Set<String> columns = (Set<String>) queryOpts.get(
					QueryConstraint.KEY_COLUMN_SELECTION);
			if (columns.size() > 0) {
				sql.append(org.apache.commons.lang.StringUtils.join(columns,","))
				.append(" FROM Form ").append(ftPred);
			} else {
				sql.append("* FROM Form ").append(ftPred);				
			}
		} else {
			sql.append("* FROM Form ").append(ftPred);
		}

		// Was an incidentId passed?
		if (queryOpts.containsKey("incidentid")) {
			sql.append(" AND incidentid = ").append(queryOpts.get("incidentid"));
		}

		// Assemble the where clause.
		//if (user != null && !user.isEmpty()) {
		//	sql.append(" AND message LIKE '%\"user\":\"")
		//	.append(user).append("\"%'");
		//}

		// Assemble the optional clauses.
		if(queryOpts != null){
			processQueryConstraintOptions(sql, queryOpts);
		}
		// TODO: use formDao calls
		log.info("Form query: \n" + sql.toString() + "\n");
		//List<Object> results = PhinicsDbUtil.sqlExecuteNative(sql.toString(), Form.class, em, log);
		
		JoinRowCallbackHandler<Form> handler = new JoinRowCallbackHandler<Form>(new FormRowMapper());
		template.query(sql.toString(), handler);
		List<Form> queryForms = handler.getResults();
		log.info("Got forms: " + queryForms.size());
		
		/*List<Form> forms = new ArrayList<Form>(results.size());
		for (Object result : results) {
			forms.add((Form)result);
		}*/

		return queryForms;
	}
	
	
	/**
	 * Persists a Form entity. 
	 * 
	 * <p>If the formid is set and exists, the following fields are updated:<br/>
	 * <ul>
	 * 	<li>message</li>
	 *  <li>distributed</li>  
	 *  <li>seq_time</li>
	 * </ul>
	 * </p>
	 * 
	 * <p>
	 * If formId is NOT set, it'll assume it's a new Form, get the next FormId in the sequence, and
	 * persist it.
	 * </p>
	 * 
	 * @param form Form entity to persist
	 * @return new/updated Form
	 */
	public Form persistForm(Form form) throws Exception {
		
		if(form == null) {
			log.debug("Received null Form entity, not persisting.");
			return null;
		}
		
		boolean update = false;
		Form newForm = null;		
		
		int formId = form.getFormId();
		Form oldForm = null;
		
		if(formId > 0) {
			try {
				oldForm = getForm(form.getFormId());
			} catch(Exception e) {
				log.error("Exception retrieving form with id: " + formId + ": " + e.getMessage());
			}
		}
		
		if(oldForm != null) {
			update = true;
		}
		
		if(update) {
			
			QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE).update()
					.equals(SADisplayConstants.MESSAGE).comma()
					.equals(SADisplayConstants.DISTRIBUTED).comma()				
					.equals("incidentname").comma()
					.equals(SADisplayConstants.SEQ_TIME)
					.where().equals("formId");
			
			BeanPropertySqlParameterSource map = new BeanPropertySqlParameterSource(form);
			
			int ret = -1;
			
			try {
				ret = this.template.update(query.toString(), map);
			} catch (Exception e) {
				throw new Exception("Error updating form with id: " + 
						oldForm.getFormId() + ": " + e.getMessage());
			}
			
			log.debug("Update performed on formid("+ formId + ") resulted in " + 
					((ret == 0) ? "no" : ret) + " affected row.");	
			
			if(ret == 1) {
				newForm = getForm(formId);
			}
			
			return newForm;
		} else {
			// Persist
			
			// TODO: refactor so either getNextFormId isn't long, or form isn't using an int for an ID
			// TODO: make form table default to use the next formid
			int newFormId = form.getFormId();
			if(form.getFormId() <= 0) {
				// Not set, so set it
				newFormId = (int)getNextFormId();
				form.setFormId(newFormId);
			} else {
				// Assume they set it already, and hope they used the form sequence
				log.debug("Using provided formid: " + form.getFormId());
			}
			
			List<String> fields = Arrays.asList(				
				"formId",
				"formtypeid",
				"incidentid",
				SADisplayConstants.USERSESSION_ID,
				SADisplayConstants.SEQ_TIME,
				SADisplayConstants.SEQ_NUM,
				SADisplayConstants.MESSAGE,
				SADisplayConstants.DISTRIBUTED,				
				"incidentname");
			
			BeanPropertySqlParameterSource map = new BeanPropertySqlParameterSource(form);
			
			QueryModel query = QueryManager.createQuery(SADisplayConstants.FORM_TABLE)
					.insertInto(fields);
			
			int ret = template.update(query.toString(), map);
			
			if(ret == 1) {
				newForm = getForm(newFormId);
			}
			
			return newForm;
		}
	}
	
	
	private void processQueryConstraintOptions(StringBuilder sql,
			Map<String, Object> queryOpts) throws Exception {
		
		
		
		if (queryOpts.containsKey(QueryConstraint.KEY_DATE_RANGE)) {
			QueryConstraint.UTCRange dateRange = (QueryConstraint.UTCRange)
					queryOpts.get(QueryConstraint.KEY_DATE_RANGE);
			try {
				
				if(dateRange != null && dateRange.colName != null && 
						dateRange.from != null){
					
					if(dateRange.to == null){
						dateRange.to = System.currentTimeMillis();
					}
					
					String timeClause = makeTimeClause(dateRange.colName,
							dateRange.from, dateRange.to, log);
					if (!timeClause.isEmpty()) {
						sql.append(" AND ").append(timeClause);
					}
				}
			} catch(IllegalArgumentException ex) {
				String errmsg = "Will not execute query: " + ex.getMessage();
				log.error(errmsg);
				throw new Exception(errmsg);
			}
		}

		if (queryOpts.containsKey(QueryConstraint.KEY_ORDER_BY)) {
			QueryConstraint.OrderBy orderBy = (QueryConstraint.OrderBy)
					queryOpts.get(QueryConstraint.KEY_ORDER_BY);		
			sql.append(" order by ").append(orderBy.colName).append(" ")
			.append(orderBy.type);
		}

		if (queryOpts.containsKey(QueryConstraint.KEY_RESULTSET_RANGE)) {
			QueryConstraint.ResultSetPage rsPage = (QueryConstraint.ResultSetPage)
					queryOpts.get(QueryConstraint.KEY_RESULTSET_RANGE);
			String resultSetRange = makeResultSetRangeClause(rsPage.offset, rsPage.limit);
			if (!resultSetRange.isEmpty()) {
				sql.append(resultSetRange);
			}			
		}		
	}
	
	
	protected static String makeResultSetRangeClause(Integer offset, Integer limit) {
		StringBuilder sb = new StringBuilder();

		if (limit != null && limit > 0) {
			sb.append(" LIMIT ").append(limit);
		}
		if (offset != null && offset > 0) {
			sb.append(" OFFSET ").append(offset);
		}
		
		return sb.toString();
	}
	
	
	protected static String makeTimeClause(String columnName, Long fromDate, Long toDate,
			Logger log) {
		
		StringBuilder sb = new StringBuilder();

		if (fromDate == null && toDate == null) {
			return "";
		}
		if (fromDate != null && toDate != null) {
			if (fromDate > toDate) {
				throw new IllegalArgumentException("fromDate cannot be greater than toDate");
			}
			if (fromDate.equals(toDate)) {
				sb.append(" ").append(columnName).append(" = ").append(fromDate);
				return sb.toString();
			} else {
				sb.append(" ").append(columnName).append(" BETWEEN ").
				append(fromDate).append(" AND ").append(toDate);
				return sb.toString();				
			}
		}
		if (fromDate != null) {
			sb.append(" ").append(columnName).append(" >= ").append(fromDate);
		} else {
			sb.append(" ").append(columnName).append(" <= ").append(toDate);
		}

		return sb.toString();
	}
	
    /** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Folder>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Form> getHandlerWith(JoinRowMapper... mappers) {
    	return new JoinRowCallbackHandler(new FormRowMapper(), mappers);
    }
	
}
