package com.ccc.ccm.rabbitmq.client;

import javax.naming.Context;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;

import com.ccc.ccm.messages.store.MessageStore;
/**
 * An AQMP Message client. This handles the aqmp messaging protocol
 * @author Adam Gibson
 *
 */
public interface AQMPMessageClient {

	/**
	 * Send a message to a destination, get back a response.
	 * @param destination the destination to send to 
	 * @param exchange the exchange to publish to
	 * @param message the message to send
	 * @return the response, if any
	 */
	public Object requestReplyExchange(String routingKey,String exchange,Object message);
	/**
	 * Send a message to a destination, get back a response.
	 * @param routingKey the routing key to send to
	 * @param message the message to send
	 * @return the response, if any
	 */
	public Object requestReply(String destination,Object message);
	/**
	 * Sends to an exchange
	 * @param exchange the exchange to publish to
	 * @param routingKey the routing key to use
	 * @param message the message to send
	 * @return true if the message was sent, false otherwise
	 */
	public boolean sendToExchange(String exchange,String routingKey,Object message);
	
	
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



	/**
	 * Declare a binding with the given name, the destination name
	 * @param destinationName the destination name to be bound
	 * @param exName the name of the exchange to be bound to
	 * @param type the type of exchange to be created
	 */
	public void declareBinding(String destinationName,String exName,int exchangeType);
	/**
	 * Declare a  queue
	 * @param name the name of the queue
	 * 
	 */
	public void declareQueue(String name);

	/**
	 * Declare an exchange of the given type
	 * @param exchange  the exchange name 
	 * @param type the type of exchange
	 */
	public void declareExchange(String exchange,int type);

	/**
	 * Creates a queue and binds it to the given exchange
	 * on the default vhost
	 * @param queue the queue to bind to
	 * @param exchange the exchange to bind to
	 * @param exchangeType the type of exchange to create
	 */

	public void createQueueAndBindToExchange(String queue,String exchange,int exchangeType);

	

	/**
	 * Creates a queue and binds it to the given exchange
	 * @param queue the queue to bind to
	 * @param exchange the exchange to bind to
	 * @param vHost the vhost to create on
	 * @param exchangeType the type of exchange to create
	 */

	public void createQueueAndBindToExchange(String queue, String exchange,
			int exchangeType, String vHost);
	/**
	 * This will send the given message to the default destination
	 * @param message the message to send
	 * @throws AmqpException if one occurs
	 */
	public void send(Message message) throws AmqpException;

	/**
	 * This will send a message with the given routing key
	 * @param routingKey the routing key to use
	 * @param message the message to send
	 * @throws AmqpException if one occurs
	 */
	public void send(String routingKey, Message message) throws AmqpException;

	/**
	 * This will send to the given exchange with the given routing key
	 * sending the given message
	 * @param exchange the name of the exchange to send to
	 * @param routingKey the routing key to use
	 * @param message the message to send
	 * @throws AmqpException if one occurs
	 */
	public void send(String exchange, String routingKey, Message message) throws AmqpException;
	/**
	 * This will return a binding associating the given queue
	 * with the given topic using the given routing key
	 * @param queue the queue to bind to
	 * @param topicExchange the exchange to bind
	 * @param routingKey the routing key to use
	 * @return a binding with the passed in associations
	 */
	public Binding bind(Queue queue,Exchange topicExchange,String routingKey);
	/**
	 * This will create a durable exchange with the given name
	 * @param name the name of the exchange to create
	 * @param autoDelete whether this exchange should get auto deleted or not
	 * @param type the type of exchange to create(Direct,Fanout,Topic,Custom,Headers)
	 * @return a durable exchange with the given settings
	 */
	public Exchange createDurableExchange(String name,boolean autoDelete,int type);
	
	/**
	 * This will create an exchange with the given name
	 * @param name the name of the exchange
	 * @param durable whether this exchange is durable
	 * @param autoDelete whether the exchange should be auto deleted
	 * @param type the type of exchange to create(Direct,Fanout,Topic,Custom,Headers)
	 * @return an exchange with the given parameters
	 */
	public Exchange createExchange(String name,boolean durable,boolean autoDelete,int type);

	/**
	 * This will create an exchange with the given name, it won't be durable
	 * and it will be auto deleted.
	 * @param name the name of the exchange
	 * @return an exchange that isn't durable and will automatically be deleted
	 */
	public Exchange createExchange(String name);


	/**
	 * This will create a topic exchange with the given name
	 * @param name the name of the exchange to create
	 * @return a new topic exchange with the given name
	 */
	public TopicExchange createTopicExchange(String name);
	/**
	 * This will receive a message from a default destination
	 * @return a message from a default destination
	 */
	public Message receive();
	/**
	 * This will receive a message from a given destination name
	 * @param queueName the name of the destination to receive from
	 * @return a message that was received or null, if none
	 */
	public Message receive(String queueName);
	/**
	 * This will create and return an anonymous queue
	 * @return an anonymous queue
	 */
	public AnonymousQueue anonymousQueue();
	/**
	 * This will create an aqmp queue
	 * @param name the name of the queue to create
	 * @return a queue with the given name
	 */
	public Queue createQueue(String name);
	/**
	 * An AQMP connection factory
	 * @param host the host to connect to
	 * @param port the port to connect on
	 * @return an aqmp connection factory with 
	 */
	public ConnectionFactory connectionFactory(String host, int port);

	/**
	 * An AQMP connection factory
	 * @param host the host to connect to
	 * @param port the port to connect on
	 * @param vHost the vhost for this connection factory
	 * @param userName the name of the user to connect with
	 * @param password the password to connect with
	 * @return an aqmp connection factory with 
	 */
	public ConnectionFactory connectionFactory(String host, String vHost, int port,String userName,String password);
	/**
	 * An AQMP connection factory
	 * @param host the host to connect to
	 * @param port the port to connect on
	 * @param vHost the vhost for the connection factory
	 * @return an aqmp connection factory with 
	 */
	public ConnectionFactory connectionFactory(String host, int port,String vHost);

	/**
	 * An AQMP connection factory
	 * @param host the host to connect to
	 * @param port the port to connect on
	 * @param userName the name of the user to connect with
	 * @param password the password to connect with
	 * @return an aqmp connection factory with 
	 */
	public ConnectionFactory connectionFactory(String host, int port,String userName,String password);

	/**
	 * This will return  an aqmp admin with the given host and port
	 * @param host the host to connect to
	 * @param port the port to connect to
	 * 
	 * @return the aqmp admin for the given host and port
	 */
	public AmqpAdmin aqmpAdmin(String host,int port,String vHost);

	/**
	 * This will return  an aqmp admin with the given host and port
	 * @param host the host to connect to
	 * @param port the port to connect to
	 * @param userName the name of the user to connect with
	 * @param password the password to connect with
	 * @return the aqmp admin for the given host and port
	 */
	public AmqpAdmin aqmpAdmin(String host,int port,String userName,String password);
	/**
	 * This will return  an aqmp admin with the given host and port
	 * @param host the host to connect to
	 * @param port the port to connect to
	 * @param vhost the vhost to connect to 
	 * @param userName the name of the user to connect with
	 * @param password the password to connect with
	 * @return the aqmp admin for the given host and port
	 */
	public AmqpAdmin aqmpAdmin(String host, int port, String vHost, String userName,
			String password);
	
	/**
	 * This will receive a message based on the given correlation id
	 * @param selector the selector to receive based on
	 * @return the message fulfilling that criteria, null otherwise
	 */
	public Message recieveSelected(String selector);
	/**
	 * Creates an amqp admin for the default virtual host
	 * and the given host and port
	 * @param host the host for the admin
	 * @param port the port for the admin
	 * @return the admin for the default virtual host
	 * and the given host and port
	 */
	public AmqpAdmin aqmpAdmin(String host, int port);
	
	public final static int DIRECT=0;
	public final static int FANOUT=1;
	public final static int TOPIC=2;
	public final static int HEADERS=3;
	public final static int QUEUE=4;
	public final static int EXCHANGE=5;
	public final static int BINDING=6;
	

}//end AQMPMessageClient
