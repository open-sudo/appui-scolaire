package org.reussite.appui.support.dashboard.service;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.Course;
@ApplicationScoped
@Path("/v1/course")
@RegisterRestClient(configKey = "course" )
public interface CourseService {

	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	List<Course> create(@HeaderParam("TenantKey") String tenantKey,List<Course> s);

}
