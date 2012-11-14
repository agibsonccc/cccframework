package com.ccc.camelcomponents.ical;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.Period;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.component.VEvent;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.camelcomponents.ical.util.api.MethodServicemapper;

public class ICalProcessor implements Processor {

	public ICalProcessor(ICalConfig icalConfig) {
		this.icalConfig=icalConfig;
	}


	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			String validate=ICalUtils.extractAddress(icalConfig.getAddress());
			//getProcessor().process(exchange);
			if (exchange.getPattern().isOutCapable() && exchange.hasOut()) {
				if(ICalUtils.isEmailInvite(validate)) {

					Message in=exchange.getIn();
					String body=in.getBody(String.class);
					int beginValidate=icalConfig.getAddress().indexOf("ical://");
					String toValidate=icalConfig.getAddress().substring(beginValidate+"ical://".length());
					//strip off parameters
					int paramsIdx=toValidate.indexOf('?');
					if(paramsIdx >=0)
						toValidate=toValidate.substring(0,paramsIdx);
					boolean isEmail=ICalUtils.isEmailInvite(toValidate);
					boolean isHttp=ICalUtils.isHttpInvite(toValidate);

					if(ICalUtils.isMeetingInvitation(in)) {
						Calendar calendar=ICalUtils.calendarFromString(body);
						ICalEndPoint endPoint=(ICalEndPoint)icalConfig.getEndPoint();

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
							Component c = (Component)o;
							if(c instanceof VEvent) {
								VEvent event=(VEvent) c;
								MeetingInfo info=ICalUtils.fromEvent(event);

								if(isEmail) {
									ICalUtils.sendEmailInvite(info,icalConfig.getMailHeaders(),icalConfig.getMailClient(),icalConfig.getServer(),icalConfig.getAddress());
								}
								else if(isHttp) {
									ICalUtils.sendHttpInvite(info,icalConfig);
								}

							}

						}


					}
				}
				else if(ICalUtils.isHttpInvite(validate)) {
					String method=icalConfig.getMethod();
					if(method==null) return;
					if(method.equals(MethodServicemapper.GET)) {
						
						String host=ICalUtils.urlWithOnlyParams(icalConfig.getEndPoint().getParams(), icalConfig);

						HttpGet get = new HttpGet(host);
						DefaultHttpClient authClient=icalConfig.getHttpClient();
						HttpResponse response=authClient.execute(get);

						HttpEntity entity=response.getEntity();
						BufferedHttpEntity eis = new BufferedHttpEntity(entity);
						
						Header contentType=entity.getContentType();
						if(contentType!=null && contentType.getValue().contains("text/calendar")) {
							if(response.containsHeader("Content-Disposition")) {
								String fileName=ICalUtils.getFileNameFromDisposition(response.getFirstHeader("Content-Disposition"));
								File file = new File(fileName);
								BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
								BufferedInputStream entityInputStream = new BufferedInputStream(eis.getContent());
								IOUtils.copy(entityInputStream, bos);
								bos.flush();
								Message out=exchange.getOut();
								out.setBody(file, File.class);
								
							}
						EntityUtils.consume(entity);
						}
						

					}
					else if(method.equals(MethodServicemapper.POST) || method.equals("put")) {
						ICalEndPoint end=(ICalEndPoint) icalConfig.getEndPoint();
						String host=ICalUtils.urlWithOnlyParams(icalConfig.getEndPoint().getParams(), icalConfig);
						HttpPost post =new HttpPost(host);
						Message out=exchange.getOut();
						File file=out.getBody(File.class);
						ICalUtils.uploadFile(file, icalConfig);
						
						/*
						HttpResponse response=icalConfig.getHttpClient().execute(post);
						HttpEntity entity=response.getEntity();
					
						EntityUtils.consume(entity);
						*/
						//log.info("Sent http post"  + entity.getContentType().getValue());

					}
				}
			}
		} catch (Exception e) {
			exchange.setException(e);
			//e.printStackTrace();
		}
	}		


	public ICalConfig getIcalConfig() {
		return icalConfig;
	}


	public void setIcalConfig(ICalConfig icalConfig) {
		this.icalConfig = icalConfig;
	}


	private ICalConfig icalConfig;
}
