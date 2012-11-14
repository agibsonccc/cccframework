package com.ccc.ui.widgets.analytics;

import com.vaadin.ui.Embedded;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
/**
 * An iframe with a graph and a table below it with search results derived from a range searc.
 * @author Adam Gibson
 *
 */
public abstract class AnalyticsDisplay extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	
	
	public Embedded getGraph() {
		return graph;
	}

	public void setGraph(Embedded graph) {
		this.graph = graph;
	}

	public Table getTable() {
		return table;
	}

	public void setTable(DisplayTable table) {
		this.table = table;
	}

	private Embedded graph;
	
	private DisplayTable table;
	
}
