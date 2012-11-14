package com.ccc.util.datatables;

import java.lang.reflect.Field;

/**
 * 
 * @author Adam Gibson
 * 
 * See: http://datatables.net/usage/server-side
 *
 */
public class DataTablesConstants {

	/**
	 * string  	sEcho  	An unaltered copy of sEcho sent from the client side. This parameter will change with each draw (it is basically a draw count) - 
	 * so it 
	 * is important that this is implemented. Note that it strongly recommended 
	 * for security reasons that you 'cast' this parameter to an integer in order to prevent Cross Site Scripting (XSS) attacks.
	 */
	public final static String S_ECHO="sEcho";

	public final static String I_TOTAL_RECORDS="iTotalRecords";
	/**
	 * int  	iTotalRecords  	Total records, before filtering (i.e. the total number of records in the database)
	 */
	public final static String I_TOTAL_DISPLAY_RECORDS="iTotalDisplayRecords";

	/**
	 * int  	iColumns  	Number of columns being displayed 
	 * (useful for getting individual column search info)
	 */
	public final static String I_COLUMNS="iColumns";
	/**
	 * int  	iDisplayStart  	Display start point in the current data set.
	 */
	public final static String DISPLAY_START="iDisplayStart";
	/**
	 * int  	iDisplayLength  	Number of records that the table can display in the current draw. 
	 * It is expected that the 
	 * number of records returned will be equal to this number, unless the server has fewer records to return.
	 */
	public final static String DISPLAY_LENGTH="iDisplayLength";
	/**
	 * int  	iSortCol_(int)  	Column being sorted on (you will need to decode this number for your database)
	 */
	public final static String SORT_COL="iSortCol";

	public final static String   B_ESCAPE_REGEX="bEscapeRegex_";
	/**
	 * string  	sSearch  	Global search field
	 */
	public final static String S_SEARCH="sSearch";

	public final static String S_SORT_DIR="sSortDir_";
	/**
	 * array  	aaData  	The data in a 2D array. Note that you can change the name of this parameter with sAjaxDataProp.
	 */
	public final static String AA_DATA="aaData";

	/**
	 * bool  	bSearchable_(int)  	Indicator for if a column is flagged as searchable or not on the client-side
	 * 
	 * Note that you need to append an integer to this in order for it to be a valid parameter.
	 */
	public final static String B_SEARCHABLE="bSearchable_";
	/**
	 * bool  	bRegex  	True if the global filter should be treated as a regular expression for advanced filtering, false if not.
	 */
	public final static String B_REGEX="bRegex";

	/**
	 * string  	sColumns  	Optional - this is a string of column names, comma separated (used in combination with sName) which will allow DataTables 
	 * to reorder data on the client-side if required for display. Note that the number of column names returned must exactly match the number of columns in the table. 
	 * For a more flexible JSON format, 
	 * please consider using mDataProp.
	 */
	public final static String S_COLUMNs="sColumns";

	/**
	 * int  	iSortingCols  	Number of columns to sort on
	 */
	public final static String I_SORTING_COLUMNs="iSortoingColumns";

	/**
	 * This specifies the column headers where necessary.
	 */
	public final static String COLUMN_NAMES="aoColumnDefs";

	/**
	 * This is an attribute that sets the values in the following way:
	 * "aoColumnDefs": [
	07.                    {
	08.                        "mDataProp":    "field1",
	09.                        "aTargets":     [0]
	10.                    },
	11.                    {
	12.                        "mDataProp":    "field2",
	13.                        "aTargets":     [1]
	14.                    }
							...
	15.                ]

	It is index based for setting the values.
	 */
	public final static String COLUMN_INDEX="aTargets";

	/**
	 * This sets the data column header names.
	 */
	public final static String COLUMN_NAME="mDataProp";

	/**
	 * This will return the column names for the given class based on it's attributes.
	 * @param toGet the object to get headers for
	 * @return a json string with the headers for this class
	 */
	public static String getColumnNamesForClass(Object toGet) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		Field[] fields=toGet.getClass().getDeclaredFields();

		for(int i=0;i<fields.length;i++) {
			sb.append('{');
			sb.append("mdataProp:");
			sb.append(fields[i].getName());
			sb.append('}');
			if(i!=fields.length-1)
				sb.append(',');
		}
		sb.append(']');
		return sb.toString();
	}//end getColumnNamesForClass


}
