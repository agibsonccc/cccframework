package com.ccc.ccm.persistence.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.ccm.persistence.model.QueueName;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("queueNameManager")
public class QueueNameManager extends GenericManager<QueueName> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2211487988441640888L;

	public QueueNameManager() {
		super(QueueName.class);
	}
	
	public QueueName queueWithName(String name) {
		List<QueueName> list= super.elementsWithValue("queue_name", name);
		return (list==null || list.isEmpty()) ? null : list.get(0);
	}
	
	public QueueName queueWithId(int id) {
		List<QueueName> list= super.elementsWithValue("id", String.valueOf(id));
		return (list==null || list.isEmpty()) ? null : list.get(0);
	}
	
	public QueueName firstQueue() {
		List<QueueName> list= super.allElements();
		return (list==null || list.isEmpty()) ? null : list.get(0);
	}
}
