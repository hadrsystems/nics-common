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
package edu.mit.ll.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;

public abstract class JoinRowMapper<T> implements RowMapper<T> {

    private String tableName;
    private Logger log = LoggerFactory.getLogger(JoinRowMapper.class);
    private List<JoinRowMapper> additionalMappers;
    private Map<Object,T> mappedObjects = new HashMap<Object,T>();

    public JoinRowMapper(String tableName) {
        this.tableName = tableName;
        this.additionalMappers = new ArrayList<JoinRowMapper>();
    }

    public abstract T createRowObject(ResultSet rs, int rowNum) throws SQLException;
    
    public abstract Object getKey(ResultSet rs) throws SQLException;
    
    @Override
    public T mapRow(ResultSet rs, int rowNum){
    	T obj = null;
    	try{
    		Object key = this.getKey(rs);
    		if (key != null){
		    	obj = this.createRowObject(rs, rowNum);
		    	this.addExistingObject(key, obj);
		    	return obj;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    	return obj;
    }

    public int getColumnId(ResultSet rs, String columnName) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int numCols = meta.getColumnCount();
        
        if(log.isDebugEnabled()){
            log.debug("Number of columns: " + numCols);
        }

        for (int i = 1; i <= numCols; i++) {
            String table = meta.getTableName(i);
            String column = meta.getColumnName(i);
            
            if(log.isDebugEnabled()){
                log.debug("Column id=" + i + " ; column name=" + column + "; column label=" + meta.getColumnLabel(i) + "; column table=" + table);
            }

            if(column.equalsIgnoreCase(columnName) && (table.equalsIgnoreCase(this.tableName) || table.isEmpty())) {
                return i;
            }
        }

        // If we break out of the loop, then it means we couldn't find the column
        // so we should throw a SQLException about the missing column
        throw new SQLException("No column with name '" + columnName + "' in table '" + tableName + "'");
    }

    public JoinRowMapper<T> attachAdditionalMapper(JoinRowMapper mapper) {
        additionalMappers.add(mapper);
        return this;
    } 

    public List<JoinRowMapper> getAdditionalMappers() {
        return additionalMappers;
    }
    
    public void addExistingObject(Object o, T row){
    	if(o != null){
    		mappedObjects.put(o, row);
    	}else{
    		log.info("Row was not added to existing objects. Key was null.");
    	}
    }
    
    public T getExistingObject(ResultSet rs){
    	try{
    		return mappedObjects.get(getKey(rs));
    	}catch(Exception e){
    		log.info("Could not retrieve object from existing mapped objects");
    	}
    	return null;
    }

}
