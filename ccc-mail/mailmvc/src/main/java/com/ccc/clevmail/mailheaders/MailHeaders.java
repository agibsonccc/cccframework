package com.ccc.clevmail.mailheaders;

import java.util.HashMap;

import com.ccc.mail.core.servers.storage.MailConstants;
/**
 * These are mail headers used for connecting to a mail server.
 * @author Adam Gibson
 *
 */
public class MailHeaders   implements MailConstants {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5207716196498440578L;
	
	public HashMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}

	private HashMap<String,String> headers;
}//end MailHeaders
