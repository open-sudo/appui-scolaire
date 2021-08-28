package org.reussite.appui.support.dashboard.service;


import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reussite.appui.support.dashbaord.utils.TimeUtils;
import org.reussite.appui.support.dashboard.model.ConferenceRoom;
import org.reussite.appui.support.dashboard.model.Schedule;
import org.reussite.appui.support.dashboard.model.StudentBooking;
import org.reussite.appui.support.dashboard.model.StudentProfile;
import org.reussite.appui.support.dashboard.model.TeacherAvailability;
import org.reussite.appui.support.dashboard.model.TeacherProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
public class ConferenceService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@ConfigProperty(name = "reussite.appui.subjects.elementary.special")
	protected List<String> specialSubjects;

	@ConfigProperty(name = "reussite.appui.conference.timeWindowInMinutes")
	protected int timeWindowInMinutes;
	private boolean bridgeOpened;

	@ConfigProperty(name = "reussite.appui.conference.url.support")
	protected String supportConferenceUrl;
	@ConfigProperty(name = "reussite.appui.conference.url.static")
	protected String staticWelcomePage;

	@Inject
	ConferenceRoom conferenceRoom;
	
	public ConferenceRoom meet(String path,TeacherProfile profile) throws IOException {
		ZonedDateTime before30 = ZonedDateTime.now().minusMinutes(timeWindowInMinutes*10);
		ZonedDateTime after30 = ZonedDateTime.now().plusMinutes(timeWindowInMinutes*10);
		List<TeacherAvailability> av=TeacherAvailability.findByStartDateBetweenAndConferenceUrl(before30, after30,path);
		if(av.size()>0) {
			logger.info("Active teacher availability found for teacher :{}. Returning the first one with conference URL:{}",path,av.get(0).conferenceUrl);
			return conferenceRoom.withTeacherAvailability(av.get(0));
		}
		logger.info("No active teacher availability found for teacher :{}. Returning support URL",path);
		ConferenceRoom room= conferenceRoom.withTeacherProfile(profile).withSupportRoom(supportConferenceUrl);
		return room;
	}


	

	public ConferenceRoom createAndAssignBooking(String path,  ZonedDateTime before30, ZonedDateTime after30) {
		logger.info("No student booking found with path:{}. Redirecting user to support room:{}", path,supportConferenceUrl);
		StudentProfile studentProfile = StudentProfile.findByConferenceUrlContaining(path);
		if (studentProfile == null) {
			logger.info("No student found matching conference URL {}. Redirecting to support: {}", path,supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("User profile found for {}: {}", studentProfile);
		int grade = studentProfile.grade;
		if (grade > 6) {
			logger.info("Path {} corresponds to grade level of  greater 6. Sending student to support: {}", path,supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("Finding a teacher who can hanbdle student of grade 6 or less for path:{}", path);
		TeacherAvailability availability = findElementaryTeacherAvailability(grade, before30, after30);
		if (availability == null) {
			logger.info("No teacher availability found to satisfy elementary school with path {}. Sending student to support: {}",path, supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		StudentBooking booking = new StudentBooking();
		List<Schedule> schedules=Schedule.findBySubjectAndTenantKeyAndStartDate("Maths", studentProfile.tenants,  TimeUtils.getCurrentTime().minusHours(1), TimeUtils.getCurrentTime().plusHours(1));
		if(schedules.size()==0) {
			logger.info("No schedule found for subject Maths in tenant {}",Arrays.deepToString(studentProfile.tenants.stream().map(t -> t.key).toArray()));
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(staticWelcomePage);
		}
		booking.schedule=schedules.get(0);
		booking.studentProfile = (studentProfile);
		booking.teacherAvailability = (availability);
		booking.teacherAssignedDate = TimeUtils.getCurrentTime();
		logger.info("New booking created for elementary student :{} : {}", path, booking);
		booking.persistAndFlush();
		long count=StudentBooking.countByTeacherAvailability(availability.id);
		availability.studentCount=count;
		availability.persistAndFlush();
		logger.info("Redirecting student: {} to teacher {}", path, availability.teacherProfile.conferenceUrl);
		return conferenceRoom.withTeacherAvailability(availability);
	}

	public ConferenceRoom meet(String path, StudentProfile studentProfile) throws IOException {
		logger.info("Bridge status:{}", bridgeOpened);
		if (!bridgeOpened) {
			logger.info("Bridge is not open. Redirecting user {} to static welcome page:{}", path,staticWelcomePage);
			return conferenceRoom.withStudentProfile(studentProfile).withWelcomePage(staticWelcomePage);
		}
		ZonedDateTime before30 = ZonedDateTime.now().minusMinutes(timeWindowInMinutes*10);
		ZonedDateTime after30 = ZonedDateTime.now().plusMinutes(timeWindowInMinutes*10);
		logger.info("Search interval of {} minutes. computed for {} : {} to {}. ", timeWindowInMinutes,path, before30, after30);
		long bookingsWithUrl = StudentBooking.countByConferenceUrlAndEndDateIsNull(path);
		
		if (bookingsWithUrl == 0) {
			logger.info("No booking found for :{} in timeframe. Creating and assinging one:{}  to {} ",path,before30,after30);
			ConferenceRoom room= createAndAssignBooking(path, before30, after30);
			return room;
		}
		List<StudentBooking> bs=StudentBooking.findByConferenceUrl(path);
		logger.info("Bookings found for :{}",path);
		logger.info(Arrays.deepToString(bs.toArray()));
		long bookingsInBetween = StudentBooking.countByStartDateBetween(before30, after30);
		if (bookingsInBetween == 0) {
			logger.info("No student booking found  in the timeframe from :{}  to :{} . Redirecting user to support room:{}",before30, after30, supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}

		List<StudentBooking> bookings = StudentBooking.findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(path,before30, after30);
		if (bookings.size() == 0) {
			logger.info("No student booking found matching request:{} in the timeframe from :{}  to :{} . Redirecting user to support room:{}",path, before30, after30, supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("{} Student bookings found matching request:{}.", bookings.size(), path);
		logger.info("{} Student bookings found matching request:{}.", bookings.get(0).getClass().getName());

		StudentBooking booking = bookings.get(0);
		logger.info("Student booking found matching request:{}. Booking: {}, {}", path, booking);
		if (booking.rejectDate != null) {
			logger.info("Student has been rejected and must be handled manually by support:{}. redirecting to support:{}",path, supportConferenceUrl);
			return conferenceRoom.withStudentBooking(booking).withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		TeacherAvailability availability = booking.teacherAvailability;
		if (availability != null) {
			logger.info("Existing teacher availability found for path:{} --> {}.", path, availability);
			return conferenceRoom.withTeacherAvailability(availability).withStudentProfile(studentProfile);
		}
		logger.info("No existing teacher availability assigned to student:{}. Finding a new one.", path);

		long count = TeacherAvailability.count();
		logger.info("Total of {} availabilities found in the database", count);

		List<TeacherAvailability> availabilities = TeacherAvailability.findByStartDateBetween(before30, after30);
		logger.info("Subtotal of {} availabilities found with start date between {} and {}", availabilities.size(),before30, after30);
		int grade = booking.studentProfile.grade;
		if (grade <= 6 && !isSpecialElementarySubject(booking.schedule.subject)) {
			logger.info("Student grade is 6 or less and subject is not special subject. Searching for elementary availability by ignoring subject");
			availability = findElementaryTeacherAvailability(grade, before30, after30);
		} else {
			logger.info("Student grade is greater than 6. Searching for secondary availability");
			availabilities = TeacherAvailability.findBySubjectAndStartDateBetween(booking.schedule.subject, before30, after30);
			logger.info("{} Availabilities for subject {} and  start date between {} and {}", availabilities.size(),booking.schedule.subject, before30, after30);
			availability = findSecondaryTeacherAvailability(booking.studentProfile.grade, booking.schedule.subject, before30,after30);
		}
		if (availability == null) {
			logger.info("No teacher availability with start date between {} and {} found to satisfy request {} and booking {}. Redirecting to support room:",before30, after30, path, booking, supportConferenceUrl);
			return conferenceRoom.withStudentBooking(booking).withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("Teacher found to satisfy request:{} -->{}", path, availability);
		booking.teacherAvailability = availability;
		booking.teacherAssignedDate = TimeUtils.getCurrentTime();
		booking.persistAndFlush();
		count=StudentBooking.countByTeacherAvailability(availability.id);
		availability.studentCount=count;
		availability.persistAndFlush();
			
		return conferenceRoom.withTeacherAvailability(availability).withStudentProfile(studentProfile);
	}

	protected boolean isSpecialElementarySubject(String subject) {
		for (String specialSubject : specialSubjects) {
			if (specialSubject.equalsIgnoreCase(subject)) {
				return true;
			}
		}
		return false;
	}

	public TeacherAvailability findElementaryTeacherAvailability(int grade, ZonedDateTime startDate,
			ZonedDateTime endDate) {
		for (int deviation = 0; deviation < 12; deviation++) {
			int deviatedGradeUp = grade + deviation;
			int deviatedGradeDown = grade - deviation;
			List<TeacherAvailability> teachers = null;
			if (deviatedGradeUp <= 12) {
				teachers = TeacherAvailability.findByGradeAndStartDateBetween(deviatedGradeUp, startDate, endDate);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
				teachers = TeacherAvailability.findByGradeAndStartDateBetween(deviatedGradeDown, startDate, endDate);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
			}
		}
		return null;
	}

	public TeacherAvailability findSecondaryTeacherAvailability(int grade, String subject, ZonedDateTime before30,
			ZonedDateTime after30) {
		for (int deviation = 0; deviation < 12; deviation++) {
			int deviatedGradeUp = grade + deviation;
			int deviatedGradeDown = grade - deviation;
			List<TeacherAvailability> teachers = null;
			if (deviatedGradeUp <= 12) {
				teachers = TeacherAvailability.findBySubjectAndGradeAndStartDateBetween(subject, deviatedGradeUp,
						before30, after30);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
				teachers = TeacherAvailability.findBySubjectAndGradeAndStartDateBetween(subject, deviatedGradeDown,
						before30, after30);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
			}
		}
		return null;
	}

	public boolean isBridgeOpened() {
		return bridgeOpened;
	}

	public void setBridgeOpened(boolean bridgeOpened) {
		this.bridgeOpened = bridgeOpened;
	}

	public List<String> getSpecialSubjects() {
		return specialSubjects;
	}

	public void setSpecialSubjects(List<String> specialSubjects) {
		this.specialSubjects = specialSubjects;
	}


}
