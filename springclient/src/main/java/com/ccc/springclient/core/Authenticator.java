package com.ccc.springclient.core;

import java.rmi.RemoteException;

import com.ccc.springclient.receiver.RemotingReceiver;

/**
 * This is a spring client for authentication.
 * @author Adam Gibson
 *
 */
public interface Authenticator extends RemotingReceiver {
	/**
	 * This authenticates with a spring service.
	 * @param user the user name to authenticate with
	 * @param password the password to use
	 * @return true if the user was authenticated, false otherwise
	 */
	public boolean authenticate(String user,String password) throws RemoteException;
}//end Authenticator
