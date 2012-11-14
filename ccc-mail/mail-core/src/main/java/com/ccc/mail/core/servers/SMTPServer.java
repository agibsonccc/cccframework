package com.ccc.mail.core.servers;

import com.ccc.mail.core.servers.storage.MailConstants;

public class SMTPServer extends BaseServer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7830611782085853838L;

	/**
	 * This default constructor will make the assumption of
	 * localhost for mail and the unencrypted pop port.
	 */
	public SMTPServer() {
		this(MailConstants.UNENCRYPTED_DEFAULT_SMTP_PORT,"localhost");
	}
	
	/**
	 * This sets the port the pop server listens on and the host the server is on.
	 * @param port the port the pop server listens on
	 * @param host the host the server listens on.
	 */
	public SMTPServer(int port,String host) {
		this.setServerType(MailConstants.SMTP_SSL);
		this.setPort(port);
		this.setServerAddress(host);
		
	}
	

	
}
