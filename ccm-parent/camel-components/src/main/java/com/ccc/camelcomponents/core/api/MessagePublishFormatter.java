package com.ccc.camelcomponents.core.api;
/**
 * This is a message publish formatter for posting to various social sites
 * @author Adam Gibson
 *
 */
public interface MessagePublishFormatter {
	/**
	 * This will format the given message in to a proper post to be 
	 * put on a given site
	 * @param message the original message to translate
	 * @return a translated message
	 */
	public String formatMessage(Object message);
	
}//end MessagePublishFormatter
