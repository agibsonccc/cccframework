package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.OAuth2Urls;

@Repository("oauth2UrlsManager")
public class OAuth2UrlsManager extends GenericManager<OAuth2Urls>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1131813334624825662L;

	public OAuth2UrlsManager() {
		super(OAuth2Urls.class);
	}
	
}
