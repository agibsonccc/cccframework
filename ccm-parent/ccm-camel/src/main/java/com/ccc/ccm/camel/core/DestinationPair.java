package com.ccc.ccm.camel.core;
/**
 * This is meant to represent a camel route on a to and from.
 * @author Adam Gibson
 *
 */
public interface DestinationPair {

	/**
	 * This will represent the to destination on a camel route.
	 * @return the to destination
	 */
	public String to();
	/**
	 * This represents a from destination
	 * @return the from of this route pair.
	 */
	public String from();
	/**
	 * This will add a from to this pair
	 * @param from the from to set for this pair
	 */
	public void setFrom(String from);
	
	/**
	 * This will set the to destination for this pair.
	 * @param to the to destination for this pair.
	 */
	public void setTo(String to);
}//end DestinationPair
