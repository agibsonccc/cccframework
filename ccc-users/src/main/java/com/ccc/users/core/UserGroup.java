package com.ccc.users.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="user_group")
@Entity
public class UserGroup implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7770601631502875135L;

	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Column(name="group_name")
	private String groupName;
	@Id
	@Column(name="user_name")
	private String userName;
}//end UserGroup
