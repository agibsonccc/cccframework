package com.ccc.users.auth;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.AbstractAuthenticationToken;

	public class MyUsernamePasswordAuthenticationToken extends AbstractAuthenticationToken {

		private static final long serialVersionUID = -9149653223029288107L;
		private final Object principal;
	    private Object credentials;

	    public MyUsernamePasswordAuthenticationToken(Object principal, Object credentials) {
	        super(null);
	        this.principal = principal;
	        this.credentials = credentials;
	        setAuthenticated(false);
	    }

	    public MyUsernamePasswordAuthenticationToken(Object principal, Object credentials, GrantedAuthority[] authorities) {
	        this(principal, credentials, Arrays.asList(authorities));
	    }

	    public MyUsernamePasswordAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
	        super(authorities);
	        this.principal = principal;
	        this.credentials = credentials;
	        super.setAuthenticated(true); // must use super, as we override
	    }

	    public Object getCredentials() {
	        return this.credentials;
	    }

	    public Object getPrincipal() {
	        return this.principal;
	    }

	    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
	        super.setAuthenticated(isAuthenticated);
	    }

	    @Override
	    public void eraseCredentials() {
	        super.eraseCredentials();
	        credentials = null;
	    }
	}
