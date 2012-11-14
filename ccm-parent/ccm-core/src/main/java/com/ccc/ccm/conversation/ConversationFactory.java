package com.ccc.ccm.conversation;

import java.util.List;
/**
 * This handles the creation of conversations.
 * @author Adam Gibson
 *
 */
public interface ConversationFactory {

	/**
	 * This will create a conversation for the given 
	 * list of participants
	 * @param participants the participants in the conversation
	 * @return a conversation with the given participants
	 */
	public Conversation create(List<String> participants);
}//end ConversationFactory
