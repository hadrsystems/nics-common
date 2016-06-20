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

public final class SADisplayConstants {
	
	//CONTACT TYPES
	public static final String EMAIL_TYPE = "email";
	public static final String PHONE_HOME_TYPE = "phone_home";
	public static final String PHONE_CELL_TYPE = "phone_cell";
	public static final String PHONE_OTHER_TYPE = "phone_other";
	public static final String PHONE_OFFICE_TYPE = "phone_office";
	public static final String RADIO_NUMBER_TYPE = "radio_number";
	public static final String TYPE = "type";
	public static final String CONTACT_TYPE_ID = "contacttypeid";
	
	public static final int EMAIL_TYPE_ID=0;
	
	//USER ROLES
	public static final String ADMIN_ROLE = "admin";
	public static final String SUPER_ROLE = "super";
	public static final String USER_ROLE = "user";
	public static final String READ_ONLY_ROLE = "readOnly";
	
	//USER ROLES
	public static final int SUPER_ROLE_ID = 0;
	public static final int USER_ROLE_ID = 1;
	public static final int READ_ONLY_ROLE_ID = 2;
	public static final int GIS_ROLE_ID = 3;
	public static final int ADMIN_ROLE_ID = 4;
	
	//TABLE NAMES
	public static final String USERSESSION_TABLE = "Usersession";
	public static final String CURRENT_USERSESSION_TABLE = "CurrentUserSession";
	public static final String CHAT_TABLE = "Chat";
	public static final String COLLAB_ROOM_TABLE = "CollabRoom";
	public static final String COLLABROOM_FEATURE = "CollabroomFeature";
	public static final String INCIDENT_TABLE = "Incident";
	public static final String MESSAGE_ARCHIVE_TABLE = "MessageArchive";
	public static final String SEAM_USER_TABLE = "SeamUser";
	public static final String USER_ORG_TABLE = "UserOrg";
	public static final String ORG_ORGTYPE_TABLE = "org_orgtype";
	public static final String ORG_ORGTYPE_ID = "org_orgtypeid";
	public static final String ORG_TYPE_TABLE = "OrgType";
	public static final String FEATURE = "Feature";
	public static final String FEATURE_TABLE = "feature";
	public static final String ROOT_FOLDER_TABLE = "Rootfolder";	
	public static final String FOLDER_TABLE = "Folder";
	public static final String CONTACT_TYPE_TABLE = "ContactType";
	public static final String CONTACT_TABLE = "contact";
	public static final String DATASOURCE_TABLE = "Datasource";;
	public static final String DATALAYER_TABLE = "Datalayer";
	public static final String DATASOURCE_TYPE_TABLE = "Datasourcetype";
	public static final String SYSTEM_ROLE_TABLE = "SystemRole";
	public static final String COLLAB_ROOM_PERMISSION_TABLE = "CollabroomPermission";
	public static final String DATALAYER_FOLDER_TABLE = "Datalayerfolder";
	public static final String FORM_TABLE = "Form";
	public static final String FORM_TYPE_TABLE = "FormType";
	public static final String ORG_TABLE = "Org";
	public static final String LOG_TABLE = "Log";
	public static final String ORG_FOLDER_TABLE = "OrgFolder";
	public static final String MESSAGE_PERMISSIONS_TABLE = "MessagePermissions";
	public static final String DATALAYER_SOURCE_TABLE = "Datalayersource";
	public static final String USER_FEATURE = "UserFeature";
	public static final String INCIDENT_TYPE_TABLE = "incidenttype";
	public static final String INCIDENT_INCIDENTTYPE_TABLE = "incident_incidenttype";
	public static final String USER_ORG_WORKSPACE_TABLE = "userorg_workspace";
    public static final String UXOREPORT_TABLE = "Uxoreport";
	
	//COMMON COLUMN NAMES
	public static final String CREATED = "created";

	//CHAT COLUMNS
	public static final String CHAT_ID = "chatid";
	
	//COLLAB ROOM COLUMNS
	public static final String COLLAB_ROOM_ID = "collabroomid";
	public static final String COLLAB_ROOM_NAME = "name";
	
	//INCIDENT COLUMNS
	public static final String INCIDENT_NAME = "incidentName";
	public static final String INCIDENT_ID = "incidentId";
	public static final String PARENT_INCIDENT_ID = "parentincidentid";
	public static final String ACTIVE = "active";
	public static final String FOLDER = "folder"; // LDDRS-648
	
	//USER COLUMNS
	public static final String USER_ID = "userId";
	public static final String USER_NAME = "username";
	public static final String ENABLED = "enabled";
	
	//MESSAGE ARCHIVE COLUMNS
	public static final String MESSAGE_TYPE = "messageType";
	public static final String TOPIC = "topic";
	public static final String INSERTED_TIME_STAMP = "insertedTimestamp";
	
	//CURRENT USERSESSION CONSTANTS
	public static final String DISPLAY_NAME = "displayname";
	public static final String LOGGED_IN = "loggedin";
	
	//USERSESSION CONSTANTS
	public static final String USERSESSION_ID ="usersessionid";
	public static final String SESSION_ID = "sessionid";
	
	//DATASOURCE TYPE CONSTANTS
	public static final String DATASOURCE_TYPE_NAME = "typename";
	public static final String DATASOURCE_INTERNAL_URL = "internalurl";
	
	//ORGANIZATION CONSTANTS
	public static final String ORG_ID = "orgId";
	
	//USER ORG WORKSPACE COLUMN NAMES
	public static final String USER_ORG_WORKSPACE_ID = "userorg_workspace_id";
	public static final String DEFAULT_ORG = "defaultorg";
	
	//FOLDER COLUMN NAMES
	public static final String FOLDER_ID = "folderid";
	public static final String PARENT_FOLDER_ID = "parentfolderid";
	
	//DATALAYER COLUMN NAMES
	public static final String DATALAYER_ID = "datalayerid";
	
	//SYSTEM ROLE COLUMN NAMES
	public static final String SYSTEM_ROLE_NAME = "rolename";
	public static final String SYSTEM_ROLE_ID = "systemroleid";
	
	//DATALAYER FOLDER COLUMN NAMES
	public static final String INDEX = "index";
	public static final String DATALAYER_FOLDER_ID = "datalayerfolderid";
	
	//FORM COLUMN NAMES
	public static final String FORM_TYPE_NAME = "formtypename";
	public static final String FORM_TYPE_ID = "formtypeid";
	public static final String SEQ_TIME = "seqtime";
		
	// LDDRS-648
	// FORM TYPE CONSTANTS
	public static final String FORM_TYPE_ROC = "ROC";
	public static final String FORM_TYPE_RESC = "RESC";
	public static final String FORM_TYPE_FDNY = "FDNY";
	public static final String FORM_TYPE_SITREP = "SITREP";
	// END LDDRS-648
	
	//LOG COLUMN NAMES
	public static final String LOG_TYPE_ID = "logtypeid";
	public static final String MESSAGE = "message";
	public static final String STATUS = "status";
	public static final String LOG_ID = "logid";
	
	//CONTACT COLUMN NAMES
	public static final String VALUE = "value";
	
	public static final String ORG_TYPE_NAME = "orgtypename";
	public static final String LAYERNAME = "layername";
	public static final String MASTER_MAP = "IncidentMap";
	public static final String MASTER_MAP_ROLE = "masterMapRole";
	public static final String WORKING_MAP = "WorkingMap";
	
	public static final String NICS_TOPIC = "NICS.ws.1.";
	public static final String NEW_USER_TOPIC = NICS_TOPIC + "user.new";
	
	public static final String NEW_USER_MSG_TYPE = "newuser";
	public static final String DATALAYER_SOURCE_ID = "datalayersourceid";
	public static final String DATASOURCE_ID = "datasourceid";
	public static final String DATASOURCE_TYPE_ID = "datasourcetypeid";
	public static final String INCIDENT_TYPE_ID = "incidenttypeid";
	public static final String USER_ESCAPED = "\"user\"";
	public static final String FEATURE_ID = "featureId";
	public static final String USER_ORG_ORG_ID = "userorg.orgid";
	public static final String ID = "id";
	
	/**Properties returned to the UI for displaying the datalayers*/
	public static final String TABNAME = "tabname";
	public static final String TITLE = "title";
	public static final String LAYER = "layer";
	public static final String URL = "url";
	public static final String LAYER_TYPE = "layerType";
	public static final String CRS = "crs";
	public static final String BASE_LAYER = "baselayer";
	public static final String FORMAT = "format";
	public static final String STYLE = "style";
	public static final String OPACITY = "opacity";
	public static final String REFRESH_RATE ="refreshrate";
	public static final String STYLE_PATH = "stylepath";
	public static final String ATTRIBUTES = "attributes";
	public static final String WMTS = "WMTS";
	public static final String PASSWORD_HASH = "passwordhash";
	public static final String PASSWORD_ENCRYPTED = "passwordencrypted";
	public static final String FOLDER_MANAGEMENT_FEATURE = "folderManagement";
	public static final int ALERT_LOGTYPE_ID = 1;
	public static final String CURRENT_USERSESSION_ID = "currentusersessionid";
	public static final String LAST_SEEN = "lastseen";
	public static final String USER_SESSION_TABLE = "usersession";
	public static final String USER_ORG_ID = "userorgid";
	public static final String CURRENT_USERSESSION_SEQ = "current_user_session_seq";
	public static final String USERSESSION_SEQ = "user_session_seq";
	public static final String USER_ORG_WORKSPACE_ENABLED = "userorg_workspace.enabled";
	public static final String OTHER_ENABLED = "other_enabled";
	public static final String PRODUCTION = "production";
	public static final String TRAINING = "training";
	public static final String ARCHIVED = "/Archived/";
	public static final String EMPTY_STRING = "";
	public static final String ORG_NAME = "name";
	public static final String ORG_TYPE_ID = "orgtypeid";
	public static final String PREFIX = "prefix";
	public static final String COLLABROOM_AND_NAME = "collabroom.name";
	public static final String FIRSTNAME = "firstname";
	public static final String LASTNAME = "lastname";
	public static final String SECURE_INCIDENT_MAP = "secureIncidentMap";
	public static final String MESSAGE_SEQUENCE = "message_sequence";
	public static final String TYPE_NAME = "typename";
	public static final String FOLDER_NAME = "foldername";
	public static final String ORG_FOLDER_ID = "orgfolderid";
	public static final Object LOG_CREATED = "log.created";
	public static final int ANNOUNCEMENTS_LOG_TYPE = 0;
	public static final String CONTACT_SEQUENCE = "contact_seq";
	public static final String CONTACT_ID = "contactid";
	public static final String CONTACT_ENABLED = "contact.enabled";
	public static final String CURRENT_USERSESSION_USER_ID = "currentusersession.userid";
	//public static final String ORG_FORM_TYPE_TABLE = "orgformtype";
	public static final String DISTRIBUTED = "distributed";
	public static final String FORM_ID = "formid";
	public static final String SEQ_NUM = "seqnum";
	public static final String DOCUMENT_TABLE = "document";
	public static final String DOCUMENT_INCIDENT_TABLE = "document_incident";
	public static final String DOCUMENT_FEATURE_TABLE = "document_feature";
	public static final String DOCUMENT_FEATURE_ID = "document_featureid";
	public static final String DOCUMENT_ID = "documentid";
	public static final String FILENAME = "filename";
	public static final String FILETYPE = "filetype";
	public static final String GLOBAL_VIEW = "globalview";
	public static final String DISTRIBUTION = "distribution";
	public static final String LONGITUDE = "lon";
	public static final String LATITUDE = "lat";
	public static final String INCIDENT_SEQUENCE_TABLE = "incident_seq";
	public static final String INCIDENT_INCIDENTTYPE_ID = "incident_incidenttypeid";
	public static final String HIBERNATE_SEQUENCE_TABLE = "hibernate_sequence";
	public static final String COLLAB_ROOM_SEQUENCE_TABLE = "collab_room_seq";
	public static final String LAST_UPDATED = "lastupdated";
	public static final String USER_SEQUENCE_TABLE = "user_seq";
	public static final String CONTACT_SEQUENCE_TABLE = "contact_seq";
	public static final String USER_ORG_SEQUENCE_TABLE = "user_org_seq";
	public static final String COLLABROOM_FEATURE_TABLE = "collabroomfeature";
	public static final String COLLAB_ROOM_FEATURE_ID = "collabroomfeatureid";
	public static final String USER_FEATURE_TABLE = "userfeature";
	public static final String COLLAB_ROOM_PERMISSION_ID = "collabroompermissionid";
	public static String EXISTING_SESSION_MSG = "User has an existing session. Removing...";
	public static String REMOVE_SESSION_SUCCESS = "Removed pre-existing session(s)";
	public static String REMOVE_SESSION_FAILURE = "No pre-existing sessions to remove";
	public static String DEFAULT_REMOVE_MESSAGE = "Your account has been logged into from another location.";
	public static int DEFAULT_SESSION_ID = -1;
	public static final String COLLAB_ROOM_DAO = "collabroomDao";
	public static final String DATA_LAYER_DAO = "datalayerDao";
	public static final String DOCUMENT_DAO = "documentDao";
	public static final String FEATURE_DAO = "featureDao";
	public static final String FOLDER_DAO = "folderDao";
	public static final String FORM_DAO = "formDao";
	public static final String INCIDENT_DAO = "incidentDao";
	public static final String LOG_DAO = "logDao";
	public static final String MESSAGE_ARCHIVE_DAO = "messageArchiveDao";
	public static final String ORG_DAO = "orgDao";
	public static final String SYSTEM_ROLE_DAO = "systemroleDao";
	public static final String USER_DAO = "userDao";
	public static final String USER_ORG_DAO = "userOrgDao";
	public static final String USER_SESSION_DAO = "userSessionDao";
	public static final String TASKING_DAO = "taskingDao";
	public static final String FEATURES = "features";
	public static final String DEFAULT_LAT = "defaultlatitude";
	public static final String DEFAULT_LON = "defaultlongitude";
	public static final String INCIDENT_TYPE_NAME = "incidenttypename";
	public static final String STATE = "state";
	public static final String COUNTRY = "country";
	public static final String IMAGE_FORMAT = "imageformat";
	public static final String NATIVE_PROJECTION = "nativeprojection";
	public static final String TILE_GRID_SET = "tilegridset";
	public static final String TILE_SIZE = "tilesize";
	public static final String EXTERNAL_URL = "externalurl";
	public static final String INTERNAL_URL = "internalurl";
	public static final String ROOT_FOLDER_ID = "rootid";
	public static final String USER_FEATURE_ID = "userfeatureid";
	public static final String LAST_UPDATE = "lastupdate";
	public static final String UNIT = "unit";
	public static final String DESCRIPTION = "description";
	public static final String RANK = "rank";
	public static final String JOB_TITLE = "jobtitle";
	public static final String WORKSPACE_TABLE = "workspace";
	public static final String WORKSPACE_ID = "workspaceid";
	public static final String WORKSPACE_NAME = "workspacename";
	public static final String WORKSPACE_DAO ="workspaceDao";
	public static final String ROOTFOLDER_WORKSPACEID = "rootfolder.workspaceid";
	public static final String LOG_WORKSPACE_TABLE = "log_workspace";
	public static final String LOG_SEQ = "log_seq";
	public static final String LOG_WORKSPACE_ID = "log_workspace_id";
	public static final String SYSTEM_ROLE_WORKSPACE_TABLE = "systemrole_workspace";
	public static final Object ARCGIS_CACHE = "ARCGISCACHE";
	public static final String DATALAYERFOLDER_INDEX = "datalayerfolder.index";
	public static final String ENABLE_LOGIN = "enablelogin";
	public static final String USER_ENABLED = USER_ESCAPED + ".enabled";
	public static final String USER_ENABLED_PARAM = "user_enabled";
	public static final String PASSWORD_CHANGED = "passwordchanged";
	public static final String SYSTEM_WORKSPACE = "system_workspace";
	public static final String SYSTEM = "nicssystem";
	public static final String SYSTEM_ID = "systemid";
	public static final String SYSTEM_NAME = "systemname";
	public static final String SYSTEM_TABLE = "nicssystem";
	public static final String WORKSPACE_ENABLED = "workspace.enabled";
	public static final String SYSTEM_ENABLED = "nicssystem.enabled";
	public static final String BOUNDS = "bounds";
	public static final String FEATURE_COLLABROOM = "collabroomfeature";
	public static final String FEATURE_USER = "userfeature";
	public static final String DELETED = "deleted";
	public static final String DEFAULT = "default";
	public static final String USER_TABLE = "\"user\"";
	public static final String COUNTY = "county";
	public static final String LEGEND = "legend";
    public static final String UXOREPORT_LOCATION = "location";
    public static final String UXOREPORTID = "uxoreportid";
	public static final String INCIDENT_MAP = "Incident Map";
	public static final String PASSWORD = "password";
	public static final String INCIDENT_CREATED = "incident.created";
	public static final Object TRACKING = "Tracking";
	public static final Object DATALAYER_DISPLAYNAME = "datalayer.displayname";
}
