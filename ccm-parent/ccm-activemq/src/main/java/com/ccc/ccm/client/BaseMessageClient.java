package com.ccc.ccm.client;

import java.util.Collection;
import java.util.Collections;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.naming.Context;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.MessageCreator;

import com.ccc.ccm.broker.BrokerStore;
import com.ccc.ccm.destinations.DestinationController;
import com.ccc.ccm.messages.store.MessageStore;
import com.ccc.jndi.context.api.ContextFetcher;
/**
 * This is a base definition for a message client.
 * @author Adam Gibson
 *
 */
public abstract class BaseMessageClient implements MessageClient  {

	@Override
	public boolean sendObject(final Object send, String destination) {
		jmsTemplate.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session session) throws JMSException {
				Message message=session.createMessage();
				if(!(send instanceof Collection))
					message.setObjectProperty("object", Collections.singletonList(send));
				else message.setObjectProperty("object",send);
				return message;
			}

		});
		return false;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3706096924625260429L;
	@Override
	public boolean sendMessage(String text) {
		try {
			Connection c=jmsTemplate.getConnectionFactory().createConnection();

			jmsTemplate.convertAndSend(text);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}





	@Override
	public boolean sendMessage(String text, String destination) {
		jmsTemplate.convertAndSend(destination, text);
		return true;
	}





	@Override
	public void purge(int numThreads, final String destination) {
		for(int m=0;m<numThreads;m++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					Message dump=null;

					while((dump=receive(destination))!=null) {

						dump=receive(destination);
						System.out.println("Dumping message");

					}
				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}		
	}//end purge





	@Override
	public void purge(final String destination) {
		for(int m=0;m<10;m++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					Message dump=null;

					while((dump=receive(destination))!=null) {

						dump=receive(destination);
						System.out.println("Dumping message");

					}
				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}				
	}//end purge





	@Override
	public Message receive() {
		return jmsTemplate.receive();
	}



	@Override
	public Message receive(String destinatonName) {
		Message ret= jmsTemplate.receive(destinatonName);
		try {
			if(ret!=null)
				ret.acknowledge();

		} catch (JMSException e) {
			e.printStackTrace();
		}
		return ret;
	}




	@Override
	public Connection createConnection(String userName, String password) {
		try {

			return	jmsTemplate.getConnectionFactory().createConnection(userName,password);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}//end createConnection

	@Override
	public Message recieveSelected(String selector) {
		return jmsTemplate.receiveSelected(selector);
	}



	@Override
	public Message receiveSelected(String destination, String selector) {
		return jmsTemplate.receiveSelected(destination, selector);
	}



	@Override
	public Message receiveSelected(Destination destination, String selector) {
		return jmsTemplate.receiveSelected(destination, selector);
	}



	@Override
	public Connection createConnection() {
		try {
			return	jmsTemplate.getConnectionFactory().createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
		}
		return null;
	}




	@Override
	public boolean sendMessageToDestination(String text, String destinationName) {
		try {
			jmsTemplate.convertAndSend(destinationName, text);
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public Destination defaultDesination() {
		return jmsTemplate.getDefaultDestination();
	}

	@Override
	public String defaultDestinationName() {
		return jmsTemplate.getDefaultDestinationName();
	}

	@Override
	public MessageStore messageStore() {
		return messageStore;
	}


	public JMSTemplateAutowired getJmsTemplate() {
		return jmsTemplate;
	}





	public void setJmsTemplate(JMSTemplateAutowired jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}





	public BrokerStore getBrokerStore() {
		return brokerStore;
	}





	public DestinationController getDestinationController() {
		return destinationController;
	}

	public void setDestinationController(DestinationController destinationController) {
		this.destinationController = destinationController;
	}

	public MessageStore getMessageStore() {
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}

	public ContextFetcher getContextFetcher() {
		return contextFetcher;
	}

	@Override
	public Context context() {
		return contextFetcher.fetch();
	}

	public void setContextFetcher(ContextFetcher contextFetcher) {
		this.contextFetcher = contextFetcher;
	}





	@Override
	public BrokerStore brokerStore() {
		return brokerStore;
	}



	public void setBrokerStore(BrokerStore brokerStore) {
		this.brokerStore = brokerStore;
	}

	@Autowired(required=false)
	protected JMSTemplateAutowired jmsTemplate;
	@Autowired(required=false)
	protected DestinationController destinationController;
	@Autowired(required=false)
	protected MessageStore messageStore;
	@Autowired(required=false)
	protected ContextFetcher contextFetcher;
	@Autowired(required=false)
	protected BrokerStore brokerStore;

}
