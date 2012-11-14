package com.ccc.users.ldap.store;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

import com.ccc.users.core.Group;
/**
 * This maps an ou to a group name in LDAP
 * @author Adam Gibson
 *
 */
public class GroupAttributesMapper extends AbstractContextMapper {

	@Override
	protected Object doMapFromContext(DirContextOperations ctx) {
		String context=ctx.getStringAttribute("cn");
		Group gr = new Group();
		
		gr.setGroupName(context);
		return gr;
		

	}

}//end GroupAttributesMapper
