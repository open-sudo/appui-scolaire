package org.reussite.appui.support.dashboard.service;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/v1/teacher")
@RegisterRestClient(baseUri = "http://appui-teacher-profile")
public interface TeacherProfileService {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    TeacherProfile getTeacherProfile(@PathParam String id);
}
