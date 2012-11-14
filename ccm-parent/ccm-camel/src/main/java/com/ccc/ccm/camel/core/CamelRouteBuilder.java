package com.ccc.ccm.camel.core;

import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.util.Assert;

import com.ccc.ccm.camel.impl.ProcessorPair;
/**
 * This is a camel route builder that can configure a camel route in multiple ways.
 * @author Adam Gibson
 *
 */
public class CamelRouteBuilder extends RouteBuilder {

	public CamelRouteBuilder(String fromUri,String toUri,Processor processor) {
		this.from=fromUri;
		this.to=toUri;
	}

	public CamelRouteBuilder(CamelRoute route) {
		Assert.notNull(route);
		DestinationPair[] pairs=route.route();
		this.pairs=pairs;
		ProcessorPair[] processorPairs=route.processorPairs();
		this.processorPairs=processorPairs;
	}

	@Override
	public void configure() throws Exception {
		if(to!=null && from!=null) {
			from(from).to(to);

			if(processor!=null) 
				from(to).process(processor);
		}
		else {
			/**
			 * Iterate through the pairs.
			 * Bind each pair such that 
			 * the last to destination
			 * to the current from.
			 */
			String lastDestination=null;
			for(DestinationPair pair : pairs) {
				String from=pair.from();
				String to=pair.to();
				super.from(from).to(to);
				//bridge the last series
				if(lastDestination!=null)
					super.from(lastDestination).to(from);
				lastDestination=to;


			}
			//Join up the processes
			for(ProcessorPair pPair : processorPairs) {
				from(pPair.destinationUri()).process(pPair.processor());
			}

		}
	}
	//from and to pairs for a singleton route builder and a singleton processor
	private String from;

	private String to;

	private Processor processor;

	//mass pairs and processor destinations
	private DestinationPair[] pairs;

	private ProcessorPair[] processorPairs;
	
}//end CamelRouteBuilder
