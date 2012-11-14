package com.ccc.camelcomponents.ical;

import javax.mail.Session;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultConsumer;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ccc.camelcomponents.ical.util.api.ServerMapper;
import com.ccc.camelcomponents.ical.util.impl.DefaultServerMapper;
import com.ccc.clevmail.mailheaders.MailHeaders;
import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
/**
 * This is an ical consumer that will consume messages from either http or email
 * to process ics files
 * @author Adam Gibson
 *
 */
public class ICalConsumer extends DefaultConsumer  {

	@Override
	protected void doStart() throws Exception {
		if(icalConfig!=null && icalConfig.getMailClient()!=null && icalConfig.getMailHeaders()!=null && icalConfig.getServer()!=null)
			session=icalConfig.getMailClient().login(icalConfig.getMailHeaders().getHeaders(), icalConfig.getServer());
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		if(icalConfig!=null && icalConfig.getMailClient()!=null && icalConfig.getServer()!=null)
			icalConfig.getMailClient().logout(icalConfig.getServer().getServerName());
		super.doStop();
	}

	public ICalConsumer(ICalConfig icalConfig,Endpoint endpoint, Processor processor) {
		super(endpoint, processor);
		this.icalConfig=icalConfig;
	}


	

	public ICalConfig getIcalConfig() {
		return icalConfig;
	}

	public void setIcalConfig(ICalConfig icalConfig) {
		this.icalConfig = icalConfig;
	}

	public void onExchange(Exchange exchange) throws Exception {
		if (isStarted()) {

			getProcessor().process(exchange);

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

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private MailClient mailClient;

	private String authType;

	private String address;

	private Session session;

	private Server server;

	private DefaultHttpClient client = new DefaultHttpClient();

	private ServerMapper serverMapper = new DefaultServerMapper();


	private MailHeaders headers;

	private SMTPServer smtpServer;

	private String method;

	private String userName;

	private String password;

	private ICalConfig icalConfig;

	private static Logger log=LoggerFactory.getLogger(ICalConsumer.class);
}
