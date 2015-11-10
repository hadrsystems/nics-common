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
import java.util.UUID;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.datalayer.Document;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.DocumentDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.DocumentRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;



public class DocumentDAOImpl extends GenericDAO implements DocumentDAO {

	private NamedParameterJdbcTemplate template;
	
	@Override
	public void initialize() {
		this.template = new NamedParameterJdbcTemplate(datasource);
	}
	
	@Override
	public Document addDocument(Document doc) {
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.DOCUMENT_ID, UUID.randomUUID());
		map.addValue(SADisplayConstants.DISPLAY_NAME, doc.getDisplayname());
		map.addValue(SADisplayConstants.DATASOURCE_ID, doc.getDatasourceid());
		map.addValue(SADisplayConstants.FILENAME, doc.getFilename());
		map.addValue(SADisplayConstants.FILETYPE, doc.getFiletype());
		map.addValue(SADisplayConstants.FOLDER_ID, doc.getFolderid());
		map.addValue(SADisplayConstants.GLOBAL_VIEW, doc.getGlobalview());
		map.addValue(SADisplayConstants.USERSESSION_ID, doc.getUsersessionid());
		map.addValue(SADisplayConstants.DESCRIPTION, doc.getDescription());
		map.addValue(SADisplayConstants.CREATED, doc.getCreated());
		
		QueryModel model = QueryManager.createQuery(SADisplayConstants.DOCUMENT_TABLE)
				.insertInto(new ArrayList<String>(map.getValues().keySet()))
				.returnValue("*");
		
		JoinRowCallbackHandler<Document> handler = getHandlerWith();
		template.query(model.toString(), map, handler);
		return handler.getSingleResult();
	}
	
	@Override
	public int addFeatureDocument(long featureId, String docId) {
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.FEATURE_ID, featureId);
		map.addValue(SADisplayConstants.DOCUMENT_ID, docId);
		
		QueryModel model = QueryManager.createQuery(SADisplayConstants.DOCUMENT_FEATURE_TABLE)
				.insertInto(new ArrayList<String>(map.getValues().keySet()))
				.returnValue(SADisplayConstants.DOCUMENT_FEATURE_ID);
		
		return template.queryForObject(model.toString(), map, Integer.class);
	}

	/** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Folder>
	 */
	private JoinRowCallbackHandler<Document> getHandlerWith(JoinRowMapper<?>... mappers) {
		return new JoinRowCallbackHandler<Document>(new DocumentRowMapper(), mappers);
	}
	
}
