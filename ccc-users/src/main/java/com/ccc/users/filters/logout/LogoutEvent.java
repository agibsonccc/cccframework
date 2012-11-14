package com.ccc.users.filters.logout;

import org.springframework.security.access.event.AbstractAuthorizationEvent;
import org.springframework.security.core.Authentication;
/**
 * See: http://forum.springsource.org/showthread.php?29115-logout-event
 * 
 *
 */
public class LogoutEvent extends AbstractAuthorizationEvent {

    //~ Constructors ===================================================================================================

    /**
	 * 
	 */
	private static final long serialVersionUID = 6496988399122052868L;

	public LogoutEvent(Authentication authentication) {
        super(authentication);
    }
}