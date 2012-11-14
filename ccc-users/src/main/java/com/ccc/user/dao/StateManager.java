package com.ccc.user.dao;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.ccc.users.core.State;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("stateManager")
@Component
public class StateManager extends GenericManager<State> {

	public StateManager() {
		super(State.class);
	}
	public List<State> allStates(){
		return getHibernateTemplate().find("from State");
		
	}
	public List<State> stateByCountry(String country){
		return getHibernateTemplate().find("from State where country_id='" + country + "'");
	}
	public State stateByName(String name){
		return (State) getHibernateTemplate().find("from State where state_id='" + name + "'").get(0);
	}
	
}
