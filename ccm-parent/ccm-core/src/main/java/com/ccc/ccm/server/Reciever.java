package com.ccc.ccm.server;

import java.util.Properties;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;

/*
 * @author Michael Godfrey
 * @version 23May2011
 */
public class Reciever implements MessageListener{
	
	/*
	 * Constructor
	 */
	public Reciever(int cap, Session s, Destination d) throws JMSException
	{
		super();
		setSession(s);
		setBufferCapacity(cap);
		buf = new StringBuffer(bufferCapacity);
		setMessageConsumer(session.createConsumer(d));
		messageConsumer.setMessageListener(this);
		
	}//end constructor
	
	public Reciever(String factoryClass, String url, String queueName, int sesType, Boolean trans)
	{	
		init(factoryClass,url,queueName,sesType,trans);
	}
	
	public void init(String factoryClass, String url, String queueName, int sesType, Boolean trans)
	{
		setContext(context(factoryClass,url));
		setConnectionFactory(factory());
		try
		{
		setConnection(connectionFactory.createConnection());
		setSession(connection.createSession(trans, sesType)); 
		setQueue((Queue)context.lookup(queueName));
		setMessageConsumer(session.createConsumer(queue));
		}
		catch(JMSException e)
		{
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	private ConnectionFactory factory() 
	{
		 try {
			return (ConnectionFactory) context.lookup(connectionFactoryName);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private Context context(String factoryClass, String url){
		Properties p = new Properties();
		p.put(Context.INITIAL_CONTEXT_FACTORY, factoryClass);
		p.put(Context.PROVIDER_URL, url);
		
		try {
			return (Context) new InitialContext(p);
		} catch (NamingException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public StringBuffer getStringBuffer()
	{
		return buf;
	}//end getStringBuffer

	public void onMessage(Message message)
	{try 
	{
		TextMessage txt = (TextMessage) message;
		String msg;
		msg = txt.getText();
		messageReceived();
		buf.append(msg);
		
	} 
	catch (JMSException e) {e.printStackTrace();}
	}//end onMessage
	
	public void messageReceived() {
		
	}
	
	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public MessageConsumer getMessageConsumer() {
		return messageConsumer;
	}

	public void setMessageConsumer(MessageConsumer messageConsumer) {
		this.messageConsumer = messageConsumer;
	}

	public Queue getQueue() {
		return queue;
	}

	public void setQueue(Queue queue) {
		this.queue = queue;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}

	public int getBufferCapacity() {
		return bufferCapacity;
	}

	public void setBufferCapacity(int bufferCapacity) {
		this.bufferCapacity = bufferCapacity;
	}



	@Autowired
	private Context context;
	@Autowired
	private ConnectionFactory connectionFactory;
	@Autowired
	private Connection connection;
	@Autowired
	private Session session;
	@Autowired
	private MessageConsumer messageConsumer;
	@Autowired
	private Queue queue;
	
	private String queueName;
	private String connectionFactoryName;
	
	private int bufferCapacity;
	private static StringBuffer buf;

}//end class
