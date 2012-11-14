package com.ccc.mail.configure.mailet;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccc.mail.mailet.BounceTracker;
import com.ccc.mail.mailet.SendTracker;
import com.ccc.mail.mailinglist.dao.BounceRateManager;
import com.ccc.mail.mailinglist.dao.ListMessageTrackingManager;
import com.ccc.mail.mailinglist.dao.MessageSendManager;
import com.ccc.mail.mailinglist.james.matcher.MailingListAddressMatcher;
import com.ccc.mail.mailinglist.services.api.MailingListService;

@Component
public class AnalyticsMailConfigurer {

	@PostConstruct
	public void init() {
		BounceTracker.setBounceRateManager(bounceRateManager);
		SendTracker.setMessageSendManager(messageSendManager);
		SendTracker.setTracker(listMessageTrackingManager);
		if(log.isDebugEnabled()) {
			log.debug("Setup mailets");
		}
		MailingListAddressMatcher.setMailingListService(mailingListService);
	}
	@Autowired
	private MessageSendManager messageSendManager;
	@Autowired
	private BounceRateManager bounceRateManager;
	@Autowired
	private ListMessageTrackingManager listMessageTrackingManager;
	@Autowired
	private MailingListService mailingListService;
	private static Logger log=LoggerFactory.getLogger(AnalyticsMailConfigurer.class);
}
