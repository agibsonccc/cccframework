package com.ccc.springclient.core;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * This is a wrapper class for a spring context.
 * @author Adam Gibson
 *
 */
public abstract  class SpringContext {
	/**
	 * This loads the context from the given xml file.
	 * @param fileFrom the file to load from
	 * @return the loaded context
	 * @throws IllegalArgumentException if fileFrom doesn't exist, the string is empty, or null
	 */
	public  ApplicationContext loadContext(String fileFrom) throws IllegalArgumentException {
		context = new ClassPathXmlApplicationContext(fileFrom);	
		return context;
	}//end loadContext
	
	/**
	 * This loads the context from the given xml files.
	 * @param fileFrom the file to load from
	 * @return the loaded context
	 * @throws IllegalArgumentException if fileFrom doesn't exist, the string is empty, or null
	 */
	public  ApplicationContext loadContext(String[] files) throws IllegalArgumentException {
		context = new ClassPathXmlApplicationContext(files);
		
		return context;
	}//end loadContext
	
	
	/**
	 * This will load the given bean name from the context.
	 * @param beanName the path to the file to load from.
	 */
	public Object loadBean(String beanName)  {
		if(beanName==null || beanName.length() < 1)
		   return null;
		return context.getBean(beanName);
		
	}//end loadBean
	/**
	 * This loads the bean using a class type
	 * @param <T> the type of the class to get
	 * @param loadFrom the unique class to get form the bean
	 * @return an object loaded from the bean with the given class, if any
	 */
	public  <T> Object loadBean(Class<T> loadFrom) {
		if(loadFrom ==null)
			return null;
		return context.getBean(loadFrom);
	}//end loadBean
	
	
	protected ApplicationContext context;
	/* THe default name when looking for an application context file. */
	public final static String DEFAULT_NAME="applicationContext.xml";
}//end SpringContext
