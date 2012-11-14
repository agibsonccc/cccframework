package com.ccc.mail.mailinglist.model;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.ccc.publisher.messages.Account;
import com.ccc.publisher.messages.Message;
import com.ccc.publisher.messages.model.MessageImpl;

@Entity
@Table(name="unique_message")
public class UniqueMessage implements Serializable,Comparator<UniqueMessage>,Message {
	@Override
	public Account[] sentTo() {
		return new Account[] {getMaiingList()};
	}
	@Override
	public String messageBody() {
		return message!=null? message.getBody() : null;
	}
	@Override
	public File[] attachments() {
		return message!=null ? message.attachments() : null;
	}
	@Override
	public Timestamp sent() {
		return message!=null  ? message.sent() : null;
	}
	@Override
	public String subject() {
		return message != null ? message.subject() : null;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public MailingList getMaiingList() {
		return maiingList;
	}
	public void setMaiingList(MailingList maiingList) {
		this.maiingList = maiingList;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}


	public int getSubsAtSent() {
		return subsAtSent;
	}
	public void setSubsAtSent(int subsAtSent) {
		this.subsAtSent = subsAtSent;
	}
	@Override
	public int compare(UniqueMessage o1, UniqueMessage o2) {
		if(o1.message.getSent().after(o2.message.getSent())) return 1;
		else if(o1.message.getSent().before(o2.message.getSent())) return -1;
		return 0;
	}
	




	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((maiingList == null) ? 0 : maiingList.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		result = prime * result + subsAtSent;
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
		UniqueMessage other = (UniqueMessage) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (maiingList == null) {
			if (other.maiingList != null)
				return false;
		} else if (!maiingList.equals(other.maiingList))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		if (subsAtSent != other.subsAtSent)
			return false;
		return true;
	}
	public MessageImpl getMessage() {
		return message;
	}
	public void setMessage(MessageImpl message) {
		this.message = message;
	}

	

	@ManyToOne
	@JoinColumn(name="list_id")
	private MailingList maiingList;
	@Id
	@Column(name="id")
	private String id;
	@Column(name="subs_at_sent")
	private int subsAtSent;

	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="message_id")
	private MessageImpl message;


}
