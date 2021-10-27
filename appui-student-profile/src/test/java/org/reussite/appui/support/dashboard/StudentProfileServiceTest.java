package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.SecureRandom;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.service.StudentParentService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class StudentProfileServiceTest {

	@InjectMock
    @RestClient
	StudentParentService parentService;
	
	String tenantKey="alpha";

//    @Test
    public void testScheduleCreation() {
    	StudentParent parent= new StudentParent();
    	parent.setFirstName("Peter");
    	parent.setCountryCode(1);
    	parent.setPhoneNumber("45027575634");
    	parent.setLastName("Moonsoon");
    	parent.setId(UUID.randomUUID().toString());
    	Mockito.when(parentService.getStudentParent(parent.getId())).thenReturn(parent);
    	StudentProfile student= new StudentProfile();
    	student.setCreateDate(null);
    	student.setStudentParentId(parent.getId());
    	student.setFirstName("Jane");
    	student.setLastName("Joe");
    	student.setGrade(4);
    	student=	given()
	    	  .header("TenantKey",tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(student)
		      .when().post("/v1/student")
		      .then()
		         .statusCode(201).extract().body().as(StudentProfile.class);
    	assertNotNull(student);
    	assertNotNull(student.getCreateDate());

    }
    
    @Transactional
	public StudentProfileEntity setup() {
    	StudentParentEntity parent= new StudentParentEntity();
		parent.countryCode=3;
		parent.firstName=UUID.randomUUID().toString();
		parent.email=UUID.randomUUID().toString()+"@gmail.com";
		parent.lastName="Jinda";
		parent.phoneNumber=String.valueOf(new SecureRandom().nextInt());
		parent.id=UUID.randomUUID().toString();
		parent.persist();
    	StudentProfileEntity student= new StudentProfileEntity();
    	student.firstName=UUID.randomUUID().toString();
    	student.email=UUID.randomUUID().toString()+"@gmail.com";
    	student.lastName="Jinda";
    	student.parent=parent;
    	student.persist();
    	return student;
	} 
    
//    @Test
	 public void testSeatchStudent() {
		 		setup();
	    		assertTrue(StudentProfileEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/student")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
    
}
