package com.ccc.ccm.client;

import java.io.Serializable;

import javax.naming.Context;

import com.ccc.ccm.messages.store.MessageStore;
/**
 * This is a message client for the handling of
 * publishing and sending of messages.
 * @author Adam Gibson
 *
 */
public interface MessageClient extends Serializable {

	
	
	
	/**
	 * This will send a text message.
	 * @param text the message to send
	 * @return true if the message was sent,
	 * false otherwise
	 */
	public boolean sendMessage(String text);

	


	/**
	 * This will send a text message.
	 * @param text the message to send
	 * @param destination the destination to send to
	 * @return true if the message was sent,
	 * false otherwise
	 */
	public boolean sendMessage(String text,String destination);

	



	





	
	/**
	 * This exposes the context by which this message 
	 * client creates connections.
	 * @return the context for this client.
	 */
	public Context context();

	/**
	 * This will send the specified message to the specified destination.
	 * @param text the message to send
	 * @param destinationName the destination to send to
	 * @return true if the message was sent successfully, false otherwise
	 */
	public boolean sendMessageToDestination(String text,String destinationName);

	/**
	 * This returns the message store used by this client.
	 * @return the message store used by this client.
	 */
	public MessageStore messageStore();


	/**
	 * This will purge the given destination with the specified number of threads.
	 * @param numThreads the number of threads to use
	 * @param destination the destination to purge
	 */
	public void purge(int numThreads,String destination);

	/**
	 * This will purge the given destination with 10 threads.
	 * @param numThreads the number of threads to use
	 * @param destination the destination to purge
	 */
	public void purge(String destination);
	
	
	public final static String TOPIC="topic";
	
	public final static String QUEUE="queue";

	
}//end MessageClient
