package com.ccc.security.session.tracking;

import java.util.LinkedList;
import java.util.List;

public class SessionTracker {
	
	public SessionTracker() {
		usersList = new LinkedList<String>();
	}
	
	public static void addUser(String username) {
		usersList.add(username);
	}
	
	public static void increment() {
		count++;
	}
	
	public static void decrement() {
		count--;
	}
	
	private static int count;
	private static List usersList;
}//end class 
