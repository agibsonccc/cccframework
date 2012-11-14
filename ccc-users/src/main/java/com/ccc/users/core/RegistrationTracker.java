package com.ccc.users.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="registration")
public class RegistrationTracker {
	
	
	
	public String getRegistrationKey() {
		return registrationKey;
	}
	public void setRegistrationKey(String registrationKey) {
		this.registrationKey = registrationKey;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public boolean isRegistered() {
		return registered;
	}
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}
	@Column(name="registration_key")
	private String registrationKey;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="user_name")
	private String userName;
	@Column(name="registered")
	private boolean registered;
	

}
