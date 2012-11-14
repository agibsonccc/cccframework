package com.ccc.ccm.fetcher.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

import org.apache.activemq.broker.Broker;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.ccm.broker.ActiveMQBrokerStore;
import com.ccc.ccm.client.MessageClient;
import com.ccc.jndi.fetcher.ldap.LDAPContextFetcher;

public class StartingBrokerLDAPContextFetcher extends LDAPContextFetcher {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2084371726695551432L;
	public StartingBrokerLDAPContextFetcher(MessageClient messageClient) {
		this.messageClient=messageClient;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Context fetch() {
		if(context==null) {
			@SuppressWarnings("rawtypes")
			Hashtable<String, String> env = new Hashtable<String, String>();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");

			env.put(Context.PROVIDER_URL,
					url);
			env.put(Context.SECURITY_PRINCIPAL, bindDn);
			env.put(Context.SECURITY_CREDENTIALS, bindPassword);
			env.put(Context.OBJECT_FACTORIES,className);

			try {
				context = new InitialDirContext(env);
			}catch(NamingException e) {
				e.printStackTrace();
				return null;
			}

		}

		return context;
	}

	public void startBroker() throws Exception {
		ActiveMQBrokerStore b=(ActiveMQBrokerStore) messageClient.brokerStore();
		if(b==null) {
			b = new ActiveMQBrokerStore();
			b.setUrl(brokerUrl);
		}
		broker=b.create(factoryName);
		//broker.start();
	}

	public String getFactoryName() {
		return factoryName;
	}

	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}

	public MessageClient getMessageClient() {
		return messageClient;
	}

	public void setMessageClient(MessageClient messageClient) {
		this.messageClient = messageClient;
	}
	
	
	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}


	private String brokerUrl;
	private String factoryName;
	@Autowired(required=false)
	private MessageClient messageClient;

	private String className;
	private Broker broker;
}
