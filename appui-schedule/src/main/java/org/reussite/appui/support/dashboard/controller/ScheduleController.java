package org.reussite.appui.support.dashboard.controller;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.ScheduleService;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@ApplicationScoped
@Path("/v1/schedule")
public class ScheduleController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Inject
	protected ScheduleService scheduleService;

	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response registerSchedule(@JsonView(Views.Request.class) List<Schedule> schedules) {
		logger.info("Registering  Schedule: :{}",Arrays.deepToString(schedules.toArray()));
		List<Schedule> result = scheduleService.registerSchedule(schedules);
		logger.info("Registering schedule completed:{}",Arrays.deepToString(schedules.toArray()));
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response findById(@PathParam("id") String id) {
		logger.info("Finding schedule by id {} ",id);
		Schedule schedule= scheduleService.findById(id);
		logger.info("Finding schedule by id {} resulted in {} ",id,schedule);
		return Response.ok(schedule).build();
	}
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	public Response updateSchedule(@JsonView(Views.Request.class) Schedule schedule) {
		logger.info("Updating schedule:{} ",schedule);
		scheduleService.updateSchedule(schedule);
		logger.info("Updating schedule completed:{}",schedule);

		return Response.ok().build();
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
    @Path("")
	@Transactional
	@JsonView(Views.Response.class)
    public Response searchSchedules(@Context SecurityContext securityContext,
    		@DefaultValue("") @QueryParam("title") String title,
    		@DefaultValue("FR") @QueryParam("title") String language,
    		@DefaultValue("startDate,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@DefaultValue("0") @QueryParam("gradeMin") Integer gradeMin,
    		@DefaultValue("30") @QueryParam("gradeMax") Integer gradeMax,
	    		@DefaultValue("01/01/2010 00:00:00 -0500") @QueryParam("startDate") String startDate, 
	    		@DefaultValue("01/01/2110 00:00:00 -0500") @QueryParam("endDate") String endDate ) {
		
		ResultPage<Schedule> result= scheduleService.searchSchedules(title, gradeMin,gradeMax, sort, size, page,startDate,endDate,language);
		return Response.ok(result).build();
	}

}
