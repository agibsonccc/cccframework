package com.ccc.camelcomponents.core.api;

import java.util.Map;

public interface URIConfigurator extends ServiceConstants {

	/**
	 * This will return a uri based on the type of service being registered
	 * @param type the type of service being registered
	 * @param params the params for the uri
	 * @return a uri matching the proper server
	 */
	public String uri(String type,Map<String,Object> params);
	
	
	
	
	
}
