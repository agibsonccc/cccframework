package com.ccc.user.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.UserContact;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("userContactManager")
public class UserContactManager extends GenericManager<UserContact>{

	public UserContactManager() {
		super(UserContact.class);
	}
	
	public List<UserContact> contactsForUser(String userName) {
		return super.elementsWithValue("user_contact_for", userName);
	}
}
