package com.ccc.oauth.services;

import java.util.HashMap;
import java.util.Map;
/**
 * A service process holder is a way of holding events for the web browser
 * in between request so an oauth service knows what service was just redirected
 * for a user
 * @author Adam Gibson
 *
 */
public class ServiceProcessHolder {
	/**
	 * This will register a service id with the service process holder.
	 * Note that subsequent calls to register are considered overrides.
	 * @param userName the user name to register
	 * @param serviceId the service id to register
	 */
	public static void registerServiceId(String userName,int serviceId) {
		services.put(userName,serviceId);
	}
	/**
	 * This will return and remove the service for the given user name
	 * @param userName the user name to get a service id for
	 * @return the service if it exists, null otherwise
	 */
	public static Integer getService(String userName) {
		return services.remove(userName);
	}
	
	private static Map<String,Integer> services = new HashMap<String,Integer>();
}//end ServiceProcessHolder
