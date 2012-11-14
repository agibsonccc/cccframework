package com.ccc.user.dao;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.UserGroup;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("userGroup")
public class UserGroupManager extends GenericManager<UserGroup> {

	public UserGroupManager() {
		super(UserGroup.class);

	}


}