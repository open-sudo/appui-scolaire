package org.reussite.appui.support.dashboard.domain;


import java.time.ZonedDateTime;
import java.util.Set;

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
public class ParentNotification{

	@Null
	@JsonView(Views.Read.class)
	private String id;
	
    @NotNull(groups = ValidationGroups.Post.class)
	@JsonView(Views.WriteOnce.class)
	private TeacherProfile sender;
    @NotNull(groups = ValidationGroups.Post.class)
	private String content;
	@JsonView(Views.Read.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();
    @NotNull(groups = ValidationGroups.Post.class)
	private Set<StudentParent> recipients;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime deleteDate;
	
	
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
	
	public TeacherProfile getSender() {
		return sender;
	}
	public void setSender(TeacherProfile sender) {
		this.sender = sender;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Set<StudentParent> getRecipients() {
		return recipients;
	}
	public void setRecipients(Set<StudentParent> recipients) {
		this.recipients = recipients;
	}
	
	
}
