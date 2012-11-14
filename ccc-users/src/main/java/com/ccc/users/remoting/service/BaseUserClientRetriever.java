package com.ccc.users.remoting.service;

import java.rmi.RemoteException;

import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.users.core.client.UserClient;

/**
 * 
 * @author mjgodfre
 */
public class BaseUserClientRetriever implements UserClientRetriever {

	@Override
	public UserClient getUserClient() {
		return userClient;
	}

	public void setUserClient(UserClient userClient) {
		this.userClient = userClient;
	}

	@Autowired(required=false)
	private UserClient userClient;
	@Autowired(required=false)
	private UserClientRemote userClientRemote;
	@Override
	public UserClientRemote getUserClientRemote() throws RemoteException {
		return userClientRemote;
	}
}
