package com.ccc.users.test.ldap;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.db.client.DBUserClient;
import com.ccc.users.db.store.DBUserStore;
import com.ccc.users.db.store.UserManager;
import com.ccc.users.ldap.client.LDAPUserClient;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@TransactionConfiguration
@Transactional
public class DBTests  extends AbstractTransactionalJUnit4SpringContextTests {
	
	
	
	@Test
	public void testAddUser() {
		BasicUser testUser=testUser();
		dBUserClient.addUser(testUser);
		BasicUser dbUser=dBUserClient.userForName("test");
		Assert.notNull(dbUser, "User was null");
		dBUserClient.deleteUser("test");
		dbUser=dBUserClient.userForName("test");
		Assert.isTrue(dbUser==null);
		
		
	}
	@Test
	public void testAddGroups() {
		
		BasicUser dbUser=dBUserClient.userForName("test");
		if(dbUser==null) dBUserClient.addUser(testUser());
		dbUser=dBUserClient.userForName("test");
		String[] groups={"test1","test2"};
		for(String s : groups) {
			
			dBUserClient.addUserToGroup(dbUser, s);
			dbUser=dBUserClient.userForName("test");
		}
		dbUser=dBUserClient.userForName("test");
		Assert.isTrue(dbUser.getGroups().equals("test1,test2"));
		
	}
	
	@Test
	public void testChangePassword() {
		BasicUser testUser=testUser();
		dBUserClient.addUser(testUser);
		Assert.isTrue(dBUserClient.isAuth("test", "test"));
		dBUserClient.changePassword("test", "test2");
		Assert.isTrue(dBUserClient.isAuth("test", "test2"));
	

	}
	
	
	@Before
	public void init() {
		String userName="test";
		BasicUser u=dBUserClient.userForName(userName);
		if(u!=null) dBUserClient.deleteUser(userName);
	
		
	}
	
	@After
	public void tearDown() {
		String userName="test";
		BasicUser u=dBUserClient.userForName(userName);
		if(u!=null) dBUserClient.deleteUser(userName);
		
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
		ret.setEmail("test@clevercloudcomputing.com");
		ret.setPassword("test");
		ret.setFirstName("test");
		ret.setLastName("test");
		ret.setGender("M");
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");
		String dateFromat=format.format("1989-11-07");
		Date d=Date.valueOf(dateFromat);
		ret.setBirthday(d);
		ret.setPhoneNumber("906-231-1820");
		ret.setUserName("test");
		ret.setEnabled(true);
		return ret;
	}
	public void testAuthentication() {
		System.out.println("Tested users.");
	}




	public UserManager getUserManager() {
		return userManager;
	}
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}
	
	public DBUserClient getdBUserClient() {
		return dBUserClient;
	}
	public void setdBUserClient(DBUserClient dBUserClient) {
		this.dBUserClient = dBUserClient;
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
	

	public DBUserStore getDbUserStore() {
		return dbUserStore;
	}


	public void setDbUserStore(DBUserStore dbUserStore) {
		this.dbUserStore = dbUserStore;
	}


	@Autowired
	private UserManager userManager=null;
	@Autowired
	private DBUserClient dBUserClient=null;
	@Autowired
	@Qualifier("dBUserStore")
	private DBUserStore dbUserStore=null;
	
	@Autowired
	private LdapShaPasswordEncoder encoder;
	@Autowired
	private SaltSource saltSource;
}
