package com.ccc.ccm.server.retrievers;

import java.rmi.Remote;

import javax.jms.ConnectionFactory;
import javax.naming.NamingException;

/**
 * This is a connection factory retriever. 
 * For a given user, users are allowed on certain topics and queues.
 * This is meant to be an interface for retrieving connection factories for a given
 * user, usually using JNDI.
 * @author Adam Gibson
 *
 */
public interface ConnectionFactoryRetriever extends Remote {
	/**
	 * This retrieves the connection factory from the server, usually using JNDI lookup.
	 * @return the connection factory with a default name
	 */
	public ConnectionFactory get();
	
	/**
	 * This will retrieve a connection factory with the given name.
	 * @param name the name of the connection factory to get
	 * @return the connection factory associated with the given name.
	 * @throws NamingException  if the jndi look up doesn't work
	 */
	public ConnectionFactory get(String name) throws NamingException;
	
	
}//end ConnectionFactoryRetriever
