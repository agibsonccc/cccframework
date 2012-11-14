package com.ccc.ccm.conversation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.ccc.ccm.persistance.StoredMessage;
import com.ccc.util.generators.SessionIdentifierGenerator;
import com.ccc.util.collections.Converter;
/**
 * This is  a utils class for conversations 
 * that will recreate messages as well as 
 * generate ids fro conversations
 * @author Adam Gibson
 *
 */
public class ConversationUtils {

	/**
	 * THis returns a conversation with the messages sorted by time
	 * @param messages the messages to create a conversation from
	 * @param destination the conversation with the destination
	 * @return a conversation with time sorted messages
	 */
	public static Conversation newSortedConversation(Collection<StoredMessage> messages,String destination) {
		String id=generateId();
		List<StoredMessage> listMessages= new Converter<StoredMessage>().toList(messages);
		
		List<StoredMessage> sorted=IMUtils.sortByTime(listMessages);
		Conversation ret = new Conversation(id);
		ret.setMessages(sorted);
		ret.setDestination(destination);
		return ret;
	}//end newSortedConversation
	
	
	
	
	/**
	 * This will generate a conversation id
	 * @return a random id for a conversation
	 */
	public static String generateId() {
		return generator.nextSessionId();
	}//end generateId
	
	
	/**
	 * This will attempt to reconstruct a conversation on the assumption
	 * the passed in messages all have the same conversation id.
	 * @param messages the messages to derive the conversation from
	 * @param destination the destination for this conversation
	 * @return a conversation constructed from the given messages, or null
	 * if the messages passed in are null or empty
	 */
	public static Conversation fromMessages(Collection<StoredMessage> messages,String destination) {
		if(messages==null || messages.isEmpty())
			return null;
		List<StoredMessage> messageList = new ArrayList<StoredMessage>();
		messageList.addAll(messages);

		StoredMessage firstMessage=messageList.get(0);

		String convoId=firstMessage.getConversationId();
		
		Conversation ret = new Conversation(convoId);
		ret.setMessages(messageList);
		
		return ret;
	}//end fromMessages
	/**
	 * This will construct a new conversation from the collection of messages.
	 * A new id is generated, but participants and all else will remain the same
	 * @param messages the messages to form in to a conversation
	 * @return a new conversation with the stored messages
	 */
	public static Conversation newfromMessages(Collection<StoredMessage> messages,String destination) {
		if(messages==null || messages.isEmpty())
			return null;

		String convoId=generator.nextSessionId();
		List<StoredMessage> messageList = new ArrayList<StoredMessage>();
		for(StoredMessage message : messageList)
			message.setConversationId(convoId);
		messageList.addAll(messages);
		Conversation ret = new Conversation(convoId);

		ret.setMessages(messageList);
		
		return ret;
	}//end newfromMessages
	/**
	 * This converts the array of messages to a new conversation
	 * @param messages the messages to convert
	 * @param destination the destination for this conversation
	 * @return a new conversation from the messages
	 */
	public static Conversation fromMessages(StoredMessage[] messages,String destination) {
		Conversation ret = new Conversation(generator.nextSessionId());
		ret.setMessages(Arrays.asList(messages));		
		return ret;
	}//end fromMessages

	private static SessionIdentifierGenerator generator = new SessionIdentifierGenerator();

}//end ConversationUtils
