package org.reussite.appui.support.dashboard.model;


import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;




@Entity(name = "StudentParent")
@Table(name = "student_parent")
public class StudentParentEntity extends PanacheEntityBase{
	@Id
	public String id;
	@Column(unique=true)
	public String email;
	@NotBlank
	public String phoneNumber;
	public String activationCode;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime activationDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime deleteDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate=TimeUtils.getCurrentTime();
	public String firstName;
	public String lastName;
	public String language="EN";
	@Positive
	public int countryCode;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	


	public static StudentParentEntity findByEmail(String email) {
		return find("lower(email) =?1 ",email.toLowerCase()).firstResult();
	}

	public static StudentParentEntity findByPhoneNumber(String phoneNumber) {
		return find("phoneNumber like concat(concat('%',?1),'%') ",phoneNumber).firstResult();
	}

	public static List<StudentParentEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from StudentParent t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
}
