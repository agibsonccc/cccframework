package com.ccc.mail.mailclient.factory;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.MailBox;
import com.ccc.mail.core.mailstore.MailStore;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.impl.DefaultMailBox;
import com.ccc.mail.impl.DefaultMailClient;
import com.ccc.mail.impl.DefaultMailStore;

public class MailClientFactory {

	/**
	 * This will attempt to lookup a mail client
	 * @param name the name of the mail client to lookup
	 * @return a mail client if one exists in the context, null otherwise
	 */
	public  MailClient lookup(String name) {
		try {
			return (MailClient) context.lookup(name);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return null;
	}//end lookup
	
	
	public MailClient fromProperties() {
		return fromProperties(fileName);
	}
	
	
	public MailClient lookup() {
		return lookup(jndiName);
	}
	
	public MailClient fromProperties(String name) {
		File file = new File(name);
		File constantsFiles = new File("mailconstants.properties");
		if(!file.exists())
			return null;
		else {
			Properties props = new Properties();
		
			try {
				BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
				BufferedInputStream cis = new BufferedInputStream(new FileInputStream(constantsFiles));

				props.load(bis);
				props.load(cis);
				IOUtils.closeQuietly(bis);
				Server defaultIncoming = new Server();
				String type=props.getProperty("mail.incoming.type");
				String address=props.getProperty("mail.incoming.address");
				String defaultPort= type.contains("imaps") ? "993" : type.contains("imap")  ? "143" : type.contains("pops") ? "996" : type.contains("pop")? "110": null;
				String port=props.getProperty("mail.incoming.port", defaultPort);
				Integer num=Integer.parseInt(port);
				String serverName=props.getProperty("mail.incoming.name");
				defaultIncoming.setPort(num);
				defaultIncoming.setServerAddress(address);
				defaultIncoming.setServerName(serverName);
				defaultIncoming.setServerType(type);
				List<Server> incoming=Collections.singletonList(defaultIncoming);
				
				SMTPServer defaultOutgoing= new SMTPServer();
				String outgoingType=props.getProperty("mail.outgoing.type");
				String outgoingAddress=props.getProperty("mail.outgoing.address");
				String outgoingPort= type.contains("smtps") ? "465" : type.contains("smtp")  ? "25" : null;
				String outPort=props.getProperty("mail.outgoing.port", outgoingPort);
				Integer numOut=Integer.parseInt(outPort);
				String outName=props.getProperty("mail.outgoing.name");
				defaultOutgoing.setPort(numOut);
				defaultOutgoing.setServerAddress(outgoingAddress);
				defaultOutgoing.setServerName(outName);
				defaultOutgoing.setServerType(outgoingType);
				List<SMTPServer> outgoing=Collections.singletonList(defaultOutgoing);
				
				MailStore store = new DefaultMailStore();
				store.setIncomingServers(incoming);
				store.setOutGoingServers(outgoing);
				
				MailBox mailBox = new DefaultMailBox();
				MailClient ret = new DefaultMailClient(mailBox,store);
				return ret;
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return null;
	}
	
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public String getJndiName() {
		return jndiName;
	}


	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	@Autowired(required=false)
	private Context context;
	
	private String fileName;
	
	private String jndiName;
}
