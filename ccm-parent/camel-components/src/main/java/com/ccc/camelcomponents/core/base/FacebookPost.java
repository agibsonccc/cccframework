package com.ccc.camelcomponents.core.base;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Timestamp;

public class FacebookPost implements Serializable {


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7633656062372918959L;

	private String message;

	private String picture;

	private String link;

	private String name;

	private String caption;

	private String description;

	private String source;

}
