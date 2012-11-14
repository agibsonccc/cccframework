package com.ccc.springclient.core;
import org.springframework.context.ApplicationContext;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * This is a base client that must be extended in order to have access to the client bean.
 * @author Adam Gibson
 *
 */

public abstract class BaseClient implements Client {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public abstract Object connect();
	/**
	 * This returns the proxy bean to use when calling a remoting service.
	 * The proxy loads the service url and the class to use as an interface when calling functions.
	 * Make sure these are configured properly.
	 * @return the proxy bean to use with this remoting service.
	 */
	public  RmiProxyFactoryBean serviceObject() {
		ApplicationContext c=null;
		//Initialize context.
		if(context==null) {
			context= new BasicSpringContext();
			//Load default
			if(fileName==null  || fileName.isEmpty()) {
				c=	context.loadContext("appconfig.xml");

			}
			//Custom file name
			else {
				c=context.loadContext(fileName);

			}
		}
		if(!c.containsBean("httpInvokerProxy"))
			throw new IllegalStateException("There was an error trying to load the proxy.");

		Object o=context.loadBean("httpInvokerProxy");
		//Check if context was loaded properly.
		if(o==null)
			throw new IllegalStateException("Proxy was null.");
		//Couldn't find by name, load from class
		if(!(o instanceof HttpInvokerProxyFactoryBean))
			o=c.getBean(HttpInvokerProxyFactoryBean.class);
		if(!(o instanceof HttpInvokerProxyFactoryBean))
			throw new IllegalStateException("Class not properly loaded");


		//	System.out.println(c.getBean(HttpInvokerProxyFactoryBean.class));
		return (RmiProxyFactoryBean) o;
	}//end serviceObject

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	private SpringContext context;
	/* Optional file name if the user wants to override the default xml */
	protected String fileName;

}//end BaseClient
