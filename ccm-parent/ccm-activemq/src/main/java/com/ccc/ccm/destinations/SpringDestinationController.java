package com.ccc.ccm.destinations;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MessageListener;

import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.support.JmsAccessor;

/**
 * This adds spring specific functionality to the destination controller.
 * @author Adam Gibson
 *
 */
public class SpringDestinationController extends BaseDestinationController  {

	/**
	 * This will return a container for use in the jms template.
	 * @param m the message listener to use
	 * @param factory the connection factory to use
	 * @param d the destination to use
	 * @return a container for use in the jms template.
	 */
	public JmsAccessor getContainerForListener(MessageListener m,ConnectionFactory factory,Destination d) {
		DefaultMessageListenerContainer ret = new DefaultMessageListenerContainer();
		ret.setConnectionFactory(factory);
		ret.setMessageListener(m);
		ret.setDestination(d);

		ret.initialize();
		return ret;
	}
}
