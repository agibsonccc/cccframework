package com.ccc.jndi.fetcher.filesystem;

import javax.naming.Context;
import javax.naming.NamingException;

import com.ccc.jndi.context.LocalContext;
import com.ccc.jndi.context.api.ContextFetcher;
/**
 * This implements a file system context fetcher.
 * @author Adam Gibson
 *
 */
public class FileSystemContextFetcher<E> implements ContextFetcher<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8950898032258009326L;

	
	@Override
	public Context fetch(String objectFactory) {
		if(context==null ){
			try {
				//Specified name for the context.
				if(contextName!=null && !contextName.isEmpty())
					context=LocalContext.localContext(contextName,objectFactory);
				context= LocalContext.localContext(objectFactory);
			} catch (NamingException e) {
				e.printStackTrace();
				return null;
			}

		}
		return context;
	}
	
	
	@Override
	public Context fetch() {
		if(context==null ){
			try {
				//Specified name for the context.
				if(contextName!=null && !contextName.isEmpty())
					context=LocalContext.localContext(contextName);
				context= LocalContext.localContext(className);
			} catch (NamingException e) {
				e.printStackTrace();
				return null;
			}

		}
		return context;
	}
	
	public String getContextName() {
		return contextName;
	}

	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	private Context context;

	private String contextName;
	
	private String className;


}//end FileSystemContextFetcher
