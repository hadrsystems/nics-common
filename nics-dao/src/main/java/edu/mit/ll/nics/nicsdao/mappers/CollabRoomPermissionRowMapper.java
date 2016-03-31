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
package edu.mit.ll.nics.nicsdao.mappers;

import edu.mit.ll.jdbc.JoinRowMapper;
import edu.mit.ll.nics.common.entity.CollabroomPermission;
import edu.mit.ll.nics.common.constants.SADisplayConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CollabRoomPermissionRowMapper extends JoinRowMapper<CollabroomPermission> {

    public CollabRoomPermissionRowMapper() {
        super("collabroompermission");
    }

    @Override
    public CollabroomPermission createRowObject(ResultSet rs, int rowNum) throws SQLException {
    	int id = rs.getInt(SADisplayConstants.COLLAB_ROOM_PERMISSION_ID);
    	if(id != 0){
	    	CollabroomPermission permission = new CollabroomPermission();
	        permission.setCollabRoomId(rs.getInt(SADisplayConstants.COLLAB_ROOM_ID));
	        permission.setCollabroomPermissionId(rs.getInt(SADisplayConstants.COLLAB_ROOM_PERMISSION_ID));
	        permission.setSystemRoleId(rs.getInt(SADisplayConstants.SYSTEM_ROLE_ID));
	        permission.setUserId(rs.getInt(SADisplayConstants.USER_ID));
	        return permission;
    	}
    	return null;
   }
    
    public Integer getKey(CollabroomPermission collabroom){
    	if(collabroom != null){
    		return new Integer(collabroom.getCollabroomPermissionId());
    	}
    	return null;
    }
    
    public Integer getKey(ResultSet rs) throws SQLException{
    	return rs.getInt(SADisplayConstants.COLLAB_ROOM_PERMISSION_ID);
    }
}