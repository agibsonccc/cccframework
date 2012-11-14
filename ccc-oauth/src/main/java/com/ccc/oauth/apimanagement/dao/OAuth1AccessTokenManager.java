package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.OAuth1AccessToken;
import com.ccc.oauth.apimanagement.model.OAuth2KeyUser;
import com.ccc.oauth.apimanagement.model.Service;

@Repository("oauth1AccessTokenManager")
public class OAuth1AccessTokenManager extends GenericManager<OAuth1AccessToken>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3294997086369124574L;

	public OAuth1AccessTokenManager() {
		super(OAuth1AccessToken.class);
	}
	
	public List<OAuth1AccessToken> tokensForServiceAndUser(Service service,String userName) {
		Session session=getHibernateTemplate().getSessionFactory().openSession();
		
		String hql="from OAuth1AccessToken where user_name=:name and service_id=:serviceId";
		Query query=session.createQuery(hql).setParameter("name", userName).setParameter("serviceId", service.getId());
		List<OAuth1AccessToken> list=query.list();
		if(list!=null && !list.isEmpty()) return list;
		return list;
		
	}
	
	public OAuth1AccessToken mostRecentKeyForUserAndService(String userName,Service service) {
		List<OAuth1AccessToken> keysForUser=tokensForServiceAndUser(service,userName);
		if(keysForUser==null || keysForUser.isEmpty()) return null;
		OAuth1AccessToken ret=null;
		for(OAuth1AccessToken comp : keysForUser) {
			if(ret==null) ret=comp;
			else if(comp.getTimeReceived().after(ret.getTimeReceived()))
				ret=comp;
		}
		return ret;
		
	}
	
	
	
}
