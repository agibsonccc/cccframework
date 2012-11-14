package com.ccc.mail.core.mailbox;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Folder;
import javax.mail.Message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ccc.mail.core.mailstore.MailStore;
import com.ccc.mail.core.servers.Server;

/**
 * This is a mailbox tied to a user and a mail store.
 * @author Adam Gibson
 *
 */
public abstract class BaseMailbox implements MailBox {
	
	/**
	 * This sets up the mailbox with the parameters it needs to function.
	 * @param incomingServers the incoming servers for this mailbox
	 * @param outgoingServers the outgoing servers for this mailbox.
	 * @param mailStore the mail store to get messages/
	 * @param userName the user name associated with this mailbox.
	 * @param client the user client that is associated with managing users for this mail instance.
	 */
	public BaseMailbox(Set<Server> incomingServers,Set<Server> outgoingServers,MailStore mailStore) {
		Assert.notNull(userName);
		Assert.notNull(incomingServers);
		Assert.notNull(outgoingServers);
		
		
	}
	

	
	/**
	 * This will allow for setter based injection.
	 */
	public BaseMailbox() {
		
	}

	public MailStore getMailStore() {
		return mailStore;
	}





	public void setMailStore(MailStore mailStore) {
		this.mailStore = mailStore;
	}


	

	public MailStore mailStore() {
		return mailStore;
	}
	
	public List<Folder> folders(String uName, String pWord) {
		return mailStore.foldersForUser(uName,pWord);
	}

	public List<Message> messagesForFolder(Folder f,Map<String,String> headers, Server s) {
		return mailStore.messagesForFolder(f,headers,s);
	}
	/* This will validate the servers and set them as the incoming servers for this mailbox */

	
	
	@Autowired(required=false)
	private MailStore mailStore;
	
	private String userName;
}//end BaseMailbox
