/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not possess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.springhibernate.dao;
import javax.naming.NamingException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.jdbc.JDBCContext.Context;

public class HibernateUtil {
	private static  SessionFactory sessionFactory;
	
	public static void setSessionFactory(SessionFactory sessionFactory) {
		HibernateUtil.sessionFactory=sessionFactory;
	}
	
	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	 public static Context getInitialContext() throws NamingException {
		    return (Context) new javax.naming.InitialContext();
		  } 
}
