package org.reussite.appui.support.dashboard.domain;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherAvailability {
	private String id;
	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate =TimeUtils.getCurrentTime();
	@NotNull
	private String teacherProfileId;
    private long studentCount;
    private String scheduleId;
    
    private String duration;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime deleteDate;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime effectiveStartDate;
	private String supervisorUrl;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	private List<TeacherProfile>assistants= new ArrayList<TeacherProfile>();
	private List<Tag> tags= new ArrayList<Tag>();

	private String studentPassword;
	private String teacherPassword;
	
	private String conferenceUrl;
	private String conferenceId;
	
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}


	public List<TeacherProfile> getAssistants() {
		return assistants;
	}

	public void setAssistants(List<TeacherProfile> assistants) {
		this.assistants = assistants;
	}

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ZonedDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}

	public String getTeacherProfileId() {
		return teacherProfileId;
	}

	public void setTeacherProfileId(String teacherProfileId) {
		this.teacherProfileId = teacherProfileId;
	}

	public long getStudentCount() {
		return studentCount;
	}

	public void setStudentCount(long studentCount) {
		this.studentCount = studentCount;
	}

	

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public ZonedDateTime getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(ZonedDateTime deleteDate) {
		this.deleteDate = deleteDate;
	}

	public ZonedDateTime getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(ZonedDateTime effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public String getSupervisorUrl() {
		return supervisorUrl;
	}

	public void setSupervisorUrl(String supervisorUrl) {
		this.supervisorUrl = supervisorUrl;
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	

	public String getStudentPassword() {
		return studentPassword;
	}

	public void setStudentPassword(String studentPassword) {
		this.studentPassword = studentPassword;
	}

	public String getTeacherPassword() {
		return teacherPassword;
	}

	public void setTeacherPassword(String teacherPassword) {
		this.teacherPassword = teacherPassword;
	}

	public String getConferenceUrl() {
		return conferenceUrl;
	}

	public void setConferenceUrl(String conferenceUrl) {
		this.conferenceUrl = conferenceUrl;
	}

	public String getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	
}
