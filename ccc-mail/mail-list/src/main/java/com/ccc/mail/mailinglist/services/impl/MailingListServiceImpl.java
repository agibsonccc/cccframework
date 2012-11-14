package com.ccc.mail.mailinglist.services.impl;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ccc.mail.mailinglist.dao.BounceRateManager;
import com.ccc.mail.mailinglist.dao.ListMessageTrackingManager;
import com.ccc.mail.mailinglist.dao.ListSubscribeManager;
import com.ccc.mail.mailinglist.dao.MailingListManager;
import com.ccc.mail.mailinglist.dao.MessageSendManager;
import com.ccc.mail.mailinglist.dao.SubscriberManager;
import com.ccc.mail.mailinglist.dao.UniqueMessageManager;
import com.ccc.mail.mailinglist.dao.UserNameListsDao;
import com.ccc.mail.mailinglist.model.BounceRate;
import com.ccc.mail.mailinglist.model.ListMessageTracking;
import com.ccc.mail.mailinglist.model.ListSubscribe;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.model.MessageSend;
import com.ccc.mail.mailinglist.model.Subscriber;
import com.ccc.mail.mailinglist.model.UniqueMessage;
import com.ccc.mail.mailinglist.model.UserNameLists;
import com.ccc.mail.mailinglist.services.api.MailingListService;
import com.ccc.mail.mailinglist.utils.DateUtils;
//@Service("mailingListService")
public class MailingListServiceImpl implements MailingListService {
	@Override
	public void addTracking(ListMessageTracking listMessageTracking) {
		Assert.isTrue(this.getListMessageTrackingManager().saveE(listMessageTracking));
	}
	@Override
	public void deleteTracking(ListMessageTracking delete) {
		Assert.isTrue(this.getListMessageTrackingManager().deleteE(delete));
	}
	@Override
	public int getNumSubscribed(MailingList list) {
		List<ListSubscribe> subbed=listSubscribeManager.elementsWithValue("list_id", String.valueOf(list.getId()));
		int count=0;
		for(ListSubscribe sub : subbed) {
			if(!sub.isUnsubbed() && sub.getUnSubbedTime()==null)
				count++;
		}
		return count;
	}
	@Override
	public List<Subscriber> getSubscribed(MailingList list) {
		List<ListSubscribe> subbed=listSubscribeManager.elementsWithValue("list_id", String.valueOf(list.getId()));
		List<Subscriber> ret = new ArrayList<Subscriber>();
		for(ListSubscribe sub : subbed) {
			if(!sub.isUnsubbed() && sub.getUnSubbedTime()==null)
				ret.add(sub.getSubscriber());
		}
		return ret;
	}


	@Override
	public int numDaysSubscribed(Subscriber subscriber) {
		Timestamp joined=subscriber.getJoined();
		Assert.notNull(joined,"Invalid joined date,unable to calculate joined");
		Timestamp currTime = new Timestamp(System.currentTimeMillis());
		Calendar cal=Calendar.getInstance();
		cal.setTime(currTime);
		Calendar cal2=Calendar.getInstance();
		cal2.setTime(joined);
		long now=cal.getTimeInMillis();
		long sub=cal2.getTimeInMillis();
		long diff=now-sub;
		long daysToMilliseconds=86400000;
		return (int) (diff/daysToMilliseconds);
	}
	@Override
	public List<UniqueMessage> messagesForRange(MailingList list, String from,
			String to) {
		List<UniqueMessage> messages = messagesForList(list);
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date fromDate=format.parse(from);
			Date toDate=format.parse(to);

			List<UniqueMessage> finalList = new ArrayList<UniqueMessage>();
			Calendar fromCal=Calendar.getInstance();
			Calendar toCal=Calendar.getInstance();
			fromCal.setTime(fromDate);
			toCal.setTime(toDate);
			for(UniqueMessage message: messages){
				if(message.getMessage()==null){
					log.warn("Message with no message found in tracking : " + message.getMaiingList());
					continue;
				}
				Timestamp stamp=message.getMessage().getSent();

				Calendar sentCal=Calendar.getInstance();

				sentCal.setTime(stamp);
				int fromDay=fromCal.get(Calendar.DATE);
				int toDay=toCal.get(Calendar.DATE);
				int sentDay=sentCal.get(Calendar.DATE);
				boolean equalOr=sentDay== fromDay || sentDay==toDay;
				if(message.getMessage().getSent().before(toDate) || message.getMessage().getSent().after(fromDate) || equalOr){
					finalList.add(message);
				}
			}
			return finalList;

		}catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<UniqueMessage> messagesForRange(String listId, String from,
			String to) {
		MailingList list = listWithId(Integer.parseInt(listId));
		List<UniqueMessage> messages = messagesForList(list);

		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

			Date fromDate=format.parse(from);
			Date toDate=format.parse(to);

			List<UniqueMessage> finalList = new ArrayList<UniqueMessage>();
			Calendar fromCal=Calendar.getInstance();
			Calendar toCal=Calendar.getInstance();
			fromCal.setTime(fromDate);
			toCal.setTime(toDate);
			for(UniqueMessage message: messages){
				Timestamp stamp=message.getMessage().getSent();

				Calendar sentCal=Calendar.getInstance();

				sentCal.setTime(stamp);
				int fromDay=fromCal.get(Calendar.DATE);
				int toDay=toCal.get(Calendar.DATE);
				int sentDay=sentCal.get(Calendar.DATE);
				boolean equalOr=sentDay== fromDay || sentDay==toDay;
				if(message.getMessage().getSent().before(toDate) || message.getMessage().getSent().after(fromDate) || equalOr){
					finalList.add(message);
				}
			}
			return finalList;

		}catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<UniqueMessage> messagesForRange(String listId, Date from,
			Date to) {
		MailingList list = listWithId(Integer.parseInt(listId));
		List<UniqueMessage> messages = messagesForList(list);


		List<UniqueMessage> finalList = new ArrayList<UniqueMessage>();
		Calendar fromCal=Calendar.getInstance();
		Calendar toCal=Calendar.getInstance();
		fromCal.setTime(from);
		toCal.setTime(to);
		for(UniqueMessage message: messages){
			Timestamp stamp=message.getMessage().getSent();

			Calendar sentCal=Calendar.getInstance();

			sentCal.setTime(stamp);
			int fromDay=fromCal.get(Calendar.DATE);
			int toDay=toCal.get(Calendar.DATE);
			int sentDay=sentCal.get(Calendar.DATE);
			boolean equalOr=sentDay== fromDay || sentDay==toDay;
			if(message.getMessage().getSent().before(to) || message.getMessage().getSent().after(from) || equalOr){
				finalList.add(message);
			}
		}
		return finalList;
	}

	@Override
	public List<UniqueMessage> messagesForRange(String listId,String from,String to, String email) {
		MailingList list = listWithId(Integer.parseInt(listId));
		List<UniqueMessage> messages = messagesForList(list);
		try {
			Date fromDate=DateUtils.parseDate(from);
			Date toDate=DateUtils.parseDate(to);

			List<UniqueMessage> finalList = new ArrayList<UniqueMessage>();
			Calendar fromCal=Calendar.getInstance();
			Calendar toCal=Calendar.getInstance();
			fromCal.setTime(fromDate);
			toCal.setTime(toDate);
			for(UniqueMessage message: messages){
				Timestamp stamp=message.getMessage().getSent();

				Calendar sentCal=Calendar.getInstance();

				sentCal.setTime(stamp);
				int fromDay=fromCal.get(Calendar.DATE);
				int toDay=toCal.get(Calendar.DATE);
				int sentDay=sentCal.get(Calendar.DATE);
				boolean equalOr=sentDay== fromDay || sentDay==toDay;
				if(message.getMessage().getSent().before(toDate) || message.getMessage().getSent().after(fromDate) || equalOr){
					finalList.add(message);
				}
			}
			return finalList;

		}catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<UniqueMessage> messagesForRange(MailingList list, Date from,
			Date to, String email) {
		List<UniqueMessage> messages = messagesForList(list);

		List<UniqueMessage> finalList = new ArrayList<UniqueMessage>();
		Calendar fromCal=Calendar.getInstance();
		Calendar toCal=Calendar.getInstance();
		fromCal.setTime(from);
		toCal.setTime(to);
		for(UniqueMessage message: messages){
			Timestamp stamp=message.getMessage().getSent();

			Calendar sentCal=Calendar.getInstance();

			sentCal.setTime(stamp);
			int fromDay=fromCal.get(Calendar.DATE);
			int toDay=toCal.get(Calendar.DATE);
			int sentDay=sentCal.get(Calendar.DATE);
			boolean equalOr=sentDay== fromDay || sentDay==toDay;
			if(message.getMessage().getSent().before(to) || message.getMessage().getSent().after(from) || equalOr){
				finalList.add(message);
			}
		}
		return finalList;
	}

	@Override
	public List<UniqueMessage> messagesForRange(MailingList list, String from,
			String to, String email) {
		List<UniqueMessage> messages = messagesForList(list);
		try {
			Date fromDate=DateUtils.parseDate(from);
			Date toDate=DateUtils.parseDate(to);

			List<UniqueMessage> finalList = new ArrayList<UniqueMessage>();
			Calendar fromCal=Calendar.getInstance();
			Calendar toCal=Calendar.getInstance();
			fromCal.setTime(fromDate);
			toCal.setTime(toDate);
			for(UniqueMessage message: messages){
				Timestamp stamp=message.getMessage().getSent();

				Calendar sentCal=Calendar.getInstance();

				sentCal.setTime(stamp);
				int fromDay=fromCal.get(Calendar.DATE);
				int toDay=toCal.get(Calendar.DATE);
				int sentDay=sentCal.get(Calendar.DATE);
				boolean equalOr=sentDay== fromDay || sentDay==toDay;
				if(message.getMessage().getSent().before(toDate) || message.getMessage().getSent().after(fromDate) || equalOr){
					finalList.add(message);
				}
			}
			return finalList;

		}catch(Exception e) {
			throw new IllegalStateException(e);
		}
	}



	@Override
	public MailingList listWithEmail(String email) {

		List<MailingList> list= mailingListManager.elementsWithValue("email", email);
		return list!=null && !list.isEmpty() ? list.get(0) : null;
	}




	@Override
	public int openRate(String email) {
		return listMessageTrackingManager.openRateForEmail(email);
	}

	@Override
	public  MailingList listWithId(int id){
		List<MailingList> list= mailingListManager.elementsWithValue("list_id", String.valueOf(id));
		return list!=null && !list.isEmpty() ? list.get(0) : null;

	}


	@Override
	public UniqueMessage prevMessage(UniqueMessage message) {
		List<UniqueMessage> messagesForList=messagesForList(message.getMaiingList());

		if(messagesForList!=null && !messagesForList.isEmpty()) {
			Collections.sort(messagesForList,messagesForList.get(0));
			int i=messagesForList.indexOf(message);
			if(i < 1 || i==0) return null;
			return messagesForList.get(i-1);
		}
		return null;
	}
	@Override
	public List<UniqueMessage> messagesForList(MailingList list) {
		String id=String.valueOf(list.getId());
		return uniqueMessageManager.elementsWithValue("list_id", id);
	}
	@Override
	public UniqueMessage lastMessageForList(MailingList list) {
		List<UniqueMessage> messagesForList=messagesForList(list);
		if(messagesForList!=null && !messagesForList.isEmpty()) {
			Collections.sort(messagesForList,messagesForList.get(0));
			int size=messagesForList.size();
			return messagesForList.get(size-1);
		}
		return null;
	}


	@Override
	public UniqueMessage nextMessage(UniqueMessage message) {
		List<UniqueMessage> messagesForList=messagesForList(message.getMaiingList());

		if(messagesForList!=null && !messagesForList.isEmpty()) {
			Collections.sort(messagesForList,messagesForList.get(0));
			int i=messagesForList.indexOf(message);
			if(i < 1 || i==messagesForList.size()-1) return null;
			return messagesForList.get(i+1);
		}
		return null;
	}


	@Override
	public List<MessageSend> allSend() {
		return messageSendManager.allElements();
	}


	@Override
	public List<BounceRate> bouncesForEmail(String email) {
		List<BounceRate> ret=bounceRateManager.elementsWithValue("sent_from",email);
		return ret;
	}




	@Override
	public List<MessageSend> sendsForEmail(String email) {
		return messageSendManager.elementsWithValue("sent_from", email);
	}

	@Override
	public boolean deleteSend(MessageSend toDelete) {
		return messageSendManager.deleteE(toDelete);
	}

	@Override
	public boolean addSend(MessageSend toAdd) {
		return messageSendManager.saveE(toAdd);
	}

	@Override
	public boolean updateSend(MessageSend toAdd) {
		return messageSendManager.updateE(toAdd);
	}

	@Override
	public List<BounceRate> allBounces() {
		return bounceRateManager.allElements();
	}

	@Override
	public boolean deleteBounceRate(BounceRate toDelete) {
		return bounceRateManager.deleteE(toDelete);
	}

	@Override
	public boolean addBounce(BounceRate toAdd) {
		return bounceRateManager.saveE(toAdd);
	}

	@Override
	public boolean updateBounce(BounceRate toAdd) {
		return bounceRateManager.updateE(toAdd);
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Override
	public List<UserNameLists> allUserNameLists() {
		return userNameListManager.allElements();
	}

	@Override
	public boolean updateUserNameLists(UserNameLists toUpdate) {
		return userNameListManager.updateE(toUpdate);
	}

	@Override
	public boolean deleteUserNameLists(UserNameLists toDelete) {
		return userNameListManager.deleteE(toDelete);
	}

	@Override
	public boolean addUserNameList(UserNameLists toAdd) {
		return userNameListManager.saveE(toAdd);
	}

	@Override
	public List<ListSubscribe> allListSubscribes() {
		return listSubscribeManager.allElements();
	}

	@Override
	public boolean updateListSubscribe(ListSubscribe toUpdate) {
		return listSubscribeManager.updateE(toUpdate);
	}

	@Override
	public boolean deleteListSubscribe(ListSubscribe toDelete) {
		return listSubscribeManager.deleteE(toDelete);
	}

	@Override
	public boolean addListSubscribe(ListSubscribe toAdd) {
		return listSubscribeManager.saveE(toAdd);
	}

	@Override
	public List<MailingList> allMailingLists() {
		return mailingListManager.allElements();
	}

	@Override
	public boolean updateMailingList(MailingList toUpdate) {
		return mailingListManager.updateE(toUpdate);
	}

	@Override
	public boolean deleteMailingList(MailingList toDelete) {
		return mailingListManager.deleteE(toDelete);
	}

	@Override
	public boolean addMailingList(MailingList toAdd) {
		return mailingListManager.saveE(toAdd);
	}

	@Override
	public List<Subscriber> allSubscribers() {
		return subscriberManager.allElements();
	}

	@Override
	public boolean updateSubscriber(Subscriber toUpdate) {
		return subscriberManager.updateE(toUpdate);
	}

	@Override
	public boolean deleteSubscriber(Subscriber toDelete) {
		return subscriberManager.deleteE(toDelete);
	}

	@Override
	public boolean addSubscriber(Subscriber toAdd) {
		return subscriberManager.saveE(toAdd);
	}

	@Override
	public void unsubUser(String subscriber, MailingList unsub) {
		List<Subscriber> subscribers=subscriberManager.elementsWithValue("email", subscriber);
		for(Subscriber sub : subscribers) {
			Set<MailingList> mailingLists=sub.getMailngLists();
			for(MailingList list : mailingLists) {
				if(list.equals(unsub)) {
					List<ListSubscribe> subscribe=listSubscribeManager.elementsWithValue("subscriber_id", String.valueOf(sub.getId()));
					if(subscribe!=null && !subscribe.isEmpty()) {
						for(ListSubscribe listSub : subscribe) {
							if(listSub.getMailingList().equals(unsub)) {
								listSub.setUnSubbedTime(new Timestamp(System.currentTimeMillis()));
								listSub.setUnsubbed(true);
								listSubscribeManager.updateE(listSub);
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void unsubUser(Subscriber subscriber, MailingList unsub) {
		unsubUser(subscriber.getEmail(),unsub);
	}

	@Override
	public void addEmails(MailingList list, List<Subscriber> emails) {
		for(Subscriber sub : emails) {
			String email=sub.getEmail().trim();
			List<ListSubscribe> subList=listSubscribeManager.elementsWithValue("id", String.valueOf(sub.getId()));
			if(subList==null  &&!alreadyUnSubbed(email,list) ) {
				ListSubscribe newSub = new ListSubscribe();
				newSub.setJoined(new Timestamp(System.currentTimeMillis()));
				newSub.setMailingList(list);
				newSub.setSubscriber(sub);
				Assert.isTrue(listSubscribeManager.saveE(newSub),"Failed to save list subscriber");
			}

		}
	}

	@Override
	public void addEmails(MailingList list, Set<Subscriber> emails) {
		for(Subscriber sub : emails) {
			String s=sub.getEmail();
			List<ListSubscribe> subList=listSubscribeManager.elementsWithValue("id", String.valueOf(sub.getId()));
			if(subList==null  && !alreadyUnSubbed(s.trim(),list) ) {
				Assert.isTrue(subscriberManager.saveE(sub),"Failed to save subscriber");

				ListSubscribe newSub = new ListSubscribe();
				newSub.setJoined(new Timestamp(System.currentTimeMillis()));
				newSub.setMailingList(list);
				newSub.setSubscriber(sub);
				Assert.isTrue(listSubscribeManager.saveE(newSub),"Failed to save list subscriber");
			}

		}		
	}
	@Override
	public void addEmails(MailingList list, String[] emails) {
		for(String s : emails) {
			List<Subscriber> subWithEmail=subscriberManager.elementsWithValue("email", s);
			if(subWithEmail!=null && !subWithEmail.isEmpty()  && !alreadyUnSubbed(s,list)) {
				Subscriber subscriber=subWithEmail.get(0);
				ListSubscribe newSub = new ListSubscribe();
				newSub.setJoined(new Timestamp(System.currentTimeMillis()));
				newSub.setMailingList(list);
				newSub.setSubscriber(subscriber);
				Assert.isTrue(listSubscribeManager.saveE(newSub),"Couldn't subsribe user");

			}


		}		
	}

	@Override
	public void addEmails(MailingList list, Collection<String> emails) {
		for(String s : emails) {
			List<Subscriber> subWithEmail=subscriberManager.elementsWithValue("email", s);
			if(subWithEmail==null || subWithEmail.isEmpty()) {
				Subscriber subscriber = new Subscriber();
				subscriber.setEmail(s);
				subscriber.setJoined(new Timestamp(System.currentTimeMillis()));
				subscriber.setMailngLists(Collections.singleton(list));
				addSubscriber(subscriber);
			}
			subWithEmail=subscriberManager.elementsWithValue("email", s);
			if(subWithEmail!=null && !subWithEmail.isEmpty() && !alreadyUnSubbed(s,list)) {
				Subscriber subscriber=subWithEmail.get(0);
				ListSubscribe newSub = new ListSubscribe();
				newSub.setJoined(new Timestamp(System.currentTimeMillis()));
				newSub.setMailingList(list);
				newSub.setSubscriber(subscriber);
				Assert.isTrue(listSubscribeManager.saveE(newSub),"Couldn't save subscriber");


			}
			else {
				Subscriber subscriber = new Subscriber();
				subscriber.setEmail(s);
				subscriber.setMailngLists(Collections.singleton(list));
				Assert.isTrue(subscriberManager.saveE(subscriber),"Failed to save subscriber");

				ListSubscribe newSub = new ListSubscribe();
				newSub.setJoined(new Timestamp(System.currentTimeMillis()));
				newSub.setMailingList(list);
				newSub.setSubscriber(subscriber);
				Assert.isTrue(listSubscribeManager.saveE(newSub),"Couldn't save list subscriber");
			}
		}
	}

	@Override
	public boolean alreadyUnSubbed(String email, MailingList list) {
		List<Subscriber> subWithEmail=subscriberManager.elementsWithValue("email", email);
		for(Subscriber sub : subWithEmail) {
			List<ListSubscribe> subList=listSubscribeManager.elementsWithValue("subscriber_id",String.valueOf(sub.getId()));
			//nothing has been added, assume the subscriber is newly added and subscribed
			if(subList==null) {
				ListSubscribe listSub = new ListSubscribe();
				listSub.setJoined(sub.getJoined());
				listSub.setMailingList(list);
				listSub.setSubscriber(sub);
				listSub.setUnsubbed(false);
				Assert.isTrue(listSubscribeManager.saveE(listSub),"Couldn't save list subscriber");
				subList = new ArrayList<ListSubscribe>();
				subList.add(listSub);
			}

			for(ListSubscribe listSub : subList) {
				if(listSub.isUnsubbed()) 
					return true;
				else continue;
			}
		}
		return false;
	}

	@Override
	public UniqueMessage messageWithId(String id) {
		List<UniqueMessage> messages= uniqueMessageManager.elementsWithValue("id", id);
		return messages==null || messages.isEmpty() ? null : messages.get(0);
	}


	@Override
	public List<UniqueMessage> allMessages() {
		return uniqueMessageManager.allElements();
	}

	@Override
	public boolean deleteUniqueMessage(UniqueMessage toDelete) {
		return uniqueMessageManager.deleteE(toDelete);
	}

	@Override
	public boolean addUniqueMessage(UniqueMessage toAdd) {
		return uniqueMessageManager.saveE(toAdd);
	}
	@Override
	public boolean deleteMessageTracking(ListMessageTracking toDelete) {
		return listMessageTrackingManager.deleteE(toDelete);
	}

	@Override
	public boolean addMessageTracking(ListMessageTracking toAdd) {
		return listMessageTrackingManager.saveE(toAdd);
	}

	@Override
	public List<ListMessageTracking> trackingForMessage(UniqueMessage message) {


		return listMessageTrackingManager.elementsWithValue("message_id", message.getId());
	}
	@Override
	public List<ListMessageTracking> allTracking() {
		return listMessageTrackingManager.allElements();
	}

	@Override
	public boolean subscribedToList(MailingList list, Subscriber subscriber) {
		return !alreadyUnSubbed(subscriber.getEmail(),list);
	}

	@Override
	public List<Subscriber> subscriberForEmail(String email) {
		return subscriberManager.elementsWithValue("email", email);
	}

	@Override
	public List<MailingList> listsForUser(String userName) {
		List<MailingList> ret=mailingListManager.elementsWithValue("owner", userName);

		return ret;
	}
	@Override
	public List<MailingList> listsWithEmail(String email) {
		return mailingListManager.elementsWithValue("list_email", email);
	}

	@Override
	public int sentTo(String sender, String emailTo) {
		MessageSend send=messageSendManager.trackerForFromAndTo(sender, emailTo);
		return  send == null ? 	0 : send.getNumTimes();
	}

	@Override
	public int bounceRateForEmailTo(String sender, String emailTo) {
		BounceRate rate=bounceRateManager.ratesForEmail(sender, emailTo);

		return rate== null ? 0 : rate.getNumTimesBounced();
	}
	public UserNameListsDao getUserNameListManager() {
		return userNameListManager;
	}

	public void setUserNameListManager(UserNameListsDao userNameListManager) {
		this.userNameListManager = userNameListManager;
	}

	public SubscriberManager getSubscriberManager() {
		return subscriberManager;
	}

	public void setSubscriberManager(SubscriberManager subscriberManager) {
		this.subscriberManager = subscriberManager;
	}

	public MailingListManager getMailingListManager() {
		return mailingListManager;
	}

	public void setMailingListManager(MailingListManager mailingListManager) {
		this.mailingListManager = mailingListManager;
	}

	public ListSubscribeManager getListSubscribeManager() {
		return listSubscribeManager;
	}

	public void setListSubscribeManager(ListSubscribeManager listSubscribeManager) {
		this.listSubscribeManager = listSubscribeManager;
	}

	public ListMessageTrackingManager getListMessageTrackingManager() {
		return listMessageTrackingManager;
	}

	public void setListMessageTrackingManager(
			ListMessageTrackingManager listMessageTrackingManager) {
		this.listMessageTrackingManager = listMessageTrackingManager;
	}

	public UniqueMessageManager getUniqueMessageManager() {
		return uniqueMessageManager;
	}

	public void setUniqueMessageManager(UniqueMessageManager uniqueMessageManager) {
		this.uniqueMessageManager = uniqueMessageManager;
	}


	@Autowired(required=false)
	private UniqueMessageManager uniqueMessageManager;
	@Autowired(required=false)
	private ListSubscribeManager listSubscribeManager;
	@Autowired(required=false)
	private UserNameListsDao userNameListManager;
	@Autowired(required=false)
	private SubscriberManager subscriberManager;
	private EmailValidator validator=EmailValidator.getInstance();
	@Autowired(required=false)
	private MailingListManager mailingListManager;
	@Autowired
	private ListMessageTrackingManager listMessageTrackingManager;
	@Autowired
	private BounceRateManager bounceRateManager;
	@Autowired
	private MessageSendManager messageSendManager;
	private static Logger log=LoggerFactory.getLogger(MailingListServiceImpl.class);






}
