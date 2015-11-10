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
package edu.mit.ll.nics.nicsdao;

import edu.mit.ll.nics.common.entity.Incident;
import edu.mit.ll.nics.common.entity.IncidentType;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface IncidentDAO extends BaseDAO {
    public List<Incident> getIncidents(int workspaceId);
    public List<Incident> getIncidents();
    public List<Map<String,Object>> getArchivedIncidentNames(String prefix, int workspaceid);
    public List<Map<String,Object>> getActiveIncidentNames(int orgid, int workspaceid);
    public void updateIncidentFolder(List<String> incidentNames, String folder, int workspaceid);
    public List<Map<String,Object>> getIncidentMapAdmins(int incidentid, String roomname);
    public List<Incident> getNonArchivedIncidents(int workspaceid);
    public int setIncidentCenter(String incidentname);
    public Incident getIncident(int incidentid);
    public Incident getIncidentByName(String incidentname, int workspaceId);
    public int create(String incidentname, double lat, double lon, int usersessionid, int workspaceid, int parentid, String description);
    public int createIncidentIncidentTypes(int incidentid, List<IncidentType> types);
    public int getNextIncidentId();
    public List<IncidentType> getIncidentTypes();
    public int getIncidentId(String name);
    public List<Incident> getParentIncidents(int workspaceId);
    public List<Incident> getChildIncidents(int parentId);
    public List<String> getChildIncidentNames(List<String> incidentNames, int workspaceid);
    public List<String> getParentIncidentNames(List<String> incidentNames, int workspaceid);
    public List<Incident> getIncidentsByName(List<String> names, int workspaceid);
}
