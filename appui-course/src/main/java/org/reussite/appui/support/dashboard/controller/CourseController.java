package org.reussite.appui.support.dashboard.controller;

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
import javax.ws.rs.HeaderParam;
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

import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.CourseService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("/v1/course")
public class CourseController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Inject
	protected CourseService courseService;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response findById(@PathParam("id") String id) {
		logger.info("Finding course by id {} ",id);
		Course course= courseService.findCourseById(id);
		logger.info("Finding course by id {} resulted in {} ",id,course);
		return Response.ok(course).build();
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response registerCourse(@Valid @ConvertGroup(to = ValidationGroups.Post.class) List<Course> courses) {
		logger.info("Registering course  Course: :{}",Arrays.deepToString(courses.toArray()));
		List<Course> result = courseService.registerCourse(courses);
		logger.info("Registering course completed:{}",Arrays.deepToString(courses.toArray()));
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
	
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateCourse(@PathParam("id") String id,@Valid @ConvertGroup(to = ValidationGroups.Patch.class)  Course course) {
		logger.info("Updating course:{} ",course);
		courseService.updateCourse(id,course);
		logger.info("Updating course completed:{}",course);

		return Response.ok().build();
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
    @Path("")
	@Transactional
    public Response searchCourses(@Context SecurityContext securityContext,
    		@DefaultValue("") @QueryParam("subject") String title,
    		@DefaultValue("EN") @QueryParam("language") String language,
    		@DefaultValue("subject,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@DefaultValue("0") @QueryParam("gradeMin") Integer gradeMin,
    		@DefaultValue("30") @QueryParam("gradeMax") Integer gradeMax) {
		
		ResultPage<Course> result= courseService.searchCourses( title, gradeMin,gradeMax, sort, size, page,language);
		return Response.ok(result).build();
	}

	@DELETE
	@Path("/")
    @Produces(MediaType.APPLICATION_JSON)
	public Response deleteCourse(@HeaderParam("TenantKey") String tenantKey,@QueryParam("id") List<String> ids) {
		logger.info("Deleting {} courses:{} ",ids.size(),Arrays.deepToString(ids.toArray()));
		courseService.deleteCourses(tenantKey,ids);
		logger.info("Deleting courses completed:{} ",Arrays.deepToString(ids.toArray()));
		return Response.ok().build();
	}
}
