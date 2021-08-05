package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.reussite.appui.support.dashboard.domain.Course;
@ApplicationScoped
@Path("/v1/course")
@RegisterRestClient(configKey = "course" )
@RegisterClientHeaders
public interface CourseService {

    @GET
    @Path("{id}")
	public Course getCourse(@PathParam String id);
}
