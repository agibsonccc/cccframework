package com.ccc.camelcomponents.ical.util.api;

public interface MethodServicemapper {

	/**
	 * This will return a method to use for the given uri
	 * @param uri the uri to get the method for
	 * @return get a or post, depending on the uri
	 */
	public String methodFor(String uri);
	
	public final static String GET="get";
	
	public final static String POST="post";
}
