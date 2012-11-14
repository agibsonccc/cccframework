package com.ccc.oauth.api;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * This is an oauth 2 authorization end point.
 * @author Adam Gibson
 *
 */
public interface Oauth2AuthEndPoint {

	/**
	 * Process the end point based on the request and response
	 * @param request 
	 * @param response
	 */
	public void processAuth(HttpServletRequest request, HttpServletResponse response);
}//end Oauth2AuthEndPoint
