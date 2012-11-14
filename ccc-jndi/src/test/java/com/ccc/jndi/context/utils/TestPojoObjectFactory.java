package com.ccc.jndi.context.utils;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;

public class TestPojoObjectFactory implements ObjectFactory {

	@Override
	public Object getObjectInstance(Object obj, Name name, Context nameCtx,
			Hashtable<?, ?> env) throws Exception {
		if(obj==null) return new TestPojo("");
		else if(obj instanceof String) {
			return new TestPojo(obj.toString());
		}
		else if(obj instanceof TestPojo) return (TestPojo) obj;
		else return new TestPojo("");
	}

}
