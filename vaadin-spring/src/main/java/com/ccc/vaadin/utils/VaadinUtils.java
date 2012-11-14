package com.ccc.vaadin.utils;

import com.vaadin.Application;
import com.vaadin.service.ApplicationContext;
import com.vaadin.terminal.gwt.client.VBrowserDetails;
import com.vaadin.terminal.gwt.server.WebApplicationContext;
/**
 * Util class for vaadin 
 * @author Adam Gibson
 *
 */
public class VaadinUtils {

	/**
	 * This will return the browser details for the passed in application
	 * @param app the application to pass in
	 * @return the browser details for the given application or null if null
	 * is passed in
	 */
	public static VBrowserDetails detailsForApp(Application app) {
		return new VBrowserDetails(getUserAgent(app));
	}
	
	/**
	 * Return the user agent from the passed in application
	 * @param app the application to get the user agent for
	 * @return the user agent string derived from the application or null if null
	 * is passed in
	 */
	public static String getUserAgent(Application app) {
		if(app==null) return null;
		 
		ApplicationContext context = app.getContext();
		if (context instanceof WebApplicationContext) {
		   String userAgent = ((WebApplicationContext)app.getContext()).
		getBrowser().getBrowserApplication();
		   return userAgent;
		}
		return null;
	}
}
