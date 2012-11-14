package com.ccc.camelcomponents.ical.uriconfigurator;

import java.util.HashMap;
import java.util.Map;

import com.ccc.camelcomponents.core.api.ServiceConstants;
import com.ccc.camelcomponents.core.api.URIConfigurator;

public class ICalUriConfigurator  implements URIConfigurator {

	@Override
	public String uri(String type, Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	private Map<String,String> baseUrlsForService() {
		Map<String,String> ret = new HashMap<String,String>();
		
		
		return ret;
	}
	
	
	private boolean requiresBaseUrl(String service) {
		if(service==null) return true;
		else {
			boolean requires=service.equals(ZIMBRA) || service.equals(SHAREPOINT) || service.equals(EXCHANGE);
			return requires;
		}
		
	}
}
