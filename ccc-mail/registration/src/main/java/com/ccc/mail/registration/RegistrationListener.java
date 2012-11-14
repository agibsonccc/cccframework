package com.ccc.mail.registration;

public interface RegistrationListener {

	/**
	 * This will set the id for the registration listener.
	 * @param id the id generated for this listener.
	 */
	public void setId(String id);
	/**
	 * This returns the id held by this listener.
	 * @return the id help by this listener
	 */
	public String getId();
}
