package org.reussite.appui.support.dashboard.service;


import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashbaord.mapper.ScheduleMapper;
import org.reussite.appui.support.dashbaord.mapper.StudentBookingMapper;
import org.reussite.appui.support.dashbaord.mapper.StudentProfileMapper;
import org.reussite.appui.support.dashbaord.mapper.TagMapper;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.domain.StudentBooking;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.domain.Tag;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.TagEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils.DateTimeFormats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;


@ApplicationScoped
public class StudentBookingService {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());
    @Inject protected StudentBookingMapper studentBookingMapper;

	@Inject
	@RestClient
	protected TagService tagService;

	@Inject
	@RestClient
	protected ScheduleService scheduleService;

	@Inject
	@RestClient
	protected StudentProfileService studentService;

	@Inject
	protected TagMapper tagMapper;

	@Inject
	protected ScheduleMapper scheduleMapper;
	
	@Inject
	protected StudentProfileMapper studentMapper;
    @Transactional
	public StudentBooking registerStudentBooking(StudentBooking body) {
    	logger.info("Creating booking:{}",body);
    	StudentBookingEntity booking=studentBookingMapper.toEntity(body);
    	booking.schedule=getSchedule( body.getSchedule().getId());
    	booking.studentProfile=getStudentProfile(body.getStudentProfile().getId());
    	logger.info("Student profile found:{}",booking.studentProfile);

		booking.persistAndFlush();
		return studentBookingMapper.toDomain(booking);
	}

	
	private ScheduleEntity getSchedule(String id) {
		logger.info("Fetching schedule:{}",id);
		Schedule schedule=scheduleService.getSchedule(id);
		ScheduleEntity existing=ScheduleEntity.findById(id);
		if(existing==null) {
			ScheduleEntity entity=scheduleMapper.toEntity(schedule);
			entity.persistAndFlush();
			logger.info("Schedule fetched from remote service:{}",entity);
			return entity;
		}
		scheduleMapper.updateModel(schedule, existing);
		existing.persistAndFlush();
		logger.info("Existing schedule updated :{}",existing);
		return existing;
	}

	private StudentProfileEntity getStudentProfile(String id) {
		logger.info("Fetching student profile:{}",id);

		StudentProfile studentProfile=studentService.getStudentProfile(id);
		StudentProfileEntity existing=StudentProfileEntity.findById(id);
		if(existing==null) {
			StudentProfileEntity entity=studentMapper.toEntity(studentProfile);
			entity.persistAndFlush();
			logger.info("StudentProfile fetched from remote service:{}",entity);
			return entity;
		}
		logger.info("Existing profile found :{}",existing);
		studentMapper.updateModel(studentProfile, existing);
		existing.persistAndFlush();
		return existing;
	}

	public ResultPage<StudentBooking>  searchStudentBookings(String tag,String firstName, String sortParams,  Integer size, Integer page,
			String startDate, String endDate, String profileId, String parentId) {
		logger.info("Searching student bookings with First Name:{}, Profile ID:{}, Parent ID:{}, Availability ID:{}",firstName,profileId,parentId);
		
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, null);
		String tagParam=(tag==null?"null":(tag)).toLowerCase();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormats.DATETIME_FORMAT);
		ZonedDateTime start=ZonedDateTime.parse(startDate,formatter);
		ZonedDateTime end=ZonedDateTime.parse(endDate,formatter);
		PanacheQuery<StudentBookingEntity> query=null;
		if(StringUtils.isNotBlank(profileId)){
			query = StudentBookingEntity.find("SELECT c FROM StudentBooking c LEFT JOIN c.tags h where (?4 ='null' OR lower(h.name) like concat(concat('%',lower(?4),'%')) )  and  c.studentProfile.id=?1  and c.schedule.startDate > ?2 and c.schedule.startDate < ?3 and c.deleteDate IS NULL ",
				sort,profileId,start,end, tag+"");
		}
		
		if(StringUtils.isNotBlank(parentId)){
			query = StudentBookingEntity.find("SELECT c FROM StudentBooking c  LEFT JOIN c.tags h  where (?4 ='null' OR lower(h.name) like concat(concat('%',lower(?4),'%')) )  and  c.studentProfile.parent.id=?1  and c.schedule.startDate > ?2 and c.schedule.startDate < ?3 and c.deleteDate IS NULL",
				sort,parentId,start,end,tagParam);
		}
		if(StringUtils.isAllBlank(profileId,parentId)){
			query = StudentBookingEntity.find("SELECT c FROM StudentBooking c  LEFT JOIN c.tags h  where  (?4 ='null' OR lower(h.name) like concat(concat('%',lower(?4),'%')) )  and (lower(c.studentProfile.firstName) like concat(concat('%',lower(?1),'%'))  OR  lower(c.studentProfile.lastName) like concat(concat('%',lower(?1),'%'))) and c.schedule.startDate > ?2 and c.schedule.startDate < ?3 and c.deleteDate IS NULL ",
				sort,firstName,start,end,tagParam);
		}
		logger.info("Number of student booking found Page:{}, Size:{}",page,size);

		List<StudentBooking> result=query.page(page, size).list().stream()
                .map(studentBookingMapper::toDomain)
                .collect(Collectors.toList());;
		logger.info("Number of student booking found:{}",result.size());
		ResultPage<StudentBooking> resultPage= new ResultPage<StudentBooking>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	
	
    @Transactional
	public void updateStudentBooking(StudentBooking body) {
		 StudentBookingEntity booking=StudentBookingEntity.findById(body.getId());
		 if(booking==null) {
				throw new NoSuchElementException(StudentBooking.class,body.getId());
		 }
		
		 if(body.getSchedule().getId()!=null) {
			 booking.schedule=ScheduleEntity.findById(body.getSchedule().getId());
		 }
		 booking.lastUpdateDate=TimeUtils.getCurrentTime();
		 booking.persistAndFlush();
	}
	

	public void tagStudentBooking(String tenantKey, String studentId, String tagId) {
		TagEntity existing=getTag(tagId);
		StudentBookingEntity profile=StudentBookingEntity.findById(studentId);
		profile.tags.add(existing);
		profile.persistAndFlush();
	}


	private TagEntity getTag(String tagId) {
		Tag tag=tagService.getTag(tagId);
		TagEntity tagEntity=tagMapper.toEntity(tag);
		TagEntity existing=TagEntity.findById(tagId);
		if(existing!=null) {
			existing.name=tagEntity.name;
			existing.url=tagEntity.url;
		}else {
			existing=tagEntity;
		}
		existing.persistAndFlush();
		return existing;
	}
	

	public void untagStudentBooking(String tenantKey, String studentId, String tagId) {
		TagEntity existing=getTag(tagId);
		StudentBookingEntity profile=StudentBookingEntity.findById(studentId);
		TagEntity deleteMe=null;
		for(TagEntity t:profile.tags) {
			if(t.id.equals(existing.id)) {
				deleteMe=t;
			}
		}
		if(deleteMe!=null) {
			profile.tags.remove(deleteMe);
		}
		profile.persistAndFlush();
	}


	public StudentBooking findById(String id) {
		StudentBookingEntity entity=StudentBookingEntity.findById(id);
		return studentBookingMapper.toDomain(entity);
	}
	 
}
