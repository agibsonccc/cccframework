package com.ccc.ccm.conversation;

import javax.jms.JMSException;
import javax.jms.Message;

import org.springframework.jms.core.MessagePostProcessor;

/**
 * This handles the adding of messages to a conversation.
 * @author Adam Gibson
 *
 */
public class ConversationMessagePostProcessor implements MessagePostProcessor {
	public ConversationMessagePostProcessor(Conversation conversation) {
		this.conversation=conversation;
	}
	@Override
	public Message postProcessMessage(Message message) throws JMSException {

		return message;
	}

	private Conversation conversation;
}
