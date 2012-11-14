package com.ccc.ccm.server.retrievers;

import java.util.Set;

import javax.naming.Context;
import javax.sql.DataSource;

import org.springframework.util.Assert;
/**
 * This is a base topic retriever for a given service which
 * contains the base framework needed for access to topics and a database
 * for associated topics where necessary. (Usually used for roles with topics)
 * @author Adam Gibson
 *
 */
public abstract class BaseTopicRetriever implements TopicRetriever {
	
	/**
	 * This initializes this topic retriever with the given context
	 * and data source
	 * @param c the context to use
	 * @param d the data source to use
	 * @throws IllegalArgumentException if context is null, note here the datasource is optional if you just
	 * want to a jndi look up
	 */
	public BaseTopicRetriever(Context c,DataSource d) throws IllegalArgumentException {
		Assert.notNull(c);
		this.c=c;
		this.d=d;
	}
	@Override
	public  abstract Set<String> topicsForUser(String userName);

	@Override
	public abstract  Set<String> topics(Context c);
	
	protected Context c=null;
	
	protected DataSource d=null;

}//end BaseTopicRetriever
