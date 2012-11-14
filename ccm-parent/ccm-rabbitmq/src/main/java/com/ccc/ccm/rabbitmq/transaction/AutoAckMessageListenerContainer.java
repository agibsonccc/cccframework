package com.ccc.ccm.rabbitmq.transaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.RabbitResourceHolder;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import com.rabbitmq.client.Channel;

public class AutoAckMessageListenerContainer extends SimpleMessageListenerContainer {

	@Override
	protected void invokeListener(Channel channel, Message message)
			throws Exception {
		super.invokeListener(channel, message);
		//channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		
	}

	@Override
	protected void executeListener(Channel channel, Message message)
			throws Throwable {
		super.executeListener(channel, message);
		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		if(log.isDebugEnabled())
			log.debug("Acknowledging message: " + new String(message.getBody()));
	
	}
	private static Logger log=LoggerFactory.getLogger(AutoAckMessageListenerContainer.class);
}
