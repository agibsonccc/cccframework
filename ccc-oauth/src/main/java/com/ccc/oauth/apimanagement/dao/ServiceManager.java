package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.Service;

@Repository("serviceManager")
public class ServiceManager extends GenericManager<Service>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2954108891248523100L;

	public ServiceManager() {
		super(Service.class);
	}
}
