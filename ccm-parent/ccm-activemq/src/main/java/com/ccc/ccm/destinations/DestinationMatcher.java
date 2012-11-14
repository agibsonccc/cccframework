package com.ccc.ccm.destinations;

import javax.jms.Destination;

/**
 * This is a matcher meant to be used by destination controllers
 * to determine the rules for destinations that should be managed.
 * @author Adam Gibson
 *
 */
public interface DestinationMatcher {

	/**
	 * This returns whether the given name matches
	 * the destination rule for this matcher
	 * @param destinationName the destination name to 
	 * match against
	 * @return true if it matches, false if null or doesn't 
	 * match
	 */
	public boolean matches(String destinationName);
	
	
	/**
	 * This returns whether the given name matches
	 * the destination rule for this matcher
	 * @param destination the destination name to 
	 * match against
	 * @return true if it matches, false if null or doesn't 
	 * match
	 */
	public boolean matches(Destination destination);
	
	
}
