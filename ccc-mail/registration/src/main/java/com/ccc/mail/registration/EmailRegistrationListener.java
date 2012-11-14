package com.ccc.mail.registration;

public class EmailRegistrationListener implements RegistrationListener {

	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	@Override
	public void setId(String id) {
		this.id=id;
	}


	@Override
	public String getId() {
		return id;
	}
	
	private String id;

	private String email;
}
