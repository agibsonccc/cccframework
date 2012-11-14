package com.ccc.ccm.broker;

import java.io.Serializable;
import java.util.Map;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
/**
 * This interface is specifically for Active MQ based implementations.
 * This will allow for dynamic broker creation.
 * @author Adam Gibson
 *
 */
public interface BrokerStore extends Serializable {
	/**
	 * This will create and return a new broker
	 * @param name the name of the broker
	 * @return a new broker
	 */
	public Broker create(String name);
	
	/**
	 * This will create and return a new broker
	 * @param url the url to connect with
	 * @param name the name of the broker
	 * @return a new broker
	 */
	public Broker create(String url,String name);
	
	/**
	 * This will return a map of the brokers 
	 * mapped by their names.
	 * @return the brokers in this store
	 */
	public Map<String,Broker> brokers();
	
	
	/**
	 * This will return the broker service for this broker store.
	 * @return the broker service for this broker store
	 */
	public BrokerService service();
}
