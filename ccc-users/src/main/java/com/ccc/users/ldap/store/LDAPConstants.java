package com.ccc.users.ldap.store;
/**
 * This class contains constants for default ldap values assuemed in ccc-users.
 * Default objectClass is: basicUser
 * 
 * @author Adam Gibson
 *
 */
public class LDAPConstants {





	/**
	 * Default user type: basicUser
	 */
	public final static String USER_TYPE="basicUser";

	/**
	 * Default email attribute: mail
	 */
	public final static  String EMAIL_ATTRIBUTE="mail";

	
	/**
	 * Default phone number attribute: telephoneNumber
	 */
	public final static String PHONE_ATTRIBUTE="telephoneNumber";

	/**
	 * Default password attribute: userPassword
	 */
	public final static String PASSWORD_ATTRIBUTE="userPassword";
	/**
	 * Default user name attribute: uid
	 */
	public final static String USER_NAME_ATTRIBUTE="uid";
	/**
	 * Default first name attribute: cn
	 */
	public final static String FIRST_NAME_ATTRIBUTE="cn";
	/**
	 * Default last name attribute: sn
	 */
	public final static String LAST_NAME_ATTRIBUTE="sn";
	/**
	 * Default birthday attribute: birthday
	 */
	public final static String BIRTHDAY_ATTRIBUTE="birthday";
	/**
	 * Default gender attribute gender
	 */
	public final static String GENDER_ATTRIBUTE="gender";
	
	/**
	 * This is the default group object class name.
	 */
	public final static String GROUP_ATTRIBUTE_NAME="groupOfUniqueNames";
	
	/**
	 * This is the member attribute for groupOfUniqueNames
	 */
	public final static String MEMBER_ID="uniqueMember";
	
	/**
	 * This is the object class for an alternate user account.
	 */
	public final static String ALT_ACCOUNT_TYPE="altMailAccount";
	/**
	 * This is the server object class in ldap.
	 */
	public final static String SERVER_TYPE="server";
	/**
	 * This is the associated domain attribute for resolving domain names for
	 * servers.
	 */
	public final static String ASSOCIATED_DOMAIN_ATTRIBUTE="associatedDomain";
	/**
	 * This is the ip address attribute for a server.
	 */
	public final static String IP_ADDRESS_ATTRIBUTE="ipHostNumber";
	
	/**
	 * This is the port the server listens on.
	 */
	public final static String SERVER_LISTENING_PORT="ipServicePort";
	
	/**
	 * This is the associated user name with the user 
	 * alt account.
	 */
	public final static String ALT_ACCOUNT_USER_NAME="uid";
	
	/**
	 * This is the default type for a mail server.
	 */
	public final static String MAIL_SERVER_TYPE="mailServerType";
	
	public final static String OTHER_ACCOUNT_NAME="otherMailBox";
	
	/**
	 * This is the property for determining whether a server requires authentication or not.
	 */
	public final static String SERVER_IS_AUTH="isAuth";
	
	/**
	 * This is the name of the settings attribute
	 */
	public final static String SETTINGS_NAME="cn";
	/**
	 * This is the setting value for adding values
	 * to different settings.
	 */
	public final static String SETTING_VAL="settingValue";
	/**
	 * This is the object class for a setting.
	 */
	public final static String SETTING_CLASS="setting";
	/**
	 * This is the setting user id to map users
	 * to setting values.
	 */
	public final static String SETTING_USER="uid";
	
	
}//end LDAPConstants
