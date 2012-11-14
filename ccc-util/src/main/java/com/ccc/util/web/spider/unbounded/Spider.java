/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;

import com.ccc.util.datastructure.graph.interfaces.Graph;


/**
 * This is a spider that will crawl across various locations and stretch itself to other locations as it finds valid
 * locations to go to.
 * @author Adam Gibson
 *
 */
public interface Spider {
	/**
	 * This will stretch the spider to the given location.
	 * @param location the location to stretch to
	 * @throws IllegalArgumentException if location is null
	 */
	public void crawl(Location location) throws IllegalArgumentException;

	/**
	 * This will harvest information that the spider collects.
	 */
	public void harvestInfo();
	
	/**
	 * This returns whether the given location is valid or not.
	 * @param location the location to test
	 * @return true if the location is valid, false otherwise
	 */
	public boolean validLocation(Location location);
	
	/**
	 * This returns a graph of locations where the spider has traveled.
	 * @return the path of where the spider has traveled.
	 */
	public Graph<Location,Location> traveled();
	
	/**
	 * This tells whether the spider is done crawling or not.
	 * @return true if it harvested all of the information in it's current location,
	 * false otherwise
	 */
	public boolean doneCrawling();
	
	/**
	 * This allows the spider to make observations about it's current location in the system.
	 */
	public void makeObservations();

}
