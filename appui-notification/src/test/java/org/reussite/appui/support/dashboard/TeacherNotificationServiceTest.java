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
import org.reussite.appui.support.dashboard.domain.TeacherNotification;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TeacherNotificationEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.service.StudentParentService;
import org.reussite.appui.support.dashboard.service.TeacherProfileService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class TeacherNotificationServiceTest {
	
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
    	teacher.setPhoneNumber("151478254545");
    	Mockito.when(teacherService.getTeacherProfile(teacher.getId())).thenReturn(teacher);
    	
    	Set<TeacherProfile> recipients= new HashSet<TeacherProfile>();
    	recipients.add(teacher);
    	
    	TeacherNotification comment = new TeacherNotification();
    	comment.setSender(teacher);
    	comment.setRecipients(recipients);
    	comment.setContent("Sample content");
    	comment=
    		given()
	    	  .header("TenantKey",tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(comment)
		      .when().post("/v1/notification/teacher")
		      .then()
		         .statusCode(201)
		         .extract().body().as(TeacherNotification.class);
    	assertNotNull(comment);
    	assertNotNull(comment.getCreateDate());
    }
    @Transactional
    public TeacherNotificationEntity setup() {
    
    	TeacherProfileEntity teacher= new TeacherProfileEntity();
    	teacher.firstName=("Peter");
    	teacher.lastName=("Moonsoon");
    	teacher.id=(UUID.randomUUID().toString());
    	teacher.email=(UUID.randomUUID().toString()+"@sample.com");
    	teacher.phoneNumber=("150478634546");

    	teacher.persist();
    	

    	Set<TeacherProfileEntity> recipients= new HashSet<TeacherProfileEntity>();
    	recipients.add(teacher);

    	TeacherNotificationEntity comment = new TeacherNotificationEntity();
    	comment.sender=(teacher);
    	comment.recipients=(recipients);
    	comment.content="Sample content";
    	comment.persist();
    	return comment;
    }

//    @Test
    public void testTeacherCommentSearch() {
    	setup();
    	ResultPage<?> result=
        		given()
    	    	  .header("TenantKey",tenantKey)
    		      .contentType(MediaType.APPLICATION_JSON)
    		      .when().get("/v1/notification/teacher")
    		      .then()
    		         .statusCode(200).extract().body().as(ResultPage.class);
        	assertNotNull(result.content);
        	assertTrue(result.content.size()>0);

    }
    
    
}
