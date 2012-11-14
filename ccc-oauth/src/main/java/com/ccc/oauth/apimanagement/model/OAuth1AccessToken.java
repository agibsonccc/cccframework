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
@Table(name="oauth1_access_token")
public class OAuth1AccessToken implements Serializable {

	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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

	public String getTokenSecret() {
		return tokenSecret;
	}
	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6008336341323939605L;
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;
	@Column(name="access_token")
	private String accessToken;
	@Column(name="user_name")
	private String userName;
	@Column(name="time_received")
	private Timestamp timeReceived;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="token_secret")
	private String tokenSecret;
}
