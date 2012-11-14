package com.ccc.camelcomponents.oauth.uribuilder;

import java.util.Map;

import com.ccc.camelcomponents.core.api.ParameterRetrievalStrategy;
import com.ccc.camelcomponents.core.api.ServiceURIBuilder;
import com.ccc.oauth.apimanagement.model.Service;
/**
 * This is a base implementation for an ical oauth uri request builder
 * @author Adam Gibson
 *
 */
public abstract class ICalOAuth2UriRequestBuilder implements ServiceURIBuilder {

	@Override
	public abstract Map<String, ParameterRetrievalStrategy> strategies();

	@Override
	public abstract Service service();

	@Override
	public String componentName() {
		return "ical";
	}

	@Override
	public abstract String baseUri();

	@Override
	public  String[] possibleParams() {
		return params;
	}

	@Override
	public abstract Map<String, Boolean> requiredParams();

	@Override
	public abstract String build();

	protected String[] params={"access_token","method"};
	
}
