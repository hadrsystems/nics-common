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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.IncidentType;
import edu.mit.ll.nics.common.entity.SystemRole;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.SystemRoleDAO;
import edu.mit.ll.nics.nicsdao.mappers.IncidentTypeRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.SystemRoleRowMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class SystemRoleDAOImpl extends GenericDAO implements SystemRoleDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(SystemRoleDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    /** getSystemRoleFeatures
     * @param int systemroleid
   	 * @return String - return a list of comma delimited features for this role
   	 */
     public String getSystemRoleFeatures(int systemroleid, int workspaceid){
		String features = "";
		try{
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.SYSTEM_ROLE_WORKSPACE_TABLE)
						.selectFromTableWhere(SADisplayConstants.FEATURES)
						.equals(SADisplayConstants.SYSTEM_ROLE_ID)
						.and().equals(SADisplayConstants.WORKSPACE_ID);
			
			features = template.queryForObject(queryModel.toString(), 
	        				new MapSqlParameterSource(SADisplayConstants.SYSTEM_ROLE_ID, systemroleid)
							.addValue(SADisplayConstants.WORKSPACE_ID, workspaceid), String.class);
        
	    }catch(Exception e){
        	log.info("No role was found with systemroleid #0", systemroleid);
        }

		return features;
	}
    
	 /** getUserMessageTypes
	  * @param int roleid
	  * @return String - return a list of message types that this role has permission to send
	  */
	  public List<String> getSystemRoleMessageTypes(int roleid){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.MESSAGE_PERMISSIONS_TABLE)
					.selectFromTableWhere(SADisplayConstants.MESSAGE_TYPE).equals(SADisplayConstants.SYSTEM_ROLE_ID);
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put(SADisplayConstants.SYSTEM_ROLE_ID, roleid);
        
        return this.template.queryForList(queryModel.toString(), params, String.class);
	}
	  
     public int getSystemRoleId(String rolename){
    	 QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.SYSTEM_ROLE_TABLE)
    			 .selectFromTableWhere(SADisplayConstants.SYSTEM_ROLE_ID)
    			 .equals(SADisplayConstants.SYSTEM_ROLE_NAME);
    	 
    	 try{
	    	 return this.template.queryForInt(queryModel.toString(), 
	    			 new MapSqlParameterSource(SADisplayConstants.SYSTEM_ROLE_NAME, rolename));
    	 }catch(Exception e){
    		 log.info("Could not find systemrole id for role name #0", rolename);
    	 }
    	 return -1;
     }
    
	  /** getHandlerWith
	   *  @param mappers - optional additional mappers
	   *  @return JoinRowCallbackHandler<SystemRole>
	   */
	   @SuppressWarnings({ "unchecked", "rawtypes" })
	private JoinRowCallbackHandler<SystemRole> getHandlerWith(JoinRowMapper... mappers) {
		   return new JoinRowCallbackHandler(new SystemRoleRowMapper(), mappers);
	   }
}
