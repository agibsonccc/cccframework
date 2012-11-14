package com.ccc.users.remoting.service;

import java.rmi.Remote;
import java.rmi.RemoteException;

import com.ccc.users.core.client.UserClient;

/**
 * This is an interface for a UserClient retriever, intended to allow remote 
 * access via RMI to the com.ccc.users functionality
 * 
 * @author mjgodfre
 */

public interface UserClientRetriever extends Remote{

	public UserClient getUserClient() throws RemoteException;
	
	public UserClientRemote getUserClientRemote() throws RemoteException;
}
