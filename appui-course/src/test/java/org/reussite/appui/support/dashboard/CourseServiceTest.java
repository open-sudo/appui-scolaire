package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.domain.MonetaryAmount;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.CourseService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class CourseServiceTest {

	
	@Inject
	CourseService parentService;
	
	String tenantKey="alpha";
	
    @Test
    public void testCourseCreation() {
    	Course course= new Course();
    	course.setName("Quarkus Programming");
    	course.setSubject("Quarkus for CRUD");
    	
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
		CourseEntity course= new CourseEntity();
		course.name="java";
		course.subject=course.name;
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
