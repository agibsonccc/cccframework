package com.ccc.mail.mailinglist.dao;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.UrlTracking;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("urlTrackingManager")
public class UrlTrackingManager extends GenericManager<UrlTracking>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5348805280391800714L;

	public UrlTrackingManager() {
		super(UrlTracking.class);
	}
}
