package com.ccc.ccm.conversation;

import javax.jms.Message;

import com.ccc.ccm.persistance.StoredMessage;
/**
 * This is a wrapper that contains a jms message 
 * and a stored message message 
 * @author Adam Gibson
 *
 */
public class ConversationMessage {

	
	public ConversationMessage(Message message, StoredMessage storedMessage) {
		super();
		this.message = message;
		this.storedMessage = storedMessage;
	}


	public StoredMessage getStoredMessage() {
		return storedMessage;
	}

	
	public Message getMessage() {
		return message;
	}
	
	private Message message;
	
	private StoredMessage storedMessage;
}//end ConversationMessage

