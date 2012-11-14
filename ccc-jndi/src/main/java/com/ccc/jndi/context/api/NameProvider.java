package com.ccc.jndi.context.api;
/**
 * This is a provider for the name of a queue to lookup with jndi
 * @author Adam Gibson
 *
 */
public interface NameProvider {
	/**
	 * Queue name to look up a queue
	 * @return the name of the queue to get
	 */
	public String getName();
}
