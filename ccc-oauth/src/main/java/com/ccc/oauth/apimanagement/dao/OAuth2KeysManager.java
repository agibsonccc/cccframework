package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.ccc.util.springhibernate.dao.GenericManager;
import com.ccc.oauth.apimanagement.model.OAuth2KeyUser;
import com.ccc.oauth.apimanagement.model.Service;

@Repository("oath2KeysManager")
public class OAuth2KeysManager extends GenericManager<OAuth2KeyUser>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1131991873865576232L;

	public OAuth2KeysManager() {
		super(OAuth2KeyUser.class);
	}

	public List<OAuth2KeyUser> keysForUser(String userName) {
		return super.elementsWithValue("user_name", userName);
	}
	
	public List<OAuth2KeyUser> keysForServiceAndUser(Service service,String userName) {
		Session session=getHibernateTemplate().getSessionFactory().openSession();
		String hql="from OAuth2KeyUser where user_name=:name and service_id=:serviceId";
		Query query=session.createQuery(hql).setParameter("name", userName).setParameter("serviceId", service.getId());
		List<OAuth2KeyUser> list=query.list();
		if(list!=null && !list.isEmpty()) return list;
		return null;
	}
	
	
	public OAuth2KeyUser mostRecentKeyForUserAndService(String userName,Service service) {
		List<OAuth2KeyUser> keysForUser=keysForServiceAndUser(service,userName);
		if(keysForUser==null || keysForUser.isEmpty()) return null;
		OAuth2KeyUser ret=null;
		for(OAuth2KeyUser comp : keysForUser) {
			if(ret==null) ret=comp;
			else if(comp.getTimeMade().after(ret.getTimeMade()))
				ret=comp;
		}
		return ret;
		
	}
	
	
	public OAuth2KeyUser mostRecentKey(String userName) {
		List<OAuth2KeyUser> keysForUser=keysForUser(userName);
		if(keysForUser==null || keysForUser.isEmpty()) return null;
		OAuth2KeyUser ret=null;
		for(OAuth2KeyUser comp : keysForUser) {
			if(ret==null) ret=comp;
			else if(comp.getTimeMade().after(ret.getTimeMade()))
				ret=comp;
		}
		return ret;
		
	}
}
