package com.ccc.jndi.referenceable;

import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
/**
 * This is a class which compiles all of the jndi needed ways to build an object.
 * @author Adam Gibson
 *
 */
public class AbstractReferenceableObject implements Referenceable {

	public AbstractReferenceableObject(String name,ObjectFactory objectFactory) {
		reference = new Reference(name,address(),objectFactory.getClass().getName(),null);
		this.setName(name);
		setObjectFactory(objectFactory);
	}
	public Reference getReference() throws NamingException {
		return reference;
	}

	public void setReference(Reference reference) {
		this.reference = reference;
	}

	public  RefAddr address() {
		return new StringRefAddr(getName(),name);
	}

	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public ObjectFactory getObjectFactory() {
		return objectFactory;
	}
	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	private Reference reference;

	private String name;

	private ObjectFactory objectFactory;
}
