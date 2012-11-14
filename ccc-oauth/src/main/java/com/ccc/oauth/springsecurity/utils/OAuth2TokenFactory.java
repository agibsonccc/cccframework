package com.ccc.oauth.springsecurity.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;

import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.OAuth2AppInfo;
import com.ccc.oauth.apimanagement.model.OAuth2KeyUser;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.client.UserClient;
/**
 * This is an oauth2 token factory. Based on the meta data relative to a user and a service.
 * It will generate oauth provider tokens for user in spring security
 * @author Adam Gibson
 *
 */
public class OAuth2TokenFactory {
	
	
	
	
	public OAuthProviderTokenImpl tokenForUser(String userName) {
		OAuthProviderTokenImpl ret = new OAuthProviderTokenImpl();
		
		OAuth2AppInfo info=oauthService.infoForService(service);
		ret.setAccessToken(accessToken);
		BasicUser user=userClient.userForName(userName);
		ret.setCallbackUrl(info.getRedirectUrl());
		ret.setConsumerKey(info.getClientId());
		ret.setSecret(info.getClientSecret());
		ret.setUserAuthentication(new UsernamePasswordAuthenticationToken(userName,user.getPassword()));
		if(accessToken) {
			OAuth2KeyUser key=oauthService.mostRecentKeyForUserAndService(userName, service);
			AccessKeyForUser keyForUser=oauthService.mostRecentAccessKeyForUserAndService(service, userName);
			ret.setValue(key.getCode());
			ret.setTimestamp(key.getTimeMade().getTime());
			ret.setVerifier(keyForUser.getAccessCode());
		}
		return ret;
	}

	public boolean isAccessToken() {
		return accessToken;
	}
	public void setAccessToken(boolean accessToken) {
		this.accessToken = accessToken;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public OAuth2Service getOauthService() {
		return oauthService;
	}
	public void setOauthService(OAuth2Service oauthService) {
		this.oauthService = oauthService;
	}
	public UserClient getUserClient() {
		return userClient;
	}
	public void setUserClient(UserClient userClient) {
		this.userClient = userClient;
	}

	private boolean accessToken;
	@Autowired(required=false)
	private Service service;
	@Autowired(required=false)
	private OAuth2Service oauthService;
	@Autowired(required=false)
	private UserClient userClient;

}
