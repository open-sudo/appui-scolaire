package org.reussite.appui.support.dashboard.domain;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


@JsonInclude(Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Course{

	private String id;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime createDate=TimeUtils.getCurrentTime();
		
    @NotBlank(message="Subject may not be null")
	private String subject;
	private Set<Integer>  grades= new HashSet<Integer>();
    @NotNull(message="Tenant may not be null")
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	private ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
    private Collection<MonetaryAmount>prices= new HashSet<MonetaryAmount>();

    private Integer durationInMinutes=120;
    private String imageUrl;
    private String name;
    private String description;
  
    private String language;
    
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Collection<MonetaryAmount> getPrices() {
		return prices;
	}
	public void setPrices(Collection<MonetaryAmount> prices) {
		this.prices = prices;
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
	
	
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Set<Integer> getGrades() {
		return grades;
	}
	public void setGrades(Set<Integer> grades) {
		this.grades = grades;
	}
	public ZonedDateTime getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(ZonedDateTime lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public Integer getDurationInMinutes() {
		return durationInMinutes;
	}
	public void setDurationInMinutes(Integer durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}
	public String getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}


	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
    
    
    
}
