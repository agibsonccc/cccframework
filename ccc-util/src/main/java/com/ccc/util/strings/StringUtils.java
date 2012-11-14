package com.ccc.util.strings;

import java.lang.reflect.Array;

public class StringUtils {

	/**
	 * This will return the beginning num characters of a string
	 * @param base the base string to extract from
	 * @param num the num to extract
	 * @return the num left characters of the string
	 */
	public static String left(String base,int num) {
		//return remaining characters
		if(num >=base.length()) {
			return base;
		}
		else {
			return base.substring(0,num);
		}
	}//end left

	/**
	 * This will return the ending 
	 * @param base
	 * @param num
	 * @return
	 */
	public static String right(String base,int num) {
		//return remaining characters
		if(num >=base.length()) {
			return base;
		}
		else {
			StringBuffer sb = new StringBuffer();
			for(int i=base.length()-1;i>0;i--) {
				sb.append(base.charAt(i));
			}
			return reverse(sb.toString());
		}
	}//end right

	/**
	 * Reverses a given string
	 * Example: 1234 becomes 4321
	 * @param toReverse the string to reverse
	 * @return if toReverse is null or empty, the base string is returned otherwise 
	 * the reverse string is returned
	 */
	public static String reverse(String toReverse) {
		if(toReverse==null || toReverse.isEmpty())
			return toReverse;
		
		else {
			StringBuffer sb = new StringBuffer();
			for(int i=toReverse.length()-1;i>0;i--) {
				sb.append(toReverse.charAt(i));
			}
			return sb.toString();
		}
	}//end reverse
	
	
	/**
	 * Removes all occurrences of a string from another string.
	 *
	 * @param inString the string to remove substrings from.
	 * @param substring the substring to remove.
	 * @return the input string with occurrences of substring removed.
	 */
	public static String removeSubstring(String inString, String substring) {

		StringBuffer result = new StringBuffer();
		int oldLoc = 0, loc = 0;
		while ((loc = inString.indexOf(substring, oldLoc))!= -1) {
			result.append(inString.substring(oldLoc, loc));
			oldLoc = loc + substring.length();
		}
		result.append(inString.substring(oldLoc));
		return result.toString();
	}//end removeSubstring

	/**
	 * This will add the <br /> tag to the original string every
	 * interval of the specified separator
	 * @param original the original string
	 * @param separator the separator to use
	 * @param interval the interval of words to add breaks for
	 * @return a string with line breaks every interval spaces,or 
	 * if any of the given strings are null or empty the original string, or if interval is 
	 * \<=0 the original string
	 */
	public static String addHtmlLineBreaks(String original,String separator,int interval) {
		if(original==null || separator==null || original.isEmpty() || separator.isEmpty() || interval<=0) 
			return original;
		StringBuffer sb = new StringBuffer();
		String[] split=original.split(separator);
		if(split.length <=1)
			return original;
		else {
			for(int i=0;i<split.length;i++) {
				String curr=split[i];
				sb.append(curr);
				if(i%interval==0 && i>=interval)
					sb.append("<br />");
				sb.append(separator);

			}
		}
		return sb.toString();
	}//end addHtmlLineBreaks


	/**
	 * Replaces with a new string, all occurrences of a string from 
	 * another string.
	 *
	 * @param inString the string to replace substrings in.
	 * @param subString the substring to replace.
	 * @param replaceString the replacement substring
	 * @return the input string with occurrences of substring replaced.
	 */
	public static String replaceSubstring(String inString, String subString,
			String replaceString) {

		StringBuffer result = new StringBuffer();
		int oldLoc = 0, loc = 0;
		while ((loc = inString.indexOf(subString, oldLoc))!= -1) {
			result.append(inString.substring(oldLoc, loc));
			result.append(replaceString);
			oldLoc = loc + subString.length();
		}
		result.append(inString.substring(oldLoc));
		return result.toString();
	}


	/**
	 * Pads a string to a specified length, inserting spaces on the left
	 * as required. If the string is too long, characters are removed (from
	 * the right).
	 *
	 * @param inString the input string
	 * @param length the desired length of the output string
	 * @return the output string
	 */
	public static String padLeft(String inString, int length) {

		return fixStringLength(inString, length, false);
	}

	/**
	 * Pads a string to a specified length, inserting spaces on the right
	 * as required. If the string is too long, characters are removed (from
	 * the right).
	 *
	 * @param inString the input string
	 * @param length the desired length of the output string
	 * @return the output string
	 */
	public static String padRight(String inString, int length) {

		return fixStringLength(inString, length, true);
	}

	/**
	 * Pads a string to a specified length, inserting spaces as
	 * required. If the string is too long, characters are removed (from
	 * the right).
	 *
	 * @param inString the input string
	 * @param length the desired length of the output string
	 * @param right true if inserted spaces should be added to the right
	 * @return the output string
	 */
	private static /*@pure@*/ String fixStringLength(String inString, int length,
			boolean right) {

		if (inString.length() < length) {
			while (inString.length() < length) {
				inString = (right ? inString.concat(" ") : " ".concat(inString));
			}
		} else if (inString.length() > length) {
			inString = inString.substring(0, length);
		}
		return inString;
	}

	/**
	 * Rounds a double and converts it into String.
	 *
	 * @param value the double value
	 * @param afterDecimalPoint the (maximum) number of digits permitted
	 * after the decimal point
	 * @return the double as a formatted string
	 */
	public static /*@pure@*/ String doubleToString(double value, int afterDecimalPoint) {

		StringBuffer stringBuffer;
		double temp;
		int dotPosition;
		long precisionValue;

		temp = value * Math.pow(10.0, afterDecimalPoint);
		if (Math.abs(temp) < Long.MAX_VALUE) {
			precisionValue = 	(temp > 0) ? (long)(temp + 0.5) 
					: -(long)(Math.abs(temp) + 0.5);
			if (precisionValue == 0) {
				stringBuffer = new StringBuffer(String.valueOf(0));
			} else {
				stringBuffer = new StringBuffer(String.valueOf(precisionValue));
			}
			if (afterDecimalPoint == 0) {
				return stringBuffer.toString();
			}
			dotPosition = stringBuffer.length() - afterDecimalPoint;
			while (((precisionValue < 0) && (dotPosition < 1)) ||
					(dotPosition < 0)) {
				if (precisionValue < 0) {
					stringBuffer.insert(1, '0');
				} else {
					stringBuffer.insert(0, '0');
				}
				dotPosition++;
			}
			stringBuffer.insert(dotPosition, '.');
			if ((precisionValue < 0) && (stringBuffer.charAt(1) == '.')) {
				stringBuffer.insert(1, '0');
			} else if (stringBuffer.charAt(0) == '.') {
				stringBuffer.insert(0, '0');
			}
			int currentPos = stringBuffer.length() - 1;
			while ((currentPos > dotPosition) &&
					(stringBuffer.charAt(currentPos) == '0')) {
				stringBuffer.setCharAt(currentPos--, ' ');
			}
			if (stringBuffer.charAt(currentPos) == '.') {
				stringBuffer.setCharAt(currentPos, ' ');
			}

			return stringBuffer.toString().trim();
		}
		return new String("" + value);
	}

	/**
	 * Rounds a double and converts it into a formatted decimal-justified String.
	 * Trailing 0's are replaced with spaces.
	 *
	 * @param value the double value
	 * @param width the width of the string
	 * @param afterDecimalPoint the number of digits after the decimal point
	 * @return the double as a formatted string
	 */
	public static /*@pure@*/ String doubleToString(double value, int width,
			int afterDecimalPoint) {

		String tempString = doubleToString(value, afterDecimalPoint);
		char[] result;
		int dotPosition;

		if ((afterDecimalPoint >= width) 
				|| (tempString.indexOf('E') != -1)) { // Protects sci notation
			return tempString;
		}

		// Initialize result
		result = new char[width];
		for (int i = 0; i < result.length; i++) {
			result[i] = ' ';
		}

		if (afterDecimalPoint > 0) {
			// Get position of decimal point and insert decimal point
			dotPosition = tempString.indexOf('.');
			if (dotPosition == -1) {
				dotPosition = tempString.length();
			} else {
				result[width - afterDecimalPoint - 1] = '.';
			}
		} else {
			dotPosition = tempString.length();
		}


		int offset = width - afterDecimalPoint - dotPosition;
		if (afterDecimalPoint > 0) {
			offset--;
		}

		// Not enough room to decimal align within the supplied width
		if (offset < 0) {
			return tempString;
		}

		// Copy characters before decimal point
		for (int i = 0; i < dotPosition; i++) {
			result[offset + i] = tempString.charAt(i);
		}

		// Copy characters after decimal point
		for (int i = dotPosition + 1; i < tempString.length(); i++) {
			result[offset + i] = tempString.charAt(i);
		}

		return new String(result);
	}



	/**
	 * Returns the given Array in a string representation. Even though the
	 * parameter is of type "Object" one can hand over primitve arrays, e.g.
	 * int[3] or double[2][4].
	 * 
	 * @param array       the array to return in a string representation
	 * @return            the array as string
	 */
	public static String arrayToString(Object array) {
		String        result;
		int           dimensions;
		int           i;       

		result     = "";
		dimensions = getArrayDimensions(array);

		if (dimensions == 0) {
			result = "null";
		}
		else if (dimensions == 1) {
			for (i = 0; i < Array.getLength(array); i++) {
				if (i > 0)
					result += ",";
				if (Array.get(array, i) == null)
					result += "null";
				else
					result += Array.get(array, i).toString();
			}
		}
		else {
			for (i = 0; i < Array.getLength(array); i++) {
				if (i > 0)
					result += ",";
				result += "[" + arrayToString(Array.get(array, i)) + "]";
			}
		}

		return result;
	}


	/**
	 * Returns the dimensions of the given array. Even though the
	 * parameter is of type "Object" one can hand over primitve arrays, e.g.
	 * int[3] or double[2][4].
	 *
	 * @param array       the array to determine the dimensions for
	 * @return            the dimensions of the array
	 */
	public static int getArrayDimensions(Class array) {
		if (array.getComponentType().isArray())
			return 1 + getArrayDimensions(array.getComponentType());
		else
			return 1;
	}

	/**
	 * Returns the dimensions of the given array. Even though the
	 * parameter is of type "Object" one can hand over primitve arrays, e.g.
	 * int[3] or double[2][4].
	 *
	 * @param array       the array to determine the dimensions for
	 * @return            the dimensions of the array
	 */
	public static int getArrayDimensions(Object array) {
		return getArrayDimensions(array.getClass());
	}
}
