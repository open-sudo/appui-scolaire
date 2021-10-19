package org.reussite.appui.support.dashboard.model;



import java.time.ZonedDateTime;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.reussite.appui.support.dashboard.utils.TimeUtils;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import io.quarkus.panache.common.Sort;


@Entity(name = "StudentBooking")
@Table(name = "student_booking")
public class StudentBookingEntity extends PanacheEntityBase{
	@Id
	public String id;

	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime teacherAssignedDate;

	public String conferenceUrl;
	
	@ManyToOne
	public TeacherAvailabilityEntity teacherAvailability;

	@ManyToOne
	public StudentProfileEntity studentProfile;

	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime effectiveStartDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime rejectDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime createDate;
	@ManyToOne(targetEntity=ScheduleEntity.class,fetch = FetchType.EAGER)
	public ScheduleEntity schedule;

	public static long countByTeacherAvailability(String id) {
		return count("teacherAvailability.id = ?1",id);
	}
	


	public static long countByConferenceUrlAndEndDateIsNull(String path) {
		return count("studentProfile.conferenceUrl like concat('%', ?1)  ",path);
	}
	public static long countByStartDateBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
		return count("schedule.startDate > ?1 and schedule.startDate <?2",startDate,endDate);
	}
	
	public static List<StudentBookingEntity> findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(String path, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("studentProfile.conferenceUrl like concat('%', ?1)  and schedule.startDate >?2 and schedule.startDate <?3 ",Sort.by("createDate").descending(),path,startDate,endDate).list();

	}
	
	public static List<StudentBookingEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from StudentBooking t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	public static List<StudentBookingEntity> findByConferenceUrl(
			String path) {
		return find("studentProfile.conferenceUrl like concat('%', ?1) ",Sort.by("createDate").descending(),path).list();

	}
	
	
	public static List<StudentBookingEntity> findByTeacherAvailability(String teacherId) {
		return find("teacherAvailability.id = ?1",teacherId).list();

	}
	public static List<StudentBookingEntity> findByStudentParentId(String tenantKey,String parentId) {
		return find("studentProfile.parent.id = ?1 and tenant.key=?2",parentId,tenantKey).list();
	}
	public static List<StudentBookingEntity> findByTenantKey(String tenantKey) {
		return find("tenant.key = ?1",tenantKey).list();
	}
	

	
	public static long countByTeacherProfile(String id) {
		return count("teacherAvailability.teacherProfile.id = ?1",id);
	}
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
	
}
