package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="oauth2_appinfo")
public class OAuth2AppInfo implements Serializable  {
	
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result
				+ ((clientSecret == null) ? 0 : clientSecret.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((redirectUrl == null) ? 0 : redirectUrl.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
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
		OAuth2AppInfo other = (OAuth2AppInfo) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (clientSecret == null) {
			if (other.clientSecret != null)
				return false;
		} else if (!clientSecret.equals(other.clientSecret))
			return false;
		if (id != other.id)
			return false;
		if (redirectUrl == null) {
			if (other.redirectUrl != null)
				return false;
		} else if (!redirectUrl.equals(other.redirectUrl))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return String
				.format("OAuth2AppInfo [clientSecret=%s, clientId=%s, id=%s, service=%s, redirectUrl=%s, scope=%s]",
						clientSecret, clientId, id, service, redirectUrl, scope);
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -5541651028854732583L;


	public String getClientSecret() {
		return clientSecret;
	}
	public void setClientSecret(String clientSecret) {
		this.clientSecret = clientSecret;
	}
	public String getRedirectUrl() {
		return redirectUrl;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}


	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}


	@Column(name="client_id")
	private String clientId;
	@Id
	@Column(name="id")
	private int id;
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;

	@Column(name="client_secret")
	private String clientSecret;
	@Column(name="redirect_uri")
	private String redirectUrl;
	@Column(name="scope")
	private String scope;
}
