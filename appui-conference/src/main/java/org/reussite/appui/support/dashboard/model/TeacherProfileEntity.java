package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;




@Entity(name = "TeacherProfile")
@Table(name = "teacher_profile")
public class TeacherProfileEntity extends PanacheEntityBase{
	@Id
	public String id;
	
	public String phoneNumber;
	public String firstName;
	public String lastName;
	public String email;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime activationDate;
	public String conferenceUrl;
	@ElementCollection(fetch = FetchType.EAGER)
	public Set<Integer>  grades=new HashSet<Integer>();
	@ManyToMany
	public Set<SubjectEntity> subjects= new HashSet<SubjectEntity>();
	  
	public Integer countryCode;

	public static TeacherProfileEntity findByConferenceUrl(String path) {
		return find("conferenceUrl like concat('%',concat(?1,'%'))",path).firstResult();
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
    
