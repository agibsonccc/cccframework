package com.ccc.mail.mailinglists.tests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.mail.mailinglist.mailclient.MailingListMailClient;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.model.Subscriber;
import com.ccc.mail.mailinglist.services.api.MailingListService;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class MailingListTests extends AbstractJUnit4SpringContextTests implements MailConstants {
	@Test
	@Transactional
	public void testPersist() {
		MailingList list = new MailingList();
		list.setMailingAddress("testlist@clevercloudcomputing.com");
		list.setOwner("agibson");
		list.setName("testlist");
		String[] emails={"agibson@clevercloudcomputing.com","aegibson@mtu.edu","destrotroll@gmail.com"};
		Set<Subscriber> save = new HashSet<Subscriber>();

		for(String s : emails) {
			Subscriber subscriber = new Subscriber();
			subscriber.setEmail(s);
			save.add(subscriber);
		}
		list.setSubscribers(save);
		Assert.isTrue(mailingListService.addMailingList(list), "failed to add list");

		





	}

	@Test
	public void testSend() {
		Map<String,String> headers=headersForCCC();
		List<MailingList> first=mailingListService.allMailingLists();
		MailingList firstlist=first.get(0);
		mailClient.sendToMailingList(headers, firstlist,false);
	}
	private Map<String,String> headersForCCC() {
		Map<String,String> testHeaders = new HashMap<String,String>();
		testHeaders.put(MailClient.SUBJECT,"Registration confirmation!");
		testHeaders.put(MailConstants.USER_NAME, "agibson");
		testHeaders.put(MailClient.IS_AUTH, "true");
		testHeaders.put(MailClient.IS_SSL, "true");
		testHeaders.put(MailConstants.PASSWORD,"destrotroll%5");
		testHeaders.put(MailClient.PORT,"465");
		testHeaders.put(MailConstants.SSL_FALLBACK, "true");
		testHeaders.put(MailConstants.TLS,"false");
		testHeaders.put(MailClient.FROM_ADDRESS, "agibson@clevercloudcomputing.com");
		return testHeaders;
	}

	public MailingListMailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailingListMailClient mailClient) {
		this.mailClient = mailClient;
	}

	public MailingListService getMailingListService() {
		return mailingListService;
	}

	public void setMailingListService(MailingListService mailingListService) {
		this.mailingListService = mailingListService;
	}

	@Autowired
	private MailingListMailClient mailClient;
	@Autowired
	private MailingListService mailingListService;
}
