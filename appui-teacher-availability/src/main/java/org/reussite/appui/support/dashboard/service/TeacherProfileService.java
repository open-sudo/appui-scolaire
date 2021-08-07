package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
@ApplicationScoped
@Path("/v1/teacher")
@RegisterRestClient(configKey="teacher")
@RegisterClientHeaders
public interface TeacherProfileService {

	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	TeacherProfile getTeacherProfile(@PathParam(value = "id") String id);

}
