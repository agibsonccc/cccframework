package com.ccc.users.ldap.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;

import com.ccc.users.core.client.BaseUserClient;
import com.ccc.users.core.store.UserStore;
import com.ccc.users.ldap.store.LDAPConstants;
import com.ccc.users.ldap.store.LDAPUserStore;
/**
 * This is a user client for an ldap store.
 * If you are using spring, this client is already annotated properly for a set up 
 * such that you just create an ldap template, and this class handles the rest.
 * @author Adam Gibson
 *
 */
public class LDAPUserClient extends BaseUserClient {
	/**
	 * BaseUserClient implements Serializable
	 */
	private static final long serialVersionUID = -8228910796181735164L;
	/**
	 * This client must be initialized with an ldap user store.
	 * @param store the user store to use
	 * @throws IllegalArgumentException if store is null or not of type ldap.
	 */
	public LDAPUserClient(UserStore store) throws IllegalArgumentException {
		super(store);

		if(!(store instanceof LDAPUserStore))
			throw new IllegalArgumentException("Wrong type of user store: must be of type ldap store.");
	}
	public LDAPUserClient() {}

	public LdapTemplate getTemplate() {
		return template;
	}

	public void setTemplate(LdapTemplate template) {
		this.template = template;
	}

	
	
	
	@Override
	public boolean isAuth(String userName, String password) {
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
	
	

}//end LDAPUserClient
