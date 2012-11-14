package com.ccc.ui.widgets.analytics;

import com.vaadin.ui.Table;

public class DisplayTable extends Table {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public DisplayTable() {
		addInitialColumn();
	}
	
	
	protected void addInitialColumn() {
		addContainerProperty("Click to view message",String.class,null);
	}

}
