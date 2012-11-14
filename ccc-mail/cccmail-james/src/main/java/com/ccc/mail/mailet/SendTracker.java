package com.ccc.mail.mailet;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.springframework.util.Assert;
import org.apache.james.transport.mailets.RemoteDelivery;

import com.ccc.mail.mailinglist.dao.ListMessageTrackingManager;
import com.ccc.mail.mailinglist.dao.MessageSendManager;
import com.ccc.mail.mailinglist.model.ListMessageTracking;
import com.ccc.mail.mailinglist.model.MessageSend;
public class SendTracker extends RemoteDelivery {

	@Override
	public void service(Mail mail) throws MessagingException {
		Collection recipients=mail.getRecipients();
		for(Object o : recipients) {
			String recipAddress=o.toString();
			MailAddress address=mail.getSender();
			String senderAddress=null;
			if(address!=null) {
				senderAddress=address.toString();



				MessageSend send=messageSendManager.trackerForFromAndTo(senderAddress, recipAddress);
				String id=findId();
				ListMessageTracking track = new ListMessageTracking();
				track.setClickId(id);
				track.setEmail(recipAddress);
				Assert.isTrue(tracker.saveE(track),"Couldn't save email tracking when sending");

				if(send!=null) {
					send.setNumTimes(send.getNumTimes()+1);
					Assert.isTrue(messageSendManager.updateE(send),"Couldn't update message send");
				}
				else {
					MessageSend messageSend = new MessageSend();
					messageSend.setEmailTo(recipAddress);
					messageSend.setSentFrom(mail.getSender().toInternetAddress().toString());
					messageSend.setNumTimes(1);
					Assert.isTrue(messageSendManager.saveE(messageSend),"Couldn't save message sending");
				}
			}
			else {
				senderAddress="None";
				MessageSend send=messageSendManager.trackerForFromAndTo(senderAddress, recipAddress);
				String id=findId();
				ListMessageTracking track = new ListMessageTracking();
				track.setClickId(id);
				track.setEmail(recipAddress);
				Assert.isTrue(tracker.saveE(track),"Couldn't save email tracking when sending");

				if(send!=null) {
					send.setNumTimes(send.getNumTimes()+1);
					Assert.isTrue(messageSendManager.updateE(send),"Couldn't update message send");
				}
				else {
					MessageSend messageSend = new MessageSend();
					messageSend.setEmailTo(recipAddress);
					MailAddress sender=mail.getSender();
					if(sender!=null) {
						InternetAddress add=sender.toInternetAddress();
						if(add!=null)
							messageSend.setSentFrom(add.toString());
						else messageSend.setSentFrom(sender.toString());
					}
					
					
					messageSend.setNumTimes(1);
					Assert.isTrue(messageSendManager.saveE(messageSend),"Couldn't save message sending");
				}
			}

		}
		super.service(mail);
	}

	private String findId() {
		String id=UUID.randomUUID().toString();
		List<ListMessageTracking> tracking=tracker.elementsWithValue("id", id);
		if(tracking==null || tracking.isEmpty())
			return id;
		else {
			while((tracking=tracker.elementsWithValue("id", id))!=null && !tracking.isEmpty())
				id=UUID.randomUUID().toString();
			return id;
		}

	}



	public static ListMessageTrackingManager getTracker() {
		return tracker;
	}

	public static void setTracker(ListMessageTrackingManager tracker) {
		SendTracker.tracker = tracker;
	}

	public static MessageSendManager getMessageSendManager() {
		return messageSendManager;
	}




	public static void setMessageSendManager(MessageSendManager messageSendManager) {
		SendTracker.messageSendManager = messageSendManager;
	}



	private static ListMessageTrackingManager tracker;
	private static MessageSendManager messageSendManager;

}
