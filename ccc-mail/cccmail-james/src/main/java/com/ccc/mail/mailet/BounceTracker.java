package com.ccc.mail.mailet;

import java.util.Collection;

import javax.mail.MessagingException;

import org.apache.james.transport.mailets.DSNBounce;
import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.springframework.util.Assert;

import com.ccc.mail.mailinglist.dao.BounceRateManager;
import com.ccc.mail.mailinglist.model.BounceRate;

public class BounceTracker extends DSNBounce {

	
	
	
	@Override
	public void service(Mail mail) throws MessagingException {
		Collection<MailAddress> addresses=this.getRecipients();
		for(MailAddress address : addresses) {
			BounceRate rate = bounceRateManager.ratesForEmail(mail.getSender().toInternetAddress().toString(),address.getLocalPart() +   "@" + address.getDomain());
			if(rate!=null) {
				int bounced=rate.getNumTimesBounced()+1;
				rate.setNumTimesBounced(bounced);
				Assert.isTrue(bounceRateManager.updateE(rate),"Couldn't update bounce rate");
				
			}
			else {
				rate = new BounceRate();
				rate.setSentFrom(mail.getSender().toInternetAddress().toString());
				rate.setEmailAddress(address.toInternetAddress().toString());
				rate.setNumTimesBounced(1);
				Assert.isTrue(bounceRateManager.saveE(rate), "Couldn't save bounce rate");
			}
		}
		
		super.service(mail);
	}

	public static BounceRateManager getBounceRateManager() {
		return bounceRateManager;
	}

	public static void setBounceRateManager(BounceRateManager bounceRateManager) {
		BounceTracker.bounceRateManager = bounceRateManager;
	}

	
	private static BounceRateManager bounceRateManager;
}
