package org.reussite.appui.support.dashboard.domain;



import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentBooking {
	@JsonView(Views.Response.class)
	@Null
	private String id;

	@JsonView(Views.Response.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime createDate=TimeUtils.getCurrentTime();
	
	
    @NotNull(groups = ValidationGroups.Post.class)
	private StudentProfile studentProfile;

    @JsonIgnore
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime deleteDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime teacherAssignedDate;

	@JsonView(Views.Response.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime effectiveStartDate;
	
	@JsonView(Views.Response.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime rejectDate;
	
    @NotNull(groups = ValidationGroups.Post.class)
	private Schedule schedule;
	
	@JsonView(Views.Response.class)
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	private List<Tag> tags= new ArrayList<Tag>();
	private List<Attachment> attachments= new ArrayList<Attachment>();
	private String conferenceUrl;
	
	
	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
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

	public ZonedDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
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

	

	
	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
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
