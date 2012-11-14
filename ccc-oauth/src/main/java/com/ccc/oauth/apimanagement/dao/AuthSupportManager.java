package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.AuthSupports;

@Repository("authSupportManager")
public class AuthSupportManager extends GenericManager<AuthSupports> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6416036475393736573L;

	public AuthSupportManager() {
		super(AuthSupports.class);
	}
}
