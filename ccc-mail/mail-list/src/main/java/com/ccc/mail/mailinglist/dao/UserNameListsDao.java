package com.ccc.mail.mailinglist.dao;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.UserNameLists;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("userNameListDao")
@Component("userNameListDao")
public class UserNameListsDao extends GenericManager<UserNameLists>{
	
	public List<UserNameLists> emailsForName(String userName) {
		return super.elementsWithValue("user_name", userName);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public UserNameListsDao() {
		super(UserNameLists.class);
	}
}
