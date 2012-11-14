package com.ccc.user.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ccc.users.core.Country;
import com.ccc.util.springhibernate.dao.GenericManager;


@Repository("countryManager")
@Component
public class CountryManager extends GenericManager<Country>{

	public CountryManager() {
		super(Country.class);
	}
	
	@SuppressWarnings("unchecked")
	public List<Country> allCountries(){
		return getHibernateTemplate().find("from Country");
	}
	public Country countryById(String id){
		return (Country) getHibernateTemplate().find("from Country where country_id='" + id + "'").get(0);
	}
	
	public boolean saveCountry(Country toSave){
		try {
			getHibernateTemplate().save(toSave);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	public boolean deleteCountry(Country toDelete){
		try {
			getHibernateTemplate().delete(toDelete);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	
	public boolean updateCountry(Country toUpdate){
		try {
			getHibernateTemplate().update(toUpdate);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}
	

	
}
