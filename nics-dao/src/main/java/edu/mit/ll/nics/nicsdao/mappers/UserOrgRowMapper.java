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
import edu.mit.ll.nics.common.entity.UserOrg;
import edu.mit.ll.nics.common.constants.SADisplayConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserOrgRowMapper extends JoinRowMapper<UserOrg> {

    public UserOrgRowMapper() {
        super("userorg");
    }

    @Override
    public UserOrg createRowObject(ResultSet rs, int rowNum) throws SQLException {
    	int userorgid = rs.getInt(SADisplayConstants.USER_ORG_ID);
    	if(userorgid != 0){
	        UserOrg userorg = new UserOrg();
	        userorg.setUserorgid(userorgid);
	        userorg.setUserid(rs.getInt(SADisplayConstants.USER_ID));
	        userorg.setSystemroleid(rs.getInt(SADisplayConstants.SYSTEM_ROLE_ID));
	        userorg.setDescription(rs.getString(SADisplayConstants.DESCRIPTION));
	        userorg.setRank(rs.getString(SADisplayConstants.RANK));
	        userorg.setJobTitle(rs.getString(SADisplayConstants.JOB_TITLE));
	        userorg.setOrgid(rs.getInt(SADisplayConstants.ORG_ID));
	        return userorg;
    	}
    	return null;
    }
    
    public Integer getKey(ResultSet rs) throws SQLException{
    	return rs.getInt(SADisplayConstants.USER_ORG_ID);
    }
}
