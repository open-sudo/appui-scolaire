package org.reussite.appui.support.dashboard.domain;

import java.time.ZonedDateTime;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Schedule {

	private String id;
	private String tenantKey;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  TimeUtils.DateTimeFormats.DATETIME_FORMAT)
	private ZonedDateTime createDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  TimeUtils.DateTimeFormats.DATETIME_FORMAT)
	private ZonedDateTime startDate;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  TimeUtils.DateTimeFormats.DATETIME_FORMAT)
	private ZonedDateTime endDate;

	private Integer durationInMinutes=0;
	  private Course course;
	   
	    public Course getCourse() {
			return course;
		}
		public void setCourse(Course course) {
			this.course = course;
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
	public Integer getDurationInMinutes() {
		return durationInMinutes;
	}
	public void setDurationInMinutes(Integer durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTenantKey() {
		return tenantKey;
	}
	public void setTenantKey(String tenantKey) {
		this.tenantKey = tenantKey;
	}
	public ZonedDateTime getCreateDate() {
		return createDate;
	}
	public void setCreateDate(ZonedDateTime createDate) {
		this.createDate = createDate;
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
