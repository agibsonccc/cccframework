package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="oauth2_urls")
public class OAuth2Urls implements Serializable {

	
	
	
	
	
	
	@Override
	public String toString() {
		return String.format(
				"OAuth2Urls [tokenUrl=%s, authzUrl=%s, id=%s, service=%s]",
				tokenUrl, authzUrl, id, service);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((authzUrl == null) ? 0 : authzUrl.hashCode());
		result = prime * result + id;
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result
				+ ((tokenUrl == null) ? 0 : tokenUrl.hashCode());
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
		OAuth2Urls other = (OAuth2Urls) obj;
		if (authzUrl == null) {
			if (other.authzUrl != null)
				return false;
		} else if (!authzUrl.equals(other.authzUrl))
			return false;
		if (id != other.id)
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (tokenUrl == null) {
			if (other.tokenUrl != null)
				return false;
		} else if (!tokenUrl.equals(other.tokenUrl))
			return false;
		return true;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 8522459451181996363L;

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}
	
	
	
	public String getTokenUrl() {
		return tokenUrl;
	}

	public void setTokenUrl(String tokenUrl) {
		this.tokenUrl = tokenUrl;
	}

	public String getAuthzUrl() {
		return authzUrl;
	}

	public void setAuthzUrl(String authzUrl) {
		this.authzUrl = authzUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}



	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}



	@Column(name="token_url")
	private String tokenUrl;
	@Column(name="authz_url")
	private String authzUrl;
	@Id
	@Column(name="id")
	private int id;
	@Column(name="data_url")
	private String dataUrl;
	
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;
}
