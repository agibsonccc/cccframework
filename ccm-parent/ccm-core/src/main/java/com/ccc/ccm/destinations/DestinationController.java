package com.ccc.ccm.destinations;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.MessageListener;
/**
 * This is an interface to manage destinations for 
 * dynamic queues and topics in JMS.
 * @author Adam Gibson
 *
 */
public interface DestinationController extends Serializable {

	/**
	 * This returns the set of destinations this
	 * controller manages.
	 * @return the set of destinations this controller manages
	 */
	public Set<Destination> destinations();
	
	/**
	 * This adds a destination to the controller
	 * @param destination the destination to add
	 */
	public void addDestination(Destination destination);
	
	/**
	 * This removes a destination from the controller
	 * @param destination the destination to remove
	 */
	public void removeDestination(Destination destination);
	
	/**
	 * This will retrieve the destination with the given name. 
	 * @param name the name of the destination add
	 * @return the destination with the given name
	 */
	public Destination destinationWithName(String name);
	/**
	 * This will return a destination matcher for this controller.
	 * @return a destination matcher for this controller.
	 */
	public DestinationMatcher matcher();
	
	/**
	 * Setter for the matcher
	 * @param matcher the matcher to use for this controller
	 */
	public void setMatcher(DestinationMatcher matcher);	
	/**
	 * This specifies a set of message listeners that wrap around
	 * destinations
	 * @return the set of listeners for this controller
	 */
	public Set<MessageListener> listeners();

	/**
	 * This will put the given destination in to the index.
	 * @param d the desination to put in the index
	 * @param m the message listener to map to
	 */
	public void put(Destination d,MessageListener m);
	
	/**
	 * This will return the index of destination names to
	 * destinations and their respective listeners.
	 * @return index of destination names to
	 * destinations and their respective listeners.
	 */
	public Map<String,Map.Entry<Destination,MessageListener>> destinationIndex();
}//end DestinationController
