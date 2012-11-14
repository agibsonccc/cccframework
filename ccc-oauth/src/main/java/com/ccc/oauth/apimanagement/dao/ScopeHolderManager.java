package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.ScopeHolder;

@Repository("scopeHolderManager")
public class ScopeHolderManager extends GenericManager<ScopeHolder> {

	
	/**
	 * This will return the scopes for a given user
	 * @param userName the user name to load
	 * @return the scopes for a given user
	 */
	public List<ScopeHolder> scopesForUser(String userName) {
		return super.elementsWithValue("user_name", userName);
	}//end scopesForUser
	/**
	 * 
	 */
	private static final long serialVersionUID = 4301103012565379759L;

	public ScopeHolderManager() {
		super(ScopeHolder.class);
	}
}
