package com.ccc.jndi.fetcher.tests;

import javax.naming.Context;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.jndi.context.utils.TestPojo;
import com.ccc.jndi.fetcher.filesystem.FileSystemContextFetcher;
import com.ccc.jndi.fetcher.ldap.LDAPContextFetcher;
import com.ccc.jndi.utils.JNDIUtils;

public class ContextFetcherTests extends TestCase  {
	@Test
	public void testLDAP() {
		LDAPContextFetcher fetcher = new LDAPContextFetcher();
		fetcher.setBindDn(userName);
		fetcher.setBindPassword(pass);
		fetcher.setUrl(url);
		fetcher.setClassName("com.ccc.jndi.context.utils.TestPojoObjectFactory");
		Context context=fetcher.fetch();
		Assert.notNull(context,"Context was null");
		
	}
	@Test
	public void testFile() {
		FileSystemContextFetcher fetcher = new FileSystemContextFetcher();
		fetcher.setContextName("testing");
		fetcher.setClassName("com.ccc.jndi.context.utils.TestPojoObjectFactory");

		Context context=fetcher.fetch();
		Assert.notNull(context);
	}
	
	@Test
	public void testLDAPObject() throws NamingException {
		LDAPContextFetcher fetcher = new LDAPContextFetcher();
		fetcher.setBindDn(userName);
		fetcher.setBindPassword(pass);
		fetcher.setUrl(url);
		fetcher.setClassName("com.ccc.jndi.context.utils.TestPojoObjectFactory");
		Context context=fetcher.fetch();
		if(JNDIUtils.nameBound(context,"cn=testing"))
				Assert.isTrue(JNDIUtils.quietDelete(context, "cn=testing"));
		Assert.notNull(context,"Context was null");
		context.bind("cn=testing", new TestPojo("testing"));
		TestPojo lookup=(TestPojo) context.lookup("cn=testing");
		Assert.notNull(lookup,"Look up was null");
		Assert.isTrue(lookup.getMessage().equals("testing"));
		context.removeFromEnvironment("testing");
	}
	@Test
	public void testFileObject() throws NamingException {
		FileSystemContextFetcher fetcher = new FileSystemContextFetcher();
		fetcher.setContextName("testing");
		fetcher.setClassName("com.ccc.jndi.context.utils.TestPojoObjectFactory");

		Context context=fetcher.fetch();
		Assert.notNull(context);
		
		if(JNDIUtils.nameBound(context,"cn=testing"))
				Assert.isTrue(JNDIUtils.quietDelete(context, "testing"));
		TestPojo lookup=(TestPojo) context.lookup("testing");
		Assert.notNull(lookup,"Look up was null");
		Assert.isTrue(lookup.getMessage().equals("testing"));
		context.removeFromEnvironment("testing");
	}
	
	private String userName="cn=manager";
	
	private String pass="c^31Da%";
	
	private String url="ldap://clevercloudcomputing.com:389/ou=adminobjects,o=amq,dc=clevercloudcomputing,dc=com";
	
}
