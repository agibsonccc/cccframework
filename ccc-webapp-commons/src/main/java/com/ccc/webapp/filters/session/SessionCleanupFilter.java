package com.ccc.webapp.filters.session;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
/**
 * Filter for cleaning up sessions and ensuring anonymous sessions 
 * get cleaned up 
 * @author Adam Gibson
 *
 */
public class SessionCleanupFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest)request;

		// The false is important, otherwise a new session will be created.
		HttpSession session = httpRequest.getSession(false);

		if (session == null) {
			chain.doFilter(request, response);
			return;
		}

		session.setMaxInactiveInterval(30 * 60);
		chain.doFilter(request, response);		
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
