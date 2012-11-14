/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.security;

import java.util.Collection;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.core.Authentication;

public class MyAuthVoter extends AuthenticatedVoter {
	public MyAuthVoter(){
		System.out.println("My voter on.");
	}
	@Override
	public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		System.out.println("Voted: " + authentication.toString());
		return super.vote(authentication, object, attributes);
	}
}
