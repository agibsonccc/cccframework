package com.ccc.ccm.context.fetcher;

import java.io.Serializable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;

import com.ccc.jndi.context.api.ContextFetcher;
import com.ccc.jndi.context.api.NamedContextFetcher;
/**
 * This will set up a dynamic connection factory 
 * based on the context fetcher and name
 * to look up the connection factory from.
 * @author Adam Gibson
 *
 */
public class DynamicJmsTemplate extends JmsTemplate implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8365130646293949086L;

	public DynamicJmsTemplate(ContextFetcher contextFetcher,String connectionFactoryName) {
		Assert.notNull(contextFetcher,"Context fetcher must not be null!");
		Assert.notNull(connectionFactoryName,"Name must not be null!");
		Assert.hasLength(connectionFactoryName,"Name must not be  empty!");
		
		Context context=contextFetcher.fetch();
		Assert.notNull(context,"Context was null!");
		try {
			

			ConnectionFactory factory=(ConnectionFactory) context.lookup(connectionFactoryName);
			Assert.notNull(factory,"Connection factory was null!");
			setConnectionFactory(factory);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	public DynamicJmsTemplate(NamedContextFetcher contextFetcher) {
		Assert.notNull(contextFetcher,"Context fetcher must not be null!");
		connectionFactoryName=contextFetcher.getName();
		Assert.notNull(connectionFactoryName,"Name must not be null!");
		Assert.hasLength(connectionFactoryName,"Name must not be  empty!");
		
		Context context=contextFetcher.fetch();
		Assert.notNull(context,"Context was null!");
		try {
			
		
			ConnectionFactory factory=(ConnectionFactory) context.lookup(connectionFactoryName);
			Assert.notNull(factory,"Connection factory was null!");
			setConnectionFactory(factory);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	
	
	public DynamicJmsTemplate(NamedContextFetcher contextFetcher,String customObjectFactory) {
		Assert.notNull(contextFetcher,"Context fetcher must not be null!");
		connectionFactoryName=contextFetcher.getName();
		Assert.notNull(connectionFactoryName,"Name must not be null!");
		Assert.hasLength(connectionFactoryName,"Name must not be  empty!");
		
		Context context=contextFetcher.fetch(customObjectFactory);
		Assert.notNull(context,"Context was null!");
		try {
			

			ConnectionFactory factory=(ConnectionFactory) context.lookup(connectionFactoryName);
			Assert.notNull(factory,"Connection factory was null!");
			setConnectionFactory(factory);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	
	

	public ContextFetcher getContextFetcher() {
		return contextFetcher;
	}

	public void setContextFetcher(ContextFetcher contextFetcher) {
		this.contextFetcher = contextFetcher;
	}

	public String getConnectionFactoryName() {
		return connectionFactoryName;
	}

	public void setConnectionFactoryName(String connectionFactoryName) {
		this.connectionFactoryName = connectionFactoryName;
	}


	private ContextFetcher contextFetcher;

	private String connectionFactoryName;
}//end DynamicJmsTemplate
