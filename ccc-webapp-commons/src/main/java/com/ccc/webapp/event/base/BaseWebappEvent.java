package com.ccc.webapp.event.base;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.Assert;

import com.ccc.webapp.event.api.WebappEvent;
/**
 * Base implementation for a webapp event.
 * @author Adam GIbson
 *
 */
public abstract class BaseWebappEvent implements WebappEvent {
	/**
	 * Constructs a webapp event with the given name
	 * @param name the name of this event.
	 * This will throw an exception if name is null or empty
	 */
	public BaseWebappEvent(String name) {
		Assert.notNull(name,"Name for event must not be null!");
		Assert.hasLength(name,"Name for event must not be empty!");
		
		this.name=name;
	}
	public Map<String, Object> params() {
		return params;
	}

	public String name() {
		return name;
	}
	
	public void registerParam(String string, Object o) {
		params.put(string,o);
	}
	public void deleteParam(String s) {
		params.remove(s);
	}

	protected Map<String,Object> params = new HashMap<String,Object>();

	
	protected String name;

}//end BaseWebappEvent
