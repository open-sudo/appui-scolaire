package org.reussite.appui.support.dashboard.model;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;



@Entity(name = "StudentProfile")
@Table(name = "student_profile")
public class StudentProfileEntity extends PanacheEntityBase{
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	public String id;
	
	@ManyToOne
	public StudentParentEntity parent;
	public String email;
    @NotBlank(message="Last name may not be blank")
	public String lastName;
    @NotBlank(message="First name may not be blank")
	public String firstName;
	public Integer grade;
	public String imageUrl;
	public ZonedDateTime registrationDate;
	public String schoolName;
	public String schoolBoard;
	public boolean isTermCondionApproved;
	@JsonIgnore
	public String emailPassword;
	 @Column(unique=true)
	public String conferenceUrl;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime deleteDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate=TimeUtils.getCurrentTime();
	public short onlineStatus;
	public String lastSeenRoom;
	public String phoneNumber;

	@ManyToMany
	public List<TagEntity> tags= new ArrayList<TagEntity>();

	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
	
	

	public  static StudentProfileEntity findByExactConferenceUrl(String path) {
		return find("conferenceUrl = lower(?1)",path.toLowerCase()).firstResult();
	}

	
	public static StudentProfileEntity findByConferenceUrlContaining(String path) {
		return find("conferenceUrl like concat('%',concat(?1,'%'))",path).firstResult();
	}

	public static  List<StudentProfileEntity>  findByParentIdy(String parentId) {
		return find("SELECT DISTINCT c FROM StudentProfile c where c.parent.id=?1   and c.deleteDate IS NULL ",
			parentId).list();
	}
	public static List<StudentProfileEntity> findByParentIdOrEmail(String parentId) {
		return find("select s from StudentProfile s where s.parent.id = ?1 OR  s.parent.email = ?1",parentId).list();
	}
	
	public static long countByParentId(String parentId) {
		return find("select s from StudentProfile s where s.parent.id = ?1 and s.deleteDate is null",parentId).count();
	}

	public static List<StudentProfileEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from StudentProfile t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
	

}
