package com.ccc.mail.tests.utils;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
@Component
public class TestUtils {

	
	public static SMTPServer gmailSMTP() {
		SMTPServer smtp = new SMTPServer();
		smtp.setAuth(true);
		smtp.setEncryptedOut(true);
		smtp.setServerType("smtps");
		smtp.setServerName("smtp.gmail.com");
		smtp.setEncryptionType("ssl");
		smtp.setPort(465);
		return smtp;
	}

	public static SMTPServer cccSSL() {
		SMTPServer smtp = new SMTPServer();
		smtp.setAuth(true);
		smtp.setEncryptedOut(true);
		smtp.setServerType("smtps");
		smtp.setServerName("clevercloudcomputing.com");
		smtp.setEncryptionType("ssl");
		smtp.setPort(465);
		return smtp;
	}

	
	public Server mailingListServer() {
		Server ret =new Server();
		
		ret.setAuth(true);
		ret.setPort(2500);
		ret.setServerAddress("50.57.101.26");
		ret.setServerName("mail.clevercloudcomputing.com");
		ret.setServerType("smtp");
		return ret;
	}
	
	

	
	
	public static  Map<String,String> headers(String userName,String password,String serverName,Server server) {
		
		Map<String,String> headers = new HashMap<String,String>();

		headers=ServerMailStoreBridge.headersForServer(server);
		headers.put(MailClient.IS_AUTH,"true");
		headers.put(MailConstants.USER_NAME,userName);
		headers.put(MailConstants.PASSWORD,password);
		headers.put("server",serverName);
		headers.put(MailConstants.SSL_FALLBACK,"true");
		headers.put(MailConstants.IS_SSL,"true");
		return headers;
	}

	public synchronized Map<String,String> headersForCCC() {
		userName="agibson";
		password="destrotroll%5";
		Map<String,String> headers = new HashMap<String,String>();

		headers=ServerMailStoreBridge.headersForServer(incoming);
		headers.put(MailClient.IS_AUTH,"true");
		headers.put(MailConstants.USER_NAME,userName);
		headers.put(MailConstants.PASSWORD,password);
		headers.put("server",incoming.getServerName());
		//headers.put(MailConstants.SSL_FALLBACK,"true");
		headers.put(MailConstants.IS_SSL,"false");
		return headers;
	}
	
	
	
	
	
	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}


	public SMTPServer getOutgoing() {
		return outgoing;
	}


	public void setOutgoing(SMTPServer outgoing) {
		this.outgoing = outgoing;
	}


	public Server getIncoming() {
		return incoming;
	}


	public void setIncoming(Server incoming) {
		this.incoming = incoming;
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	@Resource(name="mailingListMailClient")
	private MailClient mailClient;
	@Autowired
	private SMTPServer outgoing;
	@Qualifier("cccIn")
	@Autowired
	private Server incoming;

	private String userName=null;

	private String password=null;
}
