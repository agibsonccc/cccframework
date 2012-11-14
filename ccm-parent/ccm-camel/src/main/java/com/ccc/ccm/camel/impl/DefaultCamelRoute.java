package com.ccc.ccm.camel.impl;

import java.util.ArrayList;
import java.util.List;

import com.ccc.ccm.camel.core.CamelRoute;
import com.ccc.ccm.camel.core.DestinationPair;
/**
 * This is a default camel route.
 * @author Adam Gibson
 *
 */
public class DefaultCamelRoute implements CamelRoute {

	@Override
	public void addPair(DestinationPair pair) {
		route.add(pair);
	}

	@Override
	public void addProcessorPair(ProcessorPair pair) {
		processorPairs.add(pair);
	}
	
	@Override
	public DestinationPair[] route() {
		DestinationPair[] ret = new DestinationPair[route.size()];
		for(int i=0;i<ret.length;i++) {
			ret[i]=route.get(i);
		}
		
		return ret;
	}

	@Override
	public ProcessorPair[] processorPairs() {
		ProcessorPair[] ret = new ProcessorPair[processorPairs.size()];
		for(int i=0;i<ret.length;i++) {
			ret[i]=processorPairs.get(i);
		}
		
		return null;
	}
	
	
	private List<DestinationPair> route = new ArrayList<DestinationPair>();

	
	private List<ProcessorPair> processorPairs = new ArrayList<ProcessorPair>();

}//end DefaultCamelRoute
