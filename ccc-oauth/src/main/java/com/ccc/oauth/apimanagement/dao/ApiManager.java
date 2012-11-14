package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.ApiHolder;
@Repository("apiManager")
public class ApiManager extends GenericManager<ApiHolder> {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 359670403251338548L;

	public ApiManager() {
		super(ApiHolder.class);
	}
}
