package org.reussite.appui.support.dashboard;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


import javax.transaction.Transactional;
import javax.ws.rs.core.MediaType;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.reussite.appui.support.dashboard.domain.TagRequest;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TagEntity;
import org.reussite.appui.support.dashboard.service.TagService;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectSpy;
import static org.hamcrest.CoreMatchers.*;

@QuarkusTest
public class TagServiceTest {

	
	@InjectSpy
    TagService tagService;
	
	String tenantKey="alpha";
	@Transactional
	public void setup() {
		TagEntity tag= new TagEntity();
    	tag.name=("Red");
    	tag.persistAndFlush();
	}
	
	    
    @Test
    public void testTagCreation() {
    	
    	TagRequest tag= new TagRequest();
    	tag.setName("Blue");
    	tag.setEnabled(true);
	    tag=	given()
	    	  .header("TenantKey", tenantKey)
		      .contentType(MediaType.APPLICATION_JSON)
		      .body(tag)
		      .when().post("/v1/tag")
		      .then()
		         .statusCode(201).extract().body().as(TagRequest.class);
            Mockito.verify(tagService, Mockito.times(1)).createTag(Mockito.any(TagRequest.class)); 
            assertNotNull(tag);
            System.out.println("Tag created:"+tag);
    }
    
    @Test
    public void testTagSearch() {
    		setup();
    		@SuppressWarnings("rawtypes")
			ResultPage result=given()
	    	  .header("TenantKey",tenantKey)
		      .when().get("/v1/tag")
		      .then()
		         .statusCode(200).extract().body().as(ResultPage.class);
    		assertNotNull(result);
    		assertTrue(result.content.size()>0);
            Mockito.verify(tagService, Mockito.times(1)).searchTags(Mockito.eq(""), Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()); 

    }
}
