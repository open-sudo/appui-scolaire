package org.reussite.appui.support.dashboard.controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.service.ConferenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@ApplicationScoped
@Path("/{path}")
public class ConferenceController {
	@Inject protected ConferenceService conferenceService;
		
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional
	public Response meet(@QueryParam(value = "open") Integer open,@PathParam("path") String originalPath) throws IOException, URISyntaxException {
		if(open!=null) {
			if(open==2 || open==1) {
				 conferenceService.setBridgeOpened(open==1);
                 logger.info("Bridget status set to:"+open);
			}
			return Response.ok(String.valueOf(conferenceService.isBridgeOpened())).build();
		}
		URI uri=meet(originalPath);
		return  Response.temporaryRedirect(uri).build();
	}
	
	public URI meet(String originalPath) throws URISyntaxException, IOException {
		logger.info("Meet request received:{}", originalPath);
		String path = originalPath.replaceAll("[^a-zA-Z-]", "");
		logger.info("Path {} cleaned into: {}", originalPath, path);
		
		TeacherProfileEntity teacherProfile = TeacherProfileEntity.findByConferenceUrl(path);
		if(teacherProfile!=null) {
			logger.info("Teacher profile found for path:{}, :{}",path,teacherProfile);
			logger.info("Looking up conference for teacher {}",path);
			return conferenceService.meet(path,teacherProfile).setStudent(false).resolve();
		}
		StudentProfileEntity studentProfile = StudentProfileEntity.findByConferenceUrl(path);
		logger.info("Student profile found for path:{}, :{}",path,studentProfile);
		logger.info("Looking up conference for student {}",path);
		URI uri=conferenceService.meet(path,studentProfile).setStudent(true).resolve();
		return uri;
	}
}
