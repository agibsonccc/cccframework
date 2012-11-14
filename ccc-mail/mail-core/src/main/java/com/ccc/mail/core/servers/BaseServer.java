package com.ccc.mail.core.servers;

/**
 * This adds on the flag of whether a user a server's in and out 
 * is encrypted or not.
 * @author Adam Gibson
 *
 */
public class BaseServer extends Server {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 353209461056694330L;

	public boolean isEncryptedIn() {
		return encryptedIn;
	}

	public void setEncryptedIn(boolean encryptedIn) {
		this.encryptedIn = encryptedIn;
	}

	public boolean isEncryptedOut() {
		return encryptedOut;
	}

	public void setEncryptedOut(boolean encryptedOut) {
		this.encryptedOut = encryptedOut;
	}

	private boolean encryptedIn;
	
	private boolean encryptedOut;

}//end BaseServer
