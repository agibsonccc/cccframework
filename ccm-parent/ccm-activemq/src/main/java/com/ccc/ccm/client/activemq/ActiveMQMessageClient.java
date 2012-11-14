package com.ccc.ccm.client.activemq;

import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.Topic;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.ConnectionContext;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.apache.activemq.broker.jmx.QueueViewMBean;
import org.apache.activemq.command.ActiveMQDestination;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.util.BrokerSupport;
import org.apache.activemq.web.BrokerFacade;
import org.apache.activemq.web.BrokerFacadeSupport;
import org.apache.activemq.web.LocalBrokerFacade;
import org.apache.activemq.web.RemoteJMXBrokerFacade;
import org.apache.activemq.web.config.WebConsoleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.ccm.broker.ActiveMQBrokerStore;
import com.ccc.ccm.broker.BrokerStore;
import com.ccc.ccm.client.BaseMessageClient;
import com.ccc.util.network.NetworkUtils;
/**
 * This is an active mq implementation of message client.
 * @author Adam Gibson
 *
 */
public class ActiveMQMessageClient extends BaseMessageClient   {

	@Override
	public Enumeration getMessagesForQueue(String queueName) throws JMSException {
		Queue q=createQueue(queueName);
		QueueBrowser browser=queueBrowser(q);
		if(browser!=null)
			return browser.getEnumeration();

		return null;
	}

	@Override
	public Session createSession(boolean transacted, int acknowledgable) {
		ConnectionFactory factory=jmsTemplate.getConnectionFactory();
		Connection c=null;
		Session s=null;
		try {
			c = factory.createConnection();
			s=c.createSession(transacted,acknowledgable);
			return s;
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
		return null;
	}//end createSession

	@Override
	public QueueBrowser queueBrowser(Queue queue) {
		ConnectionFactory factory=jmsTemplate.getConnectionFactory();
		Connection c=null;
		Session s=null;
		try {
			c = factory.createConnection();
			s=c.createSession(false,Session.AUTO_ACKNOWLEDGE);
			QueueBrowser ret= s.createBrowser(queue);

			return ret;
		} catch (JMSException e1) {
			e1.printStackTrace();
		}		return null;
	}//end queueBrowser

	/**
	 * 
	 */
	private static final long serialVersionUID = -4153412416761687602L;


	@Override
	public boolean deleteDestination(String destination,String type) throws Exception {
		ActiveMQBrokerStore store=(ActiveMQBrokerStore) brokerStore;
		BrokerService service=store.getBrokerService();
		String url=store.getUrl();

		if(service==null) {
			service = new BrokerService();
			service.setBrokerName(destination);
			// configure the broker
			//Ensure the port is not taken, log a message if it was.
			String port=parsePort(url);
			if(port!=null) {
				if(!NetworkUtils.portTaken(port)) {
					service.addConnector(url);

					service.start();

				}

			}
			if(type.equals(QUEUE)) {
				Queue queue=createQueue(destination);

				try {
					service.removeDestination((ActiveMQDestination) queue);
				

				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}
			else if(type.equals(TOPIC)) {
				Topic topic=createTopic(destination);
				try {
					service.removeDestination((ActiveMQDestination) topic);
					Context c1=contextFetcher.fetch();
					Object o=null;
					try {
						o = c1.lookup(destination);
					} catch (NamingException e2) {
					}
					if(o!=null) 
						c1.unbind(destination);
				} catch (Exception e) {
					e.printStackTrace();
					return false;
				}
			}

			return true;
		}
		return false;
	}//end deleteDestination

	@Override
	public Queue createQueue(String name) {

		Context c1=contextFetcher.fetch();
		Object o=null;
		try {
			o = c1.lookup(name);
		} catch (NamingException e2) {
		}
		if(o!=null) 
			return (Queue) o;
		ConnectionFactory factory=jmsTemplate.getConnectionFactory();
		Connection c=null;
		Session s=null;
		Queue ret=null;
		try {
			c = factory.createConnection();
			s=c.createSession(false,Session.AUTO_ACKNOWLEDGE);
			ret= s.createQueue(name);

		} catch (JMSException e1) {
			e1.printStackTrace();
		}



		getDestinationController().addDestination(ret);
		try {
			context().bind(name, ret);
		} catch (NamingException e) {
			e.printStackTrace();
		}
		return ret;
	}

	/**
	 * This will attempt to cast an object in to an activemq queue and purge it
	 * @param queueName the name of the queue to purge
	 * @throws Exception
	 */
	public void activeMQPurge(String destinationName,String brokerName) throws Exception {
		ActiveMQBrokerStore store=(ActiveMQBrokerStore) brokerStore;
		BrokerService service=store.getBrokerService();
		String url=store.getUrl();
	


	
			if(facade==null) {
				if(url.contains("localhost") || url.contains("0.0.0.0") || url.contains("127.0.0.1")) {
					facade = new  LocalBrokerFacade(service);
				}
				else {
					facade = new RemoteJMXBrokerFacade();
					RemoteJMXBrokerFacade remoteFacade=(RemoteJMXBrokerFacade) facade;
					remoteFacade.setBrokerName(brokerName);
					WebConsoleConfiguration configuration = new WebConsoleConfiguration(){

						@Override
						public ConnectionFactory getConnectionFactory() {
							return jmsTemplate.getConnectionFactory();
						}

						@Override
						public Collection<JMXServiceURL> getJmxUrls() {
							try {
								return Collections.singleton(new JMXServiceURL(jmxUrl));
							} catch (MalformedURLException e) {
								e.printStackTrace();
							}
							return null;
						}

						@Override
						public String getJmxUser() {
							return jmxUser;
						}

						@Override
						public String getJmxPassword() {
							return jmxPassword;
						}

					};
					remoteFacade.setConfiguration(configuration);
				}
			}
			facade.purgeQueue(new ActiveMQQueue(destinationName));
			long size = getQueueSize(destinationName);
			if (size > 0)
				throw new IllegalStateException("It was not possible to clean up the queue '" +  destinationName + "'.");
		
	}
	
	public long getQueueSize(String queueName) {
		try {
			QueueViewMBean queue = facade.getQueue(queueName);
			return (queue != null ? queue.getQueueSize() : 0);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}
	
	
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
	public ConnectionContext adminContext() throws Exception {
		Map<ActiveMQDestination, org.apache.activemq.broker.region.Destination> destinations=brokerStore.service().getBroker().getDestinationMap();
		for(ActiveMQDestination dest : destinations.keySet()) {
			org.apache.activemq.broker.region.Destination dest1=destinations.get(dest);

		}
		if(brokerStore!=null) {
			ConnectionContext context=brokerStore.service().getBroker().getAdminConnectionContext();
			ActiveMQDestination[] dests=context.getBroker().getDestinations();
			return context;
		}
		else return null;
		
	}

	@Override
	public Topic createTopic(String name) {
		ConnectionFactory factory=jmsTemplate.getConnectionFactory();
		Connection c=null;
		Session s=null;
		Topic ret=null;

		Context c1=contextFetcher.fetch();
		Object o=null;
		try {
			o = c1.lookup(name);
		} 
		//naminv convention is off
		catch (NamingException e2) {}
		//not found
		catch(NullPointerException e) {}
		if(o!=null) 
			return (Topic) o;
		try {
			c = factory.createConnection();
			s=c.createSession(false,Session.AUTO_ACKNOWLEDGE);
			ret= s.createTopic(name);

		} catch (JMSException e1) {
			e1.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getDestinationController().addDestination(ret);
		try {
			context().bind(name, ret);
		} catch (NamingException e) {
			e.printStackTrace();
		}

		return ret;
	}
	@Override
	public boolean sendMessage(String text, String destination) {
		try {	
			jmsTemplate.convertAndSend(destination, text);
			return true;
		}

		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}


	public String getJmxUser() {
		return jmxUser;
	}

	public void setJmxUser(String jmxUser) {
		this.jmxUser = jmxUser;
	}

	public String getJmxPassword() {
		return jmxPassword;
	}

	public void setJmxPassword(String jmxPassword) {
		this.jmxPassword = jmxPassword;
	}

	public BrokerFacade getFacade() {
		return facade;
	}

	public void setFacade(BrokerFacade facade) {
		this.facade = facade;
	}

	public BrokerStore getBrokerStore() {
		return brokerStore;
	}

	public String getJmxUrl() {
		return jmxUrl;
	}

	public void setJmxUrl(String jmxUrl) {
		this.jmxUrl = jmxUrl;
	}
	
	public void setBrokerStore(BrokerStore brokerStore) {
		this.brokerStore = brokerStore;
	}
	@Autowired(required=false)
	private BrokerStore brokerStore;


	

	private String jmxUrl;

	private String jmxUser;

	private String jmxPassword;

	private BrokerFacade facade;

}//end ActiveMQMessageClient
