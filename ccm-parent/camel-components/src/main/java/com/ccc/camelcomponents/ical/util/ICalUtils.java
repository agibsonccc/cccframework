package com.ccc.camelcomponents.ical.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.CalendarOutputter;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.TimeZone;
import net.fortuna.ical4j.model.TimeZoneRegistry;
import net.fortuna.ical4j.model.TimeZoneRegistryFactory;
import net.fortuna.ical4j.model.ValidationException;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.component.VTimeZone;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.CalScale;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.ProdId;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;
import net.fortuna.ical4j.util.CompatibilityHints;

import org.apache.camel.Converter;
import org.apache.camel.Message;
import org.apache.commons.io.IOUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.UrlValidator;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.AuthCache;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;

import com.ccc.camelcomponents.core.api.EventFormatter;
import com.ccc.camelcomponents.core.base.FacebookEventFormatter;
import com.ccc.camelcomponents.core.base.GoogleEventFormatter;
import com.ccc.camelcomponents.ical.ICalConfig;
import com.ccc.camelcomponents.ical.ICalEndPoint;
import com.ccc.camelcomponents.ical.MeetingInfo;
import com.ccc.camelcomponents.ical.util.api.MethodServicemapper;
import com.ccc.clevmail.mailheaders.MailHeaders;
import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.util.filesystem.FileMoverUtil;
import com.ccc.util.web.URLManipulator;
import com.ccc.oauth.apimanagement.model.Service;
/**
 * This is a calendar utility that contains 
 * various ways of retrieving calendars out of camel messages
 * as well as checks for basic ics components of messages
 * @author Adam Gibson
 *
 */
public class ICalUtils {

	/**
	 * This will return url encoded parameters that are appended to the end of an ical uri
	 * @return url encoded parameters for an ical uri, or null if parameters are null 
	 * or empty
	 * @throws JSONException if the params object isn't valid json
	 */
	public static String urlEncodedParams(String params) throws JSONException {
		if(params==null || params.isEmpty())
			return null;
		else {
			JSONObject object = new JSONObject(params);
			return URLManipulator.jsonToUrlEncode(object);
		}
	}//end urlEncodedParams

	/**
	 * This will return an event for matter for the given service
	 * @param service the service to get an event formatter for
	 * @return the event formatter for the given service, or null if it doesn't exist
	 */
	public static EventFormatter formatterFor(Service service) {
		if(service.getName().toLowerCase().contains("google")) {
			return new GoogleEventFormatter();
		}
		else if(service.getName().toLowerCase().contains("facebook")) {
			return new FacebookEventFormatter();
		}
		return null;
	}//end formatterFor
	/**
	 * This will return the uri of this end point with only 
	 * the params in the params json string appended as uri 
	 * encoded
	 * @return the uri of this end point with only 
	 * the params in the params json string appended as uri 
	 * @throws JSONException
	 */
	public static String urlWithOnlyParams(String params,ICalConfig config) throws JSONException {
		if(params==null || params.isEmpty())
			return null;
		else {
			int questionIdx=config.getAddress().indexOf('?');
			int indexOfEndOfIcal=config.getAddress().indexOf("ical://") + "ical://".length();
			String baseUrl=config.getAddress().substring(indexOfEndOfIcal,questionIdx);

			String encodedParams=urlEncodedParams(params);
			Assert.notNull(encodedParams);
			StringBuffer sb = new StringBuffer();
			sb.append(baseUrl);
			sb.append("?");
			sb.append(encodedParams);
			return sb.toString();
		}
	}//end urlWithOnlyParams

	/**
	 * This will send meeting information using the given method
	 * If it's a get request, this will download whatever the response is
	 * If it's post, it will upload an ics file for the meeting information
	 * @param meetingInfo the meeting info to send
	 * @param method the method to use
	 * @throws Exception
	 */
	public static void sendHttpInvite(MeetingInfo meetingInfo,ICalConfig config) throws Exception {
		String method=config.getMethod();
		if(method==null)
			method=MethodServicemapper.POST;
		//Assert.notNull(method,"Method was null!");
		Assert.notNull(meetingInfo,"Meeting info was null");
		DefaultHttpClient client=config.getHttpClient();
		ICalEndPoint endPoint=config.getEndPoint();
		String userName=config.getUserName();
		String password=config.getPassword();
		String address=ICalUtils.urlWithOnlyParams(config.getEndPoint().getParams(), config);

		if(method.equalsIgnoreCase(MethodServicemapper.GET)) {
			HttpGet get = new HttpGet(address);
			HttpResponse response=client.execute(get);
			HttpEntity entity=response.getEntity();
			EntityUtils.consume(entity);
			//	log.info("Sent http get, response was: " + entity.getContentType().getValue());


		}
		else if(method.equalsIgnoreCase(MethodServicemapper.POST)) {
			client=getAuthenticatedClient(endPoint,userName,password);

			File f=ICalUtils.fileFromMeeting(meetingInfo);
			uploadFile(f,config);
		}
	}//end sendHttpInvite

	/**
	 * This will upload the given file based on the url for this producer
	 * using the given http client
	 * @param file the file to upload
	 * @param client the client to use
	 * @return the response to the file upload
	 * @throws HttpException 
	 * @throws IOException
	 * @throws JSONException 
	 */
	public static String uploadFile(File file,ICalConfig config) throws HttpException, IOException, JSONException {
		//get authenticated context for posting to the uri


		/*
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);

			reqEntity.addPart("string_field",
				new StringBody("field value"));

			FileBody bin = new FileBody(
				new File("/foo/bar/test.png"));
			reqEntity.addPart("attachment_field", bin );

			httppost.setEntity(reqEntity);
		 */
		String host=ICalUtils.urlWithOnlyParams(config.getEndPoint().getParams(), config);
		config.getHttpClient().getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		String userName=config.getUserName();
		String password=config.getPassword();
		DefaultHttpClient client=config.getHttpClient();
		String method=config.getMethod();
		Service service=config.getService();
		EventFormatter formatter=ICalUtils.formatterFor(service);
		VEvent[] events=null;
		try {
			events = ICalUtils.eventFromFile(file);
		} catch (ParserException e) {
			e.printStackTrace();
		}

		String formattedString=formatter.formatMultipleEvents(events);
		if(events!=null) {
			for(VEvent event : events) {
				String fileWithoutFormat=null;
				formattedString=formatter.formatEvent(event);

				int dotIdx=file.getName().indexOf('.');
				if(dotIdx >= 0) 
					fileWithoutFormat=file.getName().substring(0,dotIdx);


				File toWrite = new File(fileWithoutFormat + formatter.format());
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(toWrite));
				BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(formattedString.getBytes()));
				IOUtils.copy(bis, bos);
				bos.flush();

				if(config.getMethod().toLowerCase().equals("post")) {
					HttpPost        post   = new HttpPost( host );
					//ICalHttpEntity entity = new ICalHttpEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
					FileEntity entity = new FileEntity(toWrite,formatter.mimeType());
					//post request

					BasicScheme scheme = new BasicScheme(); 
					if(config.getUserName()!=null && config.getPassword()!=null) {
						org.apache.http.Header authorizationHeader = scheme.authenticate(new UsernamePasswordCredentials(config.getUserName(),config.getPassword()), post); 

						post.addHeader(authorizationHeader); 
					}

					post.setEntity( entity );

					String response = EntityUtils.toString( client.execute( post).getEntity(), "UTF-8" );
					EntityUtils.consume(entity);



				}
			}


			String fileWithoutFormat=null;
			int dotIdx=file.getName().indexOf('.');
			if(dotIdx >= 0) 
				fileWithoutFormat=file.getName().substring(0,dotIdx);


			File toWrite = new File(fileWithoutFormat + formatter.format());
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(toWrite));
			BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(formattedString.getBytes()));
			IOUtils.copy(bis, bos);
			bos.flush();


			if(config.getMethod().toLowerCase().equals("post")) {
				HttpPost        post   = new HttpPost( host );
				//ICalHttpEntity entity = new ICalHttpEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
				FileEntity entity = new FileEntity(toWrite,formatter.mimeType());
				//post request

				BasicScheme scheme = new BasicScheme(); 
				if(config.getUserName()!=null && config.getPassword()!=null) {
					org.apache.http.Header authorizationHeader = scheme.authenticate(new UsernamePasswordCredentials(config.getUserName(),config.getPassword()), post); 

					post.addHeader(authorizationHeader); 
				}



				// For File parameters
				//entity.addPart( file.getName(), new FileBody(file ) );


				post.setEntity( entity );

				String response = EntityUtils.toString( client.execute( post).getEntity(), "UTF-8" );
				EntityUtils.consume(entity);
				client.getConnectionManager().shutdown();
				return response;

			}
			else if(method.toLowerCase().equals("put")) {
				HttpPut put = new HttpPut(host);

				//ICalHttpEntity entity = new ICalHttpEntity( HttpMultipartMode.BROWSER_COMPATIBLE );
				FileEntity entity = new FileEntity(toWrite, formatter.mimeType());
				//post request
				BasicScheme scheme = new BasicScheme(); 
				if(userName!=null && password!=null) {
					org.apache.http.Header authorizationHeader = scheme.authenticate(new UsernamePasswordCredentials(userName,password), put); 

					put.addHeader(authorizationHeader); 
				}



				// For File parameters
				//	entity.addPart( file.getName(), new FileBody(file ) );


				put.setEntity( entity );

				String response = EntityUtils.toString( client.execute( put).getEntity(), "UTF-8" );

				client.getConnectionManager().shutdown();
				return response;
			}
			//create file entity for upload
			//FileEntity entity = new FileEntity(file, ICalUtils.CALENDAR_TYPE + " charset=\"UTF-8\"");
			//post request
			//HttpPost httppost = new HttpPost(host);
			//httppost.setEntity(entity);
			//retrieve response
			//HttpResponse response=client.execute(httppost,context);
			return "File not uploaded";
		}//end uploadFile
		return formattedString;
	}
	/**
	 * This will return a  formatted event for multiple events based on the service
	 * @param events the events to format
	 * @param service the service to format for
	 * @return the formatted string
	 * @throws JSONException
	 */
	public static String formattedEvent(VEvent[] events,Service service)  {
		EventFormatter formatter=ICalUtils.formatterFor(service);
		return formatter.formatMultipleEvents(events);
	}


	/**
	 * This will parse the file name out of a content disposition header
	 * @param header the header to extract from
	 * @return null if the header is the wrong type or head is null, or the
	 * non quote escaped file name of this header
	 */
	public static String getFileNameFromDisposition(Header header) {
		if(header==null || !header.getName().equals("Content-Disposition")) return null;
		String value=header.getValue();
		String[] semiSplit=value.split(";");
		String[] fileNameSplit=semiSplit[1].split("=");
		String ret=fileNameSplit[1].replaceAll("\"", "");
		return ret;


	}//end getFileNameFromDisposition

	/**
	 * This will attempt to get a vevent from the given file
	 * @param file the file to get vent from 
	 * @return an event from the given file or null on error
	 * @throws IOException
	 * @throws ParserException
	 */
	public static VEvent[] eventFromFile(File file) throws IOException, ParserException {
		Calendar calendar=buildCalendar(file);
		ComponentList comp=calendar.getComponents("VEVENT");
		if(!comp.isEmpty()) {
			VEvent[] ret = new VEvent[comp.size()];
			for(int i=0;i<comp.size();i++) {
				VEvent curr=(VEvent) comp.get(i);
				if(!curr.getEndDate().getDate().before(curr.getDateStamp().getDate()))
					ret[i]=(VEvent)comp.get(i);

			}

			return ret;
		}
		return null;
	}//end eventFromFile


	/* return an authenticated http client */
	public static DefaultHttpClient getAuthenticatedClient(ICalEndPoint endPoint,String userName,String password) throws JSONException {
		String host=ICalUtils.urlWithOnlyParams(endPoint.getParams(),endPoint.getConfig());
		//set credentials
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(userName, password);
		org.apache.http.HttpHost targetHost = new org.apache.http.HttpHost(host, 443); 

		DefaultHttpClient httpclient = new DefaultHttpClient();

		httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		List<String> authpref = new ArrayList<String>();
		authpref.add(AuthPolicy.BASIC);
		authpref.add(AuthPolicy.DIGEST);
		httpclient.getParams().setParameter(AuthPNames.PROXY_AUTH_PREF, authpref);
		httpclient.getCredentialsProvider().setCredentials(
				new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT,AuthScope.ANY_REALM), 
				new UsernamePasswordCredentials(userName,password));

		return httpclient;
	}

	/* Return an authenticated session based on the host */
	public static BasicHttpContext getAuthContext(ICalEndPoint endPoint) throws JSONException {
		String host=ICalUtils.urlWithOnlyParams(endPoint.getParams(),endPoint.getConfig());

		org.apache.http.HttpHost targetHost = new org.apache.http.HttpHost(host, 443); 

		// Create AuthCache instance
		AuthCache authCache = new BasicAuthCache();
		// Generate BASIC scheme object and add it to the local auth cache
		AuthScheme basicAuth = new BasicScheme();
		authCache.put(targetHost, basicAuth);
		// Add AuthCache to the execution context
		BasicHttpContext localcontext = new BasicHttpContext();
		localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);    
		return localcontext;
	}//end getAuthContext


	public static void sendEmailInvite(MeetingInfo meetingInfo,MailHeaders headers,MailClient mailClient,Server server,String address) throws Exception {
		int beginValidate=address.indexOf("ical://");

		String toValidate=ICalUtils.extractAddress(address);
		headers.getHeaders().put(MailConstants.FROM_ADDRESS,toValidate);
		Session session=mailClient.login(headers.getHeaders(), server);
		javax.mail.Message message = new MimeMessage(session);

		Multipart part=ICalUtils.getPartForInvite(meetingInfo);

		message.setContent(part);
		File f=ICalUtils.fileFromMeeting(meetingInfo);
		mailClient.sendMailWithAttachments(headers.getHeaders(), new File[]{f},false);

	}


	public static  String getAddressWithParams(String address) {
		//get the raw uri, parse out everything but params
		int beginValidate=address.indexOf("ical://");

		String toValidate=address.substring(beginValidate+"ical://".length());

		return toValidate;
	}

	public static  String extractAddress(String address) {
		int beginValidate=address.indexOf("ical://");

		String toValidate=address.substring(beginValidate+"ical://".length());
		//strip off parameters
		int paramsIdx=toValidate.indexOf('?');
		if(paramsIdx >=0)
			toValidate=toValidate.substring(0,paramsIdx);
		return toValidate;
	}

	/**
	 * This will check for an invitation type of the given uri
	 * Only two types of addresses are supported: urls and emails
	 * @param uri the uri to check
	 * @return the type of invitation for this uri, if not email or url,
	 * returns null
	 */
	public static String invitiationType(String uri) {
		if(uri==null)
			return null;
		EmailValidator validator = EmailValidator.getInstance();
		UrlValidator urlValidator = new UrlValidator();
		if(validator.isValid(uri)) 
			return ICalUtils.EMAIL_INVITATION;

		else if(urlValidator.isValid(uri))
			return ICalUtils.HTTP_INVITATION;

		else return null;

	}//end invitiationType

	/**
	 * This will return whether the given uri is an email invite or not
	 * @param uri the uri to check
	 * @return true if the uri is an email, false otherwise
	 */
	public static boolean isEmailInvite(String uri) {
		if(uri==null)
			return false;
		String invite=invitiationType(uri);

		return invite==null? false : invite.equalsIgnoreCase(ICalUtils.EMAIL_INVITATION);
	}//end isEmailInvite

	/**
	 * This will return whether the given uri is an http invite or not
	 * @param uri the uri to check
	 * @return true if the uri is a url, false otherwise
	 */
	public static boolean isHttpInvite(String uri) {
		if(uri==null)
			return false;
		String invite=invitiationType(uri);

		return invite==null? false : invite.equalsIgnoreCase(ICalUtils.HTTP_INVITATION);
	}//end isHttpInvite

	/**
	 * This will return a timezone for use by 
	 * events.
	 * @return a timezone for use by events
	 */
	public static TimeZone getRandTimeZone() {
		if(cached!=null)
			return cached;
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		Assert.notNull(registry,"Registry can't be null");
		net.fortuna.ical4j.model.TimeZone _tz = registry.getTimeZone(TimeZone.getAvailableIDs()[0]);

		if(_tz==null) {
			String[] ids=TimeZone.getAvailableIDs();
			for(String s : ids) {
				_tz=registry.getTimeZone(s);
				if(_tz!=null) break;
			}
		}
		return _tz;
	}//end getRandTimeZone


	/**
	 * This will return a basic calendar in the EST time zone
	 * @return a basic calendar with the EST timezone
	 * @throws URISyntaxException 
	 */
	public static Calendar getBasicCalendar() throws URISyntaxException {
		net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
		cal.getProperties().add(new ProdId("-//iloveoutlook//iCal4j 1.0//EN"));
		cal.getProperties().add(net.fortuna.ical4j.model.property.Version.VERSION_2_0);
		cal.getProperties().add(CalScale.GREGORIAN);
		cal.getProperties().add(net.fortuna.ical4j.model.property.Method.REQUEST);
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		Assert.notNull(registry,"Registry can't be null");
		net.fortuna.ical4j.model.TimeZone _tz = registry.getTimeZone(TimeZone.getAvailableIDs()[0]);

		if(_tz==null) {
			String[] ids=TimeZone.getAvailableIDs();
			for(String s : ids) {
				_tz=registry.getTimeZone(s);
				if(_tz!=null) break;
			}
		}

		VTimeZone tz = registry.getTimeZone(_tz.getID()).getVTimeZone();
		cal.getComponents().add(tz);
		return cal;
	}//end getBasicCalendar

	@Converter
	public String toString(VEvent event) {
		return event.toString();
	}

	@Converter
	public String toString(MeetingInfo meetingInfo) {
		try {
			return fromMeeting(meetingInfo).toString();
		} catch (URISyntaxException e) {
		}
		return null;
	}


	/**
	 * This will return a basic calendar in the EST time zone
	 * @param timeZone the timezone for this calendar
	 * @return a basic calendar with the EST timezone
	 */
	public static Calendar getBasicCalendar(String timeZone) {
		net.fortuna.ical4j.model.Calendar cal = new net.fortuna.ical4j.model.Calendar();
		cal.getProperties().add(new ProdId("-//iloveoutlook//iCal4j 1.0//EN"));
		cal.getProperties().add(net.fortuna.ical4j.model.property.Version.VERSION_2_0);
		cal.getProperties().add(CalScale.GREGORIAN);
		cal.getProperties().add(net.fortuna.ical4j.model.property.Method.REQUEST);
		TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
		net.fortuna.ical4j.model.TimeZone _tz = registry.getTimeZone(timeZone);

		VTimeZone tz = registry.getTimeZone(_tz.getID()).getVTimeZone();
		cal.getComponents().add(tz);
		return cal;
	}//end getBasicCalendar



	/**
	 * This will return a multipart for a javamail message based on the given meeting info
	 * @param meetingInfo the meeting info to use
	 * @return an equivalent multipart attachment for this meeting info
	 * @throws Exception if intiialization in the body part, or creating the byte stream goes wrong
	 */
	public static Multipart getPartForInvite(MeetingInfo meetingInfo) throws Exception {
		Multipart multipart = new MimeMultipart();
		MimeBodyPart iCalAttachment = new MimeBodyPart();
		byte[] invite = createICalInvitation(meetingInfo.getMeetingId(), meetingInfo.getMeetingSubject(), meetingInfo.getContent(), meetingInfo.getMeetingStart(), meetingInfo.getMeetingEnd(), meetingInfo.getTimeZone(),meetingInfo.getAttendees(),meetingInfo.getOrganizer());
		iCalAttachment.setDataHandler(new DataHandler(new ByteArrayDataSource(new ByteArrayInputStream(invite), "text/calendar;method=REQUEST;charset=\"UTF-8\"")));
		multipart.addBodyPart(iCalAttachment);
		return multipart;
	}//end getPartForInvite


	/**
	 * This will take the given calendar and output it as json
	 * @param calendar the calendar to to use
	 * @return a json object from the given calendar
	 * @throws JSONException if an invalid json component is found
	 */
	public static JSONObject calToJson(Calendar calendar) throws JSONException {
		JSONObject ret = new JSONObject();
		for (Iterator i = calendar.getComponents().iterator(); i.hasNext();) {
			Component component = (Component) i.next();
			System.out.println("Component [" + component.getName() + "]");
			PropertyList props=component.getProperties();
			Map map=fromProperties(props);
			JSONObject wrap = new JSONObject(map);
			ret.accumulate(component.getName(), wrap);
		}
		return ret;
	}//end calToJson

	private static Map<Object,Object> fromProperties(PropertyList props) {
		Map<Object,Object> ret = new HashMap<Object,Object>();
		for (Iterator j = props.iterator(); j.hasNext();) {
			Property property = (Property) j.next();
			ret.put(property.getName(),property.getValue());
			//System.out.println("Property [" + property.getName() + ", " + property.getValue() + "]");
		}
		return ret;
	}


	/**
	 * This will create an equivalent event from the given meeting information
	 * 
	 * @param info the meeting info to use for the event
	 * @return an equivalent v event from the given meeting info
	 * @throws URISyntaxException 
	 */
	@Converter
	public static VEvent fromMeeting(MeetingInfo info) throws URISyntaxException {
		VEvent vEvent = new VEvent();
		vEvent.getProperties().add(new Uid(info.getMeetingId()));
		vEvent.getProperties().add(new Summary(info.getMeetingSubject()));
		vEvent.getProperties().add(new Description(info.getContent()));
		vEvent.getProperties().add(new DtStart(info.getMeetingStart()));
		vEvent.getProperties().add(new DtEnd(info.getMeetingEnd()));
		vEvent.getProperties().add(new Created(new DateTime(System.currentTimeMillis())));
		vEvent.getProperties().add(new Organizer(info.getOrganizer()));
		String[] attendees=info.getAttendees();
		for(String s : attendees) {
			vEvent.getProperties().add(new Attendee(s));
		}
		return vEvent;
	}//end fromMeeting

	/**
	 * This will convert an event in to a meeting info pojo
	 * @param event the event to derive from
	 * @return an equivalent meeting info object, or null on error
	 */
	@Converter
	public static MeetingInfo fromEvent(VEvent event) {
		if(event==null)
			return null;
		MeetingInfo ret = new MeetingInfo();
		DtStart start=event.getStartDate();
		DtEnd end=event.getEndDate();
		Date startDate = start.getDate();
		Date endDate=end.getDate();
		Property attendees=event.getProperty(Attendee.ATTENDEE);
		Property organizer=event.getProperty(Organizer.ORGANIZER);
		if(attendees!=null)
			ret.setAttendees(new String[]{attendees.getValue()});
		else ret.setAttendees(new String[]{organizer.getValue()});
		ret.setMeetingStart(startDate);
		ret.setMeetingEnd(endDate);
		ret.setContent(ret.getContent()!=null ?ret.getContent() : "");
		ret.setMeetingSubject(event.getDescription()!=null? event.getDescription().toString() : "");
		ret.setTimeZone(event.getCreated()!=null ? event.getCreated().getTimeZone(): null);
		ret.setOrganizer(organizer!=null ? organizer.getValue() : "");

		return ret;
	}//end fromEvent


	/**
	 * This will create an ical invitation using the given information and return a usable
	 * byte stream to be inserted in to messages
	 * @param _meetingID the meeting id of this meeting
	 * @param _subject the subject for this meeting
	 * @param _content the content of this meeting (description,...)
	 * @param _start the start date of this meeting
	 * @param _end the end date of the meeting
	 * @param _tz the time zone of the meeting
	 * @return a byte array representing the given information 
	 * @throws Exception 
	 */
	public static byte[] createICalInvitation(String _meetingID, String _subject, String _content, Date _start, Date _end, TimeZone _tz,String[] attendees,String organizer) throws Exception {
		//outlook compatiabilty, really microsoft?
		CompatibilityHints.setHintEnabled(CompatibilityHints.KEY_OUTLOOK_COMPATIBILITY, true);
		//create event invite
		VEvent vEvent = new VEvent();
		if( _meetingID!=null)
			vEvent.getProperties().add(new Uid(_meetingID));
		if(_subject!=null)
			vEvent.getProperties().add(new Summary(_subject));
		if(_content!=null)
			vEvent.getProperties().add(new Description(_content));
		if(_start!=null)
			vEvent.getProperties().add(new DtStart(new DateTime(_start)));
		if( _end!=null)
			vEvent.getProperties().add(new DtEnd(new DateTime(_end)));
		if(attendees!=null) {
			for(String s : attendees)
				vEvent.getProperties().add(new Attendee(s));
		}
		if(organizer!=null)
			vEvent.getProperties().add(new Organizer(organizer));
		net.fortuna.ical4j.model.Calendar cal =null;
		//get a basic calendar
		if(_tz!=null) {
			cal =getBasicCalendar(_tz.getID());
			//set the time zone
			TimeZoneRegistry registry = TimeZoneRegistryFactory.getInstance().createRegistry();
			VTimeZone tz = registry.getTimeZone(_tz.getID()).getVTimeZone();
			cal.getComponents().add(tz);
			cal.getComponents().add(vEvent);
		}
		else cal=getBasicCalendar();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CalendarOutputter outputter = new CalendarOutputter();
		outputter.output(cal, bout);
		return bout.toByteArray();
	}//end createICalInvitation

	/**
	 * This will parse a calendar from the given message.
	 * It will try two conversion strategies, first parsing attachments,
	 * and then parsing the string 
	 * @param message the message to parse
	 * @return a calendar from the given message if possible,
	 * or null if no conversion strategy exists
	 * @throws IOException if one occurs
	 * @throws ParserException if the message obtained is invalid for 
	 * the ics format
	 */
	@Converter
	public static Calendar getCalendarFromMessage(Message message) throws IOException, ParserException {
		Map<String,DataHandler> attachments=message.getAttachments();

		if(!attachments.isEmpty()) {
			for(String s : attachments.keySet()) {
				DataHandler handler=attachments.get(s);
				String contentType=handler.getContentType();

				if(contentType.equalsIgnoreCase("text/calendar")) {
					InputStream stream=handler.getInputStream();
					CalendarBuilder builder = new CalendarBuilder();
					return builder.build(stream);
				}
			}
		}
		else {
			Object body= message.getBody();

			if(body instanceof String) {
				String messageBody=(String) body;
				return calendarFromString(messageBody);
			}
		}
		return null;
	}//end getCalendarFromMessage

	/**
	 * This will parse a camel message and check if the content type is a calendar
	 * @param message the message to check
	 * @return true if the message contains an ics file, false otherwise
	 */
	public static boolean isMeetingInvitation(Message message)	{
		Map<String,DataHandler> attachments=message.getAttachments();

		if(!attachments.isEmpty()) {
			for(String s : attachments.keySet()) {
				DataHandler handler=attachments.get(s);
				String contentType=handler.getContentType();

				if(contentType.equalsIgnoreCase("text/calendar"))
					return true;
			}
		}
		else {
			try {
				Calendar calendar=getCalendarFromMessage(message);
				return calendar!=null;
			} catch (IOException e) {
				return false;
			} catch (ParserException e) {
				return false;
			}

		}
		return false;
	}//end isMeetingInvitation
	/**
	 * This will build a calendar from a parsed string
	 * @param toParse the string to parse
	 * @return a calendar from the given string
	 * @throws IOException if one occurs
	 * @throws ParserException if the string is not valid for ICS parsing
	 */
	public static Calendar calendarFromString(String toParse) throws IOException, ParserException {
		if(toParse==null || toParse.isEmpty())
			return null;

		StringReader sin = new StringReader(toParse);

		CalendarBuilder builder = new CalendarBuilder();

		Calendar calendar = builder.build(sin);

		return calendar;
	}//end calendarFromString

	/**
	 * This will output a file with the given meeting info to the given file path
	 * @param info the info to sue
	 * @param filePath the path of the file to write
	 * @throws IOException if a problem occurs with the file
	 * @throws ValidationException if the calendar isn't valid
	 * @throws URISyntaxException for invalid attendees
	 */
	public static void createFileFromMeeting(MeetingInfo info,String filePath) throws IOException, ValidationException, URISyntaxException {
		BufferedOutputStream  fout = new BufferedOutputStream(new FileOutputStream(filePath));

		CalendarOutputter outputter = new CalendarOutputter();
		VEvent event=fromMeeting(info);

		Calendar calendar=getBasicCalendar();
		calendar.getComponents().add(event);

		outputter.output(calendar, fout);
	}//end createFileFromMeeting

	/**
	 * This will create and return an ics file for the given meeting info
	 * @param info the meeting info to get the file for
	 * @return an ics file for this meeting
	 * @throws Exception if one occurs
	 */
	@Converter
	public static File fileFromMeeting(MeetingInfo info) throws Exception {
		if(info==null)
			return null;
		//set subject.ics as name
		String description=info.getMeetingId() !=null ? info.getMeetingId() : "MEETING";

		File f = new File(description + ".ics");
		//create file to write to
		FileMoverUtil.createFile(f,false);
		BufferedOutputStream  fout = new BufferedOutputStream(new FileOutputStream(f));
		//	BufferedInputStream fin = new BufferedInputStream(new FileInputStream(f));
		//out put calendar information
		CalendarOutputter outputter = new CalendarOutputter();
		//get the event for the meeting
		VEvent event=fromMeeting(info);
		//build a calendar for the event
		Calendar calendar=getBasicCalendar();
		//add event to calendar
		calendar.getComponents().add(event);
		//output to file
		outputter.output(calendar, fout);

		return f;
	}//end fileFromMeeting

	/**
	 * This will read an ics file from the specified string
	 * and return a calendar
	 * @param file the file to read
	 * @return a calendar built from the file
	 * @throws IOException if one occurs
	 * @throws ParserException if the ics file is in correct
	 */
	public static Calendar buildCalendar(String file) throws IOException, ParserException {
		Assert.notNull(file);
		Assert.hasLength(file,"Please specify a file.");
		Assert.isTrue(file.endsWith(".ics"),"Not an ics file");

		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));

		CalendarBuilder builder = new CalendarBuilder();

		return builder.build(is);

	}//end buildCalendar




	/**
	 * This will read an ics file from the specified string
	 * and return a calendar
	 * @param file the file to read
	 * @return a calendar built from the file
	 * @throws IOException if one occurs
	 * @throws ParserException if the ics file is in correct
	 */
	public static Calendar buildCalendar(File file) throws IOException, ParserException {
		Assert.notNull(file);
		Assert.isTrue(file.getName().endsWith(".ics"),"Not an ics file");

		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));

		CalendarBuilder builder = new CalendarBuilder();

		return builder.build(is);

	}//end buildCalendar



	/**
	 * This will return whether the given action is an invite action
	 * @param action the action to check
	 * @return true if the action equals invite, false otherwise
	 */
	public static boolean isInviteAction(String action) {
		return action.equals(INVITE_ACTION);
	}
	/**
	 * This will return whether the given action is a sync action or not
	 * @param action the string to check
	 * @return true if the given string is sync, false otherwise
	 */
	public static boolean isSyncAction(String action) {
		return action!=null ? action.equals(SYNC_ACTION) : false;
	}



	public final static String EMAIL_INVITATION="email";

	public final static String CALENDAR_TYPE="text/calendar";

	public final static String HTTP_INVITATION="http";

	private static TimeZone cached;


	public final static String SYNC_ACTION="sync";

	public final static String INVITE_ACTION="invite";

}//end ICalUtils

