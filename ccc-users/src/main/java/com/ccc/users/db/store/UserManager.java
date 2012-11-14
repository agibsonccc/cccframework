package com.ccc.users.db.store;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;

import com.ccc.users.core.BasicUser;
import com.ccc.util.springhibernate.dao.GenericManager;
/**
 * This handles database interaction for the database user store.
 * @author Adam Gibson
 *
 */
@Repository("userManager")
public class UserManager extends GenericManager<BasicUser> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8753332369589870213L;
	public UserManager() {
		super(BasicUser.class);
	}
	/**
	 * This retrieves a list of the users with the given role
	 * @param role the role to retrieve a list of users for
	 * @return the list of users with the given role
	 */
	public List<BasicUser> usersWithRole(String role) {
		return super.elementsWithValue("role", role);
	}

	public BasicUser userWithName(String userName) {
		return (BasicUser) super.elementsWithValue("user_name", userName).get(0);
	}
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException, DataAccessException {
		return userWithName(username) ;
	}
	@Autowired
	private SessionFactory sessionFactory;

}//end UserManager
