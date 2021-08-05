package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

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
	Schedule getSchedule(@PathParam String id);

}
