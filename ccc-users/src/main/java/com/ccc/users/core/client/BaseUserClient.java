package com.ccc.users.core.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;
import com.ccc.users.core.store.UserStore;


/**
 * This is a base implementation of a user client.
 * Every client will come with a user store which will update the store as necessary.
 * @author Adam Gibson
 *
 */
public abstract class BaseUserClient implements UserClient {
	
	public BaseUserClient(UserStore store) {
		userStore=store;
	}
	
	
	
	

	@Override
	public List<BasicUser> usersWithSetting(String settingName) {
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



	/**
	 * This allows for setter based injection of a user store.
	 */
	public BaseUserClient() {}

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
	public List<BasicUser> activeSessions() {
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

	public List<UserSettings> allSettings() {
		return userStore.allSettings();
	}

	@Override
	public Map<String, String> settingsMap(UserSettings settings) {
		if(settings==null) return null;
		
		return Collections.singletonMap(settings.getSettingName(),settings.getSettingVal());
	}
	@Override
	public Map<String, String> settingsMap(Collection<UserSettings> settings) {
		if(settings==null || settings.isEmpty())
			return Collections.emptyMap();
		Map<String,String> ret = new HashMap<String,String> ();
		for(UserSettings s : settings) {
			if(s==null)
				continue;
			String settingName=s.getSettingName(),settingVal=s.getSettingVal();
			if(settingName==null || settingVal==null) {
				log.error("Won't add setting, setting name or value was null");
				continue;
			}
			ret.put(s.getSettingName(),s.getSettingVal());
		}
		
		return ret;
	}//end settingsMap
	@Override
	public boolean userOnline(String userName) {
		if(userName==null || userName.isEmpty())
			return false;
		List<BasicUser> active=activeSessions();
		for(BasicUser u : active)
			if(u.getUsername().equals(userName))
				return true;

				return false;
	}//end userOnline
	@Override
	public List<BasicUser> retrieveUsers() {
		return userStore.retrieveUsers();
	}
	@Override
	public BasicUser userForName(String userName) {
		return userStore.userForName(userName);
	}
	@Override
	public void addUser(BasicUser user) {
		userStore.addUser(user);
	}
	@Override
	public void deleteUser(String userName) {
		userStore.deleteUser(userName);
	}
	@Override
	public void modifyUser(BasicUser u) {
		userStore.modifyUser(u);
	}

	@Override
	public void deleteGroup(String group) {
		userStore.deleteGroup(group);
	}
	@Override
	public void addUserToGroup(BasicUser toAdd, String addTo) {
		userStore.addUserToGroup(toAdd, addTo);
	}
	@Override
	public void deleteUserFromGroup(BasicUser todDelete, String deleteFrom) {

		userStore.deleteUserFromGroup(todDelete, deleteFrom);
	}
	@Override
	public List<String> allGroups() {
		return userStore.allGroups();
	}
	@Override
	public List<BasicUser> usersInGroup(String g) {
		return userStore.usersInGroup(g);
	}
	@Override
	public List<UserContact> contactsForUser(BasicUser u) {
		return userStore.contactsForUser(u);
	}
	@Override
	public void addContact(UserContact toAdd) {
		userStore.addContact(toAdd);

	}
	@Override
	public void removeContact(UserContact toDelete) {
		userStore.removeContact(toDelete);
	}
	@Override
	public void updateContact(UserContact toUpdate) {
		userStore.updateContact(toUpdate);
	}

	@Override
	public void changePassword(String userName, String password) {
		userStore.changePassword(userName,password);
	}


	

	@Override
	public void bootUser(String userName) {
		if(userName==null || userName.isEmpty()) return;

		List<SessionInformation> infoForUser=sessionRegistry.getAllSessions(userName, true);
		if(infoForUser==null) return;
		
		for(SessionInformation info : infoForUser) {
			info.expireNow();
			sessionRegistry.removeSessionInformation(info.getSessionId());
			
		}
	}
	

	
	@Override
	public List<UserSettings> settingsForUser(String userName) {
		return userStore.settingsForUser(userName);
	}
	@Override
	public void addUserSetting(UserSettings toAdd) {
		if(toAdd.getId()==null)
			toAdd.setId(UUID.randomUUID().toString());
		userStore.addUserSetting(toAdd);
	}
	@Override
	public void deleteUserSettings(UserSettings toDelete) {
		userStore.deleteUserSettings(toDelete);
	}
	@Override
	public void updateUserSettings(UserSettings toUpdate) {
		userStore.updateUserSettings(toUpdate);
	}
	@Autowired(required=false)
	protected UserStore userStore;
	
	@Autowired(required=false)
	protected SessionRegistry sessionRegistry;
	
	private static Logger log=LoggerFactory.getLogger(BaseUserClient.class);
	
	private static final long serialVersionUID = 3142038382055543643L;

}//end BaseUserClient
