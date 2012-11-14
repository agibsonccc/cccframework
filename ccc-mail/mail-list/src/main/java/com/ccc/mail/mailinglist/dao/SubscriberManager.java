package com.ccc.mail.mailinglist.dao;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.Subscriber;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("subscriberManager")
@Component("subscriberManager")
public class SubscriberManager extends GenericManager<Subscriber>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8425368577956882332L;

	public SubscriberManager() {
		super(Subscriber.class);
	}
}
