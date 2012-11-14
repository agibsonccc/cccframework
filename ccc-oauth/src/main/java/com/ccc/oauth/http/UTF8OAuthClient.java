package com.ccc.oauth.http;

import java.util.HashMap;
import java.util.Map;

import org.apache.amber.oauth2.client.HttpClient;
import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;

public class UTF8OAuthClient extends OAuthClient {

	public UTF8OAuthClient(HttpClient oauthClient) {
		super(oauthClient);
	}
	 public <T extends OAuthAccessTokenResponse> T accessToken(
		        OAuthClientRequest request, String requestMethod, Class<T> responseClass)
		        throws OAuthSystemException, OAuthProblemException {

		        Map<String, String> headers = new HashMap<String, String>();
		        headers.put(OAuth.HeaderType.CONTENT_TYPE, OAuth.ContentType.URL_ENCODED);
		        headers.put("charset","utf-8");
		        return httpClient.execute(request, headers, requestMethod, responseClass);
		    }
}
