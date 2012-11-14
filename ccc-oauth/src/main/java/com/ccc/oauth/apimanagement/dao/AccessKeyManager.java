package com.ccc.oauth.apimanagement.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import com.ccc.oauth.apimanagement.model.AccessKeyForUser;
import com.ccc.oauth.apimanagement.model.Service;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("accessKeyManager")
public class AccessKeyManager extends GenericManager<AccessKeyForUser> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3317824955559343778L;

	public AccessKeyManager() {
		super(AccessKeyForUser.class);
	}
	
	
	public List<AccessKeyForUser> keysForServiceAndUser(Service service,String userName) {
		Session session=getHibernateTemplate().getSessionFactory().openSession();
		String hql="from AccessKeyForUser where user_name=:name and service_id=:serviceId";
		Query query=session.createQuery(hql).setParameter("name", userName).setParameter("serviceId", service.getId());
		List<AccessKeyForUser> list=query.list();
		if(list!=null && !list.isEmpty()) return list;
		return null;
	}
	
	
	public AccessKeyForUser mostRecentKeyForUserAndService(String userName,Service service) {
		List<AccessKeyForUser> keysForUser=keysForServiceAndUser(service,userName);
		if(keysForUser==null || keysForUser.isEmpty()) return null;
		AccessKeyForUser ret=null;
		for(AccessKeyForUser comp : keysForUser) {
			if(ret==null) ret=comp;
			else if(comp.getObtained().after(ret.getObtained()))
				ret=comp;
		}
		return ret;
	}
	public List<AccessKeyForUser> keysForUser(String userName) {
		return elementsWithValue("user_name",userName);
	}
	
}
