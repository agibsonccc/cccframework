package com.ccc.users.test.ldap;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ccc.springclient.core.SpringContext;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;
import com.ccc.users.ldap.client.LDAPUserClient;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class LDAPTest  extends AbstractJUnit4SpringContextTests {

	@Before
	public void init() {
		//List<BasicUser> users=ldapUserClient.retrieveUsers();
		
		BasicUser u=testUser();
		//ldapUserClient.addUser(u);
	//	ldapUserClient.changePassword("ccc", "destrotroll5");
		u=ldapUserClient.userForName("ccc");
		System.out.println(u.getPassword());
		System.out.println(ldapUserClient.isAuth("ccc", "c^3AI"));
		System.out.println();
	}


	@Test
	//@Ignore
	public void testAddUser() {

		List<BasicUser> users=ldapUserClient.retrieveUsers();
		Assert.notNull(ldapUserClient);
		ldapUserClient.addUser(testUser());

	}
	@Test
	@Ignore
	public void testUserLookup() {

		BasicUser u=ldapUserClient.userForName("test");
		if(u==null)
			ldapUserClient.addUser(testUser());
		Assert.notNull(u);
		Assert.isTrue("test".equals(u.getUserName()));
		Assert.notNull(u.getGroups());

		List<BasicUser> users=ldapUserClient.retrieveUsers();
		Assert.notNull(users);
		Assert.isTrue(users.size() > 0);
		u=users.get(0);
		Assert.notNull(u);
		System.out.println(u);
		Assert.isTrue(users.contains(u));
	}




	@Test
	@Ignore
	public void testUpdateUser() {
		LDAPUserClient ldapUserClient=getLdapUserClient();
		if(ldapUserClient==null)
			ldapUserClient=(LDAPUserClient) cache.get("userClient");
		Assert.notNull(ldapUserClient);
		BasicUser u=ldapUserClient.userForName("test");
		Assert.notNull(u);
		u.setEmail("test2@clevercloudcomputing.com");
		ldapUserClient.modifyUser(u);
	}

	@Test
	@Ignore
	public void testChangePassword(){
		BasicUser u=ldapUserClient.userForName("test");
		ldapUserClient.changePassword("test", "test");
		Assert.isTrue(ldapUserClient.isAuth("test", "test"));
	}

	@Test
	@Ignore
	public void testAddGroup() {
		BasicUser u=ldapUserClient.userForName("test");
		ldapUserClient.addUserToGroup(u, "test");
	}
	@Test
	@Ignore
	public void testGroupLookup() {
		List<String> groups=ldapUserClient.allGroups();
		Assert.notNull(groups);
		Assert.notEmpty(groups);

	}
	@Test
	@Ignore
	public void testGroupMembers() {
		List<BasicUser> members=ldapUserClient.usersInGroup("test");
		Assert.notNull(members);
		Assert.notEmpty(members);
		BasicUser u=members.get(0);
		Assert.notNull(u);
	}
	@Test
	@Ignore
	public void testLoadedGroups() {
		BasicUser u=ldapUserClient.userForName("test");
		Assert.notNull(u);
		String groups=u.getGroups();
		Assert.notNull(groups);
		Assert.hasLength(groups);
		Collection<GrantedAuthority> authz=u.getAuthorities();

		Assert.isTrue("test".equals(u.getGroups()));
	}
	@Test
	@Ignore
	public void deleteUserFromGroup() {
		List<BasicUser> members=ldapUserClient.usersInGroup("test");
		Assert.notNull(members);
		Assert.notEmpty(members);
		BasicUser u=members.get(0);
		Assert.notNull(u);
		Assert.isTrue(u.getUsername().equals("test"));
		ldapUserClient.deleteUserFromGroup(u, "test");
		u=ldapUserClient.userForName("test");
		Assert.isTrue(u.getGroups()==null || u.getGroups().isEmpty());
	}
	@Test
	@Ignore
	public void testDeleteGroup() {
		ldapUserClient.deleteGroup("test");

	}

	@Test
	@Ignore
	public void testDeleteUser() {

		BasicUser u=ldapUserClient.userForName("test");
		Assert.notNull(u);
		ldapUserClient.deleteUser("test");
		u=ldapUserClient.userForName("test");
		Assert.isTrue(u==null);
	}

	public UserContact testContact() {
		UserContact ret = new UserContact();
		ret.setContactFor("test");
		ret.setEmail("test@clevercloudcomputing.com");
		ret.setUserName("test");
		return ret;
	}
	public BasicUser testUser()  {
		BasicUser ret = new BasicUser();
		ret.setCredentialsExpired(false);
		ret.setEmail("ccc@clevercloudcomputing.com");
		ret.setPassword("c^3AI");
		ret.setFirstName("ccc");
		ret.setLastName("ccc");
		ret.setGender("M");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		
		ret.setPhoneNumber("906-231-1820");
		ret.setUserName("ccc");
		ret.setGroups("user");
		ret.setEnabled(true);
		return ret;
	}



	public void testAuthentication() {
		System.out.println("Tested users.");
	}





	private UserSettings testSetting() {
		UserSettings ret = new UserSettings();

		ret.setUserName("test");
		ret.setSettingVal("testval");
		ret.setSettingName("testsetting");
		return ret;
	}
	@Test
	@Ignore
	public void testAddSetting() {
		ldapUserClient.addUserSetting(testSetting());
		List<UserSettings> settings=ldapUserClient.settingsForUser("test");
		Assert.notNull(settings);
		Assert.notEmpty(settings);

	}
	@Test
	@Ignore
	public void updateSetting() {
		UserSettings newSetting=testSetting();

		newSetting.setSettingVal("newval");

		ldapUserClient.updateUserSettings(newSetting);
	}
	@Test
	@Ignore
	public void deleteSetting() {
		ldapUserClient.deleteUserSettings(testSetting());
	}





	public LDAPUserClient getLdapUserClient() {
		return ldapUserClient;
	}

	public void setLdapUserClient(LDAPUserClient ldapUserClient) {
		this.ldapUserClient = ldapUserClient;
	}


	public LdapShaPasswordEncoder getEncoder() {
		return encoder;
	}


	public void setEncoder(LdapShaPasswordEncoder encoder) {
		this.encoder = encoder;
	}


	public SaltSource getSaltSource() {
		return saltSource;
	}


	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}



	@Autowired
	private LDAPUserClient ldapUserClient;
	@Autowired
	private LdapShaPasswordEncoder encoder;
	@Autowired
	private SaltSource saltSource;
	private Map<String,Object> cache = new HashMap<String,Object>();
	private SpringContext newContext;
	private ApplicationContext context;
}
