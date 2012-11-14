package com.ccc.camelcomponents.routes;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.SSLProtocolSocketFactory;

import com.ccc.camelcomponents.core.base.ParamFormatter;
import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.OAuth2Urls;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.util.HttpAuthUtils;
/**
 * This is a route builder for processing oauth 2 requests. You can pass it all of the parameters necessary and this route will handle the
 * footwork for handling the data processing request.
 * @author Adam Gibson
 *
 */
public class OAuthRouteBuilder extends RouteBuilder {

	public OAuthRouteBuilder(OAuth2Service oauthService,Service service,String userName,String method,Processor responseReceiver,Class result,boolean httpClient4,String destinationUri) {
		this.method=method;
		this.responseReceiver=responseReceiver;
		this.resultClass=result;
		this.service=service;
		this.userName=userName;
		this.oauthService=oauthService;
		this.httpClient4=httpClient4;
		this.destinationUri=destinationUri;
		
	}

	public OAuthRouteBuilder(OAuth2Service oauthService,Service service,String userName,String method,Processor responseReceiver,Class result,boolean httpClient4) {
		this.method=method;
		this.responseReceiver=responseReceiver;
		this.resultClass=result;
		this.service=service;
		this.userName=userName;
		this.oauthService=oauthService;
		this.httpClient4=httpClient4;
		
	}
	
	public OAuthRouteBuilder(OAuth2Service oauthService,Service service,String userName,String method,Processor responseReceiver,Class result,boolean httpClient4,Map<String,String> paramReplace) {
		this.method=method;
		this.responseReceiver=responseReceiver;
		this.resultClass=result;
		this.service=service;
		this.userName=userName;
		this.oauthService=oauthService;
		this.httpClient4=httpClient4;
		this.paramReplace=paramReplace;
		
	}
	@Override
	public void configure() throws Exception {
		AccessKeyForUser key=HttpAuthUtils.requestAccessWithLookup(oauthService, userName,service.getId());
		OAuth2Urls urls=oauthService.urlsForService(service);
		if(httpClient4) {
			dataUri=urls.getDataUrl();
			if(httpClient4) {
				if(dataUri.contains("https") && !dataUri.contains("https4"))
					dataUri=dataUri.replace("https","https4");
				else if(!dataUri.contains("http4")) dataUri=dataUri.replace("http","http4");
			}
		}
		accessToken=key.getAccessCode();
		destinationUri=dataUri;
	
		if(destinationUri!=null) {
		
			/**
			 * Make a post request with the most up to date access token at the data uri for this service.
			 * When the response code is 200 unmarshal the json object with the passed in processor.
			 * This processor will should be able to handle saving of the json and processing of the error.
			 */
			String  accessHeader=HttpAuthUtils.OAUTH2_ACCESS_TOKEN + "=" + key.getAccessCode();

			if(paramReplace!=null) {
				destinationUri=ParamFormatter.replace(service, destinationUri, paramReplace);
			}
			
			
			
			from("direct:start")
			.setHeader(Exchange.HTTP_METHOD, constant(method)).to(dataUri + "?" + accessHeader);
			
			from(dataUri + "?" + accessHeader).marshal().json(JsonLibrary.Jackson).to("direct:marshalled");
			
			//
			//take the json and post it to the response receiver
			from("direct:marshalled").unmarshal().json(JsonLibrary.Jackson, resultClass).to("direct:end").process(responseReceiver);

		}
		
		else {
			if(paramReplace!=null) {
				destinationUri=ParamFormatter.replace(service, destinationUri, paramReplace);
			}
			
			/**
			 * Make a post request with the most up to date access token at the data uri for this service.
			 * When the response code is 200 unmarshal the json object with the passed in processor.
			 * This processor will should be able to handle saving of the json and processing of the error.
			 */
			String  accessHeader=HttpAuthUtils.OAUTH2_ACCESS_TOKEN + "=" + key.getAccessCode();

		
			from("direct:start")
			.setHeader(Exchange.HTTP_METHOD, constant(method)).to(dataUri + "?" + accessHeader).
			marshal().json(JsonLibrary.Jackson).to("direct:marshalled").unmarshal().json(JsonLibrary.Jackson, resultClass).to("direct:end").process(responseReceiver);

	
		}
		

	}


	protected boolean httpClient4;
	protected String accessToken;

	protected String dataUri;

	protected String method;

	protected Processor responseReceiver;

	protected Predicate notOk=header(Exchange.HTTP_RESPONSE_CODE).isNotEqualTo(200);

	protected Predicate contentIsJSON=header(Exchange.CONTENT_TYPE).isEqualTo("application/json");

	
	protected Class resultClass;

	protected OAuth2Service oauthService;

	protected Service service;

	protected String destinationUri;
	
	protected String userName;
	protected Map<String,String> paramReplace;
}
