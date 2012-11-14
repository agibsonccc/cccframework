package com.ccc.ccm.rabbitmq.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.PreDestroy;
import javax.naming.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.AnonymousQueue;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.HeadersExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.ccm.messages.store.MessageStore;
import com.ccc.ccm.rabbitmq.client.AQMPMessageClient;
import com.ccc.ccm.rabbitmq.transaction.DefaultRabbitTransactionManager;

/**
 * This is a rabbitmq messageclient that will handle memory management 
 * and creation/deletion, and sending and receiving of messages for an
 * amqp template for spring. Ensure to call destroy () if this isn't wrapped
 * in a spring container.
 * @author Adam Gibson
 *
 */
public class RabbitMQMessageClient implements AQMPMessageClient {
	@Override
	public Object requestReplyExchange(String routingKey, String exchange,
			Object message) {
		return rabbitTemplate.convertSendAndReceive(exchange, routingKey, message);
	}

	
	@Override
	public Object requestReply(String destination, Object message) {
		Object response=rabbitTemplate.convertSendAndReceive(destination, message);
		return response;
	}
	@Override
	public boolean sendToExchange(String exchange, String routingKey,
			Object message) {
		rabbitTemplate.convertAndSend(exchange, "#", message);

		return true;
	}

	@Override
	public void declareBinding(String destinationName,
			String exName, int exchangeType) {
		Queue queue=this.createQueue(destinationName);
		Exchange ex=this.createExchange(exName, true, false, exchangeType);
		Binding binding=this.bind(queue, ex, UUID.randomUUID().toString());
		getAdmin().declareBinding(binding);

	}
	@Override
	public void declareQueue(String name){
		Queue queue=createQueue(name);
		int retries=0;
		boolean succeeded=false;
		while(retries < 5 && !succeeded) {
			try {	
				getAdmin().declareQueue(queue);
				succeeded=true;

			}catch(Exception e) {
				if(e instanceof AmqpIOException) {
					retries++;
				}
				else throw new RuntimeException(e);
			}
		}


	}
	@Override
	public void declareExchange(String exchange, int type) {
		Exchange ex=createExchange(exchange,true,false,type);
		getAdmin().declareExchange(ex);
	}
	/**
	 * This returns an amqp admin with the underlying
	 * configuration (port,host,virtualhost)
	 * @return the amqp admin with the underlying configuration
	 */
	public AmqpAdmin getAdmin() {
		RabbitTemplate template=(RabbitTemplate) getRabbitTemplate();
		CachingConnectionFactory factory=(CachingConnectionFactory) template.getConnectionFactory();
		String host=factory.getHost();
		int port=factory.getPort();
		if(userName==null || password==null)
			return this.aqmpAdmin(host, port,template.getConnectionFactory().getVirtualHost());
		else return this.aqmpAdmin(host, port,template.getConnectionFactory().getVirtualHost(), userName, password);
	}
	public static final int FEDERATED = 6;
	@Override
	public void createQueueAndBindToExchange(String queue, String exchange,
			int exchangeType) {
		Queue create=createQueue(queue);
		Exchange ex=createExchange(exchange,true,false,exchangeType);
		Binding binding=this.bind(create, ex, UUID.randomUUID().toString());
		RabbitTemplate template=(RabbitTemplate) this.getRabbitTemplate();
		String host=template.getConnectionFactory().getHost();
		int port=template.getConnectionFactory().getPort();
		String vhost=template.getConnectionFactory().getVirtualHost();
		if(userName==null || password==null) {
			AmqpAdmin admin=this.aqmpAdmin(host, port, vhost);
			admin.declareExchange(ex);
			admin.declareQueue(create);
			admin.declareBinding(binding);
		}
		else {
			AmqpAdmin admin=getAdmin();
			admin.declareExchange(ex);
			admin.declareQueue(create);
			admin.declareBinding(binding);
		}

	}
	@Override
	public void createQueueAndBindToExchange(String queue, String exchange,
			int exchangeType,String vHost) {
		Queue create=createQueue(queue);
		Exchange ex=createExchange(exchange,true,false,exchangeType);
		Binding binding=this.bind(create, ex, UUID.randomUUID().toString());
		
		AmqpAdmin admin=getAdmin();
		admin.declareExchange(ex);
		admin.declareQueue(create);
		admin.declareBinding(binding);
	}
	@Override
	public boolean sendMessage(String text) {
		rabbitTemplate.convertAndSend(text);
		return true;
	}

	@Override
	public boolean sendMessage(String text, String destination) {
		rabbitTemplate.convertAndSend(destination, text);
		return true;
	}

	@Override
	public Message recieveSelected(String selector) {
		return rabbitTemplate.receive(selector);
	}

	@Override
	public Context context() {
		return null;
	}

	@Override
	public boolean sendMessageToDestination(String text, String destinationName) {
		rabbitTemplate.convertAndSend(destinationName, text);
		return true;
	}

	@Override
	public MessageStore messageStore() {
		return null;
	}

	@Override
	public void purge(int numThreads, String destination) {
		RabbitTemplate template=(RabbitTemplate) rabbitTemplate;
		String host=template.getConnectionFactory().getHost();
		int port=template.getConnectionFactory().getPort();
		AmqpAdmin admin=this.aqmpAdmin(host, port,template.getConnectionFactory().getVirtualHost());
		admin.purgeQueue(destination, false);
	}

	@Override
	public void purge(String destination) {
		AmqpAdmin admin=getAdmin();
		admin.purgeQueue(destination, false);
	}

	@Override
	public void send(Message message)
			throws AmqpException {
		rabbitTemplate.send(message);
	}

	@Override
	public void send(String routingKey,
			org.springframework.amqp.core.Message message) throws AmqpException {
		rabbitTemplate.send(routingKey, message);
	}

	@Override
	public void send(String exchange, String routingKey,
			org.springframework.amqp.core.Message message) throws AmqpException {
		rabbitTemplate.send(exchange, routingKey, message);
	}

	@Override
	public Binding bind(Queue queue,
			Exchange topicExchange, String routingKey) {
		Binding b = BindingBuilder.bind(queue).to(topicExchange).with(routingKey).noargs();
		getAdmin().declareBinding(b);		
		return b;
	}

	@Override
	public Exchange createDurableExchange(String name, boolean autoDelete,int type) {
		Exchange ret=null;
		switch(type) {
		case TOPIC:
			ret = new TopicExchange(name,true,autoDelete);
			break;
		case DIRECT:
			ret = new DirectExchange(name,true,autoDelete);
			break;
		case FANOUT:
			ret = new FanoutExchange(name,true,autoDelete);
			break;
		case HEADERS:
			ret = new HeadersExchange(name,true,autoDelete);
			break;
		}
		getAdmin().declareExchange(ret);
		return ret;
	}


	@Override
	public Exchange createExchange(String name, boolean durable,
			boolean autoDelete,int type) {
		Exchange ret =null;
		switch(type) {
		case TOPIC:
			ret = new TopicExchange(name,true,autoDelete);
			break;
		case DIRECT:
			ret = new DirectExchange(name,true,autoDelete);
		case FANOUT:
			ret = new FanoutExchange(name,true,autoDelete);
		case HEADERS:
			ret = new HeadersExchange(name,true,autoDelete);
		}
		return ret;
	}

	@Override
	public Exchange createExchange(String name) {
		return new TopicExchange(name,false,false);
	}

	@Override
	public TopicExchange createTopicExchange(String name) {
		return new TopicExchange(name);
	}

	@Override
	public org.springframework.amqp.core.Message receive() {
		return rabbitTemplate.receive();
	}

	@Override
	public org.springframework.amqp.core.Message receive(String queueName) {
		return rabbitTemplate.receive(queueName);
	}

	@Override
	public AnonymousQueue anonymousQueue() {
		AnonymousQueue ret = new AnonymousQueue();
		anonQueues.add(ret);
		return ret;
	}

	@Override
	public Queue createQueue(String name) {
		Queue ret = queues.get(name);
		if(ret==null) {
			ret = new Queue(name);
			if(log.isDebugEnabled()) {
				log.debug("Creating queue: {}",name);
			}
			queues.put(name,ret);
		}
		return ret;
	}


	public void setVHost(String vHost) {
		RabbitTemplate template=(RabbitTemplate) getRabbitTemplate();
		if(template==null)
			throw new IllegalStateException("No rabbit template found");
		CachingConnectionFactory factory=(CachingConnectionFactory) template.getConnectionFactory();
		factory.setVirtualHost(vHost);
	}

	@Override
	public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory(
			String host, int port,String vHost) {
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(host);
		keyBuffer.append(",");
		keyBuffer.append(port);
		String key=keyBuffer.toString();
		org.springframework.amqp.rabbit.connection.ConnectionFactory check=factories.get(key);
		if(check==null) {
			if(log.isDebugEnabled()) {
				log.debug("Creating new factory for: {}",key);
			}

			CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
			connectionFactory.setVirtualHost(vHost);
			return connectionFactory;
		}
		else {
			if(log.isDebugEnabled()) {
				log.debug("Found connection factory: {} returning",key);
			}

			return check;
		}
	}
	@Override
	public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory(
			String host, int port) {
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(host);
		keyBuffer.append(",");
		keyBuffer.append(port);
		String key=keyBuffer.toString();
		org.springframework.amqp.rabbit.connection.ConnectionFactory check=factories.get(key);
		if(check==null) {
			if(log.isDebugEnabled()) {
				log.debug("Creating new factory for: {}",key);
			}

			CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
			connectionFactory.setVirtualHost("/");
			return connectionFactory;
		}
		else {
			if(log.isDebugEnabled()) {
				log.debug("Found connection factory: {} returning",key);
			}

			return check;
		}
	}

	@Override
	public org.springframework.amqp.rabbit.connection.ConnectionFactory connectionFactory(
			String host, int port, String userName, String password) {
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(host);
		keyBuffer.append(",");
		keyBuffer.append(port);
		keyBuffer.append(",");
		keyBuffer.append(userName);
		keyBuffer.append(",");
		keyBuffer.append(password);

		String key=keyBuffer.toString();
		org.springframework.amqp.rabbit.connection.ConnectionFactory check=factories.get(key);
		if(check==null) {
			if(log.isDebugEnabled()) {
				log.debug("Creating neq queue with key: {}",key);
			}

			CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
			connectionFactory.setUsername(userName);
			connectionFactory.setPassword(password);
			factories.put(key,connectionFactory);
			return connectionFactory;
		}
		else {
			if(log.isDebugEnabled()) {
				log.debug("Found queue for: {} returning",key);
			}
			return check;
		}
	}
	@Override
	public ConnectionFactory connectionFactory(String host, String vHost,
			int port, String userName, String password) {
		CachingConnectionFactory connectionFactory = new CachingConnectionFactory(host,port);
		connectionFactory.setVirtualHost(vHost);
		connectionFactory.setUsername(userName);
		connectionFactory.setPassword(password);
		return connectionFactory;
	}

	@Override
	public AmqpAdmin aqmpAdmin(String host, int port, String vHost) {
		org.springframework.amqp.rabbit.connection.ConnectionFactory factory=this.connectionFactory(host, port,vHost);
		return new RabbitAdmin(factory);
	}
	@Override
	public AmqpAdmin aqmpAdmin(String host, int port, String vHost,String userName,String password) {
		org.springframework.amqp.rabbit.connection.ConnectionFactory factory=this.connectionFactory(host, vHost,port,userName,password);
		return new RabbitAdmin(factory);
	}
	public AmqpAdmin aqmpAdmin(String host, int port, String userName,
			String password) {
		StringBuffer keyBuffer = new StringBuffer();
		keyBuffer.append(host);
		keyBuffer.append(",");
		keyBuffer.append(port);
		keyBuffer.append(",");
		keyBuffer.append(userName);
		keyBuffer.append(",");
		keyBuffer.append(password);
		String key=keyBuffer.toString();
		RabbitAdmin ret=admins.get(key);
		if(ret==null) {
			org.springframework.amqp.rabbit.connection.ConnectionFactory factory=this.connectionFactory(host, port,userName,password);
			ret = new RabbitAdmin(factory);
			admins.put(key,ret); 

		}


		return ret;
	}


	public AmqpTemplate getRabbitTemplate() {
		return rabbitTemplate;
	}



	public void setRabbitTemplate(AmqpTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;

	}




	public void pollListen(MessageListener messageListener,boolean declare,String...queues) {
		if(rabbitTemplate instanceof RabbitTemplate && transactionManager==null) {
			RabbitTemplate template=(RabbitTemplate) rabbitTemplate;
			ConnectionFactory factory=template.getConnectionFactory();
			transactionManager = new DefaultRabbitTransactionManager();
			transactionManager.setMessageListener(messageListener);
			transactionManager.setConnectionFactory(factory);
			transactionManager.setRabbitClient(this);
			if(log.isDebugEnabled()) {
				log.debug("Created transaction manager ");
			}

			if(queues==null || queues.length < 1)
				throw new IllegalStateException("No queues specified");
			AmqpAdmin admin=getAdmin();
			Queue[] que = new Queue[queues.length];
			for(int i=0;i<queues.length;i++) {
				String s=queues[i];
				Queue queue=this.createQueue(s);
				que[i]=queue;
				if(declare)
					admin.declareQueue(queue);
				log.info("Queue created and declared: " + s);
			}
			transactionManager.setQueues(que);
			transactionManager.init();
		}
	}

	@PreDestroy
	public void destroy() {
		Collection<String> queueKeys=queues.keySet();
		Collection<String> adminKeys=admins.keySet();

		for(String s : adminKeys) {
			RabbitAdmin admin=admins.get(s);
			for(String s1 : queueKeys) {
				admin.purgeQueue(s1, false);
				admin.deleteQueue(s1);
			}
			break;
		}
		if(log.isDebugEnabled()) {
			log.debug("Disposed of queues");
		}


		for(org.springframework.amqp.rabbit.connection.ConnectionFactory factory : factories.values()) {
			if(factory instanceof CachingConnectionFactory) {
				CachingConnectionFactory c=(CachingConnectionFactory) factory;
				c.destroy();
			}
		}
		queues.clear();
		factories.clear();
		anonQueues.clear();
		admins.clear();

	}

	public Object getMessageListener() {
		return messageListener;
	}
	@Override
	public AmqpAdmin aqmpAdmin(String host, int port) {
		return aqmpAdmin(host,port,"/");
	}
	public void setMessageListener(Object messageListener) {
		this.messageListener = messageListener;
	}

	public DefaultRabbitTransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(
			DefaultRabbitTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
		if(getRabbitTemplate()!=null) {
			RabbitTemplate template=(RabbitTemplate) getRabbitTemplate();
			CachingConnectionFactory factory=(CachingConnectionFactory) template.getConnectionFactory();
			if(factory!=null)
				factory.setUsername(userName);
		}
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		if(getRabbitTemplate()!=null) {
			RabbitTemplate template=(RabbitTemplate) getRabbitTemplate();
			CachingConnectionFactory factory=(CachingConnectionFactory) template.getConnectionFactory();
			if(factory!=null)
				factory.setPassword(password);
		}
	}
	public static void main(String[] args) {
		RabbitMQMessageClient client = new RabbitMQMessageClient();
		CachingConnectionFactory factory = new CachingConnectionFactory();
		factory.setVirtualHost("11173835-8560-4f41-9216-8ad28fb47fef");
		factory.setUsername("b2d211cf-ef06-4c00-8c84-35ae9d439a46");
		factory.setPassword("c793ca2c-f57e-47b6-9f5d-30b83654e2eb");
		factory.setHost("clevercloudcomputing.com");
		factory.setPort(5672);
		RabbitTemplate template = new RabbitTemplate();
		template.setConnectionFactory(factory);
		client.setRabbitTemplate(template);
		client.setUserName("b2d211cf-ef06-4c00-8c84-35ae9d439a46");
		client.setPassword("c793ca2c-f57e-47b6-9f5d-30b83654e2eb");
		client.setVHost("11173835-8560-4f41-9216-8ad28fb47fef");
		String text="{\"contentType\":\"text\",\"subject\":\"alert!\",\"fromAddress\":\"alerts@gntforex.com\",\"vhost\":\"localhost\",\"destinations\":{\"amqp\":{\"topics\":[\"localhost\"]}},\"message\":{\"currency\":\"UK_100\",\"stopLoss\":\"\",\"limit\":\"2\",\"tradeType\":\"Short Term\",\"message\":\"test\n Trading is risky: See http://www.gntforex.com/risk-disclosure/\",\"messagetype\":\"4\",\"language\":\"english\",\"disclaimertext\":\"\",\"type\":\"comment\"},\"accesstoken\":\"868000ac-563b-478f-9387-210a45bef2da\",\"text\":\"{\"currency\":\"UK_100\",\"stopLoss\":\"\",\"limit\":\"2\",\"tradeType\":\"Short Term\",\"message\":\"test\n Trading is risky: See http://www.gntforex.com/risk-disclosure/\",\"messagetype\":\"4\",\"language\":\"english\",\"disclaimertext\":\"\",\"type\":\"comment\"}\"}";
		client.sendMessage(text, "1b31e9e1-0826-4447-8a71-05733ec871bd");
		client.sendToExchange("1b31e9e1-0826-4447-8a71-05733ec871bd", "#", text);
		System.exit(0);
	}
	
	public String getVirtualHost() {
		return virtualHost;
	}
	public void setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
	}
	private Map<String,Queue> queues = new HashMap<String,Queue>();
	private Map<String,org.springframework.amqp.rabbit.connection.ConnectionFactory> factories = new HashMap<String,org.springframework.amqp.rabbit.connection.ConnectionFactory>();
	private Set<AnonymousQueue> anonQueues = new HashSet<AnonymousQueue>();
	private Map<String,RabbitAdmin> admins = new HashMap<String,RabbitAdmin>();
	private static Logger log=LoggerFactory.getLogger(RabbitMQMessageClient.class);
	@Autowired(required=false)
	private AmqpTemplate rabbitTemplate;
	private Object messageListener;
	private DefaultRabbitTransactionManager transactionManager;
	private String userName="guest";
	private String password="guest";
	private String virtualHost;


}
