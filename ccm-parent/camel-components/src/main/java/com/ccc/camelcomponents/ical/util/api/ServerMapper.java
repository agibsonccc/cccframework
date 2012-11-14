package com.ccc.camelcomponents.ical.util.api;

public interface ServerMapper {

	/**
	 * This will return the service for the given uri
	 * A service is defined as a REST API for a given provider
	 * This provider is mapped to in this class and the 
	 * names for all the providers will be provided
	 * @param uri the uri to map
	 * @return the service for the given uri
	 */
	public String serviceFor(String uri);
	
	
	public final static String ZIMBRA="zimbra";
	
	public final static String GOOGLE_DATA="google_data";
	
	public final static String SHAREPOINT="sharepoint";
	
	public final static String OUTLOOK="outlook";
	
	public final static String EXCHANGE="exchange";
	
	
	
}//end ServerMapper
