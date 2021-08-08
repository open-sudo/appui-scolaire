package org.reussite.appui.support.dashboard.service;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.reussite.appui.support.dashbaord.mapper.StudentBookingMapper;
import org.reussite.appui.support.dashbaord.mapper.StudentParentMapper;
import org.reussite.appui.support.dashbaord.mapper.StudentProfileMapper;
import org.reussite.appui.support.dashbaord.mapper.TeacherCommentMapper;
import org.reussite.appui.support.dashbaord.mapper.TeacherProfileMapper;
import org.reussite.appui.support.dashboard.domain.TeacherComment;
import org.reussite.appui.support.dashboard.domain.TeacherProfile;
import org.reussite.appui.support.dashboard.exceptions.NoSuchElementException;
import org.reussite.appui.support.dashboard.model.ResultPage;
import org.reussite.appui.support.dashboard.model.StudentBookingEntity;
import org.reussite.appui.support.dashboard.model.StudentParentEntity;
import org.reussite.appui.support.dashboard.model.StudentProfileEntity;
import org.reussite.appui.support.dashboard.model.TeacherCommentEntity;
import org.reussite.appui.support.dashboard.model.TeacherProfileEntity;
import org.reussite.appui.support.dashboard.utils.SearchUtils;
import org.reussite.appui.support.dashboard.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ApplicationScoped
public class TeacherCommentService {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

	@Inject 
	protected TeacherCommentMapper commentMapper;
	@Inject 
	protected TeacherProfileMapper teacherMapper;
	@Inject 
	protected StudentBookingMapper bookingMapper;
	@Inject 
	protected StudentParentMapper parentMapper;
	@Inject 
	protected StudentProfileMapper studentMapper;
	@RestClient
	@Inject
	StudentParentService parentService;
	@RestClient
	@Inject
	StudentBookingService bookingService;
	@RestClient
	@Inject
	StudentProfileService profileService;
	@RestClient
	@Inject
	TeacherProfileService teacherService;
	
	
    @Transactional
	public TeacherComment createTeacherComment(TeacherComment commentDomain) {
    	TeacherCommentEntity comment= commentMapper.toEntity(commentDomain);
    	TeacherProfileEntity existingTeacher=TeacherProfileEntity.findById(commentDomain.getCommenter().getId());
    	if(existingTeacher==null) {
    		comment.commenter.persistAndFlush();
    	}else {
    		comment.commenter=existingTeacher;
    	}
    	StudentProfileEntity existingStudent=StudentProfileEntity.findById(comment.studentProfile.id);
    	if(existingStudent==null) {
        	comment.studentProfile.persistAndFlush();
    	}else {
    		comment.studentProfile=existingStudent;
    	}
    	StudentBookingEntity existingBooking=StudentBookingEntity.findById(comment.studentBooking.id);
    	if(existingBooking==null) {
    		comment.studentBooking.studentProfile=comment.studentProfile;
    		comment.studentBooking.persistAndFlush();
    	}else {
    		comment.studentBooking=existingBooking;
    	}
    	StudentParentEntity existingParent=StudentParentEntity.findById(comment.studentParent.id);
    	if(existingParent==null) {
    		comment.studentParent.persistAndFlush();
    	}else {
    		comment.studentParent=existingParent;
    	}
    	logger.info("Student profile :"+comment.studentBooking.studentProfile.id);
    	comment.persistAndFlush();
    	return commentMapper.toDomain(comment);
	}
	public ResultPage<TeacherComment>  searchTeacherComments( String sortParams,  Integer size, Integer page,
			 String studentId, String parentId, String bookingId) {
		logger.info("Searching comments with  Profile ID:{}, Parent ID:{}, BookingID ID:{}",studentId,parentId,bookingId);
		
		Sort sort=SearchUtils.getAbsoluteSort(sortParams, null);
		PanacheQuery<TeacherCommentEntity> query=null;
		if(StringUtils.isNotBlank(studentId)){
			query = TeacherCommentEntity.find("SELECT c FROM TeacherComment c where c.studentProfile.id=?1  and c.deleteDate IS NULL ",
				sort,studentId);
		}
		if(StringUtils.isNotBlank(bookingId)){
			query = TeacherCommentEntity.find("SELECT c FROM TeacherComment c  where  c.studentBooking.id=?1   and c.deleteDate IS NULL ",
				sort,bookingId);
		}
		if(StringUtils.isNotBlank(parentId)){
			query = TeacherCommentEntity.find("SELECT c FROM TeacherComment c where  c.studentParent.id=?1   and c.deleteDate IS NULL ",
				sort,parentId);
		}
		logger.info("Number of comments  found Page:{}, Size:{}",page,size);
		if(query==null) {
			return  new ResultPage<TeacherComment>(page,0,0,Collections. emptyList() );
		}
		List<TeacherComment> result=query.page(page, size).list().stream()
                .map(commentMapper::toDomain)
                .collect(Collectors.toList());;
		logger.info("Number of comments  found:{}",result.size());
		ResultPage<TeacherComment> resultPage= new ResultPage<TeacherComment>(page,query.pageCount(),query.count(),result);
		return resultPage;
	}
	
   @Transactional
	public TeacherComment updateTeacherComment(String tenantKey,String id, TeacherComment commentDomain) {
		 TeacherCommentEntity comment=TeacherCommentEntity.findById(id);
		 if(comment==null) {
				throw new NoSuchElementException(TeacherCommentEntity.class,commentDomain.getId());
		 }
		 comment.content=(commentDomain.getContent());
		 comment.lastUpdateDate=(TimeUtils.getCurrentTime());
		 comment.persistAndFlush();
		 return commentMapper.toDomain(comment);
   }


   @Transactional
	public void revertApproveTeacherComment(String commentId) {
		 TeacherCommentEntity comment=TeacherCommentEntity.findById(commentId);
		 if(comment==null) {
				throw new NoSuchElementException(TeacherCommentEntity.class,commentId);
		 }
		 comment.lastUpdateDate=(TimeUtils.getCurrentTime());
		 comment.approveDate=null;
		 comment.approver=null;
		 comment.persistAndFlush();
	}
   
   @Transactional
	public void approveTeacherComment(String tenantKey,String commentId, String approverId) {
		 TeacherCommentEntity comment=TeacherCommentEntity.findById(commentId);
		 if(comment==null) {
				throw new NoSuchElementException(TeacherCommentEntity.class,commentId);
		 }
		TeacherProfile teacher=teacherService.getTeacherProfile(approverId);
	    logger.info("Teacher profile  found:{}",teacher);
	    
		 comment.lastUpdateDate=(TimeUtils.getCurrentTime());
		 comment.approveDate=(TimeUtils.getCurrentTime());
		 comment.approver=teacherMapper.toEntity(teacherService.getTeacherProfile(approverId));
		 comment.persistAndFlush();
	}
   
}
