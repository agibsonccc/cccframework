package com.ccc.ccm.client;

public class ConnectionConstants {

	/**
	 * The stomp protocol:
	 * See: http://stomp.codehaus.org/Protocol
	 */
	public final static String STOMP="stomp";
	/**
	 * Traditional TCP/UDP
	 */
	public final static String TCP="tcp";
	/**
	 * A JVM connector. This means the clients run in the same
	 * virtual machine as the brokers.
	 */
	public final static String VM="vm";
	/**
	 * The default port for activemq.
	 */
	public final static int DEFAULT_PORT=61616;
	/**
	 * Whether the broker persists messages or not
	 */
	public final static String PERSISTENT_PARAMETER="persistent";
	/**
	 * the name of a broker
	 */
	public final static String BROKER_NAME_PARAMETER="brokerName";
	/**
	 * Whether to deserialize sent objects with messages
	 */
	public final static String MARSHAL="marshal";

	/**
	 * This is for specifying a configuration file for a given broker.
	 */
	public final static String BROKER_CONFIG_FILE="brokerConfig";
	/**
	 * This is for starting peer to peer connections.
	 */
	public final static String PEER_CONNECTION="peer";
	/**
	This is a parameter specifying whether objects sent should be logged.

	 */
	public final static String LOG="trace";
	/**
	 * Default value for logging
	 */
	public final static String LOG_DEFAULT_VALUE="false";

	/**
	 * This specifies the buffer size for a socket
	 * connection.
	 */
	public final static String BUFFER_SIZE="socketBufferSize";

	/**
	 * This sets the socket time out for a connection. 
	 * A connection will only block for the specified amount 
	 * of time and then a SocketTimeOUtException is thrown
	 */
	public final static String SOCKET_TIME_OUT="soTimeout";
	/**
	 * Name of the wire format to use.
	 */
	public final static String WIRE_FORMAT="wireFormat ";
	/**
	 * This specifies the connection time out for a given connection.
	 * Negative values aren't accepted and 0 means wait forever.
	 */
	public final static String CONNECTION_TIME_OUT="connectionTimeout";

	/**
	 * Java's New I/O protocol See:
	 * http://en.wikipedia.org/wiki/New_I/O
		http://www.javaworld.com/javaworld/jw-09-2001/jw-0907-merlin.html?page=1

	 */
	public final static String NIO="nio";

	/**
	 * SSL connection
	 */
	public final static String SSL="ssl";
	/**
	 * HTTP protocol
	 */
	public final static String HTTP="http";
	/**
	 * Typical ssl http protocol
	 */
	public final static String HTTPS="https";
	/**
	 * The multicast low-level connector provides a means for brokers and clients located on the same
		LAN subnet to find each other and establish connections.

	 */
	public final static String MULTI_CAST="multicast";

	/**
	 * The ActiveMQ rendezvous low-level connector is an alternative to multicast as a means to
	discover clients or brokers. The rendezvous connector uses jmDNS, the Java implementation of
	multi-cast DNS. JmDNS is fully compatible with Apple‟s Bonjour (a.k.a. Rendezvous) zero-
	configuration protocol. The rendezvous connector can be used by a client to find brokers, and by
	brokers to find other brokers.

	 */
	public final static String RENDEVOUS="rendezvous";

	/**
	 * The composite „static‟ connector is typically used within a <networkConnector> element and
	includes one or more low-level connector URIs. The message broker establishes a forwarding
		bridge for each low level URI that is listed in the static connector.

	 */
	public final static String STATIC="static";
	
	/**
	 * With URI wild cards, this is used to separate paths.
	 */
	public final static String SEPARATOR=".";
	/**
	 * This will match anything on the path.
	 */
	public final static String MATCH_ANY="*";
	/**
	 * This will match anything such that:
	 * foo.> 
	 * is anything beginning with the foo path
	 */
	public final static String MATCH_ANY_WITH_VALUE=">";
}
