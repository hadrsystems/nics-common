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

import edu.mit.ll.nics.common.entity.Assignment;
import edu.mit.ll.nics.common.entity.AssignmentId;
import edu.mit.ll.nics.common.entity.OperationalPeriod;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.TaskingConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AssignmentRowMapper extends JoinRowMapper<Assignment> {

    public AssignmentRowMapper() {
        super("assignment");
    }

    @Override
    public Assignment createRowObject(ResultSet rs, int rowNum) throws SQLException {
        Assignment assignment = new Assignment();
        AssignmentId id = new AssignmentId();
        id.setOperationalPeriodId(rs.getLong(TaskingConstants.ASSIGNMENT_OPPERIOD_ID));
        id.setUnitId(rs.getLong(TaskingConstants.ASSIGNMENT_UNIT_ID));
        assignment.setId(id);        
        assignment.setPublished(rs.getBoolean("published"));
        
        return assignment;
    }
   
    public String getKey(ResultSet rs) throws SQLException {
    	// TODO: There is no id/primary key... there's an index that's a combo of the two fields
    	// below, but not sure how getKey should represent that...
    	return "" + rs.getLong(TaskingConstants.ASSIGNMENT_UNIT_ID) + "." +
    			rs.getLong(TaskingConstants.ASSIGNMENT_OPPERIOD_ID) ;
    }
}
