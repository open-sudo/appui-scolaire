package org.reussite.appui.support.dashboard.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashboard.service.DataService;
@Path("/v1/data")
@ApplicationScoped
public class DataController {

	@Inject DataService dataService;
	
	
	
	
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	@Transactional
    public Response generateData(
    		@DefaultValue("10") @QueryParam("teacherCount")Integer teacherCount,
    		@DefaultValue("40") @QueryParam("studentCount") Integer studentCount) {
		dataService.generateData(teacherCount,studentCount);
		return Response.ok().build();
	}


}
