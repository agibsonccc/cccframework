package com.ccc.ccm.conversation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

import com.ccc.ccm.persistance.StoredMessage;

/**
 * This encapsulates a conversation between any number 
 * of people.
 * @author Adam Gibson
 *
 */
public class Conversation implements Serializable {

	public Conversation(String id) {
		super();
		this.id = id;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -4096555181958396683L;
	/**
	 * This will add a message to this conversation
	 * @param message the message to add
	 */
	public void addMessage(StoredMessage message) {
		Assert.notNull(id,"Conversation id has not been set!");
		message.setConversationId(id);
		messages.add(message);
	}
	/**
	 * This will remove the given message from the conversation
	 * @param message the message to remove
	 */
	public void deleteMessage(StoredMessage message) {
		messages.remove(message);
	}
	/**
	 * This will delete the ith message
	 * @param i the message to delete
	 */
	public void deleteMessage(int i) {
		messages.remove(i);
	}
	/**
	 * Get the ith message
	 * @param i the index of the message to get
	 * @return the ith message in the conversation
	 */
	public StoredMessage get(int i) {
		return messages.get(i);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	/**
	 * This will sort the messages according to time sent.
	 * The most recent going first
	 */
	public void sortMessagesByMostRecent() {
		if(messages!=null)
			Collections.sort(messages, new TimestampComparator());
	}


	public List<StoredMessage> getMessages() {
		return messages;
	}

	public void setMessages(List<StoredMessage> messages) {
		this.messages = messages;
	}

	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	private String destination;

	private List<StoredMessage> messages = new ArrayList<StoredMessage>();;
	private String id;
}
