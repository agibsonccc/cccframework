package com.ccc.users.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Pattern;

import org.springframework.util.Assert;
/**
 * This is a util class for dates.
 * Credit to:
 * http://en.allexperts.com/q/Java-1046/2009/3/Dates-using-Regular-expression.htm
 *
 */
public class DateUtils {
	public static final String datePattern="\\d{1,2}/\\d{1,2}/\\d{2,4}";

	public static boolean validateDayMonth(String strDate){
		Assert.notNull(strDate);
		Assert.hasText(strDate);
		boolean isValid = false;
		Pattern p = Pattern.compile(datePattern);
		String[] dateArray = strDate.split("/");

		int day = Integer.valueOf(dateArray[1]).intValue();
		int month = Integer.valueOf(dateArray[0]).intValue();
		int year = Integer.valueOf(dateArray[2]).intValue();
		
		if ( (day > 0 && day <= 31) && (month > 0 && month <= 12) ){
			/*
			 * should be correct for most cases but still will not be correct in fringe cases like
			 * feb having 30 days or april having 31 days.
			 */

			isValid = true;
			try   {
				GregorianCalendar cal = new GregorianCalendar();

				/*
				 *  setLenient to false to force calendar to throw
				 * IllegalArgumentException in case
				 *  any field, day, month or year is not valid (invalid year would be '00')
				 */
				cal.setLenient(false);
				// month - 1 is done because Calander uses 0-11 for months
				cal.set(year, (month-1), day);            
				/*
				 * add is called just to invoke the method
				 * Calendar.complete(). complete() is the method
				 *  that throws the IllegalArgumentException.
				 *  
				 *  Note : Calendar.set() does not compute the date fields
				 * only methods like add(),
				 *  roll() or getTime() force the Calendar object to calculate
				 * field values
				 */
				// done only to force Calendar to compute all fields
				cal.add(Calendar.SECOND, 1);
			}catch (IllegalArgumentException iae){
				isValid = false;
			}
		}
		return isValid;
	}
	
	public static void main(String[] args) {
		System.out.println(DateUtils.validateDayMonth("09/24/86"));
	}

}//end DateUtils
