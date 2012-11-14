package com.ccc.oauth.api;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.oauth.provider.ConsumerDetailsService;
import org.springframework.security.oauth.provider.token.OAuthProviderTokenServices;
import com.ccc.users.core.BasicUser;
import com.ccc.oauth.amber.oauth2.model.OAuthParams;
import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.AuthSupports;
import com.ccc.oauth.apimanagement.model.OAuth1AccessToken;
import com.ccc.oauth.apimanagement.model.OAuth1Info;
import com.ccc.oauth.apimanagement.model.OAuth1RequestToken;
import com.ccc.oauth.apimanagement.model.OAuth2AppInfo;
import com.ccc.oauth.apimanagement.model.OAuth2KeyUser;
import com.ccc.oauth.apimanagement.model.OAuth2Urls;
import com.ccc.oauth.apimanagement.model.ScopeHolder;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.apimanagement.model.Subscribed;
/**
 * This covers persistence of oauth2 related models
 * @author Adam Gibson
 *
 */
public interface OAuth2Service extends Serializable,ConsumerDetailsService,OAuthProviderTokenServices {

	
	/**
	 * This is able to handle a response from any oauth1 or oauth2 provider.
	 * It takes the request and processes the proper access tokens/and other information
	 * and persists the needed information for the user needed for lookup later in 
	 * different types of oauth requests.
	 * Think of this as the "end step" for the oauth dance.
	 * @param request the http request this came from
	 * @param response the response to be sent
	 * @param token an oauth 2 code for upgrading to an access token
	 * @param error an error message sent back from the oauth request
	 * @param errorMessage the message sent back for the error
	 * @param oauth1Token an oauth1 token needed to upgrade for authentication
	 * @param oauthVerifier the standard for 1.0a authentication, this serves the same role as the oauth1 token in later editions of oauth
	 * @param s the service this authorization is for
	 * @param userName the user name this authorization is for
	 * @return true if the processing was a success, false otherwise
	 */
	public boolean processRedirect(HttpServletRequest request,HttpServletResponse response,String token,String error,String errorMessage,String oauth1Token,String oauthVerifier,com.ccc.oauth.apimanagement.model.Service s,String userName);
	/**
	 * This will return the oauth1 info for the given service
	 * @param service the oauth1 info for the given service
	 * @return the oauth1 info for the given service
	 */
	public OAuth1Info oauthInfoForService(Service service);
	
	/** This will delete the given key
	 * @param toDelete the key to delete
	 * @return true if the key was deleted, false otherwise
	 */
	public boolean deleteRequestToken(OAuth1RequestToken toDelete);

	/**
	 * This will update the given key
	 * @param toDelete the key to update
	 * @return true if the key was update, false otherwise
	 */
	public boolean updateRequestToken(OAuth1RequestToken toUpdate);
	/**
	 * This will add the given key
	 * @param toAdd the key to delete
	 * @return true if the key was added, false otherwise
	 */
	public boolean addRequestToken(OAuth1RequestToken toAdd);
	
	
	
	
	/** This will delete the given key
	 * @param toDelete the key to delete
	 * @return true if the key was deleted, false otherwise
	 */
	public boolean deleteAccessToken(OAuth1AccessToken toDelete);

	/**
	 * This will update the given key
	 * @param toDelete the key to update
	 * @return true if the key was update, false otherwise
	 */
	public boolean updateAccessToken(OAuth1AccessToken toUpdate);
	/**
	 * This will add the given key
	 * @param toAdd the key to delete
	 * @return true if the key was added, false otherwise
	 */
	public boolean addAccessToken(OAuth1AccessToken toAdd);
	
	
	/**
	 * This will return the oauth 1 request token for the given user and service.
	 * @param service the service to retrieve the token for
	 * @param userName the user to retrieve the toke nfor
	 * @return  the request tokens for the given user and service
	 */
	public OAuth1AccessToken mostRecentOauthAccessTokenForUserAndService(Service service, String userName);
	
	
	/**
	 * This will return the oauth 1 request token for the given user and service.
	 * @param service the service to retrieve the token for
	 * @param userName the user to retrieve the toke nfor
	 * @return  the request tokens for the given user and service
	 */
	public OAuth1RequestToken mostRecentOauthRequestTokenForUserAndService(Service service, String userName);
	
	/**
	 * This will return the oauth 1 request token for the given user and service.
	 * @param service the service to retrieve the token for
	 * @param userName the user to retrieve the toke nfor
	 * @return  the request tokens for the given user and service
	 */
	public List<OAuth1RequestToken> oauthRequestTokenForUserAndService(Service service, String userName);
	
	/** This will delete the given key
	 * @param toDelete the key to delete
	 * @return true if the key was deleted, false otherwise
	 */
	public boolean deleteKeyForUser(AccessKeyForUser toDelete);

	/**
	 * This will update the given key
	 * @param toDelete the key to update
	 * @return true if the key was update, false otherwise
	 */
	public boolean updateKeyForUser(AccessKeyForUser toUpdate);
	/**
	 * This will add the given key
	 * @param toAdd the key to delete
	 * @return true if the key was added, false otherwise
	 */
	public boolean addKeyForUser(AccessKeyForUser toAdd);
	
	/**
	 * This will return the most recent access key for a given user and service
	 * @param toGet the service to get a key for
	 * @param userFor the user get the key for
	 * @return null if it doexn't exist, or the most recent key for the given user and service
	 */
	public AccessKeyForUser mostRecentAccessKeyForUserAndService(Service toGet,String userFor);
	
	/**
	 * This will execute a key access request and save it
	 * @param urls the urls to use
	 * @param info the info to use
	 * @param key the key to request auth with
	 * @return true if the access token was saved, false otherwise
	 */
	public boolean saveAccessToken(OAuth2Urls urls,OAuth2AppInfo info,OAuth2KeyUser key);
	
	/**
	 * This will return the service with the given id
	 * @param id the id to look up
	 * @return the service with the given id
	 */
	public Service serviceWithId(int id);
	
	
	/**
	 * This will return all of the keys for a given user
	 * @param userName the user to search for
	 * @return all of the keys for a given user or null if none exists
	 */
	public List<OAuth2KeyUser> keysForUser(String userName);
	
	/**
	 * This will return the most recent key for a user and service
	 * @param userName the user to esearch for
	 * @param service the service to get a key for
	 * @return the most recent key for the user and serviec, or null if none exists
	 */
	public OAuth2KeyUser mostRecentKeyForUserAndService(String userName,Service service);
	 
	/** This will delete the given key
	 * @param toDelete the key to delete
	 * @return true if the key was deleted, false otherwise
	 */
	public boolean deleteKeyForUser(OAuth2KeyUser toDelete);

	/**
	 * This will update the given key
	 * @param toDelete the key to update
	 * @return true if the key was update, false otherwise
	 */
	public boolean updateKeyForUser(OAuth2KeyUser toUpdate);
	/**
	 * This will add the given key
	 * @param toAdd the key to delete
	 * @return true if the key was added, false otherwise
	 */
	public boolean addKeyForUser(OAuth2KeyUser toAdd);

	/**
	 * This will return the keys for the given user and service
	 * @param userName the user to search for
	 * @param service the service to get keys for
	 * @return the list of oauth2 keys for the given user and service
	 */
	public List<OAuth2KeyUser> keyForUserAndService(String userName,Service service);

	/**
	 * This will return all the services in the database
	 * @return the list of all services
	 */
	public List<Service> allServices();

	/**
	 * This will return whether a given auth type is supported 
	 * for a given service
	 * @param authType the auth type
	 * @param service the service to check for
	 * @return true if the auth is supported, false otherwise
	 */
	public boolean authSupported(String authType,com.ccc.oauth.apimanagement.model.Service service);



	/**
	 * This will return a sorted preferences list for a given service for authentication
	 * @param service the service to get the sorted preferences for
	 * @return the sorted list of auth supported preferences
	 */
	public List<AuthSupports> sortedPreferences(com.ccc.oauth.apimanagement.model.Service service);

	/**
	 * This will return the supported authorization types for a given service
	 * @param service the service to get auth types for
	 * @return the list of supported authoirzation types for a given service
	 */
	public List<AuthSupports> supportedAuthForService(com.ccc.oauth.apimanagement.model.Service service);

	/**
	 * This will return the necessary  oauth 2 info for a given service
	 * 
	 * @param service the service to get info for
	 * @return null if none exists, or a unique app info for service
	 */
	public OAuth2AppInfo infoForService(Service service);

	/**
	 * This will get the oauth2 urls needed for a service
	 * @param service the service to get the urls for
	 * @return null if none exsits, or a unique urls object
	 * containing needed urls for oauth2 for a service
	 */
	public OAuth2Urls urlsForService(Service service);

	/**
	 * Removes a subscription for  the given user to the given service
	 * @param user the user to subscribe
	 * @param service the service to subscribe the user to
	 * @return true if the user was unsubscribed, false otherwise
	 */
	public boolean removeSubFromUser(BasicUser user,Service service);

	/**
	 * Subscribe the given user to the given service
	 * @param user the user to subscribe
	 * @param service the service to subscribe the user to
	 * @return true if the user was subscribed, false otherwise
	 */
	public boolean subscribeUser(BasicUser user,Service service);


	/**
	 * This will return a list of services a user is subscribed to
	 * @param user the user to check for
	 * @return the list of subscribed data for a user
	 */
	public List<Subscribed> servicesForUser(BasicUser user);

	/**
	 * Updates given  app info
	 * @param appInfo the app info to add
	 * @return true if the app info was added, false otherwise
	 */
	public boolean updateAppInfo(OAuth2AppInfo appInfo);

	/**
	 * Add new app info
	 * @param appInfo the app info to delete
	 * @return true if the app info was delete, false otherwise
	 */
	public boolean deleteAppInfo(OAuth2AppInfo appInfo);

	/**
	 * Add new app info
	 * @param appInfo the app info to add
	 * @return true if the app info was added, false otherwise
	 */
	public boolean addAppInfo(OAuth2AppInfo appInfo);

	/**
	 * Updates a scope holder
	 * @param holder the holder to add
	 * @return true if the holder was updated, false otherwise
	 */
	public boolean updateScopeHolder(ScopeHolder holder);
	/**
	 * Adds a scope holder
	 * @param holder the holder to add
	 * @return true if the holder was deleted, false otherwise
	 */
	public boolean deleteScopeHolder(ScopeHolder holder);

	/**
	 * Adds a scope holder
	 * @param holder the holder to add
	 * @return true if the holder was added, false otherwise
	 */
	public boolean addScopeHolder(ScopeHolder holder);

	/**
	 * Delete a new service
	 * @param service the service to add
	 * @return true if the service was added, false otherwise
	 */
	public boolean deleteService(Service service);

	/**
	 * Adds a new service
	 * @param service the service to add
	 * @return true if the service was added, false otherwise
	 */
	public boolean updateService(Service service);

	/**
	 * Adds a new service
	 * @param service the service to add
	 * @return true if the service was added, false otherwise
	 */
	public boolean addService(Service service);

	/**
	 * Persist new oauth 2 urls
	 * @param urls the urls to add
	 * @return true if the urls were added, false otherwise
	 */
	public boolean addOAuth2Urls(OAuth2Urls urls);


	/**
	 * Update current oauth 2 urls
	 * @param urls the urls to add
	 * @return true if the urls were added, false otherwise
	 */
	public boolean updateOAuth2Urls(OAuth2Urls urls);


	/**
	 * Delete new oauth 2 urls
	 * @param urls the urls to add
	 * @return true if the urls were added, false otherwise
	 */
	public boolean deleteOAuth2Urls(OAuth2Urls urls);


	/**
	 * This will return the oauth parameters needed for a given app
	 * @param app the app to get parameters for
	 * @return the oauth params for the given app
	 */
	public OAuthParams paramsForApp(String app);


	/**
	 * This will attempt to authorize the given parameters
	 * @param params the parameters to authorize
	 */
	public boolean authorize(OAuthParams params);

	/**
	 * This will return whether the given user has the given scope for the given service
	 * @param service the service to check for
	 * @param userName the user name to check for
	 * @param scope the scope to check for
	 * @return true if the given user has that scope already for the given service,
	 * false otherwise
	 */
	public boolean hasScopeForUser(String service,String userName,String scope);

	/**
	 * This will attempt to get data from an oauth resource 
	 * using the specified parameters
	 * @param params the parameters to get data from
	 * @return the data if possible, or null if an error occurs/nothing found
	 */
	public Object getData(OAuthParams params);

	/**
	 * This will attempt to execute an oauth request using
	 * the given parameters
	 * @param params the parameters to use
	 * @return true if the request was executed, false otherwise
	 */
	public boolean execute(OAuthParams params);

}
