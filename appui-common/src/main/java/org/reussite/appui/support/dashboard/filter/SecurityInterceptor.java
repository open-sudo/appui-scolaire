package org.reussite.appui.support.dashboard.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class SecurityInterceptor implements ContainerRequestFilter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void filter(ContainerRequestContext context) {
        String path=context.getUriInfo().getAbsolutePath().toString();
        MultivaluedMap<String, String> params = context.getUriInfo().getQueryParameters();  
        logger.info("Executing request:{}",path);
        if(params.get("tenantKey")!=null && params.get("tenantKey").size()>0) {
    		logger.info("Tenant key found in url param:{}",params.get("tenantKey"));
    		context.getHeaders().add("TenantKey", params.get("tenantKey").get(0));
    		return;
    	}
    	logger.info("Security filter executed successfully");
    }
}
