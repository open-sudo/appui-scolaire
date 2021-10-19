package org.reussite.appui.support.dashbaord.utils;

import java.util.List;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.i18n.phonenumbers.PhoneNumberToTimeZonesMapper;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneUtils {
	private static final Logger logger = LoggerFactory.getLogger(PhoneUtils.class);

	public static final String DEFAULT_ACTIVATION_CODE="3232";
	
	public static TimeZone toTimeZone(String phone) {
			if(StringUtils.isBlank(phone)) {
				  logger.debug("No timezone found for phone number:{}. Defaulting to {}.",phone,TimeUtils.defaultTimeZone);
				  return TimeUtils.defaultTimeZone;
			}
		   TimeZone timezone=null;
		   PhoneNumber number =new PhoneNumber().setCountryCode(1).setNationalNumber(Long.parseLong(phone));
		   PhoneNumberToTimeZonesMapper coder=PhoneNumberToTimeZonesMapper.getInstance();
		   List<String> timezones=coder.getTimeZonesForNumber(number);
		   if(timezones.size()>0 && !timezones.get(0).toLowerCase().contains("unknown")) {
			   timezone=TimeZone.getTimeZone(timezones.get(0));
		   }
		   if(timezone==null) {
			   logger.debug("No timezone found for phone number:{}. Defaulting to {}.",phone,TimeUtils.defaultTimeZone);
			   timezone=TimeUtils.defaultTimeZone;
		   }else {
			   logger.debug("Timezone for phone {} detected as:{}",phone,timezone);
		   }
		   return timezone;
	}
	
	
	public static String validate(String phone, int countryCode) {
		return validate(phone,String.valueOf(countryCode));
	}
	
	public static String validate(String phone, String countryCode) {
		String clean=phone.replaceAll("[^0-9]", "");
		clean=clean.replaceFirst("[0]*", "");
		if(!phone.toString().startsWith((countryCode))) {
			clean=countryCode+clean;
		}
		if(clean.length()<8) {
			throw new IllegalArgumentException("Phone Number:"+clean);
		}
		return clean;
	}
	
	public static void main(String[] args) {
		System.out.print(validate("0  0363 636 -36", 1));
	}
}
