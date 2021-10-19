package org.reussite.appui.conference.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.junit.jupiter.api.Test;
import org.reussite.appui.support.dashboard.controller.ConferenceController;
import org.reussite.appui.support.dashboard.model.ConferenceRoom;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.service.ConferenceService;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
public class ConferenceServiceTest extends SampleDataService  {

	@Inject
	protected ConferenceController conferenceController;
	@Inject
	protected ConferenceService conferenceService;
	@ConfigProperty(name="reussite.appui.conference.url.support")
	protected String supportConferenceUrl;
	@ConfigProperty(name="reussite.appui.conference.url.static")
	protected String staticWelcomePage;
	

	
	String conf0="jeanne-nguyen";
	String conf15="ryan-martin";
	String conf35="thomas-adams";
	String conf18="justin-martinez";
	

	@Test
	public void basicTest() throws IOException {
		assertTrue(true);
	}
	
	@Test
	@TestTransaction
	public void mathSubjectExists() throws IOException {
		generateData();
		List<SubjectEntity> subjects=SubjectEntity.listAll();
		SubjectEntity math=subjects.stream().filter( t -> t.name.toLowerCase().contains("math")).findAny().orElse(null);
		assertNotNull(math);
		assertTrue(math.general);

	}
	
	@Test
	@TestTransaction
	public void mathScheduleExists() throws IOException {
		generateData();
		List<ScheduleEntity> schedules=ScheduleEntity.listAll();
		System.out.println("\n\n\n\n=====Number of schedules:"+schedules.size());
		ScheduleEntity math=schedules.stream().filter( t -> t.course.subject.name.toLowerCase().contains("math")).findAny().orElse(null);
		assertNotNull(math);
	}
	
	@Test
	@TestTransaction
	public void mathBookingExists() throws IOException {
		generateData();
		List<StudentBookingEntity> bookings=StudentBookingEntity.listAll();
		StudentBookingEntity math=bookings.stream().filter( t -> t.schedule.course.subject.name.toLowerCase().contains("math")).findAny().orElse(null);
		assertNotNull(math);
	}
	@Test
	@TestTransaction
	public void meetWithBridgeClose() throws IOException {
		generateData();
		conferenceService.setBridgeOpened(false);
		StudentProfileEntity studentProfile = StudentProfileEntity.findByConferenceUrl(conf0);
		ConferenceRoom room=conferenceService.meet(conf0,studentProfile);
		assertNotNull(room);
		assertEquals(room.getWelcomePage(),staticWelcomePage);
	}
	
    protected String jitsiUrl="meet.jit.si";
    protected String htmlMarker="<div id=\"meet\"></div>";

	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaticDates() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);
		assertTrue(conferenceService.isBridgeOpened());
		
		StudentProfileEntity studentProfile = StudentProfileEntity.findByConferenceUrl(conf0);

		ConferenceRoom room=conferenceService.meet(conf0,studentProfile);
		assertNotNull(room);
		assertEquals(room.getSupportRoom(),supportConferenceUrl);
		ConferenceRoom page=conferenceService.meet(conf0,studentProfile);
		String url=page.resolve().toString();
		assertTrue(url.equals(room.getSupportRoom()));
	}
	
	
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndPastDates() throws IOException {
		long thenMinutes=58;
		 ZonedDateTime date1= ZonedDateTime.now().minusMinutes(thenMinutes);
		 ZonedDateTime date2=date1.plusMinutes(40);
		 ZonedDateTime date3=date2.plusMinutes(40);
		StudentProfileEntity studentProfile = StudentProfileEntity.findByConferenceUrl(conf0);

		generateDataWithGivenDates(date1,date2,date3);
		conferenceService.setBridgeOpened(true);
		ConferenceRoom room=conferenceService.meet(conf0,studentProfile);
		assertNotNull(room);
		assertNotNull(room.getSupportRoom());
		assertEquals(room.getSupportRoom(),supportConferenceUrl);
	}
	


	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDates() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);

		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> bookings=StudentBookingEntity.listAll();
		StudentBookingEntity booking=bookings.stream().filter( t -> t.studentProfile.grade<=6 &&  t.schedule.course.subject.name.toLowerCase().contains("math")).findAny().orElse(null);
		assertNotNull(booking);
		StudentProfileEntity p = StudentProfileEntity.findByConferenceUrl(conf0);
		
		String studentUrl=booking.studentProfile.conferenceUrl;
		ConferenceRoom teacherRoom=conferenceService.meet(studentUrl,p);
		assertNotNull(teacherRoom);
		assertNotNull(teacherRoom.getAvailability());
		assertNotNull(teacherRoom.getAvailability().conferenceUrl);
		assertNotNull(teacherRoom.getProfile());
		String url=teacherRoom.resolve().toString();

		assertNotEquals(url,supportConferenceUrl);
		assertNotEquals(url,staticWelcomePage);
		assertTrue(url.contains("bbb"));
	}
	String conferencePrefix="bbb1";
	
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDatesSuperiorGrades() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.listAll();
		StudentBookingEntity student=null;
		
		for(StudentBookingEntity st:students) {
			String formatedDate=TimeUtils.toLongString(st.schedule.startDate);
			System.out.println(formatedDate);
			if(formatedDate.equals(getDate(0)) && st.studentProfile.grade>7) {
				student=st;
				break;
			}
		}
		StudentProfileEntity p = StudentProfileEntity.findByConferenceUrl(conf0);

		String studentUrl=student.studentProfile.conferenceUrl;
		ConferenceRoom teacherRoom=conferenceService.meet(studentUrl,p);
		

		String url=teacherRoom.resolve().toString();

		assertNotEquals(url,supportConferenceUrl);
		assertNotEquals(url,staticWelcomePage);
		assertTrue(url.contains(conferencePrefix));

	}
	
	
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDatesWithUrlTypos() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);

		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.listAll();
		StudentBookingEntity student=null;
		for(StudentBookingEntity st:students) {
			String formatedDate=TimeUtils.toLongString(st.schedule.startDate);
			if(formatedDate.equals(getDate(0))) {
				student=st;
				break;
			}
		}
		String studentUrl=student.studentProfile.conferenceUrl;
		studentUrl=studentUrl.substring(studentUrl.lastIndexOf("/")+1);
		
		URI redirectUrl=conferenceController.meet(studentUrl);
		assertNotNull(redirectUrl);
		assertNotEquals(redirectUrl.toString(),supportConferenceUrl);
		assertNotEquals(redirectUrl.toString(),staticWelcomePage);
		String studentUrl2=" /.*& "+studentUrl+" /.*& ";
		URI redirectUrl2=conferenceController.meet(studentUrl2);
		assertEquals(redirectUrl2.toString(),redirectUrl.toString());
	}
	
	
	

	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDates2() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		
		List<StudentBookingEntity> students=StudentBookingEntity.listAll();
		
		String studentUrl=students.get(0).studentProfile.conferenceUrl;
		assertNotNull(studentUrl);
		assertNotNull(students.get(0).schedule);
		URI redirectUrl=conferenceController.meet(studentUrl);
		assertNotEquals(redirectUrl.toString(),supportConferenceUrl);
		System.out.println(redirectUrl.toString());
		assertNotEquals(redirectUrl.toString(),staticWelcomePage);
		assertTrue(redirectUrl.toASCIIString().contains(conferencePrefix));
		
	}
	
	/*
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDates3() throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.listAll();
		assertNull(students.get(0).teacherAvailability);
		String studentUrl=students.get(0).studentProfile.conferenceUrl;
		assertNotNull(studentUrl);
		URI redirectUrl=conferenceController.meet(studentUrl);
		assertNotNull(redirectUrl);
		
		StudentBookingEntity student=StudentBookingEntity.findById(students.get(0).id);
		assertNotNull(student);
		System.out.println("Student URL:"+studentUrl);
		
		assertNotNull(student.teacherAvailability);
		

		String page=conferenceService.meetStudent(studentUrl);
		assertTrue(page.contains(jitsiUrl));
		assertTrue(page.contains(htmlMarker));
		assertNotNull(redirectUrl.getAvailability());
		
		assertNotNull(redirectUrl.getAvailability().tenant);
		assertNotNull(redirectUrl.resolve(studentUrl));
		assertTrue(page.contains(redirectUrl.resolve(studentUrl)));


	}
	
	
	


	@Test
	@TestTransaction
	public void meetAndRescheduleWithBridgeOpenAndStaggeredDates3() throws IOException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException, NotSupportedException {
		
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.findByTenantKey(SampleDataService.tenants[0]);
		StudentBookingEntity student=students.get(0);
		
		
		String studenturl=students.get(0).studentProfile.conferenceUrl;
		studenturl=studenturl.substring(studenturl.lastIndexOf("/")+1);
		assertNotNull(studenturl);
		ConferenceRoom redirectUrl=conferenceService.meet(studenturl);
		
		List<TeacherAvailability> teacherAvailabilities= TeacherAvailability.findListByTenantKey(SampleDataService.tenants[0]);
		TeacherAvailability teacherAvailability=teacherAvailabilities.get(5);
		
		
		assignTeacher(teacherAvailability.tenant.key,student.id, teacherAvailability.id);
		redirectUrl=conferenceService.meet(studenturl);
		StudentBookingEntity student1=StudentBookingEntity.findById(student.id);
		assertEquals(redirectUrl.resolve(studenturl),student1.teacherAvailability.teacherProfile.conferenceUrl);

 	}
	
	

	private TeacherAvailability assignTeacher(String tenantKey,String bookingId, String availablityId) {
		 StudentBookingEntity booking=StudentBookingEntity.findById(bookingId);
		 if(booking==null) {
			 throw new NoSuchElementException(String.valueOf(bookingId));
		 }
		 if(!booking.tenant.key.equals(tenantKey)) {
	     		throw new IllegalArgumentException("Tenant mismatch");
		 }
		 TeacherAvailability previous=booking.teacherAvailability;
		 TeacherAvailability availabiliy=TeacherAvailability.findById(availablityId);
		 if(availabiliy==null) {
			 throw new NoSuchElementException("No teacher availability assigned");
		 }
		 if(!availabiliy.tenant.key.equals(tenantKey)) {
	     		throw new IllegalArgumentException("Tenant mismatch");
		 }
		 booking.teacherAvailability=availabiliy;
		 booking.teacherAssignedDate=TimeUtils.getCurrentTime();
		 booking.rejectDate=null;
		 booking.persistAndFlush();
		 
		 TeacherAvailability availability=TeacherAvailability.findById(booking.teacherAvailability.id);
		 long count=StudentBookingEntity.countByTeacherAvailability(availability.id);
		 availability.studentCount=count;
		 availability.persistAndFlush();
		 if(previous!=null) {
			 count=StudentBookingEntity.countByTeacherAvailability(previous.id);
			 previous.studentCount=count;
			 previous.persistAndFlush();
		 }
		 return availability;
    }


	@Test
	@TestTransaction
	public void nullifyrejectDate() throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.listAll();
		StudentBookingEntity student=students.get(0);
		assertNull(student.rejectDate);

		student.rejectDate=TimeUtils.getCurrentTime();
		student.persistAndFlush();
		
		
		student=StudentBookingEntity.findById(student.id);
		assertNotNull(student.rejectDate);
		
		student.rejectDate=null;
		
		student.persistAndFlush();
		
		student=StudentBookingEntity.findById(student.id);
		assertNull(student.rejectDate);
		
	}
/*
	//@Test
	@TestTransaction
	public void meetAndRejectStudent() throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.listAll();
		StudentBookingEntity student=students.get(0);
		
		
		String studentUrl=student.studentProfile.conferenceUrl;
		assertNotNull(studentUrl);
		
		String redirectUrl=conferenceService.meet(studentUrl);
		assertNotEquals(redirectUrl,supportConferenceUrl);
		assertNotEquals(redirectUrl,staticWelcomePage);
		
		
		TeacherAvailability skeleton= new TeacherAvailability();
		skeleton.id="aaa";
		
		student.teacherAvailability=skeleton;
		 StudentBookingEntity booking=StudentBookingEntity.findById(student.id);
			booking.persistAndFlush();
		bookingService.updateStudentBookingEntity(student.tenantKey,student, student.id);
		
		redirectUrl=conferenceService.meet(studentUrl);
		assertEquals(redirectUrl,supportConferenceUrl);

		

		 student=StudentBookingEntity.findById(student.id);
		 assertNotNull(student.rejectDate);
		 assertNull(student.teacherAvailability);
		 

		 List<TeacherAvailability>teachers= TeacherAvailability.listAll();
		 skeleton= new TeacherAvailability();
		 skeleton.id= teachers.get(5).id;
		 student.teacherAvailability=skeleton;
		 
		 bookingService.updateStudentBookingEntity(student.tenantKey,student, student.id);
		 
		 StudentBookingEntity b=StudentBookingEntity.findById(student.id);
		 assertNull(b.rejectDate);
		 redirectUrl=conferenceService.meet(studentUrl);
		 assertEquals(redirectUrl,teachers.get(5).teacherProfile.conferenceUrl);
 	}
	
*//*
	
	@Test
	@TestTransaction
	public void meetAndDoubleRescheduleWithBridgeOpenAndStaggeredDates3() throws IOException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException, NotSupportedException {
		conferenceService.setBridgeOpened(true);

		generateSampleDataWithStaggeredDates();
		List<StudentBookingEntity> students=StudentBookingEntity.findByTenantKey(SampleDataService.tenants[0]);
		StudentBookingEntity student=students.get(0);
		
		String studentUrl=student.studentProfile.conferenceUrl;
		ConferenceRoom redirectUrl=conferenceService.meet(studentUrl);
		assertNotEquals(redirectUrl.getSupportRoom(),supportConferenceUrl);
		assertNotEquals(redirectUrl.getWelcomePage(),staticWelcomePage);
		List<TeacherAvailability> teacherAvailabilities= TeacherAvailability.findListByTenantKey(SampleDataService.tenants[0]);
		
		
		TeacherAvailability teacherAvailability1=teacherAvailabilities.get(5);
		assertEquals(teacherAvailability1.studentCount,0);
		assignTeacher(student.tenant.key,student.id, teacherAvailability1.id);

		student=StudentBookingEntity.findById(student.id);
		
		
		teacherAvailability1=TeacherAvailability.findById(teacherAvailability1.id);
		assertEquals(teacherAvailability1.studentCount,1);

		redirectUrl=conferenceService.meet(studentUrl);
		assertEquals(redirectUrl.resolve(studentUrl),teacherAvailability1.teacherProfile.conferenceUrl);
		
		 List<StudentBookingEntity> bookings=null;
		 bookings=StudentBookingEntity.findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(studentUrl,TimeUtils.getCurrentTime().minusHours(2),TimeUtils.getCurrentTime().plusHours(2));
		
		 assertNotNull(bookings.get(0).teacherAvailability);
		 assertEquals(bookings.get(0).teacherAvailability.teacherProfile.conferenceUrl,redirectUrl.resolve(studentUrl));
		 student=StudentBookingEntity.findById(student.id);
		TeacherAvailability teacherAvailability2=teacherAvailabilities.get(8);
	
		assignTeacher(student.tenant.key,student.id, teacherAvailability2.id);

		
		
		 bookings=StudentBookingEntity.findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(studentUrl,TimeUtils.getCurrentTime().minusHours(2),TimeUtils.getCurrentTime().plusHours(2));
			
		 assertNotNull(bookings.get(0).teacherAvailability);
		 assertEquals(bookings.get(0).teacherAvailability.id,teacherAvailability2.id);
		 
		 teacherAvailability1=TeacherAvailability.findById(teacherAvailability1.id);
		 assertEquals(teacherAvailability1.studentCount,0);
		 
		 teacherAvailability2=TeacherAvailability.findById(teacherAvailability2.id);
		 assertEquals(teacherAvailability2.studentCount,1);
}
	
	*/
}
