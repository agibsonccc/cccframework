package com.ccc.oauth.util;

import java.io.IOException;
import java.net.URI;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpParameters;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.GitHubTokenResponse;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.ProtocolException;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.NTLMSchemeFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.ccc.oauth.amber.oauth2.model.OAuthParams;
import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.OAuth1AccessToken;
import com.ccc.oauth.apimanagement.model.OAuth1Info;
import com.ccc.oauth.apimanagement.model.OAuth1RequestToken;
import com.ccc.oauth.apimanagement.model.OAuth2AppInfo;
import com.ccc.oauth.apimanagement.model.OAuth2KeyUser;
import com.ccc.oauth.apimanagement.model.OAuth2Urls;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.apimanagement.model.ServiceInfo;
import com.ccc.oauth.apimanagement.model.Subscribed;
import com.ccc.oauth.http.RedirectingHttpClient4;
import com.ccc.oauth.http.UTF8OAuthClient;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.client.UserClient;
public class HttpAuthUtils {
	/**
	 * This will return a list of information for all of the services in the database
	 * @param oauthService the oauth service used to load the services
	 * @return the list of service info for all of the services,or null if none exists
	 */
	public static List<ServiceInfo> infoForServices(OAuth2Service oauthService) {
		return infoForServices(oauthService.allServices(),oauthService);
	}
	/**
	 * This will return a list of all of the given services for the given user name
	 * @param userName the user name to look up services for
	 * @param oauthService the oauth service to use to look up services
	 * @param userClient the user client to load the user
	 * @return the list of service info for the given user or null if none exists
	 */
	public static List<ServiceInfo> infoForServices(String userName,OAuth2Service oauthService,UserClient userClient) {
		BasicUser user=userClient.userForName(userName);
		if(user==null) return null;
		List<Subscribed> subscribed=oauthService.servicesForUser(user);
		
		List<Service> build = new ArrayList<Service>();
		
		for(Subscribed sub : subscribed) {
			build.add(sub.getService());
		}
		return infoForServices(build,oauthService);
	}
	/**
	 * This will return a list of all of the service information for the past in list of services
	 * @param services the services to get info for
	 * @param oauthService the oauth service to use to load information
	 * @return the list of information for the given services
	 */
	public static List<ServiceInfo> infoForServices(List<Service> services,OAuth2Service oauthService) {
		if(services==null || services.isEmpty()) return null;
		
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();

		List<ServiceInfo> info = new ArrayList<ServiceInfo>();

		for(Service service : services) {


			boolean isOauth2=oauthService.authSupported(HttpAuthUtils.OAUTH2, service);
			boolean isOAuth1=oauthService.authSupported(HttpAuthUtils.OAUTH, service);
			if(isOauth2) {
				String serviceName=service.getName();

				OAuth2AppInfo serviceInfo=oauthService.infoForService(service);
				OAuth2Urls urlInfo=oauthService.urlsForService(service);
				if(serviceInfo==null) continue;
				if(urlInfo==null) continue;

				String authUrl=null;
				try {
					authUrl = HttpAuthUtils.buildAuthUri(serviceInfo, urlInfo);
					ServiceInfo add = new ServiceInfo(service.getId(),serviceName,authUrl);
					info.add(add);
				} catch (OAuthSystemException e) {
					e.printStackTrace();

				}
			}
			else if(isOAuth1) {
				String serviceName=service.getName();
				OAuth1Info oauthInfo=oauthService.oauthInfoForService(service);
				if(oauthInfo==null) continue;
				String url=HttpAuthUtils.requestTokenUrlForInfo(oauthInfo,oauthService,userName);


				ServiceInfo add = new ServiceInfo(service.getId(),serviceName,url);
				info.add(add);
			}
		}
		return info;
	}

	/**
	 * This will build a data uri from the target service for the target user.
	 * This will use the passed in oauth2 service to do a look up on the most recent access key for the given user
	 * @param service the oauth2 service to use to look up
	 * @param userName the name of the user to get the access code for
	 * @param targetService the target service to do a look up on the access code
	 * @return the proper data uri for the given service with the authenticated access code. Note that this method makes no assumptions
	 * about whether the return access code is valid
	 */
	public static String buildBaseDataUri(OAuth2Service service,String userName,Service targetService,boolean httpClient4) {
		AccessKeyForUser key=service.mostRecentAccessKeyForUserAndService(targetService, userName);
		Assert.notNull(key, "No key was found for user " + userName + " and service: " +  targetService.getName());
		OAuth2Urls urls=service.urlsForService(targetService);
		Assert.notNull(urls,"No urls found for service; " + targetService.getName());
		String url= new StringBuilder().append(urls.getDataUrl()).toString();
		if(httpClient4) {
			if(url.contains("https") && !url.contains("https4"))
				url=url.replace("https","https4");
			else if(!url.contains("http4")) url=url.replace("http","http4");
			return url;
		}
		return url;
	}//end buildDataUri


	/**
	 * This will build a data uri from the target service for the target user.
	 * This will use the passed in oauth2 service to do a look up on the most recent access key for the given user
	 * @param service the oauth2 service to use to look up
	 * @param userName the name of the user to get the access code for
	 * @param targetService the target service to do a look up on the access code
	 * @return the proper data uri for the given service with the authenticated access code. Note that this method makes no assumptions
	 * about whether the return access code is valid
	 */
	public static String buildDataUri(OAuth2Service service,String userName,Service targetService,boolean httpClient4) {
		AccessKeyForUser key=service.mostRecentAccessKeyForUserAndService(targetService, userName);
		Assert.notNull(key, "No key was found for user " + userName + " and service: " +  targetService.getName());
		OAuth2Urls urls=service.urlsForService(targetService);
		Assert.notNull(urls,"No urls found for service; " + targetService.getName());
		String url= new StringBuilder().append(urls.getDataUrl()).append("?" + OAUTH2_ACCESS_TOKEN + "=").append(key.getAccessCode()).toString();
		if(httpClient4) {
			if(url.contains("https") && !url.contains("https4"))
				url=url.replace("https","https4");
			else if(!url.contains("http4")) url=url.replace("http","http4");
			return url;
		}
		return url;
	}//end buildDataUri
	/**
	 * This will set the given http client to always redirect and return it
	 * @param client the client to set
	 * 
	 * @return an http client that will always follow redirects on a 301/302
	 */
	public static DefaultHttpClient setAlwaysRedirect(DefaultHttpClient client) {
		client.setRedirectStrategy(new DefaultRedirectStrategy() {                
			public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {
				boolean isRedirect=false;
				try {
					isRedirect = super.isRedirected(request, response, context);
				} catch (ProtocolException e) {
					e.printStackTrace();
				}
				if (!isRedirect) {
					int responseCode = response.getStatusLine().getStatusCode();
					if (responseCode == 301 || responseCode == 302) {
						return true;
					}
				}
				return isRedirect;
			}
		});
		return client;
	}//end setAlwaysRedirect


	/**
	 * This will return an http client logged in to the given page
	 * @param targetSite the target site to login to
	 * @param params the params including form params: USER_NAME_FORM_PARAM,PASSWORD_FORM_PARAM,USER_NAME,PASSWORD
	 * @param alwaysFollowRedirect whether to always follow redirects
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 */
	public static HttpClient login(String targetSite,Map<String,String> params,boolean alwaysFollowRedirects) throws ParseException, IOException {
		String userNameFormParam=params.get(HttpAuthUtils.USERNAME_FORM_PARAM);
		String passwordFormParam=params.get(PASSWORD_FORM_PARAM);
		String userName=params.get(USER_NAME);
		String password=params.get(PASSWORD);
		String[] necessary={userNameFormParam,passwordFormParam,userName,password};
		for(String s : necessary) Assert.notNull(s,"Missing param! for http client login!");
		DefaultHttpClient httpclient = new DefaultHttpClient();
		if(alwaysFollowRedirects) httpclient=HttpAuthUtils.setAlwaysRedirect(httpclient);
		HttpGet httpget = new HttpGet(targetSite);

		HttpResponse response = httpclient.execute(httpget);
		HttpEntity entity = response.getEntity();

		System.out.println("Login form get: " + response.getStatusLine());
		if (entity != null) {
			EntityUtils.consume(entity);
		}
		System.out.println("Initial set of cookies:");
		List<org.apache.http.cookie.Cookie> cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("- " + cookies.get(i).toString());
			}
		}

		HttpPost httpost = new HttpPost(targetSite);
		List <NameValuePair> nvps = new ArrayList <NameValuePair>();
		nvps.add(new BasicNameValuePair(userNameFormParam, userName));
		nvps.add(new BasicNameValuePair(passwordFormParam, password));

		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

		response = httpclient.execute(httpost);
		entity = response.getEntity();
		System.out.println("Double check we've got right page " + EntityUtils.toString(entity));

		System.out.println("Login form get: " + response.getStatusLine());
		if (entity != null) {
			EntityUtils.consume(entity);
		}

		System.out.println("Post logon cookies:");
		cookies = httpclient.getCookieStore().getCookies();
		if (cookies.isEmpty()) {
			System.out.println("None");
		} else {
			for (int i = 0; i < cookies.size(); i++) {
				System.out.println("- " + cookies.get(i).toString());
			}
		}

		return httpclient;
	}//end login



	/**
	 * This will perform an oauth 2 authentication using the given oauth service for the given user
	 * and the given service id
	 * @param service the id of the service to request access for
	 * @param userName the name of the user to get oauth info for
	 * @param serviceId the id of the service to use
	 * @return an access key that was returned
	 */
	public static AccessKeyForUser requestAccessWithLookup(OAuth2Service service,String userName,int serviceId) {
		Service lookup=service.serviceWithId(serviceId);
		OAuth2AppInfo infoForService=service.infoForService(lookup);
		OAuth2Urls urlsForService=service.urlsForService(lookup);
		OAuth2KeyUser key=service.mostRecentKeyForUserAndService(userName, lookup);
		OAuthParams authorized=buildAuthenticated(infoForService,urlsForService,key);


		AccessKeyForUser accessToken=obtainAccessCookie(authorized,lookup);
		accessToken.setUserName(userName);
		return accessToken;
	}//end serviceId

	/**
	 * This will obtain an access cookie for the given parameters and the given service
	 * @param params the parameters to use
	 * @param service the service to get an access cookie for
	 * @return the access key for the given parameterss and service
	 */
	public static AccessKeyForUser obtainAccessCookie(OAuthParams params,Service service) {
		Assert.notNull(params,"Params must not be null");
		AccessKeyForUser ret = new AccessKeyForUser();
		OAuthClientRequest request = null;
		try {
			request = getAccess(params);

		} catch (OAuthSystemException e) {
			e.printStackTrace();
		}
		UTF8OAuthClient client = new UTF8OAuthClient(new RedirectingHttpClient4());

		// String app = Utils.findCookieValue(req, "app");

		OAuthAccessTokenResponse oauthResponse = null;
		Class<? extends OAuthAccessTokenResponse> cl = OAuthJSONAccessTokenResponse.class;

		if (service.getName().contains("facebook")) {
			cl = GitHubTokenResponse.class;
		} else if (service.getName().contains("github")) {
			cl = GitHubTokenResponse.class;
		}

		try {
			oauthResponse = client.accessToken(request,cl);

		} catch (OAuthSystemException e) {
			e.printStackTrace();
			return null;
		} catch (OAuthProblemException e) {
			e.printStackTrace();
			return null;
		}

		params.setAccessToken(isIssued(oauthResponse.getAccessToken()));
		params.setExpiresIn(isIssued(String.valueOf(oauthResponse.getExpiresIn())));
		params.setRefreshToken(isIssued(oauthResponse.getRefreshToken()));
		String accessToken=oauthResponse.getAccessToken();
		
		ret.setObtained(new Timestamp(System.currentTimeMillis()));
		ret.setRefreshToken(isIssued(oauthResponse.getRefreshToken()));
		ret.setExpiresIn(isIssued(oauthResponse.getRefreshToken()));
		ret.setService(service);
		ret.setAccessCode(accessToken);

		return ret;
	}//end obtainAccessCookie


	/**
	 * This will build an oauth client request from the given parameters
	 * @param params the parameters to build from
	 * @return an oauth client request from the given parameters
	 * @throws OAuthSystemException
	 */
	public static OAuthClientRequest offlineAccess(OAuthParams params) throws OAuthSystemException {
		OAuthClientRequest request = OAuthClientRequest
				.authorizationLocation(params.getAuthzEndpoint())
				.setClientId(params.getClientId()).setResponseType("code").setParameter("access+type", "offline")
				.setRedirectURI(params.getRedirectUri()).setScope(params.getScope())
				.buildQueryMessage();
		return request;

	}//end fromParams

	/**
	 * This will build an oauth client request from the given parameters
	 * @param params the parameters to build from
	 * @return an oauth client request from the given parameters
	 * @throws OAuthSystemException
	 */
	public static OAuthClientRequest fromParams(OAuthParams params) throws OAuthSystemException {
		OAuthClientRequest request = OAuthClientRequest
				.authorizationLocation(params.getAuthzEndpoint())
				.setClientId(params.getClientId()).setResponseType("code")
				.setRedirectURI(params.getRedirectUri()).setScope(params.getScope())
				.buildQueryMessage();
		return request;

	}//end fromParams

	/**
	 * This will return an oauth consumer based on the passed in info
	 * @param info the info to use
	 * @return an oauth consumer based on the given info
	 */
	public static oauth.signpost.OAuthConsumer consumerFromInfo(OAuth1Info info) {
		oauth.signpost.OAuthConsumer ret = new  CommonsHttpOAuthConsumer(info.getConsumerKey(),info.getConsumerSecret());

		return ret;
	}//end OAuthConsumer
	/**
	 * This will get an oauth from the given info
	 * @param info the info to use
	 * @return an oauth provider based on the given info
	 */
	public static OAuthProvider providerFromInfo(OAuth1Info info) {
		//oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
		return new CommonsHttpOAuthProvider(info.getRequestTokenUrl(),info.getAccessTokenUrl(),info.getAuthzUrl());

	}//end providerFromInfo
	/**
	 * This will send out a token request and return an initialized consumer
	 * @param info the info to use
	 * @return the consumer with the token and secret
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public static oauth.signpost.OAuthConsumer getConsumerWithRequest(OAuth1Info info) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
		OAuthProvider provider=providerFromInfo(info);
		provider.retrieveRequestToken(consumer, info.getCallback());
		return consumer;

	}

	/**
	 * This will build an oauth1 request burl based on the url and return a request token
	 * @param info the info to use for retrieving a token
	 * @return the token to use for oauth requests
	 */
	public static String requestTokenUrlForInfo(OAuth1Info info) {
		oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
		OAuthProvider provider=providerFromInfo(info);
		try {
			String requestToken=provider.retrieveRequestToken(consumer, info.getCallback());

			return requestToken;
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}//end requestTokenUrlForInfo

	/**
	 * This will save a new oauth 1 request token.
	 * @param service the service to use to save the request token
	 * @param info the auth info to use
	 * @param userName the name of the user to save a request token for
	 */
	public static void saveNewRequestToken(OAuth2Service service,OAuth1Info info,String userName) {
		String requestToken=requestTokenUrlForInfo(info);
		OAuth1RequestToken token= new OAuth1RequestToken();
		token.setService(info.getService());
		token.setUserName(userName);
		token.setConsumerToken(info.getConsumerKey());
		token.setTimeReceived(new Timestamp(System.currentTimeMillis()));
		token.setConsumerSecret(info.getConsumerSecret());
		token.setRequestToken(requestToken);
		Assert.isTrue(service.addRequestToken(token),"Couldn't save new request token");
	}
	

	/**
	 * This will build an oauth1 request burl based on the url and return a request token.
	 * Given a service, this will also saved the corresponding request token and token secret
	 * @param info the info to use for retrieving a token
	 * @return the token to use for oauth requests
	 */
	public static String requestTokenUrlForInfo(OAuth1Info info,OAuth2Service service,String userName) {
		oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
		OAuthProvider provider=providerFromInfo(info);
		try {
			String requestToken=provider.retrieveRequestToken(consumer, info.getCallback());
			OAuth1RequestToken save = new OAuth1RequestToken();
			save.setConsumerSecret(consumer.getConsumerSecret());
			save.setConsumerToken(consumer.getConsumerKey());
			save.setService(info.getService());
			save.setUserName(userName);
			save.setTimeReceived(new Timestamp(System.currentTimeMillis()));
			service.addRequestToken(save);
			ConsumerHolder.putConsumerForUserAndService(userName, info.getService(), consumer, provider);
			return requestToken;
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthNotAuthorizedException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return null;
	}//end requestTokenUrlForInfo

	/**
	 * This will return a consumer that is able sign requests for data in oauth 1.
	 * Note that the access tokens in the database must not be out of date for this to work
	 * @param service the service to get the consumer for
	 * @param userName the name of the user to get the consumer for
	 * @param oauth2Service an oauth service to do look ups
	 * @return an authenticated consumer used to sign oauth requests
	 */
	public static oauth.signpost.OAuthConsumer getRequestingConsumer(Service service,String userName,OAuth2Service oauth2Service) {
		OAuth1Info info=oauth2Service.oauthInfoForService(service);
		oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
		OAuth1AccessToken access=oauth2Service.mostRecentOauthAccessTokenForUserAndService(service, userName);
		consumer.setTokenWithSecret(access.getAccessToken(), access.getTokenSecret());
		return consumer;
	}//end getRequestingConsumer

	/**
	 * This will retrieve the given access token from the given pin
	 * @param pin the pin to get access by
	 * @param info the info to use to form the request 
	 * @return the oauth token from the request
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public static String getAccessTokenFromPin(OAuth1Info info,OAuth2Service service,OAuth1RequestToken requestToken) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		ConsumerProvider pair=ConsumerHolder.consumerForServiceAndUser(requestToken.getUserName(), requestToken.getService());
		Assert.notNull(pair,"No consumer/provider found");

		OAuthProvider provider=pair.getProvider();
		oauth.signpost.OAuthConsumer consumer=pair.getConsumer();

		//String token=provider.retrieveRequestToken(consumer, info.getCallback());
		log.info("Requesting token..");
		provider.retrieveAccessToken(consumer, requestToken.getRequestToken());
		OAuth1AccessToken access = new OAuth1AccessToken();
		HttpParameters response=provider.getResponseParameters();
		access.setService(requestToken.getService());
		access.setUserName(requestToken.getUserName());
		access.setAccessToken(consumer.getToken());
		access.setTokenSecret(consumer.getTokenSecret());
		access.setTimeReceived(new Timestamp(System.currentTimeMillis()));
		service.addAccessToken(access);
		return access.getAccessToken();
	}//end getAccessTokenFromPin


	/**
	 * This will retrieve the given access token from the given pin
	 * @param pin the pin to get access by
	 * @param info the info to use to form the request 
	 * @param token the request token from a web request, this is needed in order to retrieve an access token
	 * @return the oauth token from the request
	 * 
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public static oauth.signpost.OAuthConsumer getAuthedConsumer(OAuth1Info info,OAuth1RequestToken token) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {
		OAuthProvider provider=providerFromInfo(info);
		oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
		consumer.setTokenWithSecret(token.getConsumerToken(), token.getConsumerSecret());
		provider.retrieveAccessToken(consumer, token.getRequestToken());

		return consumer;
	}//end getAccessTokenFromPin

	/**
	 * This will retrieve the given access token from the given pin
	 * @param pin the pin to get acccess by
	 * @param info the info to use to form the request 
	 * @param token the request token from a web request, this is needed in order to retrieve an access token
	 * @return the oauth token from the request
	 * 
	 * @throws OAuthMessageSignerException
	 * @throws OAuthNotAuthorizedException
	 * @throws OAuthExpectationFailedException
	 * @throws OAuthCommunicationException
	 */
	public static oauth.signpost.OAuthConsumer getAuthedConsumer(OAuth2Service oauth2Service,String userFor,Service service) throws OAuthMessageSignerException, OAuthNotAuthorizedException, OAuthExpectationFailedException, OAuthCommunicationException {

		OAuth1Info info=oauth2Service.oauthInfoForService(service);
		OAuth1RequestToken token=oauth2Service.mostRecentOauthRequestTokenForUserAndService(service, userFor);
		Assert.notNull(token);

		if(token.getConsumerToken()==null || token.getConsumerSecret()==null) {
			throw new IllegalStateException("Consumer token or secret was null");

		}
		else {
			oauth.signpost.OAuthConsumer consumer=consumerFromInfo(info);
			consumer.setTokenWithSecret(token.getConsumerToken(), token.getConsumerSecret());
			//provider.retrieveAccessToken(consumer, token.getRequestToken());

			return consumer;	
		}
	}//end getAccessTokenFromPin

	/**
	 * This will get an access token based on the parameters passed in
	 * @param params the parameters to set
	 * @return the oauth client request for parameters
	 * @throws OAuthSystemException 
	 */
	public static OAuthClientRequest getAccess(OAuthParams params) throws OAuthSystemException  {


		OAuthClientRequest request;
		/*
		request = OAuthClientRequest
				.tokenLocation(params.getTokenEndpoint()).setCode(URLEncoder.encode(params.getAuthzCode(),"UTF-8")).setClientId(URLEncoder.encode(params.getClientId(),"UTF-8")).setClientSecret(URLEncoder.encode(params.getClientSecret(),"UTF-8"))
				.setRedirectURI(URLEncoder.encode(params.getRedirectUri(), "UTF-8")).setGrantType(GrantType.AUTHORIZATION_CODE).setScope("")
				.buildBodyMessage();
		 */
		request = OAuthClientRequest
				.tokenLocation(params.getTokenEndpoint()).setCode(params.getAuthzCode()).setClientId(params.getClientId()).setClientSecret(params.getClientSecret())
				.setRedirectURI(params.getRedirectUri()).setGrantType(GrantType.AUTHORIZATION_CODE)
				.buildBodyMessage();

		return request;
	}//end getAccess

	/**
	 * This will build the oauth params from the given data
	 * @param info the app info to use
	 * @param urls the app urls to build from
	 * @return the oauth params to use
	 */
	public static OAuthParams buildFromData(OAuth2AppInfo info,OAuth2Urls urls) {
		OAuthParams ret = new OAuthParams();
		ret.setClientId(info.getClientId());
		ret.setScope(info.getScope());
		ret.setTokenEndpoint(urls.getTokenUrl());
		ret.setClientSecret(info.getClientSecret());
		ret.setRedirectUri(info.getRedirectUrl());
		ret.setAuthzEndpoint(urls.getAuthzUrl());
		ret.setApplication(info.getService().getName());
		return ret;
	}//end buildFromData

	/**
	 * This will build an authenticated  request
	 * @param info the info to use
	 * @param urls the urls to use
	 * @param key the key to use
	 * @return authenticated parameters for oauth2
	 */
	public static  OAuthParams buildAuthenticated(OAuth2AppInfo info,OAuth2Urls urls,OAuth2KeyUser key) {
		OAuthParams params=buildFromData(info,urls);
		String authzCode=key.getCode();
		params.setAuthzCode(authzCode);
		return params;
	}//end buildAuthenticated

	/**
	 * This will extract a code for an ouauth request from the given uri
	 * @param uri the uri to extract from
	 * @return null if none exists, otherwise the id parameter from this uri
	 */
	public static String getIdFromUri(URI uri) {
		String query=uri.getQuery();
		String id="code";
		int idx=query.indexOf(id);
		if(idx < 1)
			return null;
		else {
			int equals=idx+1;
			if(!(query.charAt(equals)=='=')) {
				return null;
			}
			else {
				StringBuffer sb = new StringBuffer();
				int begin=equals+1;
				for(int i=begin;i<query.length();i++) {
					char c=query.charAt(i);
					if(c=='&')
						return sb.toString();
					else sb.append(c);
				}
				return sb.toString();
			}
		}
	}//end getIdFromUri

	/**
	 * This will extract a code for an ouauth request from the given uri
	 * @param uri the uri to extract from
	 * @return null if none exists, otherwise the id parameter from this uri
	 */
	public static String getIdFromUri(OAuthClientRequest request) {
		String query=request.getLocationUri();
		String id="code";
		int idx=query.indexOf(id);
		if(idx < 1)
			return null;
		else {
			int equals=idx+1;
			if(!(query.charAt(equals)=='=')) {
				return null;
			}
			else {
				StringBuffer sb = new StringBuffer();
				int begin=equals+1;
				for(int i=begin;i<query.length();i++) {
					char c=query.charAt(i);
					if(c=='&')
						return sb.toString();
					else sb.append(c);
				}
				return sb.toString();
			}
		}
	}//end getIdFromUri
	/**
	 * This will extract an id for an ouauth request from the given uri
	 * @param uri the uri to extract from
	 * @return null if none exists, otherwise the id parameter from this uri
	 */
	public static String getIdFromUri(String uri) {
		String query=uri;
		String id="code";
		int idx=query.indexOf(id);
		if(idx < 1)
			return null;
		else {
			int equals=idx+1;
			if(!(query.charAt(equals)=='=')) {
				return null;
			}
			else {
				StringBuffer sb = new StringBuffer();
				int begin=equals+1;
				for(int i=begin;i<query.length();i++) {
					char c=query.charAt(i);
					if(c=='&')
						return sb.toString();
					else sb.append(c);
				}
				return sb.toString();
			}
		}
	}//end getIdFromUri


	public static HttpClient getAuthenticatedClient(String type,String userName,String password,Map<String,String> urls) throws OAuthSystemException {
		DefaultHttpClient ret = new DefaultHttpClient();
		if(type.equals(BASIC)) {
			ret.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM,
							AuthScope.ANY_SCHEME),
							new UsernamePasswordCredentials(userName,password));
			return ret;
		}
		else if(type.equals(NTLM)) {
			String baseUrl=urls.get(BASE_URL);
			String ntlmDomain=urls.get(NTLM_DOMAIN);
			ret.getAuthSchemes().register("ntlm", new NTLMSchemeFactory());
			ret.getCredentialsProvider().setCredentials(
					new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM,
							AuthScope.ANY_SCHEME), 
							new NTCredentials(userName,password, baseUrl, ntlmDomain));

		}

		else if(type.equals(OAUTH2)) {
			/*
			String consumerKey=urls.get(CONSUMER_KEY);
			String secretKey=urls.get(SECRET_KEY);
			String scope=urls.get(SCOPE);
			String userAuthorizationURL=urls.get(AUTH_PATH);
			String redirectUri=urls.get(REDIRECT_URI);
			String code=urls.get(RESPONSE_CODE);
			String tokenLocation=urls.get(REQUEST_TOKEN_URI);

			OAuthClientRequest request = OAuthClientRequest
		                .authorizationLocation(userAuthorizationURL)
		                .setClientId(consumerKey).setScope(scope)
		                .setRedirectURI(redirectUri).setResponseType(ResponseType.CODE.toString())
		                .buildQueryMessage();
			 */
			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
			return (HttpClient) oAuthClient;
		}
		/**
		 * http://hc.apache.org/httpcomponents-client-ga/ntlm.html
		 */
		else if(type.equals(SPNEGO)) {

		}
		return ret;
	}
	/**
	 * This returns whether a string is empty or null
	 * @param string the string to check
	 * @return true if the string is null or empty, false otherwise
	 */
	public static boolean invalidString(String string) {
		return string==null || string.isEmpty();
	}//end invalidString

	/**
	 * This checks to see if a header is issued, and if it is
	 * returns the value, otherwise will set a not issued header
	 * @param value the value to check for
	 * @return the passed in value if it's not empty or null,
	 * or a not issued value for a header
	 */
	public static String isIssued(String value) {
		if (invalidString(value)) {
			return "none";
		}
		return value;
	}//end isIssued

	/**
	 * This executes a search for a particular cookie in a request
	 * @param request the request to search
	 * @param key the cookie to search for
	 * @return the cookie value of the target key, or empty
	 * if nothing is found
	 */
	public static String findCookieValue(HttpServletRequest request, String key) {
		Cookie[] cookies = request.getCookies();

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(key)) {
				return cookie.getValue();
			}
		}
		return "";
	}//end findCookieValue


	/**
	 * This will build an authorization request uri with the given
	 * info and the urls
	 * @param info the info to use
	 * @param url the url to use
	 * @return the url needed to make an authorization request
	 * @throws OAuthSystemException 
	 */
	public  static String buildAuthUri(OAuth2AppInfo info,OAuth2Urls url) throws OAuthSystemException {
		if(info==null || url==null)
			return null;
		String authzEndPoint=url.getAuthzUrl();
		String scope=info.getScope();
		String clientId=info.getClientId();
		String redirectUri=info.getRedirectUrl();

		OAuthParams params = new OAuthParams();
		params.setAuthzEndpoint(authzEndPoint);
		params.setClientId(clientId);
		params.setScope(scope);
		params.setRedirectUri(redirectUri);
		OAuthClientRequest request=offlineAccess(params);


		return request.getLocationUri();
	}//end buildAuthUri


	/* Basic authentication */
	public final static String BASIC="basic";
	/* Windows NT auth */
	public final static String NTLM="nt";
	/* oauth */
	public final static String OAUTH="oauth";
	/* oauth2 */
	public final static String OAUTH2="oauth2";

	/* kerberos */
	public final static String SPNEGO="spnego/kerberos";
	/* base url for requests */
	public final static String BASE_URL="baseUrl";
	/* For oauth used in request token */
	public final static String REQUEST_TOKEN_PATH="baseTokenPath";
	/* For oauth, used for verification of user */
	public final static String AUTH_PATH="authPath";
	/* For oauth, used for accessing protected resources after authenticated */
	public final static String ACCESS_TOKEN_PATH="accessToken";
	/* For oauth,Consumer application key, usually needs to be generated by corresponding platform */
	public final static String CONSUMER_KEY="consumerKey";
	/* For oauth,Consumer secret key, usually needs to be generated by corresponding oauth platform */
	public final static String SECRET_KEY="secretKey";
	/* For oauth, used for returning client applications to a call back url when authenticated */
	public final static String CALLBACK_URL="callBackUrl";
	/* domain for authentication in samba/windows/ntlm */
	public final static String NTLM_DOMAIN="ntlmdomain";

	/* request token cookie for oauth */
	public final static String REQUEST_TOKEN_COOKIE="requesttoken";
	/* access token cookie for oauth */
	public final static String ACCESS_TOKEN_COOKIE="accesstoken";
	/* secret token cookie for oauth */
	public final static String TOKEN_SECRET_COOKIE="tokensecret";

	/* For oauth 1 and 2 to specify a scope by which to act on,
	 * this can also be a spaced separated string for a value
	 */
	public final static String SCOPE="scope";
	/* For oauth 1 and 2 for redrect uri for a client application */
	public final static String REDIRECT_URI="redirectUri";
	/* For oauth 2, used in web applications to tell the oauth server 
	 * what to return as a response */

	public final static String RESPONSE_CODE="code";
	/* For oauth2 used in requesting an access token */
	public final static String REQUEST_TOKEN_URI="request_token_uri";

	public final static String OAUTH1_CALLBACKPARAM="oauth_callback";

	public final static String OAUTH1_CONSUMER_KEY="oauth_consumer_key";

	public final static String OAUTH1_CONSUMER_SECRET="oauth_consumer_secret";


	public final static String OAUTH2_ACCESS_TOKEN="access_token";

	public final static String POST="POST";


	public final static String USERNAME_FORM_PARAM="UserNameFormParam";

	public final static String PASSWORD_FORM_PARAM="passwordFormParam";
	public final static String USER_NAME="userName";
	public final static String PASSWORD="password";


	public final static String GET="GET";

	public final static String PUT="PUT";

	public final static String DELETE="DELETE";

	private static Logger log=LoggerFactory.getLogger(HttpAuthUtils.class);


}
