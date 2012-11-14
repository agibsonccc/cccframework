package com.ccc.users.auth;

import java.io.*;

/**
 * This handles the authentication for users.
 * @author Adam Gibson
 *
 */
public interface Authenticator extends Serializable{
	/**
	 * This authenticates a given user.
	 * @param userName the user name to authenticate for
	 * @param password the password to authenticate with
	 * @return true if the user was authenticated, false otherwise
	 */
	public boolean isAuth(String userName,String password);
}//end Authenticator
