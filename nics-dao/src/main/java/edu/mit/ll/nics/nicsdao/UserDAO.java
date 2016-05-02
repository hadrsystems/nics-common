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

//import javax.ejb.Local;


import edu.mit.ll.nics.common.entity.Contact;
import edu.mit.ll.nics.common.entity.User;
import edu.mit.ll.nics.common.entity.UserFeature;

//@Local
public interface UserDAO extends BaseDAO {
	public int create(String firstname, String lastname, String username, String password);
	public Long getMyUserID(String username);
    public User getUser(String username);
    public User getUserById(long userId);
    public User getUserBySessionId(long usersessionId);
    public void updateNames(int userId, String firstName, String lastName);
	public List<UserFeature> getUserFeatures(int id, int workspaceId);
	public List<User> getActiveUsers(int workspaceId);
	public List<User> getAllEnabledUsers();
	public List<User> getEnabledUsersInWorkspace(int workspaceId);
	public List<User> getUsers(String orgName);
	public boolean verifyEmailAddress(String username, String email);
	public List<String> getUsersWithPermission(int collabroomid, int roleid);
	public int getNextContactId();
	public int getNextUserId();
	public int getContactTypeId(String type);
	public List<Contact> getContacts(String username, String type);
	public List<Contact> getAllUserContacts(String username);
	public int addContact(String username, int contactTypeId, String value);
	public boolean deleteContact(int contactId);
	public List<User> getUsersNotInOrg(int notInOrgId); 
	public Contact getContact(String value);
	public boolean requiresPasswordChange(int userid, int days);
	public String getUsernameFromEmail(String emailAddress);
	public int setUserEnabled(int userId, boolean enabled);
	public int setUserActive(int userId, boolean active);
	public int isEnabled(String username);
	public long getUserId(String username);
}
