package com.ccc.mail.mailinglist.james.matcher;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMatcher;

import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.services.api.MailingListService;

public class MailingListAddressMatcher extends GenericMatcher {

	@Override
	public Collection<MailAddress> match(Mail mail) throws MessagingException {
		if(mailingListService==null) {
			throw new IllegalStateException("No mailing list service initialized");
		}
		
		String from=mail.getSender().toString();
		List<MailingList> list=mailingListService.listsWithEmail(from);
		if(list!=null && !list.isEmpty()) {
			return Collections.singletonList(mail.getSender());
		}
		
		return null;
	}

	public static void setMailingListService(MailingListService service) {
		MailingListAddressMatcher.mailingListService=service;
	}
	
	
	private static MailingListService mailingListService;
}
