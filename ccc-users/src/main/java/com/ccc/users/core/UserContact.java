package com.ccc.users.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * This is a user contact for email.
 * @author Adam Gibson
 *
 */
@Entity
@Table(name="user_contact")
public class UserContact implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -712898347302102002L;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactFor() {
		return contactFor;
	}
	public void setContactFor(String contactFor) {
		this.contactFor = contactFor;
	}
	
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}


	@Column(name="email")
	private String email;
	@Id
	@Column(name="user_contact_for")
	private String contactFor;
	@Column(name="user_name")
	private String userName;
}//end UserContact
