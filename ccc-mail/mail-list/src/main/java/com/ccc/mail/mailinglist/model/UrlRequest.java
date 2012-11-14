package com.ccc.mail.mailinglist.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="url_requests")
public class UrlRequest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -352652277378722052L;

	
	public UrlTracking getUrl() {
		return url;
	}


	public void setUrl(UrlTracking url) {
		this.url = url;
	}


	public Timestamp getAccessed() {
		return accessed;
	}


	public void setAccessed(Timestamp accessed) {
		this.accessed = accessed;
	}

	public String getAccessingIp() {
		return accessingIp;
	}


	public void setAccessingIp(String accessingIp) {
		this.accessingIp = accessingIp;
	}

	public String getAccessingHost() {
		return accessingHost;
	}


	public void setAccessingHost(String accessingHost) {
		this.accessingHost = accessingHost;
	}

	
	
	public String getMethod() {
		return method;
	}


	public void setMethod(String method) {
		this.method = method;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	@Column(name="method")
	private String method;
	@Column(name="request_host")
	private String accessingHost;
	@Column(name="ip_address")
	private String accessingIp;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	@ManyToOne
	@JoinColumn(name="url_id")
	private UrlTracking url;
	
	@Column(name="accessed")
	private Timestamp accessed;
}
