package org.reussite.appui.support.dashboard.controller;



import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashbaord.utils.PhoneUtils;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.service.StudentParentService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@ApplicationScoped
@Path("/v1/parent")
public class StudentParentController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject protected StudentParentService parentService;
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response registerParent(@PathParam("id") String id,@Valid @ConvertGroup(to = ValidationGroups.Post.class)  @JsonView(Views.Request.class) StudentParent parent) {
		logger.info("Creating parent:{}",parent);
		StudentParent result=parentService.registerParent(parent);
		logger.info("Parent creation completed:{}",result);
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
	

	@PATCH
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@Path("{id}")
	@JsonView(Views.Response.class)
	public Response updateParent(@PathParam(value = "id") String id, @Valid @ConvertGroup(to = ValidationGroups.Patch.class) @JsonView(Views.Request.class) StudentParent profile) {
		logger.info("Updating parent:{} with body:{}",id,profile);
		profile.setId(id);
		StudentParent result=parentService.updateParentProfile(profile);
		logger.info("Updating parent completed:{} ",profile);
		return Response.ok(result).build();
	}
	
	@GET
	@Path("activate/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	@Transactional
	public Response activateParent( @PathParam("id") String id,@QueryParam("activationCode") String activationCode) {
		logger.info("Activating parent:{} with code",id,activationCode);
		String jwt= parentService.activateParent(id,activationCode);
		logger.info("Activating parent {} with ocde{}, resulting in jwt:",id,activationCode,jwt);
		return Response.ok("{\"access_token\":\""+jwt+"\"}").build();
	}
	
	
	@GET
	@Path("email/{email}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response findByEmail(@PathParam("email") String email) {
		logger.info("Finding parent by email {} ",email);
		StudentParentEntity parent= StudentParentEntity.findByEmail(email);
		logger.info("Finding parent by email {} resulted in: {}",email,parent);
		return Response.ok(parent).build();
	}
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Transactional
	@JsonView(Views.Response.class)
    public Response  searchStudentParents(
    		
    		@QueryParam("email") String firstName,
    		@DefaultValue("firstName,asc") @QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page ) {
		logger.info("Executing search query:{}, {}",sort);
		ResultPage<StudentParent> result= parentService.searchStudentParents(firstName==null?"":firstName, sort, size, page);
		return Response.ok(result).build();
	}
	
	@GET
	@Path("phoneNumber/{phoneNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response findByPhoneNumber(@PathParam("phoneNumber") String phoneNumber) {
		logger.info("Finding parent by phone {} ",phoneNumber);
		String phone=PhoneUtils.validate(phoneNumber, "");
		StudentParentEntity parent= StudentParentEntity.findByPhoneNumber(phone);
		logger.info("Finding parent by phone {} resulted in {} ",phoneNumber,parent);
		return Response.ok(parent).build();
	}


	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Response.class)
	public Response findById(@PathParam("id") String id) {
		logger.info("Finding parent by id {} ",id);
		StudentParentEntity parent= StudentParentEntity.findById(id);
		logger.info("Finding parent by id {} resulted in {} ",id,parent);
		return Response.ok(parent).build();
	}
	
	
}
