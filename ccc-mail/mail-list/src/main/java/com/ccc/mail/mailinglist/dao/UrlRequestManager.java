package com.ccc.mail.mailinglist.dao;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.UrlRequest;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("urlRequestManager")
public class UrlRequestManager  extends GenericManager<UrlRequest>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5651841569300000701L;

	public UrlRequestManager() {
		super(UrlRequest.class);
	}
}
