package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import org.reussite.appui.support.dashboard.domain.Schedule;
@ApplicationScoped

@Path("/v1/schedule")
@RegisterRestClient(configKey = "schedule" )
@RegisterClientHeaders
public interface ScheduleService {

	@GET
	@Path("{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Schedule getSchedule(@PathParam String id);
}
