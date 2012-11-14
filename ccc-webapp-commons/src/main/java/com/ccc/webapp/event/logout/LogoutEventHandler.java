package com.ccc.webapp.event.logout;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccc.users.core.client.UserClient;
import com.ccc.webapp.event.api.WebappEvent;
import com.ccc.webapp.event.api.WebappEventHandler;
import com.ccc.webapp.event.constants.WebappConstants;

/**
 * This will handle logouts for a given user client and mail client.
 * @author Adam Gibson
 *
 */
//@Component("logoutEventHandler")
public class LogoutEventHandler implements WebappEventHandler,WebappConstants {

	public void handleEvent(WebappEvent event) throws Exception {
		String userName=(String) event.getParam(USER_NAME_KEY);


	}


	@Autowired(required=false)
	private UserClient userClient;
}
