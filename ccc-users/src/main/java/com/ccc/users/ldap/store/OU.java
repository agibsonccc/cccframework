package com.ccc.users.ldap.store;

import java.io.Serializable;
/**
 * This is a simple OU for serialzing names.
 * @author Adam Gibson
 *
 */
public class OU implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5202197353387132222L;

	public OU(String dn) {
		super();
		this.dn = dn;
	}

	public String getDn() {
		return dn;
	}

	public void setDn(String dn) {
		this.dn = dn;
	}

	private String dn;
}//end OU
