package com.ccc.oauth.base;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.message.OAuthResponse;

import com.ccc.oauth.api.Oauth2AuthEndPoint;

public class Oauth2EndPointImpl implements Oauth2AuthEndPoint {

	@Override
	public void processAuth(HttpServletRequest request,
			HttpServletResponse response) {
		/*
		try {
		         //dynamically recognize an OAuth profile based on request characteristic (params,
		         // method, content type etc.), perform validation
			  OAuthClientRequest oauthRequest = new OAuthClientRequest(request);

		           //some code ....


		         //build OAuth response
		         OAuthResponse resp = OAuthASResponse
		             .authorizationResponse(HttpServletResponse.SC_FOUND)
		             .setCode(oauthIssuerImpl.authorizationCode())                    
		             .location(redirectURI)
		             .buildQueryMessage();

		         response.sendRedirect(resp.getLocationUri());

		         //if something goes wrong
		    } catch(OAuthProblemException ex) {
		         final OAuthResponse resp = OAuthAccessTokenResponse
		             .errorResponse(HttpServletResponse.SC_FOUND)
		             .error(ex)
		             .location(redirectUri)
		             .buildQueryMessage();
		                
		         response.sendRedirect(resp.getLocationUri());
		    }
			*/
	}

}
