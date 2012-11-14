package com.ccc.springclient.factory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccc.springclient.core.BasicSpringContext;
import com.ccc.springclient.core.SpringContext;
/**
 * This is a spring context factory which acts as a container
 * for loading spring contexts.
 * @author Adam Gibson
 *
 */
public class SpringContextFactory {
	/**
	 * This will load a file context from the specified file
	 * @param fileName the name of the file to load
	 */
	public static void loadContext(String fileName) {
		if(fileName==null || fileName.isEmpty())
		     return;
		SpringContext put = new BasicSpringContext();
		put.loadContext(fileName);
		context.put(fileName, put);
	}
	/**
	 * This will load all of the specified files in the given array
	 * @param fileNames the names of the files to load
	 */
	public static void loadContext(String[] fileNames) {
		if(fileNames==null)
		    return;
		for(String s : fileNames) {
			SpringContext put = new BasicSpringContext();
			put.loadContext(s);
			context.put(s,put);
		}
		
	}
	
	/**
	 * This will load all of the specified files in the given array
	 * @param fileNames the names of the files to load
	 */
	public static void loadContext(List<String> fileNames) {
		if(fileNames==null)
		    return;
		for(String s : fileNames) {
			SpringContext put = new BasicSpringContext();
			put.loadContext(s);
			context.put(s,put);
		}
		
	}
	/**
	 * This will load all of the specified files in the given array
	 * @param fileNames the names of the files to load
	 */
	public static void loadContext(Set<String> fileNames) {
		if(fileNames==null)
		    return;
		for(String s : fileNames) {
			SpringContext put = new BasicSpringContext();
			put.loadContext(s);
			context.put(s,put);
		}
		
	}//end loadContext
	
	public static SpringContext getContext(String file) {
		return context.get(file);
	}
	
	private static Map<String,SpringContext> context = new HashMap<String,SpringContext>();
}//end SpringContextFactory
