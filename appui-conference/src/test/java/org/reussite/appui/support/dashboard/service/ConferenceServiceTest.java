package org.reussite.appui.support.dashboard.service;

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
import org.reussite.appui.support.dashbaord.utils.TimeUtils;
import org.reussite.appui.support.dashboard.controller.ConferenceController;
import org.reussite.appui.support.dashboard.model.ConferenceRoom;
import org.reussite.appui.support.dashboard.model.StudentBooking;
import org.reussite.appui.support.dashboard.model.StudentProfile;
import org.reussite.appui.support.dashboard.model.TeacherAvailability;
import org.reussite.appui.support.dashboard.model.TenantProfile;

import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;

@QuarkusTest
public class ConferenceServiceTest extends TestingBase  {

	@Inject
	protected ConferenceController conferenceController;
	@Inject
	protected ConferenceService conferenceService;
	@ConfigProperty(name="reussite.appui.conference.url.support")
	protected String supportConferenceUrl;
	@ConfigProperty(name="reussite.appui.conference.url.static")
	protected String staticWelcomePage;
	

	
	String conf0="michael-smith";
	String conf15="ryan-martin";
	String conf35="thomas-adams";
	String conf18="justin-martinez";
	


	
	@Test
	@TestTransaction
	public void meetWithBridgeClose() throws IOException {
		generateData();
		conferenceService.setBridgeOpened(false);
		StudentProfile studentProfile = StudentProfile.findByConferenceUrlContaining(conf0);
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
		
		StudentProfile studentProfile = StudentProfile.findByConferenceUrlContaining(conf0);

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
		StudentProfile studentProfile = StudentProfile.findByConferenceUrlContaining(conf0);

		generateDataWithGivenDates(date1,date2,date3);
		conferenceService.setBridgeOpened(true);
		ConferenceRoom room=conferenceService.meet(conf0,studentProfile);
		assertNotNull(room);
		assertEquals(room.getSupportRoom(),supportConferenceUrl);
	}
	
	

	

	@Test
	@TestTransaction
	public void specialElementarySubjectsNotNull() throws IOException {
		assertNotNull(conferenceService.getSpecialSubjects());
		assertEquals(conferenceService.getSpecialSubjects().get(0),"Anglais");
		assertEquals(conferenceService.getSpecialSubjects().get(1),"English");
	}
	
	
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDates() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);

		generateSampleDataWithStaggeredDates();
		List<StudentBooking> students=StudentBooking.listAll();
		StudentBooking student=null;
		for(StudentBooking st:students) {
			String formatedDate=TimeUtils.toLongString(st.schedule.startDate);
			if(formatedDate.equals(sampleDataService.getDates()[0])) {
				student=st;
				break;
			}
		}
		StudentProfile p = StudentProfile.findByConferenceUrlContaining(conf0);

		String studentUrl=student.studentProfile.conferenceUrl;
		ConferenceRoom teacherRoom=conferenceService.meet(studentUrl,p);
		assertNotNull(teacherRoom);
		assertNotNull(teacherRoom.getAvailability());
		assertNotNull(teacherRoom.getAvailability().conferenceUrl);

		String url=teacherRoom.resolve().toString();

		assertNotEquals(url,supportConferenceUrl);
		assertNotEquals(url,staticWelcomePage);
		assertTrue(url.contains(googlePrefix));
	}
	String googlePrefix="meet.google.com";
	
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDatesSuperiorGrades() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBooking> students=StudentBooking.listAll();
		StudentBooking student=null;
		System.out.println(sampleDataService.getDates()[0]);
		System.out.println(sampleDataService.getDates()[1]);
		System.out.println(sampleDataService.getDates()[2]);

		for(StudentBooking st:students) {
			String formatedDate=TimeUtils.toLongString(st.schedule.startDate);
			System.out.println(formatedDate);
			if(formatedDate.equals(sampleDataService.getDates()[0]) && st.studentProfile.grade>7) {
				student=st;
				break;
			}
		}
		StudentProfile p = StudentProfile.findByConferenceUrlContaining(conf0);

		String studentUrl=student.studentProfile.conferenceUrl;
		ConferenceRoom teacherRoom=conferenceService.meet(studentUrl,p);
		

		String url=teacherRoom.resolve().toString();

		assertNotEquals(url,supportConferenceUrl);
		assertNotEquals(url,staticWelcomePage);
		assertTrue(url.contains(googlePrefix));

	}
	
	
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDatesWithUrlTypos() throws IOException, URISyntaxException {
		conferenceService.setBridgeOpened(true);

		generateSampleDataWithStaggeredDates();
		List<StudentBooking> students=StudentBooking.listAll();
		StudentBooking student=null;
		for(StudentBooking st:students) {
			String formatedDate=TimeUtils.toLongString(st.schedule.startDate);
			if(formatedDate.equals(sampleDataService.getDates()[0])) {
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
		List<TenantProfile> tenants=TenantProfile.findAll().list();
		for(TenantProfile t:tenants) {
			assertNotNull(t.conferenceUrlPrefix);
		}
		List<StudentBooking> students=StudentBooking.listAll();
		
		String studentUrl=students.get(0).studentProfile.conferenceUrl;
		assertNotNull(studentUrl);
		assertNotNull(students.get(0).schedule);
		URI redirectUrl=conferenceController.meet(studentUrl);
		assertNotEquals(redirectUrl.toString(),supportConferenceUrl);
		System.out.println(redirectUrl.toString());
		assertNotEquals(redirectUrl.toString(),staticWelcomePage);
		assertTrue(redirectUrl.toASCIIString().contains(googlePrefix));
		
	}
	
	/*
	@Test
	@TestTransaction
	public void meetWithBridgeOpenAndStaggeredDates3() throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBooking> students=StudentBooking.listAll();
		assertNull(students.get(0).teacherAvailability);
		String studentUrl=students.get(0).studentProfile.conferenceUrl;
		assertNotNull(studentUrl);
		URI redirectUrl=conferenceController.meet(studentUrl);
		assertNotNull(redirectUrl);
		
		StudentBooking student=StudentBooking.findById(students.get(0).id);
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
		List<StudentBooking> students=StudentBooking.findByTenantKey(SampleDataService.tenants[0]);
		StudentBooking student=students.get(0);
		
		
		String studenturl=students.get(0).studentProfile.conferenceUrl;
		studenturl=studenturl.substring(studenturl.lastIndexOf("/")+1);
		assertNotNull(studenturl);
		ConferenceRoom redirectUrl=conferenceService.meet(studenturl);
		
		List<TeacherAvailability> teacherAvailabilities= TeacherAvailability.findListByTenantKey(SampleDataService.tenants[0]);
		TeacherAvailability teacherAvailability=teacherAvailabilities.get(5);
		
		
		assignTeacher(teacherAvailability.tenant.key,student.id, teacherAvailability.id);
		redirectUrl=conferenceService.meet(studenturl);
		StudentBooking student1=StudentBooking.findById(student.id);
		assertEquals(redirectUrl.resolve(studenturl),student1.teacherAvailability.teacherProfile.conferenceUrl);

 	}
	
	

	private TeacherAvailability assignTeacher(String tenantKey,String bookingId, String availablityId) {
		 StudentBooking booking=StudentBooking.findById(bookingId);
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
		 long count=StudentBooking.countByTeacherAvailability(availability.id);
		 availability.studentCount=count;
		 availability.persistAndFlush();
		 if(previous!=null) {
			 count=StudentBooking.countByTeacherAvailability(previous.id);
			 previous.studentCount=count;
			 previous.persistAndFlush();
		 }
		 return availability;
    }


	@Test
	@TestTransaction
	public void nullifyrejectDate() throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		generateSampleDataWithStaggeredDates();
		List<StudentBooking> students=StudentBooking.listAll();
		StudentBooking student=students.get(0);
		assertNull(student.rejectDate);

		student.rejectDate=TimeUtils.getCurrentTime();
		student.persistAndFlush();
		
		
		student=StudentBooking.findById(student.id);
		assertNotNull(student.rejectDate);
		
		student.rejectDate=null;
		
		student.persistAndFlush();
		
		student=StudentBooking.findById(student.id);
		assertNull(student.rejectDate);
		
	}
/*
	//@Test
	@TestTransaction
	public void meetAndRejectStudent() throws IOException, NotSupportedException, SystemException, SecurityException, IllegalStateException, RollbackException, HeuristicMixedException, HeuristicRollbackException {
		
		conferenceService.setBridgeOpened(true);
		generateSampleDataWithStaggeredDates();
		List<StudentBooking> students=StudentBooking.listAll();
		StudentBooking student=students.get(0);
		
		
		String studentUrl=student.studentProfile.conferenceUrl;
		assertNotNull(studentUrl);
		
		String redirectUrl=conferenceService.meet(studentUrl);
		assertNotEquals(redirectUrl,supportConferenceUrl);
		assertNotEquals(redirectUrl,staticWelcomePage);
		
		
		TeacherAvailability skeleton= new TeacherAvailability();
		skeleton.id="aaa";
		
		student.teacherAvailability=skeleton;
		 StudentBooking booking=StudentBooking.findById(student.id);
			booking.persistAndFlush();
		bookingService.updateStudentBooking(student.tenantKey,student, student.id);
		
		redirectUrl=conferenceService.meet(studentUrl);
		assertEquals(redirectUrl,supportConferenceUrl);

		

		 student=StudentBooking.findById(student.id);
		 assertNotNull(student.rejectDate);
		 assertNull(student.teacherAvailability);
		 

		 List<TeacherAvailability>teachers= TeacherAvailability.listAll();
		 skeleton= new TeacherAvailability();
		 skeleton.id= teachers.get(5).id;
		 student.teacherAvailability=skeleton;
		 
		 bookingService.updateStudentBooking(student.tenantKey,student, student.id);
		 
		 StudentBooking b=StudentBooking.findById(student.id);
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
		List<StudentBooking> students=StudentBooking.findByTenantKey(SampleDataService.tenants[0]);
		StudentBooking student=students.get(0);
		
		String studentUrl=student.studentProfile.conferenceUrl;
		ConferenceRoom redirectUrl=conferenceService.meet(studentUrl);
		assertNotEquals(redirectUrl.getSupportRoom(),supportConferenceUrl);
		assertNotEquals(redirectUrl.getWelcomePage(),staticWelcomePage);
		List<TeacherAvailability> teacherAvailabilities= TeacherAvailability.findListByTenantKey(SampleDataService.tenants[0]);
		
		
		TeacherAvailability teacherAvailability1=teacherAvailabilities.get(5);
		assertEquals(teacherAvailability1.studentCount,0);
		assignTeacher(student.tenant.key,student.id, teacherAvailability1.id);

		student=StudentBooking.findById(student.id);
		
		
		teacherAvailability1=TeacherAvailability.findById(teacherAvailability1.id);
		assertEquals(teacherAvailability1.studentCount,1);

		redirectUrl=conferenceService.meet(studentUrl);
		assertEquals(redirectUrl.resolve(studentUrl),teacherAvailability1.teacherProfile.conferenceUrl);
		
		 List<StudentBooking> bookings=null;
		 bookings=StudentBooking.findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(studentUrl,TimeUtils.getCurrentTime().minusHours(2),TimeUtils.getCurrentTime().plusHours(2));
		
		 assertNotNull(bookings.get(0).teacherAvailability);
		 assertEquals(bookings.get(0).teacherAvailability.teacherProfile.conferenceUrl,redirectUrl.resolve(studentUrl));
		 student=StudentBooking.findById(student.id);
		TeacherAvailability teacherAvailability2=teacherAvailabilities.get(8);
	
		assignTeacher(student.tenant.key,student.id, teacherAvailability2.id);

		
		
		 bookings=StudentBooking.findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(studentUrl,TimeUtils.getCurrentTime().minusHours(2),TimeUtils.getCurrentTime().plusHours(2));
			
		 assertNotNull(bookings.get(0).teacherAvailability);
		 assertEquals(bookings.get(0).teacherAvailability.id,teacherAvailability2.id);
		 
		 teacherAvailability1=TeacherAvailability.findById(teacherAvailability1.id);
		 assertEquals(teacherAvailability1.studentCount,0);
		 
		 teacherAvailability2=TeacherAvailability.findById(teacherAvailability2.id);
		 assertEquals(teacherAvailability2.studentCount,1);
}
	
	*/
}
