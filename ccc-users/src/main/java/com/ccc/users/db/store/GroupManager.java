package com.ccc.users.db.store;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.Group;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("groupManager")
public class GroupManager extends GenericManager<Group> implements java.io.Serializable{

	private static final long serialVersionUID = 5515839409474031570L;
	
	public GroupManager(){
		super(Group.class);
	}
	
}
