package com.ccc.mail.core.servers;

import com.ccc.mail.core.servers.storage.MailConstants;
/**
 * This is  a client view of an imap server. It includes basic information about a server such as
 * port, host,and server type.
 * @author Adam Gibson
 *
 */
public class IMAPServer extends BaseServer {

	
	/**
	 * This default constructor will make the assumption of
	 * localhost for mail and the unencrypted imap port.
	 */
	public IMAPServer() {
		this(MailConstants.UNENCRYPTED_DEFAULT_IMAP_PORT,"localhost");
	}
	
	/**
	 * This sets the port the imap server listens on and the host the server is on.
	 * @param port the port the imap server listens on
	 * @param host the host the server listens on.
	 */
	public IMAPServer(int port,String host) {
		this.setServerType(MailConstants.IMAP_SERVER);
		this.setPort(port);
		this.setServerAddress(host);
		
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1686532923453381769L;
	
}//end IMAPServer
