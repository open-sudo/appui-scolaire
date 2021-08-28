package org.reussite.appui.support.dashboard.service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashbaord.mapper.StudentParentMapper;
import org.reussite.appui.support.dashbaord.mapper.StudentProfileMapper;
import org.reussite.appui.support.dashbaord.mapper.TagMapper;
import org.reussite.appui.support.dashboard.domain.StudentParent;
import org.reussite.appui.support.dashboard.domain.StudentProfile;
import org.reussite.appui.support.dashboard.domain.Tag;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.TagEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;


@ApplicationScoped
public class StudentProfileService {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Inject
    @RestClient
	protected StudentParentService parentService;

	
	@Inject
	protected StudentProfileMapper studentMapper;
	
	@Inject
	protected StudentParentMapper parentMapper;
	
	@Inject
	@RestClient
	protected TagService tagService;
	@Inject
	protected TagMapper tagMapper;
	

	@ConfigProperty(name = "reussite.appui.conference.url.prefix.student") 
	protected String studentPrefixUrl = "";

	
	public ResultPage<StudentProfile>  searchStudentProfiles(String tag,String firstName, String parentId,String sortParams, Integer size, Integer page) {
		logger.info("Searching student profiles with  tag:{}",tag);
		Sort sort=SearchUtils.getAbsoluteSort(sortParams);

		PanacheQuery<StudentProfileEntity> query =null;
		String tagParam=(StringUtils.isEmpty(tag) || StringUtils.isEmpty(tag.trim()))?"null":tag.trim().toLowerCase();

		if(StringUtils.isNotBlank(parentId)) {
			query = StudentProfileEntity.find(
					"SELECT DISTINCT c FROM StudentProfile c LEFT JOIN c.tags g  where (?2 ='null' OR ?2 = lower(g.name)) and c.parent.id=?1  and c.deleteDate IS NULL ",
					parentId,tagParam);
		}else {
			query = StudentProfileEntity.find(
				"SELECT  c FROM StudentProfile c  LEFT JOIN c.tags g  where (?2 ='null' OR ?2 = lower(g.name))  and (lower(c.firstName) like concat(concat('%',lower(?1),'%'))  OR  lower(c.lastName) like concat(concat('%',lower(?1),'%'))) and c.deleteDate IS NULL  ",
				sort,firstName,tagParam);
		}
		List<StudentProfile> result=query.page(page, size).list().stream().map(studentMapper::toDomain).collect(Collectors.toList());

		for(StudentProfile student:result) {
		  	String url=student.getConferenceUrl(); 
	  		student.setConferenceUrl(studentPrefixUrl+url);
		}
		ResultPage<StudentProfile> resultPage= new ResultPage<StudentProfile>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	
	public StudentProfile registerStudent(StudentProfile studentProfile) {
		
		if(StringUtils.isAllBlank(studentProfile.getStudentParentId(),studentProfile.getEmail())) {
			throw new IllegalArgumentException("Missing parent email or id");
		}
		List<StudentProfileEntity> children=StudentProfileEntity.findByParentIdOrEmail(studentProfile.getStudentParentId());
		logger.info("Number of children found for parent:{} : {}",studentProfile.getId(),children.size());
		for(StudentProfileEntity child:children) {
			if(child.firstName.equalsIgnoreCase(studentProfile.getFirstName()) && child.lastName.equalsIgnoreCase(studentProfile.getLastName())) {
				logger.info("Child already exist for  parent with this id {}:{} ",studentProfile.getStudentParentId(),child);
				return studentMapper.toDomain(child);
			}
		}
		StudentParentEntity parent=null;
		
		if(StringUtils.isNotBlank(studentProfile.getStudentParentId())) {
			parent=StudentParentEntity.findById(studentProfile.getStudentParentId());
		}
		if(parent==null && StringUtils.isNotBlank(studentProfile.getEmail())) {
			parent=StudentParentEntity.findByEmail(studentProfile.getEmail());
		}
		if(parent==null ) {
			logger.info("Parent :{} not found in local db. fetching it from remote service");
			StudentParent found=parentService.getStudentParent(studentProfile.getStudentParentId());
			parent=parentMapper.toEntity(found);
			logger.info("Parent found from remote service. Storing it:{}",parent);
			parent.persistAndFlush();
		}
		StudentProfileEntity profile=studentMapper.toEntity(studentProfile);
		profile.parent=parent;
		if(StringUtils.isNoneBlank(profile.firstName,profile.lastName)) {
			profile=generateConferenceUrl(profile,profile.firstName.trim(),profile.lastName.trim());
		}
		profile.persistAndFlush();
		return studentMapper.toDomain(profile);
	}
	
	protected StudentProfileEntity generateConferenceUrl(StudentProfileEntity entity, String firstName, String lastName) {
		if(StringUtils.isNoneBlank(firstName,lastName)) {
			entity.conferenceUrl=((firstName.trim()+"-"+lastName.trim()).replaceAll("\\s", "").toLowerCase());
			logger.info("Searching student by conference:{}", entity.conferenceUrl);
			StudentProfileEntity existing=StudentProfileEntity.findByExactConferenceUrl(entity.conferenceUrl);
			Random random= new Random();
			String conferenceUrl=entity.conferenceUrl;
			while(existing!=null) {
				entity.conferenceUrl=(conferenceUrl+random.nextInt(100));
				logger.info("Student already with  conference:{}. Trying new one:{}",conferenceUrl,entity.conferenceUrl);
				existing=StudentProfileEntity.findByExactConferenceUrl(entity.conferenceUrl);
			}
			logger.info("Conference URL generated:{}",entity.conferenceUrl);
		}
		return entity;
	}

	@Transactional
	 public StudentProfile updateStudentProfile(String id,StudentProfile body) {
		StudentProfileEntity profile=StudentProfileEntity.findById(id);
		 if(profile==null) {
			 logger.info("Cound not find student with id:{}",id);
				throw new NoSuchElementException(StudentProfile.class,body.getId());
		 }
		 
		
		 if(StringUtils.isNotBlank(body.getFirstName())) {
			 profile.firstName=(body.getFirstName());
		 }
		 if(StringUtils.isNotBlank(body.getLastName())) {
			 profile.lastName=(body.getLastName());
		 }

		if(StringUtils.isNoneBlank(profile.firstName,profile.lastName)) {
			profile=generateConferenceUrl(profile,profile.firstName.trim(),profile.lastName.trim());
		}

		 if(StringUtils.isNotBlank(body.getImageUrl())) {
			 profile.imageUrl=(body.getImageUrl());
		 }

		 if(StringUtils.isNotBlank(body.getSchoolName())) {
			 profile.schoolName=(body.getSchoolName());
		 }
		 if(body.getGrade()!=null ) {
			 profile.grade=(body.getGrade());
		 }
		 logger.info("Updating tenant now:{}",body.getId());
		 profile.lastUpdateDate=TimeUtils.getCurrentTime();
		 StudentProfileEntity.persist(profile);
		 return studentMapper.toDomain(profile);
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
	
	public StudentProfile tagStudentProfile(String studentId, String tagId) {
		TagEntity existing=getTag(tagId);
		StudentProfileEntity profile=StudentProfileEntity.findById(studentId);
		profile.tags.add(existing);
		profile.persistAndFlush();
		return studentMapper.toDomain(profile);
	}
	

	public StudentProfile untagStudentProfile( String studentId, String tagId) {
		TagEntity existing=getTag(tagId);
		StudentProfileEntity profile=StudentProfileEntity.findById(studentId);
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
		return studentMapper.toDomain(profile);
	}

	public StudentProfile findById(String id) {
		StudentProfileEntity entity= StudentProfileEntity.findById(id);
		StudentProfile student=studentMapper.toDomain(entity);
		return student;
	}
	 
	 

}
