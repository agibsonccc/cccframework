package com.ccc.mail.mailinglist.services.api;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.ccc.mail.mailinglist.model.BounceRate;
import com.ccc.mail.mailinglist.model.ListMessageTracking;
import com.ccc.mail.mailinglist.model.ListSubscribe;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.model.MessageSend;
import com.ccc.mail.mailinglist.model.Subscriber;
import com.ccc.mail.mailinglist.model.UniqueMessage;
import com.ccc.mail.mailinglist.model.UserNameLists;
/**
 * Service layer for mailing lists including basic analytics like
 * click rate and bounce rate
 * @author Adam Gibson
 *
 */
public interface MailingListService  extends Serializable {

	/**
	 * Deletes a tracking entry
	 * @param delete the entry to delete
	 */
	public void deleteTracking(ListMessageTracking delete);
	
	/**
	 * Inserts a list message tracking in to the database
	 * @param listMessageTracking the tracker to insert
	 */
	public void addTracking(ListMessageTracking listMessageTracking);
	/**
	 * Returns a list of (active) subscribers for a given mailing list
	 * @param list the list to get subscribers for
	 * @return the list of active subscribers for this mailing list
	 */
	public List<Subscriber> getSubscribed(MailingList list);
	/**
	 * Returns the number of subscribers for a mailing list
	 * @param list the mailing list to get the count of subscribers for
	 * @return the count of the number of subscribers for a given list.
	 */
	public int getNumSubscribed(MailingList list);
	/**
	 * This will return the number of days a subscriber is subscribed to a mailing list
	 * @param subscriber the subscriber to check
	 * @return the number of days a subscriber is subscribed to a list
	 */
	public int numDaysSubscribed(Subscriber subscriber);
	
	/**
	 * Returns the messages in a given range
	 * for a given mailing list sent to 
	 * a given email
	 * @param listId the id of the list
	 * @param email the email sent to
	 * @param from the from date
	 * @param to the to date
	 * @return the messages for a specified range or null
	 * if none found
	 */
	public List<UniqueMessage> messagesForRange(String listId,String from,String to,String email);
	
	/**
	 * Returns the messages in a given range
	 * for a given mailing list sent to
	 * a given email address
	 * @param list the mailing list to get messages for
	 * @param email the email of the address sent to
	 * @param from the from date
	 * @param to the to date
	 * @return the list of messages sent for a given 
	 */
	public List<UniqueMessage> messagesForRange(MailingList list,Date from,Date to,String email);

	/**
	 * Returns the messages in a given range
	 * for a given mailing list sent to
	 * a given email address
	 * @param list the mailing list to get messages for
	 * @param email the email of the address sent to
	 * @param from the from date
	 * @param to the to date
	 * @return the list of messages sent for a given 
	 */
	public List<UniqueMessage> messagesForRange(MailingList list,String from,String to,String email);

	/**
	 * Returns the messages in a given range
	 * for a given mailing list
	 * @param list
	 * @param from the from date
	 * @param to the to date
	 * @return the messages for a given range for a mailing list or null
	 */
	public List<UniqueMessage> messagesForRange(MailingList list,String from,String to);
	/**
	 * Returns the messages in a given range
	 * for a given mailing list

	 * @return
	 */
	public List<UniqueMessage> messagesForRange(String listId,String from,String to);
	
	/**
	 * Returns the messages in a given range
	 * for a given mailing list
		@param from the from date sent to
		@param to the to date sent to
	 * @return the list of messages for a mailing list
	 *  sent from a given date to a given date
	 */
	public List<UniqueMessage> messagesForRange(String listId,Date from,Date to);
	/**
	 * Returns list with given email address
	 * @param email the email address of the list
	 * @return the mailing list associated with the given email
	 * 
	 */
	public MailingList listWithEmail(String email);
	/**
	 * This will return the number of openRate for an email address
	 * @param email - Email sent to
	 * @return number of openRate for an email address
	 */
	public int openRate(String email);
	/**
	 * This will return the list Id
	 * @param List id.
	 * @return the list of the id
	 */
	public MailingList listWithId(int id);
	
	/**
	 * This will return the previous message that was sent out before the passed in message
	 * @param message the message to get the previous message for
	 * @return the previous message that was sent or null if there is none
	 */
	public UniqueMessage prevMessage(UniqueMessage message);
	
	/**
	 * This will return all of the messages for a given mailing list
	 * @param list the list to obtain messages for
	 * @return a list of messages if there are any for a given mailing list, or null
	 * if none found
	 */
	public List<UniqueMessage> messagesForList(MailingList list);
	/**
	 * This will return the most recent message sent out for a mailing list
	 * @param list the mailing list to get the last message for
	 * @return null if none have been sent or the most recent message sent out by date
	 */
	public UniqueMessage lastMessageForList(MailingList list);
	
	/**
	 * This will return the next message that was sent out after the passed in message
	 * @param message the message to get the next message for
	 * @return the next message that was sent or null if there is none
	 */
	public UniqueMessage nextMessage(UniqueMessage message);
	/**
	 * This will return all of the bounce counts for a given email
	 * @param email the email sender
	 * @return all of the message send counts for the given sender, or null on error
	 * or no email found
	 */
	public List<BounceRate> bouncesForEmail(String email);
	
	/**
	 * This will return all of the sent message counts for a given email
	 * @param email the email sender
	 * @return all of the message send counts for the given sender, or null on error
	 * or no email found
	 */
	public List<MessageSend> sendsForEmail(String email);
	
	/**
	 * This will return the number of times the given sender
	 * has sent to the emailTo address
	 * @param sender the sender of the email
	 * @param emailTo the email that was sent to
	 * @return the number of times the sender sent to emailTo
	 */
	public int sentTo(String sender,String emailTo);
	/**
	 * This will return the bounce rate for a given email address relative
	 * to the sender of a message
	 * @param sender the sending email
	 * @param emailTo the email that was sent to
	 * @return the number of times an email was sent from the sender to 
	 * the email and having it bounce
	 */
	public int bounceRateForEmailTo(String sender,String emailTo);
	
	public List<MessageSend> allSend();
	public boolean deleteSend(MessageSend toDelete);
	public boolean addSend(MessageSend toAdd);
	public boolean updateSend(MessageSend toAdd);

	
	public List<BounceRate> allBounces();
	public boolean deleteBounceRate(BounceRate toDelete);
	public boolean addBounce(BounceRate toAdd);
	public boolean updateBounce(BounceRate toAdd);

	public List<UniqueMessage> allMessages();
	public boolean deleteUniqueMessage(UniqueMessage toDelete);
	public boolean addUniqueMessage(UniqueMessage toAdd);
	public UniqueMessage messageWithId(String id);
	
	
	public List<ListMessageTracking> allTracking();
	public boolean deleteMessageTracking(ListMessageTracking toDelete);
	public boolean addMessageTracking(ListMessageTracking toAdd);
	/**
	 * Bring up all of the tracking for the given list
	 * @param list the list to track
	 * @return the list of records for tracking a mailing list
	 */
	public List<ListMessageTracking> trackingForMessage(UniqueMessage message);
	
	
	public List<UserNameLists> allUserNameLists();
	public boolean updateUserNameLists(UserNameLists toDelete);
	public boolean deleteUserNameLists(UserNameLists toDelete);
	public boolean addUserNameList(UserNameLists toAdd);
	
	public List<ListSubscribe> allListSubscribes();
	public boolean updateListSubscribe(ListSubscribe toUpdate);
	public boolean deleteListSubscribe(ListSubscribe toDelete);
	public boolean addListSubscribe(ListSubscribe toAdd);
	
	public List<MailingList> allMailingLists();
	public boolean updateMailingList(MailingList toUpdate);
	public boolean deleteMailingList(MailingList toDelete);
	public boolean addMailingList(MailingList toAdd);
	
	public List<Subscriber> allSubscribers();
	public boolean updateSubscriber(Subscriber toUpdate);
	public boolean deleteSubscriber(Subscriber toDelete);
	public boolean addSubscriber(Subscriber toAdd);
	/**
	 * This will unsubscribe the given subscriber from the given mailing list
	 * @param subscriber the email of the subscriber to unsubscribe
	 * @param unsub the mailing listo unsubscribe from
	 */
	public void unsubUser(String subscriber,MailingList unsub);
	/**
	 * This will unsubscribe the given subscriber from the given mailing list
	 * @param subscriber the subscriber to unsubscribe
	 * @param unsub the mailing listo unsubscribe from
	 */
	public void unsubUser(Subscriber subscriber,MailingList unsub);
	/**
	 * This will subscribe the given emails to the given mailing list, skipping any users
	 * who have already unsubscribed
	 * @param list the collection of emails to add
	 * @param emails the emails to subscribe
	 */
	public void addEmails(MailingList list,List<Subscriber> emails);
	/**
	 * This will subscribe the given emails to the given mailing list, skipping any users
	 * who have already unsubscribed
	 * @param list the collection of emails to add
	 * @param emails the emails to subscribe
	 */
	public void addEmails(MailingList list,Set<Subscriber> emails);
	
	
	
	/**
	 * This will subscribe the given emails to the given mailing list, skipping any users
	 * who have already unsubscribed
	 * @param list the collection of emails to add
	 * @param emails the emails to subscribe
	 */
	public void addEmails(MailingList list,String[] emails);
	
	/**
	 * This will subscribe the given emails to the given mailing list, skipping any users
	 * who have already unsubscribed
	 * @param list the collection of emails to add
	 * @param emails the emails to subscribe
	 */
	public void addEmails(MailingList list,Collection<String> emails);
	/**
	 * This will determine whether the given email
	 * has already unsubscribed from the list
	 * @param email the email to check
	 * @param list the list to test
	 * @return true if the given email has already unsubscribed, false otherwise
	 */
	public boolean alreadyUnSubbed(String email,MailingList list);
	/**
	 * This will return whether the given subscriber is subscribed to this list
	 * @param list the list to test
	 * @param subscriber the subscriber to test
	 * @return true if the subscriber is subscribed to this list false otherwise
	 */
	public boolean subscribedToList(MailingList list,Subscriber subscriber);
	/**
	 * This will bring up the subscriber with the given email
	 * @param email the email to get the subscribers for
	 * @return the list of subscribers for a given email or null
	 */
	public List<Subscriber> subscriberForEmail(String email);
	/**
	 * This returns a list of mailing lists for a given username
	 * @param userName the user name to retrieve lists for
	 * @return the mailing lists for a user name or null
	 */
	public List<MailingList> listsForUser(String userName);
	/**
	 * This will return the lists with the given email
	 * @param email the email of the mailing list
	 * @return the mailing lists with the given email or null if none exists
	 */
	public List<MailingList> listsWithEmail(String email);
	
	
}
