package org.reussite.appui.support.dashboard.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Subject {
	

	private String id;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();
    
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
  	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
    @NotEmpty(groups = ValidationGroups.Post.class)
	private String name;
    @NotEmpty(groups = ValidationGroups.Post.class)
	private String language;
	private Boolean general;

	
	public Boolean getGeneral() {
		return general;
	}
	public void setGeneral(Boolean general) {
		this.general = general;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ZonedDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}


}
