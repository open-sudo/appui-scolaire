package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.Tag;
@ApplicationScoped
@Path("/v1/tag")
@RegisterRestClient(configKey="tag")
public interface TagService {
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	Tag create(@HeaderParam("TenantKey") String tenantKey,Tag p);	
	
}
