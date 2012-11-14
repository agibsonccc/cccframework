package com.ccc.camelcomponents.core.api;
/**
 * This represents a restful service that executes http requests
 * for a given url.
 * @author Adam Gibson
 *
 */
public interface RestfulService {
	/**
	 * This is the base url for the service.
	 * @return the base url for the service
	 */
	public String baseUrl();
	
	/**
	 * This will execute the given action using the given method: 
	 * PUT,GET,POST,DELETE
	 * @param action  the action to execute
	 * @param method the method to execute with
	 */
	public Object executeAction(String action,String method) throws NoActionException;
	/**
	 * This will add an action with the given path
	 * @param path the path to the action in the form of:
	 * baseurl/path/to/action
	 * @param action the action to add
	 */
	public void addAction(String path,String action) ;
	
}//end RestfulService
