package org.reussite.appui.support.dashboard.domain;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import javax.validation.constraints.NotEmpty;
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
public class TeacherProfile{
	@JsonView(Views.Response.class) 
	private String id;
    @NotNull(groups = ValidationGroups.Post.class)
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String schoolBoard;
    private String qualifications;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    private ZonedDateTime createDate=TimeUtils.getCurrentTime();
	@ElementCollection(fetch = FetchType.EAGER)
	private Set<Integer>  grades=new HashSet<Integer>();
    private String biographie;
    private String imageUrl;
    private String schoolName;
	private String conferenceUrl;
    private String meetUrl;
	private int countryCode;

	@JsonView(Views.Response.class) 
	private short onlineStatus;
	@JsonView(Views.Response.class) 
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	
	private List<Tag> tags= new ArrayList<Tag>();

    @NotEmpty(groups = ValidationGroups.Post.class)
	private List<Subject>subjects= new ArrayList<Subject>();


	public List<Subject> getSubjects() {
		return subjects;
	}

	public void setSubjects(List<Subject> subjects) {
		this.subjects = subjects;
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


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
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

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSchoolBoard() {
		return schoolBoard;
	}

	public void setSchoolBoard(String schoolBoard) {
		this.schoolBoard = schoolBoard;
	}

	public String getQualifications() {
		return qualifications;
	}

	public void setQualifications(String qualifications) {
		this.qualifications = qualifications;
	}

	public ZonedDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}

	public Set<Integer> getGrades() {
		return grades;
	}

	public void setGrades(Set<Integer> grades) {
		this.grades = grades;
	}

	public String getBiographie() {
		return biographie;
	}

	public void setBiographie(String biographie) {
		this.biographie = biographie;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getConferenceUrl() {
		return conferenceUrl;
	}

	public void setConferenceUrl(String conferenceUrl) {
		this.conferenceUrl = conferenceUrl;
	}

	public String getMeetUrl() {
		return meetUrl;
	}

	public void setMeetUrl(String meetUrl) {
		this.meetUrl = meetUrl;
	}

	public int getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	public short getOnlineStatus() {
		return onlineStatus;
	}

	public void setOnlineStatus(short onlineStatus) {
		this.onlineStatus = onlineStatus;
	}

	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}


	
}
