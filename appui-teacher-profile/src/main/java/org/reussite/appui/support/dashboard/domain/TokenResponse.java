package org.reussite.appui.support.dashboard.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

	public String access_token;
	public String refresh_token;
	public int expires_in;
	public int refresh_expires_in;
	public String token_type;
	public String session_state;
	public String scope;
	public String not_before_policy;
	public String id;
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
