package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Embeddable
public class AuthSupportsPk implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6237131362305533827L;
	public AuthType getAuthType() {
		return authType;
	}
	public void setAuthType(AuthType authType) {
		this.authType = authType;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	@ManyToOne
	@JoinColumn(name="auth_type_id")
	private AuthType authType;
	@ManyToOne
	@JoinColumn(name="service_id", insertable=false,updatable=false)
	private Service service;
}
