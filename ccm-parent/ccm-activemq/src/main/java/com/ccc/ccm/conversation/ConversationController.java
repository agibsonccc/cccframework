package com.ccc.ccm.conversation;

import java.io.Serializable;
import java.util.Map;

/**
 * This is a conversation controller that handles
 * the operations on a conversation such as 
 * disconnecting parties, adding participants
 * to a conversation,etc
 * @author Adam Gibson
 *
 */
public interface ConversationController extends Serializable {
	
	/**
	 * This will generate a unique id for a conversation 
	 * @return a unique id for a conversation
	 */
	public String generateConversationId();
	
	/**
	 * This returns a list of conversations currently happening.
	 * @return a map of conversations currently happening
	 */
	public Map<String,Conversation> currentConversations();
	
	/**
	 * This will return a conversation with the given queue name
	 * @param name the name of the destination to get 
	 * a conversation for
	 * @return a conversation with the given destination, or null
	 * if none found
	 */
	public Conversation getByDestinationName(String name);
	
	/**
	 * This will initialize a conversation between
	 * any number of people.
	 * @param conversation the conversation to initialize
	 */
	public void initConversation(Conversation conversation);
	

	/**
	 * This will close the given conversation removing all participants
	 * and closing all destinations where necessary.
	 * @param toClose the conversation to close
	 */
	public void closeConversation(Conversation toClose);
	
	
	/**
	 * This will send a message to the given conversation from the
	 * given sender
	 * @param sender the sender of the message
	 * @param conversation the conversation to send to
	 * @param message the message to send
	 */
	public void sendMessage(String sender,Conversation conversation,String message);
	
	/**
	 * This will register a listener with this controller
	 * @param listener a listener to register
	 * @param name the name of the listener
	 */
	public void registerListener(String name,ConversationListener listener);
	
	/**
	 * This will remove a listener from this controller
	 * @param name the name of the listener to remove
	 */
	public void removeListener(String name);
	
	/**
	 * This returns whether a conversation controller should fire events or not
	 * @return true if the conversation controller should fire events, false otherwise
	 */
	public boolean fireEvents();
	
	/**
	 * This will cause the controller to stop firing events
	 */
	public void stopFiring();
	
	/**
	 * This will cause the controller to start firing events
	 */
	public void startFiring();
}//end ConversationController
