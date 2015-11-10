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
import edu.mit.ll.nics.common.entity.ResourceAssign;
import edu.mit.ll.nics.common.entity.Unit;
import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.constants.TaskingConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResourceAssignRowMapper extends JoinRowMapper<ResourceAssign> {

    public ResourceAssignRowMapper() {
        super("resourceassign");
    }

    @Override
    public ResourceAssign createRowObject(ResultSet rs, int rowNum) throws SQLException {
        ResourceAssign resourceAssign = new ResourceAssign();
        resourceAssign.setId(rs.getLong("id")); // TODO: double check, make constant
        resourceAssign.setLeader(rs.getBoolean("leader"));
        // Need to add unit, too
        //Unit unit = new Unit();
        //unit.setId(rs.getLong("unit_id"));
        
        //resourceAssign.setUserInfo(userInfo)       
        //Assignment assignment = new Assignment();
        //assignment.set        
        //resourceAssign.setAssignment((Assignment)rs.getObject("")); // Really an assignment id in the db        
        resourceAssign.setUserid(rs.getInt("user_id"));
        
        return resourceAssign;
    }
    
    public String getKey(ResultSet rs) throws SQLException{
    	return rs.getString("id");
    }
}