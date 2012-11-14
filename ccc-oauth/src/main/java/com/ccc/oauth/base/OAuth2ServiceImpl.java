package com.ccc.oauth.base;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth.common.OAuthException;
import org.springframework.security.oauth.provider.ConsumerDetails;
import org.springframework.security.oauth.provider.token.OAuthAccessProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderToken;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenImpl;
import org.springframework.security.oauth.provider.token.RandomValueProviderTokenServices;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ccc.oauth.amber.oauth2.model.OAuthParams;
import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.dao.AccessKeyManager;
import com.ccc.oauth.apimanagement.dao.ApiManager;
import com.ccc.oauth.apimanagement.dao.AuthSupportManager;
import com.ccc.oauth.apimanagement.dao.OAuth1AccessTokenManager;
import com.ccc.oauth.apimanagement.dao.OAuth1InfoManager;
import com.ccc.oauth.apimanagement.dao.OAuth1RequestTokenManager;
import com.ccc.oauth.apimanagement.dao.OAuth2AppInfoManager;
import com.ccc.oauth.apimanagement.dao.OAuth2KeysManager;
import com.ccc.oauth.apimanagement.dao.OAuth2UrlsManager;
import com.ccc.oauth.apimanagement.dao.ScopeHolderManager;
import com.ccc.oauth.apimanagement.dao.ScopeServiceManager;
import com.ccc.oauth.apimanagement.dao.ServiceManager;
import com.ccc.oauth.apimanagement.dao.ServiceProviderManager;
import com.ccc.oauth.apimanagement.dao.SubscribedManager;
import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.AuthSupports;
import com.ccc.oauth.apimanagement.model.OAuth1AccessToken;
import com.ccc.oauth.apimanagement.model.OAuth1Info;
import com.ccc.oauth.apimanagement.model.OAuth1RequestToken;
import com.ccc.oauth.apimanagement.model.OAuth2AppInfo;
import com.ccc.oauth.apimanagement.model.OAuth2KeyUser;
import com.ccc.oauth.apimanagement.model.OAuth2Urls;
import com.ccc.oauth.apimanagement.model.ScopeHolder;
import com.ccc.oauth.apimanagement.model.Subscribed;
import com.ccc.oauth.springsecurity.model.OAuthProviderTokenImplWithService;
import com.ccc.oauth.util.HttpAuthUtils;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.client.UserClient;

@Service("oauth2Service")
public class OAuth2ServiceImpl extends RandomValueProviderTokenServices implements OAuth2Service {

	public boolean processRedirect(HttpServletRequest request,HttpServletResponse response,String token,String error,String errorMessage,String oauth1Token,String oauthVerifier,com.ccc.oauth.apimanagement.model.Service s,String userName) {
		if(log.isDebugEnabled()) {
			log.debug("Code {} token {} error {} error descriptor {}  error message {} oauthtoken {} oauthverifer {} ",new Object[]{token,token,error,errorMessage,oauth1Token,oauthVerifier});
		}

		if(error!=null && errorMessage!=null) {
			log.warn("Error found when retrieving code: " + " Error type: " + error + " and message: " + errorMessage);
		}
		OAuth2KeyUser key=null;


		List<AuthSupports> supported=supportedAuthForService(s);
		for(AuthSupports supports : supported) {
			if(supports.getAuthType().getName().equals(HttpAuthUtils.OAUTH2)) {

				key = new OAuth2KeyUser();
				key.setUserName(userName);
				key.setService(s);
				key.setTimeMade(new Timestamp(System.currentTimeMillis()));
				key.setCode(token);
				if(log.isDebugEnabled()) {
					log.debug("Added new key for: " + userName + " and service: " + s.getName());
				}


				Assert.isTrue(addKeyForUser(key));
				AccessKeyForUser accessKey=HttpAuthUtils.requestAccessWithLookup(this, userName, s.getId());
				if(log.isDebugEnabled()) {
					log.debug("Requested to find access key: " + accessKey== null ? " not found: " : accessKey.getAccessCode());

				}

				if(accessKey==null) {
					log.warn("Failed to retrieve access key");
					return false;

				}

				Assert.isTrue(addKeyForUser(accessKey),"Couldn't add key for user: " + userName);


			}
			else if(supports.getAuthType().getName().equals(HttpAuthUtils.OAUTH)) {
				OAuth1RequestToken requestToken=mostRecentOauthRequestTokenForUserAndService(s, userName);
				if(requestToken!=null) {
					if(log.isDebugEnabled()) {
						log.debug("Attempting oauth authorization");
					}

					String toSave=null;
					//1.0a compliance
					if(oauthVerifier!=null && oauth1Token!=null)
						toSave=oauthVerifier;
					else if(oauthVerifier!=null) toSave=oauthVerifier;
					else if(oauth1Token!=null) toSave=oauth1Token;

					if(toSave!=null) {

						requestToken.setRequestToken(toSave);
					}
					else {
						if(log.isDebugEnabled()) {
							log.debug("Attempted to save oauth 1 token for service: {} but couldn't find token passed in  for user {}",s.getName(),userName);
						}
					}
					if(log.isDebugEnabled()) {
						log.debug("Found request token: "   + toSave);
					}
					updateRequestToken(requestToken);
				}


				OAuth1Info oauth1Info=oauthInfoForService(s);

				try {
					String accessToken=HttpAuthUtils.getAccessTokenFromPin(oauth1Info,this,requestToken);
					if(accessToken==null) 
						log.warn("Error retrieving access token for {}",s.getName());

					log.info("Access token: " + accessToken);
				} catch (OAuthMessageSignerException e) {
					log.error("Error oauth1 token: ",e);
					return false;
				} catch (OAuthNotAuthorizedException e) {
					log.error("Error oauth1 token: ",e);
					return false;
				} catch (OAuthExpectationFailedException e) {
					log.error("Error oauth1 token: ",e);
					return false;
				} catch (OAuthCommunicationException e) {
					log.error("Error oauth1 token: ",e);
					return false;
				}

			}
		}
		return true;
	}





	@Override
	public OAuth1Info oauthInfoForService(
			com.ccc.oauth.apimanagement.model.Service service) {
		return oauth1InfoManager.infoForService(service);
	}
	@Override
	public boolean deleteRequestToken(OAuth1RequestToken toDelete) {
		return oauthRequestTokenManager.deleteE(toDelete);
	}
	@Override
	public boolean updateRequestToken(OAuth1RequestToken toUpdate) {
		return oauthRequestTokenManager.updateE(toUpdate);
	}
	@Override
	public boolean addRequestToken(OAuth1RequestToken toAdd) {
		return oauthRequestTokenManager.saveE(toAdd);
	}
	@Override
	public boolean deleteAccessToken(OAuth1AccessToken toDelete) {
		return oauth1AccessTokenManager.deleteE(toDelete);
	}
	@Override
	public boolean updateAccessToken(OAuth1AccessToken toUpdate) {
		return oauth1AccessTokenManager.updateE(toUpdate);
	}
	@Override
	public boolean addAccessToken(OAuth1AccessToken toAdd) {
		return oauth1AccessTokenManager.saveE(toAdd);
	}



	@Override
	public OAuth1RequestToken mostRecentOauthRequestTokenForUserAndService(
			com.ccc.oauth.apimanagement.model.Service service, String userName) {
		return oauthRequestTokenManager.mostRecentKeyForUserAndService(userName, service);
	}
	@Override
	public List<OAuth1RequestToken> oauthRequestTokenForUserAndService(
			com.ccc.oauth.apimanagement.model.Service service, String userName) {
		return oauthRequestTokenManager.tokensForServiceAndUser(service, userName);
	}
	@Override
	public OAuth1AccessToken mostRecentOauthAccessTokenForUserAndService(
			com.ccc.oauth.apimanagement.model.Service service, String userName) {
		return oauth1AccessTokenManager.mostRecentKeyForUserAndService(userName, service);
	}

	@Override
	public boolean deleteKeyForUser(AccessKeyForUser toDelete) {
		return accessKeyManager.deleteE(toDelete);
	}
	@Override
	public boolean updateKeyForUser(AccessKeyForUser toUpdate) {
		return accessKeyManager.updateE(toUpdate);
	}
	@Override
	public boolean addKeyForUser(AccessKeyForUser toAdd) {
		return accessKeyManager.saveE(toAdd);
	}

	@Override
	public boolean saveAccessToken(OAuth2Urls urls, OAuth2AppInfo info,
			OAuth2KeyUser key) {
		OAuthParams makeRequest=HttpAuthUtils.buildAuthenticated(info, urls, key);
		AccessKeyForUser access=HttpAuthUtils.obtainAccessCookie(makeRequest,info.getService());
		if(access==null) return false;
		else {

			return accessKeyManager.saveE(access);

		}
	}
	@Override
	public AccessKeyForUser mostRecentAccessKeyForUserAndService(
			com.ccc.oauth.apimanagement.model.Service toGet, String userFor) {
		return accessKeyManager.mostRecentKeyForUserAndService(userFor, toGet);

	}
	@Override
	public com.ccc.oauth.apimanagement.model.Service serviceWithId(int id) {
		List list =serviceManager.elementsWithValue("id", String.valueOf(id));
		return (com.ccc.oauth.apimanagement.model.Service) ((list!=null && !list.isEmpty()) ?list.get(0) : null);
	}


	public List<com.ccc.oauth.apimanagement.model.Service> allServices() {
		return serviceManager.allElements();
	}

	@Override
	public List<OAuth2KeyUser> keysForUser(String userName) {
		return keysManager.keysForUser(userName);
	}




	@Override
	public OAuth2KeyUser mostRecentKeyForUserAndService(String userName,
			com.ccc.oauth.apimanagement.model.Service service) {
		return keysManager.mostRecentKeyForUserAndService(userName, service);
	}




	@Override
	public boolean deleteKeyForUser(OAuth2KeyUser toDelete) {
		return keysManager.deleteE(toDelete);
	}




	@Override
	public boolean updateKeyForUser(OAuth2KeyUser toUpdate) {
		return keysManager.updateE(toUpdate);
	}




	@Override
	public boolean addKeyForUser(OAuth2KeyUser toAdd) {
		return keysManager.saveE(toAdd);
	}




	@Override
	public List<OAuth2KeyUser> keyForUserAndService(String userName,
			com.ccc.oauth.apimanagement.model.Service service) {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * This will return whether a given auth type is supported 
	 * for a given service
	 * @param authType the auth type
	 * @param service the service to check for
	 * @return true if the auth is supported, false otherwise
	 */
	public boolean authSupported(String authType,com.ccc.oauth.apimanagement.model.Service service) {
		List<AuthSupports> supports=authSupportManager.elementsWithValue("service_id", String.valueOf(service.getId()));
		for(AuthSupports supported : supports) {
			if(supported.getAuthType().equals(authType))
				return true;
		}

		return false;
	}

	public List<AuthSupports> supportedAuthForService(com.ccc.oauth.apimanagement.model.Service service) {
		return authSupportManager.elementsWithValue("service_id", String.valueOf(service.getId()));
	}

	/**
	 * This will return a sorted preferences list for a given service for authentication
	 * @param service the service to get the sorted preferences for
	 * @return the sorted list of auth supported preferences
	 */
	public List<AuthSupports> sortedPreferences(com.ccc.oauth.apimanagement.model.Service service) {
		List<AuthSupports> auths=supportedAuthForService(service);
		Collections.sort(auths);
		return auths;
	}//end sortedPreferences



	public OAuth2AppInfo infoForService(
			com.ccc.oauth.apimanagement.model.Service service) {
		int serviceId=service.getId();
		List<OAuth2AppInfo> info=appInfoManager.elementsWithValue("service_id", String.valueOf(serviceId));
		if(info!=null && !info.isEmpty()) return info.get(0);
		return null;
	}

	public OAuth2Urls urlsForService(
			com.ccc.oauth.apimanagement.model.Service service) {
		int serviceId=service.getId();
		List<OAuth2Urls> urls=urlsManager.elementsWithValue("service_id", String.valueOf(serviceId));
		if(urls!=null && !urls.isEmpty()) return urls.get(0);
		return null;
	}


	public List<Subscribed> servicesForUser(BasicUser user) {
		String userName=user.getUsername();
		List<Subscribed> subscribed=subscribedManager.elementsWithValue("user_name", userName);
		return subscribed;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 6482940588658778475L;
	public boolean removeSubFromUser(BasicUser user, com.ccc.oauth.apimanagement.model.Service service) {
		List<Subscribed> subs=servicesForUser(user);

		for(Subscribed sub : subs) {
			if(sub.getService().equals(service))
				return subscribedManager.deleteE(sub);
		}

		return false;
	}

	public boolean subscribeUser(BasicUser user,  com.ccc.oauth.apimanagement.model.Service service) {
		Subscribed sub = new Subscribed();
		sub.setService(service);
		sub.setUserName(user.getUsername());
		return subscribedManager.saveE(sub);
	}




	public boolean updateAppInfo(OAuth2AppInfo appInfo) {
		return appInfoManager.updateE(appInfo);
	}

	public boolean deleteAppInfo(OAuth2AppInfo appInfo) {
		return appInfoManager.deleteE(appInfo);
	}

	public boolean addAppInfo(OAuth2AppInfo appInfo) {
		return appInfoManager.saveE(appInfo);
	}

	public boolean updateScopeHolder(ScopeHolder holder) {
		return scopeHolderManager.updateE(holder);
	}

	public boolean deleteScopeHolder(ScopeHolder holder) {
		return scopeHolderManager.deleteE(holder);
	}

	public boolean addScopeHolder(ScopeHolder holder) {
		return scopeHolderManager.saveE(holder);
	}

	public boolean deleteService( com.ccc.oauth.apimanagement.model.Service service) {
		return serviceManager.deleteE(service);
	}

	public boolean updateService( com.ccc.oauth.apimanagement.model.Service service) {
		return serviceManager.updateE(service);
	}

	public boolean addService( com.ccc.oauth.apimanagement.model.Service service) {
		return serviceManager.saveE(service);
	}

	public boolean addOAuth2Urls(OAuth2Urls urls) {
		return urlsManager.saveE(urls);
	}

	public boolean updateOAuth2Urls(OAuth2Urls urls) {
		return urlsManager.updateE(urls);
	}

	public boolean deleteOAuth2Urls(OAuth2Urls urls) {
		return urlsManager.deleteE(urls);
	}


	@Override
	public ConsumerDetails loadConsumerByConsumerKey(String key)
			throws OAuthException {
		return null;
	}
	public OAuthParams paramsForApp(String app) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean authorize(OAuthParams params) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasScopeForUser(String service, String userName, String scope) {
		// TODO Auto-generated method stub
		return false;
	}

	public Object getData(OAuthParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean execute(OAuthParams params) {
		// TODO Auto-generated method stub
		return false;
	}

	public ApiManager getApiManager() {
		return apiManager;
	}

	public void setApiManager(ApiManager apiManager) {
		this.apiManager = apiManager;
	}

	public AuthSupportManager getAuthSupportManager() {
		return authSupportManager;
	}

	public void setAuthSupportManager(AuthSupportManager authSupportManager) {
		this.authSupportManager = authSupportManager;
	}

	public OAuth2AppInfoManager getAppInfoManager() {
		return appInfoManager;
	}

	public void setAppInfoManager(OAuth2AppInfoManager appInfoManager) {
		this.appInfoManager = appInfoManager;
	}

	public OAuth2UrlsManager getUrlsManager() {
		return urlsManager;
	}

	public void setUrlsManager(OAuth2UrlsManager urlsManager) {
		this.urlsManager = urlsManager;
	}

	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	public void setServiceManager(ServiceManager serviceManager) {
		this.serviceManager = serviceManager;
	}

	public ServiceProviderManager getServiceProviderManager() {
		return serviceProviderManager;
	}

	public void setServiceProviderManager(
			ServiceProviderManager serviceProviderManager) {
		this.serviceProviderManager = serviceProviderManager;
	}

	public SubscribedManager getSubscribedManager() {
		return subscribedManager;
	}

	public void setSubscribedManager(SubscribedManager subscribedManager) {
		this.subscribedManager = subscribedManager;
	}

	public ScopeServiceManager getScopeServiceManager() {
		return scopeServiceManager;
	}

	public void setScopeServiceManager(ScopeServiceManager scopeServiceManager) {
		this.scopeServiceManager = scopeServiceManager;
	}

	public ScopeHolderManager getScopeHolderManager() {
		return scopeHolderManager;
	}

	public void setScopeHolderManager(ScopeHolderManager scopeHolderManager) {
		this.scopeHolderManager = scopeHolderManager;
	}

	public OAuth2KeysManager getKeysManager() {
		return keysManager;
	}

	public void setKeysManager(OAuth2KeysManager keysManager) {
		this.keysManager = keysManager;
	}


	public AccessKeyManager getAccessKeyManager() {
		return accessKeyManager;
	}
	public void setAccessKeyManager(AccessKeyManager accessKeyManager) {
		this.accessKeyManager = accessKeyManager;
	}


	public OAuth1AccessTokenManager getOauth1AccessTokenManager() {
		return oauth1AccessTokenManager;
	}
	public void setOauth1AccessTokenManager(
			OAuth1AccessTokenManager oauth1AccessTokenManager) {
		this.oauth1AccessTokenManager = oauth1AccessTokenManager;
	}
	public OAuth1RequestTokenManager getOauthRequestTokenManager() {
		return oauthRequestTokenManager;
	}
	public void setOauthRequestTokenManager(
			OAuth1RequestTokenManager oauthRequestTokenManager) {
		this.oauthRequestTokenManager = oauthRequestTokenManager;
	}


	public OAuth1InfoManager getOauth1InfoManager() {
		return oauth1InfoManager;
	}
	public void setOauth1InfoManager(OAuth1InfoManager oauth1InfoManager) {
		this.oauth1InfoManager = oauth1InfoManager;
	}


	public UserClient getUserClient() {
		return userClient;
	}





	public void setUserClient(UserClient userClient) {
		this.userClient = userClient;
	}
	@Autowired(required=false)
	private ApiManager apiManager;
	@Autowired(required=false)
	private AuthSupportManager authSupportManager;
	@Autowired(required=false)

	private OAuth2AppInfoManager appInfoManager;
	@Autowired(required=false)

	private OAuth2UrlsManager urlsManager;
	@Autowired(required=false)

	private ServiceManager serviceManager;
	@Autowired(required=false)

	private ServiceProviderManager serviceProviderManager;
	@Autowired(required=false)

	private SubscribedManager subscribedManager;
	@Autowired(required=false)

	private ScopeServiceManager scopeServiceManager;
	@Autowired(required=false)
	private ScopeHolderManager scopeHolderManager;
	@Autowired(required=false)
	private OAuth2KeysManager keysManager;
	@Autowired(required=false)
	private AccessKeyManager accessKeyManager;
	@Autowired(required=false)
	private OAuth1AccessTokenManager oauth1AccessTokenManager;
	@Autowired(required=false)
	private OAuth1RequestTokenManager oauthRequestTokenManager;
	@Autowired(required=false)
	private OAuth1InfoManager oauth1InfoManager;

	@Autowired(required=false)
	private UserClient userClient;
	private static Logger log=LoggerFactory.getLogger(OAuth2ServiceImpl.class);
	@Override
	public OAuthProviderToken getToken(String token)
			throws AuthenticationException {
		return super.getToken(token);
	}





	@Override
	public OAuthProviderToken createUnauthorizedRequestToken(
			String consumerKey, String callbackUrl)
			throws AuthenticationException {
		return super.createUnauthorizedRequestToken(consumerKey, callbackUrl);
	}





	@Override
	public void authorizeRequestToken(String requestToken, String verifier,
			Authentication authentication) throws AuthenticationException {
		 super.authorizeRequestToken(requestToken, verifier, authentication);
	}





	@Override
	public OAuthAccessProviderToken createAccessToken(String requestToken)
			throws AuthenticationException {
		return super.createAccessToken(requestToken);
	}





	@Override
	protected OAuthProviderTokenImpl readToken(String token) {
		return null;
	}





	@Override
	protected void storeToken(String tokenValue, OAuthProviderTokenImpl token) {
		String userName=token.getUserAuthentication().getName();
		OAuthProviderTokenImplWithService token2=(OAuthProviderTokenImplWithService) token;
		com.ccc.oauth.apimanagement.model.Service service=token2.getService();
		if(token.isAccessToken()) {
			AccessKeyForUser access= new AccessKeyForUser();
			access.setService(service);
			access.setUserName(userName);
			access.setObtained(new Timestamp(token2.getTimestamp()));
			access.setAccessCode(token.getValue());
			addKeyForUser(access);
		}
		else {
			OAuth2KeyUser key = new OAuth2KeyUser();
			key.setCode(token2.getValue());
			key.setUserName(userName);
			key.setTimeMade(new Timestamp(token.getTimestamp()));
			key.setService(service);
			addKeyForUser(key);
		}
	}





	@Override
	protected OAuthProviderTokenImpl removeToken(String tokenValue) {
		
		return null;
	}


}
