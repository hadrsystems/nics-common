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
package edu.mit.ll.dao;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryModel{
	
	private Map<String, Object> parameters = new HashMap<String,Object>();
	private StringBuffer query = new StringBuffer();
	private String table;
	
	public QueryModel(){}
	
	public QueryModel(String table){
		this.table = table;
	}
	
	public QueryModel selectFromTable(){
		query.append(QueryBuilder.selectFrom(table));
		return this;
	}
	
	public QueryModel selectDistinctFromTable(String field){
		query.append(QueryBuilder.selectFrom(table, field, true));
		return this;
	}
	
	public QueryModel selectFromTable(String field){
		query.append(QueryBuilder.selectFrom(table, field, false));
		return this;
	}

	public QueryModel selectMaxFromTable(String field){
		query.append(QueryBuilder.selectMaxFromColumn(table,field));
		return this;
	}
	
	public QueryModel selectFromTable(String... fields){
		return selectFromTable(Arrays.asList(fields));
	}
	
	public QueryModel selectFromTable(List<String> fields){
		query.append(QueryBuilder.selectFrom(table, this.buildList(fields, QueryBuilder.COMMA, ""), false));
		return this;
	}
	
	public QueryModel selectAllFromTable(){
		query.append(QueryBuilder.selectFrom(table, QueryBuilder.ALL, false));
		return this;
	}
	
	public QueryModel selectAllFromFeatureTable(int geoType){
		query.append(QueryBuilder.selectFrom(table, QueryBuilder.ALL + ",ST_AsText(st_transform(geometry," + 
				geoType + "))", false));
		return this;
	}
	
	public QueryModel selectFromTableWhere(){
		query.append(QueryBuilder.selectFromWhere(this.table));
		return this;
	}
	
	public QueryModel selectDistinctFromTableWhere(String value){
		query.append(QueryBuilder.selectFromWhere(this.table, value, true));
		return this;
	}
	
	public QueryModel selectFromTableWhere(String value){
		query.append(QueryBuilder.selectFromWhere(this.table, value, false));
		return this;
	}
	
	public QueryModel selectAllFromTableWhere(){
		query.append(QueryBuilder.selectFromWhere(this.table, QueryBuilder.ALL, false));
		return this;
	}
	
	public QueryModel deleteFromTableWhere(){
		query.append(QueryBuilder.deleteFromWhere(this.table));
		return this;
	}
	
	public QueryModel where(){
		query.append(QueryBuilder.WHERE);
		return this;
	}
	
	public QueryModel update(){
		query.append(QueryBuilder.UPDATE).append(table).append(QueryBuilder.SET);
		return this;
	}
	
	public QueryModel update(String value){
		query.append(QueryBuilder.UPDATE).append(table).append(QueryBuilder.SET);
		query.append(QueryBuilder.SPACE);
		query.append(value);
		return this;
	}
	
	public QueryModel now(){
		query.append(QueryBuilder.EQUALS);
		query.append(QueryBuilder.NOW);
		return this;
	}
	
	public QueryModel equalsInnerSelect(String field, String sql){
		query.append(QueryBuilder.SPACE);
		query.append(field);
		query.append(QueryBuilder.EQUALS);
		query.append(QueryBuilder.OPEN);
		query.append(sql);
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	/* Don't need to build a paramater map - just append conditional string */
	public QueryModel equals(String field){
		query.append(QueryBuilder.addFieldCondition(field, field, QueryBuilder.EQUALS));
		return this;
	}
	
	public QueryModel equals(String field, Object value){
		return this.equals(field, field, value);
	}
	
	public QueryModel equals(String field, String pName, Object value){
		if(value != null){
			this.parameters.put(pName, value);
		}
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.EQUALS));
		return this;
	}
	
	public QueryModel notEqual(String field){
		query.append(QueryBuilder.addFieldCondition(field, field, QueryBuilder.NOT_EQUAL));
		return this;
	}
	
	public QueryModel notEqual(String field, Object value){
		return this.notEqual(field, field, value);
	}
	
	public QueryModel notEqual(String field, String pName, Object value){
		this.parameters.put(pName, value);
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.NOT_EQUAL));
		return this;
	}
	
	public QueryModel greaterThanOrEquals(String field, Object value){
		 return this.greaterThanOrEquals(field, field, value);
	}
	
	public QueryModel greaterThanOrEquals(String field, String pName, Object value){
		this.parameters.put(pName, value);
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.GREATER_THAN_OR_EQUALS));
		return this;
	}
	
	public QueryModel lessThanOrEquals(String field, Object value){
		return this.lessThanOrEquals(field, field, value);
	}
	
	public QueryModel lessThanOrEquals(String field, String pName, Object value){
		this.parameters.put(pName, value);
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.LESS_THAN_OR_EQUALS));
		return this;
	}
	
	public QueryModel greaterThan(String field, Object value){
		return this.greaterThan(field, field, value);
	}
	
	public QueryModel greaterThan(String field, String pName, Object value){
		this.parameters.put(pName, value);
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.GREATER_THAN));
		return this;
	}
	
	public QueryModel lessThan(String field, Object value){
		return this.lessThan(field, field, value);
	}
	
	public QueryModel lessThan(String field, String pName, Object value){
		this.parameters.put(pName, value);
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.LESS_THAN));
		return this;
	}
	
	public QueryModel like(String field){
		query.append(QueryBuilder.SPACE);
		query.append(field);
		query.append(QueryBuilder.LIKE);
		return this;
	}
	
	public QueryModel equalsLower(String field){
		StringBuffer tempQuery = new StringBuffer();
		tempQuery.append(QueryBuilder.LOWER);
		tempQuery.append(QueryBuilder.OPEN);
		tempQuery.append(field);
		tempQuery.append(QueryBuilder.CLOSE);
		
		query.append(QueryBuilder.addFieldCondition(tempQuery.toString(), field, QueryBuilder.EQUALS));
		
		return this;
	}
	
	public QueryModel value(String value){
		query.append(QueryBuilder.SPACE);
		query.append(value);
		return this;
	}
	
	public QueryModel like(String field, String value){
		return this.like(field, field, value);
	}
	
	public QueryModel like(String field, String pName, String value){
		this.parameters.put(pName, value);
		query.append(QueryBuilder.addFieldCondition(field, pName, QueryBuilder.LIKE));
		return this;
	}
	
	public QueryModel join(String table){
		query.append(QueryBuilder.JOIN);
		query.append(QueryBuilder.SPACE);
		query.append(table);
		return this;
	}
	
	public QueryModel notIn(String value, String sql){
		query.append(QueryBuilder.SPACE);
		query.append(value);
		query.append(QueryBuilder.SPACE);
		query.append(QueryBuilder.NOT_IN);
		query.append(QueryBuilder.SPACE);
		query.append(QueryBuilder.OPEN);
		query.append(sql);
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel left(){
		query.append(QueryBuilder.LEFT);
		return this;
	}
	
	public QueryModel using(String table){
		query.append(QueryBuilder.USING);
		query.append(QueryBuilder.OPEN);
		query.append(table);
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel insertInto(List<String> fields, String primaryKey){
		this.buildInsert(fields, primaryKey);
		this.VALUES(fields, true);
		return this;
		
	}
	
	public QueryModel insertInto(List<String> fields){
		this.buildInsert(fields, null);
		this.VALUES(fields, false);
		return this;
	}
	
	public QueryModel insertInto(List<String> fields, QueryModel select){
		this.buildInsert(fields, null);
		query.append(QueryBuilder.SPACE);
		query.append(select.toString());
		parameters.putAll(select.getParameters());
		return this;
	}
	
	private QueryModel buildInsert(List<String> fields, String primaryKey){
		query.append(QueryBuilder.INSERT);
		query.append(QueryBuilder.INTO);
		query.append(QueryBuilder.SPACE);
		query.append(this.table);
		query.append(QueryBuilder.OPEN);

		if(primaryKey != null){
			query.append(primaryKey);
			query.append(QueryBuilder.COMMA);
		}
		query.append(this.buildList(fields, QueryBuilder.COMMA, null));
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel VALUES(List<String> values, boolean hasPrimaryKey){
		query.append(QueryBuilder.VALUES);
		query.append(QueryBuilder.OPEN);
		if(hasPrimaryKey){
			query.append(QueryBuilder.DEFAULT);
			query.append(QueryBuilder.COMMA);
		}
		query.append(this.buildList(values, QueryBuilder.COMMA, QueryBuilder.COLON));
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel inAsSQL(String column, String sql){
		query.append(QueryBuilder.SPACE);
		query.append(column);
		query.append(QueryBuilder.IN);
		query.append(QueryBuilder.OPEN);
		query.append(sql);
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel inAsString(String column, List<String> fields){
		query.append(QueryBuilder.SPACE);
		query.append(column);
		query.append(QueryBuilder.IN);
		query.append(QueryBuilder.OPEN);
		
		//For single field
		if(fields.size() == 1){
			query.append(QueryBuilder.SINGLE_QUOTE);
			query.append(fields.get(0));
			query.append(QueryBuilder.SINGLE_QUOTE);
		}else{
			query.append(this.buildList(fields, QueryBuilder.SINGLE_QUOTE + QueryBuilder.COMMA, QueryBuilder.SINGLE_QUOTE));
			query.append(QueryBuilder.SINGLE_QUOTE); //add quote to the end
		}
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel selectNextVal() {
		query.append(QueryBuilder.SELECT);
		query.append(QueryBuilder.NEXT_VAL);
		query.append(QueryBuilder.OPEN);
		query.append(QueryBuilder.SINGLE_QUOTE);
		query.append(this.table);
		query.append(QueryBuilder.SINGLE_QUOTE);
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel open(){
		query.append(QueryBuilder.OPEN);
		return this;
	}
	
	public QueryModel close(){
		query.append(QueryBuilder.CLOSE);
		return this;
	}
	
	public QueryModel and(){
		query.append(QueryBuilder.AND);
		return this;
	}
	
	public QueryModel or(){
		query.append(QueryBuilder.OR);
		return this;
	}
	
	public QueryModel on(){
		query.append(QueryBuilder.ON);
		return this;
	}
	
	public QueryModel comma(){
		query.append(QueryBuilder.COMMA);
		return this;
	}
	
	public QueryModel orderBy(String value){
		query.append(QueryBuilder.orderBy(value));
		return this;
	}
	
	public QueryModel isNull(String field){
		query.append(QueryBuilder.isNull(field));
		return this;
	}
	
	public QueryModel limit(String value){
		query.append(QueryBuilder.limit(value));
		return this;
	}
	
	public QueryModel offset(String value){
		query.append(QueryBuilder.offset(value));
		return this;
	}
	
	public QueryModel asc(){
		query.append(QueryBuilder.ASC);
		return this;
	}
	
	public QueryModel desc(){
		query.append(QueryBuilder.DESC);
		return this;
	}
	
	public QueryModel returnValue(String field){
		query.append(QueryBuilder.SPACE);
		query.append(QueryBuilder.RETURNING);
		query.append(QueryBuilder.SPACE);
		query.append(field);
		return this;
	}
	
	public Map<String, Object> getParameters(){
		return this.parameters;
	}
	
	public String toString(){
		//System.out.println("***********************" + query.toString());
		return query.toString();
	}
	
	public String buildList(List<String> fields, String postDelimiter, String preDelimiter){
		StringBuffer result = new StringBuffer();
		//For multiple fields
		for(String field : fields){
			if(result.length() != 0){
				result.append(postDelimiter);
			}
			
			//handle geometry
			if(preDelimiter!=null){//VALUES list
				if(field.equalsIgnoreCase(QueryBuilder.GEOMETRY) || field.equalsIgnoreCase(QueryBuilder.BOUNDS)){
					field = insertGeometry(field);
				}else{
					result.append(preDelimiter);
				}
			}
			result.append(field);
		}
		return result.toString();
	}
	

	private String insertGeometry(String field){
		StringBuffer result = new StringBuffer();
		result.append(QueryBuilder.GEOMETRY_FUNCTION);
	    result.append(QueryBuilder.OPEN);
	    result.append(QueryBuilder.COLON);
	    result.append(field);
	    result.append(QueryBuilder.COMMA);
	    result.append(QueryBuilder.SRS_PROJECTION);
	    result.append(QueryBuilder.CLOSE);
		return result.toString();
	}
}