package com.ccc.ccm.rabbitmq.transaction;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;

public class MessageListenerContainerBuilder {


	public SimpleMessageListenerContainer build() {
		AutoAckMessageListenerContainer ret = new AutoAckMessageListenerContainer();
		ret.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		ret.setAutoStartup(this.autoStartup);
		ret.setChannelTransacted(false);
		ret.setConcurrentConsumers(this.concurrentConsumers);
		ret.setExposeListenerChannel(this.exposeListenerChannel);
		ret.setMessageListener(this.messageListener);
		if(queueNames!=null)
			ret.setQueueNames(this.queueNames);
		ret.setPrefetchCount(this.prefetchCount);
		ret.setConnectionFactory(this.connectionFactory);
		ret.setReceiveTimeout(this.receiveTimeout);
		ret.setRecoveryInterval(this.recoveryTimeout);
		ret.setShutdownTimeout(this.shutdownTimeout);
		ret.setTransactionManager(this.transactionManager);
		ret.setTxSize(this.txSize);
		ret.setErrorHandler(this.errorHandler);
		ret.setQueues(queues);
		return ret;
	}

	public MessageListenerContainerBuilder exposeListenerChannel(boolean exposeListenerChannel) {
		this.exposeListenerChannel=exposeListenerChannel;
		return this;
	}
	public MessageListenerContainerBuilder rollbackOnFailure(boolean rollbackOnFailure) {
		this.rollbackOnFailure=rollbackOnFailure;
		return this;

	}
	public MessageListenerContainerBuilder validateExistingTransaction(boolean validateExistingTransaction) {
		this.validateExistingTransaction=validateExistingTransaction;
		return this;
	}


	public MessageListenerContainerBuilder defaultTimeout(int defaultTimeout) {
		this.defaultTimeout=defaultTimeout;
		return this;
	}
	public MessageListenerContainerBuilder prefetchCount(int prefetchCount) {
		this.prefetchCount=prefetchCount;
		return this;
	}

	public MessageListenerContainerBuilder channelTransacted(boolean channelTransacted) {
		this.channelTransacted=channelTransacted;
		return this;
	}
	public MessageListenerContainerBuilder messageListener(Object messageListener) {
		this.messageListener=messageListener;
		return this;
	}

	public MessageListenerContainerBuilder ackMode(AcknowledgeMode mode) {
		this.ackMode=mode;
		return this;
	}
	public MessageListenerContainerBuilder concurrentConsumers(int concurrentConsumers) {
		this.concurrentConsumers=concurrentConsumers;
		return this;
	}
	public MessageListenerContainerBuilder queueNames(String...queueNames) {
		this.queueNames=queueNames;
		return this;
	}
	public MessageListenerContainerBuilder receiveTimeout(int receiveTimeout) {
		this.receiveTimeout=receiveTimeout;
		return this;
	}
	public MessageListenerContainerBuilder recoveryTimeout(long recoveryTimeout) {
		this.recoveryTimeout=recoveryTimeout;
		return this;
	}
	public MessageListenerContainerBuilder shutdownTimeout(long shutdownTimeout) {
		this.shutdownTimeout=shutdownTimeout;
		return this;
	}
	public MessageListenerContainerBuilder transactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager=transactionManager;
		return this;
	}
	public MessageListenerContainerBuilder txSize(int txSize) {
		this.txSize=txSize;
		return this;
	}
	public MessageListenerContainerBuilder errorHandler(ErrorHandler errorHandler) {
		this.errorHandler=errorHandler;
		return this;
	}
	public MessageListenerContainerBuilder autoStartup(boolean autoStartup) {
		this.autoStartup=autoStartup;
		return this;
	}
	public MessageListenerContainerBuilder queues(Queue...queues) {
		this.queues=queues;
		return this;
	}
	public MessageListenerContainerBuilder connectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory=connectionFactory;
		return this;
	}
	
	private boolean exposeListenerChannel;
	private boolean rollbackOnFailure=true;
	private boolean validateExistingTransaction=true;
	private int defaultTimeout=60;
	private boolean autoStartup=true;
	private int prefetchCount=1;
	private AcknowledgeMode ackMode;
	private ConnectionFactory connectionFactory;
	private Object messageListener;
	private boolean channelTransacted=true;
	private int concurrentConsumers;
	private String[] queueNames;
	private int receiveTimeout;
	private long recoveryTimeout;
	private long shutdownTimeout;
	private PlatformTransactionManager transactionManager;
	private int txSize;
	private ErrorHandler errorHandler;
	private Queue[] queues;
}
