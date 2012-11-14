package com.ccc.security.filters.logout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import com.ccc.webapp.event.api.WebappEvent;
import com.ccc.webapp.event.constants.WebappConstants;
import com.ccc.webapp.event.logout.LogoutEventListener;
import com.ccc.webapp.event.logout.LogoutWebappEvent;

/**
 * This is an extension of the log out filter that wires in a log out event handler.
 * @author Adam Gibson
 *
 */
public class LogoutEventSessionHandler extends LogoutFilter implements WebappConstants {

	public LogoutEventSessionHandler(LogoutSuccessHandler logoutSuccessHandler,
			LogoutHandler[] handlers) {
		super(logoutSuccessHandler, handlers);
	
	}

	public LogoutEventSessionHandler(String logoutSuccessUrl,
			LogoutHandler... handlers) {
		super(logoutSuccessUrl, handlers);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) {
		try {
			super.doFilter(req, res, chain);
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (ServletException e1) {
			e1.printStackTrace();
		}
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();
		WebappEvent event = new LogoutWebappEvent(userName);
		event.registerParam(USER_NAME_KEY, userName);
		try {
			listener.fireEvent(event);
			for(LogoutEventListener list2 : listeners)
				list2.fireEvent(event);
			
		} catch (Exception e) {
			log.error("Error firing listener",e);
		}
	}
	
	
	public LogoutEventListener getListener() {
		return listener;
	}

	public void setListener(LogoutEventListener listener) {
		this.listener = listener;
	}

	public void addListener(LogoutEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(LogoutEventListener listener) {
		listeners.remove(listener);
	}
	
	private static Logger log=LoggerFactory.getLogger(LogoutEventSessionHandler.class);
	private List<LogoutEventListener> listeners = new ArrayList<LogoutEventListener>();
	@Autowired
	private LogoutEventListener listener;
}//end LogoutEventSessionHandler
