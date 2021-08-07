package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.GenericGenerator;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;




@Entity(name = "TeacherProfile")
@Table(name = "teacher_profile")
public class TeacherProfileEntity{
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	private String id;
	
	@NotBlank
	@Column(unique=true)
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
    @Column(unique=true)
    private String internalEmail;
    @JsonIgnore
    private String internalEmailPassword;
    @Column(unique=true)
	private String conferenceUrl;
    private String meetUrl;
	private int countryCode;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime deleteDate;
	private String activationCode;
	private ZonedDateTime activationDate;
	private short onlineStatus;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime approveDate=TimeUtils.getCurrentTime();
	
	@ManyToMany
	private Set<TagEntity> tags= new HashSet<TagEntity>();

	@ManyToMany
	private Set<SubjectEntity> subjects= new HashSet<SubjectEntity>();

	public ZonedDateTime getApproveDate() {
		return approveDate;
	}


	public void setApproveDate(ZonedDateTime approveDate) {
		this.approveDate = approveDate;
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



	public Set<TagEntity> getTags() {
		return tags;
	}


	public void setTags(Set<TagEntity> tags) {
		this.tags = tags;
	}


	public Set<SubjectEntity> getSubjects() {
		return subjects;
	}


	public void setSubjects(Set<SubjectEntity> subjects) {
		this.subjects = subjects;
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

	public String getInternalEmail() {
		return internalEmail;
	}

	public void setInternalEmail(String internalEmail) {
		this.internalEmail = internalEmail;
	}

	public String getInternalEmailPassword() {
		return internalEmailPassword;
	}

	public void setInternalEmailPassword(String internalEmailPassword) {
		this.internalEmailPassword = internalEmailPassword;
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

	public ZonedDateTime getDeleteDate() {
		return deleteDate;
	}

	public void setDeleteDate(ZonedDateTime deleteDate) {
		this.deleteDate = deleteDate;
	}

	public String getActivationCode() {
		return activationCode;
	}

	public void setActivationCode(String activationCode) {
		this.activationCode = activationCode;
	}

	public ZonedDateTime getActivationDate() {
		return activationDate;
	}

	public void setActivationDate(ZonedDateTime activationDate) {
		this.activationDate = activationDate;
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
    
