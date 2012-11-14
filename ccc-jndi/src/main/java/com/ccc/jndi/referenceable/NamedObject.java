package com.ccc.jndi.referenceable;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;


/**
 * THis represents a named object. This is mainly a wrapper class
 * meant for JNDI.
 * @author Adam Gibson
 *
 */
public class NamedObject  implements Referenceable{
	/**
	 * This is meant for initializing a named object with the object to be held.
	 * It holds a string if only a string is specified.
	 * @param name the name of the object to be held
	 */
	public NamedObject(String name) {
		this.name=name;
		objectHeld=name;
	}
	/**
	 * This holds a copy of the object to be held and the specified name of the object.
	 * @param name the name of the object
	 * @param held the object to be held
	 */
	public NamedObject(String name,Object held,String addrType) {
		
		this.name=name;
		this.addrType=addrType;
		this.objectHeld=held;
	}
	public String name() {
		return name;
	}

	/**
	 * THis returns a reference to the named object based on 
	 * the address type and the name of the object.
	 */
	public Reference getReference() throws NamingException {

	    return new Reference(NamedObject.class.getName(), new StringRefAddr("ccc",
	    		name), NamedObjectFactory.class.getName(), null); // factory location
	  }
	
	
	public Object objectHeld() {
		return objectHeld;
	}
	
	@Override
	public String toString() {
		return name;
	}

	
	private String name;
	
	private Object objectHeld;
	
	private String addrType;
}
