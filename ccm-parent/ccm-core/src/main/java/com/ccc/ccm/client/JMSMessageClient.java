package com.ccc.ccm.client;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.Topic;
/**
 * This is a message client adherent to the jms standard.
 * @author Adam Gibson
 *
 */
public interface JMSMessageClient extends MessageClient {
	/**
	 * This will return an enumeration of messages for the given queue
	 * @param queueName the name of the queue to get messages for
	 * @return the enumeration of messages for a given queue
	 * @throws JMSException 
	 */
	public Enumeration<?> getMessagesForQueue(String queueName) throws JMSException;
	
	
	/**
	 * This will return a message based on a selector from the default
	 * destination.
	 * @param selector the selector to use
	 * @return a message from the default destination matching
	 * the passed selector
	 */
	public Message recieveSelected(String selector);


	/**
	 * This will create and return a jms session.
	 * @param transacted indicates whether the session is transacted
	 * @param acknowledgeable t indicates whether the consumer or the client will 
	 * acknowledge any messages 
	 * it receives; ignored if the session is transacted. Legal values are Session.AUTO_ACKNOWLEDGE, 
	 * Session.CLIENT_ACKNOWLEDGE, and Session.DUPS_OK_ACKNOWLEDGE.
	 * @return
	 */
	public Session createSession(boolean transacted,int acknowledgable);
	
	

	/**
	 * This will create and return a queue browser for the given queue
	 * @param queue the queue to create a browser for
	 * @return a queue browser for this queue
	 */
	public QueueBrowser queueBrowser(Queue queue);
	
	
	
	/**
	 * This will create a connection and return it.
	 * @return a new connection
	 */
	public Connection createConnection();

	/**
	 * This will create a connection with the specified user name
	 * and password.
	 * @param userName the user name to connect with
	 * @param password the password to use
	 * @return a new connection, or null on fail
	 */
	public Connection createConnection(String userName,String password);
	
	
	
	/**
	 * This returns the default destination for this client.
	 * @return the default destination for this client.
	 */
	public Destination defaultDesination();

	/**
	 * This returns the default destination's name
	 * @return the default destination's name
	 */
	public String defaultDestinationName();

	/**
	 * This will receive and return a message at a default
	 * destination.
	 * @return a message received at a default destination
	 */
	public Message receive();

	/**
	 * This will return a message received from the 
	 * specified destination
	 * @param destinatonName the name of the destination to
	 * get a message from
	 * @return a message received from the given destination
	 */
	public Message receive(String destinatonName);
	
	
	/**
	 * This will receive a message from the given destination
	 * based on the given selector
	 * @param destination the name of the destination to receive from
	 * @param selector the selector to use
	 * @return a message received from the given destination
	 * based on the given selector
	 */
	public Message receiveSelected(String destination,String selector);

	/**
	 * This will receive a message from the given destination
	 * based on the given selector
	 * @param destination the destination to receive from
	 * @param selector the selector to use
	 * @return a message received from the given destination
	 * based on the given selector
	 */
	public Message receiveSelected(Destination destination,String selector);
	
	
	/**
	 * This will create a queue and return it.
	 * @param name the name of the queue
	 * to create
	 * @return the new queue
	 */
	public Queue createQueue(String name);

	/**
	 * This will create a new topic with the given name and
	 * return it.
	 * @param name the name of the topic
	 * @return the newly created topic
	 */
	public Topic createTopic(String name);
	
	

	/**
	 * This will attempt to delete the given destination 
	 * @param destination the destination to delete
	 * @param type the type of destination to delete
	 * This can be either MessageClient.TOPIC or MessageClient.QUEUE
	 * @return true if the destination was deleted,
	 * false otherwise
	 * @throws Exception 
	 */
	public boolean deleteDestination(String destination,String type) throws Exception;

}
