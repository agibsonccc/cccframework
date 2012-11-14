/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.ccc.users.core.BasicUser;
/**
 * This is a subclass of the dao authentication provider to automatically assume encryption.
 * {@link} DaoAuthenticationProvider 
 * @author Adam Gibson
 *
 */
public class MyDaoAuth extends DaoAuthenticationProvider implements java.io.Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2224091393570403032L;

	/**
	 * This overridden implementation of authenticate will automatically assume a salt source and a password encoder.
	 * If you run in to problems authenticating, I suggest resetting passwords to their real password and setting the needsEncryption flag to true 
	 * to allow for re encryption. If there are any other problems, double check your salt source and make sure the user properties you are using 
	 * are consistent with what the password encoder has.
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.notNull(authentication);
		String username= authentication==null ? "NULL_USER" : authentication.getName();

		if(log.isDebugEnabled()) {
			log.debug("Attempting authentication on: {}",username);
		}
		//User name not provided.
		if(username==null || username.isEmpty() || username.equals("NULL_USER"))
			throw new BadCredentialsException("Please provide a user name.");
		UserDetails user=getUserDetailsService().loadUserByUsername(authentication.getName());
		if(user==null)
			throw new BadCredentialsException("No user found");
		Object credentials=authentication.getCredentials();
		Collection<? extends GrantedAuthority> auths=authentication.getAuthorities();
		//user already has roles, allow through
		if(auths!=null) {
			boolean notAnon=true;
			for(GrantedAuthority auth : auths) {
				if(auth.getAuthority().toUpperCase().contains("ANON")) 
					notAnon=true;
			}
			if(notAnon) {
				authentication = createSuccessAuthentication(authentication.getName(), authentication, user);	
				return authentication;
			}
		}


		String password= (authentication.getCredentials()==null) ? "NO_PASSWORD" : (String) credentials;
		

		//Throws exceptions if either are null.
		Assert.notNull(getPasswordEncoder());
		Assert.notNull(getSaltSource());

		/******************IMPORTANT
		 * IF YOU'RE HAVING ISSUES WITH ENCODING, ENSURE YOUR SALT SOURCE IS CONSISTENT ACROSS ALL METHODS
		 */
		//No password provided.
		if(password.equals("NO_PASSWORD"))
			throw new BadCredentialsException("Please provide a password.");

		if(getPasswordEncoder().isPasswordValid(user.getPassword(), password, getSaltSource().getSalt(user))) {
			if(log.isDebugEnabled()) {
				log.debug("Attempting success authentication creation for {}",username);
			}
			BasicUser u=(BasicUser) user;
			if(!u.isEnabled())
				throw new BadCredentialsException("User is expired");
			authentication = createSuccessAuthentication(authentication.getName(), authentication, user);	

		}
		else throw new BadCredentialsException("Wrong password");
		return authentication;

	}//end authenticate

	@Override
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		BasicUser u=(BasicUser) user;
		if(authentication==null || user==null) {
			throw new IllegalStateException("No authentication found");
		}
		
		List<GrantedAuthority> auths=u.getAuthorities();
		if(auths==null) {
			auths = new ArrayList<GrantedAuthority>();
			auths.add(new GrantedAuthority() {

				@Override
				public String getAuthority() {
					return "user";
				}
				
			});
		}
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
				authentication.getCredentials(), u.getAuthorities());
		if(log.isDebugEnabled()) {
			log.debug("Created success authentication for: {}",principal);
		}

		return result;
	}

	public boolean isAuth(String username,String rawPass){
		UserDetails user=getUserDetailsService().loadUserByUsername(username);
		if(user==null)
			return false;




		return getPasswordEncoder().isPasswordValid(user.getPassword(), rawPass,getSaltSource().getSalt(user));
	}

	private static Logger log=LoggerFactory.getLogger(MyDaoAuth.class);
}//end MyDaoAuth
