package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.StudentParent;
@ApplicationScoped
@Path("/v1/parent")
@RegisterRestClient(configKey="parent")
public interface StudentParentService {
	

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	StudentParent create(@HeaderParam("TenantKey") String tenantKey,StudentParent p);	
}
