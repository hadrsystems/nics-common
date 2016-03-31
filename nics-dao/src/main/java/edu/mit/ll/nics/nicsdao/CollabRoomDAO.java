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
package edu.mit.ll.nics.nicsdao;


import edu.mit.ll.nics.common.entity.CollabRoom;

import java.util.List;

public interface CollabRoomDAO extends BaseDAO {
	public int getCollabRoomSystemRole(int collabRoomId, int userId);
	public void createCollabPermission(int id, int adminRole, int userId);
	public void clearCollabroomPermissions(int collabroomid);
	public String getCollabroomName(int collabroomid);
	public List<CollabRoom> getAllCollabroomDatalayers(int workspaceId);
	public boolean hasRoomNamed(int incidentId, String collabRoomName);
	public boolean hasRoomNamed(String text);
	public List<CollabRoom> updateCollabrooms(String currentIncident, int currentIncidentId);
	public boolean hasPermissions(long userid, long collabroomid);
	public boolean hasPermissions(long userid, long collabroomid, boolean includeIncidentMap);
	public int create(CollabRoom collabroom);
	public int getCollabRoomId(String name);
	public int getCollabRoomId(String name, int workspaceId);
	public boolean secureRoom(long collabRoomId, int userId, int systemRoleId);
	public boolean unsecureRoom(long collabRoomId);
}
