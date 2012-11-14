package com.ccc.ccm.camel.user.api;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
/**
 * This is a user context mapper for use with 
 * multiple camel contexts
 * @author Adam Gibson
 *
 */
public interface UserCamelContextMapper {
	/**
	 * This returns a camel context mapped by a user
	 * @param userName a user name mapping to a camel context
	 * @return a camel context for the given user or null 
	 * if no camel context is found
	 */
	public CamelContext getContext(String userName);
	
	/**
	 * This will delete the given context if one is mapped
	 * @param userName the name of the user to delete a context from
	 * @return the removed context
	 */
	public CamelContext removeContext(String userName);
	
	/**
	 * This will add a context to this mapper with the given user name
	 * @param userName the user name to map
	 * @param context the context to be mapped to
	 */
	public void addContext(String userName,CamelContext context);

	/**
	 * This will add the given routes mapped by the user name
	 * @param userName the name of the user to add a context for
	 * @param buillder the builder to add
	 * @throws Exception 
	 */
	public void addRoutesToContext(String userName,RouteBuilder buillder) throws Exception;
	
	
	
	
}//end UserCamelContextMapper
