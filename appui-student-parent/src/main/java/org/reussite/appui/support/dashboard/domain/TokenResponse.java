package org.reussite.appui.support.dashboard.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenResponse {

	public String access_token;
	public String refresh_token;
	public int expires_in;
	public int refresh_expires_in;
	@JsonIgnore
	public String token_type;
	@JsonIgnore
	public String session_state;
	@JsonIgnore
	public String scope;
	@JsonIgnore
	public String not_before_policy;
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
