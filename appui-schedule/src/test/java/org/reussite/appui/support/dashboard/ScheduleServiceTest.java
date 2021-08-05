package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.mapper.CourseMapper;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.service.CourseService;
import org.reussite.appui.support.dashboard.service.ScheduleService;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.quarkus.test.junit.mockito.InjectSpy;

@QuarkusTest
public class ScheduleServiceTest {

	@InjectMock
    @RestClient 
	CourseService courseService;
	@Inject 
	CourseMapper courseMapper;
	
	@InjectSpy
	ScheduleService scheduleService;
	String tenantKey="alpha";
	@Transactional
	public ScheduleEntity setup() {
		CourseEntity course= new CourseEntity();
		course.id=UUID.randomUUID().toString();
		course.name="java";
		course.subject=course.name;
		course.grades.add(1);
		course.persistAndFlush();
		ScheduleEntity schedule= new ScheduleEntity();
    	schedule.course=course;
    	schedule.startDate=(TimeUtils.getCurrentTime());
    	schedule.endDate=(TimeUtils.getCurrentTime());
    	
    	schedule.persistAndFlush();
    	return schedule;
	}
	 @Test
	    public void testScheduleSearch() {
		 		setup();
	    		assertTrue(ScheduleEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/schedule")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	            Mockito.verify(scheduleService, Mockito.times(1)).searchSchedules(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(),Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(),Mockito.anyString(),Mockito.anyString(),Mockito.anyString()); 
	    }
	    
    @Test
    public void testScheduleCreation() {
    	String tenantKey="alpha";
    	Course course= new Course();
    	course.setName("Quarkus Programming");
    	course.setSubject("Quarkus for CRUD");
    	course.setId(UUID.randomUUID().toString());
    	Mockito.when(courseService.getCourse(course.getId())).thenReturn(course);
    	Schedule schedule= new Schedule();
    	schedule.setCourseId(course.getId());
    	schedule.setStartDate(TimeUtils.getCurrentTime());
    	schedule.setEndDate(TimeUtils.getCurrentTime());
    	
    	Schedule[] schedules=	given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(Arrays.asList(schedule))
		      .when().post("/v1/schedule")
		      .then()
		         .statusCode(201).extract().body().as(Schedule[].class);
    	assertTrue(schedules.length>0);
    	assertNotNull(schedules[0].getId());

    }

   
}
