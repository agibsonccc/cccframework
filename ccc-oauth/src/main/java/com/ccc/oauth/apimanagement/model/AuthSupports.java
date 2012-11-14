package com.ccc.oauth.apimanagement.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="auth_supports")
public class AuthSupports implements Serializable,Comparable<AuthSupports> {
	
	@Override
	public String toString() {
		return String
				.format("AuthSupports [serviceId=%s, authTypeId=%s, preference=%s, pk=%s]",
						serviceId, authTypeId, preference, pk);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + authTypeId;
		result = prime * result + ((pk == null) ? 0 : pk.hashCode());
		result = prime * result + preference;
		result = prime * result + serviceId;
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
		AuthSupports other = (AuthSupports) obj;
		if (authTypeId != other.authTypeId)
			return false;
		if (pk == null) {
			if (other.pk != null)
				return false;
		} else if (!pk.equals(other.pk))
			return false;
		if (preference != other.preference)
			return false;
		if (serviceId != other.serviceId)
			return false;
		return true;
	}

	public int compareTo(AuthSupports o) {
		if(o.preference < preference)
			return -1;
		else if(o.preference > preference)
			return 1;
		else return 0;
	}

	public AuthType getAuthType() {
		return pk.getAuthType();
	}
	
	public void setAuthType(AuthType authType) {
		pk.setAuthType(authType);
	}
	
	public void setService(Service service) {
		pk.setService(service);
	}
	
	public Service getService() {
		return pk.getService();
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8378787876816284677L;
	public int getServiceId() {
		return serviceId;
	}
	public void setServiceId(int serviceId) {
		this.serviceId = serviceId;
	}
	public int getAuthTypeId() {
		return authTypeId;
	}
	public void setAuthTypeId(int authTypeId) {
		this.authTypeId = authTypeId;
	}
	
	
	public int getPreference() {
		return preference;
	}

	public void setPreference(int preference) {
		this.preference = preference;
	}

	@Column(name="auth_type_id",insertable=false,updatable=false)
	private int serviceId;
	@Column(name="auth_type_id",insertable=false,updatable=false)
	private int authTypeId;
	@Column(name="preference")
	private int preference;
	
	@Id
	private AuthSupportsPk pk = new AuthSupportsPk();
	
	
}
