package com.ccc.ccm.client.activemq;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.ccc.jndi.context.api.ContextFetcher;

public class ActiveMQContextFetcher implements ContextFetcher  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9072289811840006406L;

	public ActiveMQContextFetcher(){}
	
	public ActiveMQContextFetcher(String queueUrl) {
		super();
		this.queueUrl = queueUrl;
	}

	@Override
	public Context fetch() {
	if(context!=null) return context;
		Hashtable props = new Hashtable();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.put(Context.PROVIDER_URL,queueUrl);
		try {
			context = new InitialContext(props);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return context;
	}

	@Override
	public Context fetch(String objectFactory) {
		if(context!=null) return context;
		Hashtable props = new Hashtable();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.put(Context.PROVIDER_URL,queueUrl);
		props.put(Context.OBJECT_FACTORIES,objectFactory);
		try {
			context = new InitialContext(props);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return context;
	}

	public String getQueueUrl() {
		return queueUrl;
	}

	public void setQueueUrl(String queueUrl) {
		this.queueUrl = queueUrl;
	}

	private String queueUrl;
	
	private Context context;
}
