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
import org.reussite.appui.support.dashboard.domain.Subject;
import org.reussite.appui.support.dashboard.domain.MonetaryAmount;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.SubjectService;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SubjectServiceTest {

	
	@Inject
	SubjectService parentService;
	
	String tenantKey="alpha";
	
//    @Test
    public void testSubjectCreation() {
    	Subject subject= new Subject();
    	subject.setName("Quarkus Programming");
    	subject.setLanguage("EN");
	    Subject[] subjects=	given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(Arrays.asList(subject))
		      .when().post("/v1/subject")
		      .then()
		         .statusCode(201).extract().body().as(Subject[].class);
    	assertTrue(subjects.length>0);
    	assertNotNull(subjects[0].getId());

    }
    @Transactional
	public SubjectEntity setup() {
		SubjectEntity subject= new SubjectEntity();
		subject.name="java";
		subject.language="EN";
		subject.persistAndFlush();
		
    	return subject;
	} 
    
//    @Test
	 public void testSubjectSearch() {
		 		setup();
	    		assertTrue(SubjectEntity.count()>0);
	    		@SuppressWarnings("rawtypes")
				ResultPage result=given()
				  .header("TenantKey",tenantKey)
			      .when().get("/v1/subject")
			      .then()
			         .statusCode(200).extract().body().as(ResultPage.class);
	    		assertNotNull(result);
	    		assertTrue(result.content.size()>0);
	    }
	    
    
}
