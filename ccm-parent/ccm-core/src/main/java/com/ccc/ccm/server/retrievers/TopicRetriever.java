package com.ccc.ccm.server.retrievers;

import java.util.Set;

import javax.naming.Context;
/**
 * This is a topic retriever for a given user.
 * @author Adam Gibson
 *
 */
public interface TopicRetriever {
	/**
	 * This retrieves a set of topics associated with a given user.
	 * @param userName the user name to get the set of topics for
	 * @return the set of topics associated with the user.
	 */
	public Set<String> topicsForUser(String userName);
	
	/**
	 * This returns a set of all the topics available within a given context.
	 * @return the set of topics associated with a given context.
	 * @param c the context to get associated topics for
	 */
	public Set<String> topics(Context c);
	
}//end TopicRetriever
