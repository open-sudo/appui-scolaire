package org.reussite.appui.support.dashboard.model;

import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;



@Entity(name = "Schedule")
@Table(name = "schedule")
public class ScheduleEntity extends PanacheEntityBase{

	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	public String id;

    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate=TimeUtils.getCurrentTime();
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime deleteDate;
	@NotNull(message="Start date may not be null")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern =  TimeUtils.DateTimeFormats.DATETIME_FORMAT)
	public ZonedDateTime startDate;
	@NotNull(message="Start date may not be null")
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime endDate;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();
    
    public Integer durationInMinutes=120;
    public Integer repeatPeriodInDays=0;
  
    @ManyToOne(targetEntity=CourseEntity.class,fetch = FetchType.EAGER)
	public CourseEntity course;


	public  static List<ScheduleEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from Schedule t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	public List<ScheduleEntity> findByTenantAfter(String tenantKey, ZonedDateTime after){
		return find("tenantKey=?1 and sessionDate > ?2",tenantKey.toLowerCase(),after).list();
	}
	
	public List<ScheduleEntity> findByTenantAfterAndGrade(String tenantKey, Integer grade,ZonedDateTime after){
		return find("lower(tenantKey) = ?1 and sessionDate > ?2 and ?3 in grades",tenantKey,after,grade).list();
	}

	public  List<ScheduleEntity> findBySubjectAndTenantKey(String subject, String tenantKey) {
		return find("lower(tenantKey)= ?1 and lower(course.subject) = ?2",tenantKey.toLowerCase(),subject.toLowerCase()).list();
	}
	

	public  List<ScheduleEntity> findBySubjectAndTenantKeyAndStartDate(String subject,String tenantKey, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("lower(tenantKey) = ?1 and lower(course.subject) = ?2 and startDate >?3 ",tenantKey.toLowerCase(),subject.toLowerCase(),startDate,endDate).list();

	}
}
