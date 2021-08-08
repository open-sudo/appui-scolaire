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
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.service.StudentParentService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class StudentParentServiceTest {

	
	@Inject
	StudentParentService parentService;
	
	String tenantKey="alpha";
	
    @Test
    public void testStudentParentCreation() {
    	
    	StudentParent parent= new StudentParent();
    	parent.setFirstName("Peter");
    	parent.setCountryCode(1);
    	parent.setPhoneNumber("45027575634");
    	parent.setLastName("Moonsoon");
    	parent.setLanguage("EN");
    	parent.setEmail(UUID.randomUUID().toString());
	    parent=	given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(parent)
		      .when().post("/v1/parent")
		      .then()
		         .statusCode(201).extract().body().as(StudentParent.class);
    	assertNotNull(parent.getId());
    }

    @Transactional
	public StudentParentEntity setup() {
    	StudentParentEntity parent= new StudentParentEntity();
		parent.countryCode=3;
		parent.firstName=UUID.randomUUID().toString();
		parent.email=UUID.randomUUID().toString()+"@gmail.com";
		parent.lastName="Jinda";
		parent.language="EN";
		parent.phoneNumber=String.valueOf(new SecureRandom().nextInt());
		parent.persist();
    	return parent;
	} 
    
    @Test
	 public void testCourseParent() {
		 		setup();
	    		assertTrue(StudentParentEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/parent")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
    
}
