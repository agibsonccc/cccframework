package com.ccc.security.entrypoints;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.Assert;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.client.UserClient;

public class RolebasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {
		String userName=SecurityContextHolder.getContext().getAuthentication().getName();
		BasicUser user=userClient.userForName(userName);
		
		if(user==null && !response.isCommitted()) 
			response.sendRedirect(defaultRedirect);
		
		Assert.notNull(user, "User didn't exist");
		Collection<GrantedAuthority> authorities=user.getAuthorities();
		if(authorities!=null) {
			for(GrantedAuthority authority : authorities) {
				String s=authority.getAuthority();
				String url=rolesToUrls.get(s);
				if(url!=null && !url.isEmpty()) {
					try {
						if(log.isDebugEnabled()) {
							log.debug("Sending redirect to: {} with role {}",url,s);
						}
						if(!response.isCommitted()){
							response.sendRedirect(url);
							return;
						}
					} catch (IOException e) {
						log.error("Error sending redirect",e);
					}
				}
				else continue;
			}
		}
		else {
			if(log.isDebugEnabled()) {
				log.debug("Unable to send redirect for user {} using default {}",userName,defaultRedirect);
				
			}
			try {
				if(defaultRedirect!=null && !defaultRedirect.isEmpty()) {
					if(!response.isCommitted()) 
						response.sendRedirect(defaultRedirect);
					

				}
				else {
					throw new IllegalStateException("Couldn't determine redirect");
				}
			} catch (IOException e) {
				e.printStackTrace();
				log.error("IO Exception when attempting to send role based redirect",e);
			}
		}		
	}
	
	public String getDefaultRedirect() {
		return defaultRedirect;
	}


	public void setDefaultRedirect(String defaultRedirect) {
		this.defaultRedirect = defaultRedirect;
	}


	public UserClient getUserClient() {
		return userClient;
	}


	public void setUserClient(UserClient userClient) {
		this.userClient = userClient;
	}

	public Map<String, String> getRolesToUrls() {
		return rolesToUrls;
	}


	public void setRolesToUrls(Map<String, String> rolesToUrls) {
		this.rolesToUrls = rolesToUrls;
	}
	
	private String defaultRedirect;
	private static Logger log=LoggerFactory.getLogger(RolebasedAuthenticationSuccessHandler.class);
	private Map<String,String> rolesToUrls;
	@Autowired
	private UserClient userClient;
	

}
