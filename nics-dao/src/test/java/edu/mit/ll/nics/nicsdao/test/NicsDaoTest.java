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
package edu.mit.ll.nics.nicsdao.test;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import edu.mit.ll.jdbc.JoinRowCallbackHandler;
import edu.mit.ll.nics.common.entity.*;
import edu.mit.ll.nics.nicsdao.impl.FormDAOImpl;
import edu.mit.ll.nics.nicsdao.impl.UserDAOImpl;
import edu.mit.ll.nics.nicsdao.mappers.FormRowMapper;
import edu.mit.ll.nics.nicsdao.mappers.UserRowMapper;



//@ContextConfiguration("file:src/test/config/nics-dao.xml")
public class NicsDaoTest {
	
	private DriverManagerDataSource dataSource;
		
	@BeforeTest
	public void beforeTest() {
		System.out.println("BeforeTest!");
		
		initDataSource();
		
		// Clean up data
		removeTestUserData();
		removeTestFormData();
	}
	
	private void removeTestUserData() {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		
		long userId = -1;
		try {
			userId = template.queryForObject("select userid from public.user where username=:username", 
					new MapSqlParameterSource("username", "nics-dao-test1@ll.mit.edu"), Long.class);
		} catch(Exception e) {
			System.out.println("Failed to get userId, not removing any other test data");
			return;
		}
		System.out.println("\n!!!Retrieve test user ID result: " + userId);
		
		if(userId < 1) {
			System.out.println("Invalid userId: " + userId + ", not removing any further test data");
			return;
		}
		
		int contactResult = template.update("delete from contact where userid=:userid",
				new MapSqlParameterSource("userid", userId));
		System.out.println("\n!!!Remove test Contacts result: " + contactResult);
		
		int userorgResult = template.update("delete from userorg where userid=:userid",
				new MapSqlParameterSource("userid", userId));
		System.out.println("\n!!!Remove userorgs result: " + userorgResult);
		
		int userResult = template.update("delete from public.user where username=:username", 
				new MapSqlParameterSource("username", "nics-dao-test1@ll.mit.edu"));		
		System.out.println("\n!!!Remove test user result: " + userResult);
		
		// TODO: remove contacts and userorg, etc
		
	}
	
	private void removeTestFormData() {
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		
		int formId = -1;
		try {
			formId = template.queryForObject("select formid from form where formid=:formid", 
					new MapSqlParameterSource("formid", 9999), Integer.class);
		} catch(Exception e) {
			System.out.println("Failed to get formId, not removing form!");
			return;
		}
		System.out.println("\n!!!Retrieve test form ID result: " + formId);
				
		
		int formResult = template.update("delete from form where formid=:formid",
				new MapSqlParameterSource("formid", formId));
		System.out.println("\n!!!Remove test Form result: " + formResult);
	}

	public void initDataSource() {
		dataSource = new DriverManagerDataSource();
	    dataSource.setUrl("jdbc:postgresql://localhost:5432/nics");
	    dataSource.setUsername("nics");
	    dataSource.setPassword("nicspassword");
	}
	
	@Test(testName="InsertUser", description="Inserts a user with Contacts, UserOrgs entries", dataProvider="userProvider")
	public void TestInsertUser(User user, List<Contact> contacts, List<UserOrg> userOrgs) {
				
		UserDAOImpl userDao = new UserDAOImpl();				
		//userDao.reInitWithNewDatasource(dataSource);
		userDao.setDataSource(dataSource);
		userDao.initialize();
		
		boolean userStatus = userDao.registerUser(user, contacts, userOrgs, null);
		
		System.out.println("Register User status: " + userStatus);
		
		JoinRowCallbackHandler<User> handler = new JoinRowCallbackHandler<User>(new UserRowMapper());
		
		NamedParameterJdbcTemplate template = new NamedParameterJdbcTemplate(dataSource);
		template.query("select * from public.user where username=:username", 
				new MapSqlParameterSource("username", "nics-dao-test1@ll.mit.edu"), handler);
		User newUser = handler.getSingleResult();
		//User newUser = userDao.getMyUserID(null) //userDao.getUser("nics-dao-test1@ll.mit.edu");
		try {
			System.out.println("newUser: " + ((newUser != null) ? newUser.toJSONObject().toString() : "null"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(newUser != null) {
			Assert.assertEquals(newUser.getUsername(), "nics-dao-test1@ll.mit.edu");
			Assert.assertEquals(newUser.getFirstname(), "Test1First");
		} else {
			Assert.fail("Test user not found in database");
		}
	}
	
	@Test(testName="GetForm", description="Tests the getForm method")
	public void TestGetForm() {
		FormDAOImpl formDaoImpl = new FormDAOImpl();
				
		formDaoImpl.setDataSource(dataSource);
		formDaoImpl.initialize();
		
		Form form = null;
		try {
			form = formDaoImpl.getForm(1,1);
			Assert.assertNotSame(form.getIncidentname(), "testing");
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		Assert.assertNotNull(form);
	}
	
	
	@Test(testName="InsertForm", description="Tests the insertion of a Form", dataProvider="formProvider")
	public void TestInsertForm(Form form) {
		FormDAOImpl formDao = new FormDAOImpl();			
		
		formDao.setDataSource(dataSource);
		formDao.initialize();
		
		Form ret = null;
		try {
			ret = formDao.persistForm(form);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
				
		
		try {			
			Form newForm = formDao.getForm(form.getFormId());
			System.out.println("Insert form result: " + newForm.toJSONObject());
			
			Assert.assertEquals(newForm.getMessage(), form.getMessage());
			Assert.assertEquals(newForm.getDistributed().booleanValue(), form.getDistributed().booleanValue());		
			Assert.assertEquals(newForm.getSeqtime(), form.getSeqtime());
			Assert.assertEquals(newForm.getSeqnum(), form.getSeqnum());
			
			
		} catch(Exception e) {
			System.out.println("Exception querying for test form: " + e.getMessage());
		}		
		
	}
	
	@Test(testName="UpdateForm", description="Tests the updating of a Form", dataProvider="formProvider")
	public void TestUpdateForm(Form form) {
		final String newMessage = "some new form content";
		final long newSeqTime = System.currentTimeMillis();
		final long newSeqNum = 54321;
		final boolean newDistributed = true;
		
		form.setMessage(newMessage);
		form.setSeqtime(newSeqTime);
		form.setSeqnum(newSeqNum);
		form.setDistributed(newDistributed);
		
		FormDAOImpl formDao = new FormDAOImpl();
		formDao.setDataSource(this.dataSource);
		formDao.initialize();
		
		Form ret = null;
		try {
			ret = formDao.persistForm(form);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Update Form response: " + ret);
		Assert.assertEquals(ret.getFormId(), form.getFormId());
		
		Form updatedForm = formDao.getForm(form.getFormId());
		System.out.println("Content of updated Form:\n" + updatedForm.toJSONObject());
		
		Assert.assertEquals(updatedForm.getMessage(), newMessage);
		Assert.assertEquals(updatedForm.getDistributed().booleanValue(), newDistributed);		
		Assert.assertEquals(updatedForm.getSeqtime(), newSeqTime);
		//Assert.assertEquals(updatedForm.getSeqnum(), newSeqNum); // Update is no longer altering seqnum
		
	}
	
	@DataProvider(name="formProvider")
	public Object[][] getInitializedForm() {
		Form form = new Form();
		form.setDistributed(false);
		form.setFormId(9999); // TODO: get actual next form seq val?
		form.setFormtypeid(0);
		form.setIncidentid(1);
		form.setIncidentname("Test Incident");
		form.setMessage("{\"some\":\"someval\", \"fields\":\"fieldsval\"}");
		form.setSeqnum(12345);
		form.setSeqtime(System.currentTimeMillis());
		form.setUsersessionid(1);
		
		
		return new Object[][]{{form}};
	}
	
	@DataProvider(name="userProvider")
	public Object[][] getInitializedUser() {
		User user = new User();
		user.setUsername("nics-dao-test1@ll.mit.edu");
		user.setPasswordHash("TODO: find out where best to put a password that gets hashed");
		user.setFirstname("Test1First");
		user.setLastname("Test1Last");		
		// ...
		
		/*
		 * contacttypeid |     type     
         ------+--------------
             0 | email
             1 | phone_home
             2 | phone_cell
             3 | phone_office
             4 | radio_number
             5 | phone_other

		 */
		List<Contact> contacts = new ArrayList<Contact>();
		Contact contact = new Contact();
		contact.setContacttypeid(0); // email
		contact.setContacttype(new ContactType(0, "email"));
		contact.setValue("test1@ll.mit.edu");
		contacts.add(contact);
		contact = new Contact();
		contact.setContacttypeid(1); // home phone
		contact.setContacttype(new ContactType(1, "phone_home"));
		contact.setValue("(123)456-789");
		contacts.add(contact);
		
		List<UserOrg> userOrgs = new ArrayList<UserOrg>();
		UserOrg userOrg = new UserOrg();
		userOrg.setDescription("Test Description");
		userOrg.setJobTitle("QA Tester");
		userOrg.setOrgid(1); // usually Academia
		userOrg.setSystemrole(new SystemRole(1, "user"));
		userOrg.setSystemroleid(1);
		//userOrg.setUserid(userid); // Leave empty, user gets id in insert step?
		userOrg.setUserorgid(999999); // TODO: leave, or let the dao set it?
		
		return new Object[][]{{user, contacts, userOrgs}};
	}
	
	@AfterTest
	public void afterTest() {
		System.out.println("AfterTest!");
		
	}
}
