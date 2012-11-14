package com.ccc.jndi.referenceable;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;



/**
 * This is an object factory that when given a reference for a Named object,
 * will create an instance of the corresponding Named Object.
 */
public class NamedObjectFactory implements ObjectFactory {

	
	public NamedObjectFactory(String addressName) {
		this.addressName=addressName;
	}
	
	public NamedObjectFactory() {
		
	}
	/**
	 * This returns an named object with the given lookup name.
	 */
	public Object getObjectInstance(Object obj, Name name, Context ctx,
			Hashtable<?, ?> env) throws Exception {

		if (obj instanceof Reference) {
			Reference ref = (Reference) obj;

			if (ref.getClassName().equals(NamedObject.class.getName())) {
				RefAddr addr = ref.get(addressName== null ? DEFAULT_NAME : addressName);
				if (addr != null) {
					NamedObject get= new NamedObject((String) addr.getContent());
					if(get.objectHeld()!=null) return get.objectHeld();
					else return get;
				}
			}
		}
		return null;
	}
	private String addressName;
	public final static String DEFAULT_NAME="ccc";
}