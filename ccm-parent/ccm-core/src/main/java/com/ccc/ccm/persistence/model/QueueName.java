package com.ccc.ccm.persistence.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="queue_names")
public class QueueName implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4360213792101970817L;

	
	public String getQueueUrl() {
		return queueUrl;
	}

	public void setQueueUrl(String queueUrl) {
		this.queueUrl = queueUrl;
	}

	public String getQueueName() {
		return queueName;
	}

	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getProviderUrl() {
		return providerUrl;
	}

	public void setProviderUrl(String providerUrl) {
		this.providerUrl = providerUrl;
	}

	@Column(name="queue_url")
	private String queueUrl;
	@Column(name="queue_name")
	private String queueName;
	@Column(name="class_name")
	private String className;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="provider_url")
	private String providerUrl;
}
