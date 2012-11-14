package com.ccc.camelcomponents.ical.util.impl;

import com.ccc.camelcomponents.ical.util.api.ServerMapper;

public class DefaultServerMapper implements ServerMapper {

	@Override
	public String serviceFor(String uri) {
		if(uri.contains("google"))
			return GOOGLE_DATA;
		
		else if(uri.contains("service") || uri.contains("zimbra"))
			return ZIMBRA;
		
		return null;
	}

}
