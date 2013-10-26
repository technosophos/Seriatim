package com.technosophos.seriatim.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Calendar;

/**
 * Utilities for manipulating Seriatim time.
 * Pun intended.
 * @author mbutcher
 *
 */
public class DateTimeUtils {

	public static long now() {
		return (new Date()).getTime();
	}
	
	public static String canonicalDateString(String date) {
		Date d;
		
		
		if(date.indexOf("/") == 2 && date.lastIndexOf("/") == 5) {
			String [] parts = date.split("/");
			
			try {
				int month = Integer.parseInt(parts[0]) - 1;
				int day = Integer.parseInt(parts[1]);
				int year = Integer.parseInt(parts[2]);
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);
				return Long.toString(cal.getTimeInMillis());
			} catch (Exception e) {
				System.err.println("Date looked like mm/dd/yyyy, but couldn't be parsed.");
				// fall through to the other date parser.
			}
			
		}
		
		try {
			d = DateFormat.getDateInstance().parse(date);
		} catch (ParseException e) {
			System.err.println("Failed to parse date.");
			d = new Date();
		}
		return Long.toString(d.getTime());
	}
	
	/**
	 * Format a time string.
	 * 
	 * A time slice is some arbitrary time notation, like 1:20 or 1.5. It is not
	 * a clock time.
	 * 
	 * This converts a given string into a canonical time string of the form 
	 * hh:mm (or, if the incoming string has a decimal instead of a colon, hh.hh).
	 * @param time Some unit of time.
	 * @return Formatted time string, or "0:00" if the time cannot be parsed.
	 */
	public static String canonicalTimeSliceString(String time) {
		
		String defaultTime = "00:00";
		
		// Eight chars is enough to hold a year of hours.
		if(time.length() > 8) {
			return defaultTime;
		}
		
		int colon = time.indexOf(":");
		if (colon == 0) {
			
			int iMinutes;
			try {
				iMinutes = Integer.parseInt(time.substring(1, 3));
			} catch (NumberFormatException e) {
				return "0:00";
			}
			String retval = String.format("0:%02d", iMinutes);
			return retval;
			
		} else if(colon > 0) {
			// We think time is/should be of hh:mm[:ss] type of format
			String hours = time.substring(0, colon);
			String minutes;// = time.substring(colon + 1);
			if(time.length() >= 4)
				minutes = time.substring(colon + 1, colon + 3);
			else
				minutes = "0";
			System.err.println("##DateTimeUtils: Minutes is " + minutes);
			
			int iHours;
			try {
				iHours = Integer.parseInt(hours);
			} catch (NumberFormatException e) {
				iHours = 0;
			}
			
			if(iHours < 0) iHours = 0;
			
			int iMinutes;
			try {
				iMinutes = Integer.parseInt(minutes);
			} catch (NumberFormatException e) {
				iMinutes = 0;
			}
			
			if(iMinutes >= 60) iMinutes = 59;
			else if(iMinutes < 0) iMinutes = 0;
			
			String retval = String.format("%d:%02d", iHours, iMinutes);
			
			return retval;
			
		} else if(time.indexOf(".") >= 0) {
			// XXX: Should this convert to hour:minute?
			// Time is in decimal notation?
			float fTime;
			try {
				fTime = Float.parseFloat(time);
			} catch (NumberFormatException e) {
				return "0:00";
			}
			
			//String retval = String.format("%0.2f", fTime);
			
			return DateTimeUtils.convertFractionalHoursToStandardTime(fTime);
		} else {
			// Maybe it's *just* hours:
			int iTime;
			try {
				iTime = Integer.parseInt(time);
			} catch (NumberFormatException e) {
				return defaultTime; // Not a time.
			}
			
			if(iTime < 0) 
				return defaultTime;
			
			return String.format("%d:00", iTime);
		}
	}
	
	/**
	 * Convert time in fractional hours (e.g. 1.5) into hh:mm format (e.g. 1:30).
	 * @param hrs Time as a fraction.
	 * @return String formatted as hh:mm.
	 */
	public static String convertFractionalHoursToStandardTime(float hrs) {
		int hundHours = Math.round(hrs * 100);
		
		int iHours = hundHours / 100;
		int iMinutes = ((hundHours % 100) * 60) / 100;
		
		return String.format("%d:%02d", iHours, iMinutes);
	}
	
	/**
	 * Add together times in the form hh:mm
	 * @param times One or more time strings.
	 * @return String representation (hh:mm) as a total.
	 */
	public static String addTimes(String... times) {
		
		int totalHours = 0;
		int totalMins = 0;
		//int totalSecs = 0;
		
		int[] t;
		for(String time: times) {
			t = parseStandardTime(time);
			//totalSecs += t[2];
			totalMins += t[1];
			totalHours += t[0];
		}
		
		int carry = 0;
		/*
		if(totalSecs > 60) {
			carry = totalSecs / 60;
			totalSecs = totalSecs % 60;
		}
		totalMins += carry;
		*/
		
		if(totalMins > 60) {
			carry = totalMins / 60;
			totalMins = totalMins % 60;
		}
		
		totalHours += carry;
		
		return String.format("%2d:%2d", totalHours, totalMins);
		
	}
	
	/**
	 * Parses hh:mm[:ss] and returns int[] {hour, min, sec}.
	 * @param stdTime String of form hh:mm[:ss]
	 * @return Hours, minutes and seconds as ints.
	 */
	public static int[] parseStandardTime(String stdTime) {
		String [] hourMin = stdTime.split(":", 3); // if hh:mm:ss
		
		int hour = Integer.parseInt(hourMin[0]);
		int min = Integer.parseInt(hourMin[1]);
		int sec;
		if(hourMin.length == 3) {
			sec = Integer.parseInt(hourMin[2]);
		} else {
			sec = 0;
		}
		return new int[] {hour, min, sec};
	}
	
}
