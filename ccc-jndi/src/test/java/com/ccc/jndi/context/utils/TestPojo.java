package com.ccc.jndi.context.utils;

import java.io.Serializable;

public class TestPojo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1826503200760055994L;

	public TestPojo(String name) {
		setMessage(name);
	}
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	private String message;
}
