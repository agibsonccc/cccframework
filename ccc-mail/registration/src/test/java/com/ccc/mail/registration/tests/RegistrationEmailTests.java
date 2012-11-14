package com.ccc.mail.registration.tests;

import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.mail.registration.ConfirmMailSender;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class RegistrationEmailTests extends AbstractJUnit4SpringContextTests {

	
	@Test
	public void sendConfirmationEmailTests() {
		try {
			confirmMailSender.sendConfirmationEmail("agibson@clevercloudcomputing.com",headersForCCC(),false);
		} catch (AddressException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		
	}
	
	private Map<String,String> headersForCCC() {
		Map<String,String> testHeaders = new HashMap<String,String>();
		testHeaders.put(MailClient.SUBJECT,"Registration confirmation!");
		testHeaders.put(MailConstants.USER_NAME, "joe@fxservices.co.uk");
		testHeaders.put(MailClient.IS_AUTH, "true");
		testHeaders.put(MailClient.IS_SSL, "true");
		testHeaders.put(MailConstants.PASSWORD,"jose1122");
		testHeaders.put(MailClient.PORT,"465");
		testHeaders.put(MailConstants.SSL_FALLBACK, "true");
		testHeaders.put(MailConstants.TLS,"false");
		testHeaders.put(MailClient.FROM_ADDRESS, "joe@fxservices.co.uk");
		return testHeaders;
	}

	public ConfirmMailSender getConfirmMailSender() {
		return confirmMailSender;
	}


	public void setConfirmMailSender(ConfirmMailSender confirmMailSender) {
		this.confirmMailSender = confirmMailSender;
	}


	@Autowired
	private ConfirmMailSender confirmMailSender;
}
