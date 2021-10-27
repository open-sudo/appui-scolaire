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
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.domain.TeacherComment;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.TeacherCommentEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.service.StudentBookingService;
import org.reussite.appui.support.dashboard.service.StudentParentService;
import org.reussite.appui.support.dashboard.service.StudentProfileService;
import org.reussite.appui.support.dashboard.service.TeacherProfileService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;

@QuarkusTest
public class TeacherCommentServiceTest {
	@InjectMock
    @RestClient
	StudentBookingService bookingService;

	@InjectMock
    @RestClient
	StudentProfileService studentService;
	@InjectMock
    @RestClient
	StudentParentService parentService;
	@InjectMock
    @RestClient
	TeacherProfileService teacherService;
	

	private String tenantKey="alpha";
//    @Test
    public void testTeacherCommentCreation() {
    	StudentParent parent = new StudentParent();
    	parent.setEmail(UUID.randomUUID().toString()+"@gmail.com");
    	parent.setFirstName("Janka");
    	parent.setLastName("Noaws");
    	parent.setPhoneNumber(UUID.randomUUID().toString());
    	parent.setId(UUID.randomUUID().toString());
    	parent.setCountryCode(5);
    	Mockito.when(parentService.getStudentParent(parent.getId())).thenReturn(parent);

    	
    	StudentProfile student= new StudentProfile();
    	student.setFirstName("Peter");
    	student.setLastName("Moonsoon");
    	student.setId(UUID.randomUUID().toString());
    	Mockito.when(studentService.getStudentProfile(student.getId())).thenReturn(student);

    	StudentBooking booking= new StudentBooking();
    	booking.setId(UUID.randomUUID().toString());
    	booking.setStudentProfile(student);
    	Mockito.when(bookingService.getStudentBooking(booking.getId())).thenReturn(booking);

    	TeacherProfile teacher= new TeacherProfile();
    	teacher.setFirstName("Peter");
    	teacher.setLastName("Moonsoon");
    	teacher.setId(UUID.randomUUID().toString());
    	Mockito.when(teacherService.getTeacherProfile(teacher.getId())).thenReturn(teacher);
    	
    	TeacherComment comment = new TeacherComment();
    	comment.setCommenter(teacher);
    	comment.setStudentBooking(booking);
    	comment.setStudentProfile(student);
    	comment.setStudentParent(parent);
    	comment.setContent("Sample content");
    	comment=
    		given()
	    	  .header("TenantKey",tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(comment)
		      .when().post("/v1/comment")
		      .then()
		         .statusCode(201).extract().body().as(TeacherComment.class);
    	assertNotNull(comment);
    	assertNotNull(comment.getCreateDate());
    }
    @Transactional
    public TeacherCommentEntity setup() {
    	StudentParentEntity parent = new StudentParentEntity();
    	parent.email=(UUID.randomUUID().toString()+"@gmail.com");
    	parent.firstName=("Janka");
    	parent.lastName=("Noaws");
    	parent.phoneNumber=(UUID.randomUUID().toString());
    	parent.id=(UUID.randomUUID().toString());
    	parent.countryCode=(5);
    	parent.persist();
    	
    	StudentProfileEntity student= new StudentProfileEntity();
    	student.firstName=("Peter");
    	student.lastName=("Moonsoon");
    	student.id=(UUID.randomUUID().toString());
    	student.persist();
    	
    	StudentBookingEntity booking= new StudentBookingEntity();
    	booking.id=(UUID.randomUUID().toString());
    	booking.studentProfile=(student);
    	booking.persist();
    	TeacherProfileEntity teacher= new TeacherProfileEntity();
    	teacher.firstName=("Peter");
    	teacher.lastName=("Moonsoon");
    	teacher.id=(UUID.randomUUID().toString());
    	teacher.persist();
    	TeacherCommentEntity comment = new TeacherCommentEntity();
    	comment.commenter=(teacher);
    	comment.studentBooking=(booking);
    	comment.studentProfile=(student);
    	comment.studentParent=(parent);
    	comment.content="Sample content";
    	comment.persist();
    	return comment;
    }

//    @Test
    public void testTeacherCommentSearch() {
    	TeacherCommentEntity comment=setup();
    	ResultPage<?> result=
        		given()
    	    	  .header("TenantKey",tenantKey)
    		      .contentType(MediaType.APPLICATION_JSON).queryParam("studentId", comment.studentProfile.id)
    		      .when().get("/v1/comment")
    		      .then()
    		         .statusCode(200).extract().body().as(ResultPage.class);
        	assertNotNull(result.content);
        	assertTrue(result.content.size()>0);

    }
    
    
}
