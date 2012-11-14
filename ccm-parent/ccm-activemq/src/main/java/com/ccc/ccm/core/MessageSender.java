package com.ccc.ccm.core;
/**
 * This is an interface for message sending.
 * @author Adam Gibson
 *
 */
public interface MessageSender {
	/**
	 * This will  send the given string as a message to an MQ server.
	 * @param message the message to send
	 * @return true if the message was sent, false otherwise
	 */
	public boolean sendMessage(String message);
}//end MessageSender
