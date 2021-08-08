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
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.*;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.reussite.appui.support.dashboard.domain.TagRequest;
import org.reussite.appui.support.dashboard.domain.TagResponse;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.TagService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/v1/tag")
@ApplicationScoped
public class TagController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject 
	protected TagService tagService;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@APIResponses(value = {
		        @APIResponse(responseCode = "400", description = "JWT generation error"),
		        @APIResponse(responseCode = "409", description = "Tag aleeady exists."),
		        @APIResponse(responseCode = "200", description = "Tag created.")})
		    @Operation(summary = "Create a tag with the given name.")
	@Transactional
	public Response createTag(@Valid @ConvertGroup(to = ValidationGroups.Post.class) TagRequest tag) {
			logger.info("Creating a tag :{}",tag);
			TagResponse result=tagService.createTag(tag);
			logger.info("Creating tag completed:{}",result);
			return Response.ok(result).status(Response.Status.CREATED).build();
	}
	

	
	
	@PATCH
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	
	public Response updateTag(@PathParam("id")String id, @Valid @ConvertGroup(to = ValidationGroups.Patch.class) TagRequest body) {
		logger.info("Executing request to update tag :{}",body);
		TagRequest tag=tagService.updateTag( id,body);
		logger.info("Executing request to update tag  completed with body :{} ",tag);
		return Response.ok(tag).build();
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("enable")
	@Produces(MediaType.APPLICATION_JSON)
	public Response enableTags(List<String> ids) {
		logger.info("Executing request to enable tags :{} ",Arrays.deepToString(ids.toArray()));
		List<TagRequest> tags=tagService.enableTags( ids);
		return Response.ok(tags).build();
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Transactional
    public Response searchTags(@Context SecurityContext securityContext,
    		
    		@DefaultValue("") @QueryParam("name") String name,
    		@DefaultValue("createDate,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page) {
		ResultPage<TagResponse> result= tagService.searchTags( name, sort, size, page);
		return Response.ok(result).build();
	}


	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("disable")
	@Produces(MediaType.APPLICATION_JSON)
	public Response disableTag(List<String> ids) {
		logger.info("Executing request to enable tags :{} ",Arrays.deepToString(ids.toArray()));
		List<TagResponse> tags=tagService.disableTag( ids);
		return Response.ok(tags).build();
	}
}
