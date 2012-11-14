package com.ccc.ccm.client.activemq;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.naming.Context;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.activemq.Service;
import org.apache.activemq.ThreadPriorities;
import org.apache.activemq.management.JMSStatsImpl;
import org.apache.activemq.pool.ConnectionKey;
import org.apache.activemq.pool.ConnectionPool;
import org.apache.activemq.pool.PooledConnection;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.protobuf.compiler.IntrospectionSupport;
import org.apache.activemq.util.IOExceptionSupport;
import org.apache.activemq.util.IdGenerator;
import org.apache.activemq.util.URISupport;
import org.apache.activemq.util.URISupport.CompositeData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.pool.ObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPoolFactory;

/**
 * A JMS provider which pools Connection, Session and MessageProducer instances
 * so it can be used with tools like <a href="http://camel.apache.org/activemq.html">Camel</a> and Spring's <a
 * href="http://activemq.apache.org/spring-support.html">JmsTemplate and MessagListenerContainer</a>.
 * Connections, sessions and producers are returned to a pool after use so that they can be reused later
 * without having to undergo the cost of creating them again.
 * 
 * b>NOTE:</b> while this implementation does allow the creation of a collection of active consumers,
 * it does not 'pool' consumers. Pooling makes sense for connections, sessions and producers, which 
 * are expensive to create and can remain idle a minimal cost. Consumers, on the other hand, are usually
 * just created at startup and left active, handling incoming messages as they come. When a consumer is
 * complete, it is best to close it rather than return it to a pool for later reuse: this is because, 
 * even if a consumer is idle, ActiveMQ will keep delivering messages to the consumer's prefetch buffer,
 * where they'll get held until the consumer is active again.
 * 
 * If you are creating a collection of consumers (for example, for multi-threaded message consumption), you
 * might want to consider using a lower prefetch value for each consumer (e.g. 10 or 20), to ensure that 
 * all messages don't end up going to just one of the consumers. See this FAQ entry for more detail: 
 * http://activemq.apache.org/i-do-not-receive-messages-in-my-second-consumer.html
 * 
 * @org.apache.xbean.XBean element="pooledConnectionFactory"
 * 
 * 
 */
public class StoreablePooledConnectionFactory extends org.apache.activemq.jndi.JNDIBaseStorable implements ConnectionFactory, Service ,Serializable {
	private static final transient Logger LOG = LoggerFactory.getLogger(PooledConnectionFactory.class);
	private ConnectionFactory connectionFactory;
	private Map<ConnectionKey, LinkedList<ConnectionPool>> cache = new HashMap<ConnectionKey, LinkedList<ConnectionPool>>();
	private ObjectPoolFactory poolFactory;
	private int maximumActive = 500;
	private int maxConnections = 1;
	private int idleTimeout = 30 * 1000;
	private AtomicBoolean stopped = new AtomicBoolean(false);
	private long expiryTimeout = 0l;

	public StoreablePooledConnectionFactory() {
		this(new ActiveMQConnectionFactory());
	}

	public StoreablePooledConnectionFactory(String brokerURL) {
		this(new ActiveMQConnectionFactory(brokerURL));
	}

	public StoreablePooledConnectionFactory(ActiveMQConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	public Connection createConnection() throws JMSException {
		return createConnection(null, null);
	}

	public synchronized Connection createConnection(String userName, String password) throws JMSException {
		if (stopped.get()) {
			LOG.debug("PooledConnectionFactory is stopped, skip create new connection.");
			return null;
		}

		ConnectionKey key = new ConnectionKey(userName, password);
		LinkedList<ConnectionPool> pools = cache.get(key);

		if (pools == null) {
			pools = new LinkedList<ConnectionPool>();
			cache.put(key, pools);
		}

		ConnectionPool connection = null;
		if (pools.size() == maxConnections) {
			connection = pools.removeFirst();
		}

		// Now.. we might get a connection, but it might be that we need to
		// dump it..
		if (connection != null && connection.expiredCheck()) {
			connection = null;
		}

		if (connection == null) {
			ActiveMQConnection delegate = createConnection(key);
			connection = createConnectionPool(delegate);
		}
		pools.add(connection);
		return new PooledConnection(connection);
	}

	protected ConnectionPool createConnectionPool(ActiveMQConnection connection) {
		ConnectionPool result =  new ConnectionPool(connection, getPoolFactory());
		result.setIdleTimeout(getIdleTimeout());
		result.setExpiryTimeout(getExpiryTimeout());
		return result;
	}

	protected ActiveMQConnection createConnection(ConnectionKey key) throws JMSException {
		if (key.getUserName() == null && key.getPassword() == null) {
			return (ActiveMQConnection)connectionFactory.createConnection();
		} else {
			return (ActiveMQConnection)connectionFactory.createConnection(key.getUserName(), key.getPassword());
		}
	}

	/**
	 * @see org.apache.activemq.service.Service#start()
	 */
	public void start() {
		try {
			stopped.set(false);
			createConnection();
		} catch (JMSException e) {
			LOG.warn("Create pooled connection during start failed.", e);
			IOExceptionSupport.create(e);
		}
	}

	public void stop() {
		LOG.debug("Stop the PooledConnectionFactory, number of connections in cache: "+cache.size());
		stopped.set(true);
		for (Iterator<LinkedList<ConnectionPool>> iter = cache.values().iterator(); iter.hasNext();) {
			for (ConnectionPool connection : iter.next()) {
				try {
					connection.close();
				}catch(Exception e) {
					LOG.warn("Close connection failed",e);
				}
			}
		}
		cache.clear();
	}

	public ObjectPoolFactory getPoolFactory() {
		if (poolFactory == null) {
			poolFactory = createPoolFactory();
		}
		return poolFactory;
	}

	/**
	 * Sets the object pool factory used to create individual session pools for
	 * each connection
	 */
	public void setPoolFactory(ObjectPoolFactory poolFactory) {
		this.poolFactory = poolFactory;
	}

	public int getMaximumActive() {
		return maximumActive;
	}

	/**
	 * Sets the maximum number of active sessions per connection
	 */
	public void setMaximumActive(int maximumActive) {
		this.maximumActive = maximumActive;
	}

	/**
	 * @return the maxConnections
	 */
	public int getMaxConnections() {
		return maxConnections;
	}

	/**
	 * @param maxConnections the maxConnections to set
	 */
	public void setMaxConnections(int maxConnections) {
		this.maxConnections = maxConnections;
	}

	protected ObjectPoolFactory createPoolFactory() {
		return new GenericObjectPoolFactory(null, maximumActive);
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	/**
	 * allow connections to expire, irrespective of load or idle time. This is useful with failover
	 * to force a reconnect from the pool, to reestablish load balancing or use of the master post recovery
	 * 
	 * @param expiryTimeout non zero in milliseconds
	 */
	public void setExpiryTimeout(long expiryTimeout) {
		this.expiryTimeout = expiryTimeout;   
	}

	public long getExpiryTimeout() {
		return expiryTimeout;
	}

	public void buildFromProperties(Properties properties) {

		if (properties == null) {
			properties = new Properties();
		}

		String temp = properties.getProperty(Context.PROVIDER_URL);
		if (temp == null || temp.length() == 0) {
			temp = properties.getProperty("brokerURL");
		}
		if (temp != null && temp.length() > 0) {
			setBrokerURL(temp);
		}

		buildFromMap(properties);    	
	}

	public boolean buildFromMap(Map properties) {
		boolean rc=false;

		ActiveMQPrefetchPolicy p = new ActiveMQPrefetchPolicy(); 
		if( IntrospectionSupport.setProperties(p, properties, "prefetchPolicy.") ) {
			setPrefetchPolicy(p);
			rc = true;
		}

		RedeliveryPolicy rp = new RedeliveryPolicy();
		if ( IntrospectionSupport.setProperties(rp, properties, "redeliveryPolicy.") ) {
			setRedeliveryPolicy(rp);
			rc = true;
		}

		rc |= IntrospectionSupport.setProperties(this, properties);

		return rc;
	}

	public void populateProperties(Properties props) {
		props.setProperty("dispatchAsync", Boolean.toString(isDispatchAsync()));

		if (getBrokerURL() != null) {
			props.setProperty(Context.PROVIDER_URL, getBrokerURL());
			props.setProperty("brokerURL", getBrokerURL());
		}

		if (getClientID() != null) {
			props.setProperty("clientID", getClientID());
		}

		IntrospectionSupport.getProperties(getPrefetchPolicy(), props, "prefetchPolicy.");
		IntrospectionSupport.getProperties(getRedeliveryPolicy(), props, "redeliveryPolicy.");

		props.setProperty("copyMessageOnSend", Boolean.toString(isCopyMessageOnSend()));
		props.setProperty("disableTimeStampsByDefault", Boolean.toString(isDisableTimeStampsByDefault()));
		props.setProperty("objectMessageSerializationDefered", Boolean.toString(isObjectMessageSerializationDefered()));
		props.setProperty("optimizedMessageDispatch", Boolean.toString(isOptimizedMessageDispatch()));

		if (getPassword() != null) {
			props.setProperty("password", getPassword());
		}


		props.setProperty("useSyncSend", Boolean.toString(isUseSyncSend()));
		props.setProperty("useAsyncSend", Boolean.toString(isUseAsyncSend()));
		props.setProperty("useCompression", Boolean.toString(isUseCompression()));
		props.setProperty("useRetroactiveConsumer", Boolean.toString(isUseRetroactiveConsumer()));
		props.setProperty("watchTopicAdvisories", Boolean.toString(isWatchTopicAdvisories()));

		if (getUserName() != null) {
			props.setProperty("userName", getUserName());
		}

		props.setProperty("closeTimeout", Integer.toString(getCloseTimeout()));
		props.setProperty("alwaysSessionAsync", Boolean.toString(isAlwaysSessionAsync()));
		props.setProperty("optimizeAcknowledge", Boolean.toString(isOptimizeAcknowledge()));
		props.setProperty("statsEnabled",Boolean.toString(isStatsEnabled()));

	}



	public String getBrokerURL() {
		return brokerURL==null?null:brokerURL.toString();
	}

	/**
	 * Sets the <a
	 * href="http://incubator.apache.org/activemq/configuring-transports.html">connection
	 * URL</a> used to connect to the ActiveMQ broker.
	 */
	public void setBrokerURL(String brokerURL) {
		this.brokerURL = createURI(brokerURL);

		// Use all the properties prefixed with 'jms.' to set the connection factory
		// options.
		if( this.brokerURL.getQuery() !=null ) {
			// It might be a standard URI or...
			try {

				Map map = URISupport.parseQuery(this.brokerURL.getQuery());
				if( buildFromMap(IntrospectionSupport.extractProperties(map, "jms.")) ) {
					this.brokerURL = URISupport.createRemainingURI(this.brokerURL, map);
				}

			} catch (URISyntaxException e) {
			}

		} else {

			// It might be a composite URI.
			try {
				CompositeData data = URISupport.parseComposite(this.brokerURL);
				if( buildFromMap(IntrospectionSupport.extractProperties(data.getParameters(), "jms.")) ) {
					this.brokerURL = data.toURI();
				}
			} catch (URISyntaxException e) {
			}
		}
	}

	/**
	 * @param brokerURL
	 * @return
	 * @throws URISyntaxException
	 */
	private static URI createURI(String brokerURL) {
		try {
			return new URI(brokerURL);
		}
		catch (URISyntaxException e) {
			throw (IllegalArgumentException) new IllegalArgumentException("Invalid broker URI: " + brokerURL).initCause(e);
		}
	}


	public String getClientID() {
		return clientID;
	}

	/**
	 * Sets the JMS clientID to use for the created connection. Note that this can only be used by one connection at once so generally its a better idea
	 * to set the clientID on a Connection
	 */
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}

	public boolean isCopyMessageOnSend() {
		return copyMessageOnSend;
	}

	/**
	 * Should a JMS message be copied to a new JMS Message object as part of the
	 * send() method in JMS. This is enabled by default to be compliant with the
	 * JMS specification. You can disable it if you do not mutate JMS messages
	 * after they are sent for a performance boost
	 */
	public void setCopyMessageOnSend(boolean copyMessageOnSend) {
		this.copyMessageOnSend = copyMessageOnSend;
	}

	public boolean isDisableTimeStampsByDefault() {
		return disableTimeStampsByDefault;
	}

	/**
	 * Sets whether or not timestamps on messages should be disabled or not. If
	 * you disable them it adds a small performance boost.
	 */
	public void setDisableTimeStampsByDefault(boolean disableTimeStampsByDefault) {
		this.disableTimeStampsByDefault = disableTimeStampsByDefault;
	}

	public boolean isOptimizedMessageDispatch() {
		return optimizedMessageDispatch;
	}

	/**
	 * If this flag is set then an larger prefetch limit is used - only
	 * applicable for durable topic subscribers.
	 */
	public void setOptimizedMessageDispatch(boolean optimizedMessageDispatch) {
		this.optimizedMessageDispatch = optimizedMessageDispatch;
	}

	public String getPassword() {
		return password;
	}

	/**
	 * Sets the JMS password used for connections created from this factory
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	public  ActiveMQPrefetchPolicy getPrefetchPolicy() {
		return prefetchPolicy;
	}

	/**
	 * Sets the <a
	 * href="http://incubator.apache.org/activemq/what-is-the-prefetch-limit-for.html">prefetch
	 * policy</a> for consumers created by this connection.
	 */
	public void setPrefetchPolicy(ActiveMQPrefetchPolicy prefetchPolicy) {
		this.prefetchPolicy = prefetchPolicy;
	}

	public boolean isUseAsyncSend() {
		return useAsyncSend;
	}

	/**
	 * Forces the use of <a
	 * href="http://incubator.apache.org/activemq/async-sends.html">Async Sends</a>
	 * which adds a massive performance boost; but means that the send() method
	 * will return immediately whether the message has been sent or not which
	 * could lead to message loss.
	 */
	public void setUseAsyncSend(boolean useAsyncSend) {
		this.useAsyncSend = useAsyncSend;
	}

	public String getUserName() {
		return userName;
	}

	/**
	 * Sets the JMS userName used by connections created by this factory
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isUseRetroactiveConsumer() {
		return useRetroactiveConsumer;
	}

	/**
	 * Sets whether or not retroactive consumers are enabled. Retroactive consumers allow
	 * non-durable topic subscribers to receive old messages that were published before the
	 * non-durable subscriber started.
	 */
	public void setUseRetroactiveConsumer(boolean useRetroactiveConsumer) {
		this.useRetroactiveConsumer = useRetroactiveConsumer;
	}

	public RedeliveryPolicy getRedeliveryPolicy() {
		return redeliveryPolicy;
	}

	/**
	 * Sets the global redelivery policy to be used when a message is delivered but the session is rolled back
	 */
	public void setRedeliveryPolicy(RedeliveryPolicy redeliveryPolicy) {
		this.redeliveryPolicy = redeliveryPolicy;
	}

	public boolean isUseCompression() {
		return useCompression;
	}

	/**
	 * Enables the use of compression of the message bodies
	 */
	public void setUseCompression(boolean useCompression) {
		this.useCompression = useCompression;
	}

	public boolean isObjectMessageSerializationDefered() {
		return objectMessageSerializationDefered;
	}

	/**
	 * When an object is set on an ObjectMessage, the JMS spec requires the
	 * object to be serialized by that set method. Enabling this flag causes the
	 * object to not get serialized. The object may subsequently get serialized
	 * if the message needs to be sent over a socket or stored to disk.
	 */
	public void setObjectMessageSerializationDefered(boolean objectMessageSerializationDefered) {
		this.objectMessageSerializationDefered = objectMessageSerializationDefered;
	}

	public boolean isDispatchAsync() {
		return dispatchAsync;
	}

	/**
	 * Enables or disables the default setting of whether or not consumers have
	 * their messages <a
	 * href="http://incubator.apache.org/activemq/consumer-dispatch-async.html">dispatched
	 * synchronously or asynchronously by the broker</a>.
	 * 
	 * For non-durable topics for example we typically dispatch synchronously by
	 * default to minimize context switches which boost performance. However
	 * sometimes its better to go slower to ensure that a single blocked
	 * consumer socket does not block delivery to other consumers.
	 * 
	 * @param asyncDispatch
	 *            If true then consumers created on this connection will default
	 *            to having their messages dispatched asynchronously. The
	 *            default value is false.
	 */
	public void setDispatchAsync(boolean asyncDispatch) {
		this.dispatchAsync = asyncDispatch;
	}

	/**
	 * @return Returns the closeTimeout.
	 */
	public int getCloseTimeout(){
		return closeTimeout;
	}

	/**
	 * Sets the timeout before a close is considered complete. Normally a
	 * close() on a connection waits for confirmation from the broker; this
	 * allows that operation to timeout to save the client hanging if there is
	 * no broker
	 */
	public void setCloseTimeout(int closeTimeout){
		this.closeTimeout=closeTimeout;
	}

	/**
	 * @return Returns the alwaysSessionAsync.
	 */
	public boolean isAlwaysSessionAsync(){
		return alwaysSessionAsync;
	}

	/**
	 * If this flag is set then a separate thread is not used for dispatching
	 * messages for each Session in the Connection. However, a separate thread
	 * is always used if there is more than one session, or the session isn't in
	 * auto acknowledge or duplicates ok mode
	 */
	public void setAlwaysSessionAsync(boolean alwaysSessionAsync){
		this.alwaysSessionAsync=alwaysSessionAsync;
	}

	/**
	 * @return Returns the optimizeAcknowledge.
	 */
	public boolean isOptimizeAcknowledge(){
		return optimizeAcknowledge;
	}

	/**
	 * @param optimizeAcknowledge The optimizeAcknowledge to set.
	 */
	public void setOptimizeAcknowledge(boolean optimizeAcknowledge){
		this.optimizeAcknowledge=optimizeAcknowledge;
	}

	public boolean isNestedMapAndListEnabled() {
		return nestedMapAndListEnabled ;
	}

	/**
	 * Enables/disables whether or not Message properties and MapMessage entries
	 * support <a
	 * href="http://incubator.apache.org/activemq/structured-message-properties-and-mapmessages.html">Nested
	 * Structures</a> of Map and List objects
	 */
	public void setNestedMapAndListEnabled(boolean structuredMapsEnabled) {
		this.nestedMapAndListEnabled = structuredMapsEnabled;
	}

	public String getClientIDPrefix() {
		return clientIDPrefix;
	}

	/**
	 * Sets the prefix used by autogenerated JMS Client ID values which are
	 * used if the JMS client does not explicitly specify on.
	 * 
	 * @param clientIDPrefix
	 */
	public void setClientIDPrefix(String clientIDPrefix) {
		this.clientIDPrefix = clientIDPrefix;
	}

	protected synchronized IdGenerator getClientIdGenerator() {
		if (clientIdGenerator == null) {
			if (clientIDPrefix != null) {
				clientIdGenerator = new IdGenerator(clientIDPrefix);
			}
			else {
				clientIdGenerator = new IdGenerator();
			}
		}
		return clientIdGenerator;
	}

	protected void setClientIdGenerator(IdGenerator clientIdGenerator) {
		this.clientIdGenerator = clientIdGenerator;
	}


	/**
	 * @return the statsEnabled
	 */
	 public boolean isStatsEnabled(){
		return this.factoryStats.isEnabled();
	 }


	 /**
	  * @param statsEnabled the statsEnabled to set
	  */
	 public void setStatsEnabled(boolean statsEnabled){
		 this.factoryStats.setEnabled(statsEnabled);
	 }

	 public boolean isUseSyncSend() {
		 return useSyncSend;
	 }

	 public void setUseSyncSend(boolean forceSyncSend) {
		 this.useSyncSend = forceSyncSend;
	 }

	 public synchronized boolean isWatchTopicAdvisories() {
		 return watchTopicAdvisories;
	 }

	 public synchronized void setWatchTopicAdvisories(boolean watchTopicAdvisories) {
		 this.watchTopicAdvisories = watchTopicAdvisories;
	 }

	 JMSStatsImpl factoryStats = new JMSStatsImpl();

	 static protected final Executor DEFAULT_CONNECTION_EXECUTOR = new ScheduledThreadPoolExecutor(5, new ThreadFactory() {
		 public Thread newThread(Runnable run) {
			 Thread thread = new Thread(run);
			 thread.setPriority(ThreadPriorities.INBOUND_CLIENT_CONNECTION);
			 return thread;
		 }
	 });

	 public static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";
	 public static final String DEFAULT_USER = null;
	 public static final String DEFAULT_PASSWORD = null;

	 private IdGenerator clientIdGenerator;
	 private String clientIDPrefix;
	 protected URI brokerURL;
	 protected String userName;
	 protected String password;
	 protected String clientID;

	 // optimization flags
	 private ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
	 private RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();

	 private boolean disableTimeStampsByDefault = false;
	 private boolean optimizedMessageDispatch = true;
	 private boolean copyMessageOnSend = true;
	 private boolean useCompression = false;
	 private boolean objectMessageSerializationDefered = false;
	 protected boolean dispatchAsync = false;
	 protected boolean alwaysSessionAsync=true;
	 private boolean useAsyncSend = false;
	 private boolean optimizeAcknowledge = false;
	 private int closeTimeout = 15000;
	 private boolean useRetroactiveConsumer;
	 private boolean nestedMapAndListEnabled = true;
	 private boolean useSyncSend=false;
	 private boolean watchTopicAdvisories=true;
}
