package com.ccc.ccm.server.retrievers;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.util.Assert;
/**
 * This is a base connection factory retriever that gives basic functionality for jndi look up.
 * @author Adam Gibson
 *
 */
public abstract class BaseConnectionFactoryRetriever implements ConnectionFactoryRetriever {
	/**
	 * This is the context that will be used by the retriever.
	 * @param context the context to be used with this retriever.
	 */
	public BaseConnectionFactoryRetriever(Context context) {
		Assert.notNull(context);
		this.context=context;
	}
	@Override
	public abstract ConnectionFactory get();

	@Override
	public ConnectionFactory get(String name) throws NamingException {
		return (ConnectionFactory) context.lookup(name);
	}

	
	/**
	 * This returns the context for this retriever
	 * @return the context for this retriever
	 */
	protected Context context() 
	{return context;}
	protected Context context=null;
	
}//end BaseConnectionFactoryRetriever
