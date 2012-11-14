package com.ccc.ccm.destinations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.Topic;
/**
 * This is a base destination controller for use with
 * managing destinations at run time.
 * @author Adam Gibson
 *
 */
public abstract class BaseDestinationController implements DestinationController {

	@Override
	public Set<Destination> destinations() {
		Set<Destination> ret = new HashSet<Destination>();
		for(String s : destinationInfo.keySet()) {
			Map.Entry<Destination,MessageListener> entry=destinationInfo.get(s);
			ret.add(entry.getKey());

		}
		return ret;
	}

	@Override
	public void addDestination(Destination destination) {
		destinations.add(destination);
	}

	@Override
	public void removeDestination(Destination destination) {
		destinations.remove(destination);
	}

	@Override
	public Destination destinationWithName(String name) {
		Destination ret= destinationInfo.get(name).getKey();
		if(ret==null) {
			for(Destination d : destinations) {
				String match=null;
				if(d instanceof Queue) {
					Queue q=(Queue) d;
					try {
						match=q.getQueueName();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
				if(d instanceof Topic) {
					Topic topic=(Topic) d;
					try {
						match=topic.getTopicName();
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
				if(match!=null && match.equals(name))
					return d;
			}
		}
		else return ret;
	
		return null;
	}
	

	@Override
	public DestinationMatcher matcher() {
		return destinationMatcher;
	}

	@Override
	public void setMatcher(DestinationMatcher matcher) {
		destinationMatcher=matcher;
	}

	@Override
	public Set<MessageListener> listeners() {
		Set<MessageListener> ret = new HashSet<MessageListener>();
		for(String s : destinationInfo.keySet()) {
			Map.Entry<Destination,MessageListener> entry=destinationInfo.get(s);
			ret.add(entry.getValue());

		}
		return ret;
	}



	@Override
	public void put(final Destination d, final MessageListener m) {
		String name=null;
		if(d instanceof Queue) {
			Queue q=(Queue) d;
			try {
				name=q.getQueueName();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		if(d instanceof Topic) {
			Topic topic=(Topic) d;
			try {
				name=topic.getTopicName();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		destinationInfo.put(name,new Map.Entry<Destination, MessageListener>(){
			{
			}

			@Override
			public Destination getKey() {
				return d;
			}

			@Override
			public MessageListener getValue() {
				return m;
			}

			@Override
			public MessageListener setValue(MessageListener value) {
				this.value=value;
				return value;
			}

			private MessageListener value;
		});
	}

	@Override
	public Map<String, Entry<Destination, MessageListener>> destinationIndex() {
		return destinationInfo;
	}

	private Set<Destination> destinations = new HashSet<Destination>();


	private Map<String,Map.Entry<Destination,MessageListener>> destinationInfo = new HashMap<String,Map.Entry<Destination,MessageListener>>();

	private DestinationMatcher destinationMatcher;

}
