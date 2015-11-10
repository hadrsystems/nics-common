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

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.common.entity.Chat;
import edu.mit.ll.nics.common.entity.UserOrg;
import edu.mit.ll.nics.nicsdao.ChatDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.ChatRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.OrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserOrgRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserRowMapper;
import edu.mit.ll.nics.nicsdao.query.QueryConstraint.OrderBy;
import edu.mit.ll.nics.nicsdao.query.QueryConstraint.OrderByType;
import edu.mit.ll.nics.nicsdao.query.QueryConstraint.ResultSetPage;
import edu.mit.ll.nics.nicsdao.query.QueryConstraint.UTCRange;

public class ChatDAOImpl extends GenericDAO implements ChatDAO {

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
        this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
    public List<Chat> getChatMessages(int collabroomId, UTCRange dateRange, OrderBy orderBy, ResultSetPage pageRange){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CHAT_TABLE)
    		.selectAllFromTable()
    		.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
    		.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
    		.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
    		.where()
    		.equals(SADisplayConstants.COLLAB_ROOM_ID, collabroomId);

    	processTimeRange(queryModel, dateRange);
    	processOrderBy(queryModel, orderBy);
    	processPageRange(queryModel, pageRange);
    	
    	JoinRowMapper<UserOrg> userOrgMapper = new UserOrgRowMapper()
    		.attachAdditionalMapper(new UserRowMapper()) //skip password fields
    		.attachAdditionalMapper(new OrgRowMapper());	
    	JoinRowCallbackHandler<Chat> handler = getHandler(userOrgMapper);
    	this.template.query(queryModel.toString(), queryModel.getParameters(), handler);
    	return handler.getResults();
    }

	@Override
	public Chat getChatMessage(int chatId) {
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.CHAT_TABLE)
			.selectAllFromTable()
			.join(SADisplayConstants.USER_ORG_TABLE).using(SADisplayConstants.USER_ORG_ID)
			.join(SADisplayConstants.USER_ESCAPED).using(SADisplayConstants.USER_ID)
			.join(SADisplayConstants.ORG_TABLE).using(SADisplayConstants.ORG_ID)
			.where()
			.equals(SADisplayConstants.CHAT_ID, chatId);

		JoinRowMapper<UserOrg> userOrgMapper = new UserOrgRowMapper()
			.attachAdditionalMapper(new UserRowMapper()) //skip password fields
			.attachAdditionalMapper(new OrgRowMapper());	
		JoinRowCallbackHandler<Chat> handler = getHandler(userOrgMapper);
		this.template.query(queryModel.toString(), queryModel.getParameters(), handler);
		return handler.getSingleResult();
	}
    
	public int addChat(Chat chat) throws Exception {
    	List<String> fields = Arrays.asList(
    		SADisplayConstants.CHAT_ID,	SADisplayConstants.COLLAB_ROOM_ID, SADisplayConstants.USER_ORG_ID,
    		SADisplayConstants.CREATED, SADisplayConstants.SEQ_NUM, SADisplayConstants.MESSAGE);
    		
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.CHAT_TABLE).insertInto(fields);
    	
    	//generate a chatid if not set
    	if (chat.getChatid() <= 0) {
    		QueryModel idModel = QueryManager.createQuery("chat_seq").selectNextVal();
    		int id = this.template.queryForObject(idModel.toString(), new MapSqlParameterSource(), Integer.class);
    		chat.setChatid(id);
    	}
    	
    	try {
    		this.template.update(model.toString(), new BeanPropertySqlParameterSource(chat));
    		return chat.getChatid();
    	} catch(Exception e) {
    		throw new Exception("Unhandled exception while persisting Chat entity:", e);
    	}
    }
    
    private void processPageRange(QueryModel queryModel, ResultSetPage pageRange) {
    	if (pageRange != null) {
    		if (pageRange.offset != null) {
        		queryModel.offset(pageRange.offset.toString());
    		}
    		if (pageRange.limit != null) {    			
        		queryModel.limit(pageRange.limit.toString());
    		}
    	}
	}

	private void processOrderBy(QueryModel queryModel, OrderBy orderBy) {
		if (orderBy != null) {
			if (orderBy.colName != null && orderBy.type != null) {
				queryModel.orderBy("chat." + orderBy.colName);
				
				if (OrderByType.DESC.equals(orderBy.type)) {
					queryModel.desc();
				} else {
					queryModel.asc();
				}
			}
			
		}
	}

	private void processTimeRange(QueryModel queryModel, UTCRange dateRange) {
		if (dateRange != null) {
			if (dateRange.colName != null) {
				String fullColumn = "chat." + dateRange.colName;
				if (dateRange.from != null) {
					queryModel.and().greaterThanOrEquals(fullColumn, new Timestamp(dateRange.from));
				}
				if (dateRange.to != null) {
					queryModel.and().lessThanOrEquals(fullColumn, new Timestamp(dateRange.to));
				}
			}
		}
		
	}
	
	private JoinRowCallbackHandler<Chat> getHandler(JoinRowMapper<?>... additionalMappers) {
		return new JoinRowCallbackHandler<Chat>(new ChatRowMapper(), additionalMappers);
	}

}
