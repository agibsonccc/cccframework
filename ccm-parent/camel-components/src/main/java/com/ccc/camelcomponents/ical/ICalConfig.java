package com.ccc.camelcomponents.ical;

import java.io.Serializable;

import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.clevmail.mailheaders.MailHeaders;
import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.oauth.apimanagement.model.Service;
public class ICalConfig implements Serializable,Cloneable {



	public ICalConfig clone() {
		ICalConfig ret = new ICalConfig();
		ret.setAddress(address);
		ret.setAuthType(authType);
		ret.setEndPoint(endPoint);
		ret.setMailClient(mailClient);
		ret.setMailHeaders(mailHeaders);
		ret.setMethod(method);
		ret.setPassword(password);
		ret.setUserName(userName);
		ret.setServer(server);
		ret.setHttpClient(httpClient);
		ret.setSmtpServer(smtpServer);
		ret.setService(service);
		return ret;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3627607408271724941L;

	public String getAuthType() {
		return authType;
	}

	public void setAuthType(String authType) {
		this.authType = authType;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public ICalEndPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(ICalEndPoint endPoint) {
		this.endPoint = endPoint;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public DefaultHttpClient getHttpClient() {
		if(httpClient==null) {
			httpClient = new DefaultHttpClient();
			if(authType!=null && !authType.isEmpty() && userName!=null && password!=null && endPoint!=null && !userName.isEmpty() && !password.isEmpty()) {
				try {
					httpClient=	ICalUtils.getAuthenticatedClient(endPoint, userName, password);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

		return httpClient;
	}

	public void setHttpClient(DefaultHttpClient httpClient) {
		this.httpClient = httpClient;
	}

	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	public MailHeaders getMailHeaders() {
		return mailHeaders;
	}

	public void setMailHeaders(MailHeaders mailHeaders) {
		this.mailHeaders = mailHeaders;
	}

	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public SMTPServer getSmtpServer() {
		return smtpServer;
	}

	public void setSmtpServer(SMTPServer smtpServer) {
		this.smtpServer = smtpServer;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	private String authType;

	private String password;

	private String userName;

	private ICalEndPoint endPoint;

	private String address;
	private DefaultHttpClient httpClient;
	@Autowired(required=false)
	private MailClient mailClient;
	@Autowired(required=false)
	private MailHeaders mailHeaders;

	private Server server;

	private Service service;

	private String method;
	private SMTPServer smtpServer;

}
