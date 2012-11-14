package com.ccc.mail.core.mailbox;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.util.filesystem.FileMoverUtil;
import com.ccc.util.strings.CSVUtils;

/**
 * This is a utility class for bridging servers to mail storage.
 * 
 * @author Adam Gibson
 * 
 */
public class ServerMailStoreBridge implements MailConstants {

	
	
	
	public static Map<String,String> headersFromMessage(MimeMessage message) throws MessagingException {
		 Map<String,String> ret = new HashMap<String,String>();
		 String body=getMessageBody(message);
		 ret.put(CONTENT, body);
		 ret.put(FROM_ADDRESS, message.getFrom()[0].toString());
		 ret.put(TO_ADDRESSES, csvToAddresses(message));
		 ret.put(SUBJECT, message.getSubject());
		 ret.put(BCC_ADDRESSES, csvBcc(message));
		 ret.put(CC_ADDRESSES, csvCC(message));
		 return ret;
	}
	/**
	 * Returns a comma separated string of all of the bcc addresses for this message.
	 * @param message the message to retrieve addresses from
	 */
	public static String csvBcc(MimeMessage message) throws MessagingException {
		return CSVUtils.arrayToCSV(message.getRecipients(RecipientType.BCC));
	}
	/**
	 * This will return a comma separated string of all of the 
	 * cc addresses on this message
	 * @param message the message to retrieve cc addresses from
	 * @return a comma separated string of all of the cc addresses
	 * for the passed in message.
	 * @throws MessagingException
	 */
	public static String csvCC(MimeMessage message) throws MessagingException {
		return CSVUtils.arrayToCSV(message.getRecipients(RecipientType.CC));
	}
	
	
	/**
	 * This will set the default trust store back to the original value.
	 */
	public static void setDefaultTrustStore(Server s) {
		System.setProperty(
				"mail." + s.getServerType() + ".socketFactory.class",
				"com.ccc.mail.ssl.trustores.DummySSLSocketFactory");

		// System.setProperty(MailConstants.PROTOCOL,
		// MailConstants.SMTP_SERVER);
	}// end setDefaultTrustStore

	/**
	 * This will retrieve a mail store for the passed in protocol using the
	 * passed in properties.
	 * 
	 * @param type
	 *            the type of server to connect to(pop,imap)
	 * @param props
	 *            the properties to use for the session.
	 * @param a
	 *            the authenticator to use
	 * @return a store that can connect to a server,or null on error.
	 */
	public static Store getStoreForServer(Server server, Properties props,
			Authenticator a) {
		Store ret=null;

		ret=storeCache.get(server);
		//if(ret!=null)
		/*
			if(log.isDebugEnabled())
				log.debug("Found cached mail store! returning from cache"); */
		if(ret==null) {

			Session s = Session.getInstance(props, a);
			try {
				if (log.isDebugEnabled()) {
					log.debug("Attempting go get store for server: "
							+ server.getServerName());
					// s.setDebug(true);

				}
				ret= s.getStore(server.getServerType());
				storeCache.put(server,ret);
				if(log.isDebugEnabled()) {
					log.debug("Store not found, adding connection to cache");
				}
			} catch (NoSuchProviderException e) {
				e.printStackTrace();
				log.warn("No such provider: ", e);
			}
		}

		return ret;
	}// end getStoreForServer


	/**
	 * Sets mail writing in these headers to true to auto create folders for maildir
	 * @param headers the headers to set
	 */
	public static void setMailDirWrite(Map<String,String> headers) {
		headers.put(MailClient.WRITE_MAIL_DIR,"true");
	}
	
	
	
	/**
	 * This retrieves the attachments from the given email
	 * 
	 * @param message
	 *            the message to retrieve from
	 * @param pathToPut
	 *            the absolute path to put the files
	 * @return true if the attachments were retrieved, false otherwise
	 * @throws FileNotFoundException
	 *             if a file doesn't exist
	 * @throws IOException
	 *             if one occurs
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static Collection<File> getAttachments(Message message)
			throws FileNotFoundException, IOException, MessagingException {
		Assert.notNull(message);
		List<File> ret = new LinkedList<File>();
		


		Multipart multipart = (Multipart) message.getContent();

		for (int i = 0, n = multipart.getCount(); i < n; i++) {
			Part part = multipart.getBodyPart(i);

			String disposition = part.getDisposition();

			if ((disposition != null)
					&& ((disposition.equals(Part.ATTACHMENT) || (disposition
							.equals(Part.INLINE))))) {
			
						// Write the file
						File f = new File(part.getFileName());
						// Overwrite if necessary
						FileMoverUtil.createFile(f,false);
						FileMoverUtil.copyInputStream(part.getInputStream(),
								new BufferedOutputStream(new FileOutputStream(f)));
						ret.add(f);
			}
		}
		return ret;
	}// end getAttachments
	
	
	
	
	

	/**
	 * This retrieves the attachments from the given email
	 * 
	 * @param message
	 *            the message to retrieve from
	 * @param pathToPut
	 *            the absolute path to put the files
	 * @return true if the attachments were retrieved, false otherwise
	 * @throws FileNotFoundException
	 *             if a file doesn't exist
	 * @throws IOException
	 *             if one occurs
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static boolean getAttachments(Message message, String pathToPut)
			throws FileNotFoundException, IOException, MessagingException {
		Assert.notNull(message);
		Assert.notNull(pathToPut);
		Assert.hasLength(pathToPut);


		Multipart multipart = (Multipart) message.getContent();

		for (int i = 0, n = multipart.getCount(); i < n; i++) {
			Part part = multipart.getBodyPart(i);

			String disposition = part.getDisposition();

			if ((disposition != null)
					&& ((disposition.equals(Part.ATTACHMENT) || (disposition
							.equals(Part.INLINE))))) {
				// Trim the string of trailing slashes
				pathToPut = pathToPut.charAt(pathToPut.length()) == File.separatorChar ? pathToPut
						.substring(0, pathToPut.length() - 1) : pathToPut;
						// Write the file
						File f = new File(pathToPut + File.separator
								+ part.getFileName());
						// Overwrite if necessary
						FileMoverUtil.createFile(f,false);
						FileMoverUtil.copyInputStream(part.getInputStream(),
								new BufferedOutputStream(new FileOutputStream(f)));
			}
		}
		return true;
	}// end getAttachments

	/**
	 * This will forward the given message to the following email addresses.
	 * 
	 * @param message
	 *            the message to forward
	 * @param emails
	 *            the emails to forward to
	 * @param headers
	 *            the headers to use for connection and from address
	 * @throws AddressException
	 *             if one occurs
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static void forwardMessage(Message message, List<String> emails,
			Map<String, String> headers) throws AddressException,
			MessagingException {
		Assert.notNull(message);
		Assert.notNull(emails);
		Assert.notEmpty(emails);
		/*
		Folder f=message.getFolder();
		ServerMailStoreBridge.openFolder(f, true);
		 */
		Properties props = propertiesFromHeaders(headers);
		String from = headers.get(MailClient.FROM_ADDRESS);

		if (isAuth(headers)) {
			String userName = headers.get(MailConstants.USER_NAME);
			String password = headers.get(MailConstants.PASSWORD);

			Session session = Session.getInstance(props,
					new MailServerAuthenticator(userName, password));

			Message forward = new MimeMessage(session);

			// Fill in header
			forward.setSubject("Fwd: " + message.getSubject());
			forward.setFrom(new InternetAddress(from));
			for (String s : emails) {
				forward.addRecipient(Message.RecipientType.TO,
						new InternetAddress(s));
			}
			// Create your new message part
			BodyPart messageBodyPart = new MimeBodyPart();
			messageBodyPart.setText("Forwarded message:\n\n");

			// Create a multi-part to combine the parts
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messageBodyPart);

			// Create and fill part for the forwarded content
			messageBodyPart = new MimeBodyPart();
			messageBodyPart.setDataHandler(message.getDataHandler());

			// Add part to multi part
			multipart.addBodyPart(messageBodyPart);

			// Associate multi-part with message
			forward.setContent(multipart);

			// Send message
			Transport.send(forward);

		}
	}// end forwardMessage

	/**
	 * This returns a store of the given type based on the properties passed in.
	 * 
	 * @param type  the server type to get the store for
	 * @param props
	 * @return
	 */
	public static Store getStoreForServer(Server server, Properties props) {
		Session s = Session.getInstance(props, null);
		try {
			/*
			if (log.isDebugEnabled()) {
				log.debug("Attempting to get store for server: "
						+ server.getServerName());
				// s.setDebug(true);

			}
			 */

			return s.getStore(server.getServerType());
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
			log.warn(
					"Trouble with provider for server: "
							+ server.getServerName(), e);
		}
		return null;
	}// end getStoreForServer

	/**
	 * Returns whether or not DNS Fallback is set
	 * 
	 * @param headers
	 *            connection headers
	 * @return whether or not DNS Fallback is set
	 */
	public static boolean isDNS(Map<String, String> headers) {
		if(headers==null || headers.isEmpty())
			return false;
		String isDns = headers.get(MailConstants.IS_DNS_FALLBACK);
		return (isDns == null || isDns.isEmpty()) ? true : Boolean
				.parseBoolean(isDns);
	}//end isDNS

	/**
	 * This is a method for determining whether the connection headers 
	 * are enabled for debugging or not
	 * @param headers the headers to test
	 * @return true if the headers are set for debug in the map, false if null
	 * or the headers are empty or the debug value is false
	 */
	public static boolean isDebug(Map<String,String> headers) {
		if(headers==null || headers.isEmpty())
			return false;
		String debugString=headers.get(MailClient.DEBUG);
		boolean debug=debugString== null || debugString.isEmpty() ? false : Boolean.parseBoolean(debugString);
		return debug;
	}//end isDebug

	/**
	 * This will return whether the given headers require authentication or not.
	 * 
	 * @param headers
	 *            the headers to use
	 * @return true if authentication is required, false otherwise
	 */
	public static boolean isAuth(Map<String, String> headers) {

		if(headers==null || headers.isEmpty())
			return false;
		String isAuth = headers.get(MailClient.IS_AUTH);
		String userName = headers.get(MailConstants.USER_NAME);
		String password = headers.get(MailConstants.PASSWORD);
		return (isAuth == null ? false : Boolean.parseBoolean(isAuth))
				|| ((userName != null && password != null) ? true : false);
	}// end isAuth

	/**
	 * This will return whether the given headers need SSL or not.
	 * 
	 * @param headers
	 *            the headers to use
	 * @return true if ssl is required, false otherwise
	 */
	public static boolean isSSL(Map<String, String> headers) {
		String isAuth = headers.get(MailClient.IS_SSL);

		return isAuth == null ? false : Boolean.parseBoolean(isAuth);
	}// end isSSL

	/**
	 * This will return whether the given headers required a start tls
	 * connection or not.
	 * 
	 * @param headers
	 *            the headers to use
	 * @return true if tls is required,false otherwise
	 */
	public static boolean isStartTls(Map<String, String> headers) {
		String isAuth = headers.get(MailClient.START_TLS);

		return isAuth == null ? false : Boolean.parseBoolean(isAuth);
	}// end isStartTls

	/**
	 * This returns headers for the given server.
	 * 
	 * @param s
	 *            the server to retrieve headers for
	 * @return null if null is passed in, or the headers for the server.
	 */
	public static Map<String, String> headersForServer(Server s) {
		if (s == null)
			return null;
		Properties props = propertiesForServer(s);

		return headersFromProperties(props);
	}// end headersForServer

	/**
	 * This will divide a give message's content up by disposition
	 * @param m the the message to divide
	 * @return a map of body parts with each type partitioned by their respective names
	 * @throws IOException  if one occurs
	 * @throws MessagingException if one occurs
	 */
	public static Map<String,List<BodyPart>> divideContentByDisposition(Message m) throws IOException, MessagingException {
		Object content=m.getContent();
		return divideContentByDisposition(content);
	}//end divideContentByDisposition
	/**
	 * This will return a text body if it finds one, otherwise
	 * if there are none in the multipart, it returns null
	 * @param part the part to get
	 * @return null if nothing is found, otherwise a string from types html and text/plain
	 * @throws MessagingException if one occurs
	 * @throws IOException if one occurs
	 */
	public static Set<String> findTextInMimeMultiPart(MimeMultipart part) throws MessagingException, IOException {
		return findTextInMimeMultiPart(part,null,new HashSet<String>());
	}
	/* Recursive algorithm for retrieving all parts of text from a mime message, it will still try to cut out any attachments it finds */
	private static Set<String> findTextInMimeMultiPart(MimeMultipart part,BodyPart current,Set<String> list) throws MessagingException, IOException {
		/**
		 * Gradually whiddle down everything, one bit at a time recurisvely.
		 * Grab each body part and process it's content, add anything body related to the list.
		 * Process anything subsequent with further calls
		 */
		if(part==null)
			return null;
		int partCount=part.getCount();
		//Initial case, start off with the first body part
		if(current==null) {
			//either there's a body part or there isn't, if there isn't return
			if(partCount>=1) {
				current=part.getBodyPart(0);
				processBodyPart(current,list);
			}
			else return null;
		}
		for(int i=1;i<partCount;i++) {
			processBodyPart(current,list);

		}

		return list;
	}//end findTextInMimeMultiPart


	private static void processBodyPart(BodyPart currPart,Set<String> list) throws IOException, MessagingException {
		Object currContent=currPart.getContent();
		if(currContent instanceof MimeMultipart) {
			MimeMultipart multicontent=(MimeMultipart) currContent;
			list.addAll(findTextInMimeMultiPart(multicontent,null,list));
		}
		else if(currContent instanceof String) {

			String contentType=currPart.getContentType();
			if(!isAttachment(currPart) && (contentType==null || contentType.toLowerCase().startsWith("text"))) {
				String contentString=(String) currPart.getContent();
				list.add(contentString);
			}
		}
	}


	/**
	 * This will return whether the given body part is an attachment or not
	 * @param part the body part to check
	 * @return true if the body part has a disposition of attachment, otherwise
	 * false
	 * @throws MessagingException if one occurs
	 */
	public static boolean isAttachment(BodyPart part) throws MessagingException {
		if(part==null)
			return false;
		String disposition=part.getDisposition();
		if(disposition!=null) {
			if(disposition.equals(BodyPart.ATTACHMENT))
				return true;
			else if(disposition.equals(BodyPart.INLINE))
				return false;
		}
		if(part.getContentType().toLowerCase().startsWith("text")) 
			return false;


		return true;
	}//end isAttachment


	/**
	 * This will divide a give message's content up by disposition
	 * @param m the the message to divide
	 * @return a map of body parts with each type partitioned by their respective names
	 * @throws IOException  if one occurs
	 * @throws MessagingException if one occurs
	 */
	public static Map<String,List<BodyPart>> divideContentByDisposition(Object content) throws IOException, MessagingException {
		if(content==null)
			return null;
		Map<String,List<BodyPart>> ret = new HashMap<String,List<BodyPart>>();
		if(content!=null) {
			if(content instanceof MimeMultipart) {
				Multipart part=(Multipart) content;
				int count=part.getCount();
				for(int i=0;i<count;i++) {
					BodyPart currPart=part.getBodyPart(i);
					String disposition=currPart.getDisposition();
					if(disposition==null) continue;
					List<BodyPart> currList=ret.get(disposition);
					if(currList==null) {
						currList = new ArrayList<BodyPart>();
						currList.add(currPart);
						ret.put(disposition,currList);
					}


					else currList.add(currPart);
				}

			}
		}


		return ret;
	}//end divideContentByDisposition

	/**
	 * This returns the system properties appended along with the server's host
	 * information.
	 * 
	 * @param s
	 *            the server to get properties for
	 * @return the properties for the given server, or null if null is passed
	 *         in.
	 */
	public static Properties propertiesForServer(Server s) {
		if (s == null)
			return null;
		Properties defaultProps = System.getProperties();
		String host = "mail." + s.getServerType() + ".host";
		String port="mail." + s.getServerType() + ".port";
		defaultProps.put(host, s.getServerAddress());
		defaultProps.put("mail." + s.getServerType() + ".timeout", "120000");
		defaultProps.put(port,s.getPort());
		if(s.isAuth()) {
			defaultProps.put("mail." +s.getServerType() + ".auth","true");
			defaultProps.put(MailClient.IS_AUTH,"true");
		}
		else defaultProps.put("mail." + s.getServerType() + ".auth","false");
		if(s.getEncryptionType()!=null && s.getEncryptionType().toLowerCase().equals("ssl")) {
			defaultProps.put(MailClient.IS_SSL,"true");
		}
		else if(s.getEncryptionType()!=null && s.getEncryptionType().toLowerCase().equals("tls")) {
			defaultProps.put(MailClient.TLS,"true");
			defaultProps.put(MailClient.START_TLS,"true");
		}
		return defaultProps;
	}// end propertiesForServer

	/**
	 * This returns a session based on the given server instance.
	 * 
	 * @param s
	 *            the server to base a session on
	 * @return a session associated with this server
	 */
	public static Session sessionForServer(Server s) {
		Session ret=null;
		ret=sessionCache.get(s);

		if(ret!=null) {
			if(log.isDebugEnabled()) {
				log.debug("Returning session for server: " + s.getServerName());

			}

			return ret;
		}

		if(log.isDebugEnabled())
			log.debug("Returning default instance for server");
		ret= Session.getInstance(propertiesForServer(s));
		sessionCache.put(s,ret);
		return ret;
	}// end sessionForServer

	/**
	 * This sets the start tls property to true in the given properties and
	 * returns it.
	 * 
	 * @param toSet
	 *            the property to set for start tls
	 * @param server
	 *            the type of server to use
	 * @return the new properties
	 */
	public static Properties setStartTLS(Properties toSet, Server s) {
		Assert.notNull(toSet);
		toSet.setProperty("mail" + s.getServerType() + ".starttls.enable",
				"true");
		toSet.setProperty(MailConstants.PROTOCOL, s.getServerType());

		return toSet;
	}// end Properties

	/**
	 * This returns a session for a server requiring authentication.
	 * 
	 * @param s
	 *            the server to get a session for
	 * @param userName
	 *            the user name to authenticate with
	 * @param password
	 *            the password to authenticate with
	 * @return a session with the given parameters
	 */
	public static Session sessionForServerAuth(Server s, String userName,
			String password) {
		Session ret=null;

		ret=sessionCache.get(s);
		if(ret!=null) {
			if(log.isDebugEnabled()) {
				log.debug("Returning cached session!");
			}
			return ret;
		}
		Properties props=propertiesForServer(s);
		if(props!=null)
			ret= Session.getInstance(props,
					new MailServerAuthenticator(userName, password));
		if(log.isDebugEnabled())
			log.debug("Returning new session for server with name: " + s.getServerName()  + " with authentication for: " + userName);
		return ret;
	}// end sessionForServerAuth

	





	/**
	 * This will set up the given server and properties for ssl and test the connection.
	 * @param props the properties to set
	 * @param headers the headers to use for properties
	 * @param s the server to use
	 * @return the new properties
	 */
	public  static Properties setSSL(Properties props,Map<String,String> headers,Server s,boolean doFallBack) {
		return setProperties(props,headers,s,doFallBack);
	}//end setSSL

	private static Properties setProperties(Properties props,Map<String,String> headers,Server s,boolean doFallBack) {



		String setSSLCert=headers.get(SSL_FALLBACK);
		Boolean fallBack=setSSLCert!=null ? Boolean.parseBoolean(setSSLCert) : false;

		//Start tls?
		String startTlsString=(headers.get(START_TLS));
		Boolean startTls=startTlsString !=null ? Boolean.parseBoolean(startTlsString) : false;

		String sslString=headers.get(IS_SSL);
		Boolean ssl=sslString != null ? Boolean.parseBoolean(sslString) : false;		

		boolean illegal= !ssl || (ssl && startTls);

		if(illegal)
			throw new IllegalStateException("SSL and Start TLs not allowed.");
		//Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
		if(fallBack)
			props.put("mail." + s.getServerType() + ".socketFactory.fallback", "false");
		props.setProperty(PROTOCOL, s.getServerType());

		//Initialize properties
		props.put(SEND_PORT_PROPERTY, s.getPort());
		props.put(SMTP_SSL_PORT_PROPERTY,s.getPort());
		props.put("mail." + s.getServerType() + ".timeout", "120000"); 
		try {
			props.put(SMTP_SSL_HOST_PROPERTY, s.getServerName());
		}catch(Exception e) {
			e.printStackTrace();
		}
		props.put("mail." +  s.getServerType() + ".ssl.trust","*");
		props.put("mail." + s.getServerType() + ".ssl.checkserveridentity","false");
		try {
			props.put("mail." + s.getServerType() + ".host", s.getServerName());
		}
		catch(NullPointerException e) {
			System.out.println("Null pointer for: " + s!=null ? s.getServerName() : "");
		}
		props.put("mail." + s.getServerType() + ".socketFactory.port", s.getPort());
		if(fallBack)
			props.put("mail." + s.getServerType() + ".socketFactory.class",
					"com.ccc.mail.ssl.truststores.DummySSLSocketFactory");
		props.put("mail." + s.getServerType() + ".auth", "true");
		props.put("mail." +s.getServerType() + ".port", s.getPort());
		//Ensure start tls is off
		props.put(TLS, "false");

		//Custom keystore combo
		String keyStoreLoc=headers.get(KEYSTORE_LOCATION);
		String keyStorePass=headers.get(KEYSTORE_PASSWORD);



		if(fallBack) {
			props.setProperty( "mail" +s.getServerType() + " + .socketFactory.class", "com.ccc.mail.ssl.truststores.DummySSLSocketFactory" );


		}

		//Check for custom key store
		if(keyStoreLoc!=null) {
			props.put(KEYSTORE_LOCATION,keyStoreLoc);
			props.put(KEYSTORE_PASSWORD,keyStorePass);
		}
		System.setProperties(props);

		return props;



	}




	/**
	 * This will form a mail session from the given headers and server
	 * @param server the server to use
	 * @param headers the headers to use
	 * @return a mail session based on the given server and connection headers
	 */
	public static Session formSession(Server server,Map<String,String> headers) {
		Properties props=System.getProperties();
		Session s1=null;
		String debugString=headers.get(DEBUG);
		Boolean debug=debugString!=null ? Boolean.parseBoolean(debugString) : false;


		//Trust all certs here?
		String sslFallBack=headers.get(SSL_FALLBACK);

		Boolean doFallBack=sslFallBack!=null  ? Boolean.parseBoolean(sslFallBack) : false;

		//Straight ssl
		String sslString=headers.get(IS_SSL);
		Boolean ssl=sslString != null ? Boolean.parseBoolean(sslString) : false;
		//Authorization required?
		String authString=headers.get(IS_AUTH);
		Boolean auth=authString != null ? Boolean .parseBoolean(authString) : false;

		//Start tls?
		String startTlsString=(headers.get(START_TLS));
		Boolean startTls=startTlsString !=null ? Boolean.parseBoolean(startTlsString) : false;
		//Not allowed
		if(ssl && startTls)
			throw new IllegalStateException("SSL and Start TLS not allowed.");

		//Initialize ssl
		if(ssl) {
			props=setSSL(props,headers,server,doFallBack);
			//Assert.isTrue(props.get(IS_SSL).equals("true"));
			if(log.isDebugEnabled())
				log.debug("Initialized SSL for server: " + server.getServerName());
		}
		//Initialize start tls
		if(startTls) {
			props=ServerMailStoreBridge.setStartTLS(props,server);
			Assert.isTrue(props.get(START_TLS).equals("true"));
			if(log.isDebugEnabled())
				log.debug("Initiated start tls for server: " + server.getServerName());
		}

		//Initialize authorized session
		if(auth) {
			String userName=headers.get(USER_NAME);
			String password=headers.get(PASSWORD);
			s1=Session.getInstance(props, new MailServerAuthenticator(userName,password));
			if(log.isDebugEnabled())
				log.debug("Initialized authenticated session for User: " + userName);
		}

		//No authentication
		else s1=Session.getInstance(props, null);

		if(debug)
			s1.setDebug(true);

		return s1;
	}//end formSession


	/**
	 * This will set the delete flag to true. Note that the message will still
	 * have to be expunged from the server.
	 * 
	 * @param toDelete
	 *            the message to delete
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if toDelete is null
	 */
	public static void deleteMessage(Message toDelete)
			throws MessagingException, IllegalArgumentException {
		Assert.notNull(toDelete);
		toDelete.setFlag(Flags.Flag.DELETED, true);
		MimeMessage newmsg = new MimeMessage((MimeMessage) toDelete);
		newmsg.saveChanges();
	}// end deleteMessage

	/**
	 * This will flag this message as a draft.
	 * 
	 * @param toDraft
	 *            the message to set as a draft.
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if toDraft is null
	 */
	public static void setDraft(Message toDraft) throws MessagingException,
	IllegalArgumentException {
		Assert.notNull(toDraft);
		toDraft.setFlag(Flags.Flag.DRAFT, true);
		MimeMessage newmsg = new MimeMessage((MimeMessage) toDraft);
		newmsg.saveChanges();
	}// end setDraft

	/**
	 * This returns whether the message is a user message or not.
	 * 
	 * @param userMessage
	 *            the user message
	 * @return true if this is a user message, false otherwise
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if userMessage is null
	 */
	public static boolean messageForUser(Message userMessage)
			throws MessagingException, IllegalArgumentException {
		return userMessage.getFlags().contains(Flags.Flag.USER);
	}// end messageForUser

	/**
	 * This returns whether the given message is recent or not
	 * 
	 * @param recent
	 *            the message to test
	 * @return true if the message is recent,false otherwise
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if recent is null
	 */
	public static boolean messageRecent(Message recent)
			throws MessagingException, IllegalArgumentException {
		Assert.notNull(recent);
		return recent.getFlags().contains(Flags.Flag.RECENT);

	}// end messageRecent

	/**
	 * This returns whether the given message is flagged or not.
	 * 
	 * @param message
	 *            the message to check
	 * @return true if the message is flagged, false otherwise
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static boolean messageFlagged(Message message)
			throws MessagingException {
		Assert.notNull(message);
		return message.getFlags().contains(Flags.Flag.FLAGGED);
	}// end messageFlagged

	/**
	 * This returns true if the given message has attachments or not
	 * 
	 * @param message
	 *            the message to check
	 * @return true if the message has attachments (more than one body part),
	 *         false otherwise
	 * @throws IOException
	 *             if one occurs
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static boolean messageHasAttachments(Message message)
			throws IOException, MessagingException {
		/*
		Folder f = message.getFolder();

		ServerMailStoreBridge.openFolder(f, false);
		 */
		Object content = message.getContent();

		if (content instanceof Multipart) {
			Multipart part = (Multipart) content;
			return part.getCount() > 1;
		}
		// ServerMailStoreBridge.closeFolder(f);
		return false;
	}// end messageHasAttachments

	/**
	 * This will return a comma separated list of the to addresses for this
	 * message.
	 * 
	 * @param message
	 *            the message to retrieve the addresses from
	 * @return a comma separated list of the to addresses for this message
	 */
	public static String csvToAddresses(Message message) {
		/*
		Folder f = message.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		MimeMessage m = (MimeMessage) message;

		try {
			return m.getHeader("To", ",");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		return "";
	}// end csvToAddresses

	/**
	 * This will return the message body of the given message.
	 * 
	 * @param m
	 *            the message to retrieve the body from
	 * @return the message body for the given message
	 */
	public static String getMessageBody(Message m) {
		Object content = null;

		try {
			content = m.getContent();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		// only has a body, just return it
		if (content instanceof String)
			return (String) content;
		else {
			// multipart, we have to find the message body
			if (content instanceof Multipart) {
				try {
					Multipart mp = (Multipart) content;

					for (int k = 0, n = mp.getCount(); k < n; k++) {
						Part part = mp.getBodyPart(k);
						if (part == null)
							continue;
						String disposition = part.getDisposition();
						if (disposition != null) {
							if (part.isMimeType("text/plain")
									|| part.isMimeType("text/html"))
								return part.toString();

						}// end for
					}

					return null;
				} catch (MessagingException e) {
					e.printStackTrace();

				}
			}
		}
		return null;

	}// end getMessageBody

	/**
	 * This handles the boiler plate code for closing a folder;
	 * 
	 * @param f
	 *            the folder to attempt to close
	 */
	public static void closeFolder(Folder f) {
		try {
			if (f != null && f.exists() && f.isOpen())

				f.close(true);
		} catch (MessagingException e) {
			e.printStackTrace();
			log.warn("Error closing folder: " + f.getFullName());
		}
	}// end closeFolder

	/**
	 * This handles the boilerplate for opening a folder.
	 * 
	 * @param f
	 *            the folder to open
	 * @param readOnly
	 *            whether the folder is read only or not
	 */
	public static void openFolder(Folder f, boolean readOnly) {
		try {
			if (f != null && f.exists() && !f.isOpen()) {
				int mode = readOnly ? Folder.READ_ONLY : Folder.READ_WRITE;
				f.open(mode);
			}
		} catch (MessagingException e) {
			e.printStackTrace();
			log.warn("Error opening folder: " + f.getFullName());
		}
	}// end openFolder

	/**
	 * This will append the messages subject and content together
	 * 
	 * @param m
	 *            the message to derive from
	 * @return the messages subject and if content length is > 100 10% of the
	 *         content, otherwise all of the content
	 * @throws IOException
	 *             if an error occurs
	 * @throws MessagingException
	 *             if an error occurs
	 */
	public static String concatSubjectFromDisplay(Message m)
			throws MessagingException {
		Assert.notNull(m);
		/*
		Folder f = m.getFolder();

		ServerMailStoreBridge.openFolder(f, false);
		 */
		String content =null;
		try {
		content= getMessageBody(m);
		}catch(Exception e) {
			log.warn(e);
			content=null;
		}
		String subject = m.getSubject();
		if (content == null)
			return "";
		int length = content.length();

		if (length > 100) {
			String ret = subject;
			ret += "-  ";
			// Try to append 10%, if the length is still
			// greater, truncate it.
			int tenPercentOfLength = (int) (length * .1);
			if (tenPercentOfLength > 100)
				tenPercentOfLength = 100;
			String toAppend = content.substring(0, tenPercentOfLength);
			ret += toAppend;
			return ret;
		} else {
			// closeFolder(f);

			return subject + " -  " + content;
		}

	}// end concatSubject

	/**
	 * This will return a comma separated list of the from addresses for this
	 * message.
	 * 
	 * @param message
	 *            the message to retrieve the addresses from
	 * @return a comma separated list of the from addresses for this message
	 */
	public static String csvFromAddresses(Message message) {
		/*
		Folder f = message.getFolder();

		ServerMailStoreBridge.openFolder(f, false);
		 */
		MimeMessage m = (MimeMessage) message;
		try {
			return m.getHeader("From", ",");
		} catch (MessagingException e) {
			e.printStackTrace();
		}
		// ServerMailStoreBridge.closeFolder(f);
		return "";
	}// end csvToAddresses

	/**
	 * This will return the attachment names of the given message, or null on
	 * error
	 * 
	 * @param message
	 *            the message to retrieve attachment names from
	 * @return a comma separated list of attachment names, or null on error
	 */
	public static String getAttachmentNames(Message message) {
		/*
		Folder f = message.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		Object content = null;

		try {
			content = message.getContent();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (MessagingException e1) {
			e1.printStackTrace();
		}
		if (content != null && content instanceof MimeMultipart) {
			StringBuffer fileNames = new StringBuffer();
			// Extract attachments
			try {
				Multipart mp = (Multipart) message.getContent();

				for (int k = 0, n = mp.getCount(); k < n; k++) {
					Part part = mp.getBodyPart(k);
					if (part == null)
						continue;
					String disposition = part.getDisposition();
					if ((disposition != null)
							&& (disposition.equals(Part.ATTACHMENT) || (disposition
									.equals(Part.INLINE)))) {
						String fileName = part.getFileName();
						if (fileName == null) {
							// System.out.println("Debug");
							continue;
						}
						fileNames.append(part.getFileName());
						if (k < n - 1) {
							fileNames.append(",");
						}

					}// end for
				}
				// ServerMailStoreBridge.closeFolder(f);
				return fileNames.toString();
			} catch (MessagingException e) {
				e.printStackTrace();
				/*
				log.warn(
						"Error retrieving attachments from folder: "
								+ f.getFullName(), e);
				 */
			} catch (IOException e) {
				e.printStackTrace();
				/*
				log.warn(
						"Error retrieving attachments from folder: "
								+ f.getFullName(), e);
				 */
			}
		}
		return null;
	}// end getAttachmentNames

	/**
	 * This returns whether the given message has been seen or not.
	 * 
	 * @param seenOrNot
	 *            the message to test
	 * @return true if the message was seen, false otherwise
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if the given message is null
	 */
	public static boolean messageSeen(Message seenOrNot)
			throws MessagingException, IllegalArgumentException {
		Assert.notNull(seenOrNot);
		/*
		Folder f = seenOrNot.getFolder();
		ServerMailStoreBridge.openFolder(f, true);
		 */
		return seenOrNot.getFlags().contains(Flags.Flag.SEEN);
	}// end messageSeen

	/**
	 * This will set the flag for the given message to true.
	 * 
	 * @param toFlag
	 *            the message to flag
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static void toggleFlag(Message toFlag) throws MessagingException {
		Assert.notNull(toFlag);
		Folder f = toFlag.getFolder();
		ServerMailStoreBridge.openFolder(f, false);

		toFlag.setFlag(Flags.Flag.FLAGGED, true);
		MimeMessage newmsg = new MimeMessage((MimeMessage) toFlag);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end toggleFlag

	/**
	 * 
	 * @param toSet
	 *            the message to set bccs for
	 * @param bccs
	 *            a comma separated list of email addresses to use for ccs
	 * @throws AddressException
	 *             if one is thrown
	 * @throws MessagingException
	 *             if one is thrown
	 * @throws IllegalArgumentException
	 *             if any of the arguments are null, or bccs has no length
	 */
	public static void setBcc(Message toSet, String bccs)
			throws AddressException, MessagingException,
			IllegalArgumentException {
		Assert.notNull(toSet);
		Assert.notNull(bccs);
		Assert.hasLength(bccs);
		/*
		Folder f = toSet.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		String[] s = bccs.split(",");
		for (String s1 : s) {
			toSet.addRecipient(RecipientType.BCC, new InternetAddress(s1));

			if (log.isDebugEnabled())
				log.debug("Added recipient: for BCC: " + s);

		}
		MimeMessage newmsg = new MimeMessage((MimeMessage) toSet);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end setBcc

	/**
	 * This will return the folder with the given name on the given server.
	 * 
	 * @param server
	 *            the server to retrieve the folder from
	 * @param name
	 *            the name of the folder to retrieve
	 * @param props
	 *            the properties to use for connecting
	 * @return the folder on the server if it exists, or false if an error
	 *         occurs
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static Folder folderWithName(Server server, String name,
			Properties props) throws MessagingException {
		Assert.notNull(server);
		Assert.notNull(name);
		Assert.notNull(props);

		Store s = getStoreForServer(server, props);

		return s.getFolder(name);

	}// end folderWithName

	/**
	 * This will translate the given headers to properties. NOTE: THIS WILL MAKE
	 * THE ASSUMPTION OF THE SYSTEM PROPERTIES THESE CASES. OVERRIDE THE
	 * PROPERTIES NEEDED IF NECESSARY.
	 * 
	 * @param headers
	 *            the headers to translate
	 * @return the derived properties
	 */
	public static Properties propertiesFromHeaders(Map<String, String> headers) {
		Assert.notNull(headers);
		Properties p = System.getProperties();
		for (String s : headers.keySet()) {
			if (s != null)
				p.put(s, headers.get(s));

		}
		return p;
	}// end propertiesFromHeaders

	/**
	 * This will return the equivalent headers based on the given properties.
	 * 
	 * @param props
	 *            the properties to derive headers from
	 * @return the headers for the given properties
	 */
	public static Map<String, String> headersFromProperties(Properties props) {
		Assert.notNull(props);
		Map<String, String> ret = new HashMap<String, String>();
		for (Object o : props.keySet()) {
			String s = (String) o;
			ret.put(s, String.valueOf(props.get(o)));
		}
		return ret;
	}// end headersFromProperties

	/**
	 * 
	 * @param toSet
	 *            the message to set to for
	 * @param toAddresses
	 *            a comma separated list of email addresses to use for send to
	 *            addresses
	 * @throws AddressException
	 *             if one is thrown
	 * @throws MessagingException
	 *             if one is thrown
	 * @throws IllegalArgumentException
	 *             if any of the arguments are null, or toAddresses has no
	 *             length
	 */
	public static void setTo(Message toSet, String toAddresses)
			throws AddressException, MessagingException,
			IllegalArgumentException {
		Assert.notNull(toSet);
		Assert.notNull(toAddresses);
		Assert.hasLength(toAddresses);
		Folder f = toSet.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		String[] s = toAddresses.split(",");
		for (String s1 : s) {
			toSet.addRecipient(RecipientType.TO, new InternetAddress(s1));
			if (log.isDebugEnabled())
				log.debug("Added to address: " + s);
		}
		MimeMessage newmsg = new MimeMessage((MimeMessage) toSet);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end setTo

	/**
	 * This will set the context type of the message to html
	 * 
	 * @param toSet
	 *            the message to set
	 * @param content
	 *            the html to use
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static void setHTMLMessage(Message toSet, Object content)
			throws MessagingException {
		Assert.notNull(toSet);
		Assert.notNull(content);
		/*
		Folder f = toSet.getFolder();
		ServerMailStoreBridge.openFolder(f, true);
		 */
		Map<String, String> mimeTypes = FileMoverUtil.getMimeTypes();
		toSet.setContent(content, mimeTypes.get("html"));
		if (log.isDebugEnabled())
			log.debug("Added html message type");
		MimeMessage newmsg = new MimeMessage((MimeMessage) toSet);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end setHTMLMessage

	/**
	 * This will attach files to the given message and use the given text as
	 * another part of the body.
	 * 
	 * @param attachments
	 *            the files to attach
	 * @param attachTo
	 *            the message to attach to
	 * @param text
	 *            the text to use for the body
	 * @throws MessagingException
	 *             if one occurs
	 */
	public static void attachFiles(File[] attachments, Message attachTo,
			String text) throws MessagingException {
		Assert.notNull(attachments);
		Assert.notNull(attachTo);
		if(text==null)
			text="";
		/*
		Folder f = attachTo.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		attachTo.setText(text);
		Multipart multipart = new MimeMultipart();

		MimeBodyPart messageBodyPart = new MimeBodyPart();
		messageBodyPart.setText(text);
		multipart.addBodyPart(messageBodyPart);
		MimeBodyPart attachments2 = null;
		// Part two is attachment
		for (int i = 0; i < attachments.length; i++) {
			attachments2 = new MimeBodyPart();
			if (attachments[i] != null) {
				FileDataSource fileDataSource = new FileDataSource(
						attachments[i]);

				attachments2.setDataHandler(new DataHandler(fileDataSource));
				attachments2.setFileName(attachments[i].getName());
				multipart.addBodyPart(attachments2);
			}

			if (log.isDebugEnabled())
				if (attachments[i] != null)
					log.debug("Attached file: " + attachments[i].getName());
		}// end for

		// Put parts in message
		attachTo.setContent(multipart);
		log.info("Attached files!");
		MimeMessage newmsg = new MimeMessage((MimeMessage) attachTo);
		newmsg.saveChanges();

	}// end attachFiles

	/**
	 * This will set the ccs for a message.
	 * 
	 * @param toSet
	 *            the message to set ccs for
	 * @param cc
	 *            a comma separated list of email addresses
	 * @throws AddressException
	 *             if one is thrown
	 * @throws MessagingException
	 *             if one is thrown
	 * @throws if
	 *             any of the arguments are null
	 */
	public static void setCC(Message toSet, String cc) throws AddressException,
	MessagingException, IllegalArgumentException {
		Assert.notNull(toSet);
		Assert.notNull(cc);
		Assert.hasLength(cc);
		String[] s = cc.split(",");
		/*
		Folder f = toSet.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		for (String s1 : s) {
			toSet.addRecipient(RecipientType.CC, new InternetAddress(s1));

			if (log.isDebugEnabled())
				log.debug("Added to: " + s);

		}
		MimeMessage newmsg = new MimeMessage((MimeMessage) toSet);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end setCC

	/**
	 * This will flag a message
	 * 
	 * @param toFlag
	 *            the message to flag
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if the given message is null
	 */
	public static void flagMessage(Message toFlag) throws MessagingException,
	IllegalArgumentException {
		toFlag.setFlag(Flags.Flag.FLAGGED, true);
		/*
		Folder f = toFlag.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		if (log.isDebugEnabled())
			log.debug("Flagged message.");
		MimeMessage newmsg = new MimeMessage((MimeMessage) toFlag);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end flagMessage

	/**
	 * This will set text mail using the given text for the given message.
	 * 
	 * @param toSet
	 *            the message to set
	 * @param text
	 *            the text to use
	 * @throws MessagingException
	 *             if one occurs
	 * @throws IllegalArgumentException
	 *             if toSet is null
	 */
	public static void setTextMail(Message toSet, String text)
			throws MessagingException, IllegalArgumentException {
		Assert.notNull(toSet);
		/*
		Folder f = toSet.getFolder();
		ServerMailStoreBridge.openFolder(f, false);
		 */
		toSet.setContent(text, "text/plain");
		MimeMessage newmsg = new MimeMessage((MimeMessage) toSet);
		newmsg.saveChanges();
		// ServerMailStoreBridge.closeFolder(f);
	}// end setTextMail

	/**
	 * This will tell whether the given headers are set for ssl fall back or
	 * not.
	 * 
	 * @param headers
	 *            the headers to test
	 * @return true if the headers are set for ssl fall back, or false if empty
	 *         or doesn't contain ssl fall back
	 */
	public static boolean isSSLFallBack(Map<String, String> headers) {
		Assert.notNull(headers);
		if (headers.isEmpty())
			return false;
		return headers.get(MailConstants.SSL_FALLBACK) != null;
	}// end isSSLFallBack



	private static Log log = LogFactory.getLog(ServerMailStoreBridge.class);

	private static Map<Server, Store> storeCache = new HashMap<Server, Store>();
	private static Map<Server,Session> sessionCache = new HashMap<Server,Session>();
}// end ServerMailStoreBridge
