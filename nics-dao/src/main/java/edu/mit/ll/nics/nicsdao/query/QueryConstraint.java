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
package edu.mit.ll.nics.nicsdao.query;

/**
 * Query constraint class developed originally for PHINICS API
 * 
 * @author santi
 *
 */
public class QueryConstraint {
	public static final String KEY_DATE_RANGE = "date_range";
	public static final String KEY_ORDER_BY = "order_by";
	public static final String KEY_RESULTSET_RANGE = "resultset_range";
	public static final String KEY_COLUMN_SELECTION = "column_selection";
	public static final String KEY_INCIDENT_ID = "incidentid";
	
	public static enum OrderByType { 
		ASC("ASC"), 
		DESC("DESC");
		String name;
		private OrderByType(String name) {
			this.name = name;
		}
		
		public boolean equals(String other) {
			return (other == null) ? false : name.equals(other);
		}
	
		public String toString() {
			return name;
		}
	};
	
	public static class OrderBy {
		public String colName;
		public OrderByType type;
		public OrderBy(String colName, OrderByType type) {
			this.colName = colName;
			this.type = type;
		}
	};
	
	public static class ResultSetPage {
		public Integer offset;
		public Integer limit;
		public ResultSetPage(Integer offset, Integer limit) {
			this.offset = offset;
			this.limit = limit;
		}		
	};
	
	public static class UTCRange {
		public Long from;
		public Long to;
		public String colName;
		public UTCRange(String colName, Long from, Long to) {
			this.colName = colName;
			this.from = from;
			this.to = to;
		}
	};
}

