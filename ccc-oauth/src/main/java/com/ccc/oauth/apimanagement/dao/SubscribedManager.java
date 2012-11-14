package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.Subscribed;

@Repository("subscribedManager")
public class SubscribedManager extends GenericManager<Subscribed> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8257179330187659622L;

	public SubscribedManager() {
		super(Subscribed.class);
	}
}
