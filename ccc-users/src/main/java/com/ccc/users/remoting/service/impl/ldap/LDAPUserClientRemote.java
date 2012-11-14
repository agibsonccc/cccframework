package com.ccc.users.remoting.service.impl.ldap;

import java.rmi.RemoteException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.ccc.users.core.store.UserStore;
import com.ccc.users.ldap.store.LDAPConstants;
import com.ccc.users.ldap.store.LDAPUserStore;
import com.ccc.users.remoting.service.impl.BaseUserClientRemote;
/**
 * LDAP implementation of an rmi user client
 * @author Adam Gibson
 *
 */
public class LDAPUserClientRemote extends BaseUserClientRemote {

	
	
	/**
	 * This client must be initialized with an ldap user store.
	 * @param store the user store to use
	 * @throws IllegalArgumentException if store is null or not of type ldap.
	 */
	public LDAPUserClientRemote(UserStore store) throws IllegalArgumentException {
		super(store);

		if(!(store instanceof LDAPUserStore))
			throw new IllegalArgumentException("Wrong type of user store: must be of type ldap store.");
	}
	public LDAPUserClientRemote() {}

	public LdapTemplate getTemplate() {
		return template;
	}

	public void setTemplate(LdapTemplate template) {
		this.template = template;
	}

	
	
	
	@Override
	public boolean isAuth(String userName, String password)throws RemoteException {
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass",userType)).and(new EqualsFilter("cn",userName));
		DistinguishedName dn = new DistinguishedName(userDirectory + "," + baseDn);
		boolean authenticated = template.authenticate(dn, filter.toString(), password);
		return authenticated;
	}

	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getBaseDn() {
		return baseDn;
	}
	public void setBaseDn(String baseDn) {
		this.baseDn = baseDn;
	}
	public String getUserDirectory() {
		return userDirectory;
	}
	public void setUserDirectory(String userDirectory) {
		this.userDirectory = userDirectory;
	}

	private String userType=LDAPConstants.USER_TYPE;

	private String baseDn;

	private String userDirectory;
	@Autowired(required=false)
	private LdapTemplate template;
	
	
}
