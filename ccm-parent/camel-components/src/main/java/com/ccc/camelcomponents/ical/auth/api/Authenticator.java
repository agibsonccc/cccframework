package com.ccc.camelcomponents.ical.auth.api;

import java.util.Map;

import org.apache.camel.Message;
/**
 * This is an authenticator service for abstracting various service
 * authentication mechanisms such as mail servers or http post in 
 * sending messages. This will allow the developer to set user names
 * and password at run time for a given producer/consumer.
 * @author Adam Gibson
 *
 */
public interface Authenticator {

	
	/**
	 * This will apply the necessary measures for authentication, and
	 * send the message along (This could be a transformer?)
	 * @param messagethe message to apply authentication to
	 */
	public Message applyAuthentication(Message message);
	
	/**
	 * This will return whether the given message is authorized 
	 * to interact with the given service
	 * @param message the message to verify
	 * @return true if the message is authenticated, false otherwise
	 */
	public boolean isAuth(Message message);
	
	
	/**
	 * This is an auth object that handles information for a given user.
	 * It could contain anything from the user name, to api keys
	 * for authentication with a given calendar server
	 * @return the object for use authentication with this authenticator
	 */
	public Object authObject();
	
	
	/**
	 * This is for headers of a particular service.
	 * Each service has different ways of authentication.
	 * @return the map of services to their authentication headers
	 */
	public Map<String,Object> headersForAuth();
}//end Authenticator
