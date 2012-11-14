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

public abstract class OptimizedMessageClient implements JMSMessageClient {

	/**
	 * 
	 */
	protected static final long serialVersionUID = 1L;





	@Override
	public boolean sendMessage(String text) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to send: " + text);
			}
			sendingJMSTemplate.convertAndSend(text);

		}
		catch(Exception e) {
			log.error("Error occured sending: " + text ,e);
			return false;
		}
		return true;
	}

	@Override
	public boolean sendMessage(String text, String destination) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to send: " + text);
			}
			sendingJMSTemplate.convertAndSend(destination,text);

		}
		catch(Exception e) {
			log.error("Error occured sending: " + text ,e);
			return false;
		}
		return true;
	}

	@Override
	public Connection createConnection() {
		try {
			return sendingJMSTemplate.getConnectionFactory().createConnection();
		} catch (JMSException e) {
			log.error("Error creating connection",e);
			try {
				return receivingJMSTemplate.getConnectionFactory().createConnection();
			} catch (JMSException e1) {
				e1.printStackTrace();
				log.error("Error creating connection after fallback to receving jms template",e);

			}
		}
		return null;
	}

	@Override
	public Connection createConnection(String userName, String password) {
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to create connection factory for user: " + userName);
			}
			return	sendingJMSTemplate.getConnectionFactory().createConnection(userName,password);
		} catch (JMSException e) {
			log.error("Error creating connection",e);

			try {
				return receivingJMSTemplate.getConnectionFactory().createConnection(userName, password);
			} catch (JMSException e1) {
				e1.printStackTrace();
				log.error("Error creating connection after fallback to receving jms template",e);

			}
		}
		return null;
	}

	@Override
	public Destination defaultDesination() {
		return sendingJMSTemplate.getDefaultDestination();
	}

	@Override
	public String defaultDestinationName() {
		return sendingJMSTemplate.getDefaultDestinationName();
	}

	@Override
	public Message receive() {
		if(log.isDebugEnabled()) {
			log.debug("Attempting to retrieve from default destination");
		}
		return receivingJMSTemplate.receive();
	}

	@Override
	public Message receive(String destinatonName) {
		if(log.isDebugEnabled()) {
			log.debug("Attempting to retrieve from default destination");
		}
		return receivingJMSTemplate.receive(destinatonName);
	}

	@Override
	public Message recieveSelected(String selector) {
		if(log.isDebugEnabled()) {
			log.debug("Attempting to retrieve from default destination");
		}
		return receivingJMSTemplate.receiveSelected(selector);
	}

	@Override
	public Message receiveSelected(String destination, String selector) {
		if(log.isDebugEnabled()) {
			log.debug("Attempting to retrieve from default destination");
		}
		return receivingJMSTemplate.receiveSelected(destination,selector);
	}

	@Override
	public Message receiveSelected(Destination destination, String selector) {
		if(log.isDebugEnabled()) {
			log.debug("Attempting to retrieve from default destination");
		}
		return receivingJMSTemplate.receiveSelected(destination,selector);
	}

	@Override
	public Context context() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendMessageToDestination(String text, String destinationName) {
		sendingJMSTemplate.convertAndSend(destinationName, text);
		return true;
	}

	@Override
	public MessageStore messageStore() {
		return messageStore;
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
	}

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
	}

	public MessageStore getMessageStore() {
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}

	public JMSTemplateAutowired getSendingJMSTemplate() {
		return sendingJMSTemplate;
	}

	public void setSendingJMSTemplate(JMSTemplateAutowired sendingJMSTemplate) {
		this.sendingJMSTemplate = sendingJMSTemplate;
	}

	public JMSTemplateAutowired getReceivingJMSTemplate() {
		return receivingJMSTemplate;
	}

	public void setReceivingJMSTemplate(JMSTemplateAutowired receivingJMSTemplate) {
		this.receivingJMSTemplate = receivingJMSTemplate;
	}

	public ContextFetcher<?> getContextFetcher() {
		return contextFetcher;
	}

	public void setContextFetcher(ContextFetcher<?> contextFetcher) {
		this.contextFetcher = contextFetcher;
	}

	public DestinationController getDestinationController() {
		return destinationController;
	}

	public void setDestinationController(DestinationController destinationController) {
		this.destinationController = destinationController;
	}
	protected static Logger log=LoggerFactory.getLogger(BaseJMSMessageClient.class);
	@Autowired(required=false)
	protected MessageStore messageStore;
	@Autowired
	protected ContextFetcher<?> contextFetcher;
	@Autowired(required=false)
	protected JMSTemplateAutowired sendingJMSTemplate;
	@Autowired
	protected JMSTemplateAutowired receivingJMSTemplate;
	@Autowired(required=false)
	protected DestinationController destinationController;
}
