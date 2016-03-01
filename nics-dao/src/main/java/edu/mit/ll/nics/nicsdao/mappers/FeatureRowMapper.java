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
import edu.mit.ll.nics.common.entity.Feature;
import edu.mit.ll.nics.common.constants.SADisplayConstants;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.postgis.PGgeometry;

public class FeatureRowMapper extends JoinRowMapper<Feature> {

    public FeatureRowMapper() {
        super("feature");
    }
   
    @Override
    public Feature createRowObject(ResultSet rs, int rowNum) throws SQLException {
Feature feature = new Feature();
    	
    	String geometryString = null;
    	try{
    		geometryString = rs.getString("st_astext");
    	}catch(SQLException sqlEx){
    		//Geometry was not returned as a string
    	}
    	
    	if(geometryString == null){
	    	try{
		    	PGgeometry pgGeometry = (PGgeometry) rs.getObject("geometry");
		    	String []temp= PGgeometry.splitSRID(pgGeometry.getGeometry().toString());
				geometryString = temp[1];
	    	}catch(Exception e){
	    		System.out.println("Could not set geometry on Feature.");
	    	}
    	}
        
    	feature.setGeometry(geometryString);
        feature.setFeatureId(rs.getLong("featureid"));
        feature.setDashStyle(rs.getString("dashstyle"));
        feature.setAttributes(rs.getString("attributes"));
        feature.setFillColor(rs.getString("fillcolor"));
        feature.setGraphic(rs.getString("graphic"));
        feature.setGraphicHeight(rs.getDouble("graphicheight"));
        feature.setGraphicWidth(rs.getDouble("graphicwidth"));
        feature.setHasGraphic(rs.getBoolean("hasgraphic"));
        feature.setIp(rs.getString("ip"));
        feature.setLabelsize(rs.getDouble("labelsize"));
        feature.setLabelText(rs.getString("labeltext"));
        feature.setLastupdate(rs.getDate("lastupdate"));
        feature.setNickname(rs.getString("nickname"));
        feature.setOpacity(rs.getDouble("opacity"));
        feature.setPointRadius(rs.getDouble("pointradius"));
        feature.setRotation(rs.getDouble("rotation"));
        feature.setSeqnum(rs.getLong("seqnum"));
        feature.setSeqtime(rs.getLong("seqtime"));
        feature.setStrokeColor(rs.getString("strokecolor"));
        feature.setStrokeWidth(rs.getDouble("strokewidth"));
        feature.setTime(rs.getString("time"));
        feature.setTopic(rs.getString("topic"));
        feature.setType(rs.getString("type"));
        feature.setUsername(rs.getString("username"));
        feature.setUsersessionId(rs.getInt("usersessionid"));
        feature.setVersion(rs.getString("version"));
        return feature;
    }
    
    public String getKey(ResultSet rs) throws SQLException{
    	return rs.getString(SADisplayConstants.FEATURE_ID);
    }
}
