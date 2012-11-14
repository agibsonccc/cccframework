package com.ccc.users.db.store;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.util.Assert;

import com.ccc.security.MyDaoAuth;
import com.ccc.security.MyPasswordEncoder;
import com.ccc.users.core.BasicUser;
import com.ccc.users.core.UserContact;
import com.ccc.users.core.UserSettings;
import com.ccc.users.core.store.UserStore;

import com.ccc.users.services.UserService;

/**
 * This is a user store for databases.
 *  
 * @author Adam Gibson
 *
 */

public class DBUserStore implements UserStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4869179477321654527L;
	@Override
	public List<BasicUser> retrieveUsers() {
		return userService.allUsers();
	}
	@Override
	public List<UserSettings> allSettings() {
		return userService.allSettings();
	}


	/**
	 * This returns a list of all the groups in the user store.
	 */
	public List<String> allGroups() {
		return userService.allGroups();
	}
	@Override
	public void addUser(BasicUser user) {
		String newPass=encoder.encodePassword(user.getPassword(),salt.getSalt(user));

		user.setPassword(newPass);
		userService.addUser(user);
	}
	@Override
	public BasicUser userForName(String userName) {
		return userService.userWithName(userName);
	}
	@Override
	public void deleteUser(String userName) {
		userService.deleteUser(userForName(userName));
	}

	@Override
	public List<BasicUser> usersInGroup(String g) {
		return userService.usersInGroup(g);
	}
	@Override
	public void deleteGroup(String group) {
		Assert.notNull(group);
		userService.deleteGroup(group);
	}
	@Override
	public void modifyUser(BasicUser u) {
		Assert.notNull(u);
		userService.updateUser(u);
	}
	@Override
	public List<BasicUser> usersWithRole(String role) {
		return userService.usersInGroup(role);
	}
	@Override
	public void addUserToGroup(BasicUser toAdd, String addTo) {
		Assert.notNull(toAdd);
		Assert.notNull(addTo);

		userService.addUserToGroup(toAdd, addTo);
	}
	@Override
	public void deleteUserFromGroup(BasicUser toDelete, String deleteFrom) {
		userService.removeUserFromGroup(toDelete, deleteFrom);
	}

	@Override
	public void changePassword(String userName, String password) {
		BasicUser u=userForName(userName);
		String enc=encoder.encodePassword(password, salt.getSalt(u));
		u.setPassword(enc);
		userService.updateUser(u);
	}



	public boolean isAuth(String user,String password) {
		BasicUser u=userForName(user);
		if(u==null) {
			if(log.isDebugEnabled())
				log.debug("No user " + user + " found");
			return false;
		}
		String encrypted=u.getPassword();
		Assert.isTrue(auth.isAuth(user, password)==(encoder.isPasswordValid(encrypted, password, salt.getSalt(u))));
		return auth.isAuth(user, password);
	}
	public MyDaoAuth getAuth() {
		return auth;
	}
	public void setAuth(MyDaoAuth auth) {
		this.auth = auth;
	}
	public UserService getUserService() {
		return userService;
	}
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	
	@Override
	public List<UserContact> contactsForUser(BasicUser u) {
		return userService.contactsForUser(u);
	}
	@Override
	public void addContact(UserContact toAdd) {
		userService.addContact(toAdd);
	}
	@Override
	public List<UserSettings> settingsForUser(String userName) {
		return userService.settingsForUserName(userName);
	}

	@Override
	public void addUserSetting(UserSettings toAdd) {
		if(toAdd.getId()==null || toAdd.getId().isEmpty())
			toAdd.setId(generateId(toAdd));

		userService.addSetting(toAdd);
	}

	private String generateId(UserSettings s) {
		if(s==null) return null;
		String userName=s.getUserName();
		String val=s.getSettingVal();
		String name=s.getSettingName();
		String[] s1={userName,val,name};
		StringBuffer sb = new StringBuffer();
		java.util.Random rgen=  new java.util.Random();
		for(String s2 : s1) {
			if(s2==null)
				s2="";
			int i=-1;
			int j=-2;
			int count=0;
			do {
				i=rgen.nextInt(s2.length() < 1 ? 1 : s2.length());
				j=rgen.nextInt(s2.length()< 1 ? 1 : s2.length());
				count++;
			}while(i==j || count<3);
			sb.append(s2.charAt(i));
			sb.append(s2.charAt(j));
			

		}
		
		List<Character> chars = new ArrayList<Character>();
		for(char c : sb.toString().toCharArray()) {
			chars.add(c);
		}
		Collections.shuffle(chars);
		
		StringBuffer sb2 = new StringBuffer();
		
		for(char c : chars) sb2.append(c);
				
		return sb2.toString();
	}	

	@Override
	public void deleteUserSettings(UserSettings toDelete) {
		userService.deleteSetting(toDelete);
	}

	@Override
	public void updateUserSettings(UserSettings toUpdate) {
		userService.updateSetting(toUpdate);
	}

	@Override
	public void updateContact(UserContact toUpdate) {
		userService.updateContact(toUpdate);
	}
	@Override
	public void removeContact(UserContact toDelete) {
		userService.deleteContact(toDelete);
	}
	public PasswordEncoder getEncoder() {
		return encoder;
	}
	public void setEncoder(MyPasswordEncoder encoder) {
		this.encoder = encoder;
	}
	public SaltSource getSalt() {
		return salt;
	}
	public void setSalt(SaltSource salt) {
		this.salt = salt;
	}

	@Autowired(required=false)
	private MyDaoAuth auth;
	@Autowired(required=false)
	private UserService userService;
	@Autowired(required=false)
	private MyPasswordEncoder encoder;
	@Autowired(required=false)
	private SaltSource salt;
	private static Logger log=LoggerFactory.getLogger(DBUserStore.class);



}//end DBUserStore
