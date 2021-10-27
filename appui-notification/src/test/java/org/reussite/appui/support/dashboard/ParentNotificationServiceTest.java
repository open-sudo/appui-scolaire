package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reussite.appui.support.dashboard.domain.ParentNotification;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.domain.TeacherNotification;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.model.ParentNotificationEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.model.TeacherNotificationEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.service.StudentParentService;
import org.reussite.appui.support.dashboard.service.TeacherProfileService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class ParentNotificationServiceTest {
	
	@InjectMock
    @RestClient
	StudentParentService parentService;
	@InjectMock
    @RestClient
	TeacherProfileService teacherService;
	

	private String tenantKey="alpha";
//    @Test
    public void testTeacherCommentCreation() {
    		
    	TeacherProfile teacher= new TeacherProfile();
    	teacher.setFirstName("Peter");
    	teacher.setLastName("Moonsoon");
    	teacher.setId(UUID.randomUUID().toString());
    	teacher.setEmail(UUID.randomUUID().toString()+"@sample.com");
    	teacher.setPhoneNumber("150478654545");
    	teacher.setCountryCode(90);
    	Mockito.when(teacherService.getTeacherProfile(teacher.getId())).thenReturn(teacher);
    	
    	StudentParent parent = new StudentParent();
    	parent.setFirstName("Peter");
    	parent.setLastName("Moonsoon");
    	parent.setId(UUID.randomUUID().toString());
    	parent.setEmail(UUID.randomUUID().toString()+"@sample.com");
    	parent.setPhoneNumber("150478654590");
    	parent.setCountryCode(90);
    	Set<StudentParent> recipients= new HashSet<StudentParent>();
    	recipients.add(parent);
    	
    	ParentNotification comment = new ParentNotification();
    	comment.setSender(teacher);
    	comment.setRecipients(recipients);
    	comment.setContent("Sample content");
    	comment=
    		given()
	    	  .header("TenantKey",tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(comment)
		      .when().post("/v1/notification/parent")
		      .then()
		         .statusCode(201)
		         .extract().body().as(ParentNotification.class);
    	assertNotNull(comment);
    	assertNotNull(comment.getCreateDate());
    }
    @Transactional
    public ParentNotificationEntity setup() {
    
    	TeacherProfileEntity teacher= new TeacherProfileEntity();
    	teacher.firstName=("Peter");
    	teacher.lastName=("Moonsoon");
    	teacher.id=(UUID.randomUUID().toString());
    	teacher.email=(UUID.randomUUID().toString()+"@sample.com");
    	teacher.phoneNumber=("150478634546");

    	teacher.persist();
    	

    	StudentParentEntity parent = new StudentParentEntity();
    	parent.firstName=("Peter");
    	parent.lastName=("Moonsoon");
    	parent.id=(UUID.randomUUID().toString());
    	parent.email=(UUID.randomUUID().toString()+"@sample.com");
    	parent.phoneNumber=("150478654590");
    	parent.countryCode=90;
    	parent.persist();
    	Set<StudentParentEntity> recipients= new HashSet<StudentParentEntity>();
    	recipients.add(parent);
    	
    	ParentNotificationEntity comment = new ParentNotificationEntity();
    	comment.sender=teacher;
    	comment.recipients=(recipients);
    	comment.content=("Sample content");
    	comment.persistAndFlush();
    	return comment;
    }

//    @Test
    public void testTeacherCommentSearch() {
    	setup();
    	ResultPage<?> result=
        		given()
    	    	  .header("TenantKey",tenantKey)
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .when().get("/v1/notification/parent")
    		      .then()
    		         .statusCode(200).extract().body().as(ResultPage.class);
        	assertNotNull(result.content);
        	assertTrue(result.content.size()>0);

    }
    
    
}
