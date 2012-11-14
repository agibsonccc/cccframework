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
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.ccc.publisher.messages.Account;
import com.ccc.util.springhibernate.model.AbstractModel;

@Entity
@Table(name="mailing_list")
@JsonIgnoreProperties("subscribers")
public class MailingList extends AbstractModel implements Serializable,Account {
	@Override
	public String accountType() {
		return "mail";
	}
	@Override
	public String accountName() {
		return name;
	}
	@Override
	public Object accountId() {
		return getId();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4965524267115577497L;
	public Object getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public Set<Subscriber> getSubscribers() {
		return subscribers;
	}
	public void setSubscribers(Set<Subscriber> subscribers) {
		this.subscribers = subscribers;
	}

	
	public Set<Subscriber> getAllSubscribers() {
		return subscribers;
	}

	public String getMailingAddress() {
		return mailingAddress;
	}
	public void setMailingAddress(String mailingAddress) {
		this.mailingAddress = mailingAddress;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((mailingAddress == null) ? 0 : mailingAddress.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result;
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
		MailingList other = (MailingList) obj;
		if (id != other.id)
			return false;
		if (mailingAddress == null) {
			if (other.mailingAddress != null)
				return false;
		} else if (!mailingAddress.equals(other.mailingAddress))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (subscribers == null) {
			if (other.subscribers != null)
				return false;
		}
			
		return true;
	}

	public String getDefaultFromName() {
		return defaultFromName;
	}
	public void setDefaultFromName(String defaultFromName) {
		this.defaultFromName = defaultFromName;
	}
	@Override
	public String toString() {
		return name;
	}
	@Id
	@Column(name="list_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="list_email")
	private String mailingAddress;
	@Column(name="name")
	private String name;
	@Column(name="owner")
	private String owner;
	
	@Column(name="from_name")
	private String defaultFromName;

	@OneToMany(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinTable(name = "mail_subscriber", joinColumns = { @JoinColumn(name = "list_id") }, inverseJoinColumns = { @JoinColumn(name = "subscriber_id") })	
	private Set<Subscriber> subscribers;
	

}//end MailingList
