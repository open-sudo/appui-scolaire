package org.reussite.appui.support.dashboard.controller;



import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashbaord.mapper.TeacherProfileMapper;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.domain.TokenResponse;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.TeacherProfileService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/v1/teacher")
@ApplicationScoped
public class TeacherProfileController {
	@Inject
	protected TeacherProfileService teacherProfileService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Inject protected TeacherProfileMapper mapper;

	@Produces(MediaType.APPLICATION_JSON)
	@GET
    @Path("")
	@JsonView(Views.Response.class) 
    public Response searchTeacherProfiles(
    		 @QueryParam("tag") String tag,
    		@DefaultValue("") @QueryParam("firstName") String firstName,
    		@QueryParam("path") String path,
    		@DefaultValue("firstName,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page ) {
		
		ResultPage<TeacherProfile>  result= teacherProfileService.searchTeacherProfiles(tag, firstName==null?"":firstName, sort, path,size, page);
		return Response.ok(result).build();
	}
		   
	@GET
	@Path("{id}")
	@JsonView(Views.Response.class) 
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response getTeacherProfileById(@PathParam("id") String id) {
		logger.info("Executing request of teacher profile by id :{}",id);
		TeacherProfile profile= teacherProfileService.getTeacherProfileById(id);
		logger.info("Executing request of teacher profile by id :{} completed with :{}",id,profile);

		return Response.ok(profile).build();
	}
	@GET
	@Path("email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	@Transactional
	public Response getTeacherProfileByEmail(@PathParam("email") String email) {
		logger.info("Executing request of teacher profile by email :{}",email);
		TeacherProfile profile= teacherProfileService.getTeacherProfileByEmail(email);
		logger.info("Executing request of teacher profile by email :{} completed with :{}",email,profile);

		return Response.ok(profile).build();
	}
	@Path("")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	public Response registerTeacher(@HeaderParam("TenantKey") String tenantKey, @Valid @ConvertGroup(to = ValidationGroups.Patch.class) @JsonView(Views.Request.class)  TeacherProfile profile) {
 		logger.info("Registering teacher profile:{}",profile);
		TeacherProfile result=teacherProfileService.registerTeacher(profile);
		logger.info("Registering tenant profile  completed:{}",result);
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
	@Path("/token")
	@Consumes(MediaType.APPLICATION_JSON)
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	public Response login(@HeaderParam("TenantKey") String tenantKey, @QueryParam("phoneNumber") String phoneNumber) {
 		logger.info("Loggin in teacher :{}",phoneNumber);
 		teacherProfileService.login(phoneNumber);
		logger.info("Teacher login   completed:{}",phoneNumber);
		return Response.ok().status(Response.Status.CREATED).build();
	}

	@GET
	@Path("activate/{phoneNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response activateParent( @PathParam("phoneNumber") String phoneNumber,@QueryParam("activationCode") String activationCode,@QueryParam("refreshToken") String refreshToken) {
		logger.info("Activating teacher:{} with code",phoneNumber,activationCode);
		TokenResponse jwt= teacherProfileService.activateTeacher(phoneNumber,activationCode,refreshToken);
		logger.info("Activating teacher {} with ocde{}, resulting in jwt:",phoneNumber,activationCode,jwt);
		return Response.ok(jwt).build();
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}/approval")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	@Transactional
	public Response approveTeacherProfile(@HeaderParam("TenantKey") String tenantKey, @PathParam("id") String id) {
		logger.info("Approving teacher  with id :{} ",id);
		teacherProfileService.approveTeacherProfile(id);
		logger.info("Approving teacher  completed for id:{}",id);
		return Response.ok().build();
	}
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}/approval")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	@Transactional
	public Response disapproveTeacherProfile(@HeaderParam("TenantKey") String tenantKey, @PathParam("id") String id) {
		logger.info("Disapproving teacher  with id :{} ",id);
		teacherProfileService.disapproveTeacherProfile(id);
		logger.info("Disapproving teacher  completed for id:{}",id);
		return Response.ok().build();
	}
	
	
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	@Transactional
	public Response updateTeacherProfile(@HeaderParam("TenantKey") String tenantKey,@PathParam("id") String id,@Valid @ConvertGroup(to = ValidationGroups.Patch.class) @JsonView(Views.Request.class) TeacherProfile profile) {
		logger.info("Updating teacher profile  :{} ",profile);
		teacherProfileService.updateTeacherProfile(tenantKey,id,profile);
		logger.info("Updating teacher profile completed :{}",profile);
		return Response.ok().build();
	}
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{teacherId}/tag/{tagId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	@Transactional
	public Response tagStudentProfile(@HeaderParam("TenantKey") String tenantKey, @PathParam("teacherId") String teacherId, @PathParam("tagId") String tagId) {
		logger.info("Tagging student profile {} with tag :{} ",teacherId, tagId);
		teacherProfileService.tagTeacherProfile(tenantKey,teacherId,tagId);
		logger.info("Tagging student profile {} with tag :{} completed. ",teacherId, tagId);
		return Response.ok().build();
	}
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{teacherId}/tag/{tagId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class) 
	@Transactional
	public Response untagStudentProfile(@HeaderParam("TenantKey") String tenantKey, @PathParam("teacherId") String teacherId, @PathParam("tagId") String tagId) {
		logger.info("Tagging student profile {} with tag :{} ",teacherId, tagId);
		teacherProfileService.untagTeacherProfile(tenantKey,teacherId,tagId);
		logger.info("Tagging student profile {} with tag :{} completed. ",teacherId, tagId);
		return Response.ok().build();
	}

	
}
