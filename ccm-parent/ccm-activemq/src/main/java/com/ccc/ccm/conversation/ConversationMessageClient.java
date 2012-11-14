package com.ccc.ccm.conversation;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.ccm.client.activemq.ActiveMQMessageClient;
import com.ccc.ccm.conversation.activemq.ConversationMessagePostProcessor;
import com.ccc.ccm.persistance.StoreMessageManager;
import com.ccc.ccm.persistance.StoredMessage;
/**
 * This is a message client that will take conversations in to account
 * with a conversation post processor to handle messages in the conversation.
 * 
 * @author Adam Gibson
 *
 */
public class ConversationMessageClient extends ActiveMQMessageClient   {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4822161482402534282L;

	@Override
	public boolean sendMessage(String text, String destination) {
		try {	
			jmsTemplate.convertAndSend(destination, text,new ConversationMessagePostProcessor(conversations.get(destination)));
			if(log.isDebugEnabled())
				log.debug("Sent messae to : " + destination);
			return true;
		}

		catch(Exception e) {
			e.printStackTrace();
			log.error(e);
			return false;
		}
	}//end sendMessage
	/**
	 * This will receive a message from the given sender
	 * @param destinatonName the name of the destination to receive on
	 * @param sender the sender that sent the message
	 * @return a jms message that was received
	 */
	public Message receive(String destinatonName,String sender) {
		try {	
			Message ret=jmsTemplate.receive(destinatonName);
			if(ret==null)
				return null;
			Conversation conversation=conversationByDestination.get(destinatonName);

			if(conversation==null) {
				String id=ConversationUtils.generateId();
				conversation = new Conversation(id);
				conversation.setDestination(destinatonName);
				conversations.put(id,conversation);
				conversationByDestination.put(destinatonName,conversation);
				if(log.isDebugEnabled())
					log.debug("Created new conversation for destination: " + destinatonName);
			}

			StoredMessage message = new StoredMessage();
			String dest=conversation.getDestination();
			if(dest.equals(destinatonName)) {
			if(ret instanceof TextMessage) {
				String text=((TextMessage) ret).getText();
				message.setBody(text);

				}
				message.setConversationId(conversation.getId());
				message.setSentTime(new Timestamp(System.currentTimeMillis()));
				message.setSender(sender);
				conversation.addMessage(message);
				if(storedMessageManager!=null)
					storedMessageManager.saveE(message);
				return ret;
			}


			return null;
		}

		catch(Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}//end receive
	/**
	 * This wraps a jms receive in a
	 * conversation message which contains the original received message
	 * as well as the stored message 
	 * @param destinationName the name of the destination to receive on
	 * @param sender the sender of the message
	 * @return a wrapped conversation message containing the original jms message
	 * as well as the stored message that is saved to the database.
	 */
	public ConversationMessage receiveConversationMessage(String destinationName,String sender) {

		try {	

			Message ret=jmsTemplate.receive(destinationName);
			if(ret==null)
				return null;
			Set<String> keys=conversations.keySet();
			for(String s : keys) {
				Conversation conversation=conversations.get(s);
				StoredMessage message = new StoredMessage();
				String dest=conversation.getDestination();
				if(dest.equals(destinationName)) {
					if(ret instanceof TextMessage) {
						String text=((TextMessage) ret).getText();
						message.setBody(text);

					}
					message.setConversationId(conversation.getId());
					message.setSentTime(new Timestamp(System.currentTimeMillis()));
					message.setSender(sender);
					conversation.addMessage(message);
					if(storedMessageManager!=null)
						storedMessageManager.saveE(message);
					ConversationMessage retMessage = new ConversationMessage(ret,message);
					return retMessage;
				}

			}
			return null;
		}

		catch(Exception e) {
			e.printStackTrace();
			log.error(e);
			return null;
		}
	}//end receiveConversationMessage

	/**
	 * This will add a conversation to this client
	 * @param conversation  the conversation to add
	 */
	public void addConversation(Conversation conversation) {
		conversations.put(conversation.getId(),conversation);
		conversationByDestination.put(conversation.getDestination(), conversation);
		if(log.isDebugEnabled())
			log.debug("Added conversation: " + conversation.getDestination());
	}

	
	public void removeConversation(Conversation conversation) {
		conversations.remove(conversation.getId());
		conversationByDestination.remove(conversation.getDestination());
		if(log.isDebugEnabled())
			log.debug("Removed conversation: " + conversation.getDestination());
	}
	
	public Conversation getConversation(String id) {
		return conversations.get(id);
	}

	/**
	 * This will return a conversation by the destination
	 * @param destination the destination to search by
	 * 
	 * @return a conversation with the given destination
	 */
	public Conversation conversationByDestination(String destination) {
		return conversationByDestination.get(destination);
	}
	public Map<String,Conversation> getConversations() {
		return conversations;
	}



	public StoreMessageManager getStoredMessageManager() {
		return storedMessageManager;
	}
	public void setStoredMessageManager(StoreMessageManager storedMessageManager) {
		this.storedMessageManager = storedMessageManager;
	}


	
	private static Logger log=Logger.getLogger(ConversationMessageClient.class);
	
	@Autowired(required=false)
	private StoreMessageManager storedMessageManager;

	private Map<String,Conversation> conversations = new HashMap<String,Conversation>();

	private Map<String,Conversation> conversationByDestination = new HashMap<String,Conversation>();
}
