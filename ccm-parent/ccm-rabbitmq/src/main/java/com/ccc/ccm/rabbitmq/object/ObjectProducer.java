package com.ccc.ccm.rabbitmq.object;

import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;

import com.ccc.ccm.rabbitmq.impl.RabbitMQMessageClient;
import com.ccc.util.objectserialize.ObjectConvertor;

public class ObjectProducer {

	
	
	public void sendObject(String routingKey,Object object) {
		try {
			byte[] bytes=ObjectConvertor.serialize(object);
			Message m = new Message(bytes,new MessageProperties());
			rabbitClient.send(routingKey, m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public RabbitMQMessageClient getRabbitClient() {
		return rabbitClient;
	}

	public void setRabbitClient(RabbitMQMessageClient rabbitClient) {
		this.rabbitClient = rabbitClient;
	}

	private RabbitMQMessageClient rabbitClient;
}
