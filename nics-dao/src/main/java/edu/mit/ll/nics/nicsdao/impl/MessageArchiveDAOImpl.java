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

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.MessageArchive;
import edu.mit.ll.nics.common.messages.sadisplay.SADisplayMessageType;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.MessageArchiveDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.MessageArchiveRowMapper;

public class MessageArchiveDAOImpl extends GenericDAO implements MessageArchiveDAO {

    private static final String FROM_TIME = "fromtime";
	private static final String TO_TIME = "totime";
	private static final String FROM_NUM = "fromnum";
	private static final String TO_NUM = "tonum";

	private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(MessageArchiveDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    public String getMapMessage(String topic){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.MESSAGE_ARCHIVE_TABLE)
    			.selectFromTableWhere(SADisplayConstants.MESSAGE)
    			.equals(SADisplayConstants.TOPIC)
    			.and()
    			.equals(SADisplayConstants.MESSAGE_TYPE)
    			.orderBy(SADisplayConstants.INSERTED_TIME_STAMP)
    			.desc()
    			.limit("1");
    	
    	try{
	    	return this.template.queryForObject(query.toString(), 
	    		new MapSqlParameterSource(SADisplayConstants.TOPIC, topic)
	    		.addValue(SADisplayConstants.MESSAGE_TYPE, SADisplayMessageType.MAP.toString()), 
	    		String.class);
    	}catch(Exception e){
    		log.info("Could not find Map Message with topic #0", topic);
    	}
    	return null;
    }
    
    public List<String> getChatMessages(String topic){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.MESSAGE_ARCHIVE_TABLE)
    			.selectFromTableWhere(SADisplayConstants.MESSAGE)
    			.equals(SADisplayConstants.TOPIC).and()
    			.equals(SADisplayConstants.MESSAGE_TYPE)
    			.orderBy(SADisplayConstants.INSERTED_TIME_STAMP).asc();
    	
    	return this.template.queryForList(queryModel.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.TOPIC, topic)
				.addValue(SADisplayConstants.MESSAGE_TYPE, SADisplayMessageType.CHAT.toString()), 
				String.class);
    	
    }
    
    public List<String> getMessages(Long seqtimeFrom, Integer seqnumFrom, Long seqtimeTo, Integer seqnumTo, String topic){
    	MapSqlParameterSource map = new MapSqlParameterSource();
    	map.addValue(SADisplayConstants.TOPIC, topic);
    	map.addValue(FROM_TIME, seqtimeFrom);
    	map.addValue(TO_TIME, seqtimeTo);
    	map.addValue(FROM_NUM, seqnumFrom);
    	
    	String query;
		// if seqnumTo is null, just get everything up until seqtimeTo
		if(seqnumTo == null){
			query = "select message from MessageArchive where topic=:topic and ((seqTime=:fromtime and " +
					"seqNum>:fromnum) or (seqTime>:fromtime and seqTime<:totime)) order by seqTime asc, seqNum asc";
		}
		else{
			query = "select message from MessageArchive where topic=:topic and ((seqTime=:fromtime and " +
					"seqNum>=:fromnum) or (seqTime>:fromtime and seqTime<=:totime) or " +
					"(seqTime=:totime and seqNum<:tonum)) order by seqTime asc, seqNum asc";
				
			map.addValue(TO_NUM, seqnumTo);
		}
		
		return this.template.queryForList(query, map, String.class);
    }
    
    
    /** getHandlerWith
     *  @param mappers - optional additional mappers
     *  @return JoinRowCallbackHandler<MessageArchive>
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private JoinRowCallbackHandler<MessageArchive> getHandlerWith(JoinRowMapper... mappers) {
    	return new JoinRowCallbackHandler(new MessageArchiveRowMapper(), mappers);
    }
}
