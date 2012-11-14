package com.ccc.user.dao;

import java.rmi.RemoteException;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.RegistrationTracker;
import com.ccc.users.core.client.UserClient;
import com.ccc.users.remoting.service.UserClientRemote;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("registrationManager")
public class RegistrationManager extends GenericManager<RegistrationTracker> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7344560518379109891L;

	public RegistrationManager() {
		super(RegistrationTracker.class);
	}

	public void registerUser(UserClientRemote client,BasicUser user,RegistrationTracker tracker) {
		user.setEnabled(true);
		try {
			client.modifyUser(user);
		} catch (RemoteException e) {
			
		}
		tracker.setRegistered(true);
		updateE(tracker);
	}
	public void registerUser(UserClient client,BasicUser user,RegistrationTracker tracker) {
		user.setEnabled(true);
		client.modifyUser(user);
		tracker.setRegistered(true);
		updateE(tracker);
	}

}//end RegistrationManager



