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
package edu.mit.ll.nics.nicsdao.mappers;

import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.datalayer.Document;
import edu.mit.ll.nics.common.constants.SADisplayConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DocumentRowMapper extends JoinRowMapper<Document> {

    public DocumentRowMapper() {
        super("document");
    }

    @Override
    public Document createRowObject(ResultSet rs, int rowNum) throws SQLException {
    	Document document = new Document();
    	document.setCreated(rs.getTimestamp(SADisplayConstants.CREATED));
    	document.setDatasourceid(rs.getString(SADisplayConstants.DATASOURCE_ID));
    	document.setDescription(rs.getString(SADisplayConstants.DESCRIPTION));
    	document.setDisplayname(rs.getString(SADisplayConstants.DISPLAY_NAME));
    	document.setDocumentid(rs.getString(SADisplayConstants.DOCUMENT_ID));
    	document.setFilename(rs.getString(SADisplayConstants.FILENAME));
    	document.setFiletype(rs.getString(SADisplayConstants.FILETYPE));
    	document.setFolderid(rs.getString(SADisplayConstants.FOLDER_ID));
    	document.setGlobalview(rs.getBoolean(SADisplayConstants.GLOBAL_VIEW));
    	document.setUsersessionid(rs.getInt(SADisplayConstants.USERSESSION_ID));
    	return document;
    }
    
    public String getKey(ResultSet rs) throws SQLException{
    	return rs.getString(SADisplayConstants.DOCUMENT_ID);
    }
}