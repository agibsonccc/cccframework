package com.ccc.mail.core.servers;

import com.ccc.mail.core.servers.storage.MailConstants;
/**
 * This is a client side view of a pop server.
 * @author Adam Gibson
 *
 */
public class POP3Server extends BaseServer {
	/**
	 * This default constructor will make the assumption of
	 * localhost for mail and the unencrypted pop port.
	 */
	public POP3Server() {
		this(MailConstants.UNENCRYPTED_DEFAULT_POP_PORT,"localhost");
	}
	
	/**
	 * This sets the port the pop server listens on and the host the server is on.
	 * @param port the port the pop server listens on
	 * @param host the host the server listens on.
	 */
	public POP3Server(int port,String host) {
		this.setServerType(MailConstants.IMAP_SERVER);
		this.setPort(port);
		this.setServerAddress(host);
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 3741198276663997504L;

}//end POP3Server
