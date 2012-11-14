package com.ccc.webapp.event.logout;

import com.ccc.webapp.event.base.BaseWebappEvent;
/**
 * This is a web app event for logging out.
 * @author Adam Gibson
 *
 */
public class LogoutWebappEvent extends BaseWebappEvent {

	public LogoutWebappEvent(String name) {
		super(name);
	}

	public Object getParam(String name) {
		return params.get(name);
	}

	
}//end LogoutWebappEvent
