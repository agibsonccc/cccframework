package com.ccc.oauth.http;

import java.net.URI;
import java.util.Map;

import org.apache.amber.oauth2.client.HttpClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.response.OAuthClientResponse;
import org.apache.amber.oauth2.client.response.OAuthClientResponseFactory;
import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.utils.OAuthUtils;
import org.apache.amber.oauth2.httpclient4.HttpClient4;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.util.EntityUtils;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RedirectingHttpClient4 extends HttpClient4 {

	public RedirectingHttpClient4() {
		client = new DefaultHttpClient();
		((DefaultHttpClient) client).setRedirectStrategy(new DefaultRedirectStrategy() {                
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
	}

	private org.apache.http.client.HttpClient client;
	public <T extends OAuthClientResponse> T execute(OAuthClientRequest request,
			Map<String, String> headers,
			String requestMethod,
			Class<T> responseClass)
					throws OAuthSystemException, OAuthProblemException {

		try {
			URI location = new URI(request.getLocationUri());
			HttpRequestBase req = null;
			String responseBody = "";

			if (!OAuthUtils.isEmpty(requestMethod) && OAuth.HttpMethod.POST.equals(requestMethod)) {
				req = new HttpPost(location);
				HttpEntity entity = new StringEntity(request.getBody());
				((HttpPost)req).setEntity(entity);

			} else {
				req = new HttpGet(location);
				HttpGet get=(HttpGet) req;


			}
			if (headers != null && !headers.isEmpty()) {
				for (Map.Entry<String, String> header : headers.entrySet()) {
					req.setHeader(header.getKey(), header.getValue());
				}
			}
			HttpResponse response = client.execute(req);
			int numRetries=0;
			
			Header contentTypeHeader = null;
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				responseBody = EntityUtils.toString(entity);
				contentTypeHeader = entity.getContentType();
			}
			String contentType = null;
			if (contentTypeHeader != null) {
				contentType = contentTypeHeader.toString();
			}
			if(contentType!=null && !contentType.isEmpty()) {
				if(!contentType.contains("json")) {
					log.warn("ResponseBody was wrong type here is the body:  " + responseBody + " and type: " + contentType + " uri was: " + request.getLocationUri());
				}
			}
			return OAuthClientResponseFactory
					.createCustomResponse(responseBody, contentType, response.getStatusLine().getStatusCode(),
							responseClass);
		} catch (Exception e) {
			throw new OAuthSystemException(e);
		}

	}

	private static Logger log=LoggerFactory.getLogger(RedirectingHttpClient4.class);
}
