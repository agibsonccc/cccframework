package com.ccc.users.ldap.store;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.naming.InvalidNameException;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.NameAlreadyBoundException;
import org.springframework.ldap.NameNotFoundException;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.LikeFilter;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.LdapShaPasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.util.Assert;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.Group;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;
import com.ccc.users.core.store.UserStore;
import com.ccc.util.collections.Converter;
/**
 * This is an LDAP user store. It uses the ldap template and makes following assumptions:
 * Your directory tree has all of it's users in one directory. 
 * Each role is divided up by ou.
 * The objectClass in LDAP is inetOrgPerson. 
 * To get other implementations, such as samaAccountName{0} in active directory, 
 * override this implementations AttributesForUser and USER_TYPE.
 * @author Adam Gibson
 *
 */
public class LDAPUserStore implements UserStore {

	@SuppressWarnings("unchecked")
	@Override
	public List<UserSettings> allSettings() {
		AndFilter filter = new AndFilter();

		filter.and(new EqualsFilter("objectclass",settingsClass));
		String searchBase=dnForSettings();
		List<UserSettings> settings=ldapTemplate.search(searchBase, filter.encode(), new SettingsAttributeMapper());
		return settings;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6361317875313514145L;

	@SuppressWarnings("unchecked")
	@Override
	public List<BasicUser> retrieveUsers() {
		/* Search the user directory overall */
		DistinguishedName dn2 = new DistinguishedName(userDirectory + "," + dnForUsers);

		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", userType));
		BasicUserAttributesMapper mapper = new BasicUserAttributesMapper();
		List<BasicUser> users=(List<BasicUser>) ldapTemplate.search(dn2,andFilter.encode(),mapper);
		for(BasicUser u : users) {
			String groups=retrieveGroups(u.getUsername());
			u.setGroups(groups);
			if(log.isDebugEnabled()) {
				log.debug("User retrieved: " + u.toString() + " groups for user: " + groups);
			}
		}
		return users;
	}//end retrieveUsers





	@Override
	public void addUser(BasicUser user) {
		Attributes a =attributesForUser(user);
		/* Get ou */
		String overallDN=dnForUser(user.getUsername());
		try {
			if(log.isDebugEnabled()) {
				log.debug("Attempting to add user with DN of:  " + overallDN + " and user of: " +  user.toString() + " and attributes of: " + a.toString());
			}
			Assert.notNull(ldapTemplate,"No LDAP template found!");
			//	System.err.println("Attempting to authenticate with salt: " + salt.getClass());
			//	user.setPassword(passwordEncoder.encodePassword(user.getPassword(),user.getUsername().getBytes()));
			ldapTemplate.bind(overallDN, user, a);
			// Update Groups

			String group2=user.getGroups();
			if(group2!=null && group2.length() > 0) {
				String[] groups=group2.split(",");
				for (String group : groups) {
					try {
						DistinguishedName groupDn =buildGroupDn();
						//Ensure that the group dn exists and then add it
						groupDn.add("cn", group);
						DirContextOperations context = null;
						try {	
							context=ldapTemplate
									.lookupContext(groupDn);
						}catch(NameNotFoundException e1) {}
						if(context!=null) {
							context.addAttributeValue(memberType, user.getUserName());

							ldapTemplate.modifyAttributes(context);

						}
						else {
							addUserToGroup(user,group);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}



			if(log.isDebugEnabled()) {
				log.debug("Added user with DN of:  " + overallDN + " and user of: " +  user.toString() + " and attributes of: " + a.toString());
			}

		}catch(NameAlreadyBoundException e) {
			if(log.isDebugEnabled())
				log.debug("User: " + user.toString() + " was already bound.");
		}
	}//end addUser

	/* Checks for the existence of an entry */
	public  boolean nameExists(String dn) {
		try {
			return ldapTemplate.lookup(dn)!=null;
		}catch(NameNotFoundException e) {
			return false;
		}
	}//end nameExists




	public static Attributes groupAttributes(Group group) {
		Attributes a = new BasicAttributes();
		a.put("cn",group.getGroupName());

		return a;
	}
	/**
	 * The ldap attributes for the given contact.
	 * @param contact the contact to get attributes for
	 * @return the ldap attributes for this contact, null if null is passed in
	 */
	public Attributes contactAttributes(UserContact contact) {
		if(contact==null)
			return null;
		Attributes ret = new BasicAttributes();
		ret.put("objectclass","basicUser");
		ret.put("mail",contact.getEmail());
		ret.put("cn",contact.getEmail());
		ret.put("sn",contact.getEmail());
		return ret;
	}//end contactAttributes




	@SuppressWarnings("unchecked")
	@Override
	public List<BasicUser> usersWithRole(String role) {
		String ou="ou=" + role + ",";
		String dn=ou + dnForUsers;
		/* Search in a specific ou for a given role. */
		DistinguishedName dn2 = new DistinguishedName(dn);

		AndFilter andFilter = new AndFilter();
		andFilter.and(new EqualsFilter("objectclass", userType)).and(new EqualsFilter("cn",role));
		BasicUserAttributesMapper mapper = new BasicUserAttributesMapper();
		return ldapTemplate.search(dn2,andFilter.encode(),mapper);
	}



	public LdapTemplate getLdapTemplate() {
		return ldapTemplate;
	}

	public void setLdapTemplate(LdapTemplate ldapTemplate) {
		this.ldapTemplate = ldapTemplate;
	}

	public String getDnForUsers() {
		return dnForUsers;
	}

	public void setDnForUsers(String dnForUsers) {
		this.dnForUsers = dnForUsers;
	}
	/**
	 * This returns the attributes for LDAP for a given user.
	 * @param u the user to get the attributes for
	 * @return the attributes for this user.
	 */
	public  Attributes attributesForUser(BasicUser u) {
		Attributes a = new BasicAttributes();
		/* Object class for user */
		BasicAttribute objClass = new BasicAttribute("objectclass");
		objClass.add("person");
		objClass.add(userType);		
		a.put(objClass);

		/* Add actual fields */
		a.put(LDAPConstants.EMAIL_ATTRIBUTE,u.getEmail());
		if(u.getBirthday()!=null)
			a.put(LDAPConstants.BIRTHDAY_ATTRIBUTE,u.getBirthday().toString());
		a.put(LDAPConstants.GENDER_ATTRIBUTE,u.getGender());
		a.put(LDAPConstants.USER_NAME_ATTRIBUTE,"cn=" +  u.getUsername());
		a.put(LDAPConstants.PASSWORD_ATTRIBUTE,  u.getPassword());
		if(u.getPhoneNumber()!=null)
			a.put(LDAPConstants.PHONE_ATTRIBUTE,u.getPhoneNumber());
		a.put(LDAPConstants.LAST_NAME_ATTRIBUTE,u.getLastName());
		a.put(LDAPConstants.FIRST_NAME_ATTRIBUTE,u.getFirstName());

		return a;
	}



	@Override
	public BasicUser userForName(String userName) {
		//Get DN
		String dn=dnForUser(userName);
		if(!nameExists(dn))
			return null;
		BasicUser u= (BasicUser) ldapTemplate.lookup(dn, new BasicUserAttributesMapper());
		String groups=retrieveGroups(userName);
		u.setGroups(groups);
		return u;
	}

	/**
	 * This will retrieve groups for a given user name.
	 * @param u the name of the user to retrieve groups for
	 * @return  a comma separated list of groups for the given user
	 */
	@SuppressWarnings("unchecked")
	private String retrieveGroups(String u) {
		Assert.notNull(u);
		//retrieve the name for searching.
		DistinguishedName groupDn = buildGroupDn();
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass",groupType));
		//All of the groups
		List<Group> groups=(List<Group>)ldapTemplate.search(groupDn, filter.encode(), new GroupAttributesMapper());

		if(groups==null || groups.isEmpty()) return "";
		//List of groups this user is in
		List<String> groupList = new ArrayList<String>();
		//Look up each group and their members
		for(Group g : groups) {
			//Build the distinguished name for this group
			DistinguishedName groupDn2 =buildGroupDn();
			//Ensure that the group dn exists and then add it

			groupDn2.add("cn",g.getGroupName());
			if(nameExists(groupDn2.encode())) {
				DirContextOperations ctx=ldapTemplate.lookupContext(groupDn2);
				//If the user name is found, add that to the list

				String[] members=ctx.getStringAttributes(memberType);
				if(members!=null && members.length > 0) {
					for(String s : members) {
						String comp=extractCn(s);
						if(comp.equals(u)) {
							String nameToAdd=extractCn(g.getGroupName());
							groupList.add(nameToAdd);
						}
					}
				}
			}
		}


		//Collect the group names for the given user
		//and append them to the group csv group list
		//for the user.
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<groupList.size();i++) {
			String g=groupList.get(i);
			sb.append(g);
			if(i < groupList.size()-1)
				sb.append(",");
		}
		return sb.toString();
	}//end retrieveGroups

	private DistinguishedName buildGroupDn() {
		if(!groupOUExists())
			bindGroupOU();
		String groupDn=groupDn();
		return new DistinguishedName(groupDn);
	}//end validateGroupDn


	private String dnForUser(String userName) {
		return "cn=" + userName + "," + userDirectory + "," + dnForUsers;
	}

	private String dnForGroup(String groupName) {
		return "cn=" + groupName +   "," + "ou=Groups" + "," + dnForUsers;
	}

	private String dnForAccount(String accountName) {
		return "cn=" + accountName +   "," + "ou=Accounts" + "," + dnForUsers;
	}


	private String dnForContact(String contactName,String contactFor) {
		return "cn=" + contactName + ",cn=" + contactFor + ",ou=Contacts,ou=Groups," + dnForUsers;
	}
	private String groupDn() {
		return "ou=Groups" + "," + dnForUsers;
	}

	private String dnForContactHolder(String userName) {
		return"cn=" + userName + ",ou=Contacts,ou=Groups," + dnForUsers;

	}

	private String dnForServerName(String serverName) {
		return"cn=" + serverName + ",ou=Servers," + dnForUsers;

	}

	private String dnForSettingName(String settingName) {
		return"cn=" + settingName + ",ou=Settings," + dnForUsers;

	}
	private String dnForSettings() {
		return "ou=Settings," + dnForUsers;
	}
	private String dnForServers() {
		return "ou=Servers," + dnForUsers;
	}
	private String dnForAltAccounts() {
		return "ou=Accounts," + dnForUsers;
	}
	@Override
	public void deleteUser(String userName) {
		String dn=dnForUser(userName);
		ldapTemplate.unbind(dn);

	}

	private void bindGroupOU() {
		DistinguishedName groupDn = new DistinguishedName(dnForUsers);
		groupDn.add("ou", "Groups");
		try {
			groupDn.add(dnForUsers);
			DirContextOperations ctx = new DirContextAdapter(groupDn);
			ldapTemplate.bind(ctx);
		} catch (InvalidNameException e) {

			e.printStackTrace();
		}
	}//end bindGroupOU


	/* This creates the overarching contacts ou */
	private void bindContactOU() {

		String groupDn="ou=Contacts,ou=Groups," + dnForUsers;
		Attributes a = new BasicAttributes();
		a.put("objectclass","organizationalUnit");
		a.put("ou","Contacts");
		ldapTemplate.bind(groupDn, new OU(groupDn), a);

	}//end bindContactOU



	/* This creates the overarching contacts ou */
	private void bindAccounts() {

		String groupDn="ou=Accounts," + dnForUsers;
		Attributes a = new BasicAttributes();
		a.put("objectclass","organizationalUnit");
		a.put("ou","Accounts");
		ldapTemplate.bind(groupDn, new OU(groupDn), a);

	}//end bindContactOU


	/* This creates the overarching contacts ou */
	private void bindServers() {

		String groupDn="ou=Servers," + dnForUsers;
		Attributes a = new BasicAttributes();
		a.put("objectclass","organizationalUnit");
		a.put("ou","Servers");
		ldapTemplate.bind(groupDn, new OU(groupDn), a);

	}//end bindServers

	/* This creates the overarching settings ou */
	private void bindSettings() {

		String groupDn="ou=Settings," + dnForUsers;
		Attributes a = new BasicAttributes();
		a.put("objectclass","organizationalUnit");
		a.put("ou","Settings");
		ldapTemplate.bind(groupDn, new OU(groupDn), a);

	}//end bindSettings


	/* This checks to ensure the settings ou exists. */
	private boolean settingsExist() {
		String groupDn="ou=Settings," + dnForUsers;

		return nameExists(groupDn);
	}//end contactsExist

	/* This checks to ensure the contacts ou exists. */
	private boolean contactsExist() {
		String groupDn="ou=Contacts,ou=Groups," + dnForUsers;

		return nameExists(groupDn);
	}//end contactsExist

	/* This checks to ensure the contacts ou exists. */
	private boolean accountsExist() {
		String groupDn="ou=Accounts," + dnForUsers;

		return nameExists(groupDn);
	}//end accountsExist

	/* This checks to ensure the contacts ou exists. */
	private boolean serversExist() {
		String groupDn="ou=Servers," + dnForUsers;

		return nameExists(groupDn);
	}//end contactsExist
	@Override
	public void deleteUserFromGroup(BasicUser toDelete, String deleteFrom) {
		try {
			DistinguishedName groupDn = buildGroupDn();
			//Ensure that the group dn exists and then add it
			groupDn.add("cn", deleteFrom);
			DirContextOperations context = null;
			try {
				context=ldapTemplate
						.lookupContext(groupDn);
			}catch(NameNotFoundException ignore){}

			if(context!=null) {
				//Convert to dn
				String userDn=dnForUser(toDelete.getUsername());

				context.removeAttributeValue(memberType, userDn);
				//Update attributes
				ldapTemplate.modifyAttributes(context);
				//Update user
				String newGroups=retrieveGroups(toDelete.getUsername());


				toDelete.setGroups(newGroups);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}


	}//end deleteUserFromGroup

	@Override
	public void modifyUser(BasicUser u) {
		String dn=dnForUser(u.getUsername());
		Attributes a=attributesForUser(u);
		ldapTemplate.rebind(dn, u, a);
	}

	private boolean groupOUExists() {
		String groupDn="ou=Groups" + "," + dnForUsers;
		return ldapTemplate.lookup(groupDn)!=null;

	}//end groupOUExists



	@Override
	public List<BasicUser> usersInGroup(String g) {
		String groupDn=dnForGroup(g);
		//no users in group that doesn't exist
		if(!nameExists(groupDn)) {
			return null;
		}
		DirContextOperations ctx=null;

		try {ctx=ldapTemplate.lookupContext(groupDn);}
		catch(NameNotFoundException e) {return null;}
		String[] userIds=retrieveUserIds(ctx);
		List<BasicUser> ret = new ArrayList<BasicUser>();
		for(String s : userIds) {
			int i=s.indexOf("cn=") + "cn=".length();
			s=s.substring(i);
			int j=s.indexOf(",");
			s=s.substring(0,j);
			BasicUser u=userForName(s);
			ret.add(u);
		}
		return ret;
	}//end usersInGroup

	private String[] retrieveUserIds(DirContextOperations ctx) {
		String[] members=ctx.getStringAttributes(memberType);
		return members;
	}





	private  Attributes attributesForGroup(Group g) {
		Attributes a = new BasicAttributes();
		a.put("objectclass",groupType);
		a.put("cn", g.getGroupName());
		return a;
	}

	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}


	public String getUserDirectory() {
		return userDirectory;
	}
	public void setUserDirectory(String userDirectory) {
		this.userDirectory = userDirectory;
	}






	@Override
	public List<String> allGroups() {
		DistinguishedName groupDn = new DistinguishedName(dnForUsers);
		//Configure the filter to look for groups
		groupDn.add("ou", "Groups");
		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass",groupType));
		@SuppressWarnings("unchecked")
		List<Group> groups=(List<Group>)ldapTemplate.search(groupDn, filter.encode(), new GroupAttributesMapper());
		Set<Group> uniqueGroups = new Converter<Group>().listToSet(groups);

		List<String> ret = new ArrayList<String>();
		for(Group g : uniqueGroups) {
			ret.add(g.getGroupName());
		}
		return ret;
	}//end allGroups




	public List<UserContact> contactsForUser(BasicUser u) {
		String dn="cn=" + u.getUserName() + ",ou=Contacts,ou=Groups," + dnForUsers;


		AndFilter filter = new AndFilter();
		filter.and(new EqualsFilter("objectclass", "person"));
		filter.and(new EqualsFilter("objectclass",userType));
		if (dn != null) {
			filter.and(new LikeFilter("cn", "*"));
		}
		@SuppressWarnings("unchecked")
		List<UserContact> users = ldapTemplate.search(dn,
				filter.encode(), new ContactsAttributeMapper());


		return users;
	}//end updateGroup





	public void addContact(UserContact toAdd) throws IllegalArgumentException {
		Assert.notNull(toAdd);
		String contactDn=this.dnForContact(toAdd.getEmail(), toAdd.getContactFor());
		//Ensure contact ou exists.
		if(!contactsExist())
			bindContactOU();
		//Ensure parent exists, if not create it
		if(!nameExists(dnForContactHolder(toAdd.getContactFor()))) {
			addContactHolder(toAdd.getContactFor());

		}
		if(!nameExists(contactDn))
			ldapTemplate.bind(contactDn, toAdd, contactAttributes(toAdd));
	}//end addContact


	/* Add the group for the user if it doesn't exist */
	private void addContactHolder(String name) {
		String dn=dnForContactHolder(name);
		Group g = new Group();
		g.setGroupName(name);
		Attributes a=attributesForGroup(g);
		ldapTemplate.bind(dn, g, a);
	}

	public void removeContact(UserContact toDelete) {
		String contactDn=this.dnForContact(toDelete.getEmail(), toDelete.getContactFor());
		ldapTemplate.unbind(contactDn);
	}

	@Override
	public void deleteGroup(String group) {
		DistinguishedName groupDn = new DistinguishedName(dnForUsers);
		groupDn.add("ou", "Groups");
		groupDn.add("cn", group);

		ldapTemplate.unbind(groupDn);
	}//end deleteGroup

	private void addGroupMember(String groupName,String userName) {
		DistinguishedName groupDn = buildGroupDn();
		//Ensure that the group dn exists and then add it
		groupDn.add("cn", groupName);
		String userDn=dnForUser(userName);
		//User must exist
		BasicUser u=userForName(userName);
		Assert.notNull(u,"User not found: " + userName + " group name was: " + groupName);
		if(!nameExists(groupDn.encode())) {
			Attributes attributes = new BasicAttributes();
			attributes.put("cn", groupName);
			attributes.put(memberType,userDn);
			attributes.put("objectclass",LDAPConstants.GROUP_ATTRIBUTE_NAME);
			ldapTemplate.bind(groupDn.encode(), null, attributes);
		}
		else {
			DirContextOperations context = ldapTemplate
					.lookupContext(groupDn);
			if(context==null) throw new IllegalStateException("No context found for adding user to group: " + groupDn);			
			//Convert to dn
			context.addAttributeValue("uniqueMember", userDn);
			ldapTemplate.modifyAttributes(context);
		}
	}
	@Override
	public void addUserToGroup(BasicUser toAdd, String addTo) {
		addGroupMember(addTo,toAdd.getUsername());
	}//end addUserToGroup



	/**
	 * This will extract the common name from a dn.
	 * @param cn the dn to extract from
	 * @return the extracted cn
	 */
	private String extractCn(String cn) {
		if(cn==null || cn.isEmpty())
			return null;
		if(!cn.contains("cn"))
			return cn;
		int i=cn.indexOf("cn=") + "cn=".length();
		int j=cn.indexOf(",");
		return  cn.substring(i,j);
	}//end extractCn





	public void updateContact(UserContact toUpdate) {
		String contactDn=this.dnForContact(toUpdate.getEmail(), toUpdate.getContactFor());

		ldapTemplate.rebind(contactDn, toUpdate, contactAttributes(toUpdate));
	}


	public String getGroupType() {
		return groupType;
	}

	public String getUserAltAccountType() {
		return userAltAccountType;
	}

	public void setUserAltAccountType(String userAltAccountType) {
		this.userAltAccountType = userAltAccountType;
	}

	public String getMailAttribute() {
		return mailAttribute;
	}

	public void setMailAttribute(String mailAttribute) {
		this.mailAttribute = mailAttribute;
	}

	public String getUserNameType() {
		return userNameType;
	}

	public void setUserNameType(String userNameType) {
		this.userNameType = userNameType;
	}






	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}





	public String getMemberType() {
		return memberType;
	}

	@Override
	public void changePassword(String userName, String password) {
		Assert.notNull(userName);
		Assert.notNull(password);
		Assert.hasLength(userName);
		Assert.hasLength(password);
		BasicUser u=userForName(userName);
		if(u==null) {
			log.warn("Attempted to change the password of a non existant user!");
		}

		u.setPassword(password);
		modifyUser(u);
	}




	@Override
	public List<UserSettings> settingsForUser(String userName) {
		//Assure user exists.
		BasicUser u=userForName(userName);
		if(u==null) return null;
		AndFilter filter = new AndFilter();

		filter.and(new EqualsFilter(settingUserAttribute,userName));
		filter.and(new EqualsFilter("objectclass",settingsClass));
		String searchBase=dnForSettings();
		@SuppressWarnings("unchecked")
		List<UserSettings> settings=ldapTemplate.search(searchBase, filter.encode(), new SettingsAttributeMapper());
		return settings;
	}





	@Override
	public void addUserSetting(UserSettings toAdd) {
		//Ensure settings ou exists.
		if(!settingsExist()) {
			bindSettings();
		}
		String dnForSetting=dnForSettingName(toAdd.getSettingName());
		if(!nameExists(dnForSetting))
			ldapTemplate.bind(dnForSetting, toAdd, attributesForSettings(toAdd));
	}





	@Override
	public void deleteUserSettings(UserSettings toDelete) {
		String dnForSetting=dnForSettingName(toDelete.getSettingName());
		ldapTemplate.unbind(dnForSetting);
	}





	@Override
	public void updateUserSettings(UserSettings toUpdate) {
		String dnForSetting=dnForSettingName(toUpdate.getSettingName());
		ldapTemplate.rebind(dnForSetting, toUpdate, attributesForSettings(toUpdate));
	}

	private Attributes attributesForSettings(UserSettings toGet) {
		Attributes ret = new BasicAttributes();
		ret.put(settingUserAttribute, toGet.getUserName());
		ret.put(settingValueAttribute, toGet.getSettingVal());
		ret.put("objectclass",settingsClass);
		ret.put(settingsNameAttribute,toGet.getSettingName());

		return ret;
	}


	public void setMemberType(String memberType) {
		this.memberType = memberType;
	}


	public String getServerType() {
		return serverType;
	}

	public void setServerType(String serverType) {
		this.serverType = serverType;
	}

	public String getIpAddressType() {
		return ipAddressType;
	}

	public void setIpAddressType(String ipAddressType) {
		this.ipAddressType = ipAddressType;
	}

	public String getServerDomain() {
		return serverDomain;
	}

	public void setServerDomain(String serverDomain) {
		this.serverDomain = serverDomain;
	}

	public PasswordEncoder getPasswordEncoder() {
		return passwordEncoder;
	}

	public void setPasswordEncoder(LdapShaPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public SaltSource getSalt() {
		return salt;
	}


	public String getServerName() {
		return serverName;
	}





	public void setServerName(String serverName) {
		this.serverName = serverName;
	}





	public String getUserName() {
		return userName;
	}





	public void setUserName(String userName) {
		this.userName = userName;
	}





	public String getServerClass() {
		return serverClass;
	}





	public void setServerClass(String serverClass) {
		this.serverClass = serverClass;
	}





	public String getServerPort() {
		return serverPort;
	}





	public void setServerPort(String serverPort) {
		this.serverPort = serverPort;
	}





	public String getUserIdType() {
		return userIdType;
	}





	public void setUserIdType(String userIdType) {
		this.userIdType = userIdType;
	}





	public String getUserAltAccountName() {
		return userAltAccountName;
	}





	public void setUserAltAccountName(String userAltAccountName) {
		this.userAltAccountName = userAltAccountName;
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


	public String getIsAuthAttribute() {
		return isAuthAttribute;
	}





	public void setIsAuthAttribute(String isAuthAttribute) {
		this.isAuthAttribute = isAuthAttribute;
	}





	public String getSettingsClass() {
		return settingsClass;
	}





	public void setSettingsClass(String settingsClass) {
		this.settingsClass = settingsClass;
	}





	public void setSalt(ReflectionSaltSource salt) {
		System.out.println(salt.getClass());
		this.salt = salt;
	}

	@Autowired(required=false)
	private  LdapTemplate ldapTemplate;
	/* Base dn for users */
	private String dnForUsers;
	/* Directory where users are located */
	private String userDirectory;
	/* Type of user in ldap */
	private  String userType=LDAPConstants.USER_TYPE;
	private  String groupType=LDAPConstants.GROUP_ATTRIBUTE_NAME;
	private String memberType=LDAPConstants.MEMBER_ID;
	private final static Logger log= LoggerFactory.getLogger(LDAPUserStore.class);

	@Autowired(required=false)
	private LdapShaPasswordEncoder passwordEncoder;
	@Autowired(required=false)
	private ReflectionSaltSource salt;



	private String userAltAccountType=LDAPConstants.ALT_ACCOUNT_TYPE;

	private String mailAttribute=LDAPConstants.EMAIL_ATTRIBUTE;

	private String userNameType=LDAPConstants.ALT_ACCOUNT_USER_NAME;

	private String serverName=LDAPConstants.ASSOCIATED_DOMAIN_ATTRIBUTE;

	private String userName=LDAPConstants.FIRST_NAME_ATTRIBUTE;

	private String settingsClass=LDAPConstants.SETTING_CLASS;

	private String serverClass=LDAPConstants.SERVER_TYPE;


	private String serverType=LDAPConstants.MAIL_SERVER_TYPE;

	private String ipAddressType=LDAPConstants.IP_ADDRESS_ATTRIBUTE;

	private String serverDomain=LDAPConstants.ASSOCIATED_DOMAIN_ATTRIBUTE;

	private String serverPort=LDAPConstants.SERVER_LISTENING_PORT;

	private String userIdType=LDAPConstants.USER_NAME_ATTRIBUTE;

	private String userAltAccountName=LDAPConstants.OTHER_ACCOUNT_NAME;

	private String isAuthAttribute=LDAPConstants.SERVER_IS_AUTH;

	private String settingsNameAttribute=LDAPConstants.SETTINGS_NAME;


	private String settingValueAttribute=LDAPConstants.SETTING_VAL;

	private String settingUserAttribute=LDAPConstants.SETTING_USER;
}//end LDAPUserStore
