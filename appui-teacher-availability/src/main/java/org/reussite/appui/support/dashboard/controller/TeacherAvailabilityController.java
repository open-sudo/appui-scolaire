package org.reussite.appui.support.dashboard.controller;

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
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.domain.TeacherAvailability;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TeacherAvailabilityEntity;
import org.reussite.appui.support.dashboard.service.TeacherAvailabilityService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;


@Path("/v1/availability")
@ApplicationScoped
public class TeacherAvailabilityController {
	@Inject
	protected TeacherAvailabilityService teacherAvailabilityService;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Transactional
	@JsonView(Views.Response.class)
    public Response searchTeacherAvailabilities(
    		 @QueryParam("tag") String tag,
    		@HeaderParam("TenantKey") String tenantKey,
    		@DefaultValue("") @QueryParam("firstName") String firstName,
    		 @QueryParam("teacherId") String teacherId,
    		@DefaultValue("firstName,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@DefaultValue("01/01/2010 00:00:00 -0500") @QueryParam("startDate") String startDate, 
    		@DefaultValue("01/01/2110 00:00:00 -0500") @QueryParam("endDate") String endDate ) {
		
		ResultPage<TeacherAvailability>  result= teacherAvailabilityService.searchTeacherAvailabilities(tag,tenantKey, firstName==null?"":firstName,teacherId, sort, size, page, startDate, endDate);
		return Response.ok(result).build();
	}
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("booking")
	@Transactional
	@JsonView(Views.Response.class)
    public Response findStudentBookings(
    		@HeaderParam("TenantKey") String tenantKey,
    		@QueryParam("id") List<String> id) {
		List<StudentBooking> bookings= teacherAvailabilityService.findStudentBookings(id);
		return Response.ok(bookings).build();
	}
			
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{availabilityId}/booking")
	@Transactional
	@JsonView(Views.Response.class)
    public Response searchStudentProfiles(
    		@HeaderParam("TenantKey") String tenantKey,
    		@PathParam("availabilityId") String availabilityId) {
		List<StudentBooking> bookings= teacherAvailabilityService.searchStudentBooking(tenantKey,availabilityId);
		return Response.ok(bookings).build();
	}
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Path("/{availabilityId}")
	@Transactional
	@JsonView(Views.Response.class)
    public Response getAvailability(
    		@HeaderParam("TenantKey") String tenantKey,
    		@PathParam("availabilityId") String availabilityId) {
		TeacherAvailability availability= teacherAvailabilityService.getAvailability(availabilityId);
		return Response.ok(availability).build();
	}
		
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response registerTeacherAvailability(@HeaderParam("TenantKey") String tenantKey,@Valid @ConvertGroup(to = ValidationGroups.Post.class)  @JsonView(Views.Request.class) TeacherAvailability availability) {
 		logger.info("Registering teacher availability in tenant:{},  :{}",tenantKey,availability);
 		logger.info("Number of teacher availabilities before insertion:{}",TeacherAvailabilityEntity.count());
 		TeacherAvailability result=teacherAvailabilityService.registerAvailability(availability);
 		logger.info("Number of teacher availabilities after insertion:{}",TeacherAvailabilityEntity.count());

 		logger.info("Registering teacher availability completed:{}",result);
		return Response.ok(result).status(Response.Status.CREATED).build();
	}


	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@Transactional
	public Response updateTeacherAvailability(@HeaderParam("TenantKey") String tenantKey,@PathParam("id") String id,@Valid @ConvertGroup(to = ValidationGroups.Patch.class)  @JsonView(Views.Request.class) TeacherAvailability availability) {
		logger.info("Updating availabilty:{} , with body:{}",tenantKey,availability);
		TeacherAvailability result=teacherAvailabilityService.updateTeacherAvailability(tenantKey,id,availability);
		logger.info("Updating availabilty completed:{} ",availability);
		return Response.ok(result).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{availabilityId}/assistant/{teacherId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response assignTeacherAssistants(@HeaderParam("TenantKey") String tenantKey, @PathParam("teacherId")  String teacherId, @PathParam("availabilityId") String availabilityId) {
		logger.info("Assigning teacher assistant:{} to availability :{}",teacherId,availabilityId);
		TeacherAvailability result=teacherAvailabilityService.assignAssistant(tenantKey,teacherId,availabilityId);
		return Response.ok(result).build();
	}
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{availabilityId}/booking/{bookingId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response assignTeacherBooking(@HeaderParam("TenantKey") String tenantKey, @PathParam("bookingId")  String bookingId, @PathParam("availabilityId") String availabilityId) {
		logger.info("Assigning booking :{} to availability :{}",bookingId,availabilityId);
		TeacherAvailability result=teacherAvailabilityService.assignTeacher(tenantKey,bookingId,availabilityId);
		return Response.ok(result).build();
	}
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{availabilityId}/booking/{bookingId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response unassignTeacherBooking(@HeaderParam("TenantKey") String tenantKey, @PathParam("bookingId")  String bookingId, @PathParam("availabilityId") String availabilityId) {
		logger.info("Assigning booking :{} to availability :{}",bookingId,availabilityId);
		TeacherAvailability result=teacherAvailabilityService.unassignTeacher(tenantKey,bookingId,availabilityId);
		return Response.ok(result).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{availabilityId}/assistant/{teacherId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response unassignTeacherAssistants(@HeaderParam("TenantKey") String tenantKey, @PathParam("teacherId")  String teacherId, @PathParam("availabilityId") String availabilityId) {
		logger.info("Unassigning teacher assistant:{} to availability :{}",teacherId,availabilityId);
		TeacherAvailability result=teacherAvailabilityService.assignAssistant(tenantKey,teacherId,availabilityId);
		return Response.ok(result).build();
	}
	
	

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{availabilityId}/tag/{tagId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response tagStudentProfile(@HeaderParam("TenantKey") String tenantKey, @PathParam("availabilityId") String availabilityId, @PathParam("tagId") String tagId) {
		logger.info("Tagging availability {} with tag :{} ",availabilityId, tagId);
		TeacherAvailability result=teacherAvailabilityService.tagTeacherAvailability(tenantKey,availabilityId,tagId);
		logger.info("Tagging availability  {} with tag :{} completed. ",availabilityId, tagId);
		return Response.ok(result).build();
	}
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{availabilityId}/tag/{tagId}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	@Transactional
	public Response untagStudentProfile(@HeaderParam("TenantKey") String tenantKey, @PathParam("availabilityId") String availabilityId, @PathParam("tagId") String tagId) {
		logger.info("Tagging availability {} with tag :{} ",availabilityId, tagId);
		TeacherAvailability result=teacherAvailabilityService.untagTeacherAvailability(tenantKey,availabilityId,tagId);
		logger.info("Tagging availability  {} with tag :{} completed. ",availabilityId, tagId);
		return Response.ok(result).build();
	}

	
	
}
