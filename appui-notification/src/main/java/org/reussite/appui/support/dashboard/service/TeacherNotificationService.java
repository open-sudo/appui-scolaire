package org.reussite.appui.support.dashboard.service;


import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.reussite.appui.support.dashbaord.mapper.TeacherNotificationMapper;
import org.reussite.appui.support.dashboard.domain.TeacherNotification;
import org.reussite.appui.support.dashboard.model.TeacherNotificationEntity;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class TeacherNotificationService {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	@Inject 
	protected TeacherNotificationMapper notificationMapper;
	
	
    @Transactional
	public TeacherNotification sendNotification(TeacherNotification notificationDomain) {
    	TeacherNotificationEntity notification= notificationMapper.toEntity(notificationDomain);
    	TeacherProfileEntity existingTeacher=TeacherProfileEntity.findById(notificationDomain.getSender().getId());
    	if(existingTeacher==null) {
    		notification.sender.persistAndFlush();
    	}else {
    		notification.sender=existingTeacher;
    	}
    	Set<TeacherProfileEntity> recipients= new HashSet<TeacherProfileEntity>();
    	for(TeacherProfileEntity parent:notification.recipients) {
    		TeacherProfileEntity existingParent=TeacherProfileEntity.findById(parent.id);
        	if(existingParent==null) {
        		existingParent=parent;
        	}
    		existingParent.persistAndFlush();
    		recipients.add(existingParent);
    		notification.recipients=recipients;
    		notification.persistAndFlush();
    	}
    	return notificationMapper.toDomain(notification);
	}
    
    
	public ResultPage<TeacherNotification>  searchNotifications( 
			String sortParams,  Integer size, Integer page, String term,String teacherId) {
		logger.info("Searching parent notifications with term:{}",term);
		
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, null);
		PanacheQuery<TeacherNotificationEntity> query=null;
			query = TeacherNotificationEntity.find("SELECT c FROM TeacherNotification  c where c.deleteDate IS NULL ",
				sort);
		
		logger.info("Number of notifications  found Page:{}, Size:{}",page,size);
		if(query==null) {
			return  new ResultPage<TeacherNotification>(page,0,0,Collections. emptyList() );
		}
		List<TeacherNotification> result=query.page(page, size).list().stream()
                .map(notificationMapper::toDomain)
                .collect(Collectors.toList());;
		logger.info("Number of notifcations  found:{}",result.size());
		ResultPage<TeacherNotification> resultPage= new ResultPage<TeacherNotification>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	

   
}
