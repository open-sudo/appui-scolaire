package org.reussite.appui.support.dashboard.utils;

import org.apache.commons.lang3.StringUtils;

import io.quarkus.panache.common.Sort;

public class SearchUtils {

	
	

	public static Sort getAbsoluteSort(String sortParams, @SuppressWarnings("rawtypes") Class clazz) {
		String field=sortParams;
		if(StringUtils.isBlank(sortParams)) {
			return Sort.descending("c.createDate");
		}
		boolean asc=false;

		if(sortParams.contains(",")) {
			String[] parts=sortParams.split(",");
			field=parts[0];
		}
		if(sortParams.contains("asc")) {
			asc=true;
		}
		if(field.contains("createDate")) {
			field= "c."+field;
			Sort sort=asc?Sort.ascending(field):Sort.descending(field);
			return sort;
		}
		if(clazz==null) { 
			Sort sort=asc?Sort.ascending(field):Sort.descending(field);
			return sort;
		}
		if((clazz.getCanonicalName().contains("TeacherAvailability")
		  ||clazz.getCanonicalName().contains("StudentBooking")) && field.contains("startDate")) {
			field="c.schedule."+field;
		}
		else {
			field="c."+field;
		}
		Sort sort=asc?Sort.ascending(field):Sort.descending(field);
		return sort;
	}
	public static Sort getAbsoluteSort(String sortParams) {
		return getAbsoluteSort(sortParams,null);
	}
	
}
