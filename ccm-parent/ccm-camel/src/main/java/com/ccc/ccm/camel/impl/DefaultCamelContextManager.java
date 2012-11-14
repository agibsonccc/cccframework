package com.ccc.ccm.camel.impl;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import com.ccc.ccm.camel.core.CamelContextManager;
import com.ccc.ccm.camel.core.CamelRoute;
import com.ccc.ccm.camel.core.CamelRouteBuilder;
import com.ccc.ccm.camel.core.DestinationPair;
/**
 * This is a default implementation of a camel context manager. A few tips of usage: 
 * Make sure to create a camel route before usage. This is  the recommended method 
 * of initializing the context manager for usage of other applications.
 * @author Adam Gibson
 *
 */
public class DefaultCamelContextManager implements CamelContextManager {

	@Override
	public void createContext() {
		managedContext = new DefaultCamelContext();
		created=true;
	}

	@Override
	public void addComponent(String name,Component component) {
		managedContext.addComponent(name, component);
	}

	@Override
	public void addRoutes(RouteBuilder routeBuilder) throws Exception {
		managedContext.addRoutes(routeBuilder);
	}

	@Override
	public void startContext() throws Exception {
		if(!created)
			createContext();
		if(!started)
			managedContext.start();

	}

	@Override
	public void stopContext() throws Exception {
		if(!created)
			throw new IllegalStateException("Context not created.");
		if(started)
			managedContext.stop();
		else throw new IllegalStateException("Can't stop a not started context.");
	}

	@Override
	public void sendMessage(String to, String message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void configureRoute(CamelRoute route) throws Exception {
		builder = new CamelRouteBuilder(route);
		builder.configure();


	}

	@Override
	public void addProcessorToDestination(String destination,
			Processor processor) {
		builder.from(destination).process(processor);
	}

	private boolean created=false;

	private boolean started=false;

	private CamelContext managedContext;

	private CamelRouteBuilder builder=null;

}
