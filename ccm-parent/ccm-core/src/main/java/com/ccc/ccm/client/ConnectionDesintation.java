package com.ccc.ccm.client;

import java.io.Serializable;
import java.net.URI;
/**
 * This is a destination that a message client connects to.
 * @author Adam Gibson
 *
 */
public class ConnectionDesintation implements Serializable {

	
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		this.uri = uri;
	}
	/**
	 * The type of destination
	 */
	private String type;
	/**
	 * The URI of this destination
	 */
	private URI uri;
	/**
	 * The queue type. A queue connects to 
	 * to 1 broker.
	 */
	public final static String QUEUE="queue";
	/**
	 * The topic type. A topic has one or more
	 * receivers called a subscriber.
	 */
	public final static String  TOPIC="topic";
}
