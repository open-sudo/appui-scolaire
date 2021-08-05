package org.reussite.appui.support.dashboard.utils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;



public class TimeUtils {

	public static ZoneId defaultZoneId=ZoneId.of("America/Toronto");
	
	public interface DateTimeFormats {	
	    String DATETIME_FORMAT = "MM/dd/yyyy HH:mm:ss Z";
	    String SIMPLE_DATETIME_FORMAT = "MM/dd/yyyy HH:mm";
	}
	
	public static TimeZone defaultTimeZone=TimeZone.getTimeZone("America/Toronto");

	public static ZonedDateTime getCurrentTime() {
		return ZonedDateTime.now();
	}
  
    public static ZonedDateTime getDateFromString(String strDateTime) {
        	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormats.DATETIME_FORMAT);
        	ZonedDateTime d=ZonedDateTime.parse(strDateTime, formatter);
        	return d;
    }
    
    public static String toLongString(ZonedDateTime dateTime){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormats.DATETIME_FORMAT);
		String formattedString = dateTime.format(formatter);
		return formattedString;
	}
    

    public static String toShortString(ZonedDateTime dateTime){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormats.SIMPLE_DATETIME_FORMAT);
		String formattedString = dateTime.format(formatter);
		return formattedString;
	}
}

