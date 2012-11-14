package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.OAuth1AccessToken;
import com.ccc.oauth.apimanagement.model.OAuth1RequestToken;
import com.ccc.oauth.apimanagement.model.Service;
@Repository("oauth1RequestTokenManager")
public class OAuth1RequestTokenManager extends GenericManager<OAuth1RequestToken>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3019406320951566158L;

	public OAuth1RequestTokenManager() {
		super(OAuth1RequestToken.class);
	}
	
	public List<OAuth1RequestToken> tokensForServiceAndUser(Service service,String userName) {
		Session session=getHibernateTemplate().getSessionFactory().openSession();
		
		String hql="from OAuth1RequestToken where user_name=:name and service_id=:serviceId";
		Query query=session.createQuery(hql).setParameter("name", userName).setParameter("serviceId", service.getId());
		List<OAuth1RequestToken> list=query.list();
		if(list!=null && !list.isEmpty()) return list;
		return list;
		
	}
	
	public OAuth1RequestToken mostRecentKeyForUserAndService(String userName,Service service) {
		List<OAuth1RequestToken> keysForUser=tokensForServiceAndUser(service,userName);
		if(keysForUser==null || keysForUser.isEmpty()) return null;
		OAuth1RequestToken ret=null;
		for(OAuth1RequestToken comp : keysForUser) {
			if(ret==null) ret=comp;
			else if(comp.getTimeReceived().after(ret.getTimeReceived()))
				ret=comp;
		}
		return ret;
		
	}
	
}
