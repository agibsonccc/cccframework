package com.ccc.oauth.springsecurity.model;

import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;

import com.ccc.oauth.apimanagement.model.Service;

public class OAuthProviderTokenImplWithService extends OAuthProviderTokenImpl {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2775458275265801429L;





	
	
	public Service getService() {
		return service;
	}







	public void setService(Service service) {
		this.service = service;
	}







	private Service service;
}
