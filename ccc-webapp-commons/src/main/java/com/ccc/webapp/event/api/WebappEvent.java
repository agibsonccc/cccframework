package com.ccc.webapp.event.api;

import java.util.Map;
/**
 * This is a webapp event used in passing parameters to listeners.
 * @author Adam Gibson
 *
 */
public interface WebappEvent {
	/**
	 * This will retrieve the given parameter
	 * based on the name
	 * @param name the name of the parameter to retrieve
	 * @return the parameter if it exists, null otherwise
	 */
	public Object getParam(String name);
	
	/**
	 * This a map of parameters identified by name
	 * to pass for listeners 
	 * @return the parameters for this event
	 */
	public Map<String,Object> params();
	/**
	 * The name of this event
	 * @return the name of this event
	 */
	public String name();
	
	/**
	 * This will register a parameter with the given name
	 * @param string the name of the event to register
	 * @param o the object to register as a parameter
	 */
	public void registerParam(String string,Object o);
	
	/**
	 * This will delete the parameter with the given name. If it doesn't exist,
	 * nothing will happen
	 * @param s the name of the parameter to delete
	 */
	public void deleteParam(String s);
}//end WebappEvent
