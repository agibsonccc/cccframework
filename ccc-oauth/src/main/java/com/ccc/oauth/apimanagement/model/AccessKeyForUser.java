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
@Table(name="access_for_user")
public class AccessKeyForUser implements Serializable {

	@Override
	public String toString() {
		return "AccessKeyForUser [userName=" + userName + ", id=" + id
				+ ", service=" + service + ", accessCode=" + accessCode
				+ ", obtained=" + obtained + ", refreshToken=" + refreshToken
				+ ", expiresIn=" + expiresIn + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessCode == null) ? 0 : accessCode.hashCode());
		result = prime * result
				+ ((expiresIn == null) ? 0 : expiresIn.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((obtained == null) ? 0 : obtained.hashCode());
		result = prime * result
				+ ((refreshToken == null) ? 0 : refreshToken.hashCode());
		result = prime * result + ((service == null) ? 0 : service.hashCode());
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
		AccessKeyForUser other = (AccessKeyForUser) obj;
		if (accessCode == null) {
			if (other.accessCode != null)
				return false;
		} else if (!accessCode.equals(other.accessCode))
			return false;
		if (expiresIn == null) {
			if (other.expiresIn != null)
				return false;
		} else if (!expiresIn.equals(other.expiresIn))
			return false;
		if (id != other.id)
			return false;
		if (obtained == null) {
			if (other.obtained != null)
				return false;
		} else if (!obtained.equals(other.obtained))
			return false;
		if (refreshToken == null) {
			if (other.refreshToken != null)
				return false;
		} else if (!refreshToken.equals(other.refreshToken))
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -9070198253897450284L;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
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

	
	public String getAccessCode() {
		return accessCode;
	}
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}
	public Timestamp getObtained() {
		return obtained;
	}
	public void setObtained(Timestamp obtained) {
		this.obtained = obtained;
	}

	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}

	@Column(name="user_name")
	private String userName;
	@Id
	@Column(name="id")
	private int id;
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;
	@Column(name="access_code")
	private String accessCode;
	@Column(name="obtained")
	private Timestamp obtained;
	@Column(name="refresh_token")
	private String refreshToken;
	@Column(name="expires_in")
	private String expiresIn;
}
