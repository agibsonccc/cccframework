package com.ccc.ccm.conversation.activemq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.Destination;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.ccm.client.MessageClient;
import com.ccc.ccm.conversation.Conversation;
import com.ccc.ccm.conversation.ConversationController;
import com.ccc.ccm.conversation.ConversationListener;
import com.ccc.ccm.conversation.ConversationMessageClient;
import com.ccc.ccm.persistance.StoreMessageManager;
import com.ccc.ccm.persistance.StoredMessage;
import com.ccc.util.generators.SessionIdentifierGenerator;
/**
 * This conversation controller keeps track of current messages
 * and conversations. It can handle multiple people in multiple
 * conversations and indexes them based on their logged in user name.
 * Each destination is self aware such that every conversation
 * will 
 * @author Adam Gibson
 *
 */
public class ActiveMQConversationController implements ConversationController  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 667798830419663382L;

	@Override
	public String generateConversationId() {
		return generator.nextSessionId();
	}

	@Override
	public Map<String,Conversation> currentConversations() {
		return currentConversations;
	}
	@Override
	public void initConversation(Conversation conversation) {
		currentConversations.put(conversation.getId(),conversation);
		destinationNamesToIds.put(conversation.getDestination(),conversation.getId());
		messageClient.addConversation(conversation);
		conversation.sortMessagesByMostRecent();


	}

	@Override
	public Conversation getByDestinationName(String name) {
		String id=destinationNamesToIds.get(name);

		if(id!=null)
			return currentConversations.get(id);
		return null;
	}


	@Override
	public void closeConversation(Conversation toClose) {
		String id=toClose.getId();
		currentConversations.remove(id);
		toClose.getMessages().clear();

	}

	public Map<String, Conversation> getCurrentConversations() {
		return currentConversations;
	}

	public void setCurrentConversations(
			Map<String, Conversation> currentConversations) {
		this.currentConversations = currentConversations;
	}

	public Map<String, Destination> getDestinations() {
		return destinations;
	}

	public void setDestinations(Map<String, Destination> destinations) {
		this.destinations = destinations;
	}


	public ConversationMessageClient getMessageClient() {
		return messageClient;
	}

	public void setMessageClient(ConversationMessageClient messageClient) {
		this.messageClient = messageClient;
	}

	@Override
	public void sendMessage(String sender, Conversation conversation,
			String message) {
	
		
		messageClient.sendMessageToDestination(message, conversation.getDestination());
		
		String destination=conversation.getDestination();
		if(fireEvents) {
			Collection<ConversationListener> eventListeners=listeners.get(destination);
			for(ConversationListener listener : eventListeners)
				listener.fireEvent(sender, message,destination);
		}

	}
	public void setGenerator(SessionIdentifierGenerator generator) {
		this.generator = generator;
	}




	public SessionIdentifierGenerator getGenerator() {
		return generator;
	}




	@Override
	public void registerListener(String name,ConversationListener listener) {
		log.info("Attempting to register listener with name: " + name);
		Collection<ConversationListener> convoListener=listeners.get(name);
		try {
		if(convoListener==null) {
			convoListener = new ArrayList<ConversationListener>();
			convoListener.add(listener);
			listeners.put(name,convoListener);
			log.info("Creating new listener for: " + name);
		}
		else {
			convoListener.add(listener);
			listeners.put(name,convoListener);

		}
		}catch(Exception e) {
			log.error("Error registering listener: " + name,e);
		}
	}

	@Override
	public void removeListener(String name) {
		listeners.remove(name);
	}




	@Override
	public boolean fireEvents() {
		return fireEvents;
	}

	@Override
	public void stopFiring() {
		fireEvents=false;
	}

	@Override
	public void startFiring() {
		fireEvents=true;
	}




	@Autowired
	private SessionIdentifierGenerator generator;
	private Map<String,Conversation> currentConversations = new HashMap<String,Conversation>();
	private Map<String,String> destinationNamesToIds = new HashMap<String,String>();
	private Map<String,Destination> destinations = new HashMap<String,Destination>();
	@Autowired(required=false)
	private ConversationMessageClient messageClient;
	private static Logger log=LoggerFactory.getLogger(ActiveMQConversationController.class);
	private Map<String,Collection<ConversationListener>> listeners = new HashMap<String,Collection<ConversationListener>>();

	private boolean fireEvents;
}
