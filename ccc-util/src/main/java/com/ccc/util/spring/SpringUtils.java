package com.ccc.util.spring;

//import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
/**
 * This is a util class for various spring related operations
 * @author Adam Gibson
 *
 */
public class SpringUtils {
	/**
	 * This will return the current session for spring security
	 * @return the current session for spring security
	 
	public static HttpSession getCurrentSession() {
		 ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		    return attr.getRequest().getSession(true); // true == allow create
	}*/
	/**
	 * This will return the current user name from the security context
	 * @return the current username of the user logged in or null
	 * if a context doesn't exist
	 */
	public static String getCurrentUserName() {
		SecurityContext context=SecurityContextHolder.getContext();
		if(context!=null) {
			Authentication auth=context.getAuthentication();
			if(auth!=null) {
				return auth.getName();
			}
		}
		return null;
	}//end getCurrentUserName
	
	public final static String LAST_USER_IN_SESSION="SPRING_SECURITY_LAST_USERNAME";
	
	public final static String ANONYMOUS_USER="anonymousUser";

	
}
