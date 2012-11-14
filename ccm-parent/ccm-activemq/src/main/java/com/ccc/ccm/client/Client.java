package com.ccc.ccm.client;

import java.util.Hashtable;
import java.net.MalformedURLException;
import java.net.URL;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


/* 
 * 
 */
public class Client {
	private static ConnectionFactory connectionFactory;
	private Context ctx;
	private final String PROVIDER_URL="java:comp/env/ccc.mtecsz.com:8080";
	
	
	public Client() throws NamingException, MalformedURLException
	{
		//URL url = new URL(PROVIDER_URL);
		Hashtable env = new Hashtable<Context,String>(2);
		env.put(Context.INITIAL_CONTEXT_FACTORY,"com.sun.jndi.cosnaming.CNCtxFactory");
		env.put(Context.PROVIDER_URL,PROVIDER_URL);
		ctx = new InitialContext(env);
		System.out.println("looking up connection factory");
		connectionFactory = (ConnectionFactory) ctx.lookup("connectionFactory");
	}
	
	public static Connection newConnection() throws JMSException
	{
		return connectionFactory.createConnection();
	}
	
	public static void main(String[]args) throws NamingException, MalformedURLException
	{
		Client client = new Client();
	}
	
}
