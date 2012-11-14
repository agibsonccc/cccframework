package com.ccc.remoting.httpinvoker;

import java.rmi.RemoteException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

/**
 * This is an rmi implementation of an {org.springframework.security.core.session.SessionRegistryImpl}
 * @author Adam Gibson
 *
 */
public class RemoteSessionRegistryImpl implements RemoteSessionRegistry {

	@Override
	public List<Object> getAllPrincipals() {
		return sessionRegistry.getAllPrincipals();
	}

	@Override
	public List<SessionInformation> getAllSessions(Object principal,
			boolean includeExpiredSessions) throws RemoteException {
		return sessionRegistry.getAllSessions(principal, includeExpiredSessions);
	}

	@Override
	public SessionInformation getSessionInformation(String sessionId)
			throws RemoteException {
		return sessionRegistry.getSessionInformation(sessionId);
	}

	@Override
	public void refreshLastRequest(String sessionId) throws RemoteException {
		sessionRegistry.refreshLastRequest(sessionId);
	}

	@Override
	public void registerNewSession(String sessionId, Object principal)
			throws RemoteException {
		sessionRegistry.registerNewSession(sessionId, principal);
		
	}

	@Override
	public void removeSessionInformation(String sessionId)
			throws RemoteException {
		sessionRegistry.removeSessionInformation(sessionId);
	}
	@Autowired
	private SessionRegistry sessionRegistry;

}
