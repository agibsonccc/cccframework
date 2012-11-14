package com.ccc.users.ldap.store;

import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import org.springframework.ldap.core.AttributesMapper;

import com.ccc.users.core.BasicUser;
/**
 * This maps attributes passed in to a basic user.
 * If you would like to override the default inetOrgPerson type, just set the attributes.
 * @author Adam Gibson
 *
 */
public class UserAttributesMapper implements AttributesMapper {
	@Override
	public Object mapFromAttributes(Attributes attributes)
			throws NamingException {
		String userName=(String) attributes.get(userNameAttribute).get();
		String email=(String) attributes.get(emailAttribute).get();
		String password=(String) attributes.get(passwordAttribute).get();
		String phone=(String) attributes.get(phoneAttribute).get();
		String firstName=(String) attributes.get(firstNameAttribute).get();
		String lastName=(String) attributes.get(lastNameAttribute).get();
		String birthday=(String) attributes.get(birthdayAttribute).get();
		String gender=(String) attributes.get(genderAttribute).get();


		BasicUser ret = new BasicUser();
		ret.setCredentialsExpired(false);
		ret.setEmail(email);
		ret.setEnabled(true);
		ret.setUserName(userName);
		ret.setPassword(password);
		ret.setPhoneNumber(phone);
		ret.setFirstName(firstName);
		ret.setLastName(lastName);
		if(birthday!=null) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");
			String dateFromat=format.format(birthday);
			Date d=Date.valueOf(dateFromat);
			ret.setBirthday(d);
		}
	
		ret.setGender(gender);
		return ret;
	}


	public String getEmailAttribute() {
		return emailAttribute;
	}

	public void setEmailAttribute(String emailAttribute) {
		this.emailAttribute = emailAttribute;
	}



	public String getPhoneAttribute() {
		return phoneAttribute;
	}

	public void setPhoneAttribute(String phoneAttribute) {
		this.phoneAttribute = phoneAttribute;
	}

	public String getPasswordAttribute() {
		return passwordAttribute;
	}

	public void setPasswordAttribute(String passwordAttribute) {
		this.passwordAttribute = passwordAttribute;
	}

	public String getUserNameAttribute() {
		return userNameAttribute;
	}

	public void setUserNameAttribute(String userNameAttribute) {
		this.userNameAttribute = userNameAttribute;
	}





	public String getFirstNameAttribute() {
		return firstNameAttribute;
	}


	public void setFirstNameAttribute(String firstNameAttribute) {
		this.firstNameAttribute = firstNameAttribute;
	}


	public String getLastNameAttribute() {
		return lastNameAttribute;
	}


	public void setLastNameAttribute(String lastNameAttribute) {
		this.lastNameAttribute = lastNameAttribute;
	}





	@Override
	public String toString() {
		return "UserAttributesMapper [EMAIL_ATTRIBUTE=" + emailAttribute
				+  ", PHONE_ATTRIBUTE=" + phoneAttribute
				+ ", PASSWORD_ATTRIBUTE=" + passwordAttribute
				+ ", USER_NAME_ATTRIBUTE=" + userNameAttribute
				+ ", FIRST_NAME_ATTRIBUTE=" + firstNameAttribute
				+ ", LAST_NAME_ATTRIBUTE=" + lastNameAttribute
				+ ", BIRTHDAY_ATTRIBUTE=" + birthdayAttribute
				+ ", GENDER_ATTRIBUTE=" + genderAttribute + "]";
	}


	public String getBirthdayAttribute() {
		return birthdayAttribute;
	}


	public void setBirthdayAttribute(String birthdayAttribute) {
		this.birthdayAttribute = birthdayAttribute;
	}


	public String getGenderAttribute() {
		return genderAttribute;
	}


	public void setGenderAttribute(String genderAttribute) {
		this.genderAttribute = genderAttribute;
	}





	private String emailAttribute=LDAPConstants.EMAIL_ATTRIBUTE;


	private String phoneAttribute=LDAPConstants.PHONE_ATTRIBUTE;


	private String passwordAttribute=LDAPConstants.PASSWORD_ATTRIBUTE;

	private String userNameAttribute=LDAPConstants.USER_NAME_ATTRIBUTE;

	private String firstNameAttribute=LDAPConstants.FIRST_NAME_ATTRIBUTE;

	private String lastNameAttribute=LDAPConstants.LAST_NAME_ATTRIBUTE;

	private String birthdayAttribute=LDAPConstants.BIRTHDAY_ATTRIBUTE;

	private String genderAttribute=LDAPConstants.GENDER_ATTRIBUTE;
}
