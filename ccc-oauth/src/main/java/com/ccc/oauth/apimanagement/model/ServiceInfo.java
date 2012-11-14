package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

public class ServiceInfo implements Serializable {

	public ServiceInfo(String serviceName, String url) {
		super();
		this.serviceName = serviceName;
		this.url = url;
	}

	@Override
	public String toString() {
		return String.format("ServiceInfo [serviceName=%s, url=%s]",
				serviceName, url);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getUrl() {
		return url;
	}

	public ServiceInfo(int id, String serviceName, String url) {
		super();
		this.id = id;
		this.serviceName = serviceName;
		this.url = url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int id;
	
	private String serviceName;
	
	private String url;
}
