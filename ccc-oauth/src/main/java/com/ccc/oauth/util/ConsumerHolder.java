package com.ccc.oauth.util;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.ccc.oauth.apimanagement.model.Service;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

public class ConsumerHolder {

	
	
	
	
	public static ConsumerProvider consumerForServiceAndUser(String userName,Service service) {
		ServiceHolder holder = new ServiceHolder(userName,service);
		return map.get(service.getId());
	}
	
	
	public static void putConsumerForUserAndService(String userName,Service service,OAuthConsumer consumer,OAuthProvider provider) {
		ServiceHolder holder = new ServiceHolder(userName,service);
		
		ConsumerProvider holder2 = new ConsumerProvider(consumer,provider);

		map.put(service.getId(),holder2);
	}
	
	
	private static Map<Integer,ConsumerProvider> map = new HashMap<Integer,ConsumerProvider>();
	
}
