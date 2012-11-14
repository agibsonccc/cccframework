package com.ccc.ccm.server.springframework;
import java.util.List;

import org.apache.activemq.command.ActiveMQQueue;
public class CustomQueue extends ActiveMQQueue{

	public CustomQueue(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public CustomQueue() {
		// TODO Auto-generated constructor stub
	}

	public List<String> getQueueNames() {
		return queueNames;
	}

	public void setQueueNames(List<String> queueNames) {
		this.queueNames = queueNames;
	}

	private List<String> queueNames;
}
 