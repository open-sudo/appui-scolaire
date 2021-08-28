package org.reussite.appui.support.dashbaord.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void filter(ContainerRequestContext context) {
    	SecurityContext securityContext = context.getSecurityContext();
    	if(securityContext.getUserPrincipal()==null) {
    		logger.info("No principal found in request.");
    		return;
    	}
    	String name=securityContext.getUserPrincipal().getName();
    	logger.info("Principal name found in JWT token:{} for tenant:{}",name);
    }
}