package org.reussite.appui.support.dashboard.domain;


import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentBooking {
	private String id;
	private StudentProfile studentProfile;
	private TeacherAvailability teacherAvailability;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime teacherAssignedDate;

    private Schedule schedule;
   
	public Schedule getSchedule() {
		return schedule;
	}
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public StudentProfile getStudentProfile() {
		return studentProfile;
	}
	public void setStudentProfile(StudentProfile studentProfile) {
		this.studentProfile = studentProfile;
	}
	
	
	public TeacherAvailability getTeacherAvailability() {
		return teacherAvailability;
	}
	public void setTeacherAvailability(TeacherAvailability teacherAvailability) {
		this.teacherAvailability = teacherAvailability;
	}
	
	public ZonedDateTime getTeacherAssignedDate() {
		return teacherAssignedDate;
	}
	public void setTeacherAssignedDate(ZonedDateTime teacherAssignedDate) {
		this.teacherAssignedDate = teacherAssignedDate;
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

}
