package org.reussite.appui.support.dashboard.service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.mapper.CourseMapper;
import org.reussite.appui.support.dashboard.mapper.ScheduleMapper;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils.DateTimeFormats;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.domain.Schedule;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.ScheduleEntity;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class ScheduleService {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());


	@Inject 
	protected ScheduleMapper scheduleMapper;
	@Inject 
	protected CourseMapper courseMapper;

	@Inject
    @RestClient
    protected CourseService courseService;

	

	public ResultPage<Schedule> searchSchedules(String courseId, List<String> subjectIds, int gradeMin,int gradeMax,String sortParams, Integer size,
			Integer page, String startDate, String endDate, String language) {
		logger.info("Searching schedules for Min grade:{}. Max grade:{}. Start date:{}. End Date:{} and title:{}. Principal:{}. Sort params:{}",gradeMin,gradeMax,startDate,endDate,Arrays.deepToString(subjectIds.toArray()),sortParams);
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, Schedule.class);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DateTimeFormats.DATETIME_FORMAT);
		ZonedDateTime start=ZonedDateTime.parse(startDate,formatter);
		ZonedDateTime end=ZonedDateTime.parse(endDate,formatter);
		List<Schedule> result=new ArrayList<Schedule>();
		logger.info("Language found:{}",language);
		PanacheQuery<ScheduleEntity> query =null;
		if(StringUtils.isNotBlank(courseId)) {
			query= ScheduleEntity.find("select DISTINCT c from Schedule c where c.course.id=?1   and  c.deleteDate IS NULL ",sort,courseId);
		}else if(subjectIds==null || subjectIds.size()==0){
				query= ScheduleEntity.find("select DISTINCT c from Schedule c,  IN(c.course.grades) g where g >=?1 and g<=?2   and  c.deleteDate IS NULL and c.startDate > ?3 and c.startDate < ?4 ",sort,gradeMin,gradeMax,start,end);
		}
		else {
			query= ScheduleEntity.find("select DISTINCT c from Schedule c,  IN(c.course.grades) g where g >=?1 and g<=?2   and  c.deleteDate IS NULL and c.startDate > ?3 and c.startDate < ?4 and (c.course.subject.id) in ?5",sort,gradeMin,gradeMax,start,end, subjectIds);
		}
		result=query.page(page, size).list().stream().map(scheduleMapper::toDomain).collect(Collectors.toList());;
		ResultPage<Schedule> resultPage= new ResultPage<Schedule>(page,query.pageCount(),query.count(),result);
		logger.info("Number of schedules found:{}",result.toArray().length);
		return resultPage;
	}


    @Transactional
	public List<Schedule>  registerSchedule(List<Schedule> schedules) {
    	logger.info("Creating schedule {} ", Arrays.deepToString(schedules.toArray()));
    	List<ScheduleEntity> entities= new ArrayList<ScheduleEntity>();
    	for(Schedule schedule:schedules) {
    		logger.info("Creating schedule :{}",schedule);
    		ScheduleEntity entity= scheduleMapper.toEntity(schedule);
	    	CourseEntity course=getCourse(schedule.getCourseId());
	    	entity.course=course;
	    	entities.add(entity);
	    	entity.persist();
    	}
		logger.info("Persisting all schedules");
		List<Schedule>result=entities.stream().map( t -> scheduleMapper.toDomain(t)).collect(Collectors.toList());
		return result;
	}
 

	private CourseEntity getCourse(String courseId) {
		logger.info("Fetching course:{}",courseId);
		Course course=courseService.getCourse(courseId);
		logger.info("Course received from remote service:{}",course);
		CourseEntity existing=CourseEntity.findById(courseId);
		if(existing==null) {
			logger.info("No existing course found with id:{}",courseId);
			CourseEntity entity=courseMapper.toEntity(course);
			SubjectEntity subject=SubjectEntity.findById(entity.subject.id);
			if(subject==null) {
				entity.subject.persistAndFlush();
			}
			entity.persistAndFlush();
			return entity;
		}
		logger.info("Existing course found:{}",existing);
		return existing;
	}

    @Transactional
	public void updateSchedule(Schedule body) {
    	ScheduleEntity schedule=ScheduleEntity.findById(body.getId());
		 if(schedule==null) {
 			throw new NoSuchElementException(Schedule.class,body.getId());
		 }
		
		
		 if(body.getStartDate()!=null) {
			 schedule.startDate=(body.getStartDate());
		 }
		 if(body.getEndDate()!=null) {
			 schedule.startDate=(body.getEndDate());
		 }
		 schedule.lastUpdateDate=TimeUtils.getCurrentTime();
		 schedule.persistAndFlush();
	}

	public Schedule findById(String id) {
		ScheduleEntity entuty=ScheduleEntity.findById(id);
		Schedule schedule=scheduleMapper.toDomain(entuty);
		return schedule;
	}
    
	
	
}
