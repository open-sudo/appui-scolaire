package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.repository.TeacherProfileRepository;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TeacherProfileServiceTest {

	String tenantKey="alpha";

    @Test
    public void testTeacherCreation() {
    	
    	TeacherProfile teacher= new TeacherProfile();
    	teacher.setCreateDate(null);
    	teacher.setFirstName("Jane");
    	teacher.setLastName("Joe");
    	teacher.setEmail("it@let.do");
    	teacher=
    		given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(teacher)
		      .when().post("/v1/teacher")
		      .then()
		         .statusCode(201).extract().body().as(TeacherProfile.class);
    	assertNotNull(teacher);
    	assertNotNull(teacher.getCreateDate());
    	assertNotNull(teacher.getId());

    }
    
    @Inject TeacherProfileRepository teacherRepo;
    
    @Transactional
	public TeacherProfileEntity setup() {
    	TeacherProfileEntity parent= new TeacherProfileEntity();
		parent.setCountryCode(5);
		parent.setFirstName(UUID.randomUUID().toString());
		parent.setEmail(UUID.randomUUID().toString()+"@gmail.com");
		parent.setLastName("Jinda");
		parent.setPhoneNumber(String.valueOf(new SecureRandom().nextInt()));
		teacherRepo.persist(parent);
    	return parent;
	} 
    
    @Test
	 public void testSearchTeacher() {
		 		setup();
	    		assertTrue(teacherRepo.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/teacher")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
    
}
