package com.ccc.users.core.store;

import java.util.List;
import java.io.*;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;

/**
 * A user store is an abstraction for user storage with the assumption of basic operations for manipulating
 * a user store.
 * @author Adam Gibson
 *
 */
public interface UserStore extends Serializable{
	/**
	 * This retrieves a list of users from this user store.
	 * @return the list of users held by this user store.
	 */
	public List<BasicUser> retrieveUsers();


	/**
	 * This retrieves a user from this store with the given user name.
	 * @param userName the user name to retrieve the user for.
	 * @param role the role the user has
	 * @return the user with the given user name, or null if no result.
	 */
	public BasicUser userForName(String userName);

	/**
	 * This will add a basic user to the user store.
	 * @param user the user to add to the store.
	 */
	public void addUser(BasicUser user);

	/**
	 * This deletes the user with the given user name from the store.
	 * @param userName the username of the user to delete.
	 * @param role the role of the user
	 */
	public void deleteUser(String userName);

	/**
	 * This modifies the passed in user within the store.
	 * @param u the user to modify
	 */
	public void modifyUser(BasicUser u);

	/**
	 * This retrieves a list of users with the given role.
	 * @param role the role to retrieve users from
	 * @return a list of users with the given role
	 */ 
	public List<BasicUser> usersWithRole(String role);

	

	/**
	 * This will delete a group from the user store.
	 * This will also wipe all users from that group in the user store.
	 * @param group the group to delete
	 */
	public void deleteGroup(String group);
	
	
	/**
	 * This will add the given user to the given group.
	 * @param toAdd the group to add the user to
	 * @param addTo the group to add the user to
	 */
	public void addUserToGroup(BasicUser toAdd,String addTo);
	
	
	/**
	 * This will delete the given user from the given group.
	 * @param todDelete the user to delete
	 * @param deleteFrom the group to delete the user from
	 */
	public void deleteUserFromGroup(BasicUser todDelete,String deleteFrom);
	

	
	/**
	 * This returns a list of all the groups in the user store.
	 */
	public List<String> allGroups();
	
	
	/**
	 * This returns a list of users in the given group.
	 * @param g the group to retrieve users for
	 * @return the list of users in that group
	 */
	public List<BasicUser> usersInGroup(String g);

	
	/**
	 * This will return a list of contacts for the given user.
	 * @param u the user to retrieve contacts for
	 * @return the list of contacts for this user
	 */
	public List<UserContact> contactsForUser(BasicUser u);
	
	
	/**
	 * This will add a contact for the given user.
	 * @param toAdd the contact to add
	 */
	public void addContact(UserContact toAdd);
	
	/**
	 * This will remove the given contact from the given user.
	 * @param toDelete the contact to delete
	 */
	public void removeContact(UserContact toDelete);
	
	/**
	 * This will update the given contact 
	 * @param toUpdate the contact to update
	 */
	public void updateContact(UserContact toUpdate);
	
	/**
	 * This will change the password of the given user in the user store
	 * @param userName the name of the user to change the password for
	 * @param password the new password to use
	 */
	public void changePassword(String userName,String password);
	
	
	
	/**
	 * This will return the user settings for a given user name.
	 * @param userName the name of the user to retrieve settings for
	 * @return the settings for the given user
	 */
	public List<UserSettings> settingsForUser(String userName);
	
	/**
	 * This will return all settings from the store.
	 * @return all settings from the store
	 */
	public List<UserSettings> allSettings();
	
	/**
	 * This will add the user settings to the user store.
	 * @param toAdd the user settings to add
	 */
	public void addUserSetting(UserSettings toAdd);
	
	/**
	 * This will delete the given user settings from the user store.
	 * @param toDelete the user settings to delete
	 */
	public void deleteUserSettings(UserSettings toDelete);
	
	/**
	 * This will update the given user settings.
	 * @param toUpdate the setting to update
	 */
	public void updateUserSettings(UserSettings toUpdate);
	
	
}//end UserStore
