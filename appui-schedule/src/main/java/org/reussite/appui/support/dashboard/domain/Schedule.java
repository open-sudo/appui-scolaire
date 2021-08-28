package org.reussite.appui.support.dashboard.domain;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.validation.ValidationGroups;
import org.reussite.appui.support.dashboard.view.Views;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule{

	@JsonView(Views.Response.class)
	@Null
	private String id;
	@JsonView(Views.Response.class)
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)  
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  TimeUtils.DateTimeFormats.DATETIME_FORMAT)
    @NotNull(groups = ValidationGroups.Post.class)
    private ZonedDateTime startDate;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT) 
    @NotNull(groups = ValidationGroups.Post.class)
	private ZonedDateTime endDate;
    
    @NotNull(groups = ValidationGroups.Post.class)
    private Course course;
   
    public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}



	private Integer repeatPeriodInDays=0;
   
	
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

	public ZonedDateTime getStartDate() {
		return startDate;
	}
	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}
	public ZonedDateTime getEndDate() {
		return endDate;
	}
	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
	}


	public Integer getRepeatPeriodInDays() {
		return repeatPeriodInDays;
	}
	public void setRepeatPeriodInDays(Integer repeatPeriodInDays) {
		this.repeatPeriodInDays = repeatPeriodInDays;
	}

    

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
