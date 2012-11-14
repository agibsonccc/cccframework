package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.OAuth1Info;
import com.ccc.oauth.apimanagement.model.Service;
@Repository("oauth1Manager")
public class OAuth1InfoManager extends GenericManager<OAuth1Info>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8507508881921337336L;

	public OAuth1InfoManager() {
		super(OAuth1Info.class);
	}
	
	public OAuth1Info infoForService(Service service) {
		Assert.notNull(service,"Service can't be nulL!");
		
		List<OAuth1Info> list= elementsWithValue("service_id", String.valueOf(service.getId()));
		return list!=null && !list.isEmpty() ? list.get(0) : null;
	}
}
