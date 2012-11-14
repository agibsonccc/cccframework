package com.ccc.users.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name="user_settings")
public class UserSettings implements Serializable {

	public UserSettings() {
		super();
	}
	public UserSettings(String settingName, String settingVal, String userName) {
		super();
		this.settingName = settingName;
		this.settingVal = settingVal;
		this.userName = userName;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4235148672779803445L;
	public String getSettingName() {
		return settingName;
	}
	public void setSettingName(String settingName) {
		this.settingName = settingName;
	}
	public String getSettingVal() {
		return settingVal;
	}
	public void setSettingVal(String settingVal) {
		this.settingVal = settingVal;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((settingName == null) ? 0 : settingName.hashCode());
		result = prime * result
				+ ((settingVal == null) ? 0 : settingVal.hashCode());
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
		UserSettings other = (UserSettings) obj;
		if (settingName == null) {
			if (other.settingName != null)
				return false;
		} else if (!settingName.equals(other.settingName))
			return false;
		if (settingVal == null) {
			if (other.settingVal != null)
				return false;
		} else if (!settingVal.equals(other.settingVal))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UserSettings [settingName=" + settingName + ", settingVal="
				+ settingVal + ", userName=" + userName + "]";
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	@Id
	@Column(name="setting_id")
	private String id;
	@Column(name="setting_name")
	private String settingName;
	@Column(name="setting_val")
	private String settingVal;
	@Column(name="user_name")
	private String userName;

}//end UserSettings
