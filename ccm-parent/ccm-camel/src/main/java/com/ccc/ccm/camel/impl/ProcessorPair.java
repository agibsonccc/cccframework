package com.ccc.ccm.camel.impl;

import org.apache.camel.Processor;
/**
 * This is a processor pair that handles mappings of uris to processors.
 * @author Adam Gibson
 *
 */
public interface ProcessorPair {

	
	/**
	 * This is the destination uri for this processor pair.
	 * @return the desitnation uri for this processor pair
	 */
	public String destinationUri();
	/**
	 * This is the processor for this pair.
	 * @return the processor for this pair.
	 */
	public Processor processor();
	
	/**
	 * Setter for the processor of this pair
	 * @param processor the processor to use
	 */
	public void setProcessor(Processor processor);
	/**
	 * This sets the destination uri for this pair.
	 * @param uri the uri for this pair
	 */
	public void setDestinationUri(String uri);
	
}//end ProcessorPair


