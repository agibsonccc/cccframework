package com.ccc.camelcomponents.ical;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.camel.Body;
import org.apache.camel.Consume;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.camelcomponents.ical.util.api.MethodServicemapper;
import com.ccc.camelcomponents.ical.util.api.ServerMapper;
import com.ccc.camelcomponents.ical.util.impl.DefaultMethodServiceMapper;
import com.ccc.camelcomponents.ical.util.impl.DefaultServerMapper;
import com.ccc.clevmail.mailheaders.MailHeaders;
import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;

//@Component("inviteSender")
public class InviteSender {

	private void sendEmailInvite(MeetingInfo meetingInfo) throws Exception {
		int beginValidate=address.indexOf("ical://");

		String toValidate=address.substring(beginValidate+"ical://".length());
		//strip off parameters
		int paramsIdx=toValidate.indexOf('?');
		if(paramsIdx >=0)
			toValidate=toValidate.substring(0,paramsIdx);

		headers.getHeaders().put(MailConstants.FROM_ADDRESS,toValidate);
		headers.getHeaders().put(MailConstants.IS_SSL, "true");
		headers.getHeaders().put(MailConstants.TO_ADDRESSES, "agibson@clevercloudcomputing.com");
		Session session=mailClient.login(headers.getHeaders(), server);
		javax.mail.Message message = new MimeMessage(session);
		headers.getHeaders().put(MailConstants.CONTENT, meetingInfo.toString());
		Multipart part=ICalUtils.getPartForInvite(meetingInfo);

		message.setContent(part);
		File f=ICalUtils.fileFromMeeting(meetingInfo);
		mailClient.sendMailWithAttachments(headers.getHeaders(), new File[]{f},false);

	}


	private void sendHttpInvite(MeetingInfo meetingInfo) throws ClientProtocolException, IOException {
		String method=methodMapper.methodFor(address);

		if(method.equals(MethodServicemapper.GET)) {
			HttpGet get = new HttpGet(address);
			HttpResponse response=client.execute(get);
			HttpEntity entity=response.getEntity();
			EntityUtils.consume(entity);
			log.info("Sent http get, response was: " + entity.getContentType().getValue());


		}
		else if(method.equals(MethodServicemapper.POST)) {
			HttpPost post =new HttpPost(address);
			HttpResponse response=client.execute(post);
			HttpEntity entity=response.getEntity();
			EntityUtils.consume(entity);
			log.info("Sent http post"  + entity.getContentType().getValue());

		}
	}
	
	@Consume(uri="ical:camel@clevercloudcomputing.com?ssl=true&userName=camel&password=camel")
	public void send(@Body Message message) throws Exception {
		address="ical:camel@clevercloudcomputing.com?ssl=true&userName=camel&password=camel";
		
		int beginValidate=address.indexOf("ical://");

		String toValidate=address.substring(beginValidate+"ical://".length());
		//strip off parameters
		int paramsIdx=toValidate.indexOf('?');
		if(paramsIdx >=0)
			toValidate=toValidate.substring(0,paramsIdx);
		String body=message.getBody(String.class);
		
		if(ICalUtils.isEmailInvite(toValidate)) {

			
			boolean isEmail=ICalUtils.isEmailInvite(toValidate);
			boolean isHttp=ICalUtils.isHttpInvite(toValidate);
			
			if(ICalUtils.isMeetingInvitation(message)) {
				Calendar calendar=ICalUtils.calendarFromString(body);

				Property begin=calendar.getProperty("DTSTART");
				Property end=calendar.getProperty(Calendar.END);
				if(begin!=null && end!=null) {
					String beginString=begin!=null ? begin.getValue() : "";
					String endString=end!=null ? end.getValue() : "";
					DateTime from = new DateTime(beginString);
					DateTime to = new DateTime(endString);
					Period period = new Period(from, to);

				}

				// For each VEVENT in the ICS
				for (Object o : calendar.getComponents("VEVENT")) {
					if(o instanceof VEvent) {
						VEvent event=(VEvent) o;
						MeetingInfo info=ICalUtils.fromEvent(event);

						if(isEmail) {
							sendEmailInvite(info);
						}
						else if(isHttp) {
							sendHttpInvite(info);
						}

					}

				}


			}
		}
		else if(ICalUtils.isHttpInvite(address)) {
			String method=methodMapper.methodFor(address);

			if(method.equals(MethodServicemapper.GET)) {
				HttpGet get = new HttpGet(address);
				HttpResponse response=client.execute(get);
				HttpEntity entity=response.getEntity();
				EntityUtils.consume(entity);
				log.info("Sent http get, response was: " + entity.getContentType().getValue());


			}
			else if(method.equals(MethodServicemapper.POST)) {
				HttpPost post =new HttpPost(address);
				HttpResponse response=client.execute(post);
				HttpEntity entity=response.getEntity();
				EntityUtils.consume(entity);
				log.info("Sent http post"  + entity.getContentType().getValue());

			}
		}
	}

	
	
	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public DefaultHttpClient getClient() {
		return client;
	}

	public void setClient(DefaultHttpClient client) {
		this.client = client;
	}

	public ServerMapper getServerMapper() {
		return serverMapper;
	}

	public void setServerMapper(ServerMapper serverMapper) {
		this.serverMapper = serverMapper;
	}

	public MailHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(MailHeaders headers) {
		this.headers = headers;
	}

	public SMTPServer getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(SMTPServer smtpServer) {
		this.smtpServer = smtpServer;
	}

	private Session session;
	@Autowired
	@Qualifier("cccIn")
	private Server server;

	private DefaultHttpClient client = new DefaultHttpClient();

	private ServerMapper serverMapper = new DefaultServerMapper();

	private MethodServicemapper methodMapper = new DefaultMethodServiceMapper();
	@Autowired
	private MailHeaders headers;
	@Autowired
	@Qualifier("cccOut")
	private SMTPServer smtpServer;
	private static Logger log=LoggerFactory.getLogger(InviteSender.class);

	
	@Autowired
	private MailClient mailClient;
	
	private String address;
	
}
