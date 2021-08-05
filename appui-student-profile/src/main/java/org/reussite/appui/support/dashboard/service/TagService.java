package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Path;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.Tag;
@ApplicationScoped
@Path("/v1/tag")
@RegisterRestClient(configKey = "tag" )
@RegisterClientHeaders
public interface TagService {
	public Tag getTag(String id);

}
