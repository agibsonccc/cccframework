package com.ccc.camelcomponents.ical;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;

import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.mail.core.servers.storage.MailConstants;
/**
 * This is a producer that sends invites to users configured in the uri for http,
 * or the headers for email
 * @author Adam Gibson
 *
 */
public class ICalProducer extends DefaultProducer implements MailConstants {


	/**
	 * This will make an ical producer with the given end point,
	 * address to send information to,
	 * and the given clients
	 * @param endPoint the end point for this producer
	 * @param address the address to send messages to
	 * @param mailClient the mail client to be used for email
	 * @param httpClient the http client to be used for http requests
	 * @param headers the headers used for sending mail in mail client
	 */
	public ICalProducer(Endpoint endPoint,ICalConfig config) {
		super(endPoint);
		icalConfig=config;
		processor = new ICalProcessor(config);
	}






	public ICalConfig getConfig() {
		return icalConfig;
	}




	@Override
	public void process(Exchange ex) throws Exception {
		if (isStarted()) {
			try {
				if (ex.getPattern().isOutCapable() && ex.hasOut()) getProcessor().process(ex);
			}
			catch (Exception e) {
				ex.setException(e);
			}
		}


	}


	public ICalConfig getIcalConfig() {
		return icalConfig;
	}






	public void setIcalConfig(ICalConfig icalConfig) {
		this.icalConfig = icalConfig;
	}

	public ICalProcessor getProcessor() {
		return processor;
	}






	public void setProcessor(ICalProcessor processor) {
		this.processor = processor;
	}

	private ICalProcessor processor;
	private ICalConfig icalConfig;

}//end ICalProducer
