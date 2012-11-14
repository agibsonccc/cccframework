package com.ccc.webapp.event.base;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Assert;

import com.ccc.webapp.event.api.WebappEvent;
import com.ccc.webapp.event.api.WebappEventHandler;
import com.ccc.webapp.event.api.WebappEventListener;
/**
 * This is a base implementation of the webapp event listener.
 * @author Adam Gibson
 *
 */
public abstract class BaseWebappEventListener implements WebappEventListener {

	public Iterable<WebappEventHandler> handlers() {
		return handlers;
	}

	public void registerHandler(WebappEventHandler handler) {
		Assert.notNull(handler);
		handlers.add(handler);
	}
	
	public void fireEvent(WebappEvent event) throws Exception {
		Assert.notNull(event);
		for(WebappEventHandler handler  :handlers) {
			handler.handleEvent(event);
		}
	}
	
	
	protected List<WebappEventHandler> handlers = new ArrayList<WebappEventHandler>();
	
}//end BaseWebappEventListener
