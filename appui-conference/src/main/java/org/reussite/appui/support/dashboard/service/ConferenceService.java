package org.reussite.appui.support.dashboard.service;


import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reussite.appui.support.dashboard.model.ConferenceRoom;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
public class ConferenceService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());


	@ConfigProperty(name = "reussite.appui.conference.timeWindowInMinutes")
	protected int timeWindowInMinutes;
	private boolean bridgeOpened;

	@ConfigProperty(name = "reussite.appui.conference.url.support")
	protected String supportConferenceUrl;
	@ConfigProperty(name = "reussite.appui.conference.url.static")
	protected String staticWelcomePage;

	@Inject
	ConferenceRoom conferenceRoom;
	
	public ConferenceRoom meet(String path,TeacherProfileEntity  profile) throws IOException {
		ZonedDateTime before30 = ZonedDateTime.now().minusMinutes(timeWindowInMinutes*10);
		ZonedDateTime after30 = ZonedDateTime.now().plusMinutes(timeWindowInMinutes*10);
		List<TeacherAvailabilityEntity> av=TeacherAvailabilityEntity.findByStartDateBetweenAndConferenceUrl(before30, after30,path);
		if(av.size()>0) {
			logger.info("Active teacher availability found for teacher :{}. Returning the first one with conference URL:{}",path,av.get(0).conferenceUrl);
			return conferenceRoom.withTeacherAvailability(av.get(0));
		}
		logger.info("No active teacher availability found for teacher :{}. Returning support URL",path);
		ConferenceRoom room= conferenceRoom.withSupportRoom(supportConferenceUrl);
		return room;
	}


	

	public ConferenceRoom createAndAssignBooking(String path,  ZonedDateTime before30, ZonedDateTime after30) {
		logger.info("No student booking found with path:{}. Redirecting user to support room:{}", path,supportConferenceUrl);
		StudentProfileEntity studentProfile = StudentProfileEntity.findByConferenceUrl(path);
		if (studentProfile == null) {
			logger.info("No student found matching conference URL {}. Redirecting to support: {}", path,supportConferenceUrl);
			List<StudentProfileEntity> conf=StudentProfileEntity.findAll().list();
			for(StudentProfileEntity e:conf) {
				logger.info("\n\n\n"+e);
			}
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("User profile found for {}: {}", studentProfile);
		int grade = studentProfile.grade;
		if (grade > 6) {
			logger.info("Path {} corresponds to grade level of  greater 6. Sending student to support: {}", path,supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("Finding a teacher who can hanbdle student of grade 6 or less for path:{}", path);
		TeacherAvailabilityEntity availability = findGeneralTeacherAvailability(grade, before30, after30);
		if (availability == null) {
			logger.info("No teacher availability found to satisfy elementary school with path {}. Sending student to support: {}",path, supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		StudentBookingEntity booking = new StudentBookingEntity();
		List<ScheduleEntity> schedules=ScheduleEntity.findByStartDate(TimeUtils.getCurrentTime().minusHours(1), TimeUtils.getCurrentTime().plusHours(1));
		if(schedules.size()==0) {
			logger.info("No schedule found that starts in window  {} to {} ",TimeUtils.getCurrentTime().minusHours(1), TimeUtils.getCurrentTime().plusHours(1));
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(staticWelcomePage);
		}
		booking.schedule=schedules.get(0);
		booking.studentProfile = (studentProfile);
		booking.teacherAvailability = (availability);
		booking.teacherAssignedDate = TimeUtils.getCurrentTime();
		logger.info("New booking created for elementary student :{} : {}", path, booking);
		booking.persistAndFlush();
		long count=StudentBookingEntity.countByTeacherAvailability(availability.id);
		availability.studentCount=count;
		availability.persistAndFlush();
		logger.info("Redirecting student: {} to teacher {}", path, availability.teacherProfile.conferenceUrl);
		return conferenceRoom.withTeacherAvailability(availability);
	}

	public ConferenceRoom meet(String path, StudentProfileEntity studentProfile) throws IOException {
		logger.info("Bridge status:{}. Support URL:{}", bridgeOpened, supportConferenceUrl);
		if (!bridgeOpened) {
			logger.info("Bridge is not open. Redirecting user {} to static welcome page:{}", path,staticWelcomePage);
			return conferenceRoom.withStudentProfile(studentProfile).withWelcomePage(staticWelcomePage);
		}
		ZonedDateTime before30 = ZonedDateTime.now().minusMinutes(timeWindowInMinutes);
		ZonedDateTime after30 = ZonedDateTime.now().plusMinutes(timeWindowInMinutes);
		logger.info("Search interval of {} minutes computed for {} : {} to {}. ", timeWindowInMinutes,path, before30, after30);
		long bookingsWithUrl = StudentBookingEntity.countByConferenceUrlAndEndDateIsNull(path);
		
		if (bookingsWithUrl == 0) {
			logger.info("No booking found for :{} in timeframe. Creating and assinging one:{}  to {} ",path,before30,after30);
			ConferenceRoom room= createAndAssignBooking(path, before30, after30);
			return room;
		}
		List<StudentBookingEntity> bs=StudentBookingEntity.findByConferenceUrl(path);
		logger.info("Bookings found for :{}",path);
		logger.info(Arrays.deepToString(bs.toArray()));
		long bookingsInBetween = StudentBookingEntity.countByStartDateBetween(before30, after30);
		if (bookingsInBetween == 0) {
			logger.info("No student booking found  in the timeframe from :{}  to :{} . Redirecting user to support room:{}",before30, after30, supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}

		List<StudentBookingEntity> bookings = StudentBookingEntity.findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(path,before30, after30);
		if (bookings.size() == 0) {
			logger.info("No student booking found matching request:{} in the timeframe from :{}  to :{} . Redirecting user to support room:{}",path, before30, after30, supportConferenceUrl);
			return conferenceRoom.withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("{} Student bookings found matching request:{}.", bookings.size(), path);
		logger.info("{} Student bookings found matching request:{}.", bookings.get(0).getClass().getName());

		
		StudentBookingEntity booking = bookings.get(0);
		for(StudentBookingEntity tmp:bookings) {
			if(tmp.schedule.course.subject.general!=null && tmp.schedule.course.subject.general) {
				booking=tmp;
				break;
			}
		}
		logger.info("Student booking found matching request:{}. Booking: {}, {}", path, booking);
		if (booking.rejectDate != null) {
			logger.info("Student has been rejected and must be handled manually by support:{}. redirecting to support:{}",path, supportConferenceUrl);
			return conferenceRoom.withStudentBooking(booking).withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		TeacherAvailabilityEntity availability = booking.teacherAvailability;
		if (availability != null) {
			logger.info("Existing teacher availability found for path:{} --> {}.", path, availability);
			return conferenceRoom.withTeacherAvailability(availability).withStudentProfile(studentProfile);
		}
		logger.info("No existing teacher availability assigned to student:{}. Finding a new one.", path);

		long count = TeacherAvailabilityEntity.count();
		logger.info("Total of {} availabilities found in the database", count);

		List<TeacherAvailabilityEntity> availabilities = TeacherAvailabilityEntity.findByStartDateBetween(before30, after30);
		logger.info("Subtotal of {} availabilities found with start date between {} and {}", availabilities.size(),before30, after30);
		int grade = booking.studentProfile.grade;
		SubjectEntity subject=booking.schedule.course.subject;
		if (subject.general!=null && subject.general) {
			logger.info("Searching availability for general subject: {}",subject);
			availability = findGeneralTeacherAvailability(grade, before30, after30);
		}
		else {
			logger.info("Student grade is greater than 6 or subject is not general. Searching for secondary availability");
			availabilities = TeacherAvailabilityEntity.findBySubjectAndStartDateBetween(booking.schedule.course.subject.id, before30, after30);
			logger.info("{} Availabilities for subject {} and  start date between {} and {}", availabilities.size(),booking.schedule.course.subject.name, before30, after30);
			availability = findSecondaryTeacherAvailability(booking.studentProfile.grade, booking.schedule.course.subject.id, before30,after30);
		}
		if (availability == null) {
			logger.info("No teacher availability with start date between {} and {} found to satisfy request {} and booking {}. Redirecting to support room:",before30, after30, path, booking, supportConferenceUrl);
			return conferenceRoom.withStudentBooking(booking).withStudentProfile(studentProfile).withSupportRoom(supportConferenceUrl);
		}
		logger.info("Teacher found to satisfy request:{} -->{}", path, availability);
		booking.teacherAvailability = availability;
		booking.teacherAssignedDate = TimeUtils.getCurrentTime();
		booking.persistAndFlush();
		count=StudentBookingEntity.countByTeacherAvailability(availability.id);
		availability.studentCount=count;
		availability.persistAndFlush();
			
		return conferenceRoom.withTeacherAvailability(availability).withStudentProfile(studentProfile);
	}


	public TeacherAvailabilityEntity findGeneralTeacherAvailability(int grade, ZonedDateTime startDate,
			ZonedDateTime endDate) {
		for (int deviation = 0; deviation < 12; deviation++) {
			int deviatedGradeUp = grade + deviation;
			int deviatedGradeDown = grade - deviation;
			List<TeacherAvailabilityEntity> teachers = null;
			if (deviatedGradeUp <= 12) {
				teachers = TeacherAvailabilityEntity.findByGradeAndStartDateBetween(deviatedGradeUp, startDate, endDate);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
				teachers = TeacherAvailabilityEntity.findByGradeAndStartDateBetween(deviatedGradeDown, startDate, endDate);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
			}
		}
		return null;
	}

	public TeacherAvailabilityEntity findSecondaryTeacherAvailability(int grade, String subject, ZonedDateTime before30,
			ZonedDateTime after30) {
		for (int deviation = 0; deviation < 12; deviation++) {
			int deviatedGradeUp = grade + deviation;
			int deviatedGradeDown = grade - deviation;
			List<TeacherAvailabilityEntity> teachers = null;
			if (deviatedGradeUp <= 12) {
				teachers = TeacherAvailabilityEntity.findBySubjectAndGradeAndStartDateBetween(subject, deviatedGradeUp,
						before30, after30);
				if (teachers.size() > 0) {
					return teachers.get(0);
				}
				teachers = TeacherAvailabilityEntity.findBySubjectAndGradeAndStartDateBetween(subject, deviatedGradeDown,
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


}
