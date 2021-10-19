package org.reussite.appui.support.dashboard.domain;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;



@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentProfile{
	@JsonView(Views.Read.class)
	private String id;
	@JsonView(Views.WriteOnce.class)
	private String studentParentId;
	private String email;
    @NotNull(groups = ValidationGroups.Post.class)
	private String lastName;
    @NotNull(groups = ValidationGroups.Post.class)
	private String firstName;
	private Integer grade;
	private String imageUrl;
	@JsonView(Views.Read.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime registrationDate;
	private String schoolName;
	private String schoolBoard;
	private boolean isTermCondionApproved;
	private String conferenceUrl;
	private Integer countryCode;
	private String phoneNumber;
	
	@JsonView(Views.Read.class)
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();

	@JsonView(Views.Read.class)
	private short onlineStatus;

	@JsonView(Views.Read.class)
	private String lastSeenRoom;
	private List<Tag> tags= new ArrayList<Tag>();
	
	

	
	public Integer getCountryCode() {
		return countryCode;
	}


	public void setCountryCode(Integer countryCode) {
		this.countryCode = countryCode;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public List<Tag> getTags() {
		return tags;
	}


	public void setTags(List<Tag> tags) {
		this.tags = tags;
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


	public String getStudentParentId() {
		return studentParentId;
	}


	public void setStudentParentId(String studentParentId) {
		this.studentParentId = studentParentId;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public Integer getGrade() {
		return grade;
	}


	public void setGrade(Integer grade) {
		this.grade = grade;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public ZonedDateTime getRegistrationDate() {
		return registrationDate;
	}


	public void setRegistrationDate(ZonedDateTime registrationDate) {
		this.registrationDate = registrationDate;
	}


	public String getSchoolName() {
		return schoolName;
	}


	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}


	public String getSchoolBoard() {
		return schoolBoard;
	}


	public void setSchoolBoard(String schoolBoard) {
		this.schoolBoard = schoolBoard;
	}


	public boolean isTermCondionApproved() {
		return isTermCondionApproved;
	}


	public void setTermCondionApproved(boolean isTermCondionApproved) {
		this.isTermCondionApproved = isTermCondionApproved;
	}



	public String getConferenceUrl() {
		return conferenceUrl;
	}


	public void setConferenceUrl(String conferenceUrl) {
		this.conferenceUrl = conferenceUrl;
	}



	public ZonedDateTime getCreateDate() {
		return createDate;
	}


	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}


	public short getOnlineStatus() {
		return onlineStatus;
	}


	public void setOnlineStatus(short onlineStatus) {
		this.onlineStatus = onlineStatus;
	}


	public String getLastSeenRoom() {
		return lastSeenRoom;
	}


	public void setLastSeenRoom(String lastSeenRoom) {
		this.lastSeenRoom = lastSeenRoom;
	}



	

}
