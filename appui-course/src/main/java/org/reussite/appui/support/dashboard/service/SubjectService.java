package org.reussite.appui.support.dashboard.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.reussite.appui.support.dashboard.domain.Subject;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.mapper.SubjectMapper;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.SubjectEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class SubjectService {
    protected final transient Logger logger = LoggerFactory.getLogger(getClass());


	@Inject protected SubjectMapper subjectMapper;
	


	public ResultPage<Subject> searchSubjects( String name,String sortParams, Integer size,
			Integer page,  String language) {
		logger.info("Searching subjects  title:{}. Principal:{}. Sort params:{}",name,language,sortParams);
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, Subject.class);
		List<Subject> result=new ArrayList<Subject>();
		logger.info("Language found:{}",language);
		PanacheQuery<SubjectEntity> query = SubjectEntity.find("select DISTINCT c from Subject c where   lower(c.name) like concat('%',concat(?1,'%')) and ('null' = ?2 or lower(c.language)=?2 )",sort, name.toLowerCase(),String.valueOf(language).toLowerCase());
		result=query.page(page, size).list().stream().map(subjectMapper::toDomain).collect(Collectors.toList());;
		ResultPage<Subject> resultPage= new ResultPage<Subject>(page,query.pageCount(),query.count(),result);
		logger.info("Number of subjects found:{}",result.toArray().length);
		return resultPage;
	}
	

    @Transactional
	public Subject findSubjectById(String id) {
    	SubjectEntity entity=SubjectEntity.findById(id);
    	Subject Subject=subjectMapper.toDomain(entity);
    	return Subject;
    }
    @Transactional
	public List<Subject>  registerSubject(List<Subject> subjects) {
    	logger.info("Creating subjects  {} ", Arrays.deepToString(subjects.toArray()));
    	List<SubjectEntity> entities= new ArrayList<SubjectEntity>();
    	for(Subject subject:subjects) {
    		SubjectEntity entity= subjectMapper.toEntity(subject);
	    	entities.add(entity);
    	}
		SubjectEntity.persist(entities);
		List<Subject>result=entities.stream().map( t -> subjectMapper.toDomain(t)).collect(Collectors.toList());
		return result;
	}
 
    @Transactional
	public void updateSubject(String id,Subject body) {
    	SubjectEntity subject=SubjectEntity.findById(id);
		 if(subject==null) {
 			throw new NoSuchElementException(Subject.class,body.getId().toString());
		 }
		
		 if(StringUtils.isNoneBlank(body.getName())) {
			 subject.name=body.getName();
		 }
		 if(StringUtils.isNoneBlank(body.getLanguage())) {
			 subject.language=body.getLanguage();
		 }
		 subject.lastUpdateDate=TimeUtils.getCurrentTime();
		 subject.persistAndFlush();
	}
   
}
