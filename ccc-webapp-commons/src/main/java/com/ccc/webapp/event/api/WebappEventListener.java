package com.ccc.webapp.event.api;
/**
 * This is an event listener for webapps. It registers event handlers
 * for webapp events
 * @author Adam Gibson
 *
 */
public interface WebappEventListener {

	/**
	 * This is an iterable collection of logout handlers which
	 * this listener will trigger
	 * @return the list of handlers for this listener.
	 */
	public Iterable<WebappEventHandler> handlers();
	
	
	/**
	 * This will register the handler  with this listener.
	 * @param handler the handler to register
	 */
	public void registerHandler(WebappEventHandler handler);
	
	
	/**
	 * This will fire an the given event off to all of the registered handlers
	 * @param event the event to fire off
	 * @throws Exception 
	 */
	public void fireEvent(WebappEvent event) throws Exception;
}//edn WebappEventListener
