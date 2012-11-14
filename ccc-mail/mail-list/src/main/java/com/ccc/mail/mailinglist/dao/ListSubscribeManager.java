package com.ccc.mail.mailinglist.dao;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.ListSubscribe;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("listSubscribeManager")
@Component("listSubscribeManager")
public class ListSubscribeManager extends GenericManager<ListSubscribe>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ListSubscribeManager() {
		super(ListSubscribe.class);
	}
}
