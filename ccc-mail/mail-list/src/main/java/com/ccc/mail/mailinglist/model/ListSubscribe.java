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
@Table(name="mail_subscriber")
public class ListSubscribe implements Serializable {



	public Timestamp getJoined() {
		return joined;
	}
	public void setJoined(Timestamp joined) {
		this.joined = joined;
	}
	public boolean isUnsubbed() {
		return unsubbed==null ? false : unsubbed;
	}
	public void setUnsubbed(boolean unsubbed) {
		this.unsubbed = unsubbed;
	}
	public MailingList getMailingList() {
		return mailingList;
	}
	public void setMailingList(MailingList mailingList) {
		this.mailingList = mailingList;
	}
	public Subscriber getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(Subscriber subscriber) {
		this.subscriber = subscriber;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}


	public Timestamp getUnSubbedTime() {
		return unSubbedTime;
	}
	public void setUnSubbedTime(Timestamp unSubbedTime) {
		this.unSubbedTime = unSubbedTime;
	}


	@Column(name="unsubbed_time",columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp unSubbedTime;
	@Column(name="joined",columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp joined;
	@Column(name="unsubbed")
	private Boolean unsubbed=false;
	@ManyToOne
	@JoinColumn(name="list_id")
	private MailingList mailingList;
	@ManyToOne
	@JoinColumn(name="subscriber_id")
	private Subscriber subscriber;
	@Column(name="id")
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
