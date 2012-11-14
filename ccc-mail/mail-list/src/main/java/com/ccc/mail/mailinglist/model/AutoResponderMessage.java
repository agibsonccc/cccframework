package com.ccc.mail.mailinglist.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.publisher.messages.model.MessageImpl;
@Entity
@Table(name="auto_responder")
public class AutoResponderMessage implements Serializable {

	public MailingList getMailingList() {
		return mailingList;
	}
	public void setMailingList(MailingList mailingList) {
		this.mailingList = mailingList;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	
	public int getNumDaysSubscribed() {
		return numDaysSubscribed;
	}
	public void setNumDaysSubscribed(int numDaysSubscribed) {
		this.numDaysSubscribed = numDaysSubscribed;
	}


	public boolean isHtml() {
		return isHtml;
	}
	public void setHtml(boolean isHtml) {
		this.isHtml = isHtml;
	}


	public MessageImpl getMessage() {
		return message;
	}
	public void setMessage(MessageImpl message) {
		this.message = message;
	}


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@ManyToOne
	@JoinColumn(name="list_id")
	private MailingList mailingList;
	@Id
	@Column(name="id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="days_subscribed")
	private int numDaysSubscribed;
	@Column(name="is_html")
	private boolean isHtml;
	@ManyToOne
	@JoinColumn(name="message_id")
	private MessageImpl message;
}
