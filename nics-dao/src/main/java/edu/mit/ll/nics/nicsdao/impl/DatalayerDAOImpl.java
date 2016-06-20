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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.common.entity.datalayer.Datalayer;
import edu.mit.ll.nics.common.entity.datalayer.Datalayerfolder;
import edu.mit.ll.nics.common.entity.datalayer.Datalayersource;
import edu.mit.ll.nics.common.entity.datalayer.Datasource;
import edu.mit.ll.nics.common.entity.datalayer.Datasourcetype;
import edu.mit.ll.nics.common.entity.datalayer.Folder;
import edu.mit.ll.nics.nicsdao.DatalayerDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.DatalayerRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatalayerfolderRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatalayersourceRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatasourceRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DatasourcetypeRowMapper;

public class DatalayerDAOImpl extends GenericDAO implements DatalayerDAO {

	private Logger log = LoggerFactory.getLogger(DatalayerDAOImpl.class);

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
	public List<Datalayerfolder> getDatalayerFolders(String folderid){
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE).selectAllFromTable()
				.join(SADisplayConstants.DATALAYER_TABLE).using(SADisplayConstants.DATALAYER_ID)
				.join(SADisplayConstants.DATALAYER_SOURCE_TABLE).using(SADisplayConstants.DATALAYER_SOURCE_ID)
				.join(SADisplayConstants.DATASOURCE_TABLE).using(SADisplayConstants.DATASOURCE_ID)
				.join(SADisplayConstants.DATASOURCE_TYPE_TABLE).using(SADisplayConstants.DATASOURCE_TYPE_ID)
				.where().equals(SADisplayConstants.FOLDER_ID).orderBy(SADisplayConstants.DATALAYERFOLDER_INDEX);
		
		JoinRowCallbackHandler<Datalayerfolder> handler = getDatalayerfolderHandlerWith(
				new DatalayerRowMapper().attachAdditionalMapper(
						new DatalayersourceRowMapper().attachAdditionalMapper(
								new DatasourceRowMapper().attachAdditionalMapper(
										new DatasourcetypeRowMapper()
									)
							)
					)
		);
		
		template.query(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.FOLDER_ID, folderid), handler);
		return handler.getResults();
	}
	
	public Datalayerfolder getDatalayerfolder(String datalayerid, String folderid){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
				.selectAllFromTable()
				.join(SADisplayConstants.DATALAYER_TABLE).using(SADisplayConstants.DATALAYER_ID)
				.join(SADisplayConstants.DATALAYER_SOURCE_TABLE).using(SADisplayConstants.DATALAYER_SOURCE_ID)
				.join(SADisplayConstants.DATASOURCE_TABLE).using(SADisplayConstants.DATASOURCE_ID)
				.join(SADisplayConstants.DATASOURCE_TYPE_TABLE).using(SADisplayConstants.DATASOURCE_TYPE_ID)
				.where().equals(SADisplayConstants.FOLDER_ID, folderid)
				.and().equals(SADisplayConstants.DATALAYER_ID, datalayerid)
				.orderBy(SADisplayConstants.DATALAYERFOLDER_INDEX);
		
		JoinRowCallbackHandler<Datalayerfolder> handler = getDatalayerfolderHandlerWith(
				new DatalayerRowMapper().attachAdditionalMapper(
						new DatalayersourceRowMapper().attachAdditionalMapper(
								new DatasourceRowMapper().attachAdditionalMapper(
										new DatasourcetypeRowMapper()
									)
							)
					)
		);
	
		this.template.query(query.toString(), query.getParameters(), handler);
		return handler.getSingleResult();
	}
	
	public Datalayerfolder getDatalayerfolder(int datalayerfolderid){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
		.selectAllFromTableWhere().equals(SADisplayConstants.DATALAYER_FOLDER_ID, datalayerfolderid);
		
		JoinRowCallbackHandler<Datalayerfolder> handler = getDatalayerfolderHandlerWith();
	
		this.template.query(query.toString(), query.getParameters(), handler);
		return handler.getSingleResult();
	}
	
	public List<Map<String,Object>> getTrackingLayers(int workspaceId, boolean secured){
		//select displayname,layername from datalayer join datalayersource using(datalayersourceid) join datalayerfolder using(datalayerid) where folderid=(select folderid from rootfolder where tabname='Tracking' and workspaceid=1);
		StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.DATALAYER_DISPLAYNAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.LAYERNAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.INTERNAL_URL);
    	
    	if(secured){
    		fields.append(QueryBuilder.COMMA);
    		fields.append(SADisplayConstants.DATASOURCE_ID);
    	}
    	
    	QueryModel folderQueryModel = QueryManager.createQuery(SADisplayConstants.ROOT_FOLDER_TABLE)
    			.selectFromTable(SADisplayConstants.FOLDER_ID)
    			.where().equals(SADisplayConstants.TABNAME)
    			.and().equals(SADisplayConstants.WORKSPACE_ID);
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE)
   			 	 .selectFromTable(fields.toString())
				 .join(SADisplayConstants.DATALAYER_SOURCE_TABLE).using(SADisplayConstants.DATALAYER_SOURCE_ID)
				 .join(SADisplayConstants.DATALAYER_FOLDER_TABLE).using(SADisplayConstants.DATALAYER_ID)
				 .join(SADisplayConstants.DATASOURCE_TABLE).using(SADisplayConstants.DATASOURCE_ID)
    			 .where().equalsInnerSelect(SADisplayConstants.FOLDER_ID, folderQueryModel.toString());
				 
    	//return the datasourceid if there is a username associated with the datasource
    	if(secured){
    		queryModel = queryModel.and().isNotNull(SADisplayConstants.USER_NAME);
    	}else{
    		queryModel = queryModel.and().isNull(SADisplayConstants.USER_NAME);
    	}
				 
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.WORKSPACE_ID, workspaceId)
    		.addValue(SADisplayConstants.TABNAME, SADisplayConstants.TRACKING);
		
		return template.queryForList(queryModel.toString(), map);
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
			Datasource ds = handler.getSingleResult();
			if (ds != null) {
				return ds.getDatasourceid();
			}
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
		
		if(source.getUsername() != null && source.getPassword() != null){
			map.addValue(SADisplayConstants.USER_NAME, source.getUsername());
			map.addValue(SADisplayConstants.PASSWORD, source.getPassword());
		}
			
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
		srcMap.addValue(SADisplayConstants.ATTRIBUTES, source.getAttributes());
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
		map.addValue(SADisplayConstants.LEGEND, datalayer.getLegend());
			
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE)
				.insertInto(new ArrayList(map.getValues().keySet()))
				.returnValue(SADisplayConstants.DATALAYER_ID);
			
		return this.template.queryForObject(queryModel.toString(), map, String.class);
	}
	
	public List<Map<String, Object>> getAuthentication(String datasourceid) {
   		StringBuffer fields = new StringBuffer();
    	fields.append(SADisplayConstants.USER_NAME);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.PASSWORD);
    	fields.append(QueryBuilder.COMMA);
    	fields.append(SADisplayConstants.INTERNAL_URL);
   		
       	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATASOURCE_TABLE)
   				.selectFromTable(fields.toString())
   				.where().equals(SADisplayConstants.DATASOURCE_ID);
   		
   		return template.queryForList(queryModel.toString(), 
				new MapSqlParameterSource(SADisplayConstants.DATASOURCE_ID, datasourceid));
   	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean removeDataLayer(String dataSourceId) {
	
		int removed = -1;
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.DATALAYER_ID);
		
		removed = this.template.update(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.DATALAYER_ID,dataSourceId));
		
		if(removed != 1){
			return false;
		}
		
		queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE)
				.deleteFromTableWhere().equals(SADisplayConstants.DATALAYER_ID);
		
		removed = this.template.update(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.DATALAYER_ID,dataSourceId));
		
		if(removed != 1){
			return false;
		}
		
		return true;
	}
	
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Datalayer updateDataLayer(Datalayer datalayer) {

		try{
		
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE)
					.update().equals(SADisplayConstants.DISPLAY_NAME).where()
					.equals(SADisplayConstants.DATALAYER_ID).returnValue("*");
	
			MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.DATALAYER_ID,datalayer.getDatalayerid());
			map.addValue(SADisplayConstants.DISPLAY_NAME, datalayer.getDisplayname());
		
			JoinRowCallbackHandler<Datalayer> handler = getHandlerWith();
			
			this.template.query(queryModel.toString(), map, handler);
			
			return handler.getSingleResult();
		
		}
		catch(Exception e){
			log.info("Failed to update datalayer #0", datalayer.getDisplayname());
		}
		
		return null;
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
	
	@Override
	public Datalayerfolder updateDatalayerfolder(Datalayerfolder dlFolder) {
		try{
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
					.update().equals(SADisplayConstants.DATALAYER_ID)
					.comma().equals(SADisplayConstants.FOLDER_ID)
					.comma().equals(SADisplayConstants.INDEX)
					.where().equals(SADisplayConstants.DATALAYER_FOLDER_ID).returnValue("*");

			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue(SADisplayConstants.DATALAYER_FOLDER_ID, dlFolder.getDatalayerfolderid());
			map.addValue(SADisplayConstants.DATALAYER_ID, dlFolder.getDatalayerid());
			map.addValue(SADisplayConstants.FOLDER_ID, dlFolder.getFolderid());
			map.addValue(SADisplayConstants.INDEX, dlFolder.getIndex());
			
			JoinRowCallbackHandler<Datalayerfolder> handler = getDatalayerfolderHandlerWith();
			
			this.template.query(queryModel.toString(), map, handler);
			
			return handler.getSingleResult();
		}
		catch(Exception e){
			log.info("Failed to update folder #0", dlFolder.getDatalayerfolderid());
		}
		
		return null;
	}
	
	@Override
	public int getNextDatalayerFolderIndex(String folderid){
		int result = 0;
		
		QueryModel indexQuery = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
				.selectMaxFromTable(SADisplayConstants.INDEX)
				.where().equals(SADisplayConstants.FOLDER_ID, folderid);
		
		try{
			int index = this.template.queryForObject(indexQuery.toString(),
					indexQuery.getParameters(), Integer.class);
			result = index + 1;
		}catch(Exception e){
			log.info("Could not find next folder index for folderid #0", folderid);
		}
		return result;
    }
	
	
	@Override
	public void decrementIndexes(String parentFolderId, int index) {
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
				.update(SADisplayConstants.INDEX).value("= index - 1")
				.where().equals(SADisplayConstants.FOLDER_ID, parentFolderId)
				.and().greaterThanOrEquals(SADisplayConstants.INDEX, index);
		this.template.update(queryModel.toString(), queryModel.getParameters());
	}

	@Override
	public void incrementIndexes(String parentFolderId, int index) {
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.DATALAYER_FOLDER_TABLE)
				.update(SADisplayConstants.INDEX).value("= index + 1")
				.where().equals(SADisplayConstants.FOLDER_ID, parentFolderId)
				.and().greaterThanOrEquals(SADisplayConstants.INDEX, index);
		this.template.update(queryModel.toString(), queryModel.getParameters());
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
