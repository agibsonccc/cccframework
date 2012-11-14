package com.ccc.oauth.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.GitHubTokenResponse;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.ccc.oauth.amber.oauth2.model.OAuthParams;
import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.apimanagement.model.Subscribed;
import com.ccc.oauth.services.ServiceProcessHolder;
import com.ccc.oauth.util.HttpAuthUtils;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.client.UserClient;
import com.ccc.util.spring.SpringUtils;



public abstract class RedirectController {
	
	public  String apiKey(HttpServletRequest request,HttpServletResponse response,@RequestParam(value="code",required=false)String token,@RequestParam(value="error",required=false)String error,@RequestParam(value="error_description",required=false)String errorMessage,@RequestParam(value="oauth_token",required=false)String oauth1Token,@RequestParam(value="oauth_verifier",required=false)String oauthVerifier,String state) throws JSONException, OAuthSystemException, OAuthProblemException {
		

		String userName=getUserName();
		if(userName!=null) {
			if(userName.equals(SpringUtils.ANONYMOUS_USER) && state!=null)
				userName=state;
			else if(request.getCookies()!=null && request.getCookies().length >=1) {
				//attempt to find via cookie
				Cookie[] cookies=request.getCookies();
				String user = null;

				for(Cookie cookie : cookies) {
					String name=cookie.getName();
					if(name.equals("user"))
						user=cookie.getValue();
				}
				if(user!=null)
					userName=user;
				else throw new IllegalStateException("User name not known");
			}
			else throw new IllegalStateException("User name not known");
			
			Integer service=ServiceProcessHolder.getService(userName);
			Assert.notNull(service,"Service integer was null!");
			BasicUser user=userClient.userForName(userName);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUserName(),null,user.getAuthorities());
			//auth.setAuthenticated(true);
			auth.setDetails(user);
			SecurityContextHolder.getContext().setAuthentication(auth);
			List<Subscribed> servicesForUser=oauthService.servicesForUser(user);
			if(servicesForUser==null || servicesForUser.isEmpty()) {
				log.info("Illegal access to api token no services found");
				return getPageUrl();
			}
			else {
				for(Subscribed sub : servicesForUser) {
					Assert.notNull(sub.getService(),"Service was null!");
					if(sub.getService()!=null && sub.getService().getId()==service) {
						oauthService.processRedirect(request, response, token, error, errorMessage, oauth1Token, oauthVerifier, sub.getService(), userName);
						postProcess(sub.getService(),userName);
						break;
					}
				}
			}

		}
		return getPageUrl();
	}
	
	
	public String getUserName() {
		return SpringUtils.getCurrentUserName();
	}
	public ModelAndView authorize( int id,
			HttpServletRequest req) throws OAuthSystemException, IOException {
		OAuthParams oauthParams = new OAuthParams();

		try {

			OAuthClientRequest request = OAuthClientRequest
					.tokenLocation(oauthParams.getTokenEndpoint())
					.setClientId(oauthParams.getClientId())
					.setClientSecret(oauthParams.getClientSecret())
					.setRedirectURI(oauthParams.getRedirectUri())
					.setCode(oauthParams.getAuthzCode())
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.buildQueryMessage();

			OAuthClient client = new OAuthClient(new URLConnectionClient());
			String app = HttpAuthUtils.findCookieValue(req, "app");

			OAuthAccessTokenResponse oauthResponse = null;
			Class<? extends OAuthAccessTokenResponse> cl = OAuthJSONAccessTokenResponse.class;

			if (app.toLowerCase().contains("facebook")) {
				cl = GitHubTokenResponse.class;
			} 

			oauthResponse = client.accessToken(request, cl);

			oauthParams.setAccessToken(oauthResponse.getAccessToken());
			oauthParams.setExpiresIn(HttpAuthUtils.isIssued(String.valueOf(oauthResponse.getExpiresIn())));
			oauthParams.setRefreshToken(HttpAuthUtils.isIssued(oauthResponse.getRefreshToken()));

			return new ModelAndView("get_resource");

		} catch (OAuthProblemException e) {
			StringBuffer sb = new StringBuffer();
			sb.append("</br>");
			sb.append("Error code: ").append(e.getError()).append("</br>");
			sb.append("Error description: ").append(e.getDescription()).append("</br>");
			sb.append("Error uri: ").append(e.getUri()).append("</br>");
			sb.append("State: ").append(e.getState()).append("</br>");
			oauthParams.setErrorMessage(sb.toString());
			return new ModelAndView("get_authz");
		}
	}
	
	/**
	 * This is for the post processing of  a service when it is found.
	 * The "current service" that is resolved after the redirect
	 * @param service the service to post process
	 * @param userName the user to post process the service for
	 */
	public abstract void postProcess(Service service,String userName);
	
	public abstract String getPageUrl();


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

	private String pageUrl;
	@Autowired(required=false)
	protected UserClient userClient;
	@Autowired(required=false)
	protected OAuth2Service oauthService;
	private static Logger log=LoggerFactory.getLogger(RedirectController.class);
}
