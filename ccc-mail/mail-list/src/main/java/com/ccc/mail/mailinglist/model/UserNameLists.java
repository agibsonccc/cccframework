package com.ccc.mail.mailinglist.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="user_lists")
public class UserNameLists implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MailingList getList() {
		return list;
	}
	public void setList(MailingList list) {
		this.list = list;
	}
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
	@ManyToOne
	@JoinColumn(name="email")
	private MailingList list;
	@Column(name="user_name")
	private String userName;
	@Id
	@Column(name="id")
	private int id;
}
