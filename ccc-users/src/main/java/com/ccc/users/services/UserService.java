package com.ccc.users.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.ccc.user.dao.UserContactManager;
import com.ccc.user.dao.UserSettingsManager;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserGroup;
import com.ccc.users.core.UserSettings;
import com.ccc.users.db.store.UserGroupManager;
import com.ccc.users.db.store.UserManager;
import com.ccc.util.collections.Converter;


@Service("userService")
public class UserService implements UserDetailsService, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7911870495919119259L;
	/**
	 * This will register a user with the mail system and the login system
	 * @param toRegister the user to register
	 * @return whether the user was successfully registered
	 */
	public boolean registerUser(BasicUser toRegister){
		if(userAlreadyExists(toRegister))
			return false;
		else {
			return doRegisterUser(toRegister);
		}
	}//end registerUser

	/**
	 * This will return all the settings in the databas.
	 * @return all the settings in the database.
	 */
	public List<UserSettings> allSettings() {
		return userSettingsManager.allElements();
	}

	private boolean  doRegisterUser(BasicUser user){

		user.setEnabled(true);
		userManager.saveE(user);

		return true;
	}//end doRegisterUser



	/**
	 * This will update the user given user contact in the database.
	 * @param toUpdate the user contact to update
	 */
	public void updateContact(UserContact toUpdate) {
		userContactManager.updateE(toUpdate);
	}//end updateContact


	/**
	 * This will return a list of contacts of the given user
	 * @param u the username of the user to get contacts for
	 * @return the list of contacts for the given user
	 */
	public List<UserContact> contactsForUser(BasicUser u) {
		return userContactManager.contactsForUser(u.getUsername());
	}//end contactsForUser
	/**
	 * This checks if the given user account exists or not.
	 * @param userName the user name to check if the account exists.
	 * @return true if the user was found, false otherwise
	 */
	public boolean userAccountExists(String userName){
		return userManager.userWithName(userName)!=null;
	}

	public List<String> allGroups() {
		List<BasicUser> users=userManager.allElements();

		Set<String> groupNames = new HashSet<String>();

		for(BasicUser u : users) {
			if(u.getGroups()==null || u.getGroups().length() < 1)
				continue;
			String[] groups=u.getGroups().split(",");

			for(String s : groups)
				groupNames.add(s);
		}
		return new Converter<String>().setToList(groupNames);
	}

	
	/**
	 * This adds the given user to the given group
	 * @param toAdd the user to add to the group
	 * @param addTo the group being added to
	 */
	public void addUserToGroup(BasicUser toAdd,String addTo) {
		Assert.notNull(toAdd);
		Assert.notNull(addTo);

		addGroup(toAdd,addTo);
		userManager.updateE(toAdd);


	}//end addUserToGroup

	private void addGroup(BasicUser u,String toAdd) {
		if(u==null || toAdd==null || toAdd.isEmpty()) {
			log.warn("Attempted to add null user or group");
			return;
		}

		StringBuffer sb = new StringBuffer();
		String groups=u.getGroups();
		if(groups!=null && !groups.isEmpty()) {
			sb.append(u.getGroups());
			sb.append(",");
		}
		sb.append(toAdd);
		u.setGroups(sb.toString());
	}

	/**
	 * This will update the given user in the database
	 * @param u the user to update
	 * @throws IllegalArgumentException if u is null
	 */
	public void updateUser(BasicUser u) throws IllegalArgumentException {
		Assert.notNull(u);
		userManager.updateE(u);
	}//end updateUser


	/**
	 * This will add the given user to the database.
	 * @param u the user to add
	 * @throws IllegalArgumentException if the user is null
	 */
	public void addUser(BasicUser u) throws IllegalArgumentException  {
		Assert.notNull(u);
		userManager.saveE(u);
	}
	/**
	 * This returns the users with the given name.
	 * @param name the name of the user to retrieve.
	 * @return the user with the given name, null otherwise
	 */
	public BasicUser userWithName(String name) {
		List<BasicUser> users=userManager.elementsWithValue("user_name", name);

		if(users==null || users.isEmpty())
			return null;

		return users.get(0);
	}//end userWithName

	@Override
	public UserDetails loadUserByUsername(String userName)
			throws UsernameNotFoundException, DataAccessException {
		return userWithName(userName);
	}
	/**
	 * This will remove the given user from the given group.
	 * @param toRemove the user to remove
	 * @param removeFrom the group to remove the user from
	 */
	public void removeUserFromGroup(BasicUser toRemove,String removeFrom) {
		Assert.notNull(toRemove);
		Assert.notNull(removeFrom);

		removeGroup(toRemove,removeFrom);
		UserGroup u = new UserGroup();
		u.setGroupName(removeFrom);
		u.setUserName(toRemove.getUsername());
		userGroupManager.deleteE(u);
		userManager.updateE(toRemove);
	}//end removeUserFromGroup


	private void removeGroup(BasicUser u, String groupName) {
		//Split up the string
		String[] group=u.getGroups().split(",");
		StringBuffer sb = new StringBuffer();
		//Search for the group name, and if it is found remove it
		for(int i=0;i<group.length;i++) {
			String s=group[i];
			if(!s.equals(groupName)) {
				sb.append(s);
				if( i < group.length)
					sb.append(",")	;
			}
		}
		u.setGroups(sb.toString());
	}//end removeGroup
	/**
	 * This returns a list of users in the given group.
	 * @param groupName the group name to get the users for
	 * @return the users in the given group.
	 */
	public List<BasicUser> usersInGroup(String groupName) {
		List<BasicUser> ret = new ArrayList<BasicUser>();
		List<BasicUser> users=allUsers();
		for(BasicUser user : users) {
			String groups=user.getGroups();
			if(groups==null || groups.isEmpty()) continue;
			String[] groupSplit=groups.split(",");
			for(String g : groupSplit)
				if(g.equals(groupName))
					ret.add(user);
		}
		return ret;
	}//end usersInGroup


	/**
	 * This will delete the group with the given name.
	 * @param group the name of the group to delete
	 * @throws IllegalArgumentException if the group didn't exist
	 */
	public void deleteGroup(String group) throws IllegalArgumentException {
		List<UserGroup> userGroups=userGroupManager.allElements();
		for(UserGroup u : userGroups) {
			if(u.getGroupName().equals(group))
				userGroupManager.deleteE(u);
		}

	}//end deleteGroup
	/**
	 * This returns all of the users in the database.
	 * @return all of the users in the database.
	 */
	public List<BasicUser> allUsers() {
		return userManager.allElements();
	}

	/**
	 * This checks to see if the user is already in the database.
	 * @param toCheck the user to check for existence
	 * @return true if the user exists, false otherwise
	 */
	public boolean userAlreadyExists(BasicUser toCheck){
		return userManager.elementsWithValue("user_name",toCheck.getUsername() )!=null;
	}//end userAlreadyExists
	/**
	 * This will delete a user from the database.
	 * @param toDelete the user to delete
	 * @throws IllegalArgumentException if the given user is null
	 */
	public void deleteUser(BasicUser toDelete) throws IllegalArgumentException {
		Assert.notNull(toDelete);
		userManager.deleteE(toDelete);
	}//end deleteUser

	/**
	 * This will delete the given contact.
	 * @param toDelete the contact to delete
	 * @throws IllegalArgumentException if toDelete is null
	 */
	public void deleteContact(UserContact toDelete) {
		Assert.notNull(toDelete);
		userContactManager.deleteE(toDelete);
	}//end deleteContact
	/**
	 * This will add the given contact to the database.
	 * @param toAdd the contact to add
	 * @throws IllegalArgumentException if toAdd is null
	 */
	public void addContact(UserContact toAdd) {
		Assert.notNull(toAdd);
		userContactManager.saveE(toAdd);
	}//end addContact
	


	/**
	 * This will return the settings for a given user
	 * @param userName the name of the user to retrieve settings for
	 * @return the settings for a given user
	 */
	public List<UserSettings> settingsForUserName(String userName) {
		Assert.notNull(userName);
		Assert.hasLength(userName);

		return userSettingsManager.elementsWithValue("user_name", userName);

	}
	/**
	 * This will update the given setting
	 * @param toUpdate the setting to update
	 */
	public void updateSetting(UserSettings toUpdate) {
		userSettingsManager.updateE(toUpdate);
	}
	/**
	 * This will add the given setting to the database.
	 * @param toAdd the setting to add
	 */
	public void addSetting(UserSettings toAdd) {
		userSettingsManager.saveE(toAdd);
	}
	/**
	 * This will delete the given setting from the database.
	 * @param setting the setting to delete
	 */
	public void deleteSetting(UserSettings setting) {
		Assert.notNull(setting);
		userSettingsManager.deleteE(setting);
	}

	
	public void setUserManager(UserManager userManager) {
		this.userManager = userManager;
	}

	public UserManager getUserManager() {
		return userManager;
	}

	
	public UserGroupManager getUserGroupManager() {
		return userGroupManager;
	}
	public void setUserGroupManager(UserGroupManager userGroupManager) {
		this.userGroupManager = userGroupManager;
	}

	public UserContactManager getUserContactManager() {
		return userContactManager;
	}
	public void setUserContactManager(UserContactManager userContactManager) {
		this.userContactManager = userContactManager;
	}




	public UserSettingsManager getUserSettingsManager() {
		return userSettingsManager;
	}


	public void setUserSettingsManager(UserSettingsManager userSettingsManager) {
		this.userSettingsManager = userSettingsManager;
	}


	@Autowired(required=false)
	private UserManager userManager;

	@Autowired(required=false)
	private UserGroupManager userGroupManager;
	
	@Autowired(required=false)
	private UserContactManager userContactManager;
	@Autowired(required=false)
	private UserSettingsManager userSettingsManager;
	
	@SuppressWarnings("unused")
	@Autowired
	private SessionFactory sessionFactory;
	private static Logger log=LoggerFactory.getLogger(UserService.class);
}//end UserService
