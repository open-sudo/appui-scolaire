package org.reussite.appui.support.dashboard.model;



import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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


@Entity(name = "StudentBooking")
@Table(name = "student_booking")
public class StudentBookingEntity extends PanacheEntityBase{
	@Id
	@GeneratedValue(generator="system-uuid")
	@GenericGenerator(name="system-uuid", strategy = "uuid")
	public String id;
	
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime createDate=TimeUtils.getCurrentTime();
		
	@ManyToOne(fetch = FetchType.EAGER)
    @NotNull(message="Student profile may not be null")
	public StudentProfileEntity studentProfile;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
	public ZonedDateTime deleteDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime teacherAssignedDate;

	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime effectiveStartDate;
	@JsonFormat(pattern = TimeUtils.DateTimeFormats.DATETIME_FORMAT)    
    public ZonedDateTime rejectDate;
	@ManyToOne(targetEntity=ScheduleEntity.class,fetch = FetchType.EAGER)
	public ScheduleEntity schedule;
	public String studentComment;
	
	public ZonedDateTime lastUpdateDate=TimeUtils.getCurrentTime();

	@ManyToMany
	public List<TagEntity> tags= new ArrayList<TagEntity>();

    @Column(columnDefinition = "text")
	public String conferenceUrl;
	
	public StudentBookingEntity() {}
	public StudentBookingEntity(StudentBookingEntity booking) {
		this.schedule=booking.schedule;
		this.studentProfile=booking.studentProfile;
	}


	

	public static long countByConferenceUrlAndEndDateIsNull(String path) {
		return count("studentProfile.conferenceUrl like concat('%', ?1) and deleteDate IS NULL ",path);
	}
	public static long countByStartDateBetween(ZonedDateTime startDate, ZonedDateTime endDate) {
		return count("schedule.startDate > ?1 and schedule.startDate <?2",startDate,endDate);
	}
	
	public static List<StudentBookingEntity> findByConferenceUrlAndDeleteDateIsNullAndEndDateIsNull(String path, ZonedDateTime startDate, ZonedDateTime endDate) {
		return find("studentProfile.conferenceUrl like concat('%', ?1)  and schedule.startDate >?2 and schedule.startDate <?3 and deleteDate IS NULL ",Sort.by("createDate").descending(),path,startDate,endDate).list();

	}
	
	public static List<StudentBookingEntity> findByTag(String tag, ZonedDateTime createDate) {
		return find("select t from StudentBooking t, IN(t.tags.name) f where ?1 = lower(f) AND t.deleteDate IS NULL AND f.createDate > ?2",
				Sort.by("createDate").descending(),tag.toLowerCase(),createDate).list();
	}
	public static List<StudentBookingEntity> findByConferenceUrl(
			String path) {
		return find("studentProfile.conferenceUrl like concat('%', ?1) ",Sort.by("createDate").descending(),path).list();

	}
	
	public static long countByTeacherAvailability(String id) {
		return count("teacherAvailability.id = ?1",id);
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
	

	
	@Override
	public String toString() 
	{ 
	    return ToStringBuilder.reflectionToString(this,ToStringStyle.MULTI_LINE_STYLE); 
	}
	public static long countByTeacherProfile(String id) {
		return count("teacherAvailability.teacherProfile.id = ?1",id);
	}
}
