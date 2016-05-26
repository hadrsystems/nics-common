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
package edu.mit.ll.dao;

public final class QueryBuilder{

	public static String NOT_IN = "not in";
	public static String BOUNDS = "bounds";
	public static String NULL = "null";
	public static String ZERO = "0";
	public static String RETURNING = "returning";
	//starter commands
	public static String SELECT = "select";
	public static String FROM = "from";
	public static String DELETE = "delete ";
	public static String UPDATE = "update ";
	public static String INSERT = "insert";
	//must add space when concatentating
	public static String WHERE = " where";
	public static String LIKE = " like";
	public static String I_LIKE = " ilike";
	public static String AND = " and";
	public static String SET = " set";
	public static String OR = " or";
	public static String JOIN = " join";
	public static String USING = " using";
	public static String ON = " on";
	public static String ASC = " ASC";
	public static String DESC = " DESC";
	public static String INTO = " into";
	public static String VALUES = " VALUES";
	public static String NEXT_VAL = " nextval";
	public static String NOW = "now()";
	public static String LEFT = " left";
	public static String LOWER = "lower";
	public static String DEFAULT = "DEFAULT";
	public static String WITH = "WITH";
	public static String AS = "AS";
	
	//handle spacing
	public static String LIMIT = " limit ";
	public static String OFFSET = " offset ";
	public static String ORDER_BY = " order by ";
	public static String IS_NULL = " is null";
	public static String IS_NOT_NULL = " is not null";
	public static String DISTINCT = "distinct";
	public static String IN = " in";
	public static String MAX = " max";
	
	public static String COLON = ":";
	public static String EQUALS = "=";
	public static String COMMA = ",";
	public static String SPACE = " ";
	public static String NOT_EQUAL = "<>";
	public static String GREATER_THAN = ">";
	public static String LESS_THAN = "<";
	public static String GREATER_THAN_OR_EQUALS = GREATER_THAN + EQUALS;
	public static String LESS_THAN_OR_EQUALS = LESS_THAN + EQUALS;
	public static String ALL = "*";
	public static String OPEN = "(";
	public static String CLOSE = ")";
	public static String SINGLE_QUOTE = "'";
	
	//Special Geometry insertion
	public static String GEOMETRY = "geometry";
	public static String SRS = "srs";
	public static String TRANS = "trans";
	public static String GEOMETRY_FUNCTION = "ST_GeomFromText";
	public static String GEO_TRANSFORM_FUNCTION = "ST_Transform";
	public static Integer SRS_PROJECTION = 3857;
	
	
	public QueryBuilder(){}
	
	public static String selectFrom(String table, String value, boolean distinct){
		StringBuffer selectFrom = new StringBuffer(SELECT);
		selectFrom.append(SPACE);
		
		if(distinct){
			selectFrom.append(DISTINCT);
			selectFrom.append(SPACE);
		}
		selectFrom.append(value);
		selectFrom.append(SPACE);
		selectFrom.append(selectFrom(table));
		return selectFrom.toString();
	}
	
	public static String selectFrom(String table){
		StringBuffer selectFrom = new StringBuffer();
		selectFrom.append(FROM);
		selectFrom.append(SPACE);
		selectFrom.append(table);
		return selectFrom.toString();
	}
	
	public static String selectFromWhere(String table){
		StringBuffer selectFromWhere = new StringBuffer();
		selectFromWhere.append(selectFrom(table));
		selectFromWhere.append(WHERE);
		return selectFromWhere.toString();
	}
	
	public static String selectFromWhere(String table, String value, boolean distinct){
		StringBuffer selectFromWhere = new StringBuffer(SELECT);
		selectFromWhere.append(SPACE);
		
		if(distinct){
			selectFromWhere.append(DISTINCT);
			selectFromWhere.append(SPACE);
		}
		
		selectFromWhere.append(value);
		selectFromWhere.append(SPACE);
		selectFromWhere.append(selectFrom(table));
		selectFromWhere.append(WHERE);
		return selectFromWhere.toString();
	}
	
	public static String deleteFromWhere(String table){
		StringBuffer deleteFromWhere = new StringBuffer();
		deleteFromWhere.append(DELETE);
		deleteFromWhere.append(FROM);
		deleteFromWhere.append(SPACE);
		deleteFromWhere.append(table);
		deleteFromWhere.append(WHERE);
		return deleteFromWhere.toString();
	}
	
	public static String orderBy(String field){
		return ORDER_BY + field;
	}
	
	public static String isNull(String field){
		return SPACE + field + IS_NULL;
	}
	
	public static String isNotNull(String field){
		return SPACE + field + IS_NOT_NULL;
	}
	
	public static String limit(String value){
		return LIMIT + value;
	}
	
	public static String offset(String value){
		return OFFSET + value;
	}
	
	public static String selectMaxFromColumn(String table, String field){
		StringBuffer maxColumnString = new StringBuffer();
		maxColumnString.append(SELECT);
		maxColumnString.append(MAX);
		maxColumnString.append(OPEN);
		maxColumnString.append(field);
		maxColumnString.append(CLOSE);
		maxColumnString.append(SPACE);
		maxColumnString.append(FROM);
		maxColumnString.append(SPACE);
		maxColumnString.append(table);
		return maxColumnString.toString();
	}
	
	public static String addFieldCondition(String name, String pName, String operator){
		StringBuffer query = new StringBuffer();
		query.append(SPACE);
		query.append(name);
		query.append(operator);
		query.append(COLON);
		query.append(pName);
		return query.toString();
	}
}