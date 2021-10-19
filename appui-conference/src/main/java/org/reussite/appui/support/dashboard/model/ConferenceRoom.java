package org.reussite.appui.support.dashboard.model;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;

import javax.enterprise.context.RequestScoped;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequestScoped
public class ConferenceRoom {

	private String supportRoom;
	private String welcomePage;
	private String teacherRoom;
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	private StudentBookingEntity booking;
	private TeacherAvailabilityEntity availability;
	private StudentProfileEntity studentProfile;
	private boolean student=true;
	@ConfigProperty(name="reussite.appui.conference.enabled")
	protected boolean conferenceServerAvailable=true;
	@ConfigProperty(name="reussite.appui.conference.baseUrl")
	protected String videoServerUrl;
	@ConfigProperty(name="reussite.appui.conference.authKey")
	protected String videoServerAuthKey;

	public ConferenceRoom setStudent(boolean b) {
		this.student=b;
		return this;
	}
	
	public boolean isStudent() {
		return student;
	}

	public StudentBookingEntity getBooking() {
		return booking;
	}

	public TeacherAvailabilityEntity getAvailability() {
		return availability;
	}

	public StudentProfileEntity getProfile() {
		return studentProfile;
	}

	public ConferenceRoom withTeacherAvailability(TeacherAvailabilityEntity availability) {
		this.availability = availability;
		return this;
	}



	public ConferenceRoom withStudentBooking(StudentBookingEntity booking) {
		this.booking = booking;
		return this;
	}

	public ConferenceRoom withSupportRoom(String supportRoom) {
		this.supportRoom=supportRoom;
		return this;
	}
	

	public ConferenceRoom withStudentProfile(StudentProfileEntity studentProfile) {
		this.studentProfile=studentProfile;
		return this;
	}
	public ConferenceRoom withWelcomePage(String welcomePage) {
		this.welcomePage=welcomePage;
		return this;
	}
	
	public String getSupportRoom() {
		return supportRoom;
	}

	public String getWelcomePage() {
		return welcomePage;
	}

	public String getTeacherRoom() {
		return teacherRoom;
	}



	private String encode(String t) {
		try {
			return URLEncoder.encode(t,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return t;
	}
	
	protected String createConferenceCreationUrl(TeacherAvailabilityEntity availability) {
		String secret=videoServerAuthKey;
		String firstName=availability.teacherProfile.firstName;
		String lastName=availability.teacherProfile.lastName;
		String meetingId=availability.conferenceId;
		String base="name="+encode(firstName)+"+"+encode(lastName+"'s Classroom")+"&";
		base=base+"meetingID="+meetingId;
		base=base+"&attendeePW="+availability.studentPassword+"&moderatorPW="+availability.teacherPassword;
		String checksum=DigestUtils.sha1Hex("create"+base+secret);
		String baseUrl=videoServerUrl+"/create?"+base+"&checksum="+checksum;
		return baseUrl;
	}
	
	protected String createJoinConferenceUrl(TeacherAvailabilityEntity availability) {
		if(!isStudent() && conferenceServerAvailable) {
			try {
				String createUrl=createConferenceCreationUrl(availability);
				logger.info("Conference creation URL created for teacher:{}: {}",availability.teacherProfile.firstName,createUrl);
				URL url=new URL(createUrl);
				InputStream input=url.openStream();
				String response=IOUtils.toString(input,"UTF-8");
				logger.info("Conference create for teacher:{}. Response:{}",availability.teacherProfile.firstName,response);
			}catch(Exception e) {
				logger.error(e.getMessage(),e);
			}
		}
		String secret=videoServerAuthKey;
		String firstName=isStudent()?studentProfile.firstName:availability.teacherProfile.firstName;
		String lastName=isStudent()?studentProfile.lastName:availability.teacherProfile.lastName;
		String meetingId=availability.conferenceId;
		String base="fullName="+encode(firstName)+"+"+encode(lastName+"'s Classroom")+"&";
		base=base+"joinViaHtml5=true&meetingID="+meetingId;
		base=base+"&password="+(isStudent()?availability.studentPassword:availability.teacherPassword);
		String checksum=DigestUtils.sha1Hex("join"+base+secret);
		String baseUrl=videoServerUrl+"/join?"+base+"&checksum="+checksum;
		logger.info("Join URL computed:{}",baseUrl);
		return baseUrl;
	}
	
	public URI resolve() throws URISyntaxException {
		if(availability!=null) {
			return new URI(createJoinConferenceUrl(availability));
		}
		if(StringUtils.isNotBlank(supportRoom)) {
			return new URI(supportRoom);
		}
		if(StringUtils.isNotBlank(welcomePage)) {
			return new URI(welcomePage);
		}
		return new URI(supportRoom);
	}
	

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

}
