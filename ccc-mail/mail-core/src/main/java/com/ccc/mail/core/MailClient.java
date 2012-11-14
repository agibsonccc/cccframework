package com.ccc.mail.core;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.internet.AddressException;

import com.ccc.mail.core.mailstore.MailStore;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.mail.ssl.exceptions.SSLErrorException;

/**
 * This is a mail client that will be used when setting up for sending and receiving of mail.
 * @author Adam Gibson
 *
 */
public interface MailClient extends MailConstants {

	/**
	 * This will extract all of the attachments from all of the passed in messages
	 * @param messages the messages to get attachments for
	 * @return all of the files within the given messages
	 */
	public Collection<File> getAttachments(Message[] messages);
	
	/**
	 * This will send a message based on the given headers to the given server.
	 * @param headers the connection headers to use
	 * @param s the server to use
	 * @param m the message to send
	 * @return true if the message was sent, false otherwise
	 */
	public boolean sendMessageToServer(Map<String,String> headers,Server s,Message m);
	
	
	/**
	 * This will return a folder that is opened with the given parameters.
	 * This is equivalent to calling getFolder(...) and opening it yourself.
	 * @param name the name of the folder to retrieve
	 * @param incoming the server the folder is on
	 * @param headersForServer the connection headers to use
	 * @param write whether to open the file in write mode
	 * @return an opened folder, or null on error
	 * @throws SSLErrorException 
	 * @throws MessagingException 
	 */
	public Folder getOpenFolder(String name,Server incoming,Map<String,String> headersForServer,boolean write) throws SSLErrorException, MessagingException;
	
	/**
	 * This will return the folder for the given name
	 * based on the default incoming server
	 * @param name the name of the folder to retrieve
	 * @return the folder with the given name from the default server,
	 * or null if an error occurs or is not found
	 * @throws SSLErrorException 
	 * @throws MessagingException 
	 */
	public Folder getFolder(String name,Server incoming,Map<String,String> headersForServer) throws SSLErrorException, MessagingException;
	
	/**
	 * This will return whether the given folder name has new messages or not.
	 * @param name name of the folder to check
	 * @param incoming server the folder is on
	 * @param headers connection headers
	 * @return whether the given folder name has new messages or not.
	 * @throws MessagingException if one occurs
	 * @throws SSLErrorException 
	 */
	public boolean folderHasNewMessage(String name,Server incoming,Map<String,String> headers) throws MessagingException, SSLErrorException;
	
	/**
	 * This will retrieve the new messages in a given folder.
	 * @param name the name of the folder to retrieve from
	 * @param incoming the server the folder is on
	 * @param headers the connection headers to use when connecting to
	 * the server
	 * @return the new messages in a folder or null otherwise
	 * @throws MessagingException 
	 * @throws SSLErrorException 
	 */
	public Message[] getNewMessagesForFolder(String name,Server incoming,Map<String,String> headers) throws MessagingException, SSLErrorException;
	
	/**
	 * Returns number of messages stored in a given folder
	 * 
	 * @param name: Folder name
	 * @param incoming: Containing Server
	 * @param headersForServer: Connection Headers
	 * @return: number of messages stored in a given folder
	 * @throws SSLErrorException 
	 */
	public int numMessagesInFolder(String name,Server incoming,Map<String,String> headersForServer)
			throws MessagingException, SSLErrorException;
	
	/**
	 * This will send the given message using the given smtp server
	 * to the given server.
	 * @param toSend the message to send
	 * @param isHtml true if html email is to be sent, false otherwise
	 * @return true if the message was sent,false otherwise
	 * @throws MessagingException  if one is thrown
	 * @throws AddressException  if one is thrown
	 */
	public boolean sendMail(Map<String,String> headers,boolean isHtml) throws AddressException, MessagingException;


	/**
	 * This will send start tls mail.
	 * @param headers the headers to use for the properties of the sending session.
	 * @param isHtml whether the mail to be sent is htmlemail or not
	 * @return true if the message was sent properly, false otherwise
	 */
	public boolean sendStartTlsMail(Map<String,String> headers,boolean isHtml);

	/**
	 * This will send a mail with the given headers.
	 * to the given address.
	 * @param headers the headers of this message
	 * @param isHtml whether html email is to be sent or not
	 * @return true if the message was sent, false otherwise
	 * @throws MessagingException  if one occurs
	 * @throws AddressException  if one occurs
	 */
	public boolean sendSSLMail(Map<String,String> headers,boolean isHtml) throws AddressException, MessagingException;

	
	/**
	 * This will add a folder with the given name to the given server.
	 * @param s the server to add to
	 * @param name the name of the folder to add
	 * @param userName if a user name is required, used for authz
	 * @param password if a password is required, used for authz
	 * @return true if the folder was added, false otherwise
	 */
	public boolean addFolderToServer(Server s,String name,String userName,String password);
	
	
	/**
	 * This will add a folder to the given server with the given name,
	 * assuming no authentication.
	 * @param s the server to add to
	 * @param name the name of the folder to add
	 * @return true if the folder was added, false otherwise
	 */
	public boolean addFolderToServer(Server s,String name);

	/**
	 * This will send mail with the given files as attachments using the given headers
	 * @param headers the headers of this message
	 * @param toAttach the files to attach
	 * @param isHtml if the email to be sent is html or not
	 * @return true if the message was sent, false otherwise
	 * @throws MessagingException if one occurs
	 * @throws AddressException  if one occurs
	 */
	public boolean sendMailWithAttachments(Map<String,String> headers,File[] toAttach,boolean isHtml) throws AddressException, MessagingException;


	/**
	 * This will add an attachment to the given message
	 * @param attachTo the message to attach to
	 * @param toAttach the files to attach
	 * @throws MessagingException 
	 */
	public void addAttachment(Message attachTo,File[] toAttach) throws MessagingException;


	/**
	 * This will delete the given message from the server.
	 * @param toDelete the message to delete
	 * @return true if the message was deleted, false otherwise
	 */
	public boolean deleteMessage(Message toDelete);


	/**
	 * This returns the mail store used by this client.
	 * @return the mail store used by this client.
	 */
	public MailStore storeForMail();


	/**
	 * This will return a list of messages for the given user.
	 * @param u the user to retrieve messages for
	 * @return the list of messages for the given user
	 */
	public List<Message> messagesForUser(String uName, String pWord);
	

	/**
	 * This will save the given message as a draft.
	 * @param toSave the message to save as a draft
	 * @return true if the message was saved as a draft,
	 * false otherwise
	 */
	public boolean saveDraft(Message toSave);



	/**
	 * This returns the mail store associated with this mailbox
	 * @return the mail store associated with this mailbox
	 */
	public MailStore mailStore();
	/**
	 * This returns a list of all the folders in this mailbox
	 * @return the list of folders for this mailbox.
	 */
	public List<Folder> folders(String uName, String pWord);


	/**
	 * This returns a list of messages from a given folder.
	 * @param f the folder to get messages for
	 * @param headers the connection headers to use
	 * @return the messages for this folder
	 */
	public List<Message> messagesForFolder(Folder f,Map<String,String> headers, Server s);

	/**
	 * This will append a folder to another folder.
	 * @param newFolderName the name of the new folder
	 * @param to the folder to append to
	 * @return true if the folder was added, false otherwise
	 */
	public boolean addFolderToFolder(String newFolderName,Folder to);
	


	
	/**
	 * This will return all of the folders on the given server.
	 * @param s the server to retrieve folders form
	 * @param headers the headers to connect with
	 * @return the list of folders on this server
	 * @throws SSLErrorException 
	 * @throws MessagingException 
	 */
	public List<Folder> foldersForServer(Server s,Map<String,String> headers) throws SSLErrorException, MessagingException;
	
	
	/**
	 * This will return the folders for a server relative to an email
	 * @param s the server to get email for
	 * @param headers the headers to connect with
	 * @param email the email to cache for
	 * @return the list of folders with the server
	 * @throws SSLErrorException
	 */
	public List<Folder> foldersForServerCache(String email,Server s,Map<String,String> headers) throws SSLErrorException, MessagingException;
		
	/**
	 * This will return all of the folders on the given server and attempt to open each one
	 * obtained.
	 * @param s the server to retrieve folders form
	 * @param headers the headers to connect with
	 * @param readOnly whether the mode for folders is read only or not
	 * @return the list of folders on this server opened
	 */
	public List<Folder> getOpenFoldersForServer(Server s,Map<String,String> headers,boolean readOnly);
	
	/**
	 * This will send an email with attachments
	 * @param headers the connection headers to use
	 * @param toAttach the files to attach
	 * @param s the server to send from
	 * @param isHtml whether the email is html or not
	 * @return true if the message was sent, false otherwise
	 * @throws AddressException if one occurs
	 * @throws MessagingException if one occurs
	 */
	public boolean sendMailWithAttachments(Map<String, String> headers,
			File[] toAttach, Server s,boolean isHtml) throws AddressException,
			MessagingException;

	
	/**
	 * This will attach the given file to the given message.
	 * @param toAttach the file to attach
	 * @param attachTo the message to attach to
	 * @throws MessagingException if one occurs
	 */
	public void attachFile(File toAttach,Message attachTo) throws MessagingException;
	
	/**
	 * This will prepare and return a reply message 
	 * @param m the message to reply to
	 * @param replyAll whether this is a reply all message
	 * @return a prepared message for replying
	 */
	public Message prepareReply(Message m,boolean replyAll);
	
	/**
	 * Setter for a mail store. A client talks to a mail store in terms of the server.
	 * @param store the store to use for this client.
	 */
	public void setMailStore(MailStore store);
	
	/**
	 * This will return the list of incoming servers used by this mail client.
	 * @return the list of incoming servers used by this mail client
	 */
	public List<Server> incomingServers();
	
	/**
	 * This will return the list of outgoing servers used by this mailclient 
	 * for sending mail.
	 * @return the servers used for sending mail
	 */
	public List<SMTPServer> outgoingServers();
	
	/**
	 * Setter for outgoing servers
	 * @param outgoing
	 */
	public void setOutgoingServers(List<SMTPServer> outgoing);
	
	/**
	 * Setter for incoming servers
	 * @param incoming
	 */
	public void setIncomingServers(List<Server> incoming);
	
	/**
	 * This will send the given message using the given server.
	 * @param s the server to use
	 * @param m the message to use
	 * @param headers connection headers such as user name and password
	 * @throws NoSuchProviderException 
	 * @throws MessagingException 
	 */
	public void sendMailWithServer(Server s,Map<String,String> headers,Message m) throws NoSuchProviderException, MessagingException;
	
	/**
	 * This will login for the mail session as well as keep this session in memory for the mail client to use as a default.
	 * THis will also add the return session to a cached mail session that allows for speedier connections.
	 * @param headers the headers to connect with
	 * @param connectTo the server to connect to
	 * @return the session used by this object
	 */
	public Session login(Map<String,String> headers,Server connectTo);
	
	
	/**
	 * This will logout the session with the given name.
	 * @param name the name of the session to log out of
	 * @throws NoSuchProviderException if the provider doesn't exist
	 * @throws MessagingException if a problem occurs logging out of the mail session
	 */
	public void logout(String name) throws NoSuchProviderException, MessagingException;
	
	
	/**
	 * This will return a map of the mail sessions to the server sessions already
	 * used by this mail client indexed by name. 
	 * @return a map of the servers and mail sessions used by this client.
	 */
	public  Map<String,Session> mailSessions();
	

	public final static String FROM_NAME="fromName";
	
	public final static String TO_ADDRESSES="to";

	public final static String FROM_ADDRESS="from";

	public final static String CC_ADDRESSES="cc";

	public final static String BCC_ADDRESSES="bcc";

	public final static String SUBJECT="subject";

	public final static String ATTACHMENTS="attachments";

	public final static String IS_SSL="ssl";

	public final static String PORT="port";

	public final static String IS_AUTH="auth";

	
	public final static String CONTENT="content";

	public final static String FLAGGED="flagged";


	public final static String START_TLS="mail.smtp.starttls.enable";

	
	public final static String WRITE_MAIL_DIR="mail.store.maildir.autocreatedir";

	
}//end MailClient
