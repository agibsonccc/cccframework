package com.ccc.mail.registration;
/**
 * This generates a registration link for 
 * confirmation emails.
 * @author Adam Gibson
 *
 */
public interface ConfirmLinkGenerator {

	public String generateLink();
	
	/**
	 * This will wire in the returned id for a given registration listener
	 * @param listener the listener to wire the id for
	 * @return the generated list
	 */
	public String generateLink(RegistrationListener listener);
}
