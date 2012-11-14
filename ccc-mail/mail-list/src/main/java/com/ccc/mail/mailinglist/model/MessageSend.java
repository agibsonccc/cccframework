package com.ccc.mail.mailinglist.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="sent_track")
public class MessageSend implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String getEmailTo() {
		return emailTo;
	}
	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}
	public String getSentFrom() {
		return sentFrom;
	}
	public void setSentFrom(String sentFrom) {
		this.sentFrom = sentFrom;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public int getNumTimes() {
		return numTimes;
	}
	public void setNumTimes(int numTimes) {
		this.numTimes = numTimes;
	}


	@Column(name="sent_to")
	private String emailTo;
	@Column(name="sent_from")
	private String  sentFrom;
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="num_times")
	private int numTimes;
}
