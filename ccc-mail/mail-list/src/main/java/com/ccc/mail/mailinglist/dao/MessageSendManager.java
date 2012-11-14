package com.ccc.mail.mailinglist.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.MessageSend;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("messageSendManager")
public class MessageSendManager extends GenericManager<MessageSend> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public MessageSendManager() {
		super(MessageSend.class);
	}
	/**
	 * This will track the number of times a sender receiver pair occurs
	 * @param fromAddress the sender
	 * @param toAddress the receiver
	 * @return  the message send for the unique sender/receiver pair
	 */
	public MessageSend trackerForFromAndTo(String fromAddress,String toAddress) {
		List<MessageSend> messages= elementsWithValue("sent_from",fromAddress);
		if(messages==null || messages.isEmpty()) return null;

		for(MessageSend m : messages) {
			if(m.getEmailTo().equals(toAddress))
				return m;
		}
		return null;
	}
	
	
	public List<MessageSend> trackerForTo(String toAddress) {
		return elementsWithValue("sent_to",toAddress);
	}
	
}
