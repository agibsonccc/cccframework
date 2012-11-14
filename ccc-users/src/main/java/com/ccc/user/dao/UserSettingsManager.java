package com.ccc.user.dao;

import org.springframework.stereotype.Repository;

import com.ccc.users.core.UserSettings;
import com.ccc.util.springhibernate.dao.GenericManager;

@Repository("userSettingsManager")
public class UserSettingsManager extends GenericManager<UserSettings> {

	public UserSettingsManager() {
		super(UserSettings.class);
	}
}
