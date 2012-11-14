package com.ccc.jndi.context;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;

import org.springframework.util.Assert;

import com.ccc.util.filesystem.FileMoverUtil;
/**
 * This is local context for binding objects to the local file system.
 * @author Adam Gibson
 *
 */
public class LocalContext {

	/**
	 * This loads a local context from the file system.
	 * @param context the name of the context. This has to be a directory within the filesystem.
	 * The context MUST BE IN URI form.
	 * @return a context with the given name
	 * @throws NamingException if one occurs
	 * @throws IllegalArgumentException if the given context isn't a directory
	 */
	public static Context localContext(String context,String classNames) throws NamingException,IllegalArgumentException {
		File f = new File(context);
		if(f.exists() && !f.isDirectory())
			throw new IllegalArgumentException("Given context must be a directory.");
		//create a new directory if it doesn't exist

		//FileMoverUtil.createFile(bind);
		if(!f.exists())
			Assert.isTrue(f.mkdir());
		Hashtable env = new Hashtable();
		
		env.put(Context.PROVIDER_URL, f.toURI().toString());
		env.put( Context.INITIAL_CONTEXT_FACTORY, 
				"com.sun.jndi.fscontext.RefFSContextFactory");
		if(classNames!=null)
			env.put(Context.OBJECT_FACTORIES,classNames);

		return  NamingManager.getInitialContext(env);

	}//end localContext

	/**
	 * This loads a local context from the local file system.
	 * @return a local context from the local file system.
	 * @throws NamingException if one occurs
	 */
	public static Context localContext(String classNames) throws NamingException {
		Hashtable env = new Hashtable();
		File bind = new File("cccjndi");
		//FileMoverUtil.createFile(bind);
		bind.mkdir();
		Assert.isTrue(bind.exists(),"File creation failed");
		System.out.println(bind.toURI());
		env.put(Context.PROVIDER_URL, bind.toURI().toString());
		env.put( Context.INITIAL_CONTEXT_FACTORY, 
				"com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.OBJECT_FACTORIES,classNames);

		return  NamingManager.getInitialContext(env);
	}//end localContext





	/**
	 * This loads a local context from the local file system.
	 * @param context the name of the context
	 * @param context the uri of the context
	 * @return a local context from the local file system.
	 * @throws NamingException if one occurs
	 */
	public static Context localContext(URI context,String classNames) throws NamingException {
		Hashtable env = new Hashtable();
		String uri=null;
		uri = context.toString();

		File f = new File(uri);
		if(!f.exists()) 
			Assert.isTrue(f.mkdir());
		else Assert.isTrue(f.canRead() && f.isDirectory());
		try {
			env.put(Context.PROVIDER_URL,f.toURI().toURL().toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		env.put( Context.INITIAL_CONTEXT_FACTORY, 
				"com.sun.jndi.fscontext.RefFSContextFactory");
		if(classNames!=null)

		env.put(Context.OBJECT_FACTORIES,classNames);
		return  NamingManager.getInitialContext(env);
	}//end localContext



	/**
	 * This loads a local context from the local file system.
	 * @param context the name of the context
	 * @return a local context from the local file system.
	 * @throws NamingException if one occurs
	 */
	public static Context localContext(URL context,String classNames) throws NamingException {
		Hashtable env = new Hashtable();
		String uri=null;
		try {
			uri = context.toURI().toString();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		File f = new File(uri);
		if(!f.exists()) Assert.isTrue(f.mkdir());
		else Assert.isTrue(f.canRead() && f.isDirectory());
		env.put(Context.PROVIDER_URL, f.toURI());
		env.put( Context.INITIAL_CONTEXT_FACTORY, 
				"com.sun.jndi.fscontext.RefFSContextFactory");
		env.put(Context.OBJECT_FACTORIES,classNames);
		return  NamingManager.getInitialContext(env);
	}//end localContext


}
