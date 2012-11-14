package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="oauth_info")
public class OAuth1Info implements Serializable {

	
	
	@Override
	public String toString() {
		return "OAuth1Info [id=" + id + ", scope=" + scope + ", service="
				+ service + ", consumerKey=" + consumerKey
				+ ", consumerSecret=" + consumerSecret + ", callback="
				+ callback + ", encryptionType=" + encryptionType
				+ ", requestTokenUrl=" + requestTokenUrl + ", authzUrl="
				+ authzUrl + ", accessTokenUrl=" + accessTokenUrl
				+ ", readLimit=" + readLimit + ", version=" + version
				+ ", dataUrl=" + dataUrl + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessTokenUrl == null) ? 0 : accessTokenUrl.hashCode());
		result = prime * result
				+ ((authzUrl == null) ? 0 : authzUrl.hashCode());
		result = prime * result
				+ ((callback == null) ? 0 : callback.hashCode());
		result = prime * result
				+ ((consumerKey == null) ? 0 : consumerKey.hashCode());
		result = prime * result
				+ ((consumerSecret == null) ? 0 : consumerSecret.hashCode());
		result = prime * result + ((dataUrl == null) ? 0 : dataUrl.hashCode());
		result = prime * result
				+ ((encryptionType == null) ? 0 : encryptionType.hashCode());
		result = prime * result + id;
		result = prime * result + readLimit;
		result = prime * result
				+ ((requestTokenUrl == null) ? 0 : requestTokenUrl.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
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
		OAuth1Info other = (OAuth1Info) obj;
		if (accessTokenUrl == null) {
			if (other.accessTokenUrl != null)
				return false;
		} else if (!accessTokenUrl.equals(other.accessTokenUrl))
			return false;
		if (authzUrl == null) {
			if (other.authzUrl != null)
				return false;
		} else if (!authzUrl.equals(other.authzUrl))
			return false;
		if (callback == null) {
			if (other.callback != null)
				return false;
		} else if (!callback.equals(other.callback))
			return false;
		if (consumerKey == null) {
			if (other.consumerKey != null)
				return false;
		} else if (!consumerKey.equals(other.consumerKey))
			return false;
		if (consumerSecret == null) {
			if (other.consumerSecret != null)
				return false;
		} else if (!consumerSecret.equals(other.consumerSecret))
			return false;
		if (dataUrl == null) {
			if (other.dataUrl != null)
				return false;
		} else if (!dataUrl.equals(other.dataUrl))
			return false;
		if (encryptionType == null) {
			if (other.encryptionType != null)
				return false;
		} else if (!encryptionType.equals(other.encryptionType))
			return false;
		if (id != other.id)
			return false;
		if (readLimit != other.readLimit)
			return false;
		if (requestTokenUrl == null) {
			if (other.requestTokenUrl != null)
				return false;
		} else if (!requestTokenUrl.equals(other.requestTokenUrl))
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
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public Service getService() {
		return service;
	}
	public void setService(Service service) {
		this.service = service;
	}
	public String getConsumerKey() {
		return consumerKey;
	}
	public void setConsumerKey(String consumerKey) {
		this.consumerKey = consumerKey;
	}
	public String getConsumerSecret() {
		return consumerSecret;
	}
	public void setConsumerSecret(String consumerSecret) {
		this.consumerSecret = consumerSecret;
	}
	public String getCallback() {
		return callback;
	}
	public void setCallback(String callback) {
		this.callback = callback;
	}
	public String getEncryptionType() {
		return encryptionType;
	}
	public void setEncryptionType(String encryptionType) {
		this.encryptionType = encryptionType;
	}
	public String getRequestTokenUrl() {
		return requestTokenUrl;
	}
	public void setRequestTokenUrl(String requestTokenUrl) {
		this.requestTokenUrl = requestTokenUrl;
	}
	public String getAuthzUrl() {
		return authzUrl;
	}
	public void setAuthzUrl(String authzUrl) {
		this.authzUrl = authzUrl;
	}
	public String getAccessTokenUrl() {
		return accessTokenUrl;
	}
	public void setAccessTokenUrl(String accessTokenUrl) {
		this.accessTokenUrl = accessTokenUrl;
	}
	
	
	public int getReadLimit() {
		return readLimit;
	}
	public void setReadLimit(int readLimit) {
		this.readLimit = readLimit;
	}


	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}


	public String getDataUrl() {
		return dataUrl;
	}
	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = -8154030624878901357L;

	@Id
	@Column(name="id")
	private int id;
	@Column(name="scope")
	private String scope;
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;
	@Column(name="consumer_key")
	private String consumerKey;
	@Column(name="consumer_secret")
	private String consumerSecret;
	@Column(name="callback")
	private String callback;
	@Column(name="encryption_type")
	private String encryptionType;
	@Column(name="request_token_path")
	private String requestTokenUrl;
	@Column(name="authz_url")
	private String authzUrl;
	@Column(name=" access_token_url")
	private String accessTokenUrl;
	@Column(name="read_limit")
	private int readLimit;
	@Column(name="version")
	private String version;
	@Column(name="data_url")
	private String dataUrl;
}
