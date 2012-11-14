package com.ccc.mail.impl;

import com.ccc.mail.core.client.BaseMailClient;
import com.ccc.mail.core.mailbox.MailBox;
import com.ccc.mail.core.mailstore.MailStore;

public class DefaultMailClient extends BaseMailClient {

	public DefaultMailClient(){}
	
	public DefaultMailClient(MailBox mailBox, MailStore store) {
		super(mailBox,store);
	}

}
