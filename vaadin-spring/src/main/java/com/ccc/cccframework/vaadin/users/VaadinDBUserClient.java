package com.ccc.cccframework.vaadin.users;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ccc.users.core.BasicUser;
import com.ccc.users.db.client.DBUserClient;
/**
 * This an override for jvm wide active sessions for users
 * This is meant for use with multiple applications within the same jvm
 * that may not have persistent sessions
 * @author Adam Gibson
 *
 */
public class VaadinDBUserClient extends DBUserClient {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3625377546554205366L;

	@Override
	public List<BasicUser> activeSessions() {
		List<BasicUser> ret = new ArrayList<BasicUser>();
		for(String s : onlineUsers) {
			BasicUser u=userForName(s);
			ret.add(u);
		}
		List sessions=sessionRegistry.getAllPrincipals();
		for(Object o : sessions) {
			String comp=o.toString();
			if(!onlineUsers.contains(comp)) {
				BasicUser u=userForName(comp);
				ret.add(u);
			}
		}
		return ret;
	}

	@Override
	public boolean userOnline(String userName) {
		return onlineUsers.contains(userName) || super.userOnline(userName);
	}

	@Override
	public void bootUser(String userName) {
		onlineUsers.remove(userName);
		super.bootUser(userName);
	}
	
	public static void addUser(String userName) {
		onlineUsers.add(userName);
	}
	
	private static Set<String> onlineUsers = new HashSet<String>();
}
