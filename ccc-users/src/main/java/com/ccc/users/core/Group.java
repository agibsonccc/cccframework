package com.ccc.users.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.core.GrantedAuthority;
/**
 * This is a POJO for a group of users. In the database, it is the entity presented. In LDAP, it follows the following specification:
 * 
 * 3.6.  'groupOfUniqueNames'

   The 'groupOfUniqueNames' object class is the same as the
   'groupOfNames' object class except that the object names are not
   repeated or reassigned within a set scope.
   (Source: X.521 [X.521])

Sciberras                   Standards Track                    [Page 22]


RFC 4519           LDAP: Schema for User Applications          June 2006


      ( 2.5.6.17 NAME 'groupOfUniqueNames'
         SUP top
         STRUCTURAL
         MUST ( uniqueMember $
               cn )
         MAY ( businessCategory $
               seeAlso $
               owner $
               ou $
               o $
               description ) )
Reference: http://tools.ietf.org/html/rfc4519#section-3.6
 * @author Adam Gibson
 *
 */
@Entity
@Table(name="groups")
public class Group implements GrantedAuthority {

	/**
	 * 
	 */
	@Transient
	private static final long serialVersionUID = 1L;
	public String getGroupName() {
		return groupName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((groupName == null) ? 0 : groupName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Group other = (Group) obj;
		if (groupName == null) {
			if (other.groupName != null)
				return false;
		} else if (!groupName.equals(other.groupName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Group [groupName=" + groupName + "]";
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}


	@Override
	
	public String getAuthority() {
		return groupName;
	}
	@Id

	@Column(name="group_name")
	private String groupName;
	

}//end Group
