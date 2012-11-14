package com.ccc.util.strings;

import java.util.Collection;
import java.util.Iterator;

public class CSVUtils {
	/**
	 * Static method for splitting a target string by comma
	 * @param target the target string
	 * @return null if null is passed in otherwise string.split(,)
	 */
	public static String[] splitCsv(String target) {
		if(target==null) return null;
		else return target.split(",");
	}//end splitCsv
	/**
	 * This will append the given string to target in csv format
	 * @param target the target to append to
	 * @param toAppend the string to append
	 * @return toAppend if target is null or 
	 */
	public static String appendTo(String target,String toAppend) {
		if(target==null) return toAppend;
		else {
			return new StringBuilder().append(target).append(",").append(toAppend).toString();
		}
	}//end appendTo
	
	/**
	 * Take an arbitrary collection and return its elements as csv
	 * @param collection the colleciton ton convert
	 * @return a csved string represenation of the specified collection
	 */
	public static String toCSV(Collection collection) {
		StringBuffer sb = new StringBuffer();
		Iterator iter=collection.iterator();
		while(iter.hasNext()) {
			Object o=iter.next();
			sb.append(o.toString());
			if(iter.hasNext()) sb.append(",");
		}
		return sb.toString();
	}//end toCSV
	
	/**
	 * This will turn the given array in to a csv string.
	 * @param objs the array to convert
	 * @return the csv form of this array
	 */
	public static String arrayToCSV(Object[] objs) {
		StringBuffer sb = new StringBuffer();
		
		for(Object o : objs) {
			sb.append(o);
			sb.append(",");
		}
		String ret=sb.toString();
		ret=ret.substring(0, ret.length()-1);
		return ret;
	}
	
}
