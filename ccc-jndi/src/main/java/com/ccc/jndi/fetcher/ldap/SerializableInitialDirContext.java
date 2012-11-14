package com.ccc.jndi.fetcher.ldap;

import java.io.Serializable;
import java.util.Hashtable;

import javax.naming.NamingException;
import javax.naming.directory.InitialDirContext;

public class SerializableInitialDirContext extends InitialDirContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerializableInitialDirContext() throws NamingException {
		super();
		// TODO Auto-generated constructor stub
	}

	public SerializableInitialDirContext(boolean lazy) throws NamingException {
		super(lazy);
		// TODO Auto-generated constructor stub
	}

	public SerializableInitialDirContext(Hashtable<?, ?> environment)
			throws NamingException {
		super(environment);
		// TODO Auto-generated constructor stub
	}

}
