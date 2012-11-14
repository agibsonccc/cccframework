package com.ccc.oauth.api;

import java.util.Map;

import com.ccc.oauth.amber.oauth2.model.OAuthParams;
/**
 * This is an oauth 2 client executor that can be executed
 * by the base implementation or a specific implementation using
 * 
 * the oauthparams pojo. This class also comes with specific keys
 * for use in the base implementation which are looked for
 * during execution to build a command to execute
 * @author Adam Gibson
 *
 */
public interface OAuth2RequestExecutor extends AuthRequestExecutor {

	public OAuthParams buildFromMap(Map<String,Object> params);
	
	/**
	 * An option to execute from oauth params as well
	 * @param params the params to use for execution
	 * @param userName username for login
	 * @param password password for login
	 */
	public void execute(OAuthParams params,String userName,String password);
	
	
	public final static  String CLIENT_ID="clientId";
	public final static  String CLIENT_SECRET="clientSecret";
	public final static  String REDIRECT_URI="redirecturi";
	public final static  String AUTHENDPOINT="authzendpoint";
	public final static  String TOKEN_ENDPOINT="tokenEndPoint";
	public final static  String AUTHZ_CODE="authz_code";
	public final static  String ACCESS_TOKEN="access_token";
	public final static  String EXPIRES_IN="expires_in";
	public final static  String REFRESH_TOKEN="refreshToken";
	public final static  String SCOPE="scope";
	public final static  String RESOURCE_URL="resource_url";
	public final static  String RESOURCE="resource";
	public final static  String APPLICATION="application";
	public final static  String ERROR_MESSGE="error_message";
	
	
	public final static String[] ALL_PARAMS={CLIENT_ID,CLIENT_SECRET,REDIRECT_URI,AUTHENDPOINT,TOKEN_ENDPOINT,AUTHZ_CODE,ACCESS_TOKEN,EXPIRES_IN,
		REFRESH_TOKEN,SCOPE,RESOURCE_URL,RESOURCE,APPLICATION,ERROR_MESSGE};
	
}
