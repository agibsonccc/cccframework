package com.ccc.mail.mailinglist.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.AutoResponderMessage;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("autoResponderManager")
public class AutoResponderManager extends GenericManager<AutoResponderMessage> {

	public AutoResponderManager() {
		super(AutoResponderMessage.class);
	}
	
	public List<AutoResponderMessage> autoResponderMessagesForList(MailingList list) {
		int id=(Integer)list.getId();
		String match=String.valueOf(id);
		return super.elementsWithValue("list_id", match);
	}
	
	
}
