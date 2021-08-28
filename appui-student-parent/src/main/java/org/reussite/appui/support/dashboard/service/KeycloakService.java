package org.reussite.appui.support.dashboard.service;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.reussite.appui.support.dashboard.domain.TokenResponse;

@ApplicationScoped
@Path("auth/realms/appui")
@RegisterRestClient(configKey="keycloak")
@RegisterClientHeaders
public interface KeycloakService {
	
	@GET
	@Path("sms/authentication-code")
	public String register(@QueryParam("phoneNumber") String phoneNumber);
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
	@POST
	@Path("protocol/openid-connect/token")
	TokenResponse activate(@FormParam("grant_type") String grantType, @FormParam("phone_number") String phoneNumber, @FormParam("code") String code, @FormParam("client_id") String clientId, @FormParam("client_secret") String clientSecret,@FormParam("refresh_token") String refreshToken);
	

}
