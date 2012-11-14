package com.ccc.ccm.core;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
//import org.apache.activemq.*;
//import org.apache.qpid.client.AMQConnectionFactory;
//import org.apache.qpid.url.URLSyntaxException;

import javax.jms.*;
import javax.naming.*;

/*
 * Generic JMS compliant messaging client. May be instantiated by any other 
 * class, otherwise a command line implementation is provided by running main
 * method.
 * 
 * Client is configurable to act as a message producer, receiver, or both by 
 * implementing a new thread for whichever functionality is desired
 * 
 * @author Michael Godfrey
 * @version 23May2011
 * @exception NamingException
 * @exception IOException 
 */
public class Conversation implements Serializable {
	private final String VERSION = "0.00";
	private final String DATE_MODIFIED = "23 May 2011";
	private Session session;
	private Queue destination;
	private static Context ctx;
	//private static ConnectionFactory conFac;
	private static Connection connection;
	//private static String cfURL = null;
	
	
	public Conversation(Connection c) throws NamingException,JMSException{
		System.out.println("Welcome to Clever Cloud Messenger");
		System.out.println("    version: " + VERSION);
		System.out.println("    Michael Godfrey: " + DATE_MODIFIED);
		System.out.println("    This software is the sole property of Clever Cloud Computing LLC");
		System.out.println("");

		connection = c;
		session = c.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
/*
	public static Session getSession)(){
		ctx.lookup(arg0);
		return getSession;
	}
	
	public static Sender createSender(int cap, String dest){
		
	}
	
	public Thread createReciever(){
		
	}
	
	/*
	 * Main is only to run if command line operation is desired
	 *
	public static void main(String[]args) throws IOException, NamingException, URLSyntaxException, JMSException
	{
		int code;
		ConnectionFactory cf = new ConnectionFactory
		Conversation conversation = new Conversation();
		InputStreamReader cin = new InputStreamReader(System.in);
			
		
		while(true){
	    //prompt user for what to do
		cl_Options();
		System.out.print("> ");
		code = cin.read();
		
		switch(code)
		{
		case(0) :
			System.out.println("Goodbye");
			System.exit(0);
		case(1) : //send only
			con.start();
			Session so_session = 
				con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Thread so = new Thread(              //send only thread
				new Runnable(){public void run() {		
			}});
			so.start();
			break;
		case(2) : //recieve only
			con.start();
			Thread ro_reciever = new Thread(     //recieve only thread
				new Runnable(){public void run(){
					
			}});
			ro_reciever.start();
			break;
		case(3) : //send/recieve
			con.start();
			Thread sr_sender = new Thread(       //sender thread
				new Runnable(){public void run(){
						
			}});
			Thread sr_reciever = new Thread(     //reciever thread 
				new Runnable(){public void run(){
						
				}});
			/**  Need to implement--when one thread exits, it must send an interrupt
			 *   to the other to tell it to exit        **		
			sr_reciever.start();
			sr_sender.start();
			break;
		default : //say what?
			System.out.println("--Enter a valid option--");
			break;
		}
		}
	}//end main
	
	/* 
	 * For command line operation of class Client
	 * Prints list of options to command line 
	 */
	private void cl_Options()
	{
		System.out.println("----Select Mode----");
		System.out.println("    (0) exit");
		System.out.println("    (1) Send Only");
		System.out.println("    (2) Recieve Only");
		System.out.println("    (3) Send/Recieve");
	}
	
}//end class Client
