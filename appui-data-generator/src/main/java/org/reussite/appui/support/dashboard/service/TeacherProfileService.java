package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
@ApplicationScoped
@Path("/v1/teacher")
@RegisterRestClient(configKey="teacher")
public interface TeacherProfileService {

	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	@POST
	TeacherProfile create(@HeaderParam("TenantKey") String tenantKey,TeacherProfile s);
}
