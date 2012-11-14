package com.ccc.mail.mailinglist.dao;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.MessageUrlTracking;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("messageUrlTrackingManager")
public class MessageUrlTrackingManager extends GenericManager<MessageUrlTracking> {

	public MessageUrlTrackingManager() {
		super(MessageUrlTracking.class);
	}
}
