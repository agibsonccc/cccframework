package com.ccc.ccm.conversation;

import java.io.Serializable;

/**
 * This is a conversation listener to be used
 * with a conversation controller.
 * @author Adam Gibson
 *
 */
public interface ConversationListener extends Serializable {
	/**
	 * This will fire an event passing the message and the sender of the message
	 * @param message the message that was sent
	 * @param sender the sender of the message
	 * @param name the name of the destination to fire for
	 */
	public void fireEvent(String sender,String message,String name);
}//end ConversationListener
