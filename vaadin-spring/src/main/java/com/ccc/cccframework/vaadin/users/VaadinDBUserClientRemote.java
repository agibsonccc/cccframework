package com.ccc.cccframework.vaadin.users;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ccc.users.core.BasicUser;
import com.ccc.users.remoting.service.impl.db.DBUserClientRemote;

public class VaadinDBUserClientRemote extends DBUserClientRemote {
	@Override
	public List<BasicUser> activeSessions() throws RemoteException {
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
	public boolean userOnline(String userName) throws RemoteException {
		return onlineUsers.contains(userName) || super.userOnline(userName);
	}

	@Override
	public void bootUser(String userName) throws RemoteException {
		onlineUsers.remove(userName);
		super.bootUser(userName);
	}
	
	public static void addUser(String userName) {
		onlineUsers.add(userName);
	}
	
	private static Set<String> onlineUsers = new HashSet<String>();
}
