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
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import edu.mit.ll.dao.QueryModel;
import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.SADisplayConstants;
import edu.mit.ll.nics.common.entity.OrgFolder;
import edu.mit.ll.nics.common.entity.datalayer.Folder;
import edu.mit.ll.nics.common.entity.datalayer.Rootfolder;
import edu.mit.ll.nics.nicsdao.FolderDAO;
import edu.mit.ll.nics.nicsdao.GenericDAO;
import edu.mit.ll.nics.nicsdao.QueryManager;
import edu.mit.ll.nics.nicsdao.mappers.FolderRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.OrgFolderRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.RootFolderRowMapper;

public class FolderDAOImpl extends GenericDAO implements FolderDAO {

	private Logger log;

    private NamedParameterJdbcTemplate template;

    @Override
    public void initialize() {
    	log = LoggerFactory.getLogger(FeatureDAOImpl.class);
        this.template = new NamedParameterJdbcTemplate(datasource);
    }

    /** getFolder
	 *  @param folderid - String - id of folder
	 *  @return Folder 
	 */
    public Folder getFolder(String folderid){
		JoinRowCallbackHandler<Folder> handler = getHandlerWith();
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER)
				.selectAllFromTableWhere().equals(SADisplayConstants.FOLDER_ID);
		
		template.query(
        		queryModel.toString(), 
        		new MapSqlParameterSource(SADisplayConstants.FOLDER_ID, folderid), 
        		handler);
		
		try{
			return handler.getSingleResult();
		}catch(Exception e){
			log.info("Could not find folder with folderid #0", folderid);
		}
		return null;
	}
		
    
    /** getFolderByName
	 *  @param folderid - String - id of folder
	 *  @return Folder 
	 */
    public Folder getFolderByName(String foldername, int workspaceId){
		JoinRowCallbackHandler<Folder> handler = getHandlerWith();
		
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER)
				.selectAllFromTableWhere().equals(SADisplayConstants.FOLDER_NAME)
				.and().equals(SADisplayConstants.WORKSPACE_ID);
		
		template.query(
        		queryModel.toString(), 
        		new MapSqlParameterSource(SADisplayConstants.FOLDER_NAME, foldername)
        			.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
        return handler.getResults().get(0);
	}
	
    /** getOrderedFolders
	 *  @param folderid - String - id of folder
	 *  @return List<Folder> - return folders ordered by index 
	 */
    public List<Folder> getOrderedFolders(String folderid, int workspaceid){
    	JoinRowCallbackHandler<Folder> handler = getHandlerWith();
    	
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER)
				.selectAllFromTableWhere().equals(SADisplayConstants.PARENT_FOLDER_ID)
				.and().equals(SADisplayConstants.WORKSPACE_ID)
				.orderBy(SADisplayConstants.INDEX);
    	
    	template.query(
        		queryModel.toString(), 
        		new MapSqlParameterSource(SADisplayConstants.PARENT_FOLDER_ID, folderid)
        		.addValue(SADisplayConstants.WORKSPACE_ID, workspaceid), 
        		handler);
        
        return handler.getResults();
    }
    
    /** getRootFolder
	 *  @param String name
	 *  @return RootFolder 
	 */
    public Rootfolder getRootFolder(String name, int workspaceid){
    		JoinRowCallbackHandler<Rootfolder> handler = getRootFolderHandlerWith(new FolderRowMapper());
    		
    		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ROOT_FOLDER_TABLE)
    				.selectAllFromTable().join(SADisplayConstants.FOLDER_TABLE).using(SADisplayConstants.FOLDER_ID)
    				.where().equals(SADisplayConstants.TABNAME)
    				.and().equals(SADisplayConstants.ROOTFOLDER_WORKSPACEID);
    		
  	        template.query(queryModel.toString(), 
  	            new MapSqlParameterSource(SADisplayConstants.TABNAME, name)
  	        		.addValue(SADisplayConstants.ROOTFOLDER_WORKSPACEID, workspaceid), handler);
  	        
  	        try{
  	        	return handler.getSingleResult();
  	        }catch(Exception e){
  	        	log.info("Error attempting to retrieve root folder #0: #1", name, e.getMessage());
  	        }
  	        return null;
    }
    
    public void updateParentFolderId(String folderId, String parentFolderId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE).update().
			equals(SADisplayConstants.PARENT_FOLDER_ID, parentFolderId).where().equals(SADisplayConstants.FOLDER_ID, folderId);
		
		this.template.update(query.toString(), new MapSqlParameterSource());
	}

    public void updateDataLayerFolderId(String datalayerId, String folderId){
		QueryModel query = QueryManager.createQuery(SADisplayConstants.DATALAYER_TABLE).update().
			equals(SADisplayConstants.FOLDER_ID, folderId).where().equals(SADisplayConstants.DATALAYER_ID, datalayerId);
		this.template.update(query.toString(), new MapSqlParameterSource());
	}
    
    public String getFolderId(String foldername, int workspaceId){
    	QueryModel query = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
		  .selectAllFromTableWhere().equals(SADisplayConstants.FOLDER_NAME)
		  .and().equals(SADisplayConstants.WORKSPACE_ID);
		
		JoinRowCallbackHandler<Folder> handler = getHandlerWith();
		
		this.template.query(query.toString(), 
				new MapSqlParameterSource(SADisplayConstants.FOLDER_NAME, foldername)
				.addValue(SADisplayConstants.WORKSPACE_ID, workspaceId), handler);
		
		if(handler.getResults().size() > 0){
			return handler.getResults().get(0).getFolderid(); //THIS IS A PROBLEM
		}else{
			log.info("Could not find folder named #0", foldername);
			return null;
		}
    }
    
	public int getNextFolderIndex(String folderid){
		int result = 0;
		
		QueryModel indexQuery = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
			.selectMaxFromTable(SADisplayConstants.INDEX)
			.where().equals(SADisplayConstants.PARENT_FOLDER_ID, folderid);
		
		try{
			int index = this.template.queryForObject(indexQuery.toString(), indexQuery.getParameters(), Integer.class);
			result = index + 1;
		}catch(Exception e){
			log.info("Could not find next folder index for folderid #0", folderid);
		}
		return result;
	}
    
    public OrgFolder getFolderOwner(String folderid){
    	QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.ORG_FOLDER_TABLE)
    			.selectAllFromTableWhere()
    			.equals(SADisplayConstants.FOLDER_ID);
    	
    	JoinRowCallbackHandler<OrgFolder> handler = getOrgFolderHandlerWith();
    	
    	this.template.query(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.FOLDER_ID, folderid), handler);
    	
    	if(handler.getResults().size() == 0){
    		return null;
    	}
    	
    	return handler.getResults().get(0); //orgfolder and folderid aren't contrained in the database
    }
    
	@Override
	public Folder createFolder(Folder folder) {
		
		try{
			
			MapSqlParameterSource map = new MapSqlParameterSource();
			map.addValue(SADisplayConstants.FOLDER_ID, UUID.randomUUID());
			map.addValue(SADisplayConstants.FOLDER_NAME, folder.getFoldername());
			map.addValue(SADisplayConstants.PARENT_FOLDER_ID, folder.getParentfolderid());
			map.addValue(SADisplayConstants.INDEX, folder.getIndex());
			map.addValue(SADisplayConstants.WORKSPACE_ID, folder.getWorkspaceid());
			
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
					.insertInto(new ArrayList<String>(map.getValues().keySet()))
					.returnValue("*");
			
			JoinRowCallbackHandler<Folder> handler = getHandlerWith();
			
			this.template.query(queryModel.toString(), map, handler);
			
			return handler.getSingleResult();
			
		}
		catch(Exception e){
			log.info("Failed to create folder #0", folder.getFoldername());
		}
		
		return null;
	}

	@Override
	public Folder updateFolder(Folder folder) {
		
		try{

			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
					.update().equals(SADisplayConstants.FOLDER_NAME).comma().equals(SADisplayConstants.PARENT_FOLDER_ID)
	    			.comma().equals(SADisplayConstants.INDEX).comma().equals(SADisplayConstants.WORKSPACE_ID)
	    			.where().equals(SADisplayConstants.FOLDER_ID).returnValue("*");

			MapSqlParameterSource map = new MapSqlParameterSource(SADisplayConstants.FOLDER_ID,folder.getFolderid());
			map.addValue(SADisplayConstants.FOLDER_NAME, folder.getFoldername());
			map.addValue(SADisplayConstants.PARENT_FOLDER_ID, folder.getParentfolderid());
			map.addValue(SADisplayConstants.INDEX, folder.getIndex());
			map.addValue(SADisplayConstants.WORKSPACE_ID, folder.getWorkspaceid());
			
			JoinRowCallbackHandler<Folder> handler = getHandlerWith();
			
			this.template.query(queryModel.toString(), map, handler);
			
			return handler.getSingleResult();
		}
		catch(Exception e){
			log.info("Failed to update folder #0", folder.getFoldername());
		}
		
		return null;
	}

	@Override
	public boolean removeFolder(String folderid) {
		
		try{
			
			QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
					.deleteFromTableWhere().equals(SADisplayConstants.FOLDER_ID);
			
			this.template.update(queryModel.toString(), new MapSqlParameterSource(SADisplayConstants.FOLDER_ID,folderid));
			
			return true;
			
		}catch(Exception e){
			log.info("Failed to delete folder with folderId #0", folderid);
		}
		
		return false;
	}

	@Override
	public void decrementIndexes(String parentFolderId, int index) {
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
				.update(SADisplayConstants.INDEX).value("= index - 1")
				.where().equals(SADisplayConstants.PARENT_FOLDER_ID, parentFolderId)
				.and().greaterThanOrEquals(SADisplayConstants.INDEX, index);
		this.template.update(queryModel.toString(), queryModel.getParameters());
	}

	@Override
	public void incrementIndexes(String parentFolderId, int index) {
		QueryModel queryModel = QueryManager.createQuery(SADisplayConstants.FOLDER_TABLE)
				.update(SADisplayConstants.INDEX).value("= index + 1")
				.where().equals(SADisplayConstants.PARENT_FOLDER_ID, parentFolderId)
				.and().greaterThanOrEquals(SADisplayConstants.INDEX, index);
		this.template.update(queryModel.toString(), queryModel.getParameters());
	}
	
    
    /** getRootFolderHandlerWith
   	 *  @param mappers - optional additional mappers
   	 *  @return JoinRowCallbackHandler<Rootfolder>
   	 */
       @SuppressWarnings({ "rawtypes", "unchecked" })
   	private JoinRowCallbackHandler<OrgFolder> getOrgFolderHandlerWith(JoinRowMapper... mappers) {
    	   return new JoinRowCallbackHandler(new OrgFolderRowMapper(), mappers);
       }
    
    /** getRootFolderHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Rootfolder>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Rootfolder> getRootFolderHandlerWith(JoinRowMapper... mappers) {
    	 return new JoinRowCallbackHandler(new RootFolderRowMapper(), mappers);
    }
    /** getHandlerWith
	 *  @param mappers - optional additional mappers
	 *  @return JoinRowCallbackHandler<Folder>
	 */
    @SuppressWarnings({ "rawtypes", "unchecked" })
	private JoinRowCallbackHandler<Folder> getHandlerWith(JoinRowMapper... mappers) {
        return new JoinRowCallbackHandler(new FolderRowMapper(), mappers);
    }

}
