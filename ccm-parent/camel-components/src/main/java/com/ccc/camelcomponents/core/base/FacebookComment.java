package com.ccc.camelcomponents.core.base;

import java.io.Serializable;
import java.sql.Timestamp;

import com.google.api.client.json.jackson.JacksonFactory;

public class FacebookComment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1468317691629182814L;

	private String id;
	
	private Timestamp createdTime;
	
	private String message;
	
	
	public class From implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3976722232909770996L;
		private String id;
		private String name;
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String toString() {
			JacksonFactory factory = new JacksonFactory();
			return factory.toPrettyString(this);
		}
		
	}
	
	public class Paging implements Serializable  {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4077464936248618984L;
		private String next;

		public String getNext() {
			return next;
		}

		public void setNext(String next) {
			this.next = next;
		}
		public String toString() {
			JacksonFactory factory = new JacksonFactory();
			return factory.toPrettyString(this);
		}
		
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Timestamp getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Timestamp createdTime) {
		this.createdTime = createdTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public From getFrom() {
		return from;
	}

	public void setFrom(From from) {
		this.from = from;
	}

	public String toString() {
		JacksonFactory factory = new JacksonFactory();
		return factory.toPrettyString(this);
	}
	
	private From from;
}
