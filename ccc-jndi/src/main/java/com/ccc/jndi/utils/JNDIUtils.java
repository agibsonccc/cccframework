package com.ccc.jndi.utils;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
/**
 * Util class for jndi operations
 * @author Adam Gibson
 *
 */
public class JNDIUtils {
	/**
	 * This will check whether a name is already bound or not
	 * @param context The context to check
	 * @param name the name to check for
	 * @return true if the object is found or a name not found is thrown on the lookup
	 */
	public static boolean nameBound(Context context,String name) {
		try {
			if(context.getNameInNamespace().equals(name))
				throw new IllegalStateException("A context can't look up itself!");
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		try {
			Object lookup=context.lookup(name);
			return lookup!=null;
		}
		catch(NameNotFoundException e) {
			return false;

		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		} 

	}//end nameBound

	/**
	 * This will check whether a name is already bound or not
	 * @param context The context to check
	 * @param name the name to check for
	 * @return true if the object is found or a name not found is thrown on the lookup
	 */
	public static boolean nameBound(Context context,Name name) {
		try {
			if(context.getNameInNamespace().equals(name))
				throw new IllegalStateException("A context can't look up itself!");
		} catch (NamingException e1) {
			e1.printStackTrace();
		}

		try {
			Object lookup=context.lookup(name);
			return lookup!=null;
		}
		catch(NameNotFoundException e) {
			return true;

		} catch (NamingException e) {
			return true;
		}
	}//end nameBound


	/**
	 * This will do a quiet add on the given named object
	 * @param context the context to delete from
	 * @param name the name to add 
	 * @param object the object to add
	 * @return true if the object was added, false otherwise
	 */
	public static boolean quietAdd(Context context,String name,Object object) {
		try {
			context.bind(name,object);
			return true;
		} 
		catch(NameAlreadyBoundException e1) {
			return false;
		}
		
		catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
	}//end quietDelete


	/**
	 * This will do a quiet delete on the given named object
	 * @param context the context to delete from
	 * @param name the name to delete 
	 * @return true if the object was deleted, false otherwise
	 */
	public static boolean quietDelete(Context context,String name) {
		try {
			context.unbind(name);
			return true;
		} catch (NamingException e) {
			e.printStackTrace();
			return false;
		}
	}//end quietDelete
}
