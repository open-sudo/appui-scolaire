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
public class StudentBooking {
	private String id;
	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime createDate=TimeUtils.getCurrentTime();
	
    @NotNull(message="Student profile may not be null")
	private String studentProfileId;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime deleteDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime teacherAssignedDate;

	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime effectiveStartDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime rejectDate;
	
	private String scheduleId;
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	private List<Tag> tags= new ArrayList<Tag>();

	private String conferenceUrl;
	
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
	public String getStudentProfileId() {
		return studentProfileId;
	}

	public void setStudentProfileId(String studentProfileId) {
		this.studentProfileId = studentProfileId;
	}

	public ZonedDateTime getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(ZonedDateTime deleteDate) {
		this.deleteDate = deleteDate;
	}

	public ZonedDateTime getTeacherAssignedDate() {
		return teacherAssignedDate;
	}

	public void setTeacherAssignedDate(ZonedDateTime teacherAssignedDate) {
		this.teacherAssignedDate = teacherAssignedDate;
	}

	public ZonedDateTime getEffectiveStartDate() {
		return effectiveStartDate;
	}

	public void setEffectiveStartDate(ZonedDateTime effectiveStartDate) {
		this.effectiveStartDate = effectiveStartDate;
	}

	public ZonedDateTime getRejectDate() {
		return rejectDate;
	}

	public void setRejectDate(ZonedDateTime rejectDate) {
		this.rejectDate = rejectDate;
	}

	public String getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(String scheduleId) {
		this.scheduleId = scheduleId;
	}



	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}


	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public String getConferenceUrl() {
		return conferenceUrl;
	}

	public void setConferenceUrl(String conferenceUrl) {
		this.conferenceUrl = conferenceUrl;
	}

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

}
