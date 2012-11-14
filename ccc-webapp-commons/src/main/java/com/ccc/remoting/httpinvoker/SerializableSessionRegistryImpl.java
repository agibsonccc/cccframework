package com.ccc.remoting.httpinvoker;

import org.springframework.security.core.session.SessionRegistryImpl;
import java.io.*;

public class SerializableSessionRegistryImpl extends SessionRegistryImpl implements Serializable {
	private static final long serialVersionUID = 4946491171588478299L;

	@Override
	public void removeSessionInformation(String sessionId) {
		if(sessionId==null)
			return;
		
		super.removeSessionInformation(sessionId);
	}

}
