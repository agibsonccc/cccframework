package com.ccc.mail.core.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.AddressException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.mail.ssl.exceptions.SSLErrorException;
import com.ccc.mail.tests.utils.TestUtils;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class MailCoreTests extends AbstractJUnit4SpringContextTests implements MailConstants{
	@Test
	public void testReceiveMail() throws MessagingException, SSLErrorException{
		Map<String,String> headers = new HashMap<String,String>();
		headers.put(USER_NAME,"abamra");
		headers.put(PASSWORD, "adsingh");
		headers.put(ATTACHMENTS,"Hello");
		headers.put(SUBJECT,"New Mail" );
		headers.put(TO_ADDRESSES,"abamra@clevercloudcomputing.com");
		headers.put(PORT,"465");
		headers.put(TO_ADDRESSES,"ccc-board@clevercloudcomputing.com");
		headers.put(IS_SSL,"true"); 
		headers.put(MailConstants.SSL_FALLBACK,"true");
		Session s=mailClient.login(headers, incoming);

		Folder inbox=mailClient.getOpenFolder("INBOX", incoming, headers,true);
		
		Message[] messages=inbox.getMessages();
		Assert.notNull(messages);
		Assert.isTrue(messages.length > 0);
	}
	@Test
	public void testFolders() throws MessagingException, SSLErrorException {
		List<Folder> f=mailClient.foldersForServer(incoming, testUtils.headersForCCC());

		for(Folder f1 : f) {
			Assert.isTrue(f1.exists());


		}
	}



	public Server getIncoming() {
		return incoming;
	}


	public void setIncoming(Server incoming) {
		this.incoming = incoming;
	}


	public SMTPServer getOutgoing() {
		return outgoing;
	}


	public void setOutgoing(SMTPServer outgoing) {
		this.outgoing = outgoing;
	}

	public MailClient getMailClient() {
		return mailClient;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public TestUtils getTestUtils() {
		return testUtils;
	}

	public void setTestUtils(TestUtils testUtils) {
		this.testUtils = testUtils;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	@Resource(name="mailingListMailClient")
	private MailClient mailClient;
	@Autowired
	private SMTPServer outgoing;
	@Qualifier("cccIn")
	@Autowired
	private Server incoming;

	private String userName=null;



	private String password=null;
	@Autowired
	private TestUtils testUtils;
}
