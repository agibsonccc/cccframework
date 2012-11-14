package com.ccc.mail.core.mailstore;
import javax.mail.Message;
import javax.mail.Store;

import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;

import java.util.List;
import java.util.Map;

import javax.mail.Folder;
/**
 * This is a mail store that stores emails and folders for a given user account/mailbox
 * @author Adam Gibson
 *
 */
public interface MailStore extends MailConstants {

	/**
	 * This returns a list of out going servers for this mail store.
	 * @return the list of outgoing servers for this mail store
	 */
	public List<SMTPServer> outgoingServers();

	/**
	 * This returns a list of in
	 * @return
	 */
	public List<com.ccc.mail.core.servers.Server> incomingServers();

	/**
	 * This will return the list of messages in all folders associated with this mail store.
	 * @return the list of messages in all folders associated with this mail store.
	 */
	public List<Message> mailForUser(String uName, String pWord);

	/**
	 * This returns the list of folders for the user
	 * associated with this mail store.
	 * @return the list of folders for the user 
	 * associated with this mail store
	 */
	public List<Folder> foldersForUser(String uName, String pWord);

	/**
	 * This will delete the given folder from the given folder using
	 * the given connection headers.
	 * @param name the name of the folder to delete
	 * @param s the server to sue
	 * @param headers the headers to use for connecting
	 * @return true if the folder was deleted, false otherwise
	 */
	public  boolean deleteFolder(String name,com.ccc.mail.core.servers.Server s,Map<String,String> headers);



	/**
	 * This will retrieve the list of messages from the given folder.
	 * @param f the folder to retrieve messages from
	 * @param headers the headers for connecting to this folder's store
	 * @return the list of messages in this folder
	 * @throws IllegalArgumentException if folder is null, or isn't in this mail store
	 */
	public List<Message> messagesForFolder(Folder f,Map<String,String> headers, Server s) throws IllegalArgumentException;

	/**
	 * This will return true if the mail store supports imap, false otherwise
	 * @return whether the mail store supports imap
	 */
	public boolean supportsIMAP();

	/**
	 * This will return true if the mail store supports POP,false otherwise
	 * @return true if the mail store supports pop, false otherwise
	 */
	public boolean supportsPOP();

	/**
	 * This returns whether the mail store can receive encrypted messages or not
	 * @return true if the mail store can receive encrypted messages, false otherwise
	 */
	public boolean canReceiveEncrypted();


	/**
	 * This returns whether the mail store can send encrypted mail or not
	 * @return true if the mail store can send encrypted, false otherwise
	 */
	public boolean canSendEncrypted();


	/**
	 * This will wipe all of the folders in this mail store.
	 */
	public void wipeFolders(String iName, String pWord);

	/**
	 * This will delete this this of messages.
	 * @param messages the messages to delete
	 */
	public void deleteMessages(List<Message> messages);

	/**
	 * This will move a list of messages from a folder to another one.
	 * @param messagesToMove the messages to move
	 * @param from the folder to move messages from
	 * @param to the folder to move the messages to
	 */
	public void moveMessage(List<Message> messagesToMove,Folder from,Folder to);

	

	/**
	 * This will add a folder to the mail store using the given server.
	 * Note that this will assume a top level  add.
	 * @param name the name of the folder  to add
	 * @param the server to add to
	 * @param headers the headers to use to connect
	 * @throws IllegalArgumentException if f is null
	 */
	public void addFolder(String name,Server s,Map<String,String> headers) throws IllegalArgumentException;

	/**
	 * This adds a folder to the given folder.
	 * @param toAdd the folder to add
	 * @param to the folder to append to
	 */
	public void addFolder(Folder toAdd,Folder to);


	/**
	 * This will add the given folder to the given server.
	 * @param toAdd the folder to add
	 * @param addTo the server to add to
	 * @param headers the headers used to connect
	 * @return true if the folder was added, false otherwise
	 */
	public boolean addFolderToServer(Folder toAdd, com.ccc.mail.core.servers.Server addTo,Map<String,String> headers);




	/**
	 * Setter for incoming servers, can only be of type pop or imap
	 * @param incomingServers the list of incomings servers to populate with
	 */
	public void setIncomingServers(List<com.ccc.mail.core.servers.Server> incomingServers);
	/**
	 * Setter for outgoing servers, only smtp is allowed
	 * @param outgoing the list of outgoing servers
	 */
	public void setOutGoingServers(List<SMTPServer> outgoing);

	/**
	 * This sets the mail stores used by this store to retrieve data.
	 * @param stores the stores to use
	 */
	public void setStores(List<Store> stores);

	/**
	 * This returns the list of stores being used by this mail store.
	 * @return the list of stores being used by this mail store
	 */
	public List<Store> stores();

	


}//end MailStore
