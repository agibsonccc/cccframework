package com.ccc.user.dao;


import org.springframework.stereotype.Repository;

import com.ccc.users.core.Company;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("companyManager")
public class CompanyManager extends GenericManager<Company> {

	public CompanyManager() {
		super(Company.class);

	}


}