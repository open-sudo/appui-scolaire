package org.reussite.appui.support.dashboard.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
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
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
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
	public Response registerSchedule(@Valid @ConvertGroup(to = ValidationGroups.Post.class)  @JsonView(Views.Request.class) List<Schedule> schedules) {
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
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@JsonView(Views.Response.class)
	public Response updateSchedule(@PathParam("id") String id, @Valid @ConvertGroup(to = ValidationGroups.Patch.class)  @JsonView(Views.Request.class) Schedule schedule) {
		logger.info("Updating schedule:{} with content ",id,schedule);
		scheduleService.updateSchedule(id,schedule);
		logger.info("Updating schedule completed:{}",schedule);

		return Response.ok().build();
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
    @Path("")
	@Transactional
	@JsonView(Views.Response.class)
    public Response searchSchedules(@Context SecurityContext securityContext,
    		@QueryParam("title") List<String> subjectIds,
    		@DefaultValue("FR") @QueryParam("language") String language,
    		@DefaultValue("startDate,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("") @QueryParam("courseId") String courseId, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@QueryParam("grade") List<Integer> grade,
	    		@DefaultValue("01/01/2010 00:00:00 -0500") @QueryParam("startDate") String startDate, 
	    		@DefaultValue("01/01/2110 00:00:00 -0500") @QueryParam("endDate") String endDate ) {
		
		ResultPage<Schedule> result= scheduleService.searchSchedules(grade,courseId,subjectIds,  sort, size, page,startDate,endDate,language);
		return Response.ok(result).build();
	}

}
