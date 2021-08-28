package org.reussite.appui.support.dashboard.service;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.reussite.appui.support.dashbaord.mapper.ParentNotificationMapper;
import org.reussite.appui.support.dashboard.domain.ParentNotification;
import org.reussite.appui.support.dashboard.model.ParentNotificationEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.model.TeacherNotificationEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class ParentNotificationService {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	@Inject 
	protected ParentNotificationMapper notificationMapper;
	
	
    @Transactional
	public ParentNotification sendNotification(ParentNotification commentDomain) {
    	ParentNotificationEntity comment= notificationMapper.toEntity(commentDomain);
    	TeacherProfileEntity existingTeacher=TeacherProfileEntity.findById(commentDomain.getSender().getId());
    	if(existingTeacher==null) {
    		comment.sender.persistAndFlush();
    	}else {
    		comment.sender=existingTeacher;
    	}
    	Set<StudentParentEntity> recipients= new HashSet<StudentParentEntity>();
    	for(StudentParentEntity parent:comment.recipients) {
    		StudentParentEntity existingParent=StudentParentEntity.findById(parent.id);
        	if(existingParent==null) {
        		existingParent=parent;
        	}
    		existingParent.persistAndFlush();
    		recipients.add(existingParent);
    		comment.recipients=recipients;
    		comment.persistAndFlush();
    	}
    	return notificationMapper.toDomain(comment);
	}
    
    
	public ResultPage<ParentNotification>  searchNotifications( 
			String sortParams,  Integer size, Integer page, String term, String parentId) {
		logger.info("Searching parent notifications with term:{}",term);
		
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, null);
		PanacheQuery<ParentNotificationEntity> query=null;
			query = TeacherNotificationEntity.find("SELECT c FROM ParentNotification c where c.deleteDate IS NULL ",
				sort);
		
		logger.info("Number of notification  found Page:{}, Size:{}",page,size);
		if(query==null) {
			return  new ResultPage<ParentNotification>(page,0,0,Collections. emptyList() );
		}
		List<ParentNotification> result=query.page(page, size).list().stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());;
		logger.info("Number of notifcations  found:{}",result.size());
		ResultPage<ParentNotification> resultPage= new ResultPage<ParentNotification>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	

   
}
