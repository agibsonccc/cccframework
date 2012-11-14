package com.ccc.mail.mailinglist.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="url_tracking")
public class UrlTracking implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 793553730456686668L;
	public String getUrlId() {
		return urlId;
	}
	public void setUrlId(String urlId) {
		this.urlId = urlId;
	}
	public String getLongUrl() {
		return longUrl;
	}
	public void setLongUrl(String longUrl) {
		this.longUrl = longUrl;
	}
	@Id
	@Column(name="id")
	private String urlId;
	@Column(name="long_url")
	private String longUrl;
	
	
}
