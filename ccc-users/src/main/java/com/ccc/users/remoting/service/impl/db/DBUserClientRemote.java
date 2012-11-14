package com.ccc.users.remoting.service.impl.db;

import java.rmi.RemoteException;

import com.ccc.users.db.store.DBUserStore;
import com.ccc.users.remoting.service.impl.BaseUserClientRemote;
/**
 * Database implementation of user client remote
 * 
 * @author Adam Gibson
 *
 */
public class DBUserClientRemote extends BaseUserClientRemote {

	@Override
	public boolean isAuth(String userName, String password) throws RemoteException {
		DBUserStore b=(DBUserStore) userStore;
		return b.isAuth(userName, password);
	}
}
