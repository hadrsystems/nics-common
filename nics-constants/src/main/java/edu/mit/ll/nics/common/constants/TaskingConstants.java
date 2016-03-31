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
package edu.mit.ll.nics.common.constants;

public class TaskingConstants {

	// TABLE NAMES
	public static final String OPPERIOD_TABLE = "operational_period";
	public static final String UNIT_TABLE = "unit";
	public static final String TASK_TABLE = "Form";
	public static final String ASSIGNMENT_TABLE = "assignment";
	
	// Assignments
	public static final String ASSIGNMENT_ID = "id";
	public static final String ASSIGNMENT_UNIT_ID = "unit_id";
	public static final String ASSIGNMENT_OPPERIOD_ID = "operational_period_id";
	
	// TASK FORM TYPE
	public static final String TASK_TYPE = "TASK";
	
	// Operational Period columns
	public static final String OPPERIOD_ID = "id";
	public static final String OPPERIOD_INCIDENT_ID = "incident_id";
	public static final String OPPERIOD_START_UTC = "start_utc";
	public static final String OPPERIOD_END_UTC = "end_utc";
	
	// Unit Columns
	public static final String UNIT_ID = "id";
	public static final String UNIT_INCIDENT_ID = "incident_id";
	public static final String UNIT_COLLABROOM_ID = "collabroom_id";
	public static final String UNIT_UNIT_NAME = "unitname";
	
}
