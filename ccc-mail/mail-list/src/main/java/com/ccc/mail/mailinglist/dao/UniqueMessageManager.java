package com.ccc.mail.mailinglist.dao;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.UniqueMessage;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("uniqueMessageManager")
public class UniqueMessageManager extends GenericManager<UniqueMessage>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UniqueMessageManager() {
		super(UniqueMessage.class);	
	}
}
