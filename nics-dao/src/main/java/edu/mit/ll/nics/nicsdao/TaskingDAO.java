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

import java.util.List;

import edu.mit.ll.nics.common.entity.Assignment;
import edu.mit.ll.nics.common.entity.AssignmentId;
import edu.mit.ll.nics.common.entity.Form;
import edu.mit.ll.nics.common.entity.ResourceAssign;
import edu.mit.ll.nics.common.entity.Unit;
import edu.mit.ll.nics.common.entity.User;


public interface TaskingDAO extends BaseDAO {

	public long opPeriodExists(long start, long end, long incidentId);
	public int createOperationalPeriod(long start, long end, long incidentId);
	public String getOperationalPeriodsString(int incidentId);
	public int createAssignment(long unitId, long operationalPeriodId);
	public int publishAssignment(int unitId, int opPeriod);
	public Assignment getAssignment(AssignmentId id);
	public Assignment getAssignment(long unitId, long opPeriodId);
	public String getAssignmentString(long unitId, long opPeriodId);
	public String getAssignmentString(AssignmentId id);
	public List<Assignment> getAssignments(int incidentId);
	public String getAssignmentsString(int incidentId);
	public int assignTask(long unitId, long opPeriodId, long taskId);
	public int unassignTask(int taskId);
	public List<Form> getTasks(int[] formIds);
	public String getTasksString(int[] formIds);
	public List<Form> getTasks(int incidentId);
	public String getTasksString(int incidentId);
	public String getTaskString(long seqnum, long seqtime, long incidentid, long formtypeid);
	public List<Unit> getUnits(int incidentId);
	public String getUnitString(int incidentId, int collabRoomId);
	public String getUnitsString(int incidentId);
	public void addCollabRoomPermission(int userid, int collabroomid, int roleid);
	public int updateAssignmentLeader(long unitId, long userId, long opPeriodId);
	public long getNextAssignResourceId();
	public long getNextUnitId();
	public int createUnit(Unit unit);
	public int assignResource(ResourceAssign resourceAssignment);
	public int assignResource(int userId, int unitId, int opPeriodId, boolean leader);
	public int removeResourceAssign(ResourceAssign resourceAssign);
	public ResourceAssign getResourceAssign(long unitId, long opPeriodId, long userId);
	public boolean isResourceAssignedForOpPeriod(long opPeriodId, long userId);
	public List<User> getAssignedUsers();
}