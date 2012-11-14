package com.ccc.camelcomponents.ical.util.impl;

import com.ccc.camelcomponents.ical.util.api.MethodServicemapper;
import com.ccc.camelcomponents.ical.util.api.ServerMapper;

public class DefaultMethodServiceMapper implements MethodServicemapper {

	@Override
	public String methodFor(String uri) {
		ServerMapper mapper = new DefaultServerMapper();

		String service=mapper.serviceFor(uri);
		
		if(service==null)
			return null;
		if(service.equals(ServerMapper.GOOGLE_DATA))
			return "post";
		else if(service.equals(ServerMapper.ZIMBRA))
			return "get";
		return null;
	}

}
