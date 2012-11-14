package com.ccc.ccm.conversation;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import com.ccc.ccm.persistance.StoredMessage;

/**
 * This contains utils for manipulating messages based on 
 * stored messages as conversations.
 * @author Adam Gibson
 *
 */
public class IMUtils {
	/**
	 * This will sort messages by a time sent
	 * @param messages the messages to be sorted
	 * @return a sorted list by time
	 */
	public static List<StoredMessage> sortByTime(List<StoredMessage> messages) {
		Collections.sort(messages, new TimestampComparator());
		return messages;
	}//end sortByTime
	
	/**
	 * Based on the comparison list and the other list of messages to compare,
	 * this will find all messages that occur after the last message in current
	 * and return a sorted queue of all the messages sorted by time
	 * @param current the current messages 
	 * @param other the messages to compare
	 * @return a queue of messages to append to current relative to conversation
	 */
	public static Queue<StoredMessage> messagesToAppend(List<StoredMessage> current,List<StoredMessage> other) {
		Queue<StoredMessage> ret = new ArrayDeque<StoredMessage>();
		Collections.sort(current,new TimestampComparator());
		StoredMessage lastMessage=current.get(current.size()-1);
		List<StoredMessage> toEnqueue = new ArrayList<StoredMessage>();
		for(StoredMessage message : other) {
			boolean after=message.getSentTime().after(lastMessage.getSentTime());
			if(after) toEnqueue.add(message);
		}
		if(!toEnqueue.isEmpty()) {
			Collections.sort(toEnqueue, new TimestampComparator());
			ret.addAll(toEnqueue);

		}
		return ret;
	}//end messagesToAppend

	/**
	 * This will return a stored message as an im formatted string
	 * in the form of: sender[time]: body
	 * @param message the message to return
	 * @return the formatted message as a string
	 */
	public static String messageToString(StoredMessage message) {
		StringBuffer sb = new StringBuffer();
		sb.append(message.getSender());
		sb.append("[");
		sb.append(message.getSentTime().toString());
		sb.append("]");
		sb.append(":");
		sb.append(message.getBody());
		return sb.toString();
	}//end messageToString

	/**
	 * This will sort the given messages according to time
	 * and then do a check on toPut to ensure that it is consistent
	 * to be added to the end of the list and returns it
	 * @param current the current list of messages
	 * @param toPut the messages to put
	 * @return the list of sorted messages
	 */
	public static List<StoredMessage> putMessage(List<StoredMessage> current,StoredMessage toPut) {
		//current.add(toPut);	
		if(!current.isEmpty()) {
			Collections.sort(current, new TimestampComparator());

			StoredMessage lastMessage=current.get(current.size()-1);
			if(toPut.getSentTime().after(lastMessage.getSentTime()))
				current.add(toPut);
		}
		return current;
	}//end putMessage


}

