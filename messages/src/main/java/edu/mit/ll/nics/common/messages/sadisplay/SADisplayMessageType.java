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
package edu.mit.ll.nics.common.messages.sadisplay;

/**
 * The Enum SADisplayMessageType.
 */
public enum SADisplayMessageType {
	
	/** The CHAT. */
	CHAT("msg"),
	
	/** The FEATURE. */
	FEATURE("feat"),
	
	/** The PRESENCE. */
	PRESENCE("pres"),
	
	/** The MAP. */
	MAP("map"),
	
	/** The IN c_ status. */
	INC_STATUS("stat"),
	
	/** The RESOURCE. */
	RESOURCE("res"),
	
	/** SITREP */
	SITREP("sitrep"),

	/** phiTASK */
	PHITASK("phiTask"),

	/** phiResreq */
	PHIRESREQ("phiResreq"),
	
	/** The DATALAYER. */
	DATALAYER("datalayer"),
	
	/** The DATALAYER. */
	DATALAYER_SOURCE("datalayersource"),
	
	/** The DATALAYER. */
	DATA_SOURCE("datasource"),
	
	// LDDRS-648
	/** Create incident message */
	CREATE_INCIDENT("createIncident"),
	
	SIMPLE_REPORT("sr"),
	
	DAMAGE_REPORT("dr"),
	
	/**
	 * Incident management message type; new/update incident messages 
	 * and incident batch messages
	 */
	INCIDENT_MANAGEMENT("imgmt"),
	// END LDDRS-648
	
	/** The FOLDER. */
	FOLDER("folder"),
	
	/** REMOVE */
	REMOVE("remove"),
	
	/** The SYSTEM. */
	SYSTEM("sys"),
	
	BATCH("batch"),
	
	INVITATION("invite"), 

	SOCIAL("social"),
	
	ROOMLOCK("room-lock");

	private String name;

	private SADisplayMessageType(String string) {
		this.name = string;
	}
	
	public String toString(){
		return this.name;
	}
	
	
	
}
