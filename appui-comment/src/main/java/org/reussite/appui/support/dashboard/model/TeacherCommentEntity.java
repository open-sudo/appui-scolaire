package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;



@Entity(name = "TeacherComment")
@Table(name = "teacher_comment")
public class TeacherCommentEntity  extends PanacheEntityBase{

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	public String id;
	@ManyToOne
	public TeacherProfileEntity commenter;
	@ManyToOne
	public TeacherProfileEntity approver;
	public String content;
	@ManyToOne
	public StudentBookingEntity studentBooking;
	
	@ManyToOne
	public StudentParentEntity studentParent;
	@ManyToOne
	public StudentProfileEntity studentProfile;

	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate=TimeUtils.getCurrentTime();
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT) 
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT) 
	public ZonedDateTime approveDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT) 
	public ZonedDateTime deleteDate;
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

}
