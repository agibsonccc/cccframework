package com.ccc.mail.mailinglist.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.ccc.mail.mailinglist.model.ListMessageTracking;
import com.ccc.util.springhibernate.dao.GenericManager;
@Repository("listMessageTrackingManager")
public class ListMessageTrackingManager extends GenericManager<ListMessageTracking> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ListMessageTrackingManager() {
		super(ListMessageTracking.class);
	}
	
	
	public int openRateForEmail(String email) {
		List<ListMessageTracking> list=elementsWithValue("email",email);
		return list!=null ? list.size() : 0;
	}
	
}
