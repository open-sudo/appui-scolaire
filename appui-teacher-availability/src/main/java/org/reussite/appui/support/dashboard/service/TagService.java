package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.reussite.appui.support.dashboard.domain.Tag;
@ApplicationScoped
@Path("/v1/tag")
@RegisterRestClient(configKey="tag")
@RegisterClientHeaders
public interface TagService {
	
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Tag getTag(@PathParam String id);

}
