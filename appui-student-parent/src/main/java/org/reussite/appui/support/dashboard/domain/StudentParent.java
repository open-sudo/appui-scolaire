package org.reussite.appui.support.dashboard.domain;


import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

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
public class StudentParent{
	@JsonView(Views.Response.class)
	private String id;
    @NotNull(groups = ValidationGroups.Post.class)
	private String email;
    @NotNull(groups = ValidationGroups.Post.class)
	private String phoneNumber;

	@JsonIgnore
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime activationDate;
	@JsonIgnore
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime deleteDate;
	@JsonView(Views.Response.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();
	private String firstName;
	private String lastName;
	@NotNull(groups = ValidationGroups.Post.class)
	private String language="EN";
	private int countryCode;

	@JsonView(Views.Response.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public ZonedDateTime getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(ZonedDateTime activationDate) {
		this.activationDate = activationDate;
	}

	public ZonedDateTime getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(ZonedDateTime deleteDate) {
		this.deleteDate = deleteDate;
	}

	public ZonedDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
