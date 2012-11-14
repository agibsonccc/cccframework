package com.ccc.user.dao;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.Group;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("GroupManager")
public class GroupManager extends GenericManager<Group>{
	public GroupManager(){
		super(Group.class);
	}
}