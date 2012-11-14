package com.ccc.users.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ccc.user.dao.CountryManager;
import com.ccc.user.dao.StateManager;
import com.ccc.users.core.State;



@Service("stateService")
public class StateService implements java.io.Serializable{

	 /**
	 * 
	 */
	private static final long serialVersionUID = -8511355282224635077L;
	public void addState(String country,String state){
		 Assert.notNull(country);
		 Assert.notNull(state);
		 Assert.hasLength(country);
		 Assert.hasLength(state);
		 State s = new State();
		 s.setCountryId(country);
		 s.setStateId(state);
	 }
	 public List<String> stateByCountry(String country){
		 List<String> ret = new ArrayList<String>();
		 List<State> states=stateManager.stateByCountry(country);
		 for(State s : states)
			 ret.add(s.getStateId());
		 return ret;
	 }
	 
	public StateManager getStateManager() {
		return stateManager;
	}
	public void setStateManager(StateManager stateManager) {
		this.stateManager = stateManager;
	}
	public CountryManager getCountryManager() {
		return countryManager;
	}
	public void setCountryManager(CountryManager countryManager) {
		this.countryManager = countryManager;
	}

	@Autowired(required=false)
	 private StateManager stateManager;
	@Autowired(required=false)
	private CountryManager countryManager;
}
