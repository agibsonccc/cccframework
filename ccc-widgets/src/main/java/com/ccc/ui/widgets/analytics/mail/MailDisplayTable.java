package com.ccc.ui.widgets.analytics.mail;

import com.ccc.ui.widgets.analytics.DisplayTable;
import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component("mailDisplayTable")
@Scope("session")
public class MailDisplayTable extends DisplayTable {
	@PostConstruct
	public void init() {
		addContainerProperty("Message",String.class,null);
		addContainerProperty("Views",String.class,null);
		addContainerProperty("Links",Integer.class,null);
		addContainerProperty("Change in subscribers",Integer.class,null);
	}
}
