package com.ccc.webapp.event.api;

/**
 * This is an event handler for webapps. Based on the listeners 
 * idea in the servlet web context, this is an interface for specifying events
 * and allowing for hooks in to the internals of a webapp
 * @author Adam Gibson
 *
 */
public interface WebappEventHandler {

	/**
	 * This will handle an event based on session information 
	 * passed in to the handler. 
	 * @throws Exception 
	 */
	public void handleEvent(WebappEvent event) throws Exception;
	

	
}//end WebappEventHandler
