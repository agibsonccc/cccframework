package com.ccc.jndi.fetcher.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingException;
/**
 * This will fetch a jndi context from ldap.
 * @author Adam Gibson
 *
 */
public class LDAPContextFetcher<E> implements com.ccc.jndi.context.api.ContextFetcher<E>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public Context fetch() {
		if(context==null) {
			Hashtable<String, String> env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");

			env.put(Context.PROVIDER_URL,
					url);
			env.put(Context.SECURITY_PRINCIPAL, bindDn);
			env.put(Context.SECURITY_CREDENTIALS, bindPassword);
			env.put(Context.OBJECT_FACTORIES,className);
			
			try {
				context = new SerializableInitialDirContext(env);
			}catch(NamingException e) {
				e.printStackTrace();
				return null;
			}

		}

		return context;
	}

	@Override
	public Context fetch(String objectFactory) {
		if(context==null) {
			Hashtable<String, String> env = new Hashtable();
			env.put(Context.INITIAL_CONTEXT_FACTORY,
					"com.sun.jndi.ldap.LdapCtxFactory");

			env.put(Context.PROVIDER_URL,
					url);
			env.put(Context.SECURITY_PRINCIPAL, bindDn);
			env.put(Context.SECURITY_CREDENTIALS, bindPassword);
			env.put(Context.OBJECT_FACTORIES,objectFactory);
			
			try {
				context = new SerializableInitialDirContext(env);
			}catch(NamingException e) {
				e.printStackTrace();
				return null;
			}

		}

		return context;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBindDn() {
		return bindDn;
	}

	public void setBindDn(String bindDn) {
		this.bindDn = bindDn;
	}

	public String getBindPassword() {
		return bindPassword;
	}

	public void setBindPassword(String bindPassword) {
		this.bindPassword = bindPassword;
	}

	public String getClassName() {
		return className;
	}



	public void setClassName(String className) {
		this.className = className;
	}

	protected String url;

	protected String bindDn;

	protected String bindPassword;

	
	private String className;
	
	protected Context context;

	
}
