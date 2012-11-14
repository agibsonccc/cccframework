package com.ccc.users.remoting.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;

/**
 * Base implementation of a user client meant for use
 * with rmi
 * @author Adam Gibson
 *
 */
public interface UserClientRemote extends Remote {
	/**
	 * This retrieves a list of USERs from this USER store.
	 * @return the list of USERs held by this USER store.
	 */
	public List<BasicUser> retrieveUsers() throws RemoteException;


	/**
	 * This will return all settings in the user store.
	 * @return all of the settings in a user store.
	 */
	public List<UserSettings> allSettings() throws RemoteException;

	/**
	 * This will return a map of key value pairs for the settings based on the given settings.
	 * @param settings the settings to pass in
	 * @return a map with the given key value pair
	 */
	public Map<String,String> settingsMap(UserSettings settings) throws RemoteException;


	/**
	 * This will return a map of key value pairs for the settings based on the given settings.
	 * @param settings the settings to pass in
	 * @return a map with the given key value pair
	 */
	public Map<String,String> settingsMap(Collection<UserSettings> settings) throws RemoteException;


	/**
	 * This will return th
	 * @param settingName
	 * @return
	 */
	public List<BasicUser> usersWithSetting(String settingName) throws RemoteException;

	/**
	 * This retrieves a USER from this store with the given USER name.
	 * @param USERName the USER name to retrieve the USER for.
	 * @param role the role the USER has
	 * @return the USER with the given USER name, or null if no result.
	 */
	public BasicUser userForName(String userName) throws RemoteException;

	/**
	 * This will add a basic USER to the USER store.
	 * @param USER the USER to add to the store.
	 */
	public void addUser(BasicUser user) throws RemoteException;

	/**
	 * This deletes the USER with the given USER name from the store.
	 * @param USERName the USERname of the USER to delete.
	 * @param role the role of the USER
	 */
	public void deleteUser(String userName) throws RemoteException;

	/**
	 * This modifies the passed in USER within the store.
	 * @param u the USER to modify
	 */
	public void modifyUser(BasicUser u) throws RemoteException;



	/**
	 * This will delete a group from the USER store.
	 * This will also wipe all USERs from that group in the USER store.
	 * @param group the group to delete
	 */
	public void deleteGroup(String group) throws RemoteException;


	/**
	 * This will add the given USER to the given group.
	 * @param toAdd the group to add the USER to
	 * @param addTo the group to add the USER to
	 */
	public void addUserToGroup(BasicUser toAdd,String addTo) throws RemoteException;


	/**
	 * This will delete the given USER from the given group.
	 * @param todDelete the USER to delete
	 * @param deleteFrom the group to delete the USER from
	 */

	public void deleteUserFromGroup(BasicUser todDelete,String deleteFrom) throws RemoteException;



	/**
	 * This returns a list of all the groups in the USER store.
	 */

	public List<String> allGroups() throws RemoteException;


	/**
	 * This returns a list of USERs in the given group.
	 * @param g the group to retrieve USERs for
	 * @return the list of USERs in that group
	 */
	public List<BasicUser> usersInGroup(String g) throws RemoteException;


	/**
	 * This will return a list of contacts for the given USER.
	 * @param u the USER to retrieve contacts for
	 * @return the list of contacts for this USER
	 */
	public List<UserContact> contactsForUser(BasicUser u) throws RemoteException;


	/**
	 * This will add a contact for the given USER.
	 * @param toAdd the contact to add
	 */
	public void addContact(UserContact toAdd) throws RemoteException;

	/**
	 * This will remove the given contact from the given USER.
	 * @param toDelete the contact to delete
	 */
	public void removeContact(UserContact toDelete) throws RemoteException;

	/**
	 * This will update the given contact 
	 * @param toUpdate the contact to update
	 */
	public void updateContact(UserContact toUpdate) throws RemoteException;

	/**
	 * This will change the password for the given USER.
	 * @param USERName the name of the USER to change the password for
	 * @param password the new password to use
	 */
	public void changePassword(String userName,String password) throws RemoteException;


	/**
	 * This will return a list of active sessions of(ie all logged in users)
	 * @return a list of active sessions
	 */
	public List<BasicUser> activeSessions() throws RemoteException;
	/**
	 * This returns whether the user with the given name is online currently or not.
	 * @param userName the name of the user to check for
	 * @return if null is passed in or user doesn't exist, false otherwise, true if the 
	 * user is online,false otherwise
	 */
	public boolean userOnline(String userName) throws RemoteException;


	/**
	 * This will return the user settings for a given user name.
	 * @param userName the name of the user to retrieve settings for
	 * @return the settings for the given user
	 */

	public List<UserSettings> settingsForUser(String userName) throws RemoteException;

	/**
	 * This will add the user settings to the user store.
	 * @param toAdd the user settings to add
	 */

	public void addUserSetting(UserSettings toAdd) throws RemoteException;

	/**
	 * This will delete the given user settings from the user store.
	 * @param toDelete the user settings to delete
	 */

	public void deleteUserSettings(UserSettings toDelete) throws RemoteException;

	/**
	 * This will update the given user settings.
	 * @param toUpdate the setting to update
	 */

	public void updateUserSettings(UserSettings toUpdate) throws RemoteException;



	/**
	 * This will force the user offline
	 * @param userName the name of the user to boot
	 */
	public void bootUser(String userName) throws RemoteException;

	/**
	 * Tests authentication on the user with the given username
	 * and password
	 * @param userName the name of the user to check for authentication
	 */
	public boolean isAuth(String userName, String password) throws RemoteException;
}
