package com.ccc.camelcomponents.core.api;

import java.util.Map;
/**
 * A Parameter retrieval strategy is a way of retrieving uri parameters for values based on 
 * the passed in values
 * @author Adam Gibson
 *
 */
public interface ParameterRetrievalStrategy {

	/**
	 * This will return the given parameter value based on the passed in values if any.
	 * @param params the needed params if necessary for the strategy
	 * @return the parameter value based on the name and the parameters passed in.
	 */
	public String getValueFor(String paramName,Map<String,Object> params);
}
