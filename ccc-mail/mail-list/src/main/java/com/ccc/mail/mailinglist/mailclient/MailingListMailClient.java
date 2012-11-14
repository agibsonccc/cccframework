package com.ccc.mail.mailinglist.mailclient;

import java.io.File;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.mail.core.mailbox.MailBox;
import com.ccc.mail.core.mailstore.MailStore;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.mail.impl.DefaultMailClient;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.model.Subscriber;
import com.ccc.mail.mailinglist.services.api.MailingListService;
import com.ccc.mail.mailinglist.utils.MailingListUtils;

public class MailingListMailClient extends DefaultMailClient {

	public MailingListMailClient(MailBox mailBox, MailStore store) {
		super(mailBox, store);
	}
	public MailingListMailClient(){}
	
	/**
	 * This will send to all of the emails of the given mailing list, provided they are subscribed
	 * @param headers the base headers to use to send
	 * @param list the mailing list to send to
	 * @return true if the mail was sent, false otherwise
	 */
	public boolean sendToMailingList(Map<String,String> headers,MailingList list,boolean isHtml) {
		Set<Subscriber> subs=list.getSubscribers();
		String csv=MailingListUtils.toCSV(subs,list,mailingListService);
		headers.put(MailConstants.TO_ADDRESSES, csv);
		try {
			if(log.isDebugEnabled())
				log.debug("Attempting to send mail to: " + list.getName());
			return sendMail(headers,isHtml);
		} catch (AddressException e) {
			log.error("Address exception sendingto the mailing list: " + list.getMailingAddress(),e);
		} catch (MessagingException e) {
			log.error("Messaging exception sending to the mailing list: " + list.getMailingAddress(),e);
		}
		return false;

	}//end sendToMailingList
	
	/**
	 * This will send to the given mailing list all of the attachments specified
	 * @param headers the base headers to use
	 * @param list the mailing list to send to
	 * @param attachments the attachments to send
	 * @return true if the mail was sent, false otherwise
	 */
	public boolean sendToMailingListWithAttachments(Map<String,String> headers,MailingList list,File[] attachments,boolean isHtml) {
		Set<Subscriber> subs=list.getSubscribers();
		String csv=MailingListUtils.toCSV(subs,list,mailingListService);
		headers.put(MailConstants.TO_ADDRESSES, csv);
		try {
			if(log.isDebugEnabled())
				log.debug("Attempting to send mail to: " + list.getName());
			return sendMailWithAttachments(headers,attachments,isHtml);
		} catch (AddressException e) {
			log.error("Address exception sendingto the mailing list: " + list.getMailingAddress(),e);
		} catch (MessagingException e) {
			log.error("Messaging exception sending to the mailing list: " + list.getMailingAddress(),e);
		}
		return false;

	}//end sendToMailingListWithAttachments
	
	public MailingListService getMailingListService() {
		return mailingListService;
	}


	public void setMailingListService(MailingListService mailingListService) {
		this.mailingListService = mailingListService;
	}

	private static Logger log=LoggerFactory.getLogger(MailingListMailClient.class);
	@Autowired(required=false)
	private MailingListService mailingListService;

}//end MailingListMailClient
