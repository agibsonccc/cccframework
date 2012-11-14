package com.ccc.camelcomponents.routes;

import java.util.Map;

import oauth.signpost.OAuthConsumer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import com.ccc.camelcomponents.core.base.ParamFormatter;
import com.ccc.camelcomponents.processors.FileProcessor;
import com.ccc.camelcomponents.processors.OAuth1FileProcessor;
import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.OAuth1Info;
import com.ccc.oauth.apimanagement.model.OAuth1RequestToken;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.util.HttpAuthUtils;

public class OAuth1RouteBuilder extends OAuthRouteBuilder  {
	
	public OAuth1RouteBuilder(OAuth2Service oauthService, Service service,
			String userName, String method, Processor responseReceiver,
			Class result, boolean httpClient4, Map<String, String> paramReplace) {
		super(oauthService, service, userName, method, responseReceiver, result,
				httpClient4, paramReplace);
		consumer=HttpAuthUtils.getRequestingConsumer(service, userName, oauthService);

	}

	public OAuth1RouteBuilder(OAuth2Service oauthService, Service service,
			String userName, String method, Processor responseReceiver,
			Class result, boolean httpClient4, String destinationUri) {
		super(oauthService, service, userName, method, responseReceiver, result,
				httpClient4, destinationUri);
		consumer=HttpAuthUtils.getRequestingConsumer(service, userName, oauthService);

	}

	public OAuth1RouteBuilder(OAuth2Service oauthService, Service service,
			String userName, String method, Processor responseReceiver,
			Class result, boolean httpClient4) {
		super(oauthService, service, userName, method, responseReceiver, result,
				httpClient4);
		consumer=HttpAuthUtils.getRequestingConsumer(service, userName, oauthService);
	}

	@Override
	public void configure() throws Exception {
		OAuth1Info info=oauthService.oauthInfoForService(service);
		OAuth1RequestToken token=oauthService.mostRecentOauthRequestTokenForUserAndService(service, userName);
		if(info==null || token==null) throw new IllegalStateException("Info or token can't be null");
		if(httpClient4) {
			dataUri=info.getDataUrl();
			if(httpClient4) {
				if(dataUri.contains("https") && !dataUri.contains("https4"))
					dataUri=dataUri.replace("https","https4");
				else if(!dataUri.contains("http4")) dataUri=dataUri.replace("http","http4");
			}
		}
		if(paramReplace!=null) {
			destinationUri=ParamFormatter.replace(service, dataUri, paramReplace);
		}
		if(responseReceiver!=null) {
			from("direct:start")
			.setHeader(Exchange.HTTP_METHOD, constant(method)).process(new OAuth1FileProcessor(consumer)).to(destinationUri).process(new OAuth1FileProcessor(consumer)).
			marshal().json(JsonLibrary.Jackson).to("direct:marshalled");
			//take the json and post it to the response receiver
			from("direct:marshalled").unmarshal().json(JsonLibrary.Jackson, resultClass).to("direct:end").process(responseReceiver);
			
		}
		else {
			from("direct:start")
			.setHeader(Exchange.HTTP_METHOD, constant(method)).process(new OAuth1FileProcessor(consumer)).to(destinationUri).process(new OAuth1FileProcessor(consumer)).
			marshal().json(JsonLibrary.Jackson).to("direct:marshalled");
			//take the json and post it to the response receiver
			from("direct:marshalled").unmarshal().json(JsonLibrary.Jackson, resultClass).to("direct:end");
			
		}
	}

	
	
	
	private String fileParamType;
	
	private boolean overWrite;
	
	private OAuthConsumer consumer;
	
	
}
