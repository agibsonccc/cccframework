package com.ccc.ui.widgets.analytics.twitter;

import com.ccc.ui.widgets.analytics.DisplayTable;
import javax.annotation.PostConstruct;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
@Component("twitterDisplayTable")
@Scope("session")
public class TwitterDisplayTable extends DisplayTable {
	@PostConstruct
	public void init() {
		addContainerProperty("Tweet content",String.class,null);
		addContainerProperty("Retweets",Integer.class,null);
		addContainerProperty("Mentions",Integer.class,null);
		addContainerProperty("Change in Followser",Integer.class,null);
	}
}
