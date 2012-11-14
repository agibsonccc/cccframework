package com.ccc.mail.core.mailbox;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;


public class MailServerAuthenticator extends Authenticator {
	public MailServerAuthenticator(String userName2, String password2) {
		username=userName2;
		password=password2;
	}

	public PasswordAuthentication getPasswordAuthentication() {




		return new PasswordAuthentication(username, password);
	}

	private String username, password;

}
