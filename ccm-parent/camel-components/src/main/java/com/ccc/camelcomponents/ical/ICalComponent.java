package com.ccc.camelcomponents.ical;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
/**
 * This is an ical component for use with calendars.
 * @author Adam Gibson
 *
 */
@Component("iCalComponent")
public class ICalComponent extends DefaultComponent{
	/*Used for spring wiring */
	public ICalComponent() {
		
	}
	
	public ICalComponent(ICalConfig config) {
		this.config=config;
			
	}

	@Override
	protected Endpoint createEndpoint(String uri, String remaining,
			Map<String, Object> parameters) throws Exception {
		
		ICalEndPoint ret = new ICalEndPoint(config,uri,this);
		return ret;
	}

	



	public ICalConfig getConfig() {
		return config;
	}

	public void setConfig(ICalConfig config) {
		this.config = config;
	}


	@Autowired(required=false)
	private ICalConfig config;
	
}//end ICalComponent
