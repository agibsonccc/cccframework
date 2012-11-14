package com.ccc.mail.tests;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.URLName;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import org.apache.james.core.MimeMessageUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.mail.impl.DefaultMailBox;
import com.ccc.mail.impl.DefaultMailStore;
import com.ccc.mail.ssl.exceptions.SSLErrorException;
import com.ccc.mail.tests.utils.TestUtils;
import com.ccc.util.filesystem.FileMoverUtil;
import com.sun.mail.smtp.SMTPTransport;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class MailTest extends AbstractJUnit4SpringContextTests {
	
	
	
	

	
	
	@Test
	@Ignore

	public void testMailet() throws MessagingException {

		Session s1=ServerMailStoreBridge.sessionForServerAuth(testUtils.mailingListServer(), "ccc-board", "c^3AI");
		URLName name = new URLName("demo.clevercloudcomputing.com");
		SMTPTransport transport = new SMTPTransport(s1,name);


		MimeMessage message = new MimeMessage(s1);
		message.setText("test message");
		message.addFrom(new Address[] {new InternetAddress("ccc-board@clevercloudcomputing.com")});
		message.addRecipients(RecipientType.TO,new Address[] {new InternetAddress("ccc-board@clevercloudcomputing.com")});
		message.setSentDate(new Date(System.currentTimeMillis()));
		transport.connect("demo.clevercloudcomputing.com", 2500, "ccc-board", "c^3AI");
		transport.send(message);

		//wrap connection parameters
		SocketAddress serverAddress = new InetSocketAddress(host,port);

		Socket s = new Socket();
		try {
			s.setKeepAlive(true);
			s.connect(serverAddress);
			//transport.connect(s);
			if(!transport.isConnected())
				transport.connect("mail.clevercloudcomputing.com", 2500, "ccc-board", "c^3AI");

		} catch (IOException e) {
			log.warn("IOException connecting to gateway: ",e);
		}
		if(s.isConnected()) {
			log.warn("ForwardMailet socket successfully connected");
			//try {

			sendTest(transport,message);
			try {
				send(message,s,serverAddress);
			} catch (IOException e) {
				log.warn("Error sending mail: ",e);
			}
		}
		else log.warn("Failed to connect to server, exiting");
	}

	
	@Test
	@Ignore
	public void testSendCCCSSL() throws AddressException, MessagingException {
		SMTPServer smtp=TestUtils.cccSSL();
		Map<String,String> headers=TestUtils.headers("agibson", "destrotroll%5", smtp.getServerName(), smtp);
		headers.put(MailConstants.TO_ADDRESSES, "aegibson@mtu.edu");
		headers.put(MailConstants.CONTENT,"unit test");
		headers.put(MailConstants.FROM_ADDRESS,"agibson");
		DefaultMailBox mailBox = new DefaultMailBox();
		DefaultMailStore store = new DefaultMailStore();
		store.setOutgoingServers(Collections.singletonList(smtp));
		mailClient.setMailStore(store);
		Assert.isTrue(mailClient.sendMail(headers, false),"Failed to send mail");
	}
	
	public void sendTest(SMTPTransport transport,MimeMessage message) throws MessagingException {
		transport.send(message);
	}


	public boolean send( MimeMessage message, 
			Socket smtpPipe, 
			SocketAddress serverAddress)
					throws IOException {


		InputStream inn;
		OutputStream outt;

		if (smtpPipe == null) {
			return false;
		}

		localhost = smtpPipe.getLocalAddress();

		inn = smtpPipe.getInputStream();
		outt = smtpPipe.getOutputStream();
		in = new BufferedReader(new InputStreamReader(inn));
		//out = new PrintWriter(new OutputStreamWriter(outt), true);
		out =   new DataOutputStream(outt);

		if (inn == null || outt == null) {
			System.out.println("Failed to open streams to socket.");
			return false;
		}

		String initialID = in.readLine();
		log.warn("Talking to server \ninitialId: "+initialID);
		System.out.println("HELO " + localhost.getHostName());
		send("HELO " +  "mail.clevercloudcomputing.com");
		send("MAIL FROM: " + "agibson@clevercloudcomputing.com");
		send("RCPT TO: " + "ccc-board@clevercloudcomputing.com");
		send("DATA");
		send(".");
		send("QUIT");
		if(!smtpPipe.isConnected()){
			log.warn("Socket Connection Lost: reconnecting");
			smtpPipe.connect(serverAddress);
		}
		try {
			log.warn("--Preparing to send--");
			log.warn("HostName: "+((InetSocketAddress) serverAddress).getHostName());
			log.warn("Port: "+Integer.toString(((InetSocketAddress) serverAddress).getPort()));
			log.warn("Address: "+((InetSocketAddress) serverAddress).getAddress());
			MimeMessageUtil.writeMessageBodyTo(message, outt);
			// MimeMessageUtil.writeTo(mail.getMessage(),outt,outt);

		} catch (MessagingException e) {
			log.warn("messaging exception from MimeMessageUtil.writeTo()",e);
		}


		String acceptedOK = in.readLine();
		System.out.println(acceptedOK);
		System.out.println("QUIT");
		out.writeBytes("QUIT");
		log.warn("--Message Successfully sent--");

		outt.flush();
		outt.close();
		smtpPipe.close();
		return true;
	}
	public void send(String s) throws IOException
	{
		if (s != null) { 
			out.writeBytes(s);
			out.flush();
		}
		String line;
		if ((line = in.readLine()) != null) //output the response
			System.out.println(line);
		if(line.contains("421")) {
			out.writeBytes(s);
			out.flush();
			if ((line = in.readLine()) != null) //output the response
				System.out.println(line);
		}

	}



	@Test
	@Ignore

	public void testMailClient() {
		Map<String,String> header=testUtils.headersForCCC();
		header.put(MailClient.CONTENT,"Hello Test");
		header.put(MailClient.PORT,"2525");
		header.put(MailClient.TO_ADDRESSES,"ccc-board@clevercloudcomputing.com");
		header.put(MailClient.IS_SSL,"false"); 

	}




	public Server getIncoming() {
		return incoming;
	}


	public void setIncoming(Server incoming) {
		this.incoming = incoming;
	}


	public SMTPServer getOutgoing() {
		return outgoing;
	}


	public void setOutgoing(SMTPServer outgoing) {
		this.outgoing = outgoing;
	}

	public MailClient getMailClient() {
		return mailClient;
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

	public TestUtils getTestUtils() {
		return testUtils;
	}

	public void setTestUtils(TestUtils testUtils) {
		this.testUtils = testUtils;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	@Autowired
	private MailClient mailClient;
	@Autowired
	private SMTPServer outgoing;
	@Qualifier("cccIn")
	@Autowired
	private Server incoming;

	private String userName=null;
	private static Logger log=LoggerFactory.getLogger(MailTest.class);
	int port=2500;

	String mailingListUser="ccc-board@clevercloudcomputing.com";

	String host="mail.clevercloudcomputing.com";

	BufferedReader in;

	DataOutputStream out;

	InetAddress mailHost;
	InetAddress localhost;
	private String password=null;
	@Autowired
	private TestUtils testUtils;
}
