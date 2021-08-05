package org.reussite.appui.support.dashboard.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.service.StudentBookingService;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@ApplicationScoped
@Path("/v1/booking")
public class StudentBookingController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());



	@Inject
	protected StudentBookingService studentBookingService;

	
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response registerStudentBooking(@JsonView(Views.Request.class) StudentBooking booking) {
		logger.info("Registering booking:{}",booking);
		StudentBooking result = studentBookingService.registerStudentBooking(booking);
		logger.info("Registering booking completed:{}",booking);
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
	@Path("")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
    public  Response searchStudents(
    		 @QueryParam("tag") String tag,
    		
    		@QueryParam("firstName") String firstName,
    		@QueryParam("parentId") String parentId,
    		@QueryParam("profileId") String profileId,
    		@QueryParam("availabilityId") String availabilityId,
    		@QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@DefaultValue("01/01/2010 00:00:00 -0500") @QueryParam("startDate") String startDate, 
    		@DefaultValue("01/01/2110 00:00:00 -0500") @QueryParam("endDate") String endDate, @HeaderParam("Authorization") String auth ) {
		ResultPage<StudentBooking>  result= studentBookingService.searchStudentBookings(tag, firstName==null?"":firstName, sort,  size, page, startDate, endDate,profileId,parentId,availabilityId);
		return Response.ok(result).build();
	}
    
    @GET
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @JsonView(Views.Response.class)
	public Response getStudentBooking( @PathParam("id") String id) {
		logger.info("Get booking by id {} :{}",id);
		StudentBooking booking=studentBookingService.findById(id);
		logger.info("Get booking:{} completed",id);
		return Response.ok(booking).build();
	}
    
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	public Response updateStudentBooking(@JsonView(Views.Request.class) StudentBooking booking) {
		logger.info("Updating booking:{} with body:{}",booking);
		studentBookingService.updateStudentBooking(booking);
		logger.info("Updating booking:{} completed",booking);

		return Response.ok().build();
	}


}
