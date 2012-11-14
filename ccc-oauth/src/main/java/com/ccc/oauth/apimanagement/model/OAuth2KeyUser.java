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
@Table(name="oauth2_keys")
public class OAuth2KeyUser implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4384950222653872935L;

	
	
	@Override
	public String toString() {
		return "OAuth2KeyUser [id=" + id + ", userName=" + userName + ", code="
				+ code + ", timeMade=" + timeMade + ", service=" + service
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + id;
		result = prime * result + ((service == null) ? 0 : service.hashCode());
		result = prime * result
				+ ((timeMade == null) ? 0 : timeMade.hashCode());
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
		OAuth2KeyUser other = (OAuth2KeyUser) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (id != other.id)
			return false;
		if (service == null) {
			if (other.service != null)
				return false;
		} else if (!service.equals(other.service))
			return false;
		if (timeMade == null) {
			if (other.timeMade != null)
				return false;
		} else if (!timeMade.equals(other.timeMade))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public Timestamp getTimeMade() {
		return timeMade;
	}

	public void setTimeMade(Timestamp timeMade) {
		this.timeMade = timeMade;
	}

	@Id
	@Column(name="id")
	private int id;
	
	
	@Column(name="user_name")
	private String userName;
	
	@Column(name="code")
	private String code;
	@Column(name="when_made")
	private Timestamp timeMade;
	
	@ManyToOne
	@JoinColumn(name="service_id")
	private Service service;
}
