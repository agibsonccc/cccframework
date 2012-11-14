package com.ccc.users.filters.logout;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
/**
 * See: http://forum.springsource.org/showthread.php?29115-logout-event
 * 
 *
 */
public class LogoutEventBroadcaster extends SecurityContextLogoutHandler implements LogoutHandler,LogoutSuccessHandler,ApplicationContextAware {

	private Logger log = LoggerFactory.getLogger(LogoutEventBroadcaster.class);

	private ApplicationContext applicationContext;

	/**
	 * 
	 */
	public LogoutEventBroadcaster() {
		super();
	}

	/* (non-Javadoc)
	 * @see org.acegisecurity.ui.logout.LogoutHandler#logout(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, org.acegisecurity.Authentication)
	 */
	public void logout(HttpServletRequest arg0, HttpServletResponse arg1, Authentication auth) {
		super.logout(arg0, arg1, auth);
		LogoutEvent event = new LogoutEvent(auth);
		if(log.isDebugEnabled())
			log.debug("publishing logout event: " + event);
		applicationContext.publishEvent(event);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void onLogoutSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		LogoutEvent event = new LogoutEvent(authentication);
		if(log.isDebugEnabled())
			log.debug("publishing logout event: " + event);
		applicationContext.publishEvent(event);
		response.sendRedirect(successUrl);
	}
	
	public String getSuccessUrl() {
		return successUrl;
	}

	public void setSuccessUrl(String successUrl) {
		this.successUrl = successUrl;
	}

	private String successUrl="/";
}
