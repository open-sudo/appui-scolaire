package org.reussite.appui.support.dashboard.controller;



import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.groups.ConvertGroup;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashboard.domain.ParentNotification;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.service.ParentNotificationService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups.Post;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/v1/notification/parent")
@ApplicationScoped
public class ParentNotificationController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	 @Inject
	 protected ParentNotificationService notificationService;
	 
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
    public  Response searchNotifications(
    		@HeaderParam("TenantKey") String tenantKey,
    		@QueryParam("sort") String sort, 
    		@QueryParam("term") String term, 
    		@QueryParam("parentId") String parentId, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@HeaderParam("Authorization") String auth ) {
			ResultPage<ParentNotification>  result= notificationService.searchNotifications(  sort,  size, page,term,parentId);
		return Response.ok(result).build();
	}
	
	
	

	
	@POST
    @Consumes(MediaType.APPLICATION_JSON) 
	@Path("")
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	public Response createNotification(@HeaderParam("TenantKey") String tenantKey,@Valid @ConvertGroup(to = Post.class)  @JsonView(Views.WriteOnce.class) ParentNotification notification) {
		logger.info("Creating parent notification. {}",notification);
		ParentNotification result=notificationService.sendNotification(notification);
		logger.info("Created parent notification. Student:{}, Commenter: {}",notification.getSender().getLastName(),notification.getSender().getLastName());
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
}
