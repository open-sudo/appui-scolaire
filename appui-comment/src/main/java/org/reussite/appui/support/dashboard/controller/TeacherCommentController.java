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

import org.reussite.appui.support.dashboard.domain.TeacherComment;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TeacherCommentEntity;
import org.reussite.appui.support.dashboard.service.TeacherCommentService;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.validation.ValidationGroups.Post;
import org.reussite.appui.support.dashboard.view.Views;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonView;

@Path("/v1/comment")
@ApplicationScoped
public class TeacherCommentController {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	 @Inject
	 protected TeacherCommentService teacherCommentService;
	 
	@PATCH
	@Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON) 
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response updateTeacherComment(@HeaderParam("TenantKey") String tenantKey,@PathParam("id") String id,
			@Valid @ConvertGroup(to = ValidationGroups.Patch.class) 	@JsonView(Views.WriteMany.class)	TeacherComment comment) {
		logger.info("Updating teacher comemnt:{}",comment);
		TeacherComment response=teacherCommentService.updateTeacherComment(tenantKey,id,comment);
		logger.info("Updating teacher completed:{} ",comment.getId());
		return Response.ok(response).build();
	}
	
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
    public  Response searchTeacherComments(
    		@HeaderParam("TenantKey") String tenantKey,
    		@QueryParam("parentId") String parentId,
    		@QueryParam("studentId") String studentId,
    		@QueryParam("bookingId") String bookingId,
    		@QueryParam("sort") String sort, 
    		@DefaultValue("20") @QueryParam("size")Integer size,
    		@DefaultValue("0") @QueryParam("page") Integer page,
    		@HeaderParam("Authorization") String auth ) {
			ResultPage<TeacherComment>  result= teacherCommentService.searchTeacherComments(  sort,  size, page,studentId,parentId,bookingId);
		return Response.ok(result).build();
	}
	
	@Path("{Id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
    public  Response getComment(
    		@HeaderParam("TenantKey") String tenantKey,
    		@PathParam("id") String id,
    		@HeaderParam("Authorization") String auth ) {
			TeacherCommentEntity comment=TeacherCommentEntity.findById(id);
			if(comment!=null) {
				return Response.ok(comment).build();
			}
			return Response.status(Response.Status.NOT_FOUND).build();
	}
	
	@POST
    @Consumes(MediaType.APPLICATION_JSON) 
	@Path("{id}/approval/{approverId}")
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	public Response approveTeacherComment(@HeaderParam("TenantKey") String tenantKey, @PathParam("id") String id,
			 @PathParam("approverId") 	String approverId) {
		logger.info("Approving teacher comment:{}, by {}",id,approverId);
		teacherCommentService.approveTeacherComment(tenantKey,id,approverId);
		logger.info("Approving teacher completed:{} , {}",id, approverId);
		return Response.ok().build();
	}

	@DELETE
    @Consumes(MediaType.APPLICATION_JSON) 
	@Path("{id}/approval")
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	public Response revertApproveTeacherComment(@HeaderParam("TenantKey") String tenantKey, @PathParam("id") String id) {
		logger.info("Revert approval for  comment:{}",id);
		teacherCommentService.revertApproveTeacherComment(id);
		logger.info("reverting pproval completed:{} , {}",id);
		return Response.ok().build();
	}
	@POST
    @Consumes(MediaType.APPLICATION_JSON) 
	@Path("")
    @Produces(MediaType.APPLICATION_JSON)
	@Transactional
	@JsonView(Views.Read.class)
	public Response createTeacherComment(@HeaderParam("TenantKey") String tenantKey,@Valid @ConvertGroup(to = Post.class)  @JsonView(Views.WriteOnce.class) TeacherComment comment) {
		logger.info("Creating teacher comment. {}",comment);

		logger.info("Creating teacher comment. Student:{}, Commenter: {}",comment.getStudentProfile().getLastName(),comment.getCommenter().getLastName());
		TeacherComment result=teacherCommentService.createTeacherComment(comment);
		logger.info("Created teacher comment. Student:{}, Commenter: {}",comment.getStudentProfile().getLastName(),comment.getCommenter().getLastName());
		return Response.ok(result).status(Response.Status.CREATED).build();
	}
}
