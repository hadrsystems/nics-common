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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.common.entity.Log;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.LogDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.LogRowMapper;

public class LogDAOImpl extends GenericDAO implements LogDAO {

	private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(FeatureDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    @Override
	public List<Log> getLogs(int workspaceId, int logTypeId) {
    	JoinRowCallbackHandler<Log> handler = getHandlerWith();
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.LOG_TABLE)
				.selectAllFromTable()
				.where().equals(SADisplayConstants.WORKSPACE_ID)
				.and().equals(SADisplayConstants.LOG_TYPE_ID);
		
		template.query(
        		queryModel.toString(), 
        		new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId)
        		.addValue(SADisplayConstants.LOG_TYPE_ID, logTypeId), 
        		handler);
		
		try{
			return handler.getResults();
		}catch(Exception e){
			log.info("Could not find logs for workspace : " + workspaceId);
		}
		return null;
	}

	@Override
	public boolean postLog(int workspaceId, Log log) {
		if(log.getLogid() > 0){
			QueryModel query = QueryManager.createQuery(SADisplayConstants.LOG_TABLE)
					.update(Arrays.asList(SADisplayConstants.MESSAGE, SADisplayConstants.USERSESSION_ID))
					.where().equals(SADisplayConstants.LOG_ID, log.getLogid());
				
			int ret = this.template.update(query.toString(), new MapSqlParameterSource()
					.addValue(SADisplayConstants.MESSAGE, log.getMessage())
					.addValue(SADisplayConstants.USERSESSION_ID, log.getUsersessionid())
					.addValue(SADisplayConstants.LOG_ID, log.getLogid())
					.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId));
			
			return (ret == 1);
		}else{
			Map<String, Object> params = new HashMap<String, Object>();
			params.put(SADisplayConstants.USERSESSION_ID, log.getUsersessionid());
			params.put(SADisplayConstants.LOG_TYPE_ID, log.getLogtypeid());
			params.put(SADisplayConstants.MESSAGE, log.getMessage());
			params.put(SADisplayConstants.WORKSPACE_ID, workspaceId);
			
			List<String> fields = new ArrayList<String>(params.keySet());
			QueryModel model = QueryManager.createQuery(SADisplayConstants.LOG_TABLE).insertInto(fields);
			
			int ret = this.template.update(model.toString(), params);
			
			return (ret == 1);
		}
	}

	@Override
	public boolean deleteLog(int logId) {
		try{
			MapSqlParameterSource paramMap = new MapSqlParameterSource(SADisplayConstants.LOG_ID, logId);
			
			QueryModel logQuery = QueryManager.createQuery(SADisplayConstants.LOG_TABLE)
					.deleteFromTableWhere().equals(SADisplayConstants.LOG_ID);
			
			return (this.template.update(logQuery.toString(), paramMap) == 1);
			
		}catch(Exception e){
			log.info("Failed to delete log entry with logId #0", logId);
		}
		
		return false;
	}
    
	/** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Folder>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Log> getHandlerWith(JoinRowMapper... mappers) {
        return new JoinRowCallbackHandler(new LogRowMapper(), mappers);
    }
}
