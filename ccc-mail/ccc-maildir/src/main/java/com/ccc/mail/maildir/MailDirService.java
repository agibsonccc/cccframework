package com.ccc.mail.maildir;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.MailServerAuthenticator;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.ssl.exceptions.SSLErrorException;
import com.ccc.util.filesystem.FileMoverUtil;
/**
 * This is a service for downloading mail via mail dir.
 * @author Adam Gibson
 *
 */
public class MailDirService {
	/**
	 * This will grab all of the messages for a given folder name
	 * for the given imap server and download them to the specified mail directory
	 * @param remoteFolder the remote folder to download from
	 * @param imapServer the imap server to use
	 * @param mailDirPath the path of the mail directory
	 * @param headers the headers to use
	 * @throws MessagingException 
	 * @throws SSLErrorException 
	 */
	public void syncMailDir(Server imapServer,String mailDirPath,Map<String,String> headers) throws SSLErrorException, MessagingException {
			List<Folder> folders=mailClient.getOpenFoldersForServer(imapServer, headers, false);
			for(Folder f : folders) {
				syncMailDir(f.getName(),imapServer,mailDirPath,headers);
			}
	}//end syncMailDir
	/**
	 * This will grab all of the messages for a given folder name
	 * for the given imap server and download them to the specified mail directory
	 * @param remoteFolder the remote folder to download from
	 * @param imapServer the imap server to use
	 * @param mailDirPath the path of the mail directory
	 * @param headers the headers to use
	 * @throws MessagingException 
	 * @throws SSLErrorException 
	 */
	public void syncMailDir(String remoteFolder,Server imapServer,String mailDirPath,Map<String,String> headers) throws SSLErrorException, MessagingException {
		Assert.hasLength(remoteFolder,"Invalid folder specified");
		Assert.notNull(imapServer,"Please specify a server");
		Assert.hasLength(mailDirPath,"Invalid mail dir path");
		Assert.notNull(headers,"Headers must exist");
		//retrieve and open the folder
		Folder f=mailClient.getOpenFolder(remoteFolder, imapServer, headers, true);
		if(log.isDebugEnabled()) {
			log.debug("Retrieved folder: " + remoteFolder);
		}
		//setup the local properties for writing to the mail store
		Map<String,String> writeHeaders = new HashMap<String,String>();
		ServerMailStoreBridge.setMailDirWrite(writeHeaders);
		Properties props=ServerMailStoreBridge.propertiesFromHeaders(writeHeaders);
		Session session = Session.getInstance(props, null);
		Store store = session.getStore(new URLName("maildir://" + mailDirPath));
		
		if(log.isDebugEnabled()) 
			log.debug("Retrieved maildir store:" + mailDirPath);
		File f1 = new File(mailDirPath);
		String path=f1.getAbsolutePath();
		createDir(path);
		createDir(path + "/INBOX");
		createDir(path + "/tmp");
		createDir(path + "/new");
		createDir(path + "/cur");
		
		Folder local=store.getDefaultFolder();
		if(!local.exists()) {
			local.create(Folder.HOLDS_MESSAGES);
		}
		
		local.open(Folder.READ_WRITE);
		Message[] messages=f.getMessages();
		local.appendMessages(messages);
		if(log.isDebugEnabled()) {
			log.debug("Synced messages for local path: " + local.getFullName() + " and remote folder: " + remoteFolder);
		}
		f.close(true);
		store.close();
		local.close(true);
		if(log.isDebugEnabled()) {
			log.debug("Cleaned up folders and stores");
		}
		
	}//end syncMailDir
	
	
	private void createDir(String path) {
		File folderCreate = new File(path);
		if(!folderCreate.exists()) {
			try {
				FileMoverUtil.createFile(folderCreate, true);
			} catch (IOException e) {
				log.error("Couldn't create file " + folderCreate, folderCreate.getName());

			}
		}
	}
	
	/**
	 * This will write a message for the given headers and if there's content specified 
	 * in the headers via headers.get(MailClient.CONTENT) it will write that to the message.s
	 * @param folderPath the folder path to write to
	 * @param headers the headers to write
	 * @param folderFetch the folder to fetch
	 * @throws MessagingException if one occurs
	 */
	public void writeMailToFolder(String folderPath,Map<String,String> headers,String folderFetch) throws MessagingException {
		Assert.notNull(headers,"Can't write null message");
		Assert.hasLength(folderPath,"Can't write non existant path");
		//set mail dir auto create
		ServerMailStoreBridge.setMailDirWrite(headers);
		Properties props=ServerMailStoreBridge.propertiesFromHeaders(headers);
		boolean isAuth=headers.get(MailClient.AUTH_PROPERTY) !=null ? Boolean.parseBoolean(headers.get(MailClient.AUTH_PROPERTY)) : false;
		if(isAuth) {
			String userName=headers.get(MailClient.USER_NAME);
			String password=headers.get(MailClient.PASSWORD);
			Session session=Session.getInstance(props, new MailServerAuthenticator(userName,password));
			Store store = session.getStore(new URLName(folderPath));
			Folder folder = store.getFolder(folderFetch);
			Message message= new MimeMessage(session);
			String content=headers.get(MailClient.CONTENT);

			if(content!=null)
				message.setText(content);
			folder.open(Folder.READ_WRITE);
			folder.appendMessages(new Message[]{message});			

		}
		else {
			Session session=Session.getDefaultInstance(props);
			Store store = session.getStore(new URLName(folderPath));
			Folder folder = store.getFolder(folderFetch);
			Message message= new MimeMessage(session);
			String content=headers.get(MailClient.CONTENT);

			if(content!=null)
				message.setText(content);
			folder.open(Folder.READ_WRITE);
			folder.appendMessages(new Message[]{message});
		}
	}

	public MailClient getMailClient() {
		return mailClient;
	}
	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	private static Logger log=LoggerFactory.getLogger(MailDirService.class);
	private MailClient mailClient;
}
