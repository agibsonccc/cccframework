package com.ccc.ccm.context.fetcher;

import java.io.Serializable;

import javax.jms.ConnectionFactory;
import javax.naming.Context;
import javax.naming.NamingException;

import org.springframework.jms.connection.CachingConnectionFactory;

import com.ccc.jndi.context.api.ContextFetcher;
import com.ccc.jndi.context.api.NamedContextFetcher;

public class DynamicCachingConnectionFactory extends CachingConnectionFactory implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6205963909098558089L;

	public DynamicCachingConnectionFactory(ContextFetcher contextFetcher,String connectionFactoryName) {
		Context context=contextFetcher.fetch();

		try {
			ConnectionFactory factory=(ConnectionFactory) context.lookup(connectionFactoryName);
			setTargetConnectionFactory(factory);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}

	public DynamicCachingConnectionFactory(NamedContextFetcher contextFetcher) {
		Context context=contextFetcher.fetch();

		try {
			ConnectionFactory factory=(ConnectionFactory) context.lookup(contextFetcher.getName());
			setTargetConnectionFactory(factory);
		} catch (NamingException e) {
			e.printStackTrace();
		}

	}
	public DynamicCachingConnectionFactory(NamedContextFetcher contextFetcher,String customObjectFactory) {
		Context context=contextFetcher.fetch(customObjectFactory);

		try {
			ConnectionFactory factory=(ConnectionFactory) context.lookup(contextFetcher.getName());
			setTargetConnectionFactory(factory);
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

}
