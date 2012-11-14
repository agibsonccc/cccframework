package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.oauth.apimanagement.model.Contact;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("contactManager")
public class ContactManager extends GenericManager<Contact>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7301658593745564313L;

	public ContactManager() {
		super(Contact.class);
	}
}
