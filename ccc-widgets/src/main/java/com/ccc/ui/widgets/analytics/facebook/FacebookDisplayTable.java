package com.ccc.ui.widgets.analytics.facebook;
import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ccc.ui.widgets.analytics.DisplayTable;
@Component("facebookDisplayTable")
@Scope("session")
public class FacebookDisplayTable extends DisplayTable {
	@PostConstruct
	public void init() {
		addContainerProperty("Message", String.class, null);
		addContainerProperty("Likes",Integer.class,null);
		addContainerProperty("Comments",Integer.class,null);
		addContainerProperty("Change in lieks",Integer.class,null);
		
	}
}
