package org.reussite.appui.support.dashboard.service;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.reussite.appui.support.dashboard.domain.StudentProfile;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@ApplicationScoped
@Path("/v1/student")
@RegisterRestClient(baseUri = "http://appui-student-profile")
public interface StudentProfileService {

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    StudentProfile getStudentProfile(@PathParam String id);
}
