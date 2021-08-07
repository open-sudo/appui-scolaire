package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.domain.MonetaryAmount;
import org.reussite.appui.support.dashboard.domain.Subject;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.service.CourseService;
import org.reussite.appui.support.dashboard.service.SubjectService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CourseServiceTest {

	
	@Inject
	CourseService parentService;
	
	@Inject
	SubjectService subjectService;
	
	String tenantKey="alpha";
	
	@Transactional
	public SubjectEntity setup1() {
    	SubjectEntity subject = new SubjectEntity();
    	subject.name="Java";
    	subject.persistAndFlush();
		
		
    	return subject;
	} 
	
    @Test
    public void testCourseCreation() {
    	SubjectEntity s=setup1();
    	
    	Subject subject= new Subject();
    	subject.setId(s.id);
    	Course course= new Course();
    	course.setName("Quarkus Programming");
    	course.setSubject(subject);
    	
    	course.setPrices(Arrays.asList(new MonetaryAmount(new BigDecimal(12.34567), "CAD")));
    	

	    Course[] courses=	given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(Arrays.asList(course))
		      .when().post("/v1/course")
		      .then()
		         .statusCode(201).extract().body().as(Course[].class);
    	assertTrue(courses.length>0);
    	assertNotNull(courses[0].getId());
    	assertEquals(courses[0].getPrices().size(),1);
    	assertNotNull(courses[0].getPrices().iterator().next().getCurrencyCode());

    }
    @Transactional
	public CourseEntity setup() {
    	SubjectEntity subject = new SubjectEntity();
    	subject.name="Java";
    	subject.persistAndFlush();
		CourseEntity course= new CourseEntity();
		course.name="java for Beginner";
		course.subject=subject;
		course.language="EN";
		course.grades.add(1);
		course.persistAndFlush();
		
    	return course;
	} 
    
    @Test
	 public void testCourseSearch() {
		 		setup();
	    		assertTrue(CourseEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/course")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
	    
    
}
