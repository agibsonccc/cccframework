package com.ccc.oauth.auth.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ccc.oauth.amber.oauth2.model.OAuthParams;

import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.AuthSupports;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.util.HttpAuthUtils;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class AuthTests extends AbstractJUnit4SpringContextTests {
	

	
	@Test
	public void testAuthToken() {
		Service service=oauthService.serviceWithId(1);
		//String accessToken=HttpAuthUtils.requestAccessWithLookup(oauthService, "agibson", 1);
		
	}
	
	
	
	@Test
	public void testAuthSupports() {
		List<Service> allServices=oauthService.allServices();
		
		for(Service service : allServices) {
			List<AuthSupports> supports=oauthService.supportedAuthForService(service);
			boolean oauthSupported=oauthService.authSupported(HttpAuthUtils.OAUTH2, service);
			boolean authEncountered=false;
			for(AuthSupports supported : supports) {
				if(supported.getAuthType().getName().equals(HttpAuthUtils.OAUTH2)) {
					authEncountered=true;
					Assert.isTrue(oauthSupported==authEncountered);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	@Test
	public void testOAuth() throws OAuthSystemException {
		/**
		 * http://code.google.com/apis/accounts/docs/OAuth2Login.html
		 * https://www.googleapis.com/calendar/v3/calendars/calendarId/events/import
		 */
		
		String baseUrl="https://accounts.google.com/o/oauth2/auth";
		String redirectUri="https://www.clevercloudcomputing.com/oauth2callback";
		String apiKey="AIzaSyB-o71Dj_Lzri9KFDAQBXbSBGh_icYq5L4";
		String clientId="247347238320.apps.googleusercontent.com";
		String clientSecret="rdx5wiMd-RqAlNCE_Vbv1a6d";
		String requestPath="https://www.google.com/accounts/OAuthGetAccessToken";
		OAuthParams params = new OAuthParams();
		params.setAuthzEndpoint("https://accounts.google.com/o/oauth2/auth");
		params.setRedirectUri("https://www.clevercloudcomputing.com/oauth2callback");
		params.setClientId("247347238320.apps.googleusercontent.com");
		params.setClientSecret("rdx5wiMd-RqAlNCE_Vbv1a6d");
		params.setScope("https://www.googleapis.com/auth/calendar");
		params.setApplication(apiKey);
		
		  OAuthClientRequest request = OAuthClientRequest
	                .authorizationLocation(baseUrl)
	                .setClientId(clientId)
	                .setRedirectURI(redirectUri)
	                .buildQueryMessage();

		
		
		Map<String,String> testUrls = new HashMap<String,String>();
		testUrls.put(HttpAuthUtils.BASE_URL, baseUrl);
		testUrls.put(HttpAuthUtils.CALLBACK_URL,redirectUri);
		testUrls.put(HttpAuthUtils.CONSUMER_KEY, clientId);
		testUrls.put(HttpAuthUtils.SECRET_KEY,clientSecret);
		testUrls.put(HttpAuthUtils.REQUEST_TOKEN_PATH, requestPath);
		//DefaultHttpClient authedClient=HttpAuthUtils.getAuthenticatedClient(HttpAuthUtils.OAUTH, "agibson@clevercloudcomputing.com", "destrotroll%5", testUrls);
		
	}
	
	
	public OAuth2Service getOauthService() {
		return oauthService;
	}







	public void setOauthService(OAuth2Service oauthService) {
		this.oauthService = oauthService;
	}


	@Autowired
	private  OAuth2Service oauthService;

}
