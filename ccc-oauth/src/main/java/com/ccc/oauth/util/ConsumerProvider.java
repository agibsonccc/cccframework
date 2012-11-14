package com.ccc.oauth.util;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;

public class ConsumerProvider {

	public ConsumerProvider(OAuthConsumer consumer, OAuthProvider provider) {
		super();
		this.consumer = consumer;
		this.provider = provider;
	}

	public OAuthConsumer getConsumer() {
		return consumer;
	}

	public void setConsumer(OAuthConsumer consumer) {
		this.consumer = consumer;
	}

	public OAuthProvider getProvider() {
		return provider;
	}

	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	private OAuthConsumer consumer;
	
	private OAuthProvider provider;
}
