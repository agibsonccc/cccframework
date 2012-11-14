package com.ccc.springclient.core;

import java.io.Serializable;

/**
 * This is a client for a spring based application.
 * @author Adam Gibson
 *
 */
public interface Client  extends Serializable{
	
	
	/**
	 * This will connect to a remote spring server.
	 * @return an object associated with the connection if necessary.
	 */
	public Object connect();
}//end Client
