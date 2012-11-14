package com.ccc.users.remoting.service.impl;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;
import com.ccc.users.core.store.UserStore;
import com.ccc.users.remoting.service.UserClientRemote;

public abstract class BaseUserClientRemote implements UserClientRemote {
	public BaseUserClientRemote(UserStore store) {
		userStore=store;
	}
	public BaseUserClientRemote() {
	}
	@Override
	public List<BasicUser> usersWithSetting(String settingName) throws RemoteException {
		List<UserSettings> settings=userStore.allSettings();
		List<BasicUser> ret = new ArrayList<BasicUser>();
		for(UserSettings set : settings) {
			if(set.getSettingName().equals(settingName)) {
				String userName=set.getUserName();
				BasicUser user=userForName(userName);
				ret.add(user);
			}
		}
		
		return ret;
	}



	

	public UserStore getUserStore() {
		return userStore;
	}
	public void setUserStore(UserStore userStore) {
		this.userStore = userStore;
	}
	public SessionRegistry getSessionRegistry() {
		return sessionRegistry;
	}
	public void setSessionRegistry(SessionRegistry sessionRegistry) {
		this.sessionRegistry = sessionRegistry;
	}

	/**
	 * @return a List<BasicUser> of all users who are currently online
	 */
	public List<BasicUser> activeSessions() throws RemoteException {
		List<BasicUser> userList = new LinkedList<BasicUser>();
		List<Object> principals=sessionRegistry.getAllPrincipals();
		//Get all active principles from the session registry
		for(Object p: principals) {
			//Cast to basic user and add to list
			//if(p instanceof UserDetails){
				BasicUser u=userForName((String)p);
				if(u!=null)
					userList.add(u);
			//}
		}
		return userList;
	}//end usersWithActiveSessions

	public List<UserSettings> allSettings() throws RemoteException {
		return userStore.allSettings();
	}

	@Override
	public Map<String, String> settingsMap(UserSettings settings) throws RemoteException {
		if(settings==null) return null;
		
		return Collections.singletonMap(settings.getSettingName(),settings.getSettingVal());
	}
	@Override
	public Map<String, String> settingsMap(Collection<UserSettings> settings)  throws RemoteException {
		if(settings==null || settings.isEmpty())
			return Collections.emptyMap();
		Map<String,String> ret = new HashMap<String,String> (settings.size());
		for(UserSettings s : settings) {
			ret.put(s.getSettingName(),s.getSettingVal());
		}
		
		return ret;
	}//end settingsMap
	@Override
	public boolean userOnline(String userName) throws RemoteException {
		if(userName==null || userName.isEmpty())
			return false;
		List<BasicUser> active=activeSessions();
		for(BasicUser u : active)
			if(u.getUsername().equals(userName))
				return true;

				return false;
	}//end userOnline
	@Override
	public List<BasicUser> retrieveUsers() throws RemoteException {
		return userStore.retrieveUsers();
	}
	@Override
	public BasicUser userForName(String userName) throws RemoteException  {
		return userStore.userForName(userName);
	}
	@Override
	public void addUser(BasicUser user)throws RemoteException  {
		userStore.addUser(user);
	}
	@Override
	public void deleteUser(String userName) throws RemoteException {
		userStore.deleteUser(userName);
	}
	@Override
	public void modifyUser(BasicUser u) throws RemoteException {
		userStore.modifyUser(u);
	}

	@Override
	public void deleteGroup(String group) throws RemoteException  {
		userStore.deleteGroup(group);
	}
	@Override
	public void addUserToGroup(BasicUser toAdd, String addTo)throws RemoteException  {
		userStore.addUserToGroup(toAdd, addTo);
	}
	@Override
	public void deleteUserFromGroup(BasicUser todDelete, String deleteFrom) throws RemoteException {

		userStore.deleteUserFromGroup(todDelete, deleteFrom);
	}
	@Override
	public List<String> allGroups() throws RemoteException {
		return userStore.allGroups();
	}
	@Override
	public List<BasicUser> usersInGroup(String g) throws RemoteException {
		return userStore.usersInGroup(g);
	}
	@Override
	public List<UserContact> contactsForUser(BasicUser u)  throws RemoteException {
		return userStore.contactsForUser(u);
	}
	@Override
	public void addContact(UserContact toAdd) throws RemoteException {
		userStore.addContact(toAdd);

	}
	@Override
	public void removeContact(UserContact toDelete)throws RemoteException  {
		userStore.removeContact(toDelete);
	}
	@Override
	public void updateContact(UserContact toUpdate) throws RemoteException {
		userStore.updateContact(toUpdate);
	}

	@Override
	public void changePassword(String userName, String password)throws RemoteException  {
		userStore.changePassword(userName,password);
	}


	
	@Override
	public void bootUser(String userName) throws RemoteException {
		if(userName==null || userName.isEmpty()) return;

		List<SessionInformation> infoForUser=sessionRegistry.getAllSessions(userName, true);
		if(infoForUser==null) return;
		
		for(SessionInformation info : infoForUser) {
			info.expireNow();
			sessionRegistry.removeSessionInformation(info.getSessionId());
			
		}
	}

	
	@Override
	public List<UserSettings> settingsForUser(String userName)throws RemoteException  {
		return userStore.settingsForUser(userName);
	}
	@Override
	public void addUserSetting(UserSettings toAdd) throws RemoteException {
		userStore.addUserSetting(toAdd);
	}
	@Override
	public void deleteUserSettings(UserSettings toDelete) throws RemoteException {
		userStore.deleteUserSettings(toDelete);
	}
	@Override
	public void updateUserSettings(UserSettings toUpdate) throws RemoteException {
		userStore.updateUserSettings(toUpdate);
	}
	@Autowired(required=false)
	protected UserStore userStore;
	
	@Autowired(required=false)
	protected SessionRegistry sessionRegistry;

}
