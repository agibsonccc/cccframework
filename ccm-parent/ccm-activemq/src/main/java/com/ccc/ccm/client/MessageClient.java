package com.ccc.ccm.client;

import java.io.Serializable;
import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.jms.QueueBrowser;
import com.ccc.ccm.broker.BrokerStore;
import com.ccc.ccm.messages.store.MessageStore;
/**
 * This is a message client for the handling of
 * publishing and sending of messages.
 * @author Adam Gibson
 *
 */
public interface MessageClient extends Serializable {
	/**
	 * Send the given object to the given destination.
	 * Obtain the object with a message header of 
	 * object in the received jms message.
	 * @param send the object to send
	 * @param destination the destination to send to
	 * @return true if the object was sent, false otherwise
	 */
	public boolean sendObject(Object send,String destination);
	/**
	 * This will return an enumeration of messages for the given queue
	 * @param queueName the name of the queue to get messages for
	 * @return the enumeration of messages for a given queue
	 * @throws JMSException 
	 */
	public Enumeration getMessagesForQueue(String queueName) throws JMSException;
	
	/**
	 * This will send a text message.
	 * @param text the message to send
	 * @return true if the message was sent,
	 * false otherwise
	 */
	public boolean sendMessage(String text);

	
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
	 * This will send a text message.
	 * @param text the message to send
	 * @param destination the destination to send to
	 * @return true if the message was sent,
	 * false otherwise
	 */
	public boolean sendMessage(String text,String destination);

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
	 * This will return a message based on a selector from the default
	 * destination.
	 * @param selector the selector to use
	 * @return a message from the default destination matching
	 * the passed selector
	 */
	public Message recieveSelected(String selector);

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
	 * This will return the broker store used by this client
	 * @return the broker store used by this client
	 */
	public BrokerStore brokerStore();

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
