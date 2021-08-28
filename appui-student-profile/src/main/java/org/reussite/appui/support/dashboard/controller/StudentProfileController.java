package org.reussite.appui.support.dashboard.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.reussite.appui.support.dashbaord.mapper.StudentProfileMapper;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.service.StudentProfileService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/v1/student")
@ApplicationScoped
public class StudentProfileController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected StudentProfileMapper mapper;
	@ConfigProperty(name = "reussite.appui.conference.url.prefix.student") 
	protected String studentPrefixUrl = "";

	 @Inject
	 protected StudentProfileService studentProfileService;

     @Consumes(MediaType.APPLICATION_JSON) 
	 @POST
     @Produces(MediaType.APPLICATION_JSON)
	 @Transactional
     @JsonView(Views.Read.class)
	 public Response  registerStudent(@JsonView(Views.WriteOnce.class)  StudentProfile profile) {
 		logger.info("Registering student profile:{}",profile);
		StudentProfile result=studentProfileService.registerStudent(profile);
		logger.info("Registering student profile  completed:{}",result);
		return Response.ok(result).status(Response.Status.CREATED).build();
	 }
	

	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@JsonView(Views.Read.class)
    public Response searchStudentProfiles(
    		@QueryParam("tag") String tag,
    		
    		@QueryParam("firstName") String firstName,
    		@QueryParam("parentId") String parentId,
    		@DefaultValue("firstName,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page ) {
		logger.info("Executing search query:{}",firstName);
		ResultPage<StudentProfile> result= studentProfileService.searchStudentProfiles(tag, firstName==null?"":firstName, parentId,sort, size, page);
		return Response.ok(result).build();
	}
		

	@PATCH
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	@Path("{id}")
	public Response updateStudent(@PathParam("id") String id,@Valid @ConvertGroup(to = ValidationGroups.Patch.class) @JsonView(Views.WriteMany.class) StudentProfile profile) {
		logger.info("Updating student  with body:{}",profile);
		StudentProfile result=studentProfileService.updateStudentProfile(id,profile);
		logger.info("Updating student completed:{} ",profile);
		return Response.ok(result).build();
	}

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	public Response findById(@PathParam("id") String id) {
		logger.info("Finding student by id {} ",id);
		StudentProfile student=studentProfileService.findById(id);
		logger.info("Finding student by id {} resulted in {} ",id,student);
		return Response.ok(student).build();
	}

	@GET
	@Path("parent/{parentId}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	public Response getStudentProfiles(@PathParam("parentId") String parentId) {
    	logger.info("Looking up students for parent:{}",parentId);
		List<StudentProfileEntity>  profiles= StudentProfileEntity.findByParentIdOrEmail(parentId);
		logger.info("Looking up students for parent:{} resulted in {} ",parentId, Arrays.deepToString(profiles.toArray()));
		List<StudentProfile> students= new ArrayList<StudentProfile>();  
		for(StudentProfileEntity profile:profiles) {
			students.add(mapper.toDomain(profile));
		}
		return Response.ok(students).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{studentId}/tag/{tagId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Read.class)
	@Transactional
	public Response tagStudentProfile( @PathParam("studentId") String studentId, @PathParam("tagId") String tagId) {
		logger.info("Tagging student profile {} with tag :{} ",studentId, tagId);
		StudentProfile result=studentProfileService.tagStudentProfile(studentId,tagId);
		logger.info("Tagging student profile {} with tag :{} completed. ",studentId, tagId);
		return Response.ok(result).build();
	}
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{studentId}/tag/{tagId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Read.class)
	@Transactional
	public Response untagStudentProfile( @PathParam("studentId") String studentId, @PathParam("tagId") String tagId) {
		logger.info("Tagging student profile {} with tag :{} ",studentId, tagId);
		StudentProfile result=studentProfileService.untagStudentProfile(studentId,tagId);
		logger.info("Tagging student profile {} with tag :{} completed. ",studentId, tagId);
		return Response.ok(result).build();
	}
}
