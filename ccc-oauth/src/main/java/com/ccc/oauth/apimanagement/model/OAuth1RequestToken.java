package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name="oauth1_request_token")
public class OAuth1RequestToken implements Serializable {

	
	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public String getRequestToken() {
		return requestToken;
	}

	public void setRequestToken(String requestToken) {
		this.requestToken = requestToken;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((consumerSecret == null) ? 0 : consumerSecret.hashCode());
		result = prime * result
				+ ((consumerToken == null) ? 0 : consumerToken.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((requestToken == null) ? 0 : requestToken.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result
				+ ((timeReceived == null) ? 0 : timeReceived.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OAuth1RequestToken other = (OAuth1RequestToken) obj;
		if (consumerSecret == null) {
			if (other.consumerSecret != null)
				return false;
		} else if (!consumerSecret.equals(other.consumerSecret))
			return false;
		if (consumerToken == null) {
			if (other.consumerToken != null)
				return false;
		} else if (!consumerToken.equals(other.consumerToken))
			return false;
		if (id != other.id)
			return false;
		if (requestToken == null) {
			if (other.requestToken != null)
				return false;
		} else if (!requestToken.equals(other.requestToken))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (timeReceived == null) {
			if (other.timeReceived != null)
				return false;
		} else if (!timeReceived.equals(other.timeReceived))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	public Timestamp getTimeReceived() {
		return timeReceived;
	}

	public void setTimeReceived(Timestamp timeReceived) {
		this.timeReceived = timeReceived;
	}


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Override
	public String toString() {
		return "OAuth1RequestToken [service=" + service + ", requestToken="
				+ requestToken + ", userName=" + userName + ", timeReceived="
				+ timeReceived + ", id=" + id + ", consumerToken="
				+ consumerToken + ", consumerSecret=" + consumerSecret + "]";
	}

	public String getConsumerToken() {
		return consumerToken;
	}

	public void setConsumerToken(String consumerToken) {
		this.consumerToken = consumerToken;
	}

	public String getConsumerSecret() {
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 2429062892276090997L;
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;
	@Column(name="request_token")
	private String requestToken;
	@Column(name="user_name")
	private String userName;
	@Column(name="time_received")
	private Timestamp timeReceived;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="consumer_token")
	private String consumerToken;
	@Column(name="consumer_secret")
	private String consumerSecret;
}
