package com.ccc.mail.core.servers.storage;


public interface MailConstants {
	/**
	 * This is the default class for sending ssl mail.
	 */
	public static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	/**
	 * JVM property for the transport protocol to use.
	 */
	public final static String PROTOCOL="mail.transport.protocol";
	/**
	 * JVM property for an smtp host
	 */
	public final static String SMTP_HOST_PROPERTY="mail.smtp.host";
	/**
	 * JVM property for an smtps host
	 */
	public final static String SMTP_SSL_HOST_PROPERTY="mail.smtps.host";
	/**
	 * JVM property for an imaps host
	 */
	public final static String IMAP_SSL_HOST_PROPERTY="mail.imaps.host";
	/**
	 * JVM property for a pops host
	 */
	public final static String POP_SSL_HOST_PROPERTY="mail.pop3s.host";
	/**
	 * JVM property for smtps auth
	 */
	public final static String SMTP_SSL_AUTH_PROPERTY="mail.smtps.auth";
	
	
	/**
	 * This is the smtp ssl protocol. 
	 * 
	 */
	public final static String SMTP_SSL="smtps";
	
	/**
	 * This is the imap ssl protocol.
	 */
	public final static String IMAP_SSL="imaps";
	
	/**
	 * This is the pop3 ssl protocol/
	 */
	public final static String POP_SSL="pop3s";
	
	/**
	 * This is the default java home from the jvm
	 */
	public final static String DEFAULT_JAVA_HOME=System.getProperty("java.home");
	/**
	 * This is the default trust store for this application.
	 */
	public final static String DEFAULT_TRUST_STORE=DEFAULT_JAVA_HOME + "/lib/security/cacerts";
	/**
	 * This is the default password for a java trust store.
	 */
	public final static String DEFAULT_KEY_STORE_PASS="changeit";
	
	/**
	 * This is the key property for setting the port for an encrypted smtp port.
	 */
	public final static String SMTP_SSL_PORT_PROPERTY="mail.smtps.port";

	/**
	 * JVM property for Start Tls
	 */
	public final static String TLS="mail.smtp.starttls.enable";

	
	/**
	 * This is the imap host property.
	 */
	public final static String IMAP_HOST_PROPERTY="mail.imap.host";

	/**
	 * This is the pop host property.
	 */
	public final static String POP_HOST_PROPERTY="mail.pop.host";
	/**
	 * This is the authorization property for whether a user
	 * needs to be authenticated or not.
	 */
	public final static String AUTH_PROPERTY="mail.smtp.auth";
	/**
	 * This is used to initialize ssl mail.
	 */
	public final static String SOCKET_FACTORY_CLASS="mail.smtp.socketFactory.class";

	
	
	public final static String SSL_SMTP_PROTOCOL_VALUE="smtps";
	
	
	public final static String IMAP_SERVER="imap";

	public final static String POP_SERVER="pop3";


	public final static String SMTP_SERVER="smtp";


	public final static String SEND_PORT_PROPERTY="mail.smtp.socketFactory.port";

	/**
	 * This is the default imap port unencrypted
	 */
	public final static int UNENCRYPTED_DEFAULT_IMAP_PORT=143;
	/**
	 * This is the default encrypted imap port.
	 */
	public final static int ENCRYPTED_DEFAULT_IMAP_PORT=993;
	/**
	 * This is the default unencrypted smtp port.
	 */
	public final static int UNENCRYPTED_DEFAULT_SMTP_PORT=25;
	/**
	 * This is the default encrypted smtp port.
	 */
	public final static int ENCRYPTED_DEFAULT_SMTP_PORT=465;
	/**
	 * This is the default encrypted pop port.
	 */
	public final static int ENCRYPTED_DEFAULT_POP_PORT=995;
	/**
	 * This is the default unencrypted pop port.
	 */
	public final static int UNENCRYPTED_DEFAULT_POP_PORT=110;
	/**
	 * This is the jvm property for a setting or getting a trust store.
	 */
	public final static String KEYSTORE_LOCATION="javax.net.ssl.trustStore";
	/**
	 * This is the jvm property for setting or getting a trust store password.
	 */
	public final static String KEYSTORE_PASSWORD="javax.net.ssl.trustStorePassword";
	
	/**
	 * User name for a user for authentication
	 */
	public final static String USER_NAME="userName";
	/**
	 * Password for a user for authentication
	 */
	public final static String PASSWORD="password";
	
	/**
	 * This is a key for headers determining whether
	 * all certs should be trusted.
	 */
	public final static String SSL_FALLBACK="fallback";
	
	/**
	 * This is a constant for determining whether
	 * a session needs to be in debug mode or not.
	 */
	public final static String DEBUG="debug";
	
	/**
	 * Key to determine if dns needs a fallback to an address
	 */
	public final static String IS_DNS_FALLBACK="dnsfallback";
	
	/**
	 * This is a server ip address.
	 */
	public final static String SERVER_ADDRESS="serverAddress";
	
	/**
	 * This is a server domain name.
	 */
	public final static String SERVER_NAME="serverName";
	
	/**
	 * This is for a server protocol. (IMAP,POP,HTTP,SMTP,...)
	 */
	public final static String SERVER_TYPE="serverType";
	/**
	 * From name on a mail address
	 */
	public final static String FROM_NAME="fromName";
	
	/**
	 * This is the key for a comma separated list of email addresses
	 */
	public final static String TO_ADDRESSES="to";
	/**
	 * This is a key for a comma separated list of
	 * from addresses
	 */
	public final static String FROM_ADDRESS="from";
	/**
	 * This is a key for a comma separated list of
	 * email addresses to cc
	 */
	public final static String CC_ADDRESSES="cc";
	/**
	 * This is a key fro a comma separated list of 
	 * email addresses to bcc
	 */
	public final static String BCC_ADDRESSES="bcc";
	/**
	 * This is a key for a subject
	 */
	public final static String SUBJECT="subject";
	/**
	 * This is a key for attachments
	 */
	public final static String ATTACHMENTS="attachments";
	/**
	 * This is a key to determine whether 
	 * there is ssl or not
	 */
	public final static String IS_SSL="ssl";
	/**
	 * This is a key for determining what 
	 * a mail port is
	 */
	public final static String PORT="port";
	/**
	 * This is a key for determining whether
	 * a given mail server requires authentication
	 */
	public final static String IS_AUTH="auth";

	/**
	 * This is a key to set the content of an email 
	 * message
	 */
	public final static String CONTENT="content";
	/**
	 * This is a key for setting whether an email
	 * is flagged or not
	 */
	public final static String FLAGGED="flagged";

	/**
	 * This is the key to determine whether there 
	 * is smtp auth or not
	 */
	public final static String START_TLS="mail.smtp.starttls.enable";
	
	
}//end MailConstants
