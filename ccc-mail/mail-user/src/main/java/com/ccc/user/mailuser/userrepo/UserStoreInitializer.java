package com.ccc.user.mailuser.userrepo;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccc.users.core.store.UserStore;
@Component
public class UserStoreInitializer  {
	@PostConstruct
	public void init() {
		MailServerUserClient.userStore=userStore;
	}
	
	
	@Autowired
	private UserStore userStore;
}
