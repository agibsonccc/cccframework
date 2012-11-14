package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.OAuth2AppInfo;
@Repository("oauth2AppInfoManager")
public class OAuth2AppInfoManager extends GenericManager<OAuth2AppInfo> {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -810666302089182503L;

	public OAuth2AppInfoManager() {
		super(OAuth2AppInfo.class);
	}
}
