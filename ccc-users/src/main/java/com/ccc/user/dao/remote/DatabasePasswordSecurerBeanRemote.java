package com.ccc.user.dao.remote;

import java.rmi.RemoteException;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.client.UserClient;
import com.ccc.users.remoting.service.UserClientRemote;
@Component("databasePasswordSecuruerBeanRemote")
public class DatabasePasswordSecurerBeanRemote {
	@Autowired(required=false)
	private PasswordEncoder passwordEncoder;
	@Resource(name="userService")
	private UserDetailsService userDetailsService;
	@Autowired(required=false)
	private SaltSource saltSource;
	
	@Autowired(required=false)
	private UserClientRemote userClient;
	private static boolean doneFirstTime=false;
	/* Toggle this to encrypt the database each time on startup. */
	private boolean needsEncryption;
	
	
	private static Logger log=LoggerFactory.getLogger(DatabasePasswordSecurerBeanRemote.class);
	/**
	 * This will re encrypt all of the passwords in the database.
	 */
	public void secureDatabase() {
		List<BasicUser> users=null;
		try {
			users = userClient.retrieveUsers();
		} catch (RemoteException e1) {
			log.error("Remote error retrieving users: {}",e1);
		}
		if(needsEncryption && !doneFirstTime){
			for(BasicUser u : users){
				String password=u.getPassword();
				String encryptedPassword=passwordEncoder.encodePassword(password, getSaltSource());
				doneFirstTime=true;

				u.setPassword(encryptedPassword);
				try {
					userClient.modifyUser(u);
				} catch (RemoteException e) {
					log.error("Remote error updating user: {}",u.getUsername());
				}
			}
		}

	}//end secureDatabase

	/**
	 * This will encrypt the password for the given user
	 * @param userName the name of the user to encrypt for
	 */
	public void encryptPasswordFor(String userName) {
		BasicUser u;
		try {
			u = userClient.userForName(userName);
		} catch (RemoteException e) {
			u=null;
			log.error("Error retrieving remote user: {}",userName,e);
		}
		doneFirstTime=false;

		if(needsEncryption && !doneFirstTime){
			String password=u.getPassword();
			String encryptedPassword=passwordEncoder.encodePassword(password, getSaltSource().getSalt(u));
			doneFirstTime=true;

			u.setPassword(encryptedPassword);
			try {
				userClient.modifyUser(u);
			} catch (RemoteException e) {
				log.error("Error updating remote user: {}",userName,e);

			}

		}

	}//end encryptPasswordFor

	/**
	 * 
	 * @return the passwordEncoder
	 */
	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}
	/**
	 * @param passwordEncoder the passwordEncoder to set
	 */
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	/**
	 * @return the userDetailsService
	 */
	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}
	/**
	 * @param userDetailsService the userDetailsService to set
	 */
	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	/**
	 * @return the saltSource
	 */
	public SaltSource getSaltSource() {
		return saltSource;
	}
	/**
	 * @param saltSource the saltSource to set
	 */
	public void setSaltSource(SaltSource saltSource) {
		this.saltSource = saltSource;
	}

	public void setNeedsEncryption(boolean needsEncryption) {
		this.needsEncryption = needsEncryption;
	}
	public boolean isNeedsEncryption() {
		return needsEncryption;
	}
}
