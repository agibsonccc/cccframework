package com.ccc.springclient.receiver;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * This is a remoting receiver for a ccc spring client.
 * @author Adam Gibson
 *
 */
public interface RemotingReceiver extends Remote {
	/**
	 * This is an implementation by which it will receive 
	 * as string parameters passed to the object.
	 * @param params the parameters to pass in
	 * @return an object associated with the return if necessary
	 */
	public Object recieve(String...params) throws RemoteException;
}//end RemotingReceiver
