package org.reussite.appui.support.dashboard.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.reussite.appui.support.dashboard.domain.Course;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.mapper.CourseMapper;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.reussite.appui.support.dashboard.model.CourseEntity;
import org.reussite.appui.support.dashboard.model.MonetaryAmountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class CourseService {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());


	@Inject protected CourseMapper courseMapper;
	
	@Inject SubjectService subjectService;

	public ResultPage<Course> searchCourses( String title, int gradeMin,int gradeMax,String sortParams, Integer size,
			Integer page,  String language) {
		logger.info("Searching schedules for  Min grade:{}. Max grade:{}. Start date:{}. End Date:{} and title:{}. Principal:{}. Sort params:{}",gradeMin,gradeMax,title,language,sortParams);
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, Course.class);
		List<Course> result=new ArrayList<Course>();
		logger.info("Language found:{}",language);
		PanacheQuery<CourseEntity> query = CourseEntity.find("select DISTINCT c from Course c , IN(c.grades) g where  g >=?1 and g<=?2    and  c.deleteDate IS NULL and  lower(c.subject.name) like concat('%',concat(?3,'%')) and ('null' = ?4 or lower(c.language)=?4 )",sort,gradeMin,gradeMax, title.toLowerCase(),String.valueOf(language).toLowerCase());
		result=query.page(page, size).list().stream().map(courseMapper::toDomain).collect(Collectors.toList());;
		ResultPage<Course> resultPage= new ResultPage<Course>(page,query.pageCount(),query.count(),result);
		logger.info("Number of schedules found:{}",result.toArray().length);
		return resultPage;
	}
	

    @Transactional
	public Course findCourseById(String id) {
    	CourseEntity entity=CourseEntity.findById(id);
    	Course course=courseMapper.toDomain(entity);
    	return course;
    }
    
    @Transactional
	public List<Course>  registerCourse(List<Course> courses) {
    	logger.info("Creating schedule  {} ", Arrays.deepToString(courses.toArray()));
    	List<CourseEntity> entities= new ArrayList<CourseEntity>();
    	for(Course course:courses) {
    		CourseEntity entity= courseMapper.toEntity(course);
    		SubjectEntity subject=SubjectEntity.findById(course.getSubject().getId());
    		entity.subject=subject;
	    	entity.id=null;
	    	MonetaryAmountEntity.persist(entity.prices);
	    	if(course.getGrades().size()==0) {
		    	for(int i=1; i<12; i++) {
		    		entity.grades.add(i);
		    	}
	    	}
	    	entities.add(entity);
    	}
		CourseEntity.persist(entities);
		List<Course>result=entities.stream().map( t -> courseMapper.toDomain(t)).collect(Collectors.toList());
		return result;
	}
 
    @Transactional
	public void updateCourse(Course body) {
    	CourseEntity schedule=CourseEntity.findById(body.getId());
		 if(schedule==null) {
 			throw new NoSuchElementException(Course.class,body.getId().toString());
		 }
		
		 if((body.getSubject())!=null) {
			//FIXME: Use masptruc schedule.subject=body.getSubject();
		 }
		
		 if(body.getGrades().size()>0) {
			 schedule.grades=body.getGrades();
		 }
		 if(body.getDurationInMinutes()>0) {
			 schedule.durationInMinutes=body.getDurationInMinutes();
		 }
		
		 if(StringUtils.isNotBlank(body.getLanguage())) {
			 schedule.language=(body.getLanguage());
		 }
		 if(StringUtils.isNotBlank(body.getImageUrl())) {
			 schedule.imageUrl=(body.getImageUrl());
		 }
		 schedule.lastUpdateDate=TimeUtils.getCurrentTime();
		 schedule.persistAndFlush();
	}
    
	@Transactional
    public void deleteCourses(String tenantKey,List<String> schedukeIds){
    	List<CourseEntity> schedules=new ArrayList<CourseEntity>();
    	for(String id:schedukeIds) {
    		CourseEntity schedule=CourseEntity.findById(id);
    		if(schedule==null) {
    			throw new NoSuchElementException(Course.class,id);
    		}
    		schedule.deleteDate=TimeUtils.getCurrentTime();
			schedules.add(schedule);
    	}
    	CourseEntity.persist(schedules);
	}
	
}
