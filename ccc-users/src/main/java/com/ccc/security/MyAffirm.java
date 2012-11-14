/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.security;
import java.util.Collection;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.core.Authentication;
public class MyAffirm extends AffirmativeBased {
	@Override
	public void decide(Authentication authentication,Object object, Collection<ConfigAttribute> configAttributes)  {
	for(AccessDecisionVoter a : getDecisionVoters()){
		System.out.println("A decided: " + a.vote(authentication, object, configAttributes));
		
	}
	super.decide(authentication, object, configAttributes);
}
}
