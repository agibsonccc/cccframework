package com.ccc.mail.core.mailstore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.MailServerAuthenticator;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.BaseServer;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.sun.mail.imap.IMAPFolder;
/**
 * This mail store handles all of the baseline functionality for manipulating mail
 * as well as the server information necessary to manipulate the email.
 * @author Adam Gibson
 *
 */
public abstract class BaseMailStore implements MailStore {
	/* Allow for setter based injection */
	public BaseMailStore() {}


	@Override
	public void addFolder(String name, Server s,Map<String,String> headers)
			throws IllegalArgumentException {
		Properties props=ServerMailStoreBridge.propertiesFromHeaders(headers);
		Assert.notNull(headers.get(USER_NAME));
		Assert.notNull(headers.get(PASSWORD));
		if(ServerMailStoreBridge.isAuth(headers)) {
			String userName=headers.get(USER_NAME);
			String password=headers.get(PASSWORD);

			Assert.notNull(userName,"Headers must contain a user name for authentication");
			Assert.notNull(password,"Headers must contain a password for authentication");
			Assert.hasLength(userName,"User name can not be empty.");
			Assert.hasLength(password,"Password can not be empty.");

			String sslFallBack=headers.get(SSL_FALLBACK);
			Boolean doFallBack=sslFallBack!=null ?Boolean.parseBoolean(sslFallBack) : false;
			//Set up SSL connection
			if(ServerMailStoreBridge.isSSL(headers) || ServerMailStoreBridge.isStartTls(headers))
				props=ServerMailStoreBridge.setSSL(props,headers,s,doFallBack);
			Store store=null;
			if(s.isAuth()) store=ServerMailStoreBridge.getStoreForServer(s,props , new MailServerAuthenticator(userName,password));
			else store=ServerMailStoreBridge.getStoreForServer(s,props ,null);
			try {
				//Attempt to connect to the store
				boolean connected=s.isAuth() ? connectToStore(store,s,headers,0) : connectToStore(store,0);
				if(connected) {
					if(log.isDebugEnabled())
						log.debug("Connected to server: " + s.getServerName());
					//Need to get default folder in order to instantiate anything.
					Folder parent=store.getDefaultFolder();

					//Need to open folder first
					//if(!parent.isOpen())
					//parent.open(Folder.READ_WRITE);
					//Create a new folder
					Folder newFolder=parent.getFolder(name);

					newFolder.create(Folder.HOLDS_FOLDERS);
					newFolder.setSubscribed(true);

					try {
						if(store!=null && store.isConnected())
							store.close();
					} catch (MessagingException e1) {
						e1.printStackTrace();
						log.warn("Trouble closing store: ",e1);
					}
					finally {
						ServerMailStoreBridge.closeFolder(parent);
						ServerMailStoreBridge.closeFolder(newFolder);
					}
				}
				else log.warn("Connection to store failed after 3 retries: stopping.");
			}//end try W
			catch (MessagingException e) {
				log.warn("Couldn't connect to server: " + s.getServerName() + " to add folder name: " + name  + " due to: ",e);
				try {
					if(store!=null && store.isConnected())
						store.close();
				} catch (MessagingException e1) {
					e1.printStackTrace();
				}
				e.printStackTrace();
			}

		}//end if
		else {
			Store store=ServerMailStoreBridge.getStoreForServer(s,props);
			if(s==null)
				throw new IllegalStateException("Store was null");

			Folder parent =null;
			Folder newFolder=null;
			try {
				if(connectToStore(store,s,headers,0)) {
					//Need to get default folder to instantiate anything.
					parent=store.getDefaultFolder();
					//Need to open folder first
					if(!parent.isOpen())
						parent.open(Folder.READ_WRITE);
					//Create the new folder
					newFolder=parent.getFolder(name);

					newFolder.create(Folder.HOLDS_MESSAGES);
					newFolder.setSubscribed(true);

					store.close();
				}
				else log.warn("Couldn't connect to store: " + store.getURLName());
			} catch (MessagingException e) {
				e.printStackTrace();
				log.warn("Couldn't connect to server: " + s.getServerName() + " to add folder name: " + name  + " due to: ",e);

			}
			finally {
				ServerMailStoreBridge.closeFolder(newFolder);
				ServerMailStoreBridge.closeFolder(parent);
				try {
					store.close();
				} catch (MessagingException e) {
					e.printStackTrace();
				}


			}
		}//end else
	}//end addFolder

	private boolean connectToStore(Store store,int numRetries) {
		if(numRetries>=3) return false;
		try {store.connect();}
		catch(MessagingException e) {
			log.warn("Connecting to store: " + store.getURLName() + "failed: attempting " + (3-numRetries) + " more times.");
			numRetries++;
			return connectToStore(store,numRetries);
		}
		return true;
	}//end connectToStore





	private boolean connectToStore(final Store store,com.ccc.mail.core.servers.Server s, Map<String,String> headers, int numRetries) {
		if(numRetries>=6) return false;

		String userName=headers.get(USER_NAME);
		String password=headers.get(PASSWORD);

		Assert.notNull(userName,"Headers must contain a user name for authentication");
		Assert.notNull(password,"Headers must contain a password for authentication");

		//If too many retries, use serverAddress instead of serverName
		String serverLocation = (numRetries>=3) ? s.getServerAddress() : s.getServerName();

		if(!store.isConnected()) {
			try {
				store.connect(serverLocation,s.getPort(),userName,password);
				return true;
			}

			catch(MessagingException e) {
				log.warn("Connecting to store: " + store.getURLName() + "failed: attempting " + (3-numRetries) + " more times.");
				numRetries++;
				return connectToStore(store,s,headers,numRetries);
			}
		}//end connectToStore
		return true;
	}//end connectToStore

	/*
	 * Nessecary if connecting based on BasicUser, rather than headers
	 */
	private boolean connectToStore(final Store store,com.ccc.mail.core.servers.Server s,String uName,String pWord, int numRetries) {
		if(numRetries>=6) return false;

		String userName=uName;
		String password=pWord;

		//If too many retries, use serverAddress instead of serverName
		String serverLocation = (numRetries>=3) ? s.getServerAddress() : s.getServerName();

		if(!store.isConnected()) {
			try {
				store.connect(serverLocation,s.getPort(),userName,password);
				return true;
			}

			catch(MessagingException e) {
				log.warn("Connecting to store: " + store.getURLName() + "failed: attempting " + (3-numRetries) + " more times.");
				numRetries++;
				return connectToStore(store,s,userName,password,numRetries);
			}
		}//end connectToStore
		return true;
	}//end connectToStore


	public List<Message> mailForUser(String uName, String pWord) {

		List<Folder> folders=foldersForUser(uName, pWord);
		//System.out.println(folders!=null);
		List<Message> messages = new ArrayList<Message>();

		for(Folder f : folders) {
			try {
				//Attempt to open up to 3 times and move on if failure
				if(connectToFolder(f,0)) {
					if(log.isDebugEnabled())
						log.debug("Opened folder: " + f.getFullName());

					if(!f.isOpen()) {
						f.open(Folder.READ_ONLY);
					}

					messages.addAll(Arrays.asList(f.getMessages()));
					
				}
				else log.warn("Couldn't open folder: " + f.getFullName() + "skipping");



			} catch (MessagingException e) {
				e.printStackTrace();
				log.warn("After 3 attempts to connect, to folder: " + f.getFullName() + "failure: ",e);

			}

			finally {
				for(Folder f1 : folders)
					ServerMailStoreBridge.closeFolder(f1);
			}
		}
	
		return messages;
	}//end mailForUser


	private boolean connectToFolder(Folder f,int numRetries) throws MessagingException {
		//retry 3 times
		if(numRetries >=3) return false;
		try {
			if(!f.isOpen()) {
				f.open(Folder.READ_ONLY);
			}
			if(log.isDebugEnabled())
				log.debug("Opened folder: " + f.getFullName());
			return true;

		}
		catch (MessagingException e) {
			log.warn("Error retrieving mail for user",e);
			numRetries++;
			return connectToFolder(f,numRetries);
		}

	}//end connectToFolder

	/**
	 * This will return all of the folders contained within this mail store.
	 */
	public List<Folder> foldersForUser(String uName, String pWord) {
		
		List<Folder> ret = new ArrayList<Folder>();
		if(incomingServers==null)
			return ret;
		try {
			for(com.ccc.mail.core.servers.Server s : incomingServers) {
				String userName=uName;
				String password=pWord;

				Assert.notNull(userName);
				Assert.notNull(password);
				Assert.hasLength(userName);
				Properties props=ServerMailStoreBridge.propertiesForServer(s);
				Store store=null;
				if(s.isAuth())
					store=ServerMailStoreBridge.getStoreForServer(s,props ,new MailServerAuthenticator(userName,password));
				else  store=ServerMailStoreBridge.getStoreForServer(s,props,null);
				//Ensure connection
				if(!store.isConnected())

					if(connectToStore(store,s,userName,password,0)) {
						Folder[] folders=store.getDefaultFolder().list();
						ret.addAll(Arrays.asList(folders));
						if(store!=null && store.isConnected())
							store.close();
					}
					else  log.warn("Couldn't connect to store after three retries: exiting");

			}
			log.info("Retrieved folders!");
			return ret;
		} catch (MessagingException e) {
			e.printStackTrace();
			log.warn("Error in retrieving folder for user",e);
		}
		return null;
	}//end foldersForUser

	public List<Message> messagesForFolder(Folder f,Map<String,String> headers,com.ccc.mail.core.servers.Server s)
			throws IllegalArgumentException {
		//Headers for connecting in case of an inconsistency
		String auth=headers.get(MailClient.IS_AUTH);
		boolean isAuth=auth!=null ? Boolean.parseBoolean(auth) : false;
		String userName=null;
		String password=null;
		String port=null;
		String host=null;
		String serverIP=null;
		//Connect authenticated
		if(isAuth) {
			userName=headers.get(USER_NAME);
			password=headers.get(PASSWORD);
			port=headers.get(MailClient.PORT);
			host=headers.get("server");
			serverIP=headers.get("serverIP");
			int realPort= port!= null ? Integer.parseInt(port) : -1;
			Assert.notNull(f);
			if(!f.getStore().isConnected()) {
				try {
					f.getStore().connect(host,realPort,userName,password);
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			if(!f.isOpen()) f.open(Folder.READ_ONLY);
			//No messages
			if(f.getMessageCount() == 0)
				return new ArrayList<Message>(1);

			if(log.isDebugEnabled())
				log.debug("Opened folder: " + f.getFullName());
			return Arrays.asList(f.getMessages());
		} catch (MessagingException e) {
			log.warn("Error retrieving messages for folder: " + f.getFullName(),e);
			e.printStackTrace();
		}
		//Close once done
		try {
			f.getStore().close();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return null;
	}//end messagesForFolder

	public boolean supportsIMAP() {
		for(com.ccc.mail.core.servers.Server s : incomingServers) {
			if(s.getServerType().equals(IMAP_SERVER)) {
				return true;
			}
		}
		return false;
	}//end supportsIMAP

	public boolean supportsPOP() {
		for(com.ccc.mail.core.servers.Server s : incomingServers) {
			if(s.getServerType().equals(POP_SERVER)) {
				return true;
			}
		}
		return false;
	}//end supportsPOP

	public boolean canReceiveEncrypted() {
		for(com.ccc.mail.core.servers.Server s : outgoingServers) {
			BaseServer s1=(BaseServer) s;
			if(s1.isEncryptedIn())
				return true;
		}

		return false;
	}//end canReceiveEncrypted

	public boolean canSendEncrypted() {
		for(com.ccc.mail.core.servers.Server s : outgoingServers) {
			BaseServer s1=(BaseServer) s;
			if(s1.isEncryptedOut())
				return true;
		}

		return false;
	}//end canSendEncrypted

	public void wipeFolders(String uName, String pWord) {
		List<Folder> folders=foldersForUser(uName,pWord);
		for(Folder f : folders) {
			try {
				if(!f.isOpen())
					f.open(Folder.READ_WRITE);
				f.delete(true);
			} catch (MessagingException e) {
				e.printStackTrace();
				log.warn("Error in deleting folders: " + f.getFullName());
			}
		}
	}//end wipeFolders


	public void deleteMessages(List<Message> messages) {
		Set<Folder> inUse = new HashSet<Folder>();

		for(Message m : messages) {
			try {
				m.setFlag(Flags.Flag.DELETED, true);
				Folder f=m.getFolder();
				inUse.add(f);
				f.expunge();


				if(log.isDebugEnabled())
					log.debug("Deleted message from folder: " + m.toString());

			} catch (MessagingException e) {
				log.warn("Errrors deleting message",e);
				e.printStackTrace();
			}

			for(Folder f : inUse)
				ServerMailStoreBridge.closeFolder(f);
		}

	}//end deleteMessages

	public void moveMessage(List<Message> messagesToMove, Folder from, Folder to) {
		Assert.notNull(messagesToMove);
		Assert.notNull(from);
		Assert.notNull(to);


		if(!from.isOpen()) {
			try {
				from.open(Folder.READ_WRITE);
			} catch (MessagingException e) {
				e.printStackTrace();
				log.warn("Error moving messages from folder: " + from.getName());
			}
			try {
				to.open(Folder.READ_WRITE);
			} catch (MessagingException e) {
				e.printStackTrace();
				log.warn("Error moving messages to folder: " + to.getName());
			}
		}
		Set<Folder> inUse = new HashSet<Folder>();
		for(Message m : messagesToMove) {
			try {

				Folder f=m.getFolder();
				inUse.add(f);

				if(!f.isOpen())
					f.open(Folder.READ_WRITE);
				m.setFlag(Flags.Flag.DELETED, true);
			} 
			catch (MessagingException e1) {
				e1.printStackTrace();
				log.warn("Error setting flag to delete for moving message",e1);
			}
		} 

		try {
			to.appendMessages(from.expunge());
			log.info("Deleted Messages from old and added to new");

		} catch (MessagingException e) {
			e.printStackTrace();
		}
		finally {
			ServerMailStoreBridge.closeFolder(from);
			ServerMailStoreBridge.closeFolder(to);

		}
	}//end moveMessage


	/* This converts a list of messages in to an array. */
	private Message[] convert(List<Message> messages) {
		Message[] ret = new Message[messages.size()];

		for(int i=0;i<ret.length;i++) {
			ret[i]=messages.get(i);
		}
		return ret;
	}//end convert


	public void addFolder(Folder f,com.ccc.mail.core.servers.Server s) throws IllegalArgumentException {
		try {
			//Append to top level.
			if(!f.exists()) {
				f.open(Folder.READ_WRITE);
				if(!f.create(Folder.HOLDS_MESSAGES))
					throw new IllegalStateException("Folder couldn't be created.");
				f.setSubscribed(true);

			}

			log.info("Created folder: " + f.getFullName());
		} catch (MessagingException e) {
			e.printStackTrace();
			log.warn("Problem adding folder:",e);
		}

		ServerMailStoreBridge.closeFolder(f);
	}//end addFolder


	public List<Store> getStores() {
		return stores;
	}

	public void setStore(List<Store> stores) {
		this.stores = stores;
	}

	private void validateIncomingServers(Collection<com.ccc.mail.core.servers.Server> servers) throws IllegalArgumentException {
		incomingServers = new ArrayList<Server>();
		for(com.ccc.mail.core.servers.Server s : servers) {
			if(!validServerForIncoming(s))
				throw new IllegalArgumentException("Server with name: " + s.getServerName() + " not a valid incoming server. Valid is either pop or imap");
			incomingServers.add(s);
		}
	}//end validateIncomingServers

	public List<SMTPServer> outGoingServers() {
		return outgoingServers;
	}

	public List<com.ccc.mail.core.servers.Server> incomingServers() {
		return incomingServers;
	}

	public List<com.ccc.mail.core.servers.Server> getIncomingServers() {
		return incomingServers;
	}




	public void setIncomingServers(List<com.ccc.mail.core.servers.Server> incomingServers) {
		validateIncomingServers(incomingServers);
		this.incomingServers = incomingServers;
	}

	public List<SMTPServer> outgoingServers() {
		return outgoingServers;
	}

	public List<SMTPServer> getOutgoingServers() {
		return outgoingServers;
	}

	@Override
	public boolean addFolderToServer(Folder toAdd, com.ccc.mail.core.servers.Server addTo,Map<String,String> headers) {
		Store s=ServerMailStoreBridge.getStoreForServer(addTo, ServerMailStoreBridge.propertiesFromHeaders(headers));

		Folder parent=null;
		Folder f=null;
		try {
			parent=s.getDefaultFolder();
			f=parent.getFolder(toAdd.getFullName());
			//New folder
			if(!f.exists()) {
				if(!f.isOpen())
					f.open(Folder.READ_WRITE);
				f.create(Folder.HOLDS_MESSAGES);


				return true;
			}

			//Folder already exists
			else  return true;

		} catch (MessagingException e) {
			log.warn("Error getting default folder from server: " +addTo.getServerName(),e);
			e.printStackTrace();
			return false;

		}
		finally {
			ServerMailStoreBridge.closeFolder(parent);
		}
	}//end addFolderToServer



	@Override
	public boolean deleteFolder(String name, com.ccc.mail.core.servers.Server s, Map<String, String> headers) {
		if(ServerMailStoreBridge.isAuth(headers)) {
			String userName=headers.get(USER_NAME);
			String password=headers.get(PASSWORD);

			Store store=ServerMailStoreBridge.getStoreForServer(s, ServerMailStoreBridge.propertiesFromHeaders(headers), new MailServerAuthenticator(userName,password));
			Folder f=null;
			boolean deleted= false;
			try {
				f = store.getFolder(name);
				if(!f.exists())
					throw new IllegalStateException("Tried to delete non-existing folder: " + name);

				deleted=f.delete(true);


			} catch (MessagingException e) {
				log.warn("Error in deleting folder: " + name ,e);
				e.printStackTrace();
			}

			finally {
				ServerMailStoreBridge.closeFolder(f);
			}
		}//end if
		//No authentication
		else {
			Store store=ServerMailStoreBridge.getStoreForServer(s, ServerMailStoreBridge.propertiesFromHeaders(headers));
			Folder f=null;
			try {
				f = store.getFolder(name);
				if(!f.exists())
					throw new IllegalStateException("Tried to delete non-existing folder: " + name);

				return f.delete(true);
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		return false;
	}//end deleteFolder


	


	public void setOutgoingServers(List<SMTPServer> outgoingServers) {
		validateOutGoingServers(outgoingServers);
		this.outgoingServers = outgoingServers;
	}

	public void addFolder(Folder toAdd, Folder to) {
		IMAPFolder f=(IMAPFolder) toAdd;
		try {
			if(!toAdd.isOpen())
				toAdd.open(Folder.READ_WRITE);

			if(!to.isOpen())
				toAdd.open(Folder.READ_WRITE);

			//Add support for holding folders.
			to.create(Folder.HOLDS_FOLDERS);
			//Add support for holding messages.
			f.create(Folder.HOLDS_MESSAGES);

			if(log.isDebugEnabled())
				log.debug("Added folder: " + toAdd.getFullName() + " to " + to.getFullName());

		} catch (MessagingException e) {
			e.printStackTrace();
			log.warn("Error adding folder: " + toAdd.getFullName() + " to " + to.getFullName());
		}

	}//end addFolder

	/* This will validate the servers and set them as the outgoing servers for this mailbox */
	private void validateOutGoingServers(Collection<SMTPServer> servers) {
		outgoingServers = new ArrayList<SMTPServer>();
		for(SMTPServer s : servers) {
			if(!validServerForOutGoing(s))
				throw new IllegalArgumentException("Invalid server for outgoing: must be smtp");
			outgoingServers.add(s);
		}

	}//end validateOutGoingServers

	private boolean validServerForIncoming(com.ccc.mail.core.servers.Server s) {
		return( s.getServerType().equals(POP_SERVER) || s.getServerType().equals(IMAP_SERVER) || s.getServerType().equals(IMAP_SSL) || s.getServerType().equals(POP_SSL));
	}

	private boolean validServerForOutGoing(com.ccc.mail.core.servers.Server s ) {
		return s.getServerType().equals(SMTP_SERVER) || s.getServerType().equals(SMTP_SSL);
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public void setOutGoingServers(List<SMTPServer> outgoing) {
		this.outgoingServers=outgoing;
	}
	


	public void setStores(List<Store> stores) {
		this.stores = stores;
	}

	public List<Store> stores() {
		return stores;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}



	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}



	private List<Store> stores;


	/* IMAP or POP Servers */
	protected List<com.ccc.mail.core.servers.Server> incomingServers;
	/* SMTP Servers */
	protected List<SMTPServer> outgoingServers;

	private String userName;

	@Autowired(required=false)
	private PasswordEncoder passwordEncoder;

	private static Logger log=LoggerFactory.getLogger(BaseMailStore.class);

}//end BaseMailStore
