package com.ccc.users.ldap.store;

import org.springframework.ldap.core.DirContextOperations;

import com.ccc.users.core.UserContact;

public class ContactsAttributeMapper extends BasicUserAttributesMapper {
	@Override
	protected Object doMapFromContext(DirContextOperations ctx) {
		UserContact c = new UserContact();
		c.setEmail(ctx.getStringAttribute("mail"));
		String name=ctx.getNameInNamespace();
		int firstComma=name.indexOf(',');
		name=name.substring(firstComma+1);
		int equalsIndex=name.indexOf('=');
		name=name.substring(equalsIndex+1);
		int comma=name.indexOf(',');
		name=name.substring(0,comma);
		c.setContactFor(name);
		c.setUserName("");

		return c;
	}


}
