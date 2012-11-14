package com.ccc.util.regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This is a class for validating various common regular expressions such as email phone number
 * @author Adam Gibson
 *
 */
public class ValidatorUtil {
	/**
	 * This will attempt to match the given phone number with a regex.
	 * @param phoneNumber the phone number to match
	 * @return true if the given string is a phone number or false when null,empty, or doesn't match
	 */
	public static boolean isPhoneNumber(String phoneNumber) {
		if(phoneNumber==null || phoneNumber.isEmpty())
			return false;
		Matcher m=phonePattern.matcher(phoneNumber);
		return m.matches();
	}//end isPhoneNumber
	
	/**
	 * This will attempt to match the given email with a regex.
	 * @param email the email to match
	 * @return true if the given string is a valid email or false when empty,doesn't match or null
	 */
	public static boolean isEmail(String email) {
		if(email==null || email.isEmpty())
			return false;
		Matcher m=emailPattern.matcher(email);
		return m.matches();
	}//end isEmail
	
	
	
	private static Pattern emailPattern=Pattern.compile("\\b[a-zA-Z-\\.-]+\\s*((@)|(\\s\\bat\\b\\s))\\s*([a-zA_Z-]+((\\.)|(\\bdot\\b)|(;)))+\\s*"  + "[a-zA-Z-]{2,6}" + "\\b");
	private static Pattern phonePattern=Pattern.compile("((\\(\\d{3}\\))|(\\(\\d{3}\\)((-)|(\\s)))|(\\d{3})((-)|(\\s)))\\s*\\d{3}((-)|(\\s))\\d{4}");
}
