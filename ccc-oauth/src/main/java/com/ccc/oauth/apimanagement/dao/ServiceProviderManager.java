package com.ccc.oauth.apimanagement.dao;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.ServiceProvider;

@Repository("serviceProviderManager")
public class ServiceProviderManager extends GenericManager<ServiceProvider>{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6554105519653577293L;

	public ServiceProviderManager() {
		super(ServiceProvider.class);
	}
	
}
