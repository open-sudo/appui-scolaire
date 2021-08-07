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

@Path("/v1/schedule")
@ApplicationScoped
@RegisterRestClient(configKey="schedule")
@RegisterClientHeaders
public interface ScheduleService {
	
	@Path("{id}")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	Schedule getSchedule(@PathParam String id);

}
