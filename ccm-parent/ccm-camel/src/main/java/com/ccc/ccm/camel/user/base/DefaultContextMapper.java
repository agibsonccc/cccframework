package com.ccc.ccm.camel.user.base;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;

import com.ccc.ccm.camel.user.api.UserCamelContextMapper;

public class DefaultContextMapper implements UserCamelContextMapper  {

	@Override
	public CamelContext getContext(String userName) {
		return map.get(userName);
	}

	@Override
	public CamelContext removeContext(String userName) {
		return map.remove(userName);
	}

	@Override
	public void addContext(String userName, CamelContext context) {
		map.put(userName,context);
	}

	@Override
	public void addRoutesToContext(String userName, RouteBuilder builder) throws Exception {
		CamelContext context=map.get(userName);
		if(context!=null) context.addRoutes(builder);
			
	}

	
	
	
	private Map<String,CamelContext> map = new HashMap<String,CamelContext>();




	
}
