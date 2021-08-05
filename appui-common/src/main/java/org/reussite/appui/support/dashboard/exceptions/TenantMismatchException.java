package org.reussite.appui.support.dashboard.exceptions;

import io.vertx.core.json.JsonObject;

public class TenantMismatchException extends ApplicationException{
	private static final long serialVersionUID = 1323351801899509847L;
	private String request;
	private String header;
	
	public TenantMismatchException(String request, String header) {
		super();
		this.request = request;
		this.header = header;
	}

	public String toJsonString() {
		JsonObject json= new JsonObject();
		json.put("request", request);
		json.put("header", header);
		return json.toString();
	}
}
