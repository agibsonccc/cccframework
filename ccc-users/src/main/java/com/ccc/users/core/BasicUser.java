package com.ccc.users.core;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.ccc.users.utils.DateUtils;
/**
 * This is a basic user for the system. If you are using ldap, it uses the following schema:
 * objectclass inetOrgPerson
        requires
                objectClass,
                sn,
                cn
        allows
                audio,
                businessCategory,
                carLicense,
                departmentNumber,
                description,
                destinationIndicator,
                displayName,
                employeeNumber,
                employeeType,
                facsimileTelephoneNumber,
                givenName,
                homePhone,
                homePostalAddress,
                jpegPhoto,
                initials,
                internationaliSDNNumber,          
                l,
                labeledURI,
                mail,
                manager,
                mobile,
                o,
                ou,
                pager,
                photo,
                physicalDeliveryOfficeName,
                postOfficeBox,
                postalAddress,
                postalCode,
                preferredDeliveryMethod,
                preferredLanguage,
                roomNumber,
                registeredAddress,
                secretary,
                seeAlso,
                st,
                street,
                telephoneNumber,             
                teletexTerminalIdentifier,
                telexNumber,
                title,
                uid,
                userCertificate,
                userPassword,
                userPKCS12,
                userSMIMECertificate,
                x121Address,
                x500UniqueIdentifier    

See: RFC-2798
http://www.faqs.org/rfcs/rfc2798.html
If you are using database, the elements will be based on the annotated fields already presented.
Take note that this user object only uses a subset of the attributes here. As this is a base object, please
feel free to extend it relative it to the rfc you need.

 * @author Adam Gibson
 *
 */
@Entity
@Table(name="user")
public class BasicUser  implements UserDetails {





	public String getGroups() {
		return groups;
	}

	public void setGroups(String groups) {
		this.groups = groups;
	}

	

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public boolean isCredentialsExpired() {
		return credentialsExpired;
	}

	public void setCredentialsExpired(boolean credentialsExpired) {
		this.credentialsExpired = credentialsExpired;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}



	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}



	public String getUsername() {
		return userName;
	}

	public boolean isAccountNonExpired() {
		return enabled;
	}

	public boolean isAccountNonLocked() {
		return enabled;
	}

	public boolean isCredentialsNonExpired() {
		return !credentialsExpired;
	}

	public boolean isEnabled() {
		return enabled;
	}

	private static void validateBirthday(String birthday) throws IllegalArgumentException {
		Assert.notNull(birthday);
		if(!DateUtils.validateDayMonth(birthday))
			throw new IllegalArgumentException("Given date does not match! " + birthday);
	}

	/**
	 * This returns the roles that this user has.
	 */
	public List<GrantedAuthority> getAuthorities() {
		//groups are csv
		String[] groups1=groups.split(",");
		List<GrantedAuthority> ret = new ArrayList<GrantedAuthority>();
		for(String s : groups1)
			ret.add(new GrantedAuthorityImpl(s));
		return ret;
	}

	
	public class GrantedAuthorityImpl implements GrantedAuthority {
		public GrantedAuthorityImpl(String s) {
			this.role=s;
		}
		@Override
		public String getAuthority() {
			return role;
		}
		private String role;
	}
	
	@Override
	public String toString() {
		return "BasicUser [userName=" + userName + ", email=" + email
				+ ", password=" + password + ", enabled=" + enabled
				+ ", credentialsExpired=" + credentialsExpired
				+ ", phoneNumber=" + phoneNumber + ", firstName=" + firstName
				+ ", lastName=" + lastName + ", birthday=" + birthday
				+ ", gender=" + gender + ", groups=" + groups + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((birthday == null) ? 0 : birthday.hashCode());
		result = prime * result + (credentialsExpired ? 1231 : 1237);
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + (enabled ? 1231 : 1237);
		result = prime * result
				+ ((firstName == null) ? 0 : firstName.hashCode());
		result = prime * result + ((gender == null) ? 0 : gender.hashCode());
		result = prime * result + ((groups == null) ? 0 : groups.hashCode());
		result = prime * result
				+ ((lastName == null) ? 0 : lastName.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result
				+ ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
		result = prime * result
				+ ((userName == null) ? 0 : userName.hashCode());
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
		BasicUser other = (BasicUser) obj;
		if (birthday == null) {
			if (other.birthday != null)
				return false;
		} else if (!birthday.equals(other.birthday))
			return false;
		if (credentialsExpired != other.credentialsExpired)
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (enabled != other.enabled)
			return false;
		if (firstName == null) {
			if (other.firstName != null)
				return false;
		} else if (!firstName.equals(other.firstName))
			return false;
		if (gender == null) {
			if (other.gender != null)
				return false;
		} else if (!gender.equals(other.gender))
			return false;
		if (groups == null) {
			if (other.groups != null)
				return false;
		} else if (!groups.equals(other.groups))
			return false;
		if (lastName == null) {
			if (other.lastName != null)
				return false;
		} else if (!lastName.equals(other.lastName))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (phoneNumber == null) {
			if (other.phoneNumber != null)
				return false;
		} else if (!phoneNumber.equals(other.phoneNumber))
			return false;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="user_name")
	private String userName;

	@Column(name="email")
	private String email;
	

	@Column(name="password")
	private String password;
	@Column(name="enabled")
	private boolean enabled;
	@Column(name="credentials_expired")
	private boolean credentialsExpired;
	@Column(name="phone")
	private String phoneNumber;
	@Column(name="last_name")
	private String firstName;
	@Column(name="first_name")
	private String lastName;
	@Column(name="birthday")
	private Date birthday;
	@Column(name="gender")
	private String gender;
	@Column(name="groups")
	private String groups;
}//end BasicUser
