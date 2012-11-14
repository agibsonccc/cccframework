/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not possess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.security;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.SpringSecurityMessageSource;

public class CustomAuthenticationManager extends ProviderManager  {
	private Authentication testAuth;
	private AuthenticationEventPublisher eventPublisher = new NullEventPublisher();
	private List<AuthenticationProvider> providers = Collections.emptyList();
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private AuthenticationManager parent;
	private boolean eraseCredentialsAfterAuthentication = false;


	   
	public Authentication  doAuthentication(Authentication authentication) throws AuthenticationException {
		Class<? extends Authentication> toTest = authentication.getClass();
		AuthenticationException lastException = null;
		Authentication result = null;
		super.setEraseCredentialsAfterAuthentication(eraseCredentialsAfterAuthentication);

		for (AuthenticationProvider provider : getProviders()) {
			if (!provider.supports(toTest)) {
				continue;
			}

			
			
			try {
				
				try {result = provider.authenticate(authentication);}
				catch(NullPointerException e) {
					
				}

				if (result != null) {
					
					copyDetails(authentication, result);
					break;
				}
			} catch (AccountStatusException e) {
				// SEC-546: Avoid polling additional providers if auth failure is due to invalid account status
				eventPublisher.publishAuthenticationFailure(e, authentication);
				throw e;
			} 
			
		}

		if (result == null && parent != null) {
			// Allow the parent to try.
			try {
				result = parent.authenticate(authentication);
				//mailboxmanager.login(result.getName(), (String)result.getCredentials(),logProvider.getLog("userstore"));
			
			} catch (ProviderNotFoundException e) {
				// ignore as we will throw below if no other exception occurred prior to calling parent and the parent
				// may throw ProviderNotFound even though a provider in the child already handled the request
			} catch (AuthenticationException e) {
				lastException = e;
			} 
		
		}

		if (result != null) {
			if (eraseCredentialsAfterAuthentication && (result instanceof CredentialsContainer)) {
				// Authentication is complete. Remove credentials and other secret data from authentication
				((CredentialsContainer)result).eraseCredentials();
				System.out.println("Remove credentials.");
			}
			eventPublisher.publishAuthenticationSuccess(result);
			return result;
		}

		// Parent was null, or didn't authenticate (or throw an exception).

		if (lastException == null) {
			lastException = new ProviderNotFoundException(messages.getMessage("ProviderManager.providerNotFound",
					new Object[] {toTest.getName()}, "No AuthenticationProvider found for {0}"));
		}

		eventPublisher.publishAuthenticationFailure(lastException, authentication);

		throw lastException;
	}

	/**
	 * Copies the authentication details from a source Authentication object to a destination one, provided the
	 * latter does not already have one set.
	 *
	 * @param source source authentication
	 * @param dest the destination authentication object
	 */
	private void copyDetails(Authentication source, Authentication dest) {
		if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
			AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

			token.setDetails(source.getDetails());
		}
	}
	
	private static final class NullEventPublisher implements AuthenticationEventPublisher {
		public void publishAuthenticationFailure(AuthenticationException exception, Authentication authentication) {}
		public void publishAuthenticationSuccess(Authentication authentication) {}
	}
}
