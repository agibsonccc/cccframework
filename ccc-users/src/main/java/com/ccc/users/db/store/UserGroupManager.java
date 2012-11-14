package com.ccc.users.db.store;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.Group;
import com.ccc.users.core.UserGroup;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("userGroupManager")
public class UserGroupManager extends GenericManager<UserGroup> implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7009706582503806914L;

	public UserGroupManager(){
		super(UserGroup.class);
	}
	
	public List<UserGroup> groupsForUser(String userName) {
		return super.elementsWithValue("user_name", userName);
	}
}
