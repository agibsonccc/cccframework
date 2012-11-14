package com.ccc.users.db.client;

import com.ccc.users.core.client.BaseUserClient;
import com.ccc.users.core.store.UserStore;
import com.ccc.users.db.store.DBUserStore;
/**
 * This is a database user client.
 * @author Adam Gibson
 *
 */
public class DBUserClient extends BaseUserClient  {
	/**
	 * BaseUserClient implements Serializable
	 */
	private static final long serialVersionUID = 4823169881087565617L;

	public DBUserClient() {
		super();
	}

	public DBUserClient(UserStore store) {
		super(store);
		if(!(store instanceof DBUserStore))
			throw new IllegalArgumentException("Wrong type of user store: must be of type database store.");
	}

	@Override
	public boolean isAuth(String userName, String password) {
		DBUserStore b=(DBUserStore) userStore;
		return b.isAuth(userName, password);
	}

}//end DBUserClient
