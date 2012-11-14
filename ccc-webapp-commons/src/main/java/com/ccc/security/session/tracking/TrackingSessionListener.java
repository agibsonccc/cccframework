package com.ccc.security.session.tracking;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.springframework.beans.factory.annotation.Autowired;



public class TrackingSessionListener implements HttpSessionListener {
 

    public void sessionCreated(HttpSessionEvent arg0) {
    }
 
    public void sessionDestroyed(HttpSessionEvent ev) {
    
    	sessionTracker.decrement();
    }

    
    
    public SessionTracker getSessionTracker() {
		return sessionTracker;
	}

	public void setSessionTracker(SessionTracker sessionTracker) {
		this.sessionTracker = sessionTracker;
	}

	@Autowired
    private SessionTracker sessionTracker;
    
}