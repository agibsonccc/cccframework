package com.ccc.oauth.api;

import java.util.Map;
/**
 * This is a base class for an authenticated 
 * executor.
 * @author Adam Gibson
 *
 */
public interface AuthRequestExecutor {

	/**
	 * This is a base implementation by which 
	 * an authenticated client will execute based on a simple
	 * method,user name,password, and passed in http client specified
	 * by the given keys
	 * 
	 * @param params
	 */
	public void execute(Map<String,Object> params);
	/**
	 * User name parameter 
	 */
	public final static String USERNAME="userName";
	/**
	 * password key
	 */
	public final static String PASSWORD="password";
	/**
	 * Method by which the client will execute
	 */
	public final static String METHOD="method";
	/**
	 * A passed in client
	 */
	public final static String CLIENT="client";
}//end AuthRequestExecutor
