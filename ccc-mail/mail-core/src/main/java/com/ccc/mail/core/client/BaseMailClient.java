package com.ccc.mail.core.client;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.MailBox;
import com.ccc.mail.core.mailbox.MailServerAuthenticator;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.mailstore.InstallCert;
import com.ccc.mail.core.mailstore.MailStore;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.ssl.exceptions.SSLErrorException;

import com.ccc.util.collections.Converter;
/**
 * This is a base implementation for 
 * reading and writing to a mailbox and
 * sending mail.
 * @author Adam Gibson
 *
 */
public  abstract class BaseMailClient implements MailClient {

	public  BaseMailClient() {}

	public  BaseMailClient(MailBox mailbox,MailStore mailStore) {
		setMailBox(mailbox);
		setMailStore(mailStore);
	}




	@Override
	public List<Folder> foldersForServerCache(String email,Server s,
			Map<String, String> headers) throws SSLErrorException, MessagingException {
		List<Folder> folders=null;
		if(cachedFolders.get(email) !=null) {
			return cachedFolders.get(email);
		}
		else {
			folders=this.foldersForServer(s, headers);
			cachedFolders.put(email,folders);
			BaseMailClient.cachedFoldersTime.put(email,System.currentTimeMillis());
		}
		return folders;
	}

	@Override
	public Collection<File> getAttachments(Message[] messages) {
		List<File> ret = new LinkedList<File>();
		for(Message m : messages) {
			try {
				if(ServerMailStoreBridge.messageHasAttachments(m)) {
					ret.addAll(ServerMailStoreBridge.getAttachments(m));
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error("Error getting attachments for message ",e);
			} catch (MessagingException e) {
				e.printStackTrace();
				log.error("Error getting attachments for message",e);
			}
		}
		return ret;
	}

	@Override
	public List<Server> incomingServers() {
		return mailStore.incomingServers();
	}

	@Override
	public List<SMTPServer> outgoingServers() {
		return mailStore.outgoingServers();
	}

	@Override
	public void setOutgoingServers(List<SMTPServer> outgoing) {
		mailStore.setOutGoingServers(outgoing);;	
	}

	@Override
	public void setIncomingServers(List<com.ccc.mail.core.servers.Server> incoming) {
		mailStore.setIncomingServers(incoming);	
	}

	@Override
	public void sendMailWithServer(com.ccc.mail.core.servers.Server s, Map<String,String> headers,Message m) throws MessagingException {
		Session s1=mailSessions.get(s);
		if(s1==null) {
			s1=login(headers,s);
		}


		Transport t=s1.getTransport(s.getServerType());

		if(ServerMailStoreBridge.isAuth(headers)) {
			String userName=headers.get(MailClient.USER_NAME);
			String password=headers.get(MailClient.PASSWORD);
			String port=headers.get(MailClient.PORT);
			String host=s.getServerName();
			int connectPort=port!=null  && !port.isEmpty() ? Integer.parseInt(port) : 25;
			if(!t.isConnected()) {
				t.connect(host,connectPort, userName, password);
			}
			t.sendMessage(m,m.getAllRecipients());
		}
		else {

			if(!t.isConnected())
				t.connect();
			t.sendMessage(m, m.getAllRecipients());
			t.close();
		}

	}//end sendMailWithServer




	@Override
	public void logout(String name) throws MessagingException {
		Assert.notNull(name);
		Assert.hasLength(name);
		Session s=mailSessions.get(name);
		Provider[] providers=s.getProviders();

		for(Provider provider : providers) {
			String protocol=provider.getProtocol();
			if(protocol.contains("pop") || protocol.contains("imap")) {
				Store store=s.getStore(protocol);
				if(store.isConnected())
					store.close();
			}

			else if(protocol.contains("smtp")) {
				Transport t=s.getTransport(protocol);
				if(t.isConnected())
					t.close();
			}
		}
		mailSessions.remove(name);
	}//end logout

	@Override
	public Session login(Map<String, String> headers, com.ccc.mail.core.servers.Server connectTo) {
		boolean isDebug=ServerMailStoreBridge.isDebug(headers);
		boolean isAuth=ServerMailStoreBridge.isAuth(headers);
		boolean isSSL=ServerMailStoreBridge.isSSL(headers);
		boolean isTls=ServerMailStoreBridge.isStartTls(headers);
		boolean isFallBack=ServerMailStoreBridge.isSSLFallBack(headers);
		Properties props=ServerMailStoreBridge.propertiesForServer(connectTo);
		Properties headersProps=ServerMailStoreBridge.propertiesFromHeaders(headers);
		props.putAll(headersProps);
		if(isSSL) {
			props=ServerMailStoreBridge.setSSL(props,headers,connectTo,isFallBack);
		}
		if(isTls) {
			props=ServerMailStoreBridge.setStartTLS(props, connectTo);
		}

		if(isAuth) {
			String userName=headers.get(MailClient.USER_NAME);
			String password=headers.get(MailClient.PASSWORD);

			if(userName==null || password==null || userName.isEmpty() || password.isEmpty())
				throw new IllegalStateException("Inconsistent headers for user name and password. Authorized was true, but no user name and password was specified.");

			Session s=Session.getInstance(props, new MailServerAuthenticator(userName,password));
			s.setDebug(isDebug);

			mailSessions.put(userName,s);
			BaseMailClient.loggedIn.put(userName, System.currentTimeMillis());
			return s;
		}
		else {
			Session s=Session.getInstance(props);
			s.setDebug(isDebug);

			mailSessions.put(connectTo.getServerName(),s);
			return s;
		}
	}//end login

	@Override
	public Map<String, Session> mailSessions() {
		return mailSessions;
	}

	@Override
	public  Folder getOpenFolder(String name, com.ccc.mail.core.servers.Server incoming,
			Map<String, String> headersForServer, boolean write) throws SSLErrorException, MessagingException {
		Folder f=getFolder(name,incoming,headersForServer);

		Assert.notNull(f,"Folder in get folder returned null");
		//this should never happen, test for to be safe.
		if(!f.isOpen()) {
			if(write)
				try {
					f.open(Folder.READ_WRITE);
					if(log.isDebugEnabled())
						log.debug("Opened folder: " + name + " in write mode");
					return f;
				} catch (MessagingException e) {
					log.error("Error opening folder: " + name + " in write mode",e);
				}
			else
				try {
					f.open(Folder.READ_ONLY);
					if(log.isDebugEnabled())
						log.debug("Opened folder: " + name + " in read mode");
					return f;
				} catch (MessagingException e) {
					log.error("Error opening folder: " + name + " in read only mode",e);

				}

			return null;
		}
		return f;
	}//end getOpenFolder
	@Override
	public  Message prepareReply(Message m,boolean replyAll) {
		Assert.notNull(m);
		try {
			if(log.isDebugEnabled())
				log.debug("Setting message for reply");
			return m.reply(replyAll);

		} catch (MessagingException e) {
			log.error("error setting message for reply: " + e);
			return null;
		}
	}//end prepareReply

	@Override
	public  boolean addFolderToServer(com.ccc.mail.core.servers.Server s, String name, String userName,
			String password) {
		//No authentication
		if(userName==null || password==null) {
			Map<String,String> headers=ServerMailStoreBridge.headersForServer(s);
			mailStore.addFolder(name, s, headers);
			return true;
		}
		//Authentication
		else {
			Map<String,String> headers=ServerMailStoreBridge.headersForServer(s);
			headers.put(USER_NAME,userName);
			headers.put(PASSWORD,password);
			mailStore.addFolder(name, s, headers);
			return true;
		}
	}//end addFolderToServer


	@Override
	public  boolean addFolderToFolder(String newFolderName, Folder to) {
		try {
			Folder f=to.getStore().getFolder(newFolderName);
			if(!f.exists())
				if( f.create(Folder.HOLDS_MESSAGES))
					f.setSubscribed(true);
			if(f.isOpen())
				f.close(true);
		} catch (MessagingException e) {
			log.error("Error creating folder: " + newFolderName + " in " + to.getFullName(),e);
		}
		return false;
	}//end addFolderToFolder

	@Override
	public  boolean addFolderToServer(com.ccc.mail.core.servers.Server s, String name) {
		return addFolderToServer(s,name,null,null);
	}


	public  List<Folder> folders(String uName, String pWord) {
		return mailStore.foldersForUser(uName, pWord);
	}

	@Override
	public  List<Message> messagesForFolder(Folder f,Map<String,String> headers, com.ccc.mail.core.servers.Server s) {
		return mailStore.messagesForFolder(f,headers,s);
	}

	@Override
	public  Folder getFolder(String name,com.ccc.mail.core.servers.Server incoming,Map<String,String> headersForServer) throws SSLErrorException, MessagingException {
		Assert.notNull(name);
		Assert.hasLength(name);
		if(name.charAt(0)=='\"')
			name=name.substring(1);
		if(name.charAt(name.length()-1)=='\"')
			name=name.substring(0,name.length()-1);
		//Collect connection headers

		//Retrieve the folders
		List<Folder> folders=foldersForServer(incoming, headersForServer);
		if(folders==null)
			return null;
		//Find the folder
		for(Folder f : folders) {
			if(f.getName().equals(name)) {
				return f;
			}
			//error occurred, or folder wasn't found
		}
		//error occurred, or folder wasn't found
		return null;
	}//end getFolder




	@Override
	public  boolean folderHasNewMessage(String name, com.ccc.mail.core.servers.Server incoming,
			Map<String, String> headers) throws MessagingException, SSLErrorException {
		Folder f=getFolder(name,incoming,headers);
		//Ensure open
		if(!f.isOpen())
			f.open(Folder.READ_ONLY);
		return f.hasNewMessages();
	}

	@Override
	public  Message[] getNewMessagesForFolder(String name, com.ccc.mail.core.servers.Server incoming,
			Map<String, String> headers) throws MessagingException, SSLErrorException {

		Folder f = getFolder(name,incoming,headers);
		if(f==null) {
			log.error("No folder found, returningn null");
			return null;
		}

		if(!f.isOpen()) {
			f.open(Folder.READ_WRITE) ;
		}
		int newMessageCount=f.getNewMessageCount();

		if(newMessageCount > 0) {
			int size=f.getMessageCount();
			int min=size-newMessageCount;
			Message[] m= f.getMessages(min, size);
			f.close(true);
			return m;
		}
		return null;
	}//end getNewMessagesForFolder

	@Override
	public  int numMessagesInFolder(String name, com.ccc.mail.core.servers.Server incoming,
			Map<String, String> headersForServer) throws MessagingException, SSLErrorException {

		Folder f = getOpenFolder(name,incoming,headersForServer,true);
		int size= f.getMessageCount();

		return size;
	}

	@Override
	public  List<Folder> foldersForServer(com.ccc.mail.core.servers.Server s,Map<String,String> headers) throws SSLErrorException,MessagingException {
		Assert.notNull(headers);
		if(ServerMailStoreBridge.isAuth(headers)) {
			String userName=headers.get(USER_NAME);
			String password=headers.get(PASSWORD);
			Properties props=ServerMailStoreBridge.propertiesFromHeaders(headers);
			if(ServerMailStoreBridge.isSSL(headers)) {
				if(ServerMailStoreBridge.isSSLFallBack(headers))
					props=ServerMailStoreBridge.setSSL(props,headers,s,true);
				else props=ServerMailStoreBridge.setSSL(props,headers,s,false);
			}
			if(BaseMailClient.cachedFolders.get(userName)!=null && !foldersExpired(userName))
				return BaseMailClient.cachedFolders.get(userName);
			Store s1=ServerMailStoreBridge.getStoreForServer(s,props , new MailServerAuthenticator(userName,password));

			try {
				if(!s1.isConnected())
					s1.connect(s.getServerName(), s.getPort(), userName, password);
				return new Converter<Folder>().setToList(allFoldersFromStore(s1,s1.getDefaultFolder()));

			} 
			catch (MessagingException e2) {
				log.error("Error in connecting to store: ",e2);
				if(e2.getMessage()!=null && e2.getMessage().toLowerCase().contains("ssl"))
					throw new SSLErrorException(e2);


			}
		}

		return null;
	}//end foldersForServer





	@Override
	public  List<Folder> getOpenFoldersForServer(com.ccc.mail.core.servers.Server s,Map<String,String> headers,boolean readOnly) {
		Assert.notNull(headers);
		if(ServerMailStoreBridge.isAuth(headers)) {
			String userName=headers.get(USER_NAME);
			String password=headers.get(PASSWORD);
			Properties props=ServerMailStoreBridge.propertiesFromHeaders(headers);
			if(ServerMailStoreBridge.isSSL(headers)) {
				if(ServerMailStoreBridge.isSSLFallBack(headers))
					props=ServerMailStoreBridge.setSSL(props,headers,s,true);
				else props=ServerMailStoreBridge.setSSL(props,headers,s,false);
			}
			Store s1=ServerMailStoreBridge.getStoreForServer(s,props , new MailServerAuthenticator(userName,password));
			try {
				if(!s1.isConnected())
					s1.connect(s.getServerName(), s.getPort(), userName, password);
			} catch (MessagingException e2) {
				e2.printStackTrace();
				log.warn("Error in connecting to store: ",e2);
				return null;
			}
			try {
				Set<Folder> folders=allFoldersFromStore(s1,s1.getDefaultFolder());

				for(Folder f : folders) {
					try {
						if(!f.isOpen() && (f.getType() & javax.mail.Folder.HOLDS_MESSAGES) != 0) {
							int mode= readOnly ? Folder.READ_ONLY : Folder.READ_WRITE;
							f.open(mode);
						}
					}catch(MessagingException e) {
						log.error("Error opening folder: " + f.getName() + " type was : " + f.getType() + " error was: " + e.getMessage());
						if(log.isDebugEnabled())
							e.printStackTrace();
					}
				}

				return new Converter<Folder>().setToList(folders);
			} catch (MessagingException e) {
				log.warn("Error getting folders from server: ",e);
				e.printStackTrace();
				if(s1!=null && s1.isConnected()) {
					try {
						s1.close();
					} catch (MessagingException e1) {
						log.error("Error closing store connection",e1);
					}
				}
			}


		}

		return null;
	}//end foldersForServer





	private Set<Folder> allFoldersFromStore(Store s,Folder current) throws MessagingException {
		if(!s.isConnected())
			throw new IllegalStateException("Unable to return folder, store is closed");
		return new Converter<Folder>().listToSet(Arrays.asList(current.list()));

	}//end allFoldersFromStore

	@Override
	public  boolean sendStartTlsMail(Map<String, String> headers,boolean isHtml) {

		String authVal=headers.get(IS_AUTH);
		Boolean auth=authVal != null ? Boolean.parseBoolean(authVal) : false; 
		//Needs authentication
		if(auth) {
			try {

				doSendAuthMail(headers,isHtml);
			} catch (AddressException e) {
				log.error("Addressexception sending mail",e);

				return false;
			} catch (MessagingException e) {
				log.error("Messaging exception sending mail",e);

				return false;
			}
			return true;
		}
		else  {
			try {
				doSendMail(headers,isHtml);
			} catch (AddressException e) {
				log.error("Addressexception sending mail",e);
				return false;
			} catch (MessagingException e) {
				log.error("Messaging exception sending mail",e);
				return false;
			}
			return true;
		}
	}//end sendStartTlsMail

	@Override
	public  boolean sendMailWithAttachments(Map<String, String> headers,
			File[] toAttach,boolean isHtml) throws AddressException, MessagingException {

		if(toAttach==null) {
			log.warn("Attachments were null, just sending normal mail");
			return sendMail(headers, isHtml);
		}
		Assert.notNull(headers,"Headers must not be null!");
		Assert.isTrue(!headers.isEmpty());
		Assert.isTrue(toAttach.length >= 1);
		Assert.notNull(mailStore);
		String text=headers.get(CONTENT);
		for(com.ccc.mail.core.servers.Server s : mailStore.outgoingServers()) {
			//Message m=initHeaders(headers,s);
			Session session=login(headers,s);
			Transport transport=session.getTransport(s.getServerType());

			if(transport==null) {
				log.warn("Transport for " + s.getServerName() + " was null the type was " + s.getServerType());
				log.warn("Not sending mail due to null transport");
				return false;
			}

			Message m = new MimeMessage(session);
			ServerMailStoreBridge.attachFiles(toAttach, m, text);

			if(!transport.isConnected()) {
				if(ServerMailStoreBridge.isAuth(headers)) {
					String userName=headers.get(USER_NAME);
					String password=headers.get(PASSWORD);
					transport.connect(s.getServerName(),s.getPort(), userName, password);
				}
				else {
					transport.connect();
				}
			}
			initHeaders(m, headers,isHtml);
			Address[] recipients=m.getAllRecipients();
			if(recipients==null) {
				log.warn("Recipients are null, please specifiy recipients, not sending a message");
				return false;
			}
			Assert.notNull(m.getAllRecipients(),"No recipients to send to!");
			transport.sendMessage(m, m.getAllRecipients());
			transport.close();
		}


		return false;
	}//end sendMailWithAttachments

	@Override
	public  MailStore mailStore() {
		return mailStore;
	}

	public  boolean sendMail(Map<String, String> headers,boolean isHtml) throws AddressException, MessagingException {
		String authString=headers.get(MailClient.IS_AUTH);
		String sslString=headers.get(MailClient.IS_SSL);
		String tlsString=headers.get(MailClient.START_TLS);
		String debugString=headers.get(DEBUG);
		Boolean auth=authString != null ? Boolean.parseBoolean(authString) : false;
		Boolean ssl=sslString!=null ? Boolean.parseBoolean(sslString) : false;
		Boolean startTls=tlsString!=null ? Boolean.parseBoolean(tlsString) : false;
		Boolean debug=debugString!=null ? Boolean.parseBoolean(debugString) : false;

		//Both aren't allowed
		if(ssl && startTls)
			throw new IllegalStateException("SSL and TLS are not both allowed to be active at the same time.");

		//Needs to send encrypted mail
		if(ssl) return sendSSLMail(headers,isHtml);

		else if(startTls) return sendStartTlsMail(headers,isHtml);

		List<SMTPServer> outgoingServers=mailStore.outgoingServers();

		if(outgoingServers!=null) {
			for(SMTPServer s : outgoingServers) {
				Properties props=ServerMailStoreBridge.propertiesForServer(s);
				//Needs authentication
				if(auth) {
					String userName=headers.get(USER_NAME);
					String password=headers.get(PASSWORD);
					Session s1=Session.getInstance(props, new MailServerAuthenticator(userName,password));
					if(debug)
						s1.setDebug(true);
					Message m = new MimeMessage(s1);
					ssl=s.getServerType().toLowerCase().contains("smtps");
					initHeaders(m,headers,isHtml);
					Transport t=s1.getTransport(ssl ? SSL_SMTP_PROTOCOL_VALUE : SMTP_SERVER);
					try {
						t.connect(s.getServerName(),s.getPort(), userName, password);
					}catch(Exception e) {
						log.warn("Error connecting to server: " + s.getServerName() + " trying IP Address");
						t.connect(s.getServerAddress(),s.getPort(), userName, password);
					}
					Assert.notNull(m.getAllRecipients(),"No recipients to send to!");
					t.sendMessage(m,m.getAllRecipients());
					t.close();
					log.info("Sent mail for user: " + userName + " using server: " + s.getServerName());

					if(log.isDebugEnabled())
						log.debug("Server used for authenticated session was: " + s);

					if(log.isDebugEnabled())
						log.debug("User was: " + userName);
					return true;
				}
				else {
					Session s1=Session.getInstance(props,null);
					//s1.setDebug(true);
					Message m = new MimeMessage(s1);
					initHeaders(m,headers,isHtml);
					Transport t=s1.getTransport(ssl ? SSL_SMTP_PROTOCOL_VALUE : SMTP_SERVER);
					t.connect();
					Transport.send(m);
					t.close();
					log.info("Message sent using server: " + s.getServerName());

					if(log.isDebugEnabled())
						log.debug("Server used was: " + s);
					return true;
				}
			}
		}
		else log.warn("Tried sending mail to no servers");

		return false;
	}//end sendMail


	@SuppressWarnings("unused")
	private void installSSLCert(String host) throws Exception {
		try {
			InstallCert.install(new String[]{host});
		}catch(Exception e) {
			int i=host.indexOf(':');
			if(i >=0) {
				StringBuffer sb = new StringBuffer();
				sb.append(host.substring(0, i));
				sb.append(ENCRYPTED_DEFAULT_SMTP_PORT);
				InstallCert.install(new String[]{sb.toString()});
			}
		}

	}//end sendStartTlsMail

	/**
	 * This will init the headers and set the message according to the passed in map
	 * @param m the message to use
	 * @param headers the headers to set
	 * @throws AddressException if one is thrown
	 * @throws MessagingException if one is thrown
	 */
	private void initHeaders(Message m,Map<String,String> headers,boolean isHtml) throws AddressException, MessagingException {
		//Comma separated list of email addresses
		String to=headers.get(MailClient.TO_ADDRESSES);
		String from=headers.get(MailClient.FROM_ADDRESS);
		//Comma separated email addresses
		String ccAddresses=headers.get(MailClient.CC_ADDRESSES);
		//Comma separated email addresses
		String bccAddresses=headers.get(MailClient.BCC_ADDRESSES);
		String subject=headers.get(MailClient.SUBJECT);
		String content=headers.get(MailClient.CONTENT);
		String fromName=headers.get(MailClient.FROM_NAME);

		Boolean isFlagged=headers.get(MailClient.FLAGGED) != null ? Boolean.parseBoolean(headers.get(MailClient.FLAGGED)) : false ;
		if(from!=null && fromName!=null) {
			try {
				m.setFrom(new InternetAddress(from,fromName));
				m.setHeader("MAIL FROM", from);
			} catch (UnsupportedEncodingException e1) {
				log.error("Unsupported encoding: " + e1);
			}

		}
		else if(from!=null) {
			m.setFrom(new InternetAddress(from));
			m.setHeader("MAIL FROM", from);
		}
		Object currContent=null;
		try {
			currContent=m.getContent();
		} catch (IOException e) {
			currContent=null;
		}

		if(currContent==null) {
			if(content==null)
				content="";
			if(!isHtml) m.setText(content);
			else m.setContent(content, "text/html");
		}
		m.setSentDate(new Date());
		m.setSubject(subject);
		//	m.setFrom();
		//Append as addresses to the message
		if(validString(bccAddresses))
			ServerMailStoreBridge.setBcc(m, bccAddresses);
		if(validString(ccAddresses))
			ServerMailStoreBridge.setCC(m, ccAddresses);
		if(validString(to))
			ServerMailStoreBridge.setTo(m, to);
		if(isFlagged)
			ServerMailStoreBridge.toggleFlag(m);
	}//end initHeaders

	private static boolean foldersExpired(String userName) {
		long time=System.currentTimeMillis();
		Long get=BaseMailClient.cachedFoldersTime.get(userName);
		if(get!=null) return false;
		else {
			long diff=time-get;
			return diff <= BaseMailClient.clearFolderCacheTime;
		}
	}

	/**
	 * This will init the headers and set the message according to the passed in map
	 * @param m the message to use
	 * @param headers the headers to set
	 * @throws AddressException if one is thrown
	 * @throws MessagingException if one is thrown
	 */
	private Message initHeaders(Map<String,String> headers,com.ccc.mail.core.servers.Server s,boolean isHtml) throws AddressException, MessagingException {
		Session s1=ServerMailStoreBridge.formSession(s, headers);
		Message ret = new MimeMessage(s1);


		//Prepare the message
		String to=headers.get(MailClient.TO_ADDRESSES);
		String from=headers.get(MailClient.FROM_ADDRESS);
		String fromName=headers.get(MailClient.FROM_NAME);
		String ccAddresses=headers.get(MailClient.CC_ADDRESSES);
		String bccAddresses=headers.get(MailClient.BCC_ADDRESSES);
		String subject=headers.get(MailClient.SUBJECT);
		String content=headers.get(MailClient.CONTENT);

		if(!isHtml) ret.setText(content);
		else ret.setContent(content, "text/html");
		Boolean isFlagged=headers.get(MailClient.FLAGGED) != null ? Boolean.parseBoolean(headers.get(MailClient.FLAGGED)) : false;
		ret.setFrom(new InternetAddress(from));
		ret.setText(content);
		ret.setSentDate(new Date());
		ret.setSubject(subject);
		if(from!=null && fromName!=null) {
			try {
				ret.setFrom(new InternetAddress(fromName,from));
			} catch (UnsupportedEncodingException e1) {
				log.error("Unsupported encoding: " + e1);
			}

		}
		else if(from!=null) {
			ret.setFrom(new InternetAddress(from));
		}

		//Set up the addresses
		if(validString(bccAddresses))
			ServerMailStoreBridge.setBcc(ret, bccAddresses);
		if(validString(ccAddresses))
			ServerMailStoreBridge.setCC(ret, ccAddresses);
		if(validString(to))
			ServerMailStoreBridge.setTo(ret, to);
		if(isFlagged)
			ServerMailStoreBridge.toggleFlag(ret);
		return ret;
	}//end initHeaders


	@Override
	public boolean sendMessageToServer(Map<String,String> headers,com.ccc.mail.core.servers.Server s,Message m) {


		return false;
	}



	/* Validate addresses primarily */
	private boolean validString(String s) {
		return s!=null && s.length() > 0;
	}
	@Override
	public  boolean sendSSLMail(Map<String, String> headers,boolean isHtml) throws AddressException, MessagingException {
		String authVal=headers.get(IS_AUTH);
		Boolean auth=authVal != null ? Boolean.parseBoolean(authVal) : false; 
		//Needs authentication
		if(auth) {
			doSendAuthMail(headers,isHtml);

			if(log.isDebugEnabled())
				log.debug("Sent ssl auth mail");
			return true;
		}
		//No authentication
		else  {
			doSendMail(headers,isHtml);
			if(log.isDebugEnabled())
				log.debug("Sent ssl non auth mail");
			return true;
		}

	}//end sendSSLMail




	/**
	 * This send a mail without attachments based on the given headers.
	 * @param headers the headers to use
	 * @param ssl whether the mail is encrypted or not
	 * @throws AddressException if one occurs
	 * @throws MessagingException if one occurs
	 */
	private void doSendMail(Map<String,String> headers,boolean isHtml) throws AddressException, MessagingException {
		Assert.notNull(headers);
		//Debug?
		String debugString=headers.get(DEBUG);
		Boolean debug=debugString!=null ? Boolean.parseBoolean(debugString) : false;

		//SSL?
		String sslString=headers.get(IS_SSL);
		Boolean ssl=sslString != null ? Boolean.parseBoolean(sslString) : false;		

		//Start tls?
		String startTlsString=(headers.get(START_TLS));
		Boolean startTls=startTlsString !=null ? Boolean.parseBoolean(startTlsString) : false;
		//SSL and Start TLS not allowed at same time
		if(ssl && startTls)
			throw new IllegalStateException("SSL and Start TLS not allowed at same time, set one to false");

		//Trust all certs?
		String sslFallBack=headers.get(SSL_FALLBACK);
		Boolean doFallBack=sslFallBack!=null  ? Boolean.parseBoolean(sslFallBack) : false;

		List<SMTPServer> outgoingServers=mailStore.outgoingServers();
		//Need out going servers to be able to send mail
		Assert.notEmpty(outgoingServers);
		log.warn("No messages in out going servers");
		for(Server s : outgoingServers) {
			Properties props=ServerMailStoreBridge.propertiesForServer(s);
			if(ssl) {
				props=ServerMailStoreBridge.setSSL(props,headers,s,doFallBack);
				if(log.isDebugEnabled())
					log.debug("Initialzed SSL for sending mail for server: " + s.getServerName());

			}
			if(startTls) {
				props=ServerMailStoreBridge.setStartTLS(props,s);

				String keyStoreLoc=headers.get(KEYSTORE_LOCATION);
				String keyStorePass=headers.get(KEYSTORE_PASSWORD);


				//Check for custom key store
				if(keyStoreLoc!=null) {
					props.put(KEYSTORE_LOCATION,keyStoreLoc);
					props.put(KEYSTORE_PASSWORD,keyStorePass);
					System.setProperties(props);
				}

				if(log.isDebugEnabled())
					log.debug("Initialized Start tls for sending mail for server: " + s.getServerName());

			}
			//Unauthenticated mail
			Session s1=Session.getInstance(props, null);
			if(debug)
				s1.setDebug(true);
			Message m = new MimeMessage(s1);
			//Prepare the message
			initHeaders(m,headers,isHtml);
			log.info("Prepared message for session");
			try {
				Transport t=s1.getTransport(s.getServerType());
				if(sentMail(t,m,s,0))
					log.info("Mail sent successfully to server: " + s.getServerName());

				return;
			}catch(Exception e) {
				log.warn("Error sending from server: " + s.getServerName(), e);
				e.printStackTrace();
			} 
		}

	}//end doSendMail

	private boolean sentMail(Transport t,Message m,com.ccc.mail.core.servers.Server s,int numRetries) {
		if(numRetries >=3) {
			log.warn("Gave up on trying to send message to server: " + s.getServerName() + " after: " +numRetries + " retries");

			return false;
		}
		try {
			if(!t.isConnected())
				t.connect();

			Transport.send(m);
			t.close();
			return true;
		}catch(Exception e) {
			log.warn("Error sending from server: " + s.getServerName(), e);
			numRetries++;
			return sentMail(t,m,s,numRetries);
		}
	}//end sentMail





	@SuppressWarnings("unused")
	private void testCertificate(Properties props,Map<String,String> headers,com.ccc.mail.core.servers.Server s) {
		Assert.notNull(props);
		Assert.notNull(headers);
		Assert.notNull(s);

		String debugString=headers.get(DEBUG);
		Boolean debug=debugString!=null ? Boolean.parseBoolean(debugString) : false;

		Boolean auth=Boolean.parseBoolean(headers.get(MailClient.IS_AUTH));
		String userName=headers.get(USER_NAME);
		String password=headers.get(PASSWORD);
		Session s1=Session.getInstance(props,auth ? new MailServerAuthenticator(userName,password) : null);
		if(debug)
			s1.setDebug(true);
		//s1.setDebugOut(System.out);
		try {

			Transport t=s1.getTransport(s.getServerType());
			if(auth) {

				try {t.connect(s.getServerName(), userName, password);}

				catch (MessagingException e) {
					log.warn("Error occurred connecting to server: with ",e);
					log.warn("Failed to connect: attempting to install cert");
					try {
						InstallCert.install(new String[] {s.getServerName() + ":" + String.valueOf(s.getPort())});
						System.out.println("Installed cerficate.");
						log.info("Installed certificate");
						t.close();
					}//end try 
					catch (Exception e1) {
						e1.printStackTrace();
						log.warn("Error in installing cert: ",e);
						try {
							t.close();
						} catch (MessagingException e2) {
							e2.printStackTrace();
						}
					}//end catch
				}//end catch

			}//end if
		} //end try
		catch (NoSuchProviderException e) {
			e.printStackTrace();
			log.warn("Error finding provider",e);

		}//end catch

	}//end testCertificate



	/**
	 * This sends authenticated mail
	 * @param headers the headers to use
	 * @param ssl if the mail is encrypted or not
	 * @throws AddressException if one occurs
	 * @throws MessagingException if one occurs
	 */
	private void doSendAuthMail(Map<String,String> headers,boolean isHtml) throws AddressException, MessagingException {
		Assert.notNull(headers);
		List<SMTPServer> outgoingServers=mailStore.outgoingServers();
		Assert.notNull((outgoingServers!=null && !outgoingServers.isEmpty()),"No servers specified for sending mail");
		String debugString=headers.get(DEBUG);
		Boolean debug=debugString!=null ? Boolean.parseBoolean(debugString) : false;

		//For servers without set up certificates
		String sslFallBack=headers.get(SSL_FALLBACK);

		Boolean doFallBack=sslFallBack!=null  ? Boolean.parseBoolean(sslFallBack) : false;

		//SSL?
		String sslString=headers.get(IS_SSL);
		Boolean ssl=sslString != null ? Boolean.parseBoolean(sslString) : false;		


		//Start tls?
		String startTlsString=(headers.get(START_TLS));
		Boolean startTls=startTlsString !=null ? Boolean.parseBoolean(startTlsString) : false;
		//Check for keystores and keystore passwords for customization of SSL
		String keyStoreLoc=headers.get(KEYSTORE_LOCATION);
		String keyStorePass=headers.get(KEYSTORE_PASSWORD);

		//SSL and Start TLS not allowed at same time
		if(ssl && startTls)
			throw new IllegalStateException("SSL and Start TLS not allowed at same time, set one to false");
		//Ensure default trust store is set.

		for(SMTPServer s : outgoingServers) {
			Properties props=ServerMailStoreBridge.propertiesForServer(s);
			//Check for custom certs where necessary.
			if(ssl || startTls) {
				//Check for custom key store
				if(keyStoreLoc!=null) {
					props.put(KEYSTORE_LOCATION,keyStoreLoc);
					props.put(KEYSTORE_PASSWORD,keyStorePass);
					System.setProperties(props);
				}
			}

			//Initialize ssl
			if(ssl) {
				props=ServerMailStoreBridge.setSSL(props,headers,s,doFallBack);
			}
			//Initialize start tls
			if(startTls) {
				props=ServerMailStoreBridge.setStartTLS(props,s);
				System.setProperties(props);
				Assert.isTrue(props.get(START_TLS).equals("true"));
			}

			//User authentication
			String userName=headers.get(USER_NAME);
			String password=headers.get(PASSWORD);


			//Make sure default certificate is set back up if a custom one was specified.
			//if((ssl || startTls) && keyStoreLoc!=null)
			//ServerMailStoreBridge.setDefaultTrustStore();
			//Initialize the message
			Session s1=Session.getInstance(props, new MailServerAuthenticator(userName,password));
			if(debug)
				s1.setDebug(true);
			Message m = new MimeMessage(s1);

			initHeaders(m,headers,isHtml);
			if(log.isDebugEnabled())
				log.debug("Initialized headers for sending authorized mail");
			try {
				Transport t=s1.getTransport(s.getServerType());


				if(sentAuthMail(t,m,s,userName,password,0))
					log.info("Message sent successfully!");
				else log.warn("Message for authz not sent.");
				//t.close();
				if(log.isDebugEnabled())
					log.debug("Sent message using server: " + s.getServerName());


				return;


			}catch(Exception e) {
				log.warn("Error sending from server: " + s.getServerName(), e);
				e.printStackTrace();
			}
		}//end for loop

	}//end doSendAuthMail

	/**
	 * This attempts to send mail up to 3 times before giving up.
	 * @param t the transport to send with
	 * @param m the message to send
	 * @param s the server to send from
	 * @param userName the user name to use
	 * @param password the password to use
	 * @param numRetries the number of times tried so far
	 * @return true if the message was sent, false otherwise
	 */
	private boolean sentAuthMail(Transport t,Message m,com.ccc.mail.core.servers.Server s,String userName,String password,int numRetries) {
		if(numRetries >=3) {
			log.warn("Gave up on trying to send message to server: " + s.getServerName() + " after: " +numRetries + " retries");
			return false;
		}
		try {
			if(!t.isConnected())
				t.connect(s.getServerName(), s.getPort(), userName, password);
			Address[] recipients=m.getAllRecipients();
			if(recipients!=null && recipients.length >=1) {
				t.sendMessage(m, m.getAllRecipients());
				m.saveChanges();
			}
			else log.warn("Tried to send message to no recipients");
			t.close();
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			log.warn("Error sending from server: trying ip address.. " + s.getServerName(), e);
			try {
				if(!t.isConnected())
					t.connect(s.getServerAddress(), s.getPort(), userName, password);
				Address[] recipients=m.getAllRecipients();
				if(recipients!=null && recipients.length >=1)
					t.sendMessage(m, m.getAllRecipients());
				t.close();
			}catch(Exception e1) {
				log.warn("Error connecting to IP Address: " + s.getServerAddress());
			}
			numRetries++;
			return sentAuthMail(t,m,s,userName,password,numRetries);
		}
	}//end sentMail

	@SuppressWarnings("unused")
	private void testConnection(String userName,String password,Session s,com.ccc.mail.core.servers.Server server) throws Exception {
		s.setDebug(true);
		Transport t=s.getTransport(server.getServerType());
		if(!t.isConnected())
			t.connect(server.getServerName(), server.getPort(),userName, password);
	}//end testConnection

	@SuppressWarnings("unused")
	private void testConnection(Session s,com.ccc.mail.core.servers.Server server) throws MessagingException {
		s.setDebug(true);
		Transport t=s.getTransport(server.getServerType());
		if(!t.isConnected()) t.connect();
		t.close();

	}//end testConnection

	@Override
	public  boolean sendMailWithAttachments(Map<String, String> headers,
			File[] toAttach,com.ccc.mail.core.servers.Server s,boolean isHtml) throws AddressException, MessagingException {

		Message m=initHeaders(headers,s,isHtml);
		String text=headers.get(CONTENT);

		ServerMailStoreBridge.attachFiles(toAttach, m, text);

		Transport.send(m);
		if(log.isDebugEnabled())
			log.debug("Sent message with attachments from server: " + s.getServerName());
		return true;
	}//end sendMailWithAttachments

	public   void addAttachment(Message attachTo, File[] toAttach) throws MessagingException {
		Assert.notNull(attachTo);
		Assert.notNull(attachTo);

		ServerMailStoreBridge.attachFiles(toAttach, attachTo, "");
		if(log.isDebugEnabled())
			log.debug("Added attachments sucessfully");
	}//end addAttachment

	@Override
	public  boolean deleteMessage(Message toDelete) {
		Assert.notNull(toDelete);
		mailStore.deleteMessages(Collections.singletonList(toDelete));
		return true;
	}//end deleteMessage

	@Override
	public  MailStore storeForMail() {
		return mailStore;
	}

	@Override
	public  List<Message> messagesForUser(String uName, String pWord) {
		return mailStore.mailForUser(uName, pWord);
	}



	@Override
	public  boolean saveDraft(Message toSave) {
		try {
			Assert.notNull(toSave);
			ServerMailStoreBridge.setDraft(toSave);
			if(log.isDebugEnabled())
				log.debug("Saved draft");
			return true;
		}catch(Exception e) {
			log.warn("Error saving draft: ",e);
			return false;
		}
	}//end saveDraft



	public  void attachFile(File toAttach, Message attachTo) throws MessagingException {
		ServerMailStoreBridge.attachFiles(new File[] {toAttach}, attachTo, "");
	}

	public  MailStore getMailStore() {
		return mailStore;
	}

	public  void setMailStore(MailStore mailStore) {
		this.mailStore = mailStore;
	}

	public  MailBox getMailBox() {
		return mailBox;
	}

	public  void setMailBox(MailBox mailbox2) {
		this.mailBox = mailbox2;
	}

	public Map<String, Session> getMailSessions() {
		return mailSessions;
	}

	public void setMailSessions(Map<String, Session> mailSessions) {
		this.mailSessions = mailSessions;
	}

	public long  getClearFolderCacheTime() {
		return clearFolderCacheTime;
	}

	public void setClearFolderCacheTime(long clearFolderCacheTime) {
		this.clearFolderCacheTime = clearFolderCacheTime;
	}


	public static Map<String, List<Folder>> getCachedFolders() {
		return cachedFolders;
	}

	public static void setCachedFolders(Map<String, List<Folder>> cachedFolders) {
		BaseMailClient.cachedFolders = cachedFolders;
	}

	public static boolean isTimerStarted() {
		return timerStarted;
	}

	public static void setTimerStarted(boolean timerStarted) {
		BaseMailClient.timerStarted = timerStarted;
	}

	public static Map<String, Long> getCachedFoldersTime() {
		return cachedFoldersTime;
	}

	public static void setCachedFoldersTime(Map<String, Long> cachedFoldersTime) {
		BaseMailClient.cachedFoldersTime = cachedFoldersTime;
	}

	private static void startClearFolders() {
		if(!BaseMailClient.timerStarted) {
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					for(String s : cachedFoldersTime.keySet()) {
						Long time=cachedFoldersTime.get(s);
						long curr=System.currentTimeMillis();
						long diff=curr-time;
						if(diff >= clearFolderCacheTime) {
							cachedFoldersTime.remove(s);
							cachedFolders.remove(s);
						}
					}
				}

			}, clearFolderCacheTime);
		}
	}
	private static void startLoggedIn() {
		if(!BaseMailClient.timerStarted) {
			timer.schedule(new TimerTask() {

				@Override
				public void run() {
					for(String s : loggedIn.keySet()) {
						Long time=loggedIn.get(s);
						long curr=System.currentTimeMillis();
						long diff=curr-time;
						if(diff >= clearFolderCacheTime) {
							loggedIn.remove(s);
							loggedIn.remove(s);
						}
					}
				}

			}, loggedInDuration);
		}
	}
	private final static Timer timer = new Timer();

	static {
		
		BaseMailClient.startClearFolders();
		BaseMailClient.startLoggedIn();
	}
	/* Mail store used by this client */
	@Autowired(required=false)
	protected MailStore mailStore;
	/* Mail box to use for this client */
	@Autowired(required=false)
	protected MailBox mailBox;
	protected static Logger log=LoggerFactory.getLogger(BaseMailClient.class);
	protected Map<String,Session> mailSessions = new HashMap<String,Session>();
	protected static Map<String,List<Folder>> cachedFolders = new HashMap<String,List<Folder>>();
	private static long clearFolderCacheTime=180000;
	private static long loggedInDuration=180000;
	private static boolean timerStarted=false;
	private static Map<String,Long> loggedIn = new HashMap<String,Long>();
	private static Map<String,Long> cachedFoldersTime = new HashMap<String,Long>();
}//end BaseMailClient
