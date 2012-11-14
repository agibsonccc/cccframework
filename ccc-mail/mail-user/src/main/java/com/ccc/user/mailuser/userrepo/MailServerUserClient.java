package com.ccc.user.mailuser.userrepo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.james.domainlist.api.DomainList;
import org.apache.james.domainlist.api.DomainListException;
import org.apache.james.lifecycle.api.Configurable;
import org.apache.james.lifecycle.api.LogEnabled;
import org.apache.james.user.api.UsersRepository;
import org.apache.james.user.api.UsersRepositoryException;
import org.apache.james.user.api.model.User;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.users.core.BasicUser;
import com.ccc.users.core.store.UserStore;
import com.ccc.users.db.client.DBUserClient;
/**
 * A custom hook for a user client managed james user repository.
 * Any james specific code comes from @{org.apache.james.user.lib.AbstractUsersRepository}
 * which is liscenced under:
 * /****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one   *
 * or more contributor license agreements.  See the NOTICE file *
 * distributed with this work for additional information        *
 * regarding copyright ownership.  The ASF licenses this file   *
 * to you under the Apache License, Version 2.0 (the            *
 * "License"); you may not use this file except in compliance   *
 * with the License.  You may obtain a copy of the License at   *
 *                                                              *
 *   http://www.apache.org/licenses/LICENSE-2.0                 *
 *                                                              *
 * Unless required by applicable law or agreed to in writing,   *
 * software distributed under the License is distributed on an  *
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY       *
 * KIND, either express or implied.  See the License for the    *
 * specific language governing permissions and limitations      *
 * under the License.                                           *
 ****************************************************************
 * @author Adam Gibson
 *
 */
public class MailServerUserClient extends DBUserClient implements   UsersRepository,LogEnabled, Configurable {






	@Override
	public void addUser(String userName, String password)
			throws UsersRepositoryException {
		BasicUser user = userForName(userName);
		if(user!=null) {
			throw new UsersRepositoryException("User already exists");
		}
		user = new BasicUser();
		user.setCredentialsExpired(false);
		user.setEnabled(true);
		user.setUserName(userName);
		user.setPassword(password);
		addUser(user);

	}

	@Override
	public boolean contains(String userName) throws UsersRepositoryException {

		return userForName(userName)!=null;
	}

	@Override
	public int countUsers() throws UsersRepositoryException {
		return retrieveUsers().size();
	}

	@Override
	public User getUserByName(String userName) throws UsersRepositoryException {
		final BasicUser user=userForName(userName);

		if(user!=null) {
			User ret = new User(){



				@Override
				public String getUserName() {
					return user.getUsername();
				}

				@Override
				public boolean setPassword(String password) {
					// TODO Auto-generated method stub
					return false;
				}

				@Override
				public boolean verifyPassword(String password) {
					return isAuth(user.getUsername(),password);
				}



			};
			return ret;
		}
		return null;

	}

	@Override
	public Iterator<String> list() throws UsersRepositoryException {
		List<BasicUser> users=retrieveUsers();
		List<String> names = new ArrayList<String>();
		for(BasicUser u : users) names.add(u.getUsername());
		return names.iterator();
	}

	@Override
	public void removeUser(String userName) throws UsersRepositoryException {
		deleteUser(userName);
	}

	@Override
	public boolean supportVirtualHosting() throws UsersRepositoryException {
		return virtualHosting;
	}

	@Override
	public boolean test(String userName, String password)
			throws UsersRepositoryException {
		return isAuth(userName,password);
	}

	@Override
	public void updateUser(User user) throws UsersRepositoryException {

	}

	private DomainList domainList;
	private boolean virtualHosting;
	private Logger logger;

	protected Logger getLogger() {
		return logger;
	}

	/**
	 * @see
	 * org.apache.james.lifecycle.api.Configurable#configure(org.apache.commons.configuration.HierarchicalConfiguration)
	 */
	public void configure(HierarchicalConfiguration configuration) throws ConfigurationException {

		virtualHosting = configuration.getBoolean("enableVirtualHosting", false);

		doConfigure(configuration);
	}

	protected void doConfigure(HierarchicalConfiguration config) throws ConfigurationException {

	}

	public void setEnableVirtualHosting(boolean virtualHosting) {
		this.virtualHosting = virtualHosting;
	}

	@Resource(name = "domainlist")
	public void setDomainList(DomainList domainList) {
		this.domainList = domainList;
	}

	protected void isValidUsername(String username) throws UsersRepositoryException {
		int i = username.indexOf("@");
		if (supportVirtualHosting()) {
			// need a @ in the username
			if (i == -1) {
				throw new UsersRepositoryException("Given Username needs to contain a @domainpart");
			} else {
				String domain = username.substring(i + 1);
				try {
					if (domainList.containsDomain(domain) == false) {
						throw new UsersRepositoryException("Domain does not exist in DomainList");
					} else {
						return;
					}
				} catch (DomainListException e) {
					throw new UsersRepositoryException("Unable to query DomainList", e);
				}
			}
		} else {
			// @ only allowed when virtualhosting is supported
			if (i != -1) {
				throw new UsersRepositoryException("Given Username contains a @domainpart but virtualhosting support is disabled");
			}
		}
	}

	@Override
	public void setLog(Logger logger) {
		this.logger=logger;

	}


	

	@Autowired
	public static UserStore userStore;



}
