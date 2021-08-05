package org.reussite.appui.support.dashboard.exceptions;

import io.vertx.core.json.JsonObject;

public class NoSuchElementException extends ApplicationException
{

	private static final long serialVersionUID = 1L;
	private Class<?> clazz;
	private String id;
	
	public NoSuchElementException(Class<?> clazz, String id) {
		super();
		this.clazz = clazz;
		this.id = id;
	}
   
	public String toJsonString() {
		JsonObject json= new JsonObject();
		json.put("type", clazz.getName().toString());
		json.put("id", id);
		return json.toString();
	}
}
