package com.ccc.mail.mailinglist.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
@Entity
@Table(name="message_tracking")
public class ListMessageTracking implements Serializable {



	public Timestamp getClickedTime() {
		return clickedTime;
	}
	public void setClickedTime(Timestamp clickedTime) {
		this.clickedTime = clickedTime;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}

	public UniqueMessage getUniqueMessage() {
		return uniqueMessage;
	}
	public void setUniqueMessage(UniqueMessage uniqueMessage) {
		this.uniqueMessage = uniqueMessage;
	}

	public String getClickId() {
		return clickId;
	}
	public void setClickId(String clickId) {
		this.clickId = clickId;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name="ip")
	private String ip;
	@Column(name="clicked_time")
	private Timestamp clickedTime;
	@ManyToOne
	@JoinColumn(name="message_id")
	private UniqueMessage uniqueMessage;
	@Column(name="click_id")
	private String clickId;
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="email")
	private String email;
}
