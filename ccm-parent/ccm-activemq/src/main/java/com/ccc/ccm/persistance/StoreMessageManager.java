package com.ccc.ccm.persistance;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Repository;
import org.apache.commons.lang3.time.DateUtils;

import com.ccc.util.collections.Converter;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("storedMessageManager")
public class StoreMessageManager extends GenericManager<StoredMessage>{

	/**
	 * This will return all of the messages for a given sender.
	 * @param sender the sender of the messages
	 * @return the list of messages for this sender
	 */
	public List<StoredMessage> messagesFromSender(String sender) {
		return elementsWithValue("sender",sender);
	}

	/**
	 * This will return all of the messages sent by a specific person
	 * at a specific time
	 * @param sender
	 * @param timestamp
	 * @return
	 */
	public List<StoredMessage> messagesForSenderForDay(String sender,Timestamp timestamp) {
		List<StoredMessage> messagesForDay=messagesForDay(timestamp);

		List<StoredMessage> ret = new ArrayList<StoredMessage>();

		for(StoredMessage message : messagesForDay)
			if(message.getSender().equals(sender))
				ret.add(message);

		return ret;
	}//end messagesForSenderForDay

	/**
	 * This will return a list of messages for a given conversation id
	 * @param id the id of the conversation to get messages
	 * @return the list of messages for a given conversation, or null
	 * if the given id doesn't exist
	 */
	public List<StoredMessage> messagesForConversation(String id) {
		return elementsWithValue("conversation_id",id);
	}//end messagesForConversation
	/**
	 * This will return a list of messages based on a conversation id and the 
	 * array of user names passed in
	 * @param conversationId the conversation id to check for
	 * @param userNames the user names to filter
	 * @return the list of messages for a given conversation between any number of users
	 */
	public List<StoredMessage> conversationForUsers(String conversationId,String[] userNames) {
		List<StoredMessage> messages=messagesForConversation(conversationId);
		List<StoredMessage> ret = new ArrayList<StoredMessage>();
		
		for(StoredMessage message : messages) {
			for(String s : userNames) {
				if(message.getSender().equals(s))
					ret.add(message);
			}
		}
		return ret;
	}//end conversationForUsers
	/**
	 * This will return a list of messages for a number of users
	 * based on the day
	 * @param timestamp the day to use
	 * @param userNames the names of the users to sort by
	 * @return the list of messages for a number of users
	 */
	public List<StoredMessage> messagesForUserAndDay(Timestamp timestamp,String[] userNames) {
		Set<StoredMessage> sort = new HashSet<StoredMessage>();
		for(String s : userNames) {
			List<StoredMessage> messagesForDayAndUser=messagesForSenderForDay(s,timestamp);
			sort.addAll(messagesForDayAndUser);
		}
		return new Converter<StoredMessage>().setToList(sort);
	}//end messagesForUserAndDay
	
	
	/**
	 * This will return all of the messages for a given day
	 * @param timestamp the timestamp to get
	 * @return the list of messages for a given day
	 */
	public List<StoredMessage> messagesForDay(Timestamp timestamp) {
		List<StoredMessage> allMessages=allElements();
		List<StoredMessage> ret = new ArrayList<StoredMessage>();
		for(StoredMessage message : allMessages) {
			Timestamp messageTimeStamp=message.getSentTime();
			if(DateUtils.isSameDay(timestamp, messageTimeStamp))
				ret.add(message);
		}

		return ret;
	}//end messagesForDay

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StoreMessageManager() {
		super(StoredMessage.class);
	}
}
