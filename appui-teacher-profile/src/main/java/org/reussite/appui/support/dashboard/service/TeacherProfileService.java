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
import org.reussite.appui.support.dashbaord.mapper.TagMapper;
import org.reussite.appui.support.dashbaord.mapper.TeacherProfileMapper;
import org.reussite.appui.support.dashboard.domain.Tag;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TagEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.repository.TeacherProfileRepository;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;


@ApplicationScoped
public class TeacherProfileService {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	
	@ConfigProperty(name = "reussite.appui.conference.url.prefix.teacher") 
	protected String teacherPrefixUrl = "";
	@ConfigProperty(name = "reussite.appui.conference.url.prefix.student") 
	protected String studentPrefixUrl = "";

	@Inject 
	protected TeacherProfileMapper teacherMapper;
	@Inject
	protected TeacherProfileRepository teacherRepo;
	
	@Inject
	@RestClient
	protected TagService tagService;

	@Inject
	protected TagMapper tagMapper;


	public ResultPage<TeacherProfile>  searchTeacherProfiles(String tag,String firstName, String sortParams, Integer size, Integer page) {
		String tagParam=(StringUtils.isEmpty(tag) || StringUtils.isEmpty(tag.trim()))?"null":tag.trim().toLowerCase();

		Sort sort=SearchUtils.getAbsoluteSort(sortParams);
		PanacheQuery<TeacherProfileEntity> query = teacherRepo.find(
				"SELECT   c FROM TeacherProfile c  LEFT JOIN c.tags h where (?2 ='null' OR ?2 = lower(h.name))  and (lower(c.firstName) like concat(concat('%',lower(?1),'%'))  OR  lower(c.lastName) like concat(concat('%',lower(?1),'%'))) and c.deleteDate IS NULL ",
				sort,firstName,tagParam);
		List<TeacherProfile> result=query.page(page, size).list().stream().map(teacherMapper::toDomain).collect(Collectors.toList());

		for(TeacherProfile teacher:result) {
		  	String url=teacher.getConferenceUrl(); 
	  		teacher.setConferenceUrl(studentPrefixUrl+url);
		}
		ResultPage<TeacherProfile> resultPage= new ResultPage<TeacherProfile>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	
	
	@Transactional
	public TeacherProfile registerTeacher(TeacherProfile profile) {
		TeacherProfileEntity entity=teacherMapper.toEntity(profile);
		if(StringUtils.isNoneBlank(profile.getFirstName())) {
			String firstNames[]=profile.getFirstName().trim().split(" ");
			entity.setFirstName("");;
			for(String firstName:firstNames) {
				entity.setFirstName((profile.getFirstName()+" "+firstName).trim());
			}
		}
		logger.info("First name cleaned up into:{}",entity.getFirstName());
		if(StringUtils.isNoneBlank(profile.getLastName())) {
			String lastNames[]=profile.getLastName().trim().split(" ");
			entity.setLastName("");
			for(String lastName:lastNames) {
				entity.setLastName((profile.getLastName()+" "+lastName).trim());
			}
		}
		logger.info("Last name cleaned up into:{}",entity.getLastName());

		if(StringUtils.isNoneBlank(profile.getFirstName(),profile.getLastName())) {
			entity.setConferenceUrl((profile.getFirstName().trim()+"-"+profile.getLastName().trim()).replaceAll("\\s", "").toLowerCase());
			TeacherProfileEntity existing=teacherRepo.findByExactConferenceUrl(entity.getConferenceUrl());
			Random random= new Random();
			String conferenceUrl=entity.getConferenceUrl();
			while(existing!=null) {
				entity.setConferenceUrl(conferenceUrl+random.nextInt(100));
				existing=teacherRepo.findByExactConferenceUrl(entity.getConferenceUrl());
			}
			logger.info("Conference URL generated:{}",entity.getConferenceUrl());

		}
		if(StringUtils.isNotBlank(profile.getPhoneNumber())) {
			entity.setPhoneNumber(profile.getPhoneNumber().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
		}
		teacherRepo.persistAndFlush(entity);
		profile=teacherMapper.toDomain(entity);
		return profile;
	}

	public TeacherProfile getTeacherProfileByEmail(String email) {
		TeacherProfileEntity profile=teacherRepo.findByEmail(email);
		if(StringUtils.isNotBlank(profile.getConferenceUrl()) && !profile.getConferenceUrl().contains(teacherPrefixUrl)) {
			profile.setConferenceUrl(teacherPrefixUrl+profile.getConferenceUrl());
		}
		return teacherMapper.toDomain(profile);
	}
	

	public TeacherProfile getTeacherProfileById(String id) {
		TeacherProfileEntity profile=teacherRepo.findById(id);
		if(StringUtils.isNotBlank(profile.getConferenceUrl()) && !profile.getConferenceUrl().contains(teacherPrefixUrl)) {
			profile.setConferenceUrl(teacherPrefixUrl+profile.getConferenceUrl());
		}
		return teacherMapper.toDomain(profile);
	}
	
	@Transactional
	public void approveTeacherProfile( String id) {
		logger.info("Approving teacher profile:{}",id);
		TeacherProfileEntity profile = teacherRepo.findById(id);
		if(profile==null) {
			return;
		}
		
		profile.setApproveDate(TimeUtils.getCurrentTime());
		teacherRepo.persist(profile);
		logger.info("Approval for teacher profile completed :{}",id);
	}
	
	@Transactional
	public void disapproveTeacherProfile( String id) {
		logger.info("Deleting approval for teacher profile:{}",id);
		TeacherProfileEntity profile = teacherRepo.findById(id);
		if(profile==null ) {
			return;
		}
		
		profile.setApproveDate(null);
		teacherRepo.persist(profile);
		logger.info("Approval for teacher profile deleted for : {}",id);
	}
	@Transactional
	public void updateTeacherProfile(String tenantKey, TeacherProfile body) {
		logger.info("Executing teacher profile update :{}",body.getId());
		TeacherProfileEntity profile = teacherRepo.findById(body.getId());
		logger.info("Teacher profile found in db:{}",profile);

		if (profile == null) {
			throw new NoSuchElementException(TeacherProfile.class,body.getId());
		}

		
		if (StringUtils.isNotBlank(body.getFirstName())) {
			profile.setFirstName (body.getFirstName());
		}
		if (StringUtils.isNotBlank(body.getLastName())) {
			profile.setLastName (body.getLastName());
		}
		if(StringUtils.isNoneBlank(body.getFirstName(),body.getLastName()) && (!body.getFirstName().equalsIgnoreCase(profile.getFirstName()) || !body.getLastName().equalsIgnoreCase(profile.getLastName()))) {
			String conferenceUrl=(body.getFirstName().trim()+"-"+body.getLastName().trim()).replaceAll("\\s", "").toLowerCase();
			TeacherProfileEntity existing=teacherRepo.findByExactConferenceUrl(conferenceUrl);
			Random random= new Random();
			String finalConferenceUrl=conferenceUrl;
			while(existing!=null) {
				finalConferenceUrl=conferenceUrl+random.nextInt(100);
				existing=teacherRepo.findByExactConferenceUrl(finalConferenceUrl);
			}
			profile.setConferenceUrl(finalConferenceUrl);;
		}
		if (StringUtils.isNotBlank(body.getEmail())) {
			profile.setEmail(body.getEmail());
		}
		if (StringUtils.isNotBlank(body.getBiographie())) {
			profile.setBiographie(body.getBiographie());
		}
		if (StringUtils.isNotBlank(body.getImageUrl())) {
			profile.setImageUrl(body.getImageUrl());
		}
		
		if (StringUtils.isNotBlank(body.getSchoolBoard())) {
			profile.setSchoolBoard(body.getSchoolBoard());
		}
		if (StringUtils.isNotBlank(body.getQualifications())) {
			profile.setQualifications( body.getQualifications());
		}
		if (StringUtils.isNotBlank(body.getSchoolName())) {
			profile.setSchoolName(body.getSchoolName());
		}
		if (body.getSubjects() != null && body.getSubjects().size() > 0) {
			profile.setSubjects (body.getSubjects());
		}
		if (body.getGrades() != null && body.getGrades().size() > 0) {
			profile.setGrades (body.getGrades());
		}
		if (StringUtils.isNotBlank(body.getPhoneNumber())) {
			profile.setPhoneNumber(body.getPhoneNumber().replaceAll("[^0-9]", "").replaceFirst("^0+(?!$)", ""));
		}
		
		logger.info("Persisting teacher profile");
		teacherRepo.persist(profile);
	}

	

	public void tagTeacherProfile(String tenantKey, String studentId, String tagId) {
		TagEntity existing=getTag(tenantKey,tagId);
		TeacherProfileEntity profile=teacherRepo.findById(studentId);
		profile.getTags().add(existing);
		teacherRepo.persist(profile);

	}
	

	private TagEntity getTag(String tenantKey,String tagId) {
		Tag tag=tagService.getTag(tenantKey,tagId);
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
	
	public void untagTeacherProfile(String tenantKey, String studentId, String tagId) {
		TagEntity existing=getTag(tenantKey,tagId);
		TeacherProfileEntity profile=teacherRepo.findById(studentId);
		TagEntity deleteMe=null;
		for(TagEntity t:profile.getTags()) {
			if(t.id.equals(existing.id)) {
				deleteMe=t;
			}
		}
		if(deleteMe!=null) {
			profile.getTags().remove(deleteMe);
		}
		teacherRepo.persist(profile);
	}
}
