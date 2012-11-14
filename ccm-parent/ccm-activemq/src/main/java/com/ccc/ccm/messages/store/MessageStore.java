package com.ccc.ccm.messages.store;

import java.util.List;

import javax.jms.Message;
/**
 * This is an interface for managing messages.
 * It could be used as a placeholder for conversations,
 * or as a way of wiring in to a message persistence.
 * @author Adam Gibson
 *
 */
public interface MessageStore {

	/**
	 * This will delete the given message from the store.
	 * @param toDelete the message to delete
	 */
	public void deleteMessage(Message toDelete);
	/**
	 * This adds a message to the store.
	 * @param toAdd the message to add
	 */
	public void addMessage(Message toAdd);
	
	/**
	 * This will return the list of messages in this store.
	 * @return the list of messages in this store
	 */
	public List<Message> messages();
}
