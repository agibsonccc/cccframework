package com.ccc.camelcomponents.core.api;

import org.json.JSONException;

/**
 * This is a representation of an event formatter
 * @author Adam Gibson
 *
 */
public interface EventFormatter {

	/**
	 * This will take in an input object representing the original event type
	 * @param event the event type to format
	 * @return the formatted string
	 */
	public String formatEvent(Object event);
	/**
	 * This will return one string for multiple events
	 * @param events the events to format
	 * @return the formatted string for multiple events
	 * @throws JSONException 
	 */
	public String formatMultipleEvents(Object[] events);
	/**
	 * This will return the mime type for this event formatter
	 * @return the mime type of this event
	 */
	public String mimeType();
	
	/**
	 * This returns the file type of the particular format
	 * @return the file type for the particular format
	 */
	public String format();
}//end EventFormatter
