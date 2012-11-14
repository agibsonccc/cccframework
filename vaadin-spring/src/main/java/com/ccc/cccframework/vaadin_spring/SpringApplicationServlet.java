/*
 * Copyright 2011 Nicolas Frankel
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ccc.cccframework.vaadin_spring;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.ccc.util.spring.SpringUtils;
import com.vaadin.Application;
import com.vaadin.terminal.gwt.server.AbstractApplicationServlet;
import com.vaadin.terminal.gwt.server.SessionExpiredException;

/**
 * Vaadin application servlet that knows how to connect to a Spring's
 * application context and to retrieve the application bean from it.
 * 
 * <p>
 * The servlet has to be configured in the web.xml, as well as the application
 * bean name (defaults to "application"):
 * 
 * <pre>
 * &lt;servlet&gt;
 *   &lt;servlet-name&gt;Spring Integration&lt;/servlet-name&gt;
 *   &lt;servlet-class&gt;com.packtpub.vaadin.SpringApplicationServlet&lt;/servlet-class&gt;
 *   &lt;init-param&gt;
 *     &lt;param-name&gt;applicationBeanName&lt;/param-name&gt;
 *     &lt;param-value&gt;app&lt;/param-value&gt;
 *   &lt;/init-param&gt;
 * &lt;/servlet&gt;
 * 
 * @author Nicolas Fränkel
 * @since 1.0
 */
@SuppressWarnings("serial")
public class SpringApplicationServlet extends AbstractApplicationServlet {

	/** Default application bean name in Spring application context. */
	private static final String DEFAULT_APP_BEAN_NAME = "application";

	/** Application bean name in Spring application context. */
	private String name;

	/**
	 * Get and stores in the servlet the application bean's name in the Spring's
	 * context. It's expected to be configured as a the servlet
	 * &lt;code&gt;init-param&lt;/code&gt; named applicationBeanName. If no
	 * param is found, the default is "application".
	 * 
	 * @see AbstractApplicationServlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {

		super.init(config);

		String name = config.getInitParameter("applicationBeanName");

		this.name = name == null ? DEFAULT_APP_BEAN_NAME : name;
	}

	/**
	 * Get the application bean in Spring's context.
	 * 
	 * @see AbstractApplicationServlet#getNewApplication(HttpServletRequest)
	 */
	@Override
	protected Application getNewApplication(HttpServletRequest request) throws ServletException {

		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		
		if (wac == null) {

			throw new ServletException("Cannot get an handle on Spring's context. Is Spring running?"
					+ "Check there's an org.springframework.web.context.ContextLoaderListener configured.");
		}

		Object bean = wac.getBean(name);

		if (!(bean instanceof Application)) {

			throw new ServletException("Bean " + name + " is not of expected class Application");
		}

		return (Application) bean;
	}

	/**
	 * Get the application class from the bean configured in Spring's context.
	 * 
	 * @see AbstractApplicationServlet#getApplicationClass()
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Class<? extends Application> getApplicationClass() throws ClassNotFoundException {

		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());

		if (wac == null) {

			throw new ClassNotFoundException("Cannot get an handle on Spring's context. Is Spring running? "
					+ "Check there's an org.springframework.web.context.ContextLoaderListener configured.");
		}

		Object bean = wac.getBean(name);

		if (bean == null) {

			throw new ClassNotFoundException("No application bean found under name " + name);
		}

		return (Class) bean.getClass();
	}


	/**
	 * 
	 * Gets the application context from an HttpSession. If no context is
	 * currently stored in a session a new context is created and stored in the
	 * session.
	 * 
	 * @param session
	 *            the HTTP session.
	 * @return the application context for HttpSession.
	 */
	protected com.vaadin.terminal.gwt.server.WebApplicationContext getApplicationContext(HttpSession session) {
		/*
		 * TODO the ApplicationContext.getApplicationContext() should be removed
		 * and logic moved here. Now overriding context type is possible, but
		 * the whole creation logic should be here. MT 1101
		 */
		return com.vaadin.terminal.gwt.server.WebApplicationContext.getApplicationContext(session);
	}



	/**
	 * Gets the existing application for given request. Looks for application
	 * instance for given request based on the requested URL.
	 * 
	 * @param request
	 *            the HTTP request.
	 * @param allowSessionCreation
	 *            true if a session should be created if no session exists,
	 *            false if no session should be created
	 * @return Application instance, or null if the URL does not map to valid
	 *         application.
	 * @throws MalformedURLException
	 *             if the application is denied access to the persistent data
	 *             store represented by the given URL.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SessionExpiredException
	 */
	@Override
	protected Application getExistingApplication(HttpServletRequest request,
			boolean allowSessionCreation) throws MalformedURLException,
			SessionExpiredException {

		String userName=SpringUtils.getCurrentUserName();
		if(userName!=null && !userName.equals(SpringUtils.ANONYMOUS_USER)) {
			// Ensures that the session is still valid
			final HttpSession session = request.getSession(allowSessionCreation);
			if(session!=null) {
				Object lastUser=session.getAttribute(SpringUtils.LAST_USER_IN_SESSION);
				if(lastUser!=null) {
					String lastUserName=lastUser.toString();
					if(lastUserName.equals(userName)) {
						 //String sessId = (String) request.getSession().getAttribute(ApplicationConnection.UIDL_SECURITY_TOKEN_ID);
						 //if(sessId==null) {
							// session.setAttribute(ApplicationConnection.UIDL_SECURITY_TOKEN_ID, "");
						 //}
						
						return findApplication(request,session);

					}
				}
			}
		}

		return null;

	}

	private Application findApplication(HttpServletRequest request,HttpSession session) throws MalformedURLException {
		com.vaadin.terminal.gwt.server.WebApplicationContext appContext = getApplicationContext(session);
		String currentUserName=SpringUtils.getCurrentUserName();
		String userName=(String) session.getAttribute(SpringUtils.LAST_USER_IN_SESSION);
		//force creation of new application, no anonymous applications are retained
		if(userName==null && currentUserName==null || userName.equals(SpringUtils.ANONYMOUS_USER) || currentUserName.equals(SpringUtils.ANONYMOUS_USER))
			return null;
		Application app=usersApps.get(userName==null ? currentUserName : userName);
		if(app!=null)
			return app;
		else return null;
		
		/*
		// Gets application list for the session.
		final Collection<Application> applications = appContext.getApplications();

		// Search for the application (using the application URI) from the list
		for (final Iterator<Application> i = applications.iterator(); i
				.hasNext();) {
			final Application sessionApplication = i.next();
			URL appUrl=sessionApplication.getURL();
			final String sessionApplicationPath = appUrl!=null ? appUrl.getPath() : "";
			String requestApplicationPath = getApplicationUrl(request)
					.getPath();

			if (requestApplicationPath.equals(sessionApplicationPath)) {
				// Found a running application
				if (sessionApplication.isRunning()) {
					return sessionApplication;
				}
				// Application has stopped, so remove it before creating a new
				// application
				 com.vaadin.terminal.gwt.server.WebApplicationContext context=  com.vaadin.terminal.gwt.server.WebApplicationContext.getApplicationContext(session);
				 context.getApplications().remove(sessionApplication);
				break;
			}
		}

		// Existing application not found
		return null;
		*/
	}

	private static Map<String,Application> usersApps = new HashMap<String,Application>();
}
