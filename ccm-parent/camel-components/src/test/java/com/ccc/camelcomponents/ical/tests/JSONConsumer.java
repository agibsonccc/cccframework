package com.ccc.camelcomponents.ical.tests;

import org.apache.camel.Consume;
import org.apache.camel.Message;

public class JSONConsumer {

	
	//@Consume(uri="direct:marshalled")
	public void consumer(Message message) {
		System.out.println(message.getBody(String.class));
	}
}
