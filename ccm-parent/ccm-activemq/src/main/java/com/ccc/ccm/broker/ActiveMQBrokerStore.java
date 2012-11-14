package com.ccc.ccm.broker;

import java.util.HashMap;
import java.util.Map;

import org.apache.activemq.broker.Broker;
import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;

import com.ccc.util.network.NetworkUtils;
/**
 * This is an active mq implementation to store brokers.
 * When creating the brokers using this class, this class will transparently, check for whether
 * there is already something listening on the port. If nothing is listening, it will start a broker it creates
 * otherwise it will just log a message warning that something was already there and the user will have to start it themselves.
 * @author Adam Gibson
 *
 */
public class ActiveMQBrokerStore implements BrokerStore  {



	/**
	 * 
	 */
	private static final long serialVersionUID = -2100935766282990521L;
	@Override
	public Broker create(String url,String name) {

		Broker b=brokers.get(name);
		if(b!=null)
			return b;
		brokerService = new BrokerService();
		try {
			// configure the broker
			brokerService.addConnector(url);
			brokerService.setBrokerName(name);
			//Ensure the port is not taken, log a message if it was.
			String port=parsePort(url);
			if(port!=null) {
				if(!NetworkUtils.portTaken(port)) {
					brokerService.start();

				}
				else {
					log.warn("Broker for port: " + port + " was already started. If this was not intentional, please start the returned broker yourself.");
				}
			}

			brokers.put(name, brokerService.getBroker());
			return brokerService.getBroker();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}//end create
	/* Isolates the port in the passed in url */
	private String parsePort(String url) {
		int i=url!=null ? url.lastIndexOf(':')+1 : 1;
		if(i==-1)
			return null;
		else {
			StringBuffer sb = new StringBuffer();
			for(;i<url.length();i++) {
				sb.append(url.charAt(i));
			}
			return sb.toString();
		}
	}

	@Override
	public Broker create(String name) {
		Broker b=brokers.get(name);
		if(b!=null)
			return b;
		 brokerService = new BrokerService();
		try {
			brokerService.setBrokerName(name);
			// configure the broker
			brokerService.addConnector(url);
			brokers.put(name, brokerService.getBroker());
			//Ensure the port is not taken, log a message if it was.
			String port=parsePort(url);
			if(port!=null) {
				if(!NetworkUtils.portTaken(port)) {
					brokerService.start();

				}
				else {
					log.warn("Broker for port: " + port + " was already started. If this was not intentional, please start the returned broker yourself.");
				}
			}



			return brokerService.getBroker();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}//end create

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}


	@Override
	public Map<String, Broker> brokers() {
		return brokers;
	}
	
	
	public BrokerService getBrokerService() {
		return brokerService;
	}
	
	@Override
	public BrokerService service() {
		return brokerService;
	}

	private Map<String,Broker> brokers = new HashMap<String,Broker>();
	private String url;
	private static Logger log=Logger.getLogger(ActiveMQBrokerStore.class);
	private BrokerService brokerService;


}
