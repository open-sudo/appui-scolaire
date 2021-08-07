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
import org.reussite.appui.support.dashboard.domain.TeacherAvailability;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.service.ScheduleService;
import org.reussite.appui.support.dashboard.service.TeacherProfileService;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class TeacherAvailabilityServiceTest {

	
	@InjectMock
    @RestClient
	TeacherProfileService teacherService;
	
	@InjectMock
    @RestClient
	ScheduleService scheduleService;
	String tenantKey="alpha";

    @Test
    public void testScheduleCreation() {
    	
    	TeacherProfile teacher= new TeacherProfile();
    	teacher.setFirstName("Peter");
    	teacher.setCountryCode(1);
    	teacher.setPhoneNumber("45027575634");
    	teacher.setLastName("Moonsoon");
    	teacher.setId(UUID.randomUUID().toString());
    	Schedule schedule= new Schedule();
    	schedule.setStartDate(TimeUtils.getCurrentTime().plusHours(5));
    	schedule.setEndDate(TimeUtils.getCurrentTime().plusHours(6));
    	schedule.setId(UUID.randomUUID().toString());
    	Mockito.when(teacherService.getTeacherProfile(teacher.getId())).thenReturn(teacher);
    	Mockito.when(scheduleService.getSchedule(schedule.getId())).thenReturn(schedule);

    	TeacherAvailability availability= new TeacherAvailability();
    	availability.setTeacherProfile(teacher);
    	availability.setCreateDate(null);
    	availability.setSchedule(schedule);
    	availability=
    		given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(availability)
		      .when().post("/v1/availability")
		      .then()
		         .statusCode(201).extract().body().as(TeacherAvailability.class);
    	assertNotNull(availability);
    	assertNotNull(availability.getCreateDate());
    }

    @Transactional
	public TeacherAvailabilityEntity setup() {
    	TeacherProfileEntity teacher= new TeacherProfileEntity();
    	teacher.firstName=("Peter");
    	teacher.phoneNumber=("45027575634");
    	teacher.lastName=("Moonsoon");
    	teacher.id=(UUID.randomUUID().toString());
    	teacher.persist();
    	
    	ScheduleEntity schedule= new ScheduleEntity();
    	schedule.startDate=TimeUtils.getCurrentTime();
    	schedule.endDate=TimeUtils.getCurrentTime().plusHours(2);
    	schedule.id=UUID.randomUUID().toString();
    	schedule.persist();
    	
    	TeacherAvailabilityEntity availability= new TeacherAvailabilityEntity();
    	availability.teacherProfile=(teacher);
    	availability.schedule=(schedule);
    	availability.persist();
    	
    	return availability;
	} 
    
    @Test
	 public void testSearchTeacherAvailability() {
		 		setup();
	    		assertTrue(TeacherAvailabilityEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/availability")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
    
}
