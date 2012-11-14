package com.ccc.ccm.rabbitmq.impl.tests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import com.ccc.ccm.rabbitmq.impl.RabbitMQMessageClient;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
@DirtiesContext
public class RabbitMQTests extends AbstractJUnit4SpringContextTests {
	@Test
	public void testSend() {
		RabbitTemplate template = new RabbitTemplate();
		RabbitMQMessageClient client = new RabbitMQMessageClient();
		client.setRabbitTemplate(template);
		ConnectionFactory factory=client.connectionFactory("localhost", 5672);
		template.setConnectionFactory(factory);
		AmqpAdmin admin=client.aqmpAdmin("localhost", 5672);
		org.springframework.amqp.core.Queue queue=client.createQueue("local");
		admin.declareQueue(queue);
		
		client.sendMessageToDestination("text", "local");
		Message message=client.receive("local");
		byte[] bytes=message.getBody();
		String s = new String(bytes);
		Assert.isTrue(s.equals("text"));
		
	}
}
