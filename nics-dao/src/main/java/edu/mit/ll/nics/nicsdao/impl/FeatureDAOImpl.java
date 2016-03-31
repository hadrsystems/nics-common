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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.mit.ll.dao.QueryBuilder;
import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.CollabroomFeature;
import edu.mit.ll.nics.common.entity.Feature;
import edu.mit.ll.nics.common.entity.UserFeature;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.nicsdao.FeatureDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.CollabRoomFeatureRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.DocumentRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.FeatureRowMapper;
import edu.mit.ll.nics.nicsdao.query.QueryConstraint.UTCRange;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;



public class FeatureDAOImpl extends GenericDAO implements FeatureDAO {

    private Logger log;

   
    private NamedParameterJdbcTemplate template;
        
    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(FeatureDAOImpl.class);
    	if(datasource != null) {
    		this.template = new NamedParameterJdbcTemplate(datasource);
    	}
    }
    
     public List<Feature> getFeatureState(int collabroomId, UTCRange dateRange, int geoType){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FEATURE)
    			.selectAllFromFeatureTable(geoType)
    			.join(SADisplayConstants.COLLABROOM_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
    			.left().join(SADisplayConstants.DOCUMENT_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
    			.left().join(SADisplayConstants.DOCUMENT_TABLE).using(SADisplayConstants.DOCUMENT_ID)
    			.where().equals(SADisplayConstants.COLLAB_ROOM_ID, collabroomId).and()
    			.equals(SADisplayConstants.DELETED);
    	
    	MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.COLLAB_ROOM_ID, collabroomId)
		.addValue(SADisplayConstants.DELETED, false);
    	
    	if(dateRange != null && dateRange.colName != null && dateRange.from != null){
    		query.and().greaterThanOrEquals(dateRange.colName, new Timestamp(dateRange.from));
    		
    		if(dateRange.colName.equals("seqtime")){
    			map.addValue(dateRange.colName, dateRange.from);
    		}
    		else{
    			map.addValue(dateRange.colName, new Timestamp(dateRange.from));
    		}
    		
    	}
    	
    	JoinRowCallbackHandler<Feature> handler = getHandlerWith(new DocumentRowMapper());
    	
    	this.template.query(query.toString(), map, handler);
    	
    	return handler.getResults();
    }
     
     public List<Long> getDeletedFeatures(int collabroomId, UTCRange dateRange){
     	QueryModel query = QueryManager.createQuery(SADisplayConstants.FEATURE)
     			.selectFromTable(SADisplayConstants.FEATURE_ID)
     			.join(SADisplayConstants.COLLABROOM_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
     			.where().equals(SADisplayConstants.COLLAB_ROOM_ID, collabroomId).and()
     			.equals(SADisplayConstants.DELETED);
     	
     	MapSqlParameterSource map = new MapSqlParameterSource(
     			SADisplayConstants.COLLAB_ROOM_ID, collabroomId)
 				.addValue(SADisplayConstants.DELETED, true);
     	
     	if(dateRange != null && dateRange.colName != null && dateRange.from != null){
     		query.and().greaterThanOrEquals(dateRange.colName, new Timestamp(dateRange.from));
     		
     		if(dateRange.colName.equals("seqtime")){
    			map.addValue(dateRange.colName, dateRange.from);
    		}
    		else{
    			map.addValue(dateRange.colName, new Timestamp(dateRange.from));
    		}
     	}
     	
     	System.out.println("Query: " + query.toString());
     	
     	return this.template.queryForList(query.toString(), map, Long.class);
     }
    
    public long addFeature(JSONObject feature, List<String> fields, int geoType) throws Exception{
    	try {
    		
    		boolean transform = geoType != QueryBuilder.SRS_PROJECTION;
    		
    		//Update the feature
    		if(!fields.contains(SADisplayConstants.LAST_UPDATE)){
    			fields.add(SADisplayConstants.LAST_UPDATE);
    			feature.put(SADisplayConstants.LAST_UPDATE, new Date());
    		}
    	
    		QueryModel query = QueryManager.createQuery(SADisplayConstants.FEATURE)
    				.insertIntoFeatureWithGeo(fields, transform, SADisplayConstants.FEATURE_ID).returnValue(SADisplayConstants.FEATURE_ID);
    		
    		MapSqlParameterSource valueMap = new MapSqlParameterSource();
    		
    		for(Iterator<String> itr = fields.iterator(); itr.hasNext();){
    			String field = itr.next();
    			valueMap.addValue(field, feature.get(field));
    		}
    		
    		//Has a geometry field so need to specify the SRS
    		valueMap.addValue(QueryBuilder.SRS, QueryBuilder.SRS_PROJECTION);
    		if(transform){
    			valueMap.addValue(QueryBuilder.TRANS, geoType);
    		}
    		
    		
    		return this.template.queryForObject(query.toString(), valueMap, Long.class);
    	} catch(Exception e) {
    		throw new Exception("Unhandled exception while persisting a new Feature entity: " + e.getMessage());
    	}
    }
    
    public void updateFeature(long featureId, JSONObject properties) throws Exception{
    	StringBuffer model = new StringBuffer();
    	model.append("UPDATE feature set ");
    	
    	MapSqlParameterSource valueMap = new MapSqlParameterSource();
    	valueMap.addValue("featureid", featureId);
    	
    	boolean start = true;
    	for(Iterator<String> itr = properties.keys(); itr.hasNext();){
    		if(start){
    			start = false;
    		}else{
    			model.append(",");
    		}
    		String key = itr.next();
    		model.append(key);
    		model.append("=");
    		if(key.equalsIgnoreCase("geometry")){
    			model.append("ST_GeomFromText(");
    			model.append(QueryBuilder.COLON);
				model.append(key);
				model.append(QueryBuilder.COMMA);
				model.append(QueryBuilder.COLON);
				model.append("srs");
				model.append(QueryBuilder.CLOSE);
				
				valueMap.addValue("srs", 3857);
    		}else{
    			model.append(":");
        		model.append(key);
    		}
    		valueMap.addValue(key, properties.get(key));
    	}
    	model.append(" where featureid=:featureid");
    	
    	try{
    		this.template.update(model.toString(), valueMap);
    	}catch(Exception e){
    		throw new Exception("Unhandled exception while persisting Feature update:", e);
    	}
    }
    
   public void addCollabroomFeature(CollabroomFeature collabroomFeature) throws Exception{
    	try{
	    	List<String> fields = Arrays.asList(
	        		SADisplayConstants.COLLAB_ROOM_ID, SADisplayConstants.FEATURE_ID);
	
	    	QueryModel model = QueryManager.createQuery(SADisplayConstants.FEATURE_COLLABROOM).insertInto(fields);
	    	
	    	this.template.update(model.toString(), new BeanPropertySqlParameterSource(collabroomFeature));
    	}catch(Exception e) {
    		throw new Exception("Unhandled exception while persisting Collabroom Feature entity: " + e.getMessage());
    	}
    }
    
    public void addUserFeature(UserFeature userFeature) throws Exception{
    	try{
	    	List<String> fields = Arrays.asList(
	        		SADisplayConstants.USER_ID, SADisplayConstants.FEATURE_ID);
	
	    	QueryModel model = QueryManager.createQuery(SADisplayConstants.FEATURE_USER).insertInto(fields);
	    	
	    	this.template.update(model.toString(), new BeanPropertySqlParameterSource(userFeature));
    	}catch(Exception e) {
    		throw new Exception("Unhandled exception while persisting User Feature entity: " + e.getMessage());
    	}
    }
    
    public Feature getFeature(long featureId){
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.FEATURE)
    			.selectAllFromTable()
    			.left().join(SADisplayConstants.DOCUMENT_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
    			.left().join(SADisplayConstants.DOCUMENT_TABLE).using(SADisplayConstants.DOCUMENT_ID)
    			.where()
    			.equals(SADisplayConstants.FEATURE_ID);
    	
    	JoinRowCallbackHandler<Feature> handler = getHandlerWith(new DocumentRowMapper());
    	
    	this.template.query(model.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.FEATURE_ID, featureId), 
    			handler);
    	
    	return handler.getSingleResult();
    }
    
    public List<Feature> getUserFeatureState(int userId){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FEATURE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.USER_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
    			.left().join(SADisplayConstants.DOCUMENT_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
    			.left().join(SADisplayConstants.DOCUMENT_TABLE).using(SADisplayConstants.DOCUMENT_ID)
    			.where().equals(SADisplayConstants.USER_ID, userId)
    			.and().equals(SADisplayConstants.DELETED, false);
    			
    	
    	JoinRowCallbackHandler<Feature> handler = getHandlerWith(new DocumentRowMapper());
    	
    	this.template.query(query.toString(), query.getParameters(), handler);
    	
    	return handler.getResults();
    }
    
    /**
     * Get Features by the specified FeatureIDs list
     * 
     * @param featureIds
     * @return
     */
    public List<Feature> getFeatures(List<String> featureIds) {
    	List<Feature> features = new ArrayList<Feature>();
    	
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.FEATURES)
    			.selectAllFromTable()
    			.left().join(SADisplayConstants.DOCUMENT_FEATURE_TABLE).using(SADisplayConstants.FEATURE_ID)
    			.left().join(SADisplayConstants.DOCUMENT_TABLE).using(SADisplayConstants.DOCUMENT_ID)
    			.where()
    			.inAsString(SADisplayConstants.FEATURE_ID, featureIds);
    	
    	JoinRowCallbackHandler<Feature> handler = getHandlerWith(new DocumentRowMapper());
    	
    	template.query(model.toString(), new MapSqlParameterSource(), handler);
    	features = handler.getResults();
    	
    	return features;
    }
    
    public int setCollabroomFeatureDeleted(long featureId, boolean deleted) throws Exception{
    	QueryModel deleteUpdate = QueryManager.createQuery(SADisplayConstants.COLLABROOM_FEATURE)
    		.update().equals(SADisplayConstants.DELETED, deleted)
    		.where().equals(SADisplayConstants.FEATURE_ID, featureId);
    	
    	QueryModel dateUpdate = QueryManager.createQuery(SADisplayConstants.FEATURE)
        		.update().equals(SADisplayConstants.LAST_UPDATE, new Date()).comma()
        		.equals(SADisplayConstants.SEQ_TIME, (System.currentTimeMillis()/1000) )
        		.where().equals(SADisplayConstants.FEATURE_ID, featureId);
    	
    	template.update(dateUpdate.toString(), dateUpdate.getParameters());
    	
    	return template.update(deleteUpdate.toString(), deleteUpdate.getParameters());
    }
    
    public int setUserFeatureDeleted(long featureId, boolean deleted) throws Exception{
    	QueryModel model = QueryManager.createQuery(SADisplayConstants.USER_FEATURE)
    		.update().equals(SADisplayConstants.DELETED, deleted)
    		.where().equals(SADisplayConstants.FEATURE_ID, featureId);
    	
    	QueryModel dateUpdate = QueryManager.createQuery(SADisplayConstants.FEATURE)
        		.update().equals(SADisplayConstants.LAST_UPDATE, new Date()).comma()
        		.equals(SADisplayConstants.SEQ_TIME, (System.currentTimeMillis()/1000) )
        		.where().equals(SADisplayConstants.FEATURE_ID, featureId);
    	
    	template.update(dateUpdate.toString(), dateUpdate.getParameters());
    	
    	return template.update(model.toString(), model.getParameters());
    }
    
    public int deleteUserFeature(long featureId) throws Exception{
    	QueryModel delUserFeature = QueryManager.createQuery(SADisplayConstants.USER_FEATURE)
    			.deleteFromTableWhere().equals(SADisplayConstants.FEATURE_ID);
    	
    	int userFeature = template.update(delUserFeature.toString(), 
    			new MapSqlParameterSource(SADisplayConstants.FEATURE_ID, featureId));
    	
    	//if one feature was deleted
    	if(userFeature == 1){
	    	QueryModel delFeature = QueryManager.createQuery(SADisplayConstants.FEATURE)
	    			.deleteFromTableWhere().equals(SADisplayConstants.FEATURE_ID);
	    	
	    	return template.update(delFeature.toString(), 
	    			new MapSqlParameterSource(SADisplayConstants.FEATURE_ID, featureId));
    	}
    	return -1;
    }
    
    public List<CollabroomFeature> getCollabroomFeatures(long featureId){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.COLLABROOM_FEATURE_TABLE)
    			.selectAllFromTable()
    			.join(SADisplayConstants.FEATURE).using(SADisplayConstants.FEATURE_ID)
    			.where().equals(SADisplayConstants.FEATURE_ID, featureId)
    			.and().equals(SADisplayConstants.DELETED, false);
    	
    	JoinRowCallbackHandler<CollabroomFeature> handler = getCollabroomFeatureHandlerWith();
    	
    	this.template.query(query.toString(), query.getParameters(), handler);
    	return handler.getResults();
    }

    /**
     * Share all of the specified user's userfeatures to a given collabroom
     * 
     * We create a collabroom feature for each user feature.
     * 
     * @param userId The id of the user whose userfeatures to share 
     * @param collabRoomId The id of the collabroom to share to 
     */
    public List<Long> shareFeatures( int userId, int collabRoomId){
	QueryModel selectQuery = QueryManager.createQuery(SADisplayConstants.USER_FEATURE_TABLE)
		.selectFromTable(Arrays.asList(Integer.toString(collabRoomId), SADisplayConstants.FEATURE_ID))
		.where().equals(SADisplayConstants.USER_ID, userId);
	
	QueryModel insertStatement = QueryManager.createQuery(SADisplayConstants.COLLABROOM_FEATURE_TABLE)
		.insertInto(Arrays.asList(SADisplayConstants.COLLAB_ROOM_ID, SADisplayConstants.FEATURE_ID), selectQuery)
		.returnValue(SADisplayConstants.FEATURE_ID);
	
	return this.template.queryForList(insertStatement.toString(), insertStatement.getParameters(), Long.class);
    }

    /**
     * Remove all of the specified user's shared features from a given collabroom
     * 
     * @param userId The id of the user whose features to remove 
     * @param collabRoomId The id of the collabroom to remove from
     */
    public List<Long> deleteSharedFeatures(int userId, int collabRoomId){
	QueryModel selectQuery = QueryManager.createQuery(SADisplayConstants.USER_FEATURE_TABLE)
		.selectFromTable(SADisplayConstants.FEATURE_ID)
		.where().equals(SADisplayConstants.USER_ID, userId);
	
	QueryModel deleteStatement = QueryManager.createQuery(SADisplayConstants.COLLABROOM_FEATURE_TABLE)
		.deleteFromTableWhere().equals(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId)
		.and().inAsSQL(SADisplayConstants.FEATURE_ID, selectQuery.toString())
		.returnValue(SADisplayConstants.FEATURE_ID);

	Map<String, Object> parameters = deleteStatement.getParameters();
	parameters.putAll(selectQuery.getParameters());
	
	return this.template.queryForList(deleteStatement.toString(), parameters, Long.class);
    }
    
    /**
     * Mark all collabroom features shared by a user as delete.
     * 
     * We do this rather than deleting them outright so that we can track deleted features. 
     *
     * @param userId The id of the user whose features to mark
     * @param collabRoomId The id of the collabroom the feature were shared to
     */
    public List<Long> markSharedFeaturesDeleted(int userId, int collabRoomId){
	QueryModel selectQuery = QueryManager.createQuery(SADisplayConstants.USER_FEATURE_TABLE)
		.selectFromTable(SADisplayConstants.FEATURE_ID)
		.where().equals(SADisplayConstants.USER_ID, userId);
	
	QueryModel updateStatement = QueryManager.createQuery(SADisplayConstants.COLLABROOM_FEATURE_TABLE)
		.update().equals(SADisplayConstants.DELETED, true)
		.where().inAsSQL(SADisplayConstants.FEATURE_ID, selectQuery.toString())
		.and().equals(SADisplayConstants.COLLAB_ROOM_ID, collabRoomId)
		.returnValue(SADisplayConstants.FEATURE_ID);
	
	Map<String, Object> parameters = updateStatement.getParameters();
	parameters.putAll(selectQuery.getParameters());
	
	return this.template.queryForList(updateStatement.toString(), parameters, Long.class);
    }
    
    /**
     * Utility method for getting the next value from the message_sequence
     * 
     * @return
     */
    public long getNextMessageId() {
    	long messageId = -1;
    	
    	messageId = template.queryForObject("select nextVal('message_sequence')", new MapSqlParameterSource(),
    			Long.class);
    	
    	return messageId;
    }

    /** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Folder>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Feature> getHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new FeatureRowMapper(), mappers);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<CollabroomFeature> getCollabroomFeatureHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new CollabRoomFeatureRowMapper(), mappers);
    }
    
}
