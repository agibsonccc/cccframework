package com.ccc.users.ldap.store;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.ccc.users.core.Group;
/**
 * This is an implementation of the group mapper for LDAP.
 * @author Adam Gibson
 *
 */
public class GroupAttributesMapperImpl implements AttributesMapper {

	@Override
	public Object mapFromAttributes(Attributes attributes)
			throws NamingException {
		String name=(String)attributes.get("cn").get();
		
		Group g = new Group();
		g.setGroupName(name);
		return g;
	}

}//end GroupAttributesMapperImpl
