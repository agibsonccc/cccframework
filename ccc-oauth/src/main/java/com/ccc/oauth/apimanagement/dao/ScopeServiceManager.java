package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.ScopeService;
import com.ccc.oauth.apimanagement.model.Service;

@Repository("scopeServiceManager")
public class ScopeServiceManager extends GenericManager<ScopeService> {
	/**
	 * This will return all of the scopes for a given service
	 * @param service the service to get the scopes for
	 * @return the list of scopes for a given service, or null
	 * if none exist
	 */
	public List<ScopeService> scopesForService(Service service) {
		return super.elementsWithValue("service_id", String.valueOf(service.getId()));
		
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1194183110519602977L;

	public ScopeServiceManager()  {
		super(ScopeService.class);

	}
	
}//end ScopeServiceManager
