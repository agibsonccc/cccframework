package com.ccc.mail.core.mailbox;

import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;

import com.ccc.mail.core.mailstore.MailStore;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
/**
 * This is a mailbox that a user communicates with when sending and receiving email.
 * @author Adam Gibson
 *
 */
public interface MailBox extends MailConstants {



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
	 * @param headers connection headers
	 * @return the messages for this folder
	 */
	public List<Message> messagesForFolder(Folder f,Map<String,String> headers, Server s);

}//end MailBox
