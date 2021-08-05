package org.reussite.appui.support.dashboard.model;



import java.time.ZonedDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;


@Entity(name = "StudentBooking")
@Table(name = "student_booking")
public class StudentBookingEntity extends PanacheEntityBase{
	@Id
	public String id;

	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime teacherAssignedDate;

	public String conferenceUrl;
	
	@ManyToOne
	public StudentProfileEntity studentProfile;

	public static long countByTeacherAvailability(String id) {
		return count("teacherAvailability.id = ?1",id);
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
	
}
