package com.ccc.ccm.rabbitmq.transaction;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.rabbit.transaction.RabbitTransactionManager;
import org.springframework.util.Assert;

import com.ccc.ccm.rabbitmq.impl.RabbitMQMessageClient;

public class DefaultRabbitTransactionManager {

	
	@PostConstruct
	public void init() {
		transactionManager = new RabbitTransactionManager(connectionFactory);
		transactionManager.setRollbackOnCommitFailure(true);
		transactionManager.setValidateExistingTransaction(true);
		transactionManager.setDefaultTimeout(60);
		messageListenerContainer = new MessageListenerContainerBuilder().ackMode(AcknowledgeMode.NONE)
				.autoStartup(true).channelTransacted(true).concurrentConsumers(5)
				.connectionFactory(connectionFactory).defaultTimeout(100)
				.exposeListenerChannel(true).messageListener(messageListener)
				.prefetchCount(5).queues(queues).receiveTimeout(200)
				.recoveryTimeout(1000).rollbackOnFailure(true).recoveryTimeout(1000).shutdownTimeout(10000)
				.txSize(300).transactionManager(transactionManager).build();
		
		/*
		messageListenerContainer= new SimpleMessageListenerContainer();
		messageListenerContainer.setAutoStartup(true);
		messageListenerContainer.setMessageListener(messageListener);
		messageListenerContainer.setConnectionFactory(connectionFactory);
		messageListenerContainer.setReceiveTimeout(100);
		messageListenerContainer.setTransactionManager(transactionManager);
		messageListenerContainer.setChannelTransacted(true);
		messageListenerContainer.setShutdownTimeout(300000);
		messageListenerContainer.setAcknowledgeMode(AcknowledgeMode.AUTO);
		messageListenerContainer.setExposeListenerChannel(true);
		messageListenerContainer.setConcurrentConsumers(5);
		messageListenerContainer.setQueues(queues);
		messageListenerContainer.setPrefetchCount(1);*/
		messageListenerContainer.initialize();
		messageListenerContainer.start();
		
		Assert.isTrue(messageListenerContainer.isRunning(),"Message listener container isn't running");
		Assert.isTrue(messageListenerContainer.isActive(),"Message listener isn't active");
		log.info("Setup rabbit transaction manager");
		
	}
	
	
	
	
	
	public RabbitMQMessageClient getRabbitClient() {
		return rabbitClient;
	}





	public void setRabbitClient(RabbitMQMessageClient rabbitClient) {
		this.rabbitClient = rabbitClient;
	}




	public void setQueues(org.springframework.amqp.core.Queue[] queues) {
		this.queues=queues;
	}



	public MessageListener getMessageListener() {
		return messageListener;
	}





	public void setMessageListener(MessageListener messageListener) {
		this.messageListener = new MessageListenerAdapter(messageListener);
	}





	public ConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
	private static Logger log=LoggerFactory.getLogger(DefaultRabbitTransactionManager.class);
	private ConnectionFactory connectionFactory;
	private SimpleMessageListenerContainer messageListenerContainer;
	private RabbitTransactionManager transactionManager;
	private String[] queueNames;
	private MessageListener messageListener;
	private RabbitMQMessageClient rabbitClient;
	private org.springframework.amqp.core.Queue[] queues;
	
}
