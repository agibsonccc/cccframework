package com.ccc.mail.mailinglist.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="bounce_rate")
public class BounceRate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getNumTimesBounced() {
		return numTimesBounced;
	}
	public void setNumTimesBounced(int numTimesBounced) {
		this.numTimesBounced = numTimesBounced;
	}
	public int getNumTimesSent() {
		return numTimesSent;
	}
	public void setNumTimesSent(int numTimesSent) {
		this.numTimesSent = numTimesSent;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	
	
	public String getSentFrom() {
		return sentFrom;
	}
	public void setSentFrom(String sentFrom) {
		this.sentFrom = sentFrom;
	}


	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="bounced")
	private int numTimesBounced;
	@Column(name="sent")
	private int numTimesSent;
	@Column(name="email")
	private String emailAddress;
	@Column(name="sent_from")
	private String sentFrom;
}
