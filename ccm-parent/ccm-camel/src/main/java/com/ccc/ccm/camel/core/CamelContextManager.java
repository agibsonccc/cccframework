package com.ccc.ccm.camel.core;

import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
/**
 * http://camel.apache.org/book-in-one-page.html
 * @author Adam Gibson
 *
 */
public interface CamelContextManager {

	/**
	 * This builds a context which 
	 * this client will maintain.
	 */
	public void createContext();
	
	/**
	 * This will add a component to the context managed by this client.
	 * @param component the component to add
	 * @param name the uri of this component
	 */
	public void addComponent(String name,Component component);
	
	/**
	 * This will add call the add routes method of the past in route builder.
	 * @param routeBuilder the route builder to use
	 * @throws Exception 
	 */
	public void addRoutes(RouteBuilder routeBuilder) throws Exception;
	/**
	 * This will start the context managed by this context manager.
	 * @throws Exception 
	 */
	public void startContext() throws Exception;
	
	
	/**
	 * This will stop the context managed by this 
	 * context manager.
	 * @throws Exception 
	 */
	public void stopContext() throws Exception;
	
	/**
	 * This will send a message to the given route.
	 * @param to the to route
	 * @param message the message to send
	 */
	public void sendMessage(String to,String message);
	
	/**
	 * This will take the given route and append to/from pairs for a route
	 * builder.
	 * @param route the route to configure a route builder for.
	 * @throws Exception 
	 */
	public void configureRoute(CamelRoute route) throws Exception;
	/**
	 * This will add the given processor to the given destination.
	 * @param destination the destination to add to 
	 * @param processor the processor to add to this destination.
	 */
	public void addProcessorToDestination(String destination,Processor processor);
	
}//end CamelContextManager
