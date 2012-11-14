package com.ccc.ccm.openmq.core;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.Topic;
import com.sun.messaging.*;
import com.sun.messaging.jmq.jmsclient.QueueSessionImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccc.ccm.client.BaseJMSMessageClient;

public class OpenMQMessageClient extends BaseJMSMessageClient  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6977314815047282757L;

	@Override
	public Enumeration<?> getMessagesForQueue(String queueName)
			throws JMSException {
		Queue q=createQueue(queueName);
		QueueBrowser browser=queueBrowser(q);
		if(browser!=null)
			return browser.getEnumeration();

		return null;
	}

	@Override
	public Session createSession(boolean transacted, int acknowledgable) {
		javax.jms.ConnectionFactory factory=this.jmsTemplate.getConnectionFactory();
		if(factory instanceof TopicConnectionFactory) {
			TopicConnectionFactory topicFactory=(TopicConnectionFactory) factory;
			try {
				Connection connection=topicFactory.createConnection();
				return connection.createSession(transacted, acknowledgable);
			} catch (JMSException e) {
				log.error("Error creating connection for session",e);
			}

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
		}		
		return null;
	}

	@Override
	public Queue createQueue(String name) {
		try {
			return new com.sun.messaging.Queue(name);
		} catch (JMSException e) {
			log.error("Error creating open mq queue with name: " + name,e);
		}
		return null;
	}

	@Override
	public Topic createTopic(String name) {
		try {
			return new com.sun.messaging.Topic(name);
		} catch (JMSException e) {
			log.error("Error creating open mq topic with name: " + name,e);
		}
		return null;
	}

	@Override
	public boolean deleteDestination(String destination, String type)
			throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	private static Logger log=LoggerFactory.getLogger(OpenMQMessageClient.class);
}
