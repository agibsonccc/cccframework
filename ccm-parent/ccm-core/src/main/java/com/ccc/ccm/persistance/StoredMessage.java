package com.ccc.ccm.persistance;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="im_message")
public class StoredMessage {
	
	
	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Timestamp getSentTime() {
		return sentTime;
	}

	public void setSentTime(Timestamp sentTime) {
		this.sentTime = sentTime;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getConversationId() {
		return conversationId;
	}

	public void setConversationId(String conversationId) {
		this.conversationId = conversationId;
	}

	@Column(name="body")
	private String body;
	
	@Column(name="time_sent")
	private Timestamp sentTime;
	
	@Column(name="sender")
	private String sender;
	@Column(name="conversation_id")
	private String conversationId;
	
	@Id
	@Column(name="id")
	private int id;
}//end StoredMessage
