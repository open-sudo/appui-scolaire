package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.service.ScheduleService;
import org.reussite.appui.support.dashboard.service.StudentProfileService;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class StudentBookingServiceTest {

	@InjectMock
    @RestClient
	StudentProfileService studentService;
	
	@InjectMock
    @RestClient
	ScheduleService scheduleService;
	String tenantKey="alpha";

    @Test
    public void testStudentBookingCreation() {
    	
    	StudentProfile student= new StudentProfile();
    	student.setFirstName("Peter");
    	student.setLastName("Moonsoon");
    	student.setId(UUID.randomUUID().toString());
    	Schedule schedule= new Schedule();
    	schedule.setStartDate(TimeUtils.getCurrentTime().plusHours(5));
    	schedule.setEndDate(TimeUtils.getCurrentTime().plusHours(6));
    	schedule.setId(UUID.randomUUID().toString());
    	Mockito.when(studentService.getStudentProfile(student.getId())).thenReturn(student);
    	Mockito.when(scheduleService.getSchedule(schedule.getId())).thenReturn(schedule);

    	StudentBooking booking= new StudentBooking();
    	booking.setStudentProfile(student);
    	booking.setCreateDate(null);
    	booking.setSchedule(schedule);
    	booking=
    		given()
	    	  .header("TenantKey",tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(booking)
		      .when().post("/v1/booking")
		      .then()
		         .statusCode(201).extract().body().as(StudentBooking.class);
    	assertNotNull(booking);
    	assertNotNull(booking.getCreateDate());
    }

    
    
    
    @Transactional
	public StudentProfileEntity setup() {
    	
    	ScheduleEntity schedule= new ScheduleEntity();
    	schedule.startDate=TimeUtils.getCurrentTime();
    	schedule.endDate=TimeUtils.getCurrentTime().plusHours(2);
    	schedule.id=UUID.randomUUID().toString();
    	schedule.persist();
    	StudentProfileEntity student= new StudentProfileEntity();
    	student.firstName=UUID.randomUUID().toString();
    	student.email=UUID.randomUUID().toString()+"@gmail.com";
    	student.lastName="Jinda";
    	student.id=UUID.randomUUID().toString();
    	student.persist();
    	
    	StudentBookingEntity booking= new StudentBookingEntity();
		booking.studentProfile=student;
		booking.schedule=schedule;
    	booking.persist();
    	return student;
	} 
    
    @Test
	 public void testSearchBooking() {
		 		setup();
	    		assertTrue(StudentProfileEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/booking")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
    
}
