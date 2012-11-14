package com.ccc.jndi.context.api;

import java.io.Serializable;

import javax.naming.Context;
/**
 * This will fetch a context for JNDI
 * based on settings specified.
 * @author Adam Gibson
 *
 */
public interface ContextFetcher<E> extends Serializable {
	/**
	 * This will fetch a JNDI context to be used
	 * with queues.
	 * @return a context used for JNDI
	 */
	public Context fetch();
	/**
	 * This will fetch a jndi context using the specified object favtory
	 * @param objectFactory the object factory to use
	 * @return a jndi context
	 */
	public Context fetch(String objectFactory);
}//end ContextFetcher
