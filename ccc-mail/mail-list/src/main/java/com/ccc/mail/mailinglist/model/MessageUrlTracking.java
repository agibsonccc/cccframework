package com.ccc.mail.mailinglist.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="message_url_tracking")
public class MessageUrlTracking implements Serializable {

	
	
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
	public UniqueMessage getUniqueMessage() {
		return uniqueMessage;
	}
	public void setUniqueMessage(UniqueMessage uniqueMessage) {
		this.uniqueMessage = uniqueMessage;
	}
	public UrlTracking getUrlTracking() {
		return urlTracking;
	}
	public void setUrlTracking(UrlTracking urlTracking) {
		this.urlTracking = urlTracking;
	}
	
	
	public Set<UrlRequest> getRequests() {
		return requests;
	}
	public void setRequests(Set<UrlRequest> requests) {
		this.requests = requests;
	}


	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="id")
	private int id;
	@ManyToOne
	@JoinColumn(name="message_id")
	private UniqueMessage uniqueMessage;
	@ManyToOne
	@JoinColumn(name="url_id")
	private UrlTracking urlTracking;
	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinTable(name = "message_link_track", joinColumns = { @JoinColumn(name = "url_id") })	
	private Set<UrlRequest> requests;
}
