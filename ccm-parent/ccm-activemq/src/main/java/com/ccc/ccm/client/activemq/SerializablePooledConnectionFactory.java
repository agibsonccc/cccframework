package com.ccc.ccm.client.activemq;

import java.io.Serializable;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

public class SerializablePooledConnectionFactory extends PooledConnectionFactory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2918192455768231817L;

	public SerializablePooledConnectionFactory() {
		super();
	}

	public SerializablePooledConnectionFactory(
			ActiveMQConnectionFactory connectionFactory) {
		super(connectionFactory);
	}

	public SerializablePooledConnectionFactory(String brokerURL) {
		super(brokerURL);
	}

}
