package org.reussite.appui.support.dashboard.controller;

import java.util.Arrays;
import java.util.List;

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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.reussite.appui.support.dashboard.domain.Subject;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.SubjectService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
@Path("/v1/subject")
public class SubjectController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());


	@Inject
	protected SubjectService subjectService;

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response findById(@PathParam("id") String id) {
		logger.info("Finding Subject by id {} ",id);
		Subject Subject= subjectService.findSubjectById(id);
		logger.info("Finding Subject by id {} resulted in {} ",id,Subject);
		return Response.ok(Subject).build();
	}
	
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response registerSubject(@Valid @ConvertGroup(to = ValidationGroups.Post.class) List<Subject> Subjects) {
		logger.info("Registering Subject  Subject: :{}",Arrays.deepToString(Subjects.toArray()));
		List<Subject> result = subjectService.registerSubject(Subjects);
		logger.info("Registering Subject completed:{}",Arrays.deepToString(Subjects.toArray()));
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
	
	@PATCH
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateSubject(@PathParam("id") String id,@Valid @ConvertGroup(to = ValidationGroups.Patch.class) Subject Subject) {
		logger.info("Updating Subject:{} ",Subject);
		subjectService.updateSubject(id,Subject);
		logger.info("Updating Subject completed:{}",Subject);

		return Response.ok().build();
	}

	@Produces(MediaType.APPLICATION_JSON)
	@GET
    @Path("")
	@Transactional
    public Response searchSubjects(@Context SecurityContext securityContext,
    		@DefaultValue("") @QueryParam("subject") String title,
    		@DefaultValue("EN") @QueryParam("language") String language,
    		@DefaultValue("name,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page) {
		
		ResultPage<Subject> result= subjectService.searchSubjects( title,  sort, size, page,language);
		return Response.ok(result).build();
	}
}
