package com.ccc.users.ldap.store;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.util.Assert;

import com.ccc.users.core.UserSettings;
/**
 * This will map a user settings to an ldap context.
 * @author Adam Gibson
 *
 */
public class SettingsAttributeMapper extends AbstractContextMapper {

	@Override
	protected Object doMapFromContext(DirContextOperations ctx) {
	
		UserSettings ret = new UserSettings();
		String settingsName=ctx.getStringAttribute(settingsNameAttribute);
		Assert.notNull(settingsName);
		Assert.hasLength(settingsName);
		ret.setSettingName(settingsName);
		String settingsValue=ctx.getStringAttribute(settingValueAttribute);
		Assert.notNull(settingsValue);
		Assert.hasLength(settingsValue);
		ret.setSettingVal(settingsValue);
		String settingsUser=ctx.getStringAttribute(settingUserAttribute);
		Assert.notNull(settingsUser);
		Assert.hasLength(settingsUser);
		ret.setUserName(settingsUser);
		return ret;
			}
	
	public String getSettingsNameAttribute() {
		return settingsNameAttribute;
	}

	public void setSettingsNameAttribute(String settingsNameAttribute) {
		this.settingsNameAttribute = settingsNameAttribute;
	}

	public String getSettingValueAttribute() {
		return settingValueAttribute;
	}

	public void setSettingValueAttribute(String settingValueAttribute) {
		this.settingValueAttribute = settingValueAttribute;
	}

	public String getSettingUserAttribute() {
		return settingUserAttribute;
	}

	public void setSettingUserAttribute(String settingUserAttribute) {
		this.settingUserAttribute = settingUserAttribute;
	}

	private String settingsNameAttribute=LDAPConstants.SETTINGS_NAME;
	
	
	private String settingValueAttribute=LDAPConstants.SETTING_VAL;
	
	private String settingUserAttribute=LDAPConstants.SETTING_USER;
}
