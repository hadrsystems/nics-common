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
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.common.entity.datalayer.Datalayer;
import edu.mit.ll.nics.common.entity.datalayer.Datalayerfolder;
import edu.mit.ll.nics.common.entity.datalayer.Datalayersource;
import edu.mit.ll.nics.common.entity.datalayer.Datasource;
import edu.mit.ll.nics.common.entity.datalayer.Datasourcetype;
import edu.mit.ll.nics.nicsdao.DatalayerDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.DatalayerRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatalayerfolderRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatalayersourceRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatasourceRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatasourcetypeRowMapper;

public class DatalayerDAOImpl extends GenericDAO implements DatalayerDAO {

	private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
        this.template = new NamedParameterJdbcTemplate(datasource);
    }
    
     /** getDatalayerFolders
	 *  @param folderid - String - id of folder holding datalayers
	 *  @return JSONArray - array of objects representing a datalayer.
	 *  Each object has information needed by the UI to popuplate the folder
	 */
	public List<Datalayer> getDatalayerFolders(String folderid){
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE).selectAllFromTable()
				.join(SADisplayConstants.DATALAYER_FOLDER_TABLE).using(SADisplayConstants.DATALAYER_ID)
				.join(SADisplayConstants.DATALAYER_SOURCE_TABLE).using(SADisplayConstants.DATALAYER_SOURCE_ID)
				.join(SADisplayConstants.DATASOURCE_TABLE).using(SADisplayConstants.DATASOURCE_ID)
				.join(SADisplayConstants.DATASOURCE_TYPE_TABLE).using(SADisplayConstants.DATASOURCE_TYPE_ID)
				.where().equals(SADisplayConstants.FOLDER_ID).orderBy(SADisplayConstants.DATALAYERFOLDER_INDEX);
		
		JoinRowCallbackHandler<Datalayer> handler = getHandlerWith(new DatalayersourceRowMapper().attachAdditionalMapper(
				new DatasourceRowMapper().attachAdditionalMapper(new DatasourcetypeRowMapper())
		));
		
		template.query(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.FOLDER_ID, folderid), handler);
    	return handler.getResults();
	}
	
	public Datalayerfolder getDatalayerfolder(String datalayerid, String folderid){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
		.selectAllFromTableWhere().equals(SADisplayConstants.DATALAYER_ID, datalayerid).and()
		.equals(SADisplayConstants.FOLDER_ID, folderid);
		
		JoinRowCallbackHandler<Datalayerfolder> handler = getDatalayerfolderHandlerWith();
	
		this.template.query(query.toString(),
				new MapSqlParameterSource(SADisplayConstants.DATALAYER_ID, datalayerid)
				.addValue(SADisplayConstants.FOLDER_ID, folderid),
				handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			log.info("Error retrieving datalayer with datatlayerid #0 and folderid #1", datalayerid, folderid);
		}
		return null;
	}
	
	public List<Datasource> getDatasources(String type){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATASOURCE_TABLE)
		.selectAllFromTable()
		.join(SADisplayConstants.DATASOURCE_TYPE_TABLE).using(SADisplayConstants.DATASOURCE_TYPE_ID)
		.where().equals(SADisplayConstants.DATASOURCE_TYPE_NAME);
	
		JoinRowCallbackHandler<Datasource> handler = getDatasourceHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.DATASOURCE_TYPE_NAME, type.toLowerCase()), handler);
		
		return handler.getResults();
	}
	
	@Override
	public Datasource getDatasource(String datasourceId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATASOURCE_TABLE)
		.selectAllFromTable().where().equals(SADisplayConstants.DATASOURCE_ID, datasourceId);
	
		JoinRowCallbackHandler<Datasource> handler = getDatasourceHandlerWith();
		
		this.template.query(query.toString(), query.getParameters(), handler);
		
		return handler.getSingleResult();
	}
	
	public Datalayer reloadDatalayer(String datalayerid){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE)
				  .selectAllFromTable()
				  .join(SADisplayConstants.DATALAYER_SOURCE_TABLE).using(SADisplayConstants.DATALAYER_SOURCE_ID)
				  .join(SADisplayConstants.DATASOURCE_TABLE).using(SADisplayConstants.DATASOURCE_ID)
				  .join(SADisplayConstants.DATASOURCE_TYPE_TABLE).using(SADisplayConstants.DATASOURCE_TYPE_ID)
				  .where().equals(SADisplayConstants.DATALAYER_ID);
		
		JoinRowCallbackHandler<Datalayer> handler = getHandlerWith(new DatalayersourceRowMapper()
			.attachAdditionalMapper(new DatasourceRowMapper().attachAdditionalMapper(new DatasourcetypeRowMapper())));
		
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.DATALAYER_ID, datalayerid), handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			log.info("Could not retrieve datalayer with id #0", datalayerid);
		}
		return null;
	}
	
	public int getDatasourceTypeId(String datasourcetype){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATASOURCE_TYPE_TABLE)
		  .selectAllFromTableWhere().equals(SADisplayConstants.TYPE_NAME);
		
		JoinRowCallbackHandler<Datasourcetype> handler = getDatasourceTypeHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.TYPE_NAME, datasourcetype.toLowerCase()), handler);
		
		try{
			return handler.getSingleResult().getDatasourcetypeid();
		}catch(Exception e){
			log.info("Error retrieving data source type for #0: #1", datasourcetype, e.getMessage());
		}
		return -1;
	}
	
	public String getDatasourceId(String internalurl){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATASOURCE_TABLE)
		.selectAllFromTableWhere().equals(SADisplayConstants.DATASOURCE_INTERNAL_URL);
		
		JoinRowCallbackHandler<Datasource> handler = getDatasourceHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.DATASOURCE_INTERNAL_URL, internalurl), handler);
		
		try{
			return handler.getSingleResult().getDatasourceid(); //Query for results and get the first one?
		}catch(Exception e){
			log.info("Error retrieving datasource for internal url #0", internalurl);
		}
		return null;
	}
	
	public String getDatalayersourceId(String layername){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATALAYER_SOURCE_TABLE)
		.selectAllFromTableWhere().equals(SADisplayConstants.LAYERNAME);
		
		JoinRowCallbackHandler<Datalayersource> handler = getDatalayersourceHandlerWith();
		
		this.template.query(query.toString(), new MapSqlParameterSource(SADisplayConstants.LAYERNAME, layername.trim()), handler);
		
		try{
			return handler.getResults().get(0).getDatalayersourceid();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	public String getUnofficialDatalayerId(String collabroom, String folderId){
		String query = "select datalayerid from datalayer join collabroom on name=displayname join datalayerfolder using(datalayerid) where folderid=:" +
				SADisplayConstants.FOLDER_ID + " and incidentid=0 and name=:" + SADisplayConstants.COLLAB_ROOM_NAME;
		
		try{
			return this.template.queryForObject(query.toString(), 
					new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_NAME, collabroom)
					.addValue(SADisplayConstants.FOLDER_ID, folderId), String.class);
		}catch(Exception e){
			log.info("Could not retrieve matching datalayer for collabroom #0", collabroom);
		}
		return "";
	}
	
	public List<String> getAvailableStyles(){
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_SOURCE_TABLE)
				.selectFromTableWhere(SADisplayConstants.ATTRIBUTES)
				.like(SADisplayConstants.ATTRIBUTES).value("'%availableStyle%'");
		
		return this.template.queryForList(queryModel.toString(), new MapSqlParameterSource(), String.class);
	}
	
	public int getNextDatalayerFolderId() {
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.HIBERNATE_SEQUENCE_TABLE).selectNextVal();
    	try{
    		return this.template.queryForInt(queryModel.toString(), new HashMap<String, Object>());
    	}catch(Exception e){
    		log.info("Error retrieving the next datalayer folder id: #0", e.getMessage());
    	}
    	return -1;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String insertDataSource(Datasource source){
		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.DATASOURCE_ID, UUID.randomUUID());
		map.addValue(SADisplayConstants.INTERNAL_URL, source.getInternalurl());
		map.addValue(SADisplayConstants.DATASOURCE_TYPE_ID, source.getDatasourcetypeid());
		map.addValue(SADisplayConstants.DISPLAY_NAME, source.getDisplayname());
		map.addValue(SADisplayConstants.EXTERNAL_URL, source.getExternalurl());
			
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATASOURCE_TABLE)
			.insertInto(new ArrayList(map.getValues().keySet()))
			.returnValue(SADisplayConstants.DATASOURCE_ID);
		
		return this.template.queryForObject(queryModel.toString(), map, String.class);
	}
	

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String insertDataLayer(String dataSourceId, Datalayer datalayer) {
		
		Datalayersource source = datalayer.getDatalayersource();
		MapSqlParameterSource srcMap = new MapSqlParameterSource();
		srcMap.addValue(SADisplayConstants.DATALAYER_SOURCE_ID, UUID.randomUUID());
		srcMap.addValue(SADisplayConstants.DATASOURCE_ID, dataSourceId);
		srcMap.addValue(SADisplayConstants.REFRESH_RATE, source.getRefreshrate());
		srcMap.addValue(SADisplayConstants.IMAGE_FORMAT, source.getImageformat());
		srcMap.addValue(SADisplayConstants.LAYERNAME, source.getLayername());
		srcMap.addValue(SADisplayConstants.CREATED, source.getCreated());
		srcMap.addValue(SADisplayConstants.USERSESSION_ID, datalayer.getUsersessionid());
		
		QueryModel srcQueryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_SOURCE_TABLE)
				.insertInto(new ArrayList(srcMap.getValues().keySet()))
				.returnValue(SADisplayConstants.DATALAYER_SOURCE_ID);
			
		String datalayersourceid = this.template.queryForObject(srcQueryModel.toString(), srcMap, String.class);

		
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.DATALAYER_ID, UUID.randomUUID());
		map.addValue(SADisplayConstants.DATALAYER_SOURCE_ID, datalayersourceid);
		map.addValue(SADisplayConstants.BASE_LAYER, datalayer.getBaselayer());
		map.addValue(SADisplayConstants.DISPLAY_NAME, datalayer.getDisplayname());
		map.addValue(SADisplayConstants.GLOBAL_VIEW, true); //TODO: add to datalayer model?
		map.addValue(SADisplayConstants.CREATED, datalayer.getCreated());
		map.addValue(SADisplayConstants.USERSESSION_ID, datalayer.getUsersessionid());
			
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE)
				.insertInto(new ArrayList(map.getValues().keySet()))
				.returnValue(SADisplayConstants.DATALAYER_ID);
			
		return this.template.queryForObject(queryModel.toString(), map, String.class);
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public int insertDataLayerFolder(String folderId, String datalayerId, int folderIndex) {
		MapSqlParameterSource map = new MapSqlParameterSource();
		map.addValue(SADisplayConstants.DATALAYER_FOLDER_ID, this.getNextDatalayerFolderId());
		map.addValue(SADisplayConstants.FOLDER_ID, folderId);
		map.addValue(SADisplayConstants.DATALAYER_ID, datalayerId);
		map.addValue(SADisplayConstants.INDEX, folderIndex);
			
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
				.insertInto(new ArrayList(map.getValues().keySet()))
				.returnValue(SADisplayConstants.DATALAYER_FOLDER_ID);
		
		return this.template.queryForObject(queryModel.toString(), map, Integer.class);
	}
	
	 /** getHandlerWith
		 *  @param mappers - optional additional mappers
		 *  @return JoinRowCallbackHandler<Datalayer>
		 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Datalayer> getHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new DatalayerRowMapper(), mappers);
    }
    
    /** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Datalayer>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Datasource> getDatasourceHandlerWith(JoinRowMapper... mappers) {
		 return new JoinRowCallbackHandler(new DatasourceRowMapper(), mappers);
	}
	
	 /** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Datalayer>
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Datalayersource> getDatalayersourceHandlerWith(JoinRowMapper... mappers) {
		 return new JoinRowCallbackHandler(new DatalayersourceRowMapper(), mappers);
		}
	
	 /** getHandlerWith
		 *  @param mappers - optional additional mappers
		 *  @return JoinRowCallbackHandler<Datalayer>
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private JoinRowCallbackHandler<Datasourcetype> getDatasourceTypeHandlerWith(JoinRowMapper... mappers) {
			 return new JoinRowCallbackHandler(new DatasourcetypeRowMapper(), mappers);
		}
		
		 /** getHandlerWith
		 *  @param mappers - optional additional mappers
		 *  @return JoinRowCallbackHandler<Datalayer>
		 */
		@SuppressWarnings({ "rawtypes", "unchecked" })
		private JoinRowCallbackHandler<Datalayerfolder> getDatalayerfolderHandlerWith(JoinRowMapper... mappers) {
			 return new JoinRowCallbackHandler(new DatalayerfolderRowMapper(), mappers);
		}

}
