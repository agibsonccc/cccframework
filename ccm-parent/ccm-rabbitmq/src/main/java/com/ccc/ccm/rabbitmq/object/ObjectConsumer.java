package com.ccc.ccm.rabbitmq.object;

import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

import com.ccc.ccm.rabbitmq.impl.RabbitMQMessageClient;
/**
 * An object consumer is a consumer with a built in poll
 * mechanism for the receiving of objects.
 * @author Adam Gibson
 *
 */
public abstract class ObjectConsumer implements MessageListener {

	/**
	 * Polls the given destinations using
	 * the rabbitmq client.
	 * @param destinations the destinations to poll
	 */
	public void poll(String...destinations) {
		//rabbitClient.pollListen(this, destinations);
		if(destinations==null)
			return;
		for(final String queueName : destinations) {
			executor.execute(new Runnable() {
				public void run() {
					Message message=rabbitClient.receive(queueName);
					if(message!=null)
						onMessage(message);
				}
			});

		}

	}


	@Override
	public void onMessage(Message message) {
		byte[] bytes=message.getBody();
		if(message!=null && bytes.length < 1) 
			return;

		try {
			Object o=message.getMessageProperties().getHeaders().get("object");
			if(o!=null)
				processObject(o);
			else {
				processObject(bytes);
			}
		} catch (ObjectConverterException e) {
			e.printStackTrace();
		}

	}

	public void shutdown() {
		alive=false;
		log.info("Shutting down object consumer");

	}

	public abstract void processObject(Object object) throws ObjectConverterException;


	public RabbitMQMessageClient getRabbitClient() {
		return rabbitClient;
	}


	public void setRabbitClient(RabbitMQMessageClient rabbitClient) {
		this.rabbitClient = rabbitClient;
	}

	private ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5);
	private static Logger log=LoggerFactory.getLogger(ObjectConsumer.class);
	private boolean alive=true;
	private RabbitMQMessageClient rabbitClient;
}
