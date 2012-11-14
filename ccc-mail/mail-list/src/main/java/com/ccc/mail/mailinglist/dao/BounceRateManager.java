package com.ccc.mail.mailinglist.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.BounceRate;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("bounceRateManager")
public class BounceRateManager extends GenericManager<BounceRate> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public BounceRateManager()  {
		super(BounceRate.class);
	}
	/**
	 * Get the unique bounce rate for a given sender and receiver pair
	 * @param emailFrom the sender
	 * @param emailTo the receiver
	 * @return the unique bounce rate for the given sender/receiver pair
	 * or null if none exists
	 */
	public BounceRate ratesForEmail(String emailFrom,String emailTo) {
		List<BounceRate> rates= elementsWithValue("email",emailTo);
		if(rates!=null && !rates.isEmpty()) {
			for(BounceRate rate : rates) {
				if(rate.getEmailAddress().equals(emailTo))
					return rate;
			}
		}
		return rates==null || rates.isEmpty() ? null : rates.get(0);
	}
	
}
