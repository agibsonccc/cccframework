package com.ccc.camelcomponents.ical.tests;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Session;
import javax.mail.Store;

import net.fortuna.ical4j.model.Calendar;

import oauth.signpost.OAuthConsumer;

import org.apache.camel.CamelContext;
import org.apache.camel.Component;
import org.apache.camel.ConsumerTemplate;
import org.apache.camel.Endpoint;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.ExchangePattern;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.Produce;
import org.apache.camel.Producer;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.http4.HttpComponent;
import org.apache.camel.component.http4.HttpEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ccc.camelcomponents.core.base.ParamFormatter;
import com.ccc.camelcomponents.ical.ICalComponent;
import com.ccc.camelcomponents.ical.ICalConfig;
import com.ccc.camelcomponents.ical.ICalConsumer;
import com.ccc.camelcomponents.ical.ICalEndPoint;
import com.ccc.camelcomponents.ical.ICalProcessor;
import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.camelcomponents.ical.util.api.MethodServicemapper;
import com.ccc.camelcomponents.processors.FileProcessor;
import com.ccc.camelcomponents.processors.OAuth1FileProcessor;
import com.ccc.camelcomponents.routes.OAuth1RouteBuilder;
import com.ccc.camelcomponents.routes.OAuthRouteBuilder;
import com.ccc.clevmail.mailheaders.MailHeaders;
import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.oauth.api.OAuth2Service;
import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.Contact;
import com.ccc.oauth.apimanagement.model.OAuth1AccessToken;
import com.ccc.oauth.apimanagement.model.OAuth1Info;
import com.ccc.oauth.apimanagement.model.OAuth1RequestToken;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.oauth.util.HttpAuthUtils;

import edu.emory.mathcs.backport.java.util.Collections;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/ccc/camelcomponents/ical/tests/RouteTester-context.xml" })
public class RouteTester extends AbstractJUnit4SpringContextTests {

	@Produce(uri="ical:camel@clevercloudcomputing.com?ssl=true&userName=camel&password=camel")
	protected ProducerTemplate producerTemplate;
	@Autowired
	private CamelContext camelContext;
	private String body2;
	private static Logger log=LoggerFactory.getLogger(RouteTester.class);
	//@EndpointInject(uri="ical:http://www.huskymail.mtu.edu/home/aegibson/?method=GET&amp;userName=aegibson&amp;password=password&amp;params={hello:hi,yep:no}")
	@EndpointInject(uri="ical:http://www.huskymail.mtu.edu/home/aegibson/?method=GET&userName=aegibson&password=password&params={hello:hi,yep:no}")
	protected ICalEndPoint urlEndPoint;
	@Autowired
	protected ICalComponent component;
	@Autowired
	private MailClient mailClient;
	private String body;
	@Autowired
	@Qualifier("cccIn")
	private Server incoming;
	@Autowired
	@Qualifier("cccOut")
	private SMTPServer outgoing;
	@Autowired
	private MailHeaders mailHeaders;
	@Autowired
	private OAuth2Service oauth2Service;
	@Autowired
	private ICalConfig icalConfig;

	private int facebookId=13;
	//@Test
	//@DirtiesContext
	public void testEmailRouting() throws Exception {
		/*
		Calendar calendar=ICalUtils.buildCalendar("src/main/resources/calendar.ics");

		String uri="ical:camel@clevercloudcomputing.com?ssl=true&userName=camel&password=camel";
		Producer producer=camelContext.getEndpoint(uri).createProducer();
		Exchange exchange=producer.createExchange(ExchangePattern.InOut);
		Message in=exchange.getIn();
		Message out=exchange.getOut();
		in.setBody(calendar.toString());
		out.setBody(calendar.toString());
		out.setHeader(MailConstants.TO_ADDRESSES, "agibson@clevercloudcomputing.com");
		producer.start();
		producer.process(exchange);
		 */
		Session s=mailClient.login(headersForCCC(), incoming);

		Store store=s.getStore("imap");
		if(!store.isConnected())
			store.connect("demo.clevercloudcomputing.com",143, "agibson", "destrotroll%5");


		Folder f=store.getFolder("INBOX");
		f.open(Folder.READ_WRITE);

		javax.mail.Message[] messages=f.getMessages();

		for(javax.mail.Message m : messages) {
			Address[] from=m.getFrom();

			for(Address a : from)
				if(a.toString().equals("camel@clevercloudcomputing.com"))
					mailClient.deleteMessage(m);

		}




	}

	public void setBody(String body) {
		this.body=body;
	}
	private RouteBuilder syncRouteBuilder() {

		String getFileUri="ical:https://www.huskymail.mtu.edu/home/aegibson/calendar?ssl=true&userName=aegibson&method=post&password=goblinhacker%255&params={fmt:ics,auth:ba}";

		RouteBuilder builder = new RouteBuilder() {

			@Override
			public void configure() throws Exception {

			}

		};
		return builder;
	}
	@Test
	public void testUrls() throws JSONException {
		String params=urlEndPoint.getParams();
		Assert.notNull(params);
		Map<String,String> mapParams=urlEndPoint.params();
		Assert.notNull(mapParams);
		Assert.isTrue(!mapParams.isEmpty());
		String encodedUrl=ICalUtils.urlEncodedParams(urlEndPoint.getAddress());
		Assert.notNull(encodedUrl);
		Assert.isTrue(!encodedUrl.isEmpty());
		String straightUrl=urlEndPoint.urlWithParams();
		Assert.notNull(straightUrl);
		Assert.isTrue(!straightUrl.isEmpty());
		String comp="http://www.huskymail.mtu.edu/home/aegibson/?method=GET&%7D&password=password&userName=aegibson&hello=hi&yep=no";
		Assert.isTrue(straightUrl.equals(comp),"Straight url: " + straightUrl + " did not equal " + comp);
		String urlWithOnlyParams=ICalUtils.urlEncodedParams(urlEndPoint.getParams());
		String expected="http://www.huskymail.mtu.edu/home/aegibson/?hello=hi&yep=no";
		Assert.isTrue(expected.equals(urlWithOnlyParams));
	}


	public ICalConfig getIcalConfig() {
		return icalConfig;
	}


	public void setIcalConfig(ICalConfig icalConfig) {
		this.icalConfig = icalConfig;
	}


	@Test
	@Ignore
	public void testSync() throws Exception {
		final String getFileUri="ical://https://www.huskymail.mtu.edu/home/aegibson/calendar?ssl=true&userName=aegibson&method=post&password=goblinhacker%255&params={fmt:ics,auth:ba}";
		ICalConfig getFile=icalConfig.clone();
		getFile.setAddress(getFileUri);
		getFile.setAuthType(HttpAuthUtils.BASIC);
		getFile.setMethod(MethodServicemapper.GET);
		getFile.setUserName("aegibson");
		Service huskyMail=oauth2Service.serviceWithId(10);
		getFile.setPassword("goblinhacker%5");
		getFile.setService(huskyMail);
		Service lookup=oauth2Service.serviceWithId(2);
		AccessKeyForUser access=oauth2Service.mostRecentAccessKeyForUserAndService(lookup, "agibson");
		final String sendTo="ical://https://www.googleapis.com/calendar/v3/calendars/primary/events/import?method=post&ssl=true&params={access_token:" + access.getAccessCode() + "}";
		RouteBuilder builder = new RouteBuilder() {
			public void configure() {
				from(getFileUri).to(sendTo);
			}
		};
		ICalConfig google=icalConfig.clone();
		google.setAddress(sendTo);
		google.setMethod("post");
		google.setAuthType(HttpAuthUtils.OAUTH2);
		google.setService(lookup);
		DefaultCamelContext context = new DefaultCamelContext();

		ICalComponent c2 = new ICalComponent(getFile);
		context.addRoutes(builder);
		c2.setCamelContext(context);
		Endpoint file=c2.createEndpoint(getFileUri);
		Endpoint send=c2.createEndpoint(sendTo);
		ICalEndPoint googleEndPoint=(ICalEndPoint) send;

		getFile.setEndPoint((ICalEndPoint) file);
		google.setEndPoint((ICalEndPoint) googleEndPoint);
		context.addComponent("ical", c2);
		context.addEndpoint(getFileUri, file);
		context.addEndpoint(sendTo,send);
		final Producer producer=context.getEndpoint(getFileUri).createProducer();
		google.setMethod("post");

		ICalProcessor googleProcessor = new ICalProcessor(google);
		googleProcessor.setIcalConfig(google);

		final ICalConsumer consumer=(ICalConsumer) context.getEndpoint(sendTo).createConsumer(googleProcessor);

		Exchange exchange=producer.createExchange(ExchangePattern.InOut);
		Message in=exchange.getIn();
		Message out=exchange.getOut();
		in.setBody("sync");
		out.setBody("sync");
		context.start();
		producer.start();
		ProducerTemplate template=context.createProducerTemplate();
		Exchange calendar=template.send(context.getEndpoint(getFileUri),exchange);
		template.send(context.getEndpoint(sendTo),calendar);

		googleProcessor.process(calendar);
		//	producer.process(exchange);
		//consumer.onExchange(exchange);


	}

	@Test
	@Ignore
	public void testFacebookContacts() throws Exception {
		Service facebookContacts=oauth2Service.serviceWithId(facebookId);
		Service postOnMyWall=oauth2Service.serviceWithId(14);
		
		final DefaultCamelContext context = new DefaultCamelContext();
		String userName="agibson";
		String dataUri=HttpAuthUtils.buildDataUri(oauth2Service, userName, facebookContacts,true);
		String dataUri2=HttpAuthUtils.buildDataUri(oauth2Service, userName, postOnMyWall, true);
		//dataUri=dataUri.replace("https4","https");
		String facebookUserName="aegibson@mtu.edu";
		String facebookPassword="destrotroll5";
		String facebookFormUserName="email";
		String facebookFormPass="pass";
		Map<String,String> loginParams = new HashMap<String,String>();
		loginParams.put(HttpAuthUtils.USER_NAME, facebookUserName);
		loginParams.put(HttpAuthUtils.PASSWORD,facebookPassword);
		loginParams.put(HttpAuthUtils.USERNAME_FORM_PARAM, facebookFormUserName);
		loginParams.put(HttpAuthUtils.PASSWORD_FORM_PARAM,facebookFormPass);
		/*
		final DefaultHttpClient client=(DefaultHttpClient) HttpAuthUtils.login("http://www.facebook.com/login.php", loginParams,true);
		List<org.apache.http.cookie.Cookie> cookies=client.getCookieStore().getCookies();

		HttpComponent httpComponent = context.getComponent("https4", HttpComponent.class);
		httpComponent.setHttpClientConfigurer(new HttpClientConfigurer(){

			@Override
			public void configureHttpClient(HttpClient httpClient) {
				DefaultHttpClient camelClient=(DefaultHttpClient) httpClient;

				//camelClient.setCookieStore(client.getCookieStore());
			}

		});
		 */
		final String dataUriClone=dataUri.replace("https4","https");

		/*
		HttpGet get = new HttpGet(dataUri);
		DefaultHttpClient client = new DefaultHttpClient();
		HttpResponse resp=client.execute(get);
		String response=EntityUtils.toString(resp.getEntity());
		 */ 
		AccessKeyForUser key=HttpAuthUtils.requestAccessWithLookup(oauth2Service, userName, 13);
		final String  accessHeader=HttpAuthUtils.OAUTH2_ACCESS_TOKEN + "=" + key.getAccessCode();

		final Map<String,Object> headers = new HashMap<String,Object>();
		headers.put(Exchange.HTTP_QUERY, accessHeader);
		headers.put(Exchange.HTTP_METHOD,"GET");

		final List<Object> obj = new ArrayList<Object>();
		OAuthRouteBuilder builder = new OAuthRouteBuilder(oauth2Service,facebookContacts,userName,"GET",new Processor(){
			public void process(Exchange exchange) {
				Message out=exchange.getOut();

				try {

					String response=out.getBody(String.class);
					out.setBody(response);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},new ArrayList<Contact>().getClass(),true);



		final String wallClone=dataUri2.replace("https4","https");
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost postToWall = new HttpPost("https://graph.facebook.com/225432427537727/feed");
		StringEntity entity = new StringEntity("Posting from My new App One Cloud");
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("message", "Posting from My new App One Cloud"));
		nameValuePairs.add(new BasicNameValuePair("access_token",key.getAccessCode()));
		postToWall.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		HttpResponse resp=client.execute(postToWall);
		String response=EntityUtils.toString(resp.getEntity());
		Map<String,String> params = new HashMap<String,String>();
		params.put(ParamFormatter.FACEBOOKID,"225432427537727");

		OAuthRouteBuilder wallPost = new OAuthRouteBuilder(oauth2Service,facebookContacts,userName,"POST",new Processor(){
			public void process(Exchange exchange) {
				Message out=exchange.getOut();

				try {

					String response=out.getBody(String.class);
					out.setBody(response);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		},new ArrayList<Contact>().getClass(),true,params);

		context.addRoutes(builder);
		context.addRoutes(wallPost);
		context.start();

		ProducerTemplate template=context.createProducerTemplate();
		//Endpoint endpoint=context.getEndpoint(dataUri);
		String result=template.requestBody(dataUri,dataUri,String.class);
		System.out.println(result);
		producerTemplate.sendBody(dataUri2, "Posting from One Cloud");
		ConsumerTemplate consumer=context.createConsumerTemplate();
		Exchange receive=consumer.receive("direct:end");
		body=receive.getOut(String.class);


		//template.sendBody(dataUri,"hello");
		//Thread.sleep(30000);

		System.out.println(body);

	}
	
	
	@Test
	public void testDropBox() throws Exception {
		Service dropbox=oauth2Service.serviceWithId(8);
		OAuth1Info info=oauth2Service.oauthInfoForService(dropbox);
		OAuth1AccessToken token=oauth2Service.mostRecentOauthAccessTokenForUserAndService(dropbox, "agibson");
		OAuthConsumer consumer=HttpAuthUtils.getRequestingConsumer(dropbox, "agibson", oauth2Service);
		OAuth1FileProcessor processor = new OAuth1FileProcessor(consumer);
		String uri=info.getDataUrl();
		//String retrieve=HttpAuthUtils.getAccessTokenFromPin(token.getRequestToken(), info);
		
		if(uri.contains("https") && !uri.contains("https4")) uri=uri.replace("https","https4");
		else if(uri.contains("http") && !uri.contains("http4"))
		uri=uri.replace("http","http4");
		final DefaultCamelContext context = new DefaultCamelContext();
		File props = new File("/home/agibson/workspace4/camel-components/src/test/resources/calendar.ics");
		uri=uri.replace("{path}","calendar.ics");
		List<File> files=Collections.singletonList(props);
		Map<String,String> replace = new HashMap<String,String>();
		String name=props.getName();
		replace.put("{path}",props.getName());
		
		OAuth1RouteBuilder builder = new OAuth1RouteBuilder(oauth2Service,dropbox,"agibson","put",new Processor(){
			public void process(Exchange exchange) {
				Message out=exchange.getOut();

				try {

					String response=out.getBody(String.class);
					out.setBody(response);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		},new ArrayList<Contact>().getClass(),true,replace);
		//context.addEndpoint(uri, urlEndPoint);
		context.addRoutes(builder);
		ProducerTemplate template=context.createProducerTemplate();
		
		
		Map<String,Object> params = new HashMap<String,Object>();
		params.put(FileProcessor.ATTACH,false);
		params.put(FileProcessor.FILE_PARAM_TYPE,"file");
		params.put(FileProcessor.METHOD,"put");
		params.put(FileProcessor.OVERWRITE,true);
		params.put(FileProcessor.FILE_KEY,files);
		//params.put(OAuth1FileProcessor.ACCESS_PIN,retrieve);
		//context.addComponent("https4", new HttpComponent());
		
		context.start();
		//Component component=context.getComponent("https4");
		//HttpEndpoint endpoint=(HttpEndpoint) component.createEndpoint(uri);
		Endpoint endpoint=context.getEndpoint(uri);
		Exchange exchange=FileProcessor.getExchange(endpoint, params);
		processor.process(exchange);
		
		
		//template.sendBody("direct:start", params);

		
	}
	
	
	
	
	
	@After
	public void tearDown() {
		System.out.println("THIS IS THE RESULT " + body);
	}



	public ICalComponent getComponent() {
		return component;
	}
	public void setComponent(ICalComponent component) {
		this.component = component;
	}


	public Server getIncoming() {
		return incoming;
	}
	public void setIncoming(Server incoming) {
		this.incoming = incoming;
	}


	public synchronized Map<String,String> headersForCCC() {
		String userName="agibson";
		String password="destrotroll%5";
		Map<String,String> headers = new HashMap<String,String>();

		headers=ServerMailStoreBridge.headersForServer(incoming);
		headers.put(MailClient.IS_AUTH,"true");
		headers.put(MailConstants.USER_NAME,userName);
		headers.put(MailConstants.PASSWORD,password);
		headers.put("server",incoming.getServerName());
		//	headers.put(MailConstants.SSL_FALLBACK,"true");
		headers.put(MailConstants.IS_SSL,"false");
		return headers;
	}

	@Test
	public void testHttpInvite() throws Exception {
		Calendar calendar=ICalUtils.buildCalendar("src/main/resources/calendar.ics");
		String comp="https://www.huskymail.mtu.edu/home/aegibson/?method=GET&%7D&password=password&userName=aegibson&params={fmt:ics}";
		camelContext.addRoutes(builder());
		String uri="ical:https://www.huskymail.mtu.edu/home/aegibson/calendar?ssl=true&userName=aegibson&method=post&password=goblinhacker%255&params={fmt:ics,auth:ba}";
		Producer producer=camelContext.getEndpoint(uri).createProducer();
		Exchange exchange=producer.createExchange(ExchangePattern.InOut);
		Message in=exchange.getIn();
		Message out=exchange.getOut();
		in.setBody(calendar.toString());
		out.setBody(calendar.toString());
		out.setHeader(MailConstants.TO_ADDRESSES, "agibson@clevercloudcomputing.com");
		producer.start();
		producer.process(exchange);



	}

	private RouteBuilder builder() {
		return new RouteBuilder() {
			public void configure() {
				String url="ical:https://www.huskymail.mtu.edu/home/aegibson/calendar?ssl=true&userName=aegibson&method=post&password=goblinhacker%255&params={fmt:ics,auth:ba}";
				String encoded=null;
				try {
					encoded = URLEncoder.encode(url, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				from(url)
				.to("ical:camel@clevercloudcomputing.com?ssl=true&userName=camel&password=camel");
			}
		};
	}


	public MailClient getMailClient() {
		return mailClient;
	}
	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}
	public CamelContext getCamelContext() {
		return camelContext;
	}

	public void setCamelContext(CamelContext camelContext) {
		this.camelContext = camelContext;
	}


	public OAuth2Service getOauth2Service() {
		return oauth2Service;
	}


	public void setOauth2Service(OAuth2Service oauth2Service) {
		this.oauth2Service = oauth2Service;
	}


	public SMTPServer getOutgoing() {
		return outgoing;
	}


	public void setOutgoing(SMTPServer outgoing) {
		this.outgoing = outgoing;
	}


	public MailHeaders getMailHeaders() {
		return mailHeaders;
	}


	public void setMailHeaders(MailHeaders mailHeaders) {
		this.mailHeaders = mailHeaders;
	}


}

