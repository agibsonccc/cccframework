package com.ccc.ccm.client;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.ccm.destinations.DestinationController;
import com.ccc.ccm.messages.store.MessageStore;
import com.ccc.jndi.context.api.ContextFetcher;
/**
 * This is a base definition for a message client.
 * @author Adam Gibson
 *
 */
public abstract class BaseJMSMessageClient implements JMSMessageClient  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3706096924625260429L;
	@Override
	public boolean sendMessage(String text) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to send: " + text);
			}
			jmsTemplate.convertAndSend(text);

		}
		catch(Exception e) {
			log.error("Error occured sending: " + text ,e);
			return false;
		}
		return true;
	}//end sendMessage





	@Override
	public boolean sendMessage(String text, String destination) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to send: " + text);
			}
			jmsTemplate.convertAndSend(destination,text);

		}
		catch(Exception e) {
			log.error("Error occured sending: " + text ,e);
			return false;
		}
		return true;
	}





	@Override
	public void purge(int numThreads, final String destination) {
		for(int m=0;m<numThreads;m++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					@SuppressWarnings("unused")
					Message dump=null;

					while((dump=receive(destination))!=null) {

						dump=receive(destination);
						if(log.isTraceEnabled()) {
							log.debug("Purged message");
						}
					}
					if(log.isDebugEnabled()) {
						log.debug("Done dumping messages");
					}

				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				log.error("Thread interrupted in purge",e);
			}
		}		
	}//end purge





	@Override
	public void purge(final String destination) {
		for(int m=0;m<10;m++) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					@SuppressWarnings("unused")
					Message dump=null;

					while((dump=receive(destination))!=null) {

						dump=receive(destination);
						if(log.isTraceEnabled()) {
							log.debug("Purged message");
						}

					}
					if(log.isDebugEnabled()) {
						log.debug("Done dumping messages");
					}
				}
			});
			t.start();
			try {
				t.join();
			} catch (InterruptedException e) {
				log.error("Thread interrupted while doing a purge on: " + destination,e);
			}
		}				
	}//end purge





	@Override
	public Message receive() {
		if(log.isDebugEnabled()) {
			log.debug("Attempting to retrieve from default destination");
		}
		return jmsTemplate.receive();
	}



	@Override
	public Message receive(String destinationName) {
		Message ret= jmsTemplate.receive(destinationName);
		try {
			if(ret!=null)
				ret.acknowledge();
			if(log.isDebugEnabled()) {
				log.debug("Received and acknowledged message from: " + destinationName);
			}

		} catch (JMSException e) {
			log.error("Error receiving message from : " + destinationName,e);
			
		}
		return ret;
	}




	@Override
	public Connection createConnection(String userName, String password) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to create connection factory for user: " + userName);
			}
			return	jmsTemplate.getConnectionFactory().createConnection(userName,password);
		} catch (JMSException e) {
			log.error("Error creating connection",e);
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
			if(log.isDebugEnabled()) {
				log.debug("Attempting to create connection");
			}
			return	jmsTemplate.getConnectionFactory().createConnection();
		} catch (JMSException e) {
			log.error("Error createing jms connection");
		}
		return null;
	}




	@Override
	public boolean sendMessageToDestination(String text, String destinationName) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to send " + text + " to " + destinationName);
			}
			jmsTemplate.convertAndSend(destinationName, text);
		}catch(Exception e) {
			log.error("Error sending " + text + " to " + destinationName,e);
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

	public ContextFetcher<?> getContextFetcher() {
		return contextFetcher;
	}

	@Override
	public Context context() {
		return contextFetcher.fetch();
	}

	public void setContextFetcher(ContextFetcher<?> contextFetcher) {
		this.contextFetcher = contextFetcher;
	}







	@Autowired(required=false)
	protected JMSTemplateAutowired jmsTemplate;
	@Autowired(required=false)
	protected DestinationController destinationController;
	@Autowired(required=false)
	protected MessageStore messageStore;
	@Autowired(required=false)
	protected ContextFetcher<?> contextFetcher;
	
	private static Logger log=LoggerFactory.getLogger(BaseJMSMessageClient.class);

}//end BaseJMSMessageClient
