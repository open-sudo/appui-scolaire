package org.reussite.appui.support.dashboard.model;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.GenericGenerator;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;



@Entity(name = "TeacherAvailability")
@Table(name = "teacher_availability")
public class TeacherAvailabilityEntity extends PanacheEntityBase{
	@Id
	public String id;
	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime createDate =TimeUtils.getCurrentTime();
	@ManyToOne
	@NotNull
	public TeacherProfileEntity teacherProfile;
    public long studentCount;
    @ManyToOne(targetEntity=ScheduleEntity.class,fetch = FetchType.EAGER)
    public ScheduleEntity schedule;
    
    public String duration;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime deleteDate;
    @JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime effectiveStartDate;
	public String supervisorUrl;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();

	
	@ManyToMany
	public List<TagEntity> tags= new ArrayList<TagEntity>();


	@ManyToMany
	public Set<SubjectEntity> subjects= new HashSet<SubjectEntity>();

	@ManyToMany(targetEntity=TeacherProfileEntity.class,fetch = FetchType.EAGER)
	public Set<TeacherProfileEntity> assistants= new HashSet<TeacherProfileEntity>();
	
	public String studentPassword;
	public String teacherPassword;
    @Column(columnDefinition = "text")
	public String conferenceUrl;
	public String conferenceId;
	

	public static List<TeacherAvailabilityEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from TeacherAvailability t, IN(t.tags.name) f where ?1 = lower(f)  AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	
	public static List<TeacherAvailabilityEntity> findBySubjectAndStartDateBetween(String subject, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("select t from TeacherAvailability t, IN(t.teacherProfile.subjects) f where ?1 = f.id AND  t.conferenceUrl IS NOT NULL  AND t.effectiveStartDate IS NOT NULL AND t.schedule.startDate > ?2 AND t.schedule.startDate < ?3",
				Sort.by("studentCount").ascending(),subject,startDate,endDate).list();
	}
	
	public static List<TeacherAvailabilityEntity>findByGradeAndStartDateBetween(int grade, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("select t from TeacherAvailability t, IN(t.teacherProfile.grades) f where ?1= f  AND   t.deleteDate IS NULL  AND t.schedule.startDate > ?2 AND t.schedule.startDate < ?3",Sort.by("studentCount").ascending(),grade,startDate,endDate).list();
	}
	public static List<TeacherAvailabilityEntity> findBySubjectAndGradeAndStartDateBetween(String subject, int grade, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("select t from TeacherAvailability t,  IN(t.teacherProfile.grades) g, IN(t.teacherProfile.subjects) s  where ?1 = s.id AND ?2 = g  AND  t.conferenceUrl IS NOT NULL AND  t.effectiveStartDate IS NOT NULL AND t.schedule.startDate > ?3 AND t.schedule.startDate < ?4",
				Sort.by("studentCount").ascending(),subject,grade,startDate,endDate).list();
	}
	
	
	public static List<TeacherAvailabilityEntity> findByStartDateBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("deleteDate IS NULL AND schedule.startDate > ?1 AND schedule.startDate < ?2",
				Sort.by("studentCount").ascending(),startDate,endDate).list();
	}
	

	public static List<TeacherAvailabilityEntity> findByConferenceUrl( String conferenceUrl) {
		return find("teacherProfile.conferenceUrl like concat('%',?1)",
				Sort.by("studentCount").ascending(),conferenceUrl).list();
	}
	public static List<TeacherAvailabilityEntity> findByStartDateBetweenAndConferenceUrl(ZonedDateTime startDate, ZonedDateTime endDate, String conferenceUrl) {
		return find("teacherProfile.conferenceUrl like concat('%',?3) AND (  schedule.startDate > ?1 AND schedule.startDate < ?2)",
				Sort.by("studentCount").ascending(),startDate,endDate,conferenceUrl).list();
	}
	public static List<TeacherAvailabilityEntity> findBySupervisorUrlAndEffectiveStartDateIsNotNullAndStartDateBetween(
			String path, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("supervisorUrl =?1  AND effectiveStartDate IS NOT NULL AND schedule.startDate > ?2 AND schedule.startDate < ?3",
				path,startDate,endDate).list();
	}
	public static List<TeacherAvailabilityEntity> findBySupervisorUrlIsNullAndEffectiveStartDateIsNotNullAndStartDateBetween(
			ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("supervisorUrl IS NULL  AND effectiveStartDate IS NOT NULL AND schedule.startDate > ?1 AND schedule.startDate < ?2",
				startDate,endDate).list();

	}


	public static long countByTeacherProfile(String id) {
		return find("teacherProfile.id=?1",id).count();
	}
	public static TeacherAvailabilityEntity findByTenantKey(String tenantKey) {
		return find("tenantKey =?1",tenantKey).firstResult();
	}
	public static List<TeacherAvailabilityEntity>findListByTenantKey(String tenantKey) {
		return find("tenantKey =?1",tenantKey).list();
	}

	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}

	
	public static long countByTeacherProfileAndSchedule(TeacherProfileEntity p, ScheduleEntity s) {
		return find("teacherProfile.id=?1 AND ((schedule.startDate>=?2 AND schedule.startDate<=?3) OR (schedule.endDate>=?2 AND schedule.endDate<=?3))",p.id,s.startDate,s.endDate).count();

	}
}
