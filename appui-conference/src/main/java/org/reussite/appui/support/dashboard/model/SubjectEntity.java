package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


@Entity(name = "Subject")
@Table(name = "subject")
public class SubjectEntity  extends PanacheEntityBase {
	

	@Id
	public String id;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate=TimeUtils.getCurrentTime();
    
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
   	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	public String name;
	public String language;
	public Boolean general;
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

}
