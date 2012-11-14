package com.ccc.users.auth;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity()
@Table(name = "persistent_login")
//@org.hibernate.annotations.Entity(mutable = false)
/**
 * A Hibernate class used to create the table for the "remember-me"
 * persistent token mechanism.
 *
 * @author skashani
 *
 */
public class PersistentLogin implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -280349165922542558L;

	@Column(name = "username")
	private String userName;

	@Id
	//@NotNull
	private String series;

	//@NotNull
	private String token;

	@Column(name = "last_used", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP")
	//@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUsed;

	public String getUserName() {
		return userName;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public String getSeries() {
		return series;
	}

	public void setSeries(final String series) {
		this.series = series;
	}

	public String getToken() {
		return token;
	}

	public void setToken(final String token) {
		this.token = token;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(final Date lastUsed) {
		this.lastUsed = lastUsed;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	 @Override
	 public boolean equals(final Object other) {
		 if (!(other instanceof PersistentLogin)) {
			 return false;
		 }
		 final PersistentLogin castOther = (PersistentLogin) other;
		 return new EqualsBuilder().append(userName, castOther.userName)
				 .append(token, castOther.token)
				 .append(lastUsed, castOther.lastUsed)
				 .isEquals();
	 }

	 /*
	  * (non-Javadoc)
	  *
	  * @see java.lang.Object#hashCode()
	  */
	 @Override
	 public int hashCode() {
		 return new HashCodeBuilder().append(userName).append(token).append(lastUsed).toHashCode();
	 }

	 /*
	  * (non-Javadoc)
	  *
	  * @see java.lang.Object#toString()
	  */
	 @Override
	 public String toString() {
		 return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("userName",
				 userName)
				 .append("series", series)
				 .append("token", token)
				 .append("lastUsed",
						 lastUsed)
						 .toString();
	 }
}
