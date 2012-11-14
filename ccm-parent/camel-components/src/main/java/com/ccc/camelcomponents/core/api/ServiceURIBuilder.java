package com.ccc.camelcomponents.core.api;

import java.util.Map;

import com.ccc.oauth.apimanagement.model.Service;
/**
 * This is a uri builder for camel components
 * @author Adam Gibson
 *
 */
public interface ServiceURIBuilder {
	/**
	 * A service uri builder will use given strategies to retrieve uri parameters.
	 * @return the map of uri parameters to retrieval strategies
	 */
	public Map<String,ParameterRetrievalStrategy> strategies();
	/**
	 * The service this uri builder belongs to
	 * @return the service this uri builder belongs to
	 */
	public Service service();
	/**
	 * The name of the camel component this uri builder uses
	 * @return the name of the camel component this uri builder uses
	 */
	public String componentName();
	/**
	 * The base uri for this builder
	 * @return the base uri for this builder
	 */
	public String baseUri();

	/**
	 * All of the names of the possible parameters for this uri builder
	 * @return the possible names of this uri builder
	 */
	public String[] possibleParams();
	/**
	 * This will return whether the parameters are required or not
	 * @return the possible parameters 
	 */
	public Map<String,Boolean> requiredParams();
	/**
	 * This will return a built uri
	 * @return the built uri based on this builder
	 */
	public String build();
}
