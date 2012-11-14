package com.ccc.mail.mailinglist.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("mailingListManager")
@Component("mailingListManager")
public class MailingListManager extends GenericManager<MailingList> {

	/**
	 * This will return all of the mailing lists a particular email is subscribed to
	 * @param email the email to check for
	 * @return the list of mailing lists that have the given email, or null if null is passed in
	 */
	public List<MailingList> listsForEmail(String email) {
		if(email==null || email.isEmpty()) return null;
		return super.elementsWithValue("list_email",email);
	}//end listsForEmail

	public List<MailingList> listsWithName(String name) {
		return elementsWithValue("name",name);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -3213082434438714853L;

	public MailingListManager() {
		super(MailingList.class);
	}
	
	
	
	
	
}
