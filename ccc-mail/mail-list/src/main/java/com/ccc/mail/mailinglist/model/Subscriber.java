package com.ccc.mail.mailinglist.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;
@Entity
@Table(name="subscriber")
public class Subscriber implements Serializable {

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String toString() {
		return email;
	}




	public void setMailngLists(Set<MailingList> mailngLists) {
		this.mailngLists = mailngLists;
	}
	public Set<MailingList> getMailngLists() {
		return mailngLists;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((mailngLists == null) ? 0 : mailngLists.hashCode());
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
		Subscriber other = (Subscriber) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id != other.id)
			return false;

		return true;
	}



	public Timestamp getJoined() {
		return joined;
	}
	public void setJoined(Timestamp joined) {
		this.joined = joined;
	}



	/**
	 * 
	 */
	private static final long serialVersionUID = 8056963358977589725L;
	@OneToMany(cascade=CascadeType.ALL,fetch=FetchType.EAGER)
	@JoinTable(name = "mail_subscriber", joinColumns = { @JoinColumn(name = "subscriber_id") }, inverseJoinColumns = { @JoinColumn(name = "list_id") })
	private Set<MailingList> mailngLists;

	@Column(name="email")
	private String email;

	@Column(name="joined")
	private Timestamp joined;
	@Id
	@Column(name="subscriber_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;


}//end Subscriber
