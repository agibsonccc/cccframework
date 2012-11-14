package com.ccc.util.datatables;

import java.util.HashMap;
import java.util.List;

/**
 * This is a data tables request object meant for receiving requests translated from json.
 * This is only a base object. There are other parameters needed which need to be specified in a subclass
 * depending on the object. These are:
 * 	bool  	bSearchable_(int)  	Indicator for if a column is flagged as searchable or not on the client-side
 * 	string  sSearch_(int)  		Individual column filter
	bool 	bRegex_(int) 		True if the individual column filter should be treated as a regular expression for advanced filtering, false if not
	bool 	bSortable_(int) 	Indicator for if a column is flagged as sortable or not on the client-side
	int  	iSortCol_(int)  	Column being sorted on (you will need to decode this number for your database)
	string 	sSortDir_(int) 		Direction to be sorted - "desc" or "asc".
	string 	mDataProp_(int) 	The value specified by mDataProp for each column. This can be useful for ensuring that the processing of data is independent from the order of the columns.
 * @author Adam Gibson
 *
 */
public class DataTablesRequestObject {

	private List<HashMap<String,String>> objects;

	public List<HashMap<String, String>> getObjects() {
		return objects;
	}
	public void setObjects(List<HashMap<String, String>> objects) {
		this.objects = objects;
	}
	public boolean isbRegex() {
		return bRegex;
	}
	public void setbRegex(boolean bRegex) {
		this.bRegex = bRegex;
	}
	public String getsSearch() {
		return sSearch;
	}
	public void setsSearch(String sSearch) {
		this.sSearch = sSearch;
	}
	public int getiColumns() {
		return iColumns;
	}
	public void setiColumns(int iColumns) {
		this.iColumns = iColumns;
	}
	public int getiDisplayLength() {
		return iDisplayLength;
	}
	public void setiDisplayLength(int iDisplayLength) {
		this.iDisplayLength = iDisplayLength;
	}
	public int getiDisplayStart() {
		return iDisplayStart;
	}
	public void setiDisplayStart(int iDisplayStart) {
		this.iDisplayStart = iDisplayStart;
	}
	public int getiSortingCols() {
		return iSortingCols;
	}
	public void setiSortingCols(int iSortingCols) {
		this.iSortingCols = iSortingCols;
	}
	public String getsEcho() {
		return sEcho;
	}
	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}
	private boolean bRegex;
	private String sSearch;
	private int iColumns;
	private int iDisplayLength;
	private int iDisplayStart;
	private int iSortingCols;
	private String sEcho;
}
