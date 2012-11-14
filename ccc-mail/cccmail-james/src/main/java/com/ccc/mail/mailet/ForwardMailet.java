package com.ccc.mail.mailet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.mailet.Mail;
import org.apache.mailet.MailAddress;
import org.apache.mailet.base.GenericMailet;
import org.apache.james.core.MimeMessageUtil;

import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.Server;
import com.sun.mail.smtp.SMTPTransport;


/**
 * This is a mailet that acts as a sort of smtp gateway able to write
 * to arbitrary ports and servers based on the ports and hosts specified.
 * @author Adam Gibson
 *
 */
public class ForwardMailet extends GenericMailet {
	@Override
	public void init() {
		String portString=super.getInitParameter("port");
		String debugString=super.getInitParameter("debug");
		String hostString=super.getInitParameter("host");
		String ip=super.getInitParameter("ip");
		mailListUserName=super.getInitParameter("listUserName");
		mailListPassword=super.getInitParameter("listPassword");
		mailListEmail=super.getInitParameter("listEmail");
		debug=debugString!=null ? Boolean.parseBoolean(debugString) : false;
		host=hostString!=null ? hostString : "localhost";
		port=portString!=null ? Integer.parseInt(portString) : 25;
		
		outgoing = new Server();
		outgoing.setPort(port);
		outgoing.setServerName(host);
		outgoing.setAuth(true);
		outgoing.setServerType("smtp");
		outgoing.setServerAddress(ip);
		
	}

	@Override
	public void service(Mail mail) throws MessagingException {
		Session s1=ServerMailStoreBridge.sessionForServerAuth(outgoing, mailListUserName, mailListPassword);


		URLName name = new URLName(host);
		SMTPTransport transport = new SMTPTransport(s1,name);


		MimeMessage message =mail.getMessage();
		message.addFrom(new Address[] {new InternetAddress(mailListEmail)});
		//message.setSentDate(new Date(System.currentTimeMillis()));
		transport.connect(host, port, mailListUserName, mailListPassword);
		Transport.send(message);
		mail.setState(Mail.GHOST); //Mark message as okay to discard
	}





	public String getMailingListUser() {
		return mailingListUser;
	}

	public void setMailingListUser(String mailingListUser) {
		this.mailingListUser = mailingListUser;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}
	
	public Server getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(Server outgoing) {
		this.outgoing = outgoing;
	}

	public PrintWriter getOut() {
		return out;
	}

	public void setOut(PrintWriter out) {
		this.out = out;
	}

	public InetAddress getMailHost() {
		return mailHost;
	}

	public void setMailHost(InetAddress mailHost) {
		this.mailHost = mailHost;
	}

	public InetAddress getLocalhost() {
		return localhost;
	}

	public void setLocalhost(InetAddress localhost) {
		this.localhost = localhost;
	}
	
	public BufferedReader getIn() {
		return in;
	}

	public void setIn(BufferedReader in) {
		this.in = in;
	}

	public String getMailListUserName() {
		return mailListUserName;
	}

	public void setMailListUserName(String mailListUserName) {
		this.mailListUserName = mailListUserName;
	}

	public String getMailListPassword() {
		return mailListPassword;
	}

	public void setMailListPassword(String mailListPassword) {
		this.mailListPassword = mailListPassword;
	}

	public String getMailListEmail() {
		return mailListEmail;
	}

	public void setMailListEmail(String mailListEmail) {
		this.mailListEmail = mailListEmail;
	}

	private Server outgoing;
	private boolean debug;

	private int port;

	private String mailingListUser;

	private String host;

	private BufferedReader in;

	private PrintWriter out;

	private InetAddress mailHost;

	private InetAddress localhost;
	
	private String mailListUserName;
	
	private String mailListPassword;
	
	private String mailListEmail;
}
