package org.reussite.appui.support.dashboard.domain;


import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;


@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TeacherComment{

	@Null
	@JsonView(Views.Read.class)
	private String id;
	
    @NotNull(groups = ValidationGroups.Post.class)
	@JsonView(Views.WriteOnce.class)
	private TeacherProfile commenter;
	private TeacherProfile approver;
    @NotNull(groups = ValidationGroups.Post.class)
	private String content;
	@JsonView(Views.Read.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();
	
    @NotNull(groups = ValidationGroups.Post.class)
	private StudentBooking studentBooking;
    @NotNull(groups = ValidationGroups.Post.class)
	private StudentParent studentParent;
    @NotNull(groups = ValidationGroups.Post.class)
	private StudentProfile studentProfile;
	
	
	public ZonedDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
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
	public TeacherProfile getCommenter() {
		return commenter;
	}
	public void setCommenter(TeacherProfile commenter) {
		this.commenter = commenter;
	}
	public TeacherProfile getApprover() {
		return approver;
	}
	public void setApprover(TeacherProfile approver) {
		this.approver = approver;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public StudentBooking getStudentBooking() {
		return studentBooking;
	}
	public void setStudentBooking(StudentBooking studentBooking) {
		this.studentBooking = studentBooking;
	}
	public StudentParent getStudentParent() {
		return studentParent;
	}
	public void setStudentParent(StudentParent studentParent) {
		this.studentParent = studentParent;
	}
	public StudentProfile getStudentProfile() {
		return studentProfile;
	}
	public void setStudentProfile(StudentProfile studentProfile) {
		this.studentProfile = studentProfile;
	}
	
	
}
