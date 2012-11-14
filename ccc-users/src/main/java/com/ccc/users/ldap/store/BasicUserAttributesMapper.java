package com.ccc.users.ldap.store;

import java.sql.Date;
import java.text.SimpleDateFormat;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.util.Assert;

import com.ccc.users.core.BasicUser;
/**
 * This is a mapper for attributes relative to LDAP. 
 * The basic user makes the assumption of an inetOrgPerson as the type. 
 * If you would like to change the type, please set the fields for the attributes.
 * @author Adam Gibson
 *
 */
public class BasicUserAttributesMapper extends AbstractContextMapper   {



	@Override
	protected Object doMapFromContext(DirContextOperations ctx) {
		String email=ctx.getStringAttribute(emailAttribute);
		Assert.notNull(email);
		Assert.hasText(email);
		String phoneNumber=ctx.getStringAttribute(phoneAttribute);
		Assert.notNull(phoneNumber);
		Assert.hasText(phoneNumber);
		String firstName=ctx.getStringAttribute(firstNameAttribute);
		Assert.notNull(firstName);
		Assert.hasText(firstName);
		String lastName=ctx.getStringAttribute(lastNameAttribute);
		Assert.notNull(lastName);
		Assert.hasText(lastName);
		String birthday=ctx.getStringAttribute(birthdayAttribute);
		
		String gender=ctx.getStringAttribute(genderAttribute);
		Assert.notNull(gender);
		Assert.hasText(gender);
		String userName=ctx.getStringAttribute(userNameAttribute);
		Assert.notNull(userName);
		Assert.hasText(userName);
		BasicUser ret = new BasicUser();
		ret.setCredentialsExpired(false);
		ret.setEmail(email);
		ret.setEnabled(true);
		ret.setPhoneNumber(phoneNumber);
		ret.setFirstName(firstName);
		ret.setLastName(lastName);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-DD");
		if(birthday!=null) {
			String dateFromat=format.format(birthday);
			Date d=Date.valueOf(dateFromat);
			ret.setBirthday(d);
		}
		if(gender!=null)
			ret.setGender(gender);
		ret.setUserName(userName);
		/* Can't retrieve a password directly, must decode, and pass along the buffered string */
		Object[] pass=ctx.getObjectAttributes(passwordAttribute);
		StringBuilder buf = new StringBuilder();
		for(Object o : pass) buf.append(o);
		ret.setPassword(buf.toString());
		ret.setUserName(ctx.getStringAttribute(userNameAttribute));
		return ret;
	}//end doMapFromContext





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


	protected String emailAttribute=LDAPConstants.EMAIL_ATTRIBUTE;


	protected String phoneAttribute=LDAPConstants.PHONE_ATTRIBUTE;

	protected String passwordAttribute=LDAPConstants.PASSWORD_ATTRIBUTE;

	protected String userNameAttribute=LDAPConstants.USER_NAME_ATTRIBUTE;

	protected String firstNameAttribute=LDAPConstants.FIRST_NAME_ATTRIBUTE;

	protected String lastNameAttribute=LDAPConstants.LAST_NAME_ATTRIBUTE;

	protected String birthdayAttribute=LDAPConstants.BIRTHDAY_ATTRIBUTE;

	protected String genderAttribute=LDAPConstants.GENDER_ATTRIBUTE;
}//end BasicUserAttributesMapper
