package com.ccc.ccm.camel.core;

import com.ccc.ccm.camel.impl.ProcessorPair;

/**
 * This is a route for destination pairs. 
 * Each index is a destination pair such that the route 
 * would move from beginning to end and the next beginning pair in the array maps to another
 * destination.
 * @author Adam Gibson
 *
 */
public interface CamelRoute {
	
	/**
	 * This will add a pair to this route.
	 * @param pair the pair to add to this route
	 */
	public void addPair(DestinationPair pair);
	
	/**
	 * This will add a processor pair to this route.
	 * @param pair the processor pair to add
	 */
	public void addProcessorPair(ProcessorPair pair);
	
	/**
	 * This will return the route for this destination pair. 
	 * Each to maps to another from in the route. You can then process
	 * the route much like a graph traversing from node to node.
	 * @return the destination pairs of this route
	 */
	public DestinationPair[] route();
	
	/**
	 * These are the processor pairs for each uri
	 * @return the processor pairs for this route
	 */
	public ProcessorPair[] processorPairs();
}//end CamelRoute
