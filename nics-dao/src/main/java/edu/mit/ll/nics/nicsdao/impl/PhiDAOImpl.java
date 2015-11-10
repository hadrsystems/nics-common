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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.nics.common.entity.Image;
import edu.mit.ll.nics.common.entity.Location;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.Chat;
import edu.mit.ll.nics.common.entity.Form;
import edu.mit.ll.nics.common.entity.IncidentType;
import edu.mit.ll.nics.common.messages.sadisplay.SADisplayMessageType;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.ChatDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.MessageArchiveDAO;
import edu.mit.ll.nics.nicsdao.PhiDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.FormRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.IncidentTypeRowMapper;

// TODO:refactor rename / factor out the need for this class
public class PhiDAOImpl extends GenericDAO implements PhiDAO {

    private Logger log;

    private NamedParameterJdbcTemplate template;

    
    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(PhiDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }

    
    public int persistLocation(Location location) throws Exception {
    	
    	List<String> fields = new ArrayList<String>();
    	fields.add("id"); // null should make it choose default, which uses sequence
    	fields.add("user_id");
    	fields.add("device_id");
    	fields.add("location");
    	fields.add("accuracy");
    	fields.add("course");
    	fields.add("speed");
    	fields.add("time");
    	
    	MapSqlParameterSource params = new MapSqlParameterSource();
    	params.addValue("id", location.getId());
    	params.addValue("user_id", location.getUserid());
    	params.addValue("device_id", location.getDeviceId());
    	params.addValue("location", location.getLocation());
    	params.addValue("accuracy", location.getAccuracy());
    	params.addValue("course", location.getCourse());
    	params.addValue("speed", location.getSpeed());
    	params.addValue("time", location.getTime());
    	
    	QueryModel model = QueryManager.createQuery("location")
    			.insertInto(fields);
    	
    	int result = -1;
    	try {
    		result = this.template.update(model.toString(), params);
    	} catch(Exception e) {
    		
    		throw new Exception("Unhandled exception while persisting Image entity: " + e.getMessage());
    	}
    	
    	return result;
    }
    
    
    public int persistImage(Image image) throws Exception {
    	
    	List<String> fields = new ArrayList<String>();
    	fields.add("id"); // null should make it choose default, which uses sequence
    	fields.add("location_id");
    	fields.add("incident_id");
    	fields.add("url");
    	fields.add("fullpath");
    	
    	MapSqlParameterSource params = new MapSqlParameterSource();
    	params.addValue("location_id", image.getLocation().getId());
    	params.addValue("incident_id", image.getIncident().getIncidentid());
    	params.addValue("url", image.getUrl());
    	params.addValue("fullpath", image.getFullPath());
    	
    	QueryModel model = QueryManager.createQuery("image")
    			.insertInto(fields);
    	
    	int result = -1;
    	try {
    		result = this.template.update(model.toString(), params);
    	} catch(Exception e) {
    		
    		throw new Exception("Unhandled exception while persisting Image entity: " + e.getMessage());
    	}
    	
    	return result;
    }

}
