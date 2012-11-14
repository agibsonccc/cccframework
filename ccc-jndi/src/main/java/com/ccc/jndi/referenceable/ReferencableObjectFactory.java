package com.ccc.jndi.referenceable;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

public class ReferencableObjectFactory<E> implements ObjectFactory {


	public Object getObjectInstance(Object obj, Name arg1, Context arg2,
			Hashtable arg3) throws Exception {
		if (obj instanceof Reference) {
			Reference ref = (Reference) obj;

			if (ref.getClassName().equals(e.getClass().getName())) {
				RefAddr addr = ref.get(e.getClass().getName().toLowerCase());
				if (addr != null) {
					return new NamedObject ((String) addr.getContent());
				}
			}
		}
		return null;
	}

	private E e;
}
